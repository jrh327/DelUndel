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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import net.jonhopkins.delundel.fs.FSIOManager;

public class FATIOManager implements FSIOManager {
	private RandomAccessFile handle;
	private int lastReadSectorNumber;
	private byte[] lastReadSector;
	
	public FATIOManager(String filename) {
		try {
			handle = new RandomAccessFile(filename, "r");
			lastReadSectorNumber = -1;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Override
	public byte[] readSector(int sectorNumber, int size) {
		if (sectorNumber < 0) {
			return null;
		}
		
		if (sectorNumber == lastReadSectorNumber) {
			return Arrays.copyOf(lastReadSector, lastReadSector.length);
		}
		
		lastReadSector = new byte[size];
		try {
			handle.seek(sectorNumber * size);
			handle.readFully(lastReadSector);
			lastReadSectorNumber = sectorNumber;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return Arrays.copyOf(lastReadSector, size);
	}
	
	@Override
	public void writeSector(int sectorNumber, byte[] data) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void close() {
		try {
			handle.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
