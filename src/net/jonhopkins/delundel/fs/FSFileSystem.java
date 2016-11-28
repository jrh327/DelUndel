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
package net.jonhopkins.delundel.fs;

public interface FSFileSystem {
	
	/**
	 * Set the handle to the filesystem being worked with.
	 * 
	 * @param manager
	 */
	public void setIOManager(FSIOManager manager);
	
	/**
	 * Get the root directory of the file system.
	 * 
	 * @return
	 */
	public FSDirectory getRootDirectory();
	
	/**
	 * Read a portion of the given file. The position within 
	 * the file will be advanced by the number of bytes read.
	 * 
	 * @param file The file whose contents are to be retrieved
	 * @return Part of the contents of the file
	 */
	public byte[] getFileContents(FSFile file);
	
	/**
	 * Get a stream to a file from a directory entry.
	 * 
	 * @param file The directory entry for the file
	 * @return The file object
	 */
	public FSFile getFile(FSDirectoryEntry file);
	
	/**
	 * Get a directory from a directory entry.
	 *  
	 * @param dir The directory entry for the directory
	 * @return The directory object
	 */
	public FSDirectory getDirectory(FSDirectoryEntry dir);
	
	/**
	 * Mark a file as deleted.
	 * 
	 * @param file The file to delete
	 */
	public void deleteFile(FSDirectoryEntry file);
	
	/**
	 * Restore a deleted file.
	 * <p>
	 * The results of this procedure are not guaranteed in the case of a file 
	 * whose data sectors may have been overwritten by another file.
	 * <p>
	 * If the given file is not deleted, nothing happens.
	 * 
	 * @param file The file to undelete
	 */
	public void undeleteFile(FSDirectoryEntry file);
	
	/**
	 * Overwrite a file's contents with zeroes and delete all trace of the 
	 * file within the filesystem.
	 * 
	 * @param file The file to delete
	 */
	public void fullDeleteFile(FSDirectoryEntry file);
	
	/**
	 * Attempt to recover a broken filesystem.
	 * <p>
	 * May not be implemented for all filesystems.
	 */
	public void recover();
	
	/**
	 * Get the type of filesystem being used.
	 * 
	 * @return The type of filesystem
	 */
	public String getFileSystemType();
	
	/**
	 * Get the name of the filesystem, if one is set.
	 * 
	 * @return The name of the filesystem
	 */
	public String getFileSystemName();
	
	/**
	 * Release the filesystem being used.
	 */
	public void unmount();
}
