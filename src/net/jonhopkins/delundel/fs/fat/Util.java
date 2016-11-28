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

class Util {
	static final int BAD_CLUSTER_16 = 0xFFF7;
	static final int BAD_CLUSTER_32 = 0x0FFFFFF7;
	static final int END_OF_CHAIN_16 = 0xFFF8;
	static final int END_OF_CHAIN_32 = 0x0FFFFFF8;
	static final int FREE_DIR_ENTRY = 0x00;
	static final int DELETED_DIR_ENTRY = 0xe5;
	static final int ACTUALLY_0xE5 = 0x05;
	
	/**
	 * Indicates that writes to the file should fail.
	 */
	static final int ATTR_READ_ONLY = 0x01;
	
	/**
	 * Indicates that normal directory listings should not show this file.
	 */
	static final int ATTR_HIDDEN = 0x02;
	
	/**
	 * Indicates that this is an operating system file.
	 */
	static final int ATTR_SYSTEM = 0x04;
	
	/**
	 * There should only be one file on the volume with this attribute set, 
	 * and that file must be in the root directory. The name of this file is 
	 * actually the label for the volume. {@link dir_firstCluster_lowWord} 
	 * and {@link dir_firstCluster_highWord} must always be 0 for the volume 
	 * label (no data clusters are allocated to the volume label file).
	 */
	static final int ATTR_VOLUME_ID = 0x08;
	
	/**
	 * Indicates that this file is a directory.
	 */
	static final int ATTR_DIRECTORY = 0x10;
	
	/**
	 * Supports backup utilities. This bit is set by the FAT file system 
	 * driver when a file is created, renamed, or written to. Backup 
	 * utilities may use this attribute to indicate which files on the volume 
	 * have been modified since the last time a backup was performed.
	 */
	static final int ATTR_ARCHIVE = 0x20;
	
	/**
	 * Indicates that the entry is actually part of the long name entry for 
	 * some other file.
	 */
	static final int ATTR_LONG_NAME = ATTR_READ_ONLY
			| ATTR_HIDDEN | ATTR_SYSTEM | ATTR_VOLUME_ID;
	
	/**
	 * Indicates the final entry in a set of long directory entries. 
	 */
	static final int LAST_LONG_ENTRY = 0x40;
	
	static final int SIZE_DIR_ENTRY = 32;
	
	/**
	 * Reads {@code numBytes} bytes from the given byte array and converts 
	 * them from little-endian to big-endian while preventing values greater 
	 * than 127 from automatically being converted to a negative number.
	 *  
	 * @param bootSector
	 * @param offset
	 * @param numBytes
	 * @return
	 */
	static int unsignedInt(byte[] byteArray, int offset, int numBytes) {
		int ret = 0;
		for (int i = 0; i < numBytes; i++) {
			ret += ((byteArray[offset + i] & 0x00ff) << (i * 8));
		}
		return ret;
	}
	
	static long unsignedLong(byte[] byteArray, int offset, int numBytes) {
		int ret = 0;
		for (int i = 0; i < numBytes; i++) {
			ret += ((byteArray[offset + i] & 0x00ff) << (i * 8));
		}
		return ret;
	}
}
