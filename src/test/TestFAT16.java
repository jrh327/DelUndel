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

import net.jonhopkins.delundel.fs.FSDirectory;
import net.jonhopkins.delundel.fs.FSFileSystem;
import net.jonhopkins.delundel.fs.fat.FAT;
import net.jonhopkins.delundel.fs.fat.FATFile;
import test.resources.TestDataFAT16;

public class TestFAT16 extends Test {
	
	private FSFileSystem fs;
	private FSDirectory root;
	
	public void runTests() {
		before();
		
		testIsFAT16();
		testRootHasFourEntries();
		testRootEntryNames();
		testDirectoryEntryLongName();
		testDirectoryEntryLongNameAcrossSectors();
		testGetContentsOfFile();
		
		after();
	}
	
	@Override
	public void before() {
		fs = FAT.getFAT(new TestIOManager(new TestDataFAT16()));
	}
	
	public void testIsFAT16() {
		printStartOfTest("testIsFAT16");
		assertEquals(fs.getFileSystemType() + " == FAT16", fs.getFileSystemType(), "FAT16");
		printEndOfPassedTest();
	}
	
	public void testRootHasFourEntries() {
		printStartOfTest("testRootHasFourEntries");
		root = fs.getRootDirectory();
		assertTrue(root.listDirectory().size() == 4);
		printEndOfPassedTest();
	}
	
	public void testRootEntryNames() {
		printStartOfTest("testRootEntryNames");
		assertEquals("first directory entry is 'this_is_a_test.txt'",
				root.listDirectory().get(0).getName(), "this_is_a_test.txt");
		assertEquals("second directory entry is 'test2.txt'",
				root.listDirectory().get(1).getName(), "test2.txt");
		assertEquals("third directory entry is 'this_is_a_test_also.txt'",
				root.listDirectory().get(2).getName(), "this_is_a_test_also.txt");
		assertEquals("fourth directory entry is " + (char)0xe5 + "'eleted.txt'",
				root.listDirectory().get(3).getName(), (char)0xe5 + "eleted.txt");
		printEndOfPassedTest();
	}
	
	public void testDirectoryEntryLongName() {
		printStartOfTest("testDirectoryEntryLongName");
		assertEquals("first directory entry is 'this_is_a_test.txt'",
				root.listDirectory().get(0).getName(), "this_is_a_test.txt");
		printEndOfPassedTest();
	}
	
	public void testDirectoryEntryLongNameAcrossSectors() {
		printStartOfTest("testDirectoryEntryLongNameAcrossSectors");
		assertEquals("third directory entry is 'this_is_a_test_also.txt'",
				root.listDirectory().get(2).getName(), "this_is_a_test_also.txt");
		printEndOfPassedTest();
	}
	
	public void testGetContentsOfFile() {
		printStartOfTest("testGetContentsOfFile");
		
		FATFile file = (FATFile)fs.getFile(root.listDirectory().get(0));
		long fileSize = file.getFileSize();
		
		int bytesRead = 0;
		byte[] sector = fs.getFileContents(file);
		bytesRead += sector.length;
		
		assertTrue("this_is_a_test.txt contains \"this is the text in the file this_is_a_test.txt.\"",
				new String(sector)
						.startsWith("this is the text in the file this_is_a_test.txt."));
		
		while (!file.isEndOfFile()) {
			sector = fs.getFileContents(file);
			bytesRead += sector.length;
		}
		
		assertTrue("total bytes read from this_is_a_test.txt == " + fileSize, bytesRead == fileSize);
		
		printEndOfPassedTest();
	}
	
	@Override
	public void after() {
		
	}
	
	private void printStartOfTest(String testName) {
		System.out.println("---------------------------");
		System.out.println("Running test: " + testName);
	}
	
	private void printEndOfPassedTest() {
		System.out.println("Passed!");
		System.out.println("---------------------------");
	}
}
