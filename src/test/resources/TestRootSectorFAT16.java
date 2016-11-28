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

public class TestRootSectorFAT16 extends TestRootSector {
	@Override
	public byte[] getRootSector(int sectorNumber) {
		switch (sectorNumber) {
		case 0:
			return new byte[] {
				// entry 1 - lfn
					0x42, // sequence number
					't', 0, '.', 0, 't', 0, 'x', 0, 't', 0, // name 1
					0x0f, // file attributes (READ_ONLY|HIDDEN|SYSTEM|VOLUME_LABEL)
					0, // type
					125, // checksum
					0, 0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
					(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, // name 2
					0, 0, // first cluster
					(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, // name 3
				// entry 2 - lfn
					0x01, // sequence number
					't', 0, 'h', 0, 'i', 0, 's', 0, '_', 0, // name 1
					0x0f, // file attributes (READ_ONLY|HIDDEN|SYSTEM|VOLUME_LABEL)
					0, // type
					125, // checksum
					'i', 0, 's', 0, '_', 0, 'a', 0, '_', 0, 't', 0, // name 2
					0, 0, // first cluster
					'e', 0, 's', 0, // name 3
				// entry 3 - shortname
					't', 'h', 'i', 's', '_', 'i', '~', '1', // filename, 8 ASCII chars
					't', 'x', 't', // extension, 3 ASCII chars
					1, // fileAttributes
					0, // userAttributes
					0, // originalFirstCharacter
					0, 0, // createdTime
					0, 0, // createdDate
					0, 0, // owner
					0, 0, // extendedAttributes
					0, 0, // modifiedTime
					0, 0, // modifiedDate
					2, 0, // firstCluster
					0x7b, 0x30, 0, 0, // fileSize
				// entry 4 - shortname
					't', 'e', 's', 't', '2', ' ', ' ', ' ', // filename, 8 ASCII chars
					't', 'x', 't', // extension, 3 ASCII chars
					1, // fileAttributes
					0, // userAttributes
					0, // originalFirstCharacter
					0, 0, // createdTime
					0, 0, // createdDate
					0, 0, // owner
					0, 0, // extendedAttributes
					0, 0, // modifiedTime
					0, 0, // modifiedDate
					9, 0, // firstCluster
					8, 6, 0xd, 0, // fileSize
				// entry 5
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 6
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 7
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 8
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 9
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 10
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 11
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 12
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 13
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 14
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 15
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 16 - lfn
					0x42, // sequence number
					't', 0, '_', 0, 'a', 0, 'l', 0, 's', 0, // name 1
					0x0f, // file attributes (READ_ONLY|HIDDEN|SYSTEM|VOLUME_LABEL)
					0, // type
					93, // checksum
					'o', 0, '.', 0, 't', 0, 'x', 0, 't', 0, 0, 0, // name 2
					0, 0, // first cluster
					(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, // name 3
			};
		case 1:
			return new byte[] {
				// entry 17 - lfn
					0x01, // sequence number
					't', 0, 'h', 0, 'i', 0, 's', 0, '_', 0, // name 1
					0x0f, // file attributes (READ_ONLY|HIDDEN|SYSTEM|VOLUME_LABEL)
					0, // type
					93, // checksum
					'i', 0, 's', 0, '_', 0, 'a', 0, '_', 0, 't', 0, // name 2
					0, 0, // first cluster
					'e', 0, 's', 0, // name 3
				// entry 18 - shortname
					't', 'h', 'i', 's', '_', 'i', '~', '2', // filename, 8 ASCII chars
					't', 'x', 't', // extension, 3 ASCII chars
					1, // fileAttributes
					0, // userAttributes
					0, // originalFirstCharacter
					0, 0, // createdTime
					0, 0, // createdDate
					0, 0, // owner
					0, 0, // extendedAttributes
					0, 0, // modifiedTime
					0, 0, // modifiedDate
					0xB, 0, // firstCluster
					5, 3, 0xc, 0, // fileSize
				// entry 19 - deleted
					(byte)0xe5, 'e', 'l', 'e', 't', 'e', 'd', ' ', // filename, 8 ASCII chars
					't', 'x', 't', // extension, 3 ASCII chars
					1, // fileAttributes
					0, // userAttributes
					'd', // originalFirstCharacter
					0, 0, // createdTime
					0, 0, // createdDate
					0, 0, // owner
					0, 0, // extendedAttributes
					0, 0, // modifiedTime
					0, 0, // modifiedDate
					0x13, 0, // firstCluster
					12, 0, 0, 0, // fileSize
				// entry 20
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 21
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 22
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 23
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 24
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 25
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 26
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 27
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 28
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 29
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 30
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 31
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				// entry 32
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			};
		default:
			return null;
		}
	}
}
