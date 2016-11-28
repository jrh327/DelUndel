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
package test.resources;

public class TestBootSectorFAT12 extends TestBootSector {
	
	@Override
	public byte[] getBootSector() {
		return new byte[] {
				0, 0, 0, // bs_jmpBoot
				'J', 'H', 'O', 'P', 'W', 'A', 'S', 'X', // bs_OEMName
				0, 2, // bpb_bytesPerSector
				1, // bpb_sectorsPerCluster
				1, 0, // bpb_reservedSectorCount
				1, // bpb_FATCount
				32, 0, // bpb_rootEntryCount
				0, 1, // bpb_totalSectorCount_16
				0, // bpb_mediaType
				1, 0, // bpb_FATSize_16
				0, 0, // bpb_sectorsPerTrack
				0, 0, // bpb_headCount
				0, 0, 0, 0, // bpb_hiddenSectorCount
				0, 0, 0, 0, // bpb_totalSectorCount_32
				0, // bs_driveNumber
				0, // bs_reserved1
				0, // bs_bootSignature
				0, 0, 0, 0, // bs_volumeID
				'*', 'T', 'E', 'S', 'T', 'F', 'A', 'T', '1', '2', '*', // bs_volumeLabel
				'F', 'A', 'T', '1', '2', ' ', ' ', ' ', // bs_fileSystemType
		};
	}
}
