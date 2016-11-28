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

import java.util.Arrays;

public class TestFATSectorFAT16 extends TestFATSector {
	@Override
	public byte[] getFATSector(int sectorNumber) {
		switch(sectorNumber) {
		case 0:
			// signature: 0, 1
			// free clusters: 12, 1b, 1c, 1e, 1f
			// bad clusters: 17, 18, 1d
			// file1: 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8
			//   dec: 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8
			//   rel: 0 -> 1 -> 2 -> 3 -> 4 -> 5 -> 6
			// file2: 9 -> a -> 14 -> 15 -> 16 -> 19 -> 1a
			//   dec: 9 -> 10 -> 20 -> 21 -> 22 -> 25 -> 26
			//   rel: 7 -> 8 -> 18 -> 19 -> 20 -> 23 -> 24
			// file3: b -> c -> d -> e -> f -> 10 -> 11
			//   dec: 11 -> 12 -> 13 -> 14 -> 15 -> 16 -> 17
			//   rel: 9 -> 10 -> 11 -> 12 -> 13 -> 14 -> 15
			// file4: 13
			//   dec: 19
			//   rel: 17
			return Arrays.copyOf(new byte[] {
					/* 00 */(byte)0xF0, (byte)0xFF, /* 01 */(byte)0xFF, (byte)0xFF,
					/* 02 */(byte)0x03, (byte)0x00, /* 03 */(byte)0x04, (byte)0x00,	
					/* 04 */(byte)0x05, (byte)0x00, /* 05 */(byte)0x06, (byte)0x00,
					/* 06 */(byte)0x07, (byte)0x00, /* 07 */(byte)0x08, (byte)0x00,
					/* 08 */(byte)0xFF, (byte)0xFF, /* 09 */(byte)0x0A, (byte)0x00,
					/* 0A */(byte)0x14, (byte)0x00, /* 0B */(byte)0x0C, (byte)0x00,	
					/* 0C */(byte)0x0D, (byte)0x00, /* 0D */(byte)0x0E, (byte)0x00,
					/* 0E */(byte)0x0F, (byte)0x00, /* 0F */(byte)0x10, (byte)0x00,
					/* 10 */(byte)0x11, (byte)0x00, /* 11 */(byte)0xFF, (byte)0xFF,
					/* 12 */(byte)0x00, (byte)0x00, /* 13 */(byte)0xFF, (byte)0xFF,	
					/* 14 */(byte)0x15, (byte)0x00, /* 15 */(byte)0x16, (byte)0x00,
					/* 16 */(byte)0x19, (byte)0x00, /* 17 */(byte)0xF7, (byte)0xFF,
					/* 18 */(byte)0xF7, (byte)0xFF, /* 19 */(byte)0x1A, (byte)0x00,
					/* 1A */(byte)0xFF, (byte)0xFF, /* 1B */(byte)0x00, (byte)0x00,	
					/* 1C */(byte)0x00, (byte)0x00, /* 1D */(byte)0xF7, (byte)0xFF,
					/* 1E */(byte)0x00, (byte)0x00, /* 1F */(byte)0x00, (byte)0x00,
			}, 512);
		default:
			return null;
		}
	}
}
