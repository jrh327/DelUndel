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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.jonhopkins.delundel.fs.FSDetector;
import net.jonhopkins.delundel.fs.FSDirectory;
import net.jonhopkins.delundel.fs.FSDirectoryEntry;
import net.jonhopkins.delundel.fs.FSFile;
import net.jonhopkins.delundel.fs.FSFileSystem;

public class FSGui extends JFrame {
	private static final long serialVersionUID = 1L;
	private FSDetector filesystemDetector;
	private FSDirectory pwd;
	private FSFileSystem fs;
	
	private JPanel mainPanel;
	private JPanel fsPanel;
	private JPanel filePanel;
	private JPanel fileViewPanel;
	
	public FSGui(FSDetector fsDetector) {
		filesystemDetector = fsDetector;
	}
	
	public void start() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initComponents();
                setVisible(true);
            }
        });
	}
	
	public void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		
		fsPanel = new JPanel();
		fsPanel.setLayout(new FlowLayout());
		
		filePanel = new JPanel();
		filePanel.setLayout(new FlowLayout());
		
		fileViewPanel = new JPanel();
		fileViewPanel.setLayout(new FlowLayout());
		
		setTitle("DelUndel");
		setSize(400, 400);
		setLocationRelativeTo(null);
		
		add(mainPanel, BorderLayout.CENTER);
		
		displayFileSystems();
	}
	
	public void displayFileSystems() {
		FSFileSystem[] fileSystems = filesystemDetector.detectFileSystems();
		
		fsPanel.removeAll();
		
		for (FSFileSystem fs : fileSystems) {
			addFS(fs);
		}
		
		setPanel(fsPanel);
	}
	
	public void addFS(final FSFileSystem fs) {
		JButton btnFS = new JButton(fs.getFileSystemName());
		btnFS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clickFileSystem(fs);
			}
		});
		fsPanel.add(btnFS);
	}
	
	public void addEntry(final FSDirectoryEntry entry) {
		JButton btnFile = new JButton(entry.getName());
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				clickEntry(entry);
			}
		});
		filePanel.add(btnFile);
	}
	
	public void clearScreen() {
		mainPanel.removeAll();
		revalidate();
		repaint();
	}
	
	public void clickFileSystem(FSFileSystem filesystem) {
		fs = filesystem;
		setTitle("DelUndel - " + fs.getFileSystemName());
		clickDirectory(fs.getRootDirectory());
	}
	
	public void clickEntry(FSDirectoryEntry entry) {
		if (entry.isDirectory()) {
			clickDirectory(fs.getDirectory(entry));
		} else if (entry.isFile()) {
			clickFile(fs.getFile(entry));
		}
	}
	
	public void clickDirectory(FSDirectory dir) {
		clearScreen();
		List<FSDirectoryEntry> entries = dir.listDirectory();
		
		filePanel.removeAll();
		
		for (FSDirectoryEntry entry : entries) {
			addEntry(entry);
		}
		
		setPanel(filePanel);
	}
	
	public void clickFile(FSFile file) {
		clearScreen();
		displayData(file);
	}
	
	public void clickBack() {
		pwd = pwd.getParent();
		if (pwd == null) {
			displayFileSystems();
		} else {
			clickDirectory(pwd);
		}
	}
	
	public void displayData(FSFile file) {
		// if large file, try to stream in data as user scrolls
		// for images, just grab the whole thing at once
		clearScreen();
		
		byte[] data = fs.getFileContents(file);
		if (file.isText()) {
			addTextBox(data);
		} else if (file.isImage()) {
			addImageBox(data);
		} else {
			hexDump(data);
		}
		
		setPanel(fileViewPanel);
	}
	
	public void addTextBox(byte[] data) {
		fileViewPanel.removeAll();
		JTextArea txt = new JTextArea();
		txt.setText(new String(data));
		fileViewPanel.add(txt);
	}
	
	public void addImageBox(byte[] data) {
		fileViewPanel.removeAll();
		ImageIcon img = new ImageIcon(data);
		fileViewPanel.add(new JLabel(img));
	}
	
	public void hexDump(byte[] data) {
		fileViewPanel.removeAll();
		JTextArea txt = new JTextArea();
		txt.setText(new String(data));
		fileViewPanel.add(txt);
	}
	
	private void setPanel(JPanel panel) {
		mainPanel.add(panel);
		revalidate();
		repaint();
	}
}
