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
package test;

import net.jonhopkins.delundel.fs.FSIOManager;
import test.resources.TestData;

public class TestIOManager implements FSIOManager {
	
	private TestData data;
	
	public TestIOManager(TestData data) {
		this.data = data;
	}
	
	@Override
	public byte[] readSector(int sectorNumber, int size) {
		switch (sectorNumber) {
		case 0:
			return data.getBootSector();
		case 1:
			// only 1 FAT sector
			return data.getFATSector(0);
		case 2:
		case 3:
			// 2 root sectors
			return data.getRootSector(sectorNumber - 2);
		default:
			// 4 reserved sectors before the user data sectors
			return data.getDataSector(sectorNumber - 4);
		}
	}
	
	@Override
	public void writeSector(int sectorNumber, byte[] data) {
		
	}
	
	@Override
	public void close() {
		
	}
}
