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

class FAT12 extends FAT {
	
	/*
	 * Much of the following documentation and names of variables are derived from
	 * https://staff.washington.edu/dittrich/misc/fatgen103.pdf
	 */
	
	/**
	 * Int 0x13 drive number (e.g. 0x80). This field supports MS-DOS
	 * bootstrap and is set to the INT 0x13 drive number of the media
	 * (0x00 for floppy disks, 0x80 for hard disks).
	 * <p>
	 * NOTE: This field is actually operating system specific.
	 * <p>
	 * Offset: 36<br>
	 * Size: 1<br>
	 */
	protected static int bs_driveNumber;
	
	/**
	 * Reserved for use by Windows NT.
	 * <p>
	 * In FAT volumes, this should always be 0.
	 * <p>
	 * Offset: 37<br>
	 * Size: 1<br>
	 */
	protected static int bs_reserved1;
	
	/**
	 * Extended boot signature (0x29). Indicates that 
	 * {@link bs_volumeID}, {@link bs_volumeLabel}, 
	 * and {@link bs_fileSystemType} are present in the boot sector.
	 * <p>
	 * Offset: 38<br>
	 * Size: 1<br>
	 */
	protected static int bs_bootSignature;
	
	/**
	 * Volume serial number. This field, together with sb_VolumeLabel,
	 * supports volume tracking on removable media. These values allow
	 * FAT file system drivers to detect that the wrong disk is inserted in a
	 * removable drive. This ID is usually generated by simply combining
	 * the current date and time into a 32-bit value.
	 * <p>
	 * offset: 39<br>
	 * size: 4<br>
	 */
	protected static int bs_volumeID;
	
	/**
	 * Volume label. This field matches the 11-byte volume label
	 * recorded in the root directory.
	 * <p>
	 * NOTE: FAT file system drivers should make sure that they update
	 * this field when the volume label file in the root directory has its
	 * name changed or created. The setting for this field when there is no
	 * volume label is the string “NO NAME ”.
	 * <p>
	 * offset: 43<br>
	 * size: 11<br>
	 */
	protected static String bs_volumeLabel;
	
	/**
	 * One of the strings “FAT12 ”, “FAT16 ”, or “FAT ”.
	 * <p>
	 * NOTE: Many people think that the string in this field has
	 * something to do with the determination of what type of FAT—
	 * FAT12, FAT16, or FAT32—that the volume has. This is not true.
	 * You will note from its name that this field is not actually part of the
	 * BPB. This string is informational only and is not used by Microsoft
	 * file system drivers to determine FAT type, because it is frequently
	 * not set correctly or is not present. See the FAT Type Determination
	 * section of this document. This string should be set based on the
	 * FAT type though, because some non-Microsoft FAT file system
	 * drivers do look at it.
	 * <p>
	 * offset: 54<br>
	 * size: 8<br>
	 */
	protected static String bs_fileSystemType;
	
	@Override
	protected boolean isEndOfClusterChain(int fatEntry) {
		return fatEntry >= Util.END_OF_CHAIN_12;
	}
	
	@Override
	protected boolean isBadCluster(int fatEntry) {
		return fatEntry == Util.BAD_CLUSTER_12;
	}
	
	@Override
	protected int getFATEntry(int entryNumber) {
		int entryPos = (entryNumber & 0x1); // even or odd entry number
		int byteOffset = (entryNumber - entryPos) / 2 * 3 + entryPos;
		int fatSector = byteOffset / bpb_bytesPerSector + FAT.startOfFAT();
		byteOffset = byteOffset % (bpb_bytesPerSector / 2);
		
		return Util.unsignedInt12(ioManager.readSector(fatSector, bpb_bytesPerSector),
				byteOffset, (entryPos != 1));
	}

	@Override
	protected void initializeBootParameterBlock(byte[] bootSector) {
		super.initializeBootParameterBlock(bootSector);
		
		bs_driveNumber = Util.unsignedInt(bootSector, 36, 1);
		bs_reserved1 = Util.unsignedInt(bootSector, 37, 1);
		bs_bootSignature = Util.unsignedInt(bootSector, 38, 1);
		bs_volumeID = Util.unsignedInt(bootSector, 39, 4);
		
		char[] volumeLabel = new char[11];
		for (int i = 0; i < 11; i++) {
			volumeLabel[i] = (char)bootSector[i + 43];
		}
		bs_volumeLabel = new String(volumeLabel);
		
		char[] fileSystemType = new char[8];
		for (int i = 0; i < 8; i++) {
			fileSystemType[i] = (char)bootSector[i + 54];
		}
		bs_fileSystemType = new String(fileSystemType);
	}
	
	@Override
	protected void printFAT() {
		System.out.println("OEM:                 " + bs_OEMName);
		System.out.println("Bytes Per Sector:    " + bpb_bytesPerSector);
		System.out.println("Sectors Per Cluster: " + bpb_sectorsPerCluster);
		System.out.println("Reserved Sectors:    " + bpb_reservedSectorCount);
		System.out.println("FATs:                " + bpb_FATCount);
		System.out.println("Entries in Root:     " + bpb_rootEntryCount);
		System.out.println("Sectors:             " + totalSectors());
		System.out.println("Media:               " + bpb_mediaType);
		System.out.println("FAT Sectors:         " + fatSize() * bpb_FATCount);
		System.out.println("Sectors Per Track:   " + bpb_sectorsPerTrack);
		System.out.println("Hidden Sectors:      " + bpb_hiddenSectorCount);
		System.out.println("Signature:           " + bs_bootSignature);
		System.out.println("Volume ID:           " + bs_volumeID);
		System.out.println("Volume Label:        " + bs_volumeLabel);
		System.out.println("File System Type:    " + bs_fileSystemType);
		System.out.println("FAT Type is FAT12, disk has " + clustersInDataRegion() + " clusters");
	}
	
	@Override
	public String getFileSystemType() {
		return "FAT12";
	}
	
	@Override
	public String getFileSystemName() {
		return bs_volumeLabel.trim();
	}
}
