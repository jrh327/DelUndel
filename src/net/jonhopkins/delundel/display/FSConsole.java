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
package net.jonhopkins.delundel.display;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jonhopkins.delundel.fs.FSDetector;
import net.jonhopkins.delundel.fs.FSDirectory;
import net.jonhopkins.delundel.fs.FSDirectoryEntry;
import net.jonhopkins.delundel.fs.FSFileSystem;
import net.jonhopkins.delundel.fs.fat.FATFile;

public class FSConsole {
	private FSDetector filesystemDetector;
	private FSFileSystem[] filesystems;
	private FSFileSystem fs;
	private FSDirectory pwd;
	
	private final String COMMAND_CHANGE_DIR = "cd";
	private final String COMMAND_COPY = "cp";
	private final String COMMAND_HELP = "help";
	private final String COMMAND_LIST_DIR = "ls";
	private final String COMMAND_MOUNT_FS = "mount";
	private final String COMMAND_PRINT_WORKING_DIR = "pwd";
	private final String COMMAND_UNMOUNT_FS = "unmount";
	private final String ERROR_ALREADY_MOUNTED = "A filesystem is already mounted";
	private final String ERROR_FAILED_TO_COPY = "Error copying to file on host";
	private final String ERROR_FILE_NOT_FOUND = "No such file or directory";
	private final String ERROR_FS_NOT_FOUND = "No such filesystem found";
	private final String ERROR_MISSING_PARAMS = "Missing parameters";
	private final String ERROR_NO_MOUNTED_FS = "No filesystem mounted";
	private final String ERROR_NOT_DIR = "Not a directory";
	private final String ERROR_NOT_FILE = "Not a file";
	private final String ERROR_UNRECOGNIZED_COMMAND = "Command not recognized. Type 'help' for more information.";
	private final String SUCCESS_COPY = "Successfully copied the file";
	private final String SUCCESS_MOUNT = "Successfully mounted filesystem";
	private final String SUCCESS_UNMOUNT = "Successfully unmounted filesystem";
	
	public FSConsole(FSDetector fsDetector) {
		filesystemDetector = fsDetector;
	}
	
	public void start() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command;
		String prefix = "> ";
		
		while (true) {
			System.out.print(prefix);
			try {
				command = br.readLine();
				if (command.equals("exit")) {
					break;
				}
				doCommand(command);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	private void doCommand(String command) {
		if (command.trim().isEmpty()) {
			return;
		}
		
		List<String> paramList = tokenizeCommand(command);
		if (paramList.isEmpty()) {
			return;
		}
		
		String com = paramList.get(0);
		switch (com) {
		case COMMAND_CHANGE_DIR:
			if (fs == null) {
				error(COMMAND_CHANGE_DIR, ERROR_NO_MOUNTED_FS);
				break;
			}
			
			// jump back to root by leaving off destination
			if (paramList.size() == 1) {
				pwd = fs.getRootDirectory();
				break;
			}
			
			String destDir = paramList.get(1);
			// . == current directory
			if (destDir.equals(".")) {
				break;
			}
			// .. == parent directory
			if (destDir.equals("..")) {
				pwd = pwd.getParent();
				break;
			}
			
			boolean found = false;
			for (FSDirectoryEntry entry : pwd.listDirectory()) {
				if (entry.getName().equals(destDir)) {
					found = true;
					if (entry.isDirectory()) {
						pwd = fs.getDirectory(entry);
					} else {
						error(COMMAND_CHANGE_DIR, destDir, ERROR_NOT_DIR);
					}
					break;
				}
			}
			
			// tried to change to a non-existent location
			if (!found) {
				error(COMMAND_CHANGE_DIR, destDir, ERROR_FILE_NOT_FOUND);
			}
			break;
		case COMMAND_COPY:
			if (fs == null) {
				error(COMMAND_COPY, ERROR_NO_MOUNTED_FS);
				break;
			}
			
			if (paramList.size() < 3) {
				error(COMMAND_COPY, ERROR_MISSING_PARAMS);
				break;
			}
			
			String sourceFile = paramList.get(1);
			String destFile = paramList.get(2);
			FATFile fileToCopy = null;
			
			boolean sourceExists = false;
			for (FSDirectoryEntry entry : pwd.listDirectory()) {
				if (entry.getName().equals(sourceFile)) {
					sourceExists = true;
					if (entry.isDirectory()) {
						error(COMMAND_COPY, sourceFile, ERROR_NOT_FILE);
					} else {
						fileToCopy = (FATFile)fs.getFile(entry);
					}
					break;
				}
			}
			
			if (!sourceExists) {
				error(COMMAND_COPY, ERROR_FILE_NOT_FOUND);
				break;
			}
			if (fileToCopy == null) {
				break;
			}
			
			try {
				FileOutputStream fos = new FileOutputStream(destFile);
				while (!fileToCopy.isEndOfFile()) {
					fos.write(fs.getFileContents(fileToCopy));
				}
				fos.close();
				info(COMMAND_COPY, SUCCESS_COPY);
			} catch (IOException e) {
				e.printStackTrace();
				error(COMMAND_COPY, ERROR_FAILED_TO_COPY);
			}
			break;
		case COMMAND_HELP:
			info(COMMAND_CHANGE_DIR, "<destination directory>");
			info("\tChange the current directory to the specified directory");
			info("\tSpecial cases:");
			info("\t\t<blank> : Change to the root of the filesystem");
			info("\t\t      . : Nothing happens");
			info("\t\t     .. : Change to the parent of the current location");
			
			info(COMMAND_COPY, "<source file> <destination location>");
			info("\tCopy the specified file to the specified location on the host filesystem");
			
			info(COMMAND_LIST_DIR, "Print information about the files in the current directory");
			
			info(COMMAND_HELP, "Print this help message");
			
			info(COMMAND_MOUNT_FS, "<target filesystem>");
			info("\tMount the specified filesystem to access its files");
			info("\tSpecial cases:");
			info("\t\t<blank> : List the available filesystems");
			
			info(COMMAND_PRINT_WORKING_DIR, "Print the name of the current directory");
			
			info(COMMAND_UNMOUNT_FS, "Unmount the current filesystem");
			
			break;
		case COMMAND_LIST_DIR:
			if (fs == null) {
				error(COMMAND_LIST_DIR, ERROR_NO_MOUNTED_FS);
				break;
			}
			
			for (FSDirectoryEntry entry : pwd.listDirectory()) {
				System.out.format("%c %s %s %s %s %d bytes\n",
						(entry.isDirectory() ? 'd' : ' '),
						entry.getName(),
						entry.getDateTimeCreated(),
						entry.getDateTimeModified(),
						entry.getDateAccessed(),
						entry.getFileSize()
						);
			}
			break;
		case COMMAND_MOUNT_FS:
			if (paramList.size() == 1) {
				// redo detection every time filesystems are listed
				filesystems = filesystemDetector.detectFileSystems();
				System.out.println("mount: Available filesystems:");
				for (FSFileSystem filesystem : filesystems) {
					System.out.format("  %s\n", filesystem.getFileSystemName());
				}
				break;
			} else if (fs == null) {
				String destFS = paramList.get(1);
				for (FSFileSystem filesystem : filesystems) {
					if (filesystem.getFileSystemName().equals(destFS)) {
						fs = filesystem;
						pwd = fs.getRootDirectory();
						info(COMMAND_MOUNT_FS, destFS, SUCCESS_MOUNT);
						break;
					}
				}
				if (fs == null) {
					error(COMMAND_MOUNT_FS, destFS, ERROR_FS_NOT_FOUND);
				}
			} else {
				error(COMMAND_MOUNT_FS, ERROR_ALREADY_MOUNTED);
			}
			break;
		case COMMAND_PRINT_WORKING_DIR:
			if (fs == null) {
				error(COMMAND_PRINT_WORKING_DIR, ERROR_NO_MOUNTED_FS);
				break;
			}
			info(pwd.getName());
			break;
		case COMMAND_UNMOUNT_FS:
			if (fs == null) {
				error(COMMAND_UNMOUNT_FS, ERROR_NO_MOUNTED_FS);
				break;
			}
			
			fs.unmount();
			info(COMMAND_UNMOUNT_FS, fs.getFileSystemName(), SUCCESS_UNMOUNT);
			fs = null;
			pwd = null;
			break;
		default:
			error(com, ERROR_UNRECOGNIZED_COMMAND);
			break;
		}
	}
	
	private List<String> tokenizeCommand(String command) {
		List<String> list = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
		while (m.find()) {
		    list.add(m.group(1).replace("\"", ""));
		}
		return list;
	}
	
	private void info(String message) {
		System.out.format("%s\n", message);
	}
	
	private void info(String command, String message) {
		System.out.format("%s: %s\n", command, message);
	}
	
	private void info(String command, String parameter, String message) {
		System.out.format("%s: %s: %s\n", command, parameter, message);
	}
	
	private void error(String command, String message) {
		System.out.format("%s: %s\n", command, message);
	}
	
	private void error(String command, String parameter, String message) {
		System.out.format("%s: %s: %s\n", command, parameter, message);
	}
}
