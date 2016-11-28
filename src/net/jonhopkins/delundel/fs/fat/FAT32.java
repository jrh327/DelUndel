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

class FAT32 extends FAT {
	
	/*
	 * Much of the following documentation and names of variables are derived from
	 * https://staff.washington.edu/dittrich/misc/fatgen103.pdf
	 */
	
	/**
	 * The FAT32 32-bit count of sectors occupied by <em>one</em> FAT. If 
	 * this field has a value, then {@link bpb_FATSize_16} must be 0.
	 * <p>
	 * Offset: 36<br>
	 * Size: 4<br>
	 */
	protected static int bpb_FATSize_32;
	
	/**
	 * Flags for FAT mirroring.
	 * <ul>
	 * <li>Bits 0-3 -- Zero-based number of the currently active FAT. Only 
	 * valid if mirroring is disabled.</li>
	 * <li>Bits 4-6 -- Reserved.</li>
	 * <li>Bit 7 --
	 * 	<ul>
	 * 		<li>0: the FAT is mirrored at runtime into all FATs.</li>
	 *  	<li>1: only one FAT (the one denoted by bits 0-3) is active</li>
	 * 	</ul>
	 * </li>
	 * <li>Bits 8-15 -- Reserved.</li>
	 * </ul>
	 * <p>
	 * Offset: 40<br>
	 * Size: 2<br>
	 */
	protected static int bpb_extraFATFlags;
	
	/**
	 * The version number of the FAT32 volume.
	 * <p>
	 * High byte is major revision number. Low byte is minor revision number.
	 * <p>
	 * Offset: 42<br>
	 * Size: 2<br>
	 */
	protected static int bpb_fileSystemVersion;
	
	/**
	 * The cluster number of the first cluster of the root directory.
	 * <p>
	 * This field is usually, but not required to be, 2.
	 * <p>
	 * Offset: 44<br>
	 * Size: 4<br>
	 */
	protected static int bpb_rootCluster;
	
	/**
	 * Sector number of {@code FSINFO} structure in the reserved area of the 
	 * FAT32 volume.
	 * <p>
	 * This field is usually, but not required to be, 1.
	 * <p>
	 * <strong>Note</strong>: There will be a copy of the {@code FSINFO} 
	 * structure in the backup boot record, but only the copy pointed to by 
	 * this field will be kept up to date (i.e. both the primary and backup 
	 * boot record will point to the same {@code FSINFO} sector).
	 * <p>
	 * Offset: 48<br>
	 * Size: 2<br>
	 */
	protected static int bpb_fileSystemInfo;
	
	/**
	 * If non-zero, indicates the sector number in the reserved area of the 
	 * volume of a backup copy of the boot record.
	 * <p>
	 * This field is usually 6. No value other than 6 is recommended.
	 * <p>
	 * Offset: 50<br>
	 * Size: 2<br>
	 */
	protected static int bpb_backupBootSector;
	
	/**
	 * Reserved for future expansion.
	 * <p>
	 * All bytes should be 0.
	 * <p>
	 * Offset: 52<br>
	 * Size: 12<br>
	 * </ul>
	 */
	protected static int bpb_reserved;
	
	/**
	 * Int 0x13 drive number (e.g. 0x80). This field supports MS-DOS
	 * bootstrap and is set to the INT 0x13 drive number of the media
	 * (0x00 for floppy disks, 0x80 for hard disks).
	 * <p>
	 * NOTE: This field is actually operating system specific.
	 * <p>
	 * Offset: 64<br>
	 * Size: 1<br>
	 */
	protected static int bs_driveNumber;
	
	/**
	 * Reserved for use by Windows NT.
	 * <p>
	 * In FAT volumes, this should always be 0.
	 * <p>
	 * Offset: 65<br>
	 * Size: 1<br>
	 */
	protected static int bs_reserved1;
	
	/**
	 * This field has the same definition as it does for FAT12 and FAT16
	 * media. The only difference for FAT32 media is that the field is at a
	 * different offset in the boot sector.
	 * <p>
	 * offset: 66<br>
	 * size: 1<br>
	 */
	protected static int bs_bootSignature;
	
	/**
	 * This field has the same definition as it does for FAT12 and FAT16
	 * media. The only difference for FAT32 media is that the field is at a
	 * different offset in the boot sector.
	 * <p>
	 * offset: 64<br>
	 * size: 4<br>
	 */
	protected static int bs_volumeID;
	
	/**
	 * This field has the same definition as it does for FAT12 and FAT16
	 * media. The only difference for FAT32 media is that the field is at a
	 * different offset in the boot sector.
	 * <p>
	 * offset: 71<br>
	 * size: 11<br>
	 */
	protected static String bs_volumeLabel;
	
	/**
	 * Always set to the string ”FAT32 ”. Please see the note for this
	 * field in the FAT12/FAT16 section earlier. This field has nothing to
	 * do with FAT type determination.
	 * <p>
	 * offset: 82<br>
	 * size: 8<br>
	 */
	protected static String bs_fileSystemType;
	
	@Override
	protected boolean isEndOfClusterChain(int fatEntry) {
		return fatEntry >= Util.END_OF_CHAIN_32;
	}
	
	@Override
	protected boolean isBadCluster(int fatEntry) {
		return fatEntry == Util.BAD_CLUSTER_32;
	}
	
	@Override
	protected int getFATEntry(int entryNumber) {
		return 0;
	}

	@Override
	protected void initializeBootParameterBlock(byte[] bootSector) {
		super.initializeBootParameterBlock(bootSector);
		
		bpb_FATSize_32 = Util.unsignedInt(bootSector, 36, 4);
		bpb_extraFATFlags = Util.unsignedInt(bootSector, 40, 2);
		bpb_fileSystemVersion = Util.unsignedInt(bootSector, 42, 2);
		bpb_rootCluster = Util.unsignedInt(bootSector, 44, 4);
		bpb_fileSystemInfo = Util.unsignedInt(bootSector, 48, 2);
		bpb_backupBootSector = Util.unsignedInt(bootSector, 50, 2);
		bpb_reserved = Util.unsignedInt(bootSector, 52, 12);
		bs_driveNumber = Util.unsignedInt(bootSector, 64, 1);
		bs_reserved1 = Util.unsignedInt(bootSector, 65, 1);
		bs_bootSignature = Util.unsignedInt(bootSector, 66, 1);
		bs_volumeID = Util.unsignedInt(bootSector, 67, 4);
		
		char[] volumeLabel = new char[11];
		for (int i = 0; i < 11; i++) {
			volumeLabel[i] = (char)bootSector[i + 71];
		}
		bs_volumeLabel = new String(volumeLabel);
		
		char[] fileSystemType = new char[8];
		for (int i = 0; i < 8; i++) {
			fileSystemType[i] = (char)bootSector[i + 82];
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
		System.out.println("FAT Type is FAT32, disk has " + clustersInDataRegion() + " clusters");
	}
	
	@Override
	public String getFileSystemType() {
		return "FAT32";
	}
	
	@Override
	public String getFileSystemName() {
		return bs_volumeLabel.trim();
	}
}
