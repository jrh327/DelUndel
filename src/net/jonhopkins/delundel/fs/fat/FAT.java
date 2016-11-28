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

import java.util.LinkedList;
import java.util.List;

import net.jonhopkins.delundel.fs.FSDirectory;
import net.jonhopkins.delundel.fs.FSDirectoryEntry;
import net.jonhopkins.delundel.fs.FSFile;
import net.jonhopkins.delundel.fs.FSFileSystem;
import net.jonhopkins.delundel.fs.FSIOManager;

public abstract class FAT implements FSFileSystem {
	
	/*
	 * Much of the following documentation and names of variables are derived from
	 * https://staff.washington.edu/dittrich/misc/fatgen103.pdf
	 */
	
	/*
	 * Start of Boot Sector (bs_) variables
	 */
	
	/**
	 * Jump instruction to boot code. This field has two allowed forms:
	 * <ul>
	 * 	<li>jmpBoot[0] = 0xEB, jmpBoot[1] = 0x??, jmpBoot[2] = 0x90</li>
	 * 	<li>jmpBoot[0] = 0xE9, jmpBoot[1] = 0x??, jmpBoot[2] = 0x??</li>
	 * </ul>
	 * 0x?? indicates that any 8-bit value is allowed in that byte. What this
	 * forms is a three-byte Intel x86 unconditional branch (jump)
	 * instruction that jumps to the start of the operating system bootstrap
	 * code. This code typically occupies the rest of sector 0 of the volume
	 * following the BPB and possibly other sectors. Either of these forms
	 * is acceptable. JmpBoot[0] = 0xEB is the more frequently used
	 * format.
	 * <p>
	 * offset: 0<br>
	 * size: 3<br>
	 */
	protected static byte[] bs_jmpBoot = new byte[3];
	
	/**
	 * “MSWIN4.1” There are many misconceptions about this field. It is
	 * only a name string. Microsoft operating systems don’t pay any
	 * attention to this field. Some FAT drivers do. This is the reason that
	 * the indicated string, “MSWIN4.1”, is the recommended setting,
	 * because it is the setting least likely to cause compatibility problems.
	 * If you want to put something else in here, that is your option, but
	 * the result may be that some FAT drivers might not recognize the
	 * volume. Typically this is some indication of what system formatted
	 * the volume.
	 * <p>
	 * offset: 3<br>
	 * size: 8<br>
	 */
	protected static String bs_OEMName;
	
	/*
	 * Start of Boot Parameter Block (bpb_) variables
	 */
	
	/**
	 * Count of bytes per sector. This value may take on only the
	 * following values: 512, 1024, 2048 or 4096. If maximum
	 * compatibility with old implementations is desired, only the value
	 * 512 should be used. There is a lot of FAT code in the world that is
	 * basically “hard wired” to 512 bytes per sector and doesn’t bother to
	 * check this field to make sure it is 512. Microsoft operating systems
	 * will properly support 1024, 2048, and 4096.
	 * <p>
	 * Note: Do not misinterpret these statements about maximum
	 * compatibility. If the media being recorded has a physical sector size
	 * N, you must use N and this must still be less than or equal to 4096.
	 * Maximum compatibility is achieved by only using media with
	 * specific sector sizes.
	 * <p>
	 * offset: 11<br>
	 * size: 2<br>
	 */
	protected static int bpb_bytesPerSector;
	
	/**
	 * Number of sectors per allocation unit. This value must be a power
	 * of 2 that is greater than 0. The legal values are 1, 2, 4, 8, 16, 32, 64,
	 * and 128. Note however, that a value should never be used that
	 * results in a “bytes per cluster” value (BPB_BytsPerSec *
	 * BPB_SecPerClus) greater than 32K (32 * 1024). There is a
	 * misconception that values greater than this are OK. Values that
	 * cause a cluster size greater than 32K bytes do not work properly; do
	 * not try to define one. Some versions of some systems allow 64K
	 * bytes per cluster value. Many application setup programs will not
	 * work correctly on such a FAT volume.
	 * <p>
	 * offset: 13<br>
	 * size: 1<br>
	 */
	protected static int bpb_sectorsPerCluster;
	
	/**
	 * Number of reserved sectors in the Reserved region of the volume
	 * starting at the first sector of the volume. This field must not be 0.
	 * For FAT12 and FAT16 volumes, this value should never be
	 * anything other than 1. For FAT32 volumes, this value is typically
	 * 32. There is a lot of FAT code in the world “hard wired” to 1
	 * reserved sector for FAT12 and FAT16 volumes and that doesn’t
	 * bother to check this field to make sure it is 1. Microsoft operating
	 * systems will properly support any non-zero value in this field.
	 * <p>
	 * offset: 14<br>
	 * size: 2<br>
	 */
	protected static int bpb_reservedSectorCount;
	
	/**
	 * The count of FAT data structures on the volume. This field should
	 * always contain the value 2 for any FAT volume of any type.
	 * Although any value greater than or equal to 1 is perfectly valid,
	 * many software programs and a few operating systems’ FAT file
	 * system drivers may not function properly if the value is something
	 * other than 2. All Microsoft file system drivers will support a value
	 * other than 2, but it is still highly recommended that no value other
	 * than 2 be used in this field.
	 * <p>
	 * The reason the standard value for this field is 2 is to provide redundancy
	 * for the FAT data structure so that if a sector goes bad in one
	 * of the FATs, that data is not lost because it is duplicated in the other
	 * FAT. On non-disk-based media, such as FLASH memory cards,
	 * where such redundancy is a useless feature, a value of 1 may be
	 * used to save the space that a second copy of the FAT uses, but
	 * some FAT file system drivers might not recognize such a volume
	 * properly.
	 * <p>
	 * offset: 16<br>
	 * size: 1<br>
	 */
	protected static int bpb_FATCount;
	
	/**
	 * For FAT12 and FAT16 volumes, this field contains the count of 32-
	 * byte directory entries in the root directory. For FAT32 volumes,
	 * this field must be set to 0. For FAT12 and FAT16 volumes, this
	 * value should always specify a count that when multiplied by 32
	 * results in an even multiple of BPB_BytsPerSec. For maximum
	 * compatibility, FAT16 volumes should use the value 512.
	 * <p>
	 * offset: 17<br>
	 * size: 2<br>
	 */
	protected static int bpb_rootEntryCount;
	
	/**
	 * This field is the old 16-bit total count of sectors on the volume.
	 * This count includes the count of all sectors in all four regions of the
	 * volume. This field can be 0; if it is 0, then BPB_TotSec32 must be
	 * non-zero. For FAT32 volumes, this field must be 0. For FAT12 and
	 * FAT16 volumes, this field contains the sector count, and
	 * BPB_TotSec32 is 0 if the total sector count “fits” (is less than
	 * 0x10000).
	 * <p>
	 * offset: 19<br>
	 * size: 2<br>
	 */
	protected static int bpb_totalSectorCount_16;
	
	/**
	 * 0xF8 is the standard value for “fixed” (non-removable) media. For
	 * removable media, 0xF0 is frequently used. The legal values for this
	 * field are 0xF0, 0xF8, 0xF9, 0xFA, 0xFB, 0xFC, 0xFD, 0xFE, and
	 * 0xFF. The only other important point is that whatever value is put
	 * in here must also be put in the low byte of the FAT[0] entry. This
	 * dates back to the old MS-DOS 1.x media determination noted
	 * earlier and is no longer usually used for anything.
	 * <p>
	 * offset: 21<br>
	 * size: 1<br>
	 */
	protected static int bpb_mediaType;
	
	/**
	 * This field is the FAT12/FAT16 16-bit count of sectors occupied by
	 * ONE FAT. On FAT32 volumes this field must be 0, and
	 * BPB_FATSz32 contains the FAT size count.
	 * <p>
	 * offset: 22<br>
	 * size: 2<br>
	 */
	protected static int bpb_FATSize_16;
	
	/**
	 * Sectors per track for interrupt 0x13. This field is only relevant for
	 * media that have a geometry (volume is broken down into tracks by
	 * multiple heads and cylinders) and are visible on interrupt 0x13.
	 * This field contains the “sectors per track” geometry value.
	 * <p>
	 * offset: 24<br>
	 * size: 2<br>
	 */
	protected static int bpb_sectorsPerTrack;
	
	/**
	 * Number of heads for interrupt 0x13. This field is relevant as
	 * discussed earlier for BPB_SecPerTrk. This field contains the one
	 * based “count of heads”. For example, on a 1.44 MB 3.5-inch floppy
	 * drive this value is 2.
	 * <p>
	 * offset: 26<br>
	 * size: 2<br>
	 */
	protected static int bpb_headCount;
	
	/**
	 * Count of hidden sectors preceding the partition that contains this
	 * FAT volume. This field is generally only relevant for media visible
	 * on interrupt 0x13. This field should always be zero on media that
	 * are not partitioned. Exactly what value is appropriate is operating
	 * system specific.
	 * <p>
	 * offset: 28<br>
	 * size: 4<br>
	 */
	protected static int bpb_hiddenSectorCount;
	
	/**
	 * This field is the new 32-bit total count of sectors on the volume.
	 * This count includes the count of all sectors in all four regions of the
	 * volume. This field can be 0; if it is 0, then BPB_TotSec16 must be
	 * non-zero. For FAT32 volumes, this field must be non-zero. For
	 * FAT12/FAT16 volumes, this field contains the sector count if
	 * BPB_TotSec16 is 0 (count is greater than or equal to 0x10000).
	 * <p>
	 * offset: 32<br>
	 * size: 4<br>
	 */
	protected static int bpb_totalSectorCount_32;
	
	/*
	 * A cheat to get the determine the size of the FAT without instantiating 
	 * a specific filesystem, since we know that this value is stored at 
	 * offset 36 in a FAT32 system, which is the only place it exists.
	 */
	private static int bpb_FATSize_32;
	
	protected void initializeBootParameterBlock(byte[] bootSector) {
		for (int i = 0; i < 3; i++) {
			bs_jmpBoot[i] = bootSector[i];
		}
		
		char[] oemName = new char[8];
		for (int i = 0; i < 8; i++) {
			oemName[i] = (char)bootSector[i + 3];
		}
		bs_OEMName = new String(oemName);
		
		bpb_bytesPerSector = Util.unsignedInt(bootSector, 11, 2);
		bpb_sectorsPerCluster = Util.unsignedInt(bootSector, 13, 1);
		bpb_reservedSectorCount = Util.unsignedInt(bootSector, 14, 2);
		bpb_FATCount = Util.unsignedInt(bootSector, 16, 1);
		bpb_rootEntryCount = Util.unsignedInt(bootSector, 17, 2);
		bpb_totalSectorCount_16 = Util.unsignedInt(bootSector, 19, 2);
		bpb_mediaType = Util.unsignedInt(bootSector, 21, 1);
		bpb_FATSize_16 = Util.unsignedInt(bootSector, 22, 2);
		bpb_sectorsPerTrack = Util.unsignedInt(bootSector, 24, 2);
		bpb_headCount = Util.unsignedInt(bootSector, 26, 2);
		bpb_hiddenSectorCount = Util.unsignedInt(bootSector, 28, 4);
		bpb_totalSectorCount_32 = Util.unsignedInt(bootSector, 32, 4);
	}
	
	/*
	 * This is the one and only way that FAT type is determined. There is no 
	 * such thing as a FAT12 volume that has more than 4084 clusters. There 
	 * is no such thing as a FAT16 volume that has less than 4085 clusters 
	 * or more than 65,524 clusters. There is no such thing as a FAT32 volume 
	 * that has less than 65,525 clusters. If you try to make a FAT volume 
	 * that violates this rule, Microsoft operating systems will not handle 
	 * it correctly because they will think the volume has a different type 
	 * of FAT than what you think it does. 
	 */
	public static FAT getFAT(FSIOManager ioManager) {
		byte[] bootSector = ioManager.readSector(0, 512);
		
		bpb_bytesPerSector = Util.unsignedInt(bootSector, 11, 2);
		bpb_sectorsPerCluster = Util.unsignedInt(bootSector, 13, 1);
		bpb_reservedSectorCount = Util.unsignedInt(bootSector, 14, 2);
		bpb_FATCount = Util.unsignedInt(bootSector, 16, 1);
		bpb_rootEntryCount = Util.unsignedInt(bootSector, 17, 2);
		bpb_totalSectorCount_16 = Util.unsignedInt(bootSector, 19, 2);
		bpb_FATSize_16 = Util.unsignedInt(bootSector, 22, 2);
		bpb_totalSectorCount_32 = Util.unsignedInt(bootSector, 32, 4);
		bpb_FATSize_32 = Util.unsignedInt(bootSector, 36, 4);
		
		int numClusters = clustersInDataRegion();
		FAT fat;
		if (numClusters < 4085) {
			fat = new FAT12();
		} else if(numClusters < 65525) {
			fat = new FAT16();
		} else {
			fat = new FAT32();
		}
		
		fat.initializeBootParameterBlock(bootSector);
		fat.setIOManager(ioManager);
		
		return fat;
	}
	
	/**
	 * The count of sectors occupied by the root directory.
	 * <p>
	 * Note that on a FAT32 volume the BPB_RootEntCnt value is always 0,  
	 * so on a FAT32 volume RootDirSectors is always 0.
	 * <p>
	 * Note also that this computation rounds up.
	 * 
	 * @return The number of sectors occupied by the root directory.
	 */
	protected static int numRootDirSectors() {
		return ((bpb_rootEntryCount * Util.SIZE_DIR_ENTRY) + (bpb_bytesPerSector - 1)) / bpb_bytesPerSector;
	}
	
	/**
	 * Get the number of sectors occupied by one FAT structure.
	 * 
	 * @return The size of the FAT
	 */
	protected static int fatSize() {
		if (bpb_FATSize_16 != 0) {
			return bpb_FATSize_16;
		}
		return bpb_FATSize_32;
	}
	
	protected static int totalSectors() {
		if (bpb_totalSectorCount_16 != 0) {
			return bpb_totalSectorCount_16;
		} else {
			return bpb_totalSectorCount_32;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected static int sectorsInDataRegion() {
		return totalSectors() - (bpb_reservedSectorCount + (bpb_FATCount * fatSize()) + numRootDirSectors());
	}
	
	protected static int startOfFAT() {
		return bpb_reservedSectorCount;
	}
	
	protected static int startOfRootDir() {
		return startOfFAT() + (bpb_FATCount * fatSize());
	}
	
	/**
	 * 
	 * @return
	 */
	protected static int clustersInDataRegion() {
		return sectorsInDataRegion() / bpb_sectorsPerCluster;
	}
	
	/**
	 * Get the location of the first sector of the data region of the 
	 * partition, which is the same as the first sector of cluster 2.
	 * <p>
	 * NOTE: This sector number is relative to the first sector of the 
	 * volume that contains the BPB (the sector that contains the BPB is 
	 * sector number 0). This does not necessarily map directly onto the 
	 * drive, because sector 0 of the volume is not necessarily sector 0 
	 * of the drive due to partitioning.
	 * 
	 * @return The location of the start of the data region.
	 */
	protected static int startOfDataRegion() {
		return startOfRootDir() + numRootDirSectors();
	}
	
	/**
	 * Given any valid data cluster {@code cluster}, get the first sector 
	 * of that cluster, relative to sector 0 of the FAT volume.
	 * 
	 * @param cluster The cluster whose location is to be calculated
	 * @return The location of the start of the given cluster
	 */
	protected static int startOfCluster(int cluster) {
		return ((cluster - 2) * bpb_sectorsPerCluster) + startOfDataRegion();
	}
	
	protected FSIOManager ioManager;
	protected FATDirectory root;
	
	protected abstract boolean isEndOfClusterChain(int fatEntry);
	protected abstract boolean isBadCluster(int fatEntry);
	protected abstract int getFATEntry(int entryNumber);
	protected abstract void printFAT();
	
	@Override
	public void setIOManager(FSIOManager manager) {
		this.ioManager = manager;
	}
	
	@Override
	public FSDirectory getRootDirectory() {
		if (root != null) {
			return root;
		}
		
		root = new FATDirectory();
		int startOfRoot = startOfRootDir();
		int endOfRoot = startOfRoot + numRootDirSectors();
		
		for (int sectorNumber = startOfRoot; sectorNumber < endOfRoot; sectorNumber++) {
			root.addEntries(ioManager.readSector(sectorNumber, bpb_bytesPerSector));
		}
		
		root.setName("/");
		
		return root;
	}
	
	@Override
	public byte[] getFileContents(FSFile file) {
		FATFile fatFile = (FATFile)file;
		
		if (fatFile.isEndOfFile()) {
			return null;
		}
		
		int sectorNumber = startOfCluster(fatFile.getCurrentCluster())
				+ fatFile.getCurrentSector();
		int len = (int)(fatFile.getDataLeft() < bpb_bytesPerSector
				? fatFile.getDataLeft() : bpb_bytesPerSector);
		byte[] data = ioManager.readSector(sectorNumber, len);
		
		fatFile.advancePosition(data.length);
		
		return data;
	}
	
	@Override
	public FSFile getFile(FSDirectoryEntry file) {
		if (!file.isFile()) {
			return null;
		}
		
		FATDirectoryEntry fatDE = (FATDirectoryEntry)file;
		int firstCluster = fatDE.getFirstCluster();
		
		FATFile fatFile = new FATFile();
		fatFile.setFileName(file.getName());
		fatFile.setClusterChain(getClusterChain(firstCluster));
		fatFile.setFileSize(fatDE.getFileSize());
		fatFile.setParent(file.getParent());
		fatFile.setSectorsPerCluster(bpb_sectorsPerCluster);
		
		return fatFile;
	}
	
	@Override
	public FSDirectory getDirectory(FSDirectoryEntry dir) {
		if (!dir.isDirectory()) {
			return null;
		}
		
		FATDirectoryEntry fatDE = (FATDirectoryEntry)dir;
		
		FATDirectory fatDir = new FATDirectory();
		List<Integer> clusterChain = getClusterChain(fatDE.getFirstCluster());
		
		for (Integer cluster : clusterChain) {
			for (int i = 0; i < bpb_sectorsPerCluster; i++) {
				fatDir.addEntries(ioManager.readSector(startOfCluster(cluster) + i,
						bpb_bytesPerSector));
			}
		}
		
		fatDir.setName(fatDE.getName());
		
		return fatDir;
	}
	
	@Override
	public void deleteFile(FSDirectoryEntry file) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void undeleteFile(FSDirectoryEntry file) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void fullDeleteFile(FSDirectoryEntry file) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void recover() {
		// TODO Auto-generated method stub
		System.out.println("This action is not yet implemented.");
	}
	
	@Override
	public void unmount() {
		ioManager.close();
	}
	
	private List<Integer> getClusterChain(int firstCluster) {
		List<Integer> clusterChain = new LinkedList<Integer>();
		int fatEntry = firstCluster;
		
		while (!isEndOfClusterChain(fatEntry)) {
			clusterChain.add(fatEntry);
			fatEntry = getFATEntry(fatEntry);
		}
		
		return clusterChain;
	}
}
