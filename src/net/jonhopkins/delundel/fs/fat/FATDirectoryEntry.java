/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Jon Hopkins
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package net.jonhopkins.delundel.fs.fat;

import java.util.ArrayList;
import java.util.List;

import net.jonhopkins.delundel.fs.FSDirectory;
import net.jonhopkins.delundel.fs.FSDirectoryEntry;

public class FATDirectoryEntry extends FSDirectoryEntry {
	private List<DE> dataStructures;
	private ShortNameDE info;
	
	public FATDirectoryEntry(FSDirectory parent) {
		super(parent);
		dataStructures = new ArrayList<DE>();
	}
	
	/**
	 * Generates a directory entry data structure based on the passed array 
	 * to build up this directory entry's information.
	 * 
	 * @param dirEntry The directory entry
	 * @return Whether the directory entry is the final one for this file
	 */
	boolean addDataStructure(byte[] dirEntry) {
		if (Util.unsignedInt(dirEntry, 11, 1) == Util.ATTR_LONG_NAME) {
			dataStructures.add(0, new LongNameDE(dirEntry));
			return false;
		} else {
			dataStructures.add(0, new ShortNameDE(dirEntry));
			return true;
		}
	}
	
	protected boolean invalidFileNameCharacter(char character) {
		if (character < 0x20 && character != Util.ACTUALLY_0xE5) {
			return true;
		}
		
		switch (character) {
		case 0x22: // "
		case 0x2a: // *
		case 0x2b: // +
		case 0x2c: // ,
		case 0x2e: // .
		case 0x2f: // /
		case 0x3a: // :
		case 0x3b: // ;
		case 0x3c: // <
		case 0x3d: // =
		case 0x3e: // >
		case 0x3f: // ?
		case 0x5b: // [
		case 0x5c: // \
		case 0x5d: // ]
		case 0x7c: // |
			return true;
		default:
			return false;
		}
	}
	
	//-----------------------------------------------------------------------------
	// ChkSum()
	// Returns an unsigned byte checksum computed on an unsigned byte
	// array. The array must be 11 bytes long and is assumed to contain
	// a name stored in the format of a MS-DOS directory entry.
	// Passed: pFcbName Pointer to an unsigned byte array assumed to be
	// 11 bytes long.
	// Returns: Sum An 8-bit unsigned checksum of the array pointed
	// to by pFcbName.
	//------------------------------------------------------------------------------
	private int checksum(String name) {
		int sum = 0;
		for (int i = 0; i < 11; i++) {
			sum = (((sum & 1) == 1 ? 0x80 : 0) + (sum >> 1) + name.charAt(i)) & 0x00ff;
		}
		return sum;
	}
	
	void generateName() {
		int size = dataStructures.size();
		if (size == 0) {
			return;
		}
		
		if (size > 1) {
			// if got a longname, skip shortname and build the longname
			StringBuilder name = new StringBuilder();
			for (int i = 1; i < size; i++) {
				name.append(dataStructures.get(i).getName());
			}
			this.entryName = name.toString();
		} else {
			this.entryName = dataStructures.get(0).getName();
		}
	}
	
	boolean verifyDataStructures() {
		int size = dataStructures.size();
		if (size == 0) {
			return false;
		}
		
		info = (ShortNameDE)dataStructures.get(0);
		if (size == 1) {
			return true;
		}
		
		int checksum = checksum(info.dir_name + info.dir_extension);
		for (int i = 1; i < size; i++) {
			if (((LongNameDE)dataStructures.get(i)).ldir_checksum != checksum) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String getDateTimeCreated() {
		int timeCreated = info.dir_createdTime;
		int dateCreated = info.dir_createdDate;
		
		int year = ((dateCreated & 0xfe00) >> 9) + 1980;
		int month = (dateCreated & 0x1e0) >> 5;
		int day = (dateCreated & 0x1f);
		
		int hour = (timeCreated & 0xf800) >> 11;
		int min = (timeCreated & 0x7e0) >> 5;
		int sec = (timeCreated & 0x1f) * 2; // 2 second resolution
		int millis = info.dir_createdTimeMillis;
		
		return String.format("%02d-%02d-%04d %02d:%02d:%02d.%03d",
				month, day, year, hour, min, sec, millis);
	}
	
	@Override
	public String getDateTimeModified() {
		int timeModified = info.dir_lastWriteTime;
		int dateModified = info.dir_lastWriteDate;
		
		int year = ((dateModified & 0xfe00) >> 9) + 1980;
		int month = (dateModified & 0x1e0) >> 5;
		int day = (dateModified & 0x1f);
		
		int hour = (timeModified & 0xf800) >> 11;
		int min = (timeModified & 0x7e0) >> 5;
		int sec = (timeModified & 0x1f) * 2; // 2 second resolution
		
		return String.format("%02d-%02d-%04d %02d:%02d:%02d",
				month, day, year, hour, min, sec);
	}
	
	public String getDateAccessed() {
		int dateAccessed = info.dir_lastAccessDate;
		
		int year = ((dateAccessed & 0xfe00) >> 9) + 1980;
		int month = (dateAccessed & 0x1e0) >> 5;
		int day = (dateAccessed & 0x1f);
		
		return String.format("%02d-%02d-%04d",
				month, day, year);
	}
	
	public long getFileSize() {
		return info.dir_fileSize;
	}
	
	int getFirstCluster() {
		int high = info.dir_firstCluster_highWord;
		int low = info.dir_firstCluster_lowWord;
		return (high << 16) + low;
	}
	
	public boolean isReadOnly() {
		return (info.dir_attributes & Util.ATTR_READ_ONLY) != 0;
	}
	
	@Override
	public boolean isHidden() {
		return (info.dir_attributes & Util.ATTR_HIDDEN) != 0;
	}
	
	public boolean isSystemFile() {
		return (info.dir_attributes & Util.ATTR_SYSTEM) != 0;
	}
	
	public boolean isVolumeID() {
		return (info.dir_attributes & Util.ATTR_VOLUME_ID) != 0;
	}
	
	@Override
	public boolean isDirectory() {
		return (info.dir_attributes & Util.ATTR_DIRECTORY) != 0;
	}
	
	@Override
	public boolean isFile() {
		return (info.dir_attributes & Util.ATTR_DIRECTORY) == 0;
	}
	
	public boolean isArchive() {
		return (info.dir_attributes & Util.ATTR_ARCHIVE) != 0;
	}
	
	@Override
	public boolean isDeleted() {
		return entryName.charAt(0) == Util.DELETED_DIR_ENTRY;
	}
	
	private abstract class DE {
		public abstract String getName();
	}
	
	private class ShortNameDE extends DE {
		
		/**
		 * The file's short name.
		 * <p>
		 * Offset: 0<br>
		 * Size: 8
		 */
		protected String dir_name;
		
		/**
		 * The file's extension.
		 * <p>
		 * Offset: 8<br>
		 * Size: 3
		 */
		protected String dir_extension;
		
		/**
		 * File attribute flags. Can be any of the following:
		 * <ul>
		 * <li>ATTR_READ_ONLY -- 0x01</li>
		 * <li>ATTR_HIDDEN -- 0x02</li>
		 * <li>ATTR_SYSTEM -- 0x04</li>
		 * <li>ATTR_VOLUME_ID -- 0x08</li>
		 * <li>ATTR_DIRECTORY -- 0x10</li>
		 * <li>ATTR_ARCHIVE -- 0x20</li>
		 * <li>ATTR_LONG_NAME -- ATTR_READ_ONLY
		 *       | ATTR_HIDDEN | ATTR_SYSTEM | ATTR_VOLUME_ID</li>
		 * </ul>
		 * The upper two bits of the attribute byte are reserved and should
		 * always be set to 0 when a file is created and never modified or
		 * looked at after that.
		 * <p>
		 * Offset: 11<br>
		 * Size: 1
		 */
		protected int dir_attributes;
		
		/**
		 * Reserved for use by Windows NT. Set value to 0 when a file is
		 * created and never modify or look at it after that.
		 * <p>
		 * Offset: 12<br>
		 * Size: 1
		 */
		protected int dir_reserved;
		
		/**
		 * Millisecond stamp at file creation time. This field actually
		 * contains a count of tenths of a second. The granularity of the
		 * seconds part of DIR_CrtTime is 2 seconds so this field is a
		 * count of tenths of a second and its valid value range is 0-199
		 * inclusive.
		 * <p>
		 * Offset: 13<br>
		 * Size: 1
		 */
		protected int dir_createdTimeMillis;
		
		/**
		 * Time file was created.
		 * <p>
		 * Offset: 14<br>
		 * Size: 2
		 */
		protected int dir_createdTime;
		
		/**
		 * Date file was created.
		 * <p>
		 * Offset: 16<br>
		 * Size: 2
		 */
		protected int dir_createdDate;
		
		/**
		 * Last access date. Note that there is no last access time, only a
		 * date. This is the date of last read or write. In the case of a write,
		 * this should be set to the same date as DIR_WrtDate.
		 * <p>
		 * Offset: 18<br>
		 * Size: 2
		 */
		protected int dir_lastAccessDate;
		
		/**
		 * High word of this entry’s first cluster number (always 0 for a
		 * FAT12 or FAT16 volume).
		 * <p>
		 * Offset: 20<br>
		 * Size: 2
		 */
		protected int dir_firstCluster_highWord;
		
		/**
		 * Time of last write. Note that file creation is considered a write.
		 * <p>
		 * Offset: 22<br>
		 * Size: 2
		 */
		protected int dir_lastWriteTime;
		
		/**
		 * Date of last write. Note that file creation is considered a write.
		 * <p>
		 * Offset: 24<br>
		 * Size: 2
		 */
		protected int dir_lastWriteDate;
		
		/**
		 * Low word of this entry’s first cluster number.
		 * <p>
		 * Offset: 26<br>
		 * Size: 2
		 */
		protected int dir_firstCluster_lowWord;
		
		/**
		 * File's size in bytes.
		 * <p>
		 * Offset: 28
		 * Size: 4
		 */
		protected long dir_fileSize;
		
		public ShortNameDE(byte[] dirEntry) {
			int numChars = 0;
			for (int i = 0; i < 8; i++) {
				if (dirEntry[i] != 0) {
					numChars++;
				}
			}
			char[] fileName = new char[numChars];
			int count = 0;
			for (int i = 0; i < numChars; i++) {
				char ch = (char)Util.unsignedInt(dirEntry, i, 1);
				if (ch != 0) {
					fileName[count] = ch;
					count++;
				}
			}
			dir_name = new String(fileName);
			
			numChars = 0;
			for (int i = 0; i < 3; i++) {
				if (dirEntry[i + 8] != 0) {
					numChars++;
				}
			}
			char[] extension = new char[numChars];
			count = 0;
			for (int i = 0; i < numChars; i++) {
				char ch = (char)Util.unsignedInt(dirEntry, i + 8, 1);
				if (ch != 0) {
					extension[count] = ch;
					count++;
				}
			}
			dir_extension = new String(extension);
			
			dir_attributes = Util.unsignedInt(dirEntry, 11, 1);
			dir_reserved = Util.unsignedInt(dirEntry, 12, 1);
			dir_createdTimeMillis = Util.unsignedInt(dirEntry, 13, 1);
			dir_createdTime = Util.unsignedInt(dirEntry, 14, 2);
			dir_createdDate = Util.unsignedInt(dirEntry, 16, 2);
			dir_lastAccessDate = Util.unsignedInt(dirEntry, 18, 2);
			dir_firstCluster_highWord = Util.unsignedInt(dirEntry, 20, 2);
			dir_lastWriteTime = Util.unsignedInt(dirEntry, 22, 2);
			dir_lastWriteDate = Util.unsignedInt(dirEntry, 24, 2);
			dir_firstCluster_lowWord = Util.unsignedInt(dirEntry, 26, 2);
			dir_fileSize = Util.unsignedLong(dirEntry, 28, 4);
		}
		
		public String getName() {
			if (dir_extension.trim().isEmpty()) {
				return dir_name.trim();
			}
			return dir_name.trim() + "." + dir_extension.trim();
		}
	}
	
	private class LongNameDE extends DE {
		
		/**
		 * The order of this entry in the sequence of long directory entries 
		 * associated with the short directory entry at the end of the long 
		 * directory set.
		 * <p>
		 * If masked with 0x40 (LAST_LONG_ENTRY), indicates this entry is the 
		 * final long directory entry in a set of long directory entries. All 
		 * valid sets of long directory entries must begin with an entry having 
		 * this mask.
		 * <p>
		 * Offset: 0<br>
		 * Size: 1 
		 */
		protected int ldir_ordinal;
		
		/**
		 * The first 5 Unicode characters of the long-name sub-component of this 
		 * directory entry.
		 * <p>
		 * Offset: 1<br>
		 * Size: 10
		 */
		protected char[] ldir_name1;
		
		/**
		 * Attributes; must be {@code Util.ATTR_LONG_NAME}
		 * <p>
		 * Offset: 11<br>
		 * Size: 1
		 * 
		 * @see Util.ATTR_LONG_NAME
		 */
		protected int ldir_attributes;
		
		/**
		 * If 0, indicates a directory entry that is a sub-component of a 
		 * long-name. Other values are reserved for future extensions.
		 * <p>
		 * Offset: 12<br>
		 * Size: 1
		 */
		protected int ldir_type;
		
		/**
		 * Checksum of {@code name} in the short directory entry at the end of 
		 * the long directory entry set.
		 * <p>
		 * Offset: 13<br>
		 * Size: 1
		 */
		protected int ldir_checksum;
		
		/**
		 * The 6-11 Unicode characters of the long-name sub-component in this 
		 * directory entry.
		 * <p>
		 * Offset: 14<br>
		 * Size: 12
		 */
		protected char[] ldir_name2;
		
		/**
		 * Must be 0. This is an artifact of the FAT "first cluster" and must be 
		 * 0 for compatibility with existing disk utilities. It is meaningless 
		 * in the context of a long directory entry.
		 * <p>
		 * Offset: 26<br>
		 * Size: 2
		 */
		protected int ldir_firstCluster_lowWord;
		
		/**
		 * The 12-13 Unicode characters of the long-name sub-component in this 
		 * directory entry.
		 * <p>
		 * Offset: 28<br>
		 * Size: 4
		 */
		protected char[] ldir_name3;
		
		public LongNameDE(byte[] dirEntry) {
			ldir_ordinal = Util.unsignedInt(dirEntry, 0, 1);
			
			// long name is 0x0000 terminated, and any remaining character 
			// spaces are filled with 0xffff. Find the 0 if there is one 
			// and skip the remaining characters.
			boolean foundEnd = false;
			int numChars = 0;
			for (int i = 0; i < 5; i++) {
				char ch = (char)Util.unsignedInt(dirEntry, i * 2 + 1, 2);
				if (ch == 0) {
					foundEnd = true;
					break;
				}
				numChars++;
			}
			ldir_name1 = new char[numChars];
			int count = 0;
			for (int i = 0; i < numChars; i++) {
				char ch = (char)Util.unsignedInt(dirEntry, i * 2 + 1, 2);
				if (ch != 0) {
					ldir_name1[count] = ch;
					count++;
				}
			}
			
			ldir_attributes = Util.unsignedInt(dirEntry, 11, 1);
			ldir_type = Util.unsignedInt(dirEntry, 12, 1);
			ldir_checksum = Util.unsignedInt(dirEntry, 13, 1);
			
			if (!foundEnd) {
				numChars = 0;
				for (int i = 0; i < 6; i++) {
					int ch = Util.unsignedInt(dirEntry, i * 2 + 14, 2);
					if (ch == 0) {
						foundEnd = true;
						break;
					}
					numChars++;
				}
				ldir_name2 = new char[numChars];
				count = 0;
				for (int i = 0; i < numChars; i++) {
					char ch = (char)Util.unsignedInt(dirEntry, i * 2 + 14, 2);
					if (ch != 0) {
						ldir_name2[count] = ch;
						count++;
					}
				}
			} else {
				ldir_name2 = new char[0];
			}
			
			ldir_firstCluster_lowWord = Util.unsignedInt(dirEntry, 26, 2);
			
			if (!foundEnd) {
				numChars = 0;
				for (int i = 0; i < 2; i++) {
					int ch = Util.unsignedInt(dirEntry, i * 2 + 28, 2);
					if (ch == 0) {
						foundEnd = true;
						break;
					}
					numChars++;
				}
				ldir_name3 = new char[numChars];
				count = 0;
				for (int i = 0; i < numChars; i++) {
					char ch = (char)Util.unsignedInt(dirEntry, i * 2 + 28, 2);
					if (ch != 0) {
						ldir_name3[count] = ch;
						count++;
					}
				}
			} else {
				ldir_name3 = new char[0];
			}
		}
		
		public String getName() {
			int len = ldir_name1.length + ldir_name2.length + ldir_name3.length;
			char[] name = new char[len];
			int count = 0;
			
			for (int i = 0; i < ldir_name1.length; i++) {
				name[count] = ldir_name1[i];
				count++;
			}
			for (int i = 0; i < ldir_name2.length; i++) {
				name[count] = ldir_name2[i];
				count++;
			}
			for (int i = 0; i < ldir_name3.length; i++) {
				name[count] = ldir_name3[i];
				count++;
			}
			
			return new String(name);
		}
	}
}
