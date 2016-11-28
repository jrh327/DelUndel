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

import java.util.List;

import net.jonhopkins.delundel.fs.FSDirectory;
import net.jonhopkins.delundel.fs.FSFile;

public class FATFile extends FSFile {
	private List<Integer> clusterChain;
	private int sectorsPerCluster;
	private int currentCluster = 0;
	private int currentSector = 0;
	private long dataLeft;
	
	void setFileName(String fileName) {
		this.fileName = fileName;
		
		detectFileType();
	}
	
	void setParent(FSDirectory parent) {
		this.parent = parent;
	}
	
	void setClusterChain(List<Integer> clusterChain) {
		this.clusterChain = clusterChain;
	}
	
	void setFileSize(long fileSize) {
		this.fileSize = fileSize;
		this.dataLeft = fileSize;
	}
	
	void setSectorsPerCluster(int sectorsPerCluster) {
		this.sectorsPerCluster = sectorsPerCluster;
	}
	
	public boolean isEndOfFile() {
		return dataLeft <= 0;
	}
	
	int getCurrentCluster() {
		return clusterChain.get(currentCluster);
	}
	
	/**
	 * Get the current sector within the current cluster.
	 * 
	 * @return The current sector
	 */
	int getCurrentSector() {
		return currentSector;
	}
	
	long getDataLeft() {
		return dataLeft;
	}
	
	void advancePosition(int amount) {
		dataLeft -= amount;
		currentSector++;
		if (currentSector >= sectorsPerCluster) {
			currentSector = 0;
			currentCluster++;
		}
	}
}
