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

public class TestUserDataSectorFAT16 extends TestUserDataSector {
	public static final int i = (512 - 48);
	@Override
	public byte[] getUserDataSector(int sectorNumber) {
		switch (sectorNumber) {
		case 0:
			return Arrays.copyOf(new byte[] {
					't', 'h', 'i', 's', ' ', 'i', 's', ' ', 't', 'h', 'e', ' ',
					't', 'e', 'x', 't', ' ', 'i', 'n', ' ', 't', 'h', 'e', ' ',
					'f', 'i', 'l', 'e', ' ', 't', 'h', 'i', 's', '_', 'i', 's',
					'_', 'a', '_', 't', 'e', 's', 't', '.', 't', 'x', 't', '.',
			}, 512);
		case 24:
			return new byte[123];
		default:
			if (sectorNumber < 0 || sectorNumber > 0x7f00) {
				return null;
			}
			return new byte[512];
		}
	}
}
