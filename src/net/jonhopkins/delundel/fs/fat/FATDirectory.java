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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.jonhopkins.delundel.fs.FSDirectory;
import net.jonhopkins.delundel.fs.FSDirectoryEntry;

public class FATDirectory extends FSDirectory {
	private FATDirectoryEntry partialDirEntry = null;
	
	void setName(String dirName) {
		this.dirName = dirName;
	}
	
	void setParent(FSDirectory parent) {
		this.parent = parent;
	}
	
	void addEntries(byte[] directorySector) {
		List<FSDirectoryEntry> dirEntries = new ArrayList<FSDirectoryEntry>();
		
		for (int i = 0; i < directorySector.length; i += 32) {
			FATDirectoryEntry de;
			if (partialDirEntry != null) {
				de = partialDirEntry;
				partialDirEntry = null;
			} else {
				de = new FATDirectoryEntry(this);
			}
			
			boolean finishedCreating = false;
			while (!finishedCreating) {
				if (i >= directorySector.length) {
					break;
				}
				
				byte[] directoryEntry = Arrays.copyOfRange(directorySector, i, i + 32);
				
				// make sure we aren't trying to build an entry with no data
				// normally should stop all processing upon finding an empty
				// directory entry, but gonna keep going just in case
				if (emptyEntry(directoryEntry)) {
					finishedCreating = true;
					break;
				}
				
				// longname entries are listed in reverse order, before the 
				// corresponding shortname entry, add each new entry to the 
				// beginning of the list until the shortname entry is found
				if (de.addDataStructure(directoryEntry)) {
					finishedCreating = true;
				} else {
					i += 32;
				}
			}
			if (!finishedCreating) {
				// in case an entry was cut off by the end of the given sector
				// save the progress to finish up in the next sector
				partialDirEntry = de;
			} else {
				de.generateName();
				if (de.verifyDataStructures()) {
					dirEntries.add(de);
				}
			}
		}
		
		this.entries.addAll(dirEntries);
	}
	
	private boolean emptyEntry(byte[] entry) {
		for (int i = 0; i < entry.length; i++) {
			if (entry[i] != 0) {
				return false;
			}
		}
		return true;
	}
}
