package org.smartfrog.vast.architecture.archive;

import java.io.IOException;
import java.io.File;

abstract class BaseArchive implements Archive {
	/**
	 * Adds a file or a folder to the archive. If it's a folder it will be added recursively.
	 * @param inPath The path to the file/folder.
	 * @throws java.io.IOException
	 */
	public void add(String inPath) throws IOException {
		File file = new File(inPath);
		if (file.isFile()) {
			addFile(inPath);
		} else if (file.isDirectory()) {
			addFolder(inPath, true);
		}
	}

	/**
	 * Adds a file to the archive.
	 * @param inPath Path to the file.
	 * @throws IOException
	 */
	public void addFile(String inPath) throws IOException {
		addFile(inPath, "");
	}

	/**
	 * Adds a file to the archive.
	 * @param inPath The absolute path to the file.
	 * @param inRelPath The relative path (within the archive) to the file.
	 * @throws IOException
	 */
	public void addFile(String inPath, String inRelPath) throws IOException {
		File file = new File(inPath);
		if (file.isFile())
			putNextEntry(inPath, inRelPath);
	}

	/**
	 * Adds a folder to the archive.
	 * @param inPath Path to the folder.
	 * @param inRecursively Add the subfolders recursively?
	 * @throws IOException
	 */
	public void addFolder(String inPath, boolean inRecursively) throws IOException {
		addFolder(inPath, inRecursively, "");
	}

	/**
	 * Adds a folder to the archive.
	 * @param inPath The absolute path to the folder.
	 * @param inRecursively Add the subfolders recursively?
	 * @param inRelPath The relative path (within the archive) to the folder.
	 * @throws IOException
	 */
	public void addFolder(String inPath, boolean inRecursively, String inRelPath) throws IOException {
		File folder = new File(inPath);

		// add all files of this folder
		if (folder.isDirectory()) {
			String curFolder = String.format("%s%s/", inRelPath, folder.getName());

			// add this folder
			putNextEntry(inPath, curFolder);

			for(File file : folder.listFiles()) {
				if (file.isDirectory() && inRecursively)
					addFolder(file.getPath(), true, curFolder);
				else 
					addFile(file.getPath(), String.format("%s%s", curFolder, file.getName()));
			}
		}
	}
}
