package com.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	private List<File> queue = new ArrayList<File>();
	
	@SuppressWarnings("unused")
	public void addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			// ===================================================
			// Only index text files
			// ===================================================
			if(filename.endsWith(".ds_store")){
				System.out.println("Skipped " + filename);
			}
			else if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".xml")
					|| filename.endsWith(".txt") || filename.endsWith("")) {
				queue.add(file);
			} else {
				System.out.println("Skipped " + filename);
			}
		}
	}
	
	public List<File> getQueue() {
		return queue;
	}

	public void setQueue(List<File> queue) {
		this.queue = queue;
	}
	
	public void clear(){
		this.queue.clear();
	}
}
