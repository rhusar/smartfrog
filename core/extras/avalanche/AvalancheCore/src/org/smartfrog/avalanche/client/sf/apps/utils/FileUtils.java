/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Dec 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileUtils {
	private static Log log = LogFactory.getLog(FileUtils.class);

	/**
	 * 
	 */
	public FileUtils() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static boolean createDir(String directory) {
		directory = directory.replace('\\', File.separatorChar);
		directory = directory.replace('/', File.separatorChar);
		File dir = new File(directory);
		
		if (dir.exists() && dir.isDirectory()) {
			//log.info("The directory " + directory + " exists");
			return true;
		}
		if (dir.isFile()) {
			log.error("Cannot create the directory " + directory);
			log.error("A file with name " + directory + " exists");
			return false;
		}
		
		if (!dir.mkdirs()) {
			log.error("Unable to create the directory " + directory);
			return false;
		}		
		return true;
	}
	
	public static boolean checkDir(File dir) {
		String dirName = dir.getName();
		if (!dir.exists()) {
			log.error("The directory " + dirName + " does not exist");
			return false;
		}
		if (!dir.isDirectory()) {
			log.error(dirName + " is not a directory");
			return false;
		}
		if (!dir.canRead()) {
			log.error("The directory " + dirName + " is not readable");
			return false;
		}
		if (!dir.canWrite()) {
			log.error("The directory " + dirName + " is not writable");
			return false;
		}
		return true;
	}
	
	public static boolean createFile(String newFile) {
		File file = new File(newFile);
		
		if (file.exists() && file.isFile()) {
			return true;
		}
		
		if (file.isDirectory()) {
			log.error(newFile + " is a directory");
			log.error("Cannot create file " + newFile);
			return false;
		}
		
		try {
			if (!file.createNewFile()) {
				log.error("Cannot create the file " + newFile);
				return false;
			}			
		} catch (IOException ioe) {
			log.error(ioe);
			return false;
		}
		
		return true;
	}
	
	public static boolean createFile(File file) throws IOException {
		//File file = new File(newFile);
		
		if (file.exists() && file.isFile()) {
			return true;
		}
		
		if (file.isDirectory()) {
			log.error(file.getAbsolutePath() + " is a directory");
			log.error("Cannot create file " + file.getAbsolutePath());
			return false;
		}
		
		if (!file.createNewFile()) {
				log.error("Cannot create the file " + file.getAbsolutePath());
				throw new IOException("Cannot create the file " + file.getAbsolutePath());				
			}			
			
		return true;
	}
	
	public static String file2String(File file) 
			throws FileNotFoundException, IOException {
		StringBuffer key = new StringBuffer();
		
		if (!file.exists()) {
			log.error("File " + file.getAbsolutePath() + " does not exist");
			return null;
		}
		if  (!file.isFile()) {
			log.error("File " + file.getAbsolutePath() + " is not a file");
			return null;
		}
		if (!file.canRead()) {
			log.error("The file " + file.getAbsolutePath() + " does not have read permissions");			
			return null;
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		int numRead = 0;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			key.append(line);
			key.append(System.getProperty("line.separator"));			
		}		
		reader.close();
		
		return key.toString();
	}
	
	public static boolean writeString2File(String text, File file) {
		String fileName = file.getAbsolutePath();
		if (!file.exists()) {
			log.error("File " +  fileName + " does not exist");
			return false;
		}
		if (!file.isFile()) {
			log.error(fileName + " is not a file");
			return false;
		}
		if (!file.canWrite()) {
			log.error("File " + fileName + " is not writable");
			return false;
		}
		
		FileOutputStream fout;
		PrintStream pStream = null;
		try {
			fout = new FileOutputStream(file);
			pStream = new PrintStream(fout);
			pStream.println(text);			
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
		}
		
		pStream.close();
		
		return true;
	}
	
	public static boolean appendString2File(File file, String str) 
			throws IOException {
		if (!checkFile(file)) {
			log.error("Cannot append to file " + file.getAbsolutePath());
			return false;
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(str);
		writer.newLine();
		writer.close();
		
		return true;
	}
	
	public static boolean checkFile(File file) {
		String fileName = file.getAbsolutePath();
		if (!file.exists()) {
			log.error("File " +  fileName + " does not exist");
			return false;
		}
		if (!file.isFile()) {
			log.error(fileName + " is not a file");
			return false;
		}
		if (!file.canRead()) {
			log.error("File " + fileName + " is not readable");
			return false;
		}
		if (!file.canWrite()) {
			log.error("File " + fileName + " is not writable");
			return false;
		}
		return true;
	}
	
	public static boolean chgPermissions(String fileName, String perms)
			throws IOException, InterruptedException {
		File file = new File (fileName);
		if (!file.exists()) {
			log.error("File " + fileName + "does not exist");
			return false;
		}
		
		Runtime rt = Runtime.getRuntime();
		Process p = rt.exec("chmod " + perms + " " + file.getAbsolutePath());
		int exitVal = 0;
		BufferedReader cmdError = new BufferedReader(
				new InputStreamReader(p.getErrorStream()));
		exitVal = p.waitFor();
		if (exitVal != 0) {
			log.error("Error in changing permissions to " + fileName);
			String line = null;
			String error = null;
			if ((line = cmdError.readLine()) != null) {
				log.error(line);
				error = line;
				while ((line = cmdError.readLine()) != null) {
					log.error(line);
					error = error + "\n" + line;
				}				
				throw new IOException(error);
			}
		}
		return true;
	}
	// Does chown username:username fileName
	public static boolean chgOwner(String fileName, String userName)
	throws IOException, InterruptedException {
		File file = new File (fileName);
		if (!file.exists()) {
			log.error("File " + fileName + "does not exist");
			return false;
		}
		
		Runtime rt = Runtime.getRuntime();
		Process p = rt.exec("chown " + userName + ":" + userName + " " + file.getAbsolutePath());
		int exitVal = 0;
		BufferedReader cmdError = new BufferedReader(
				new InputStreamReader(p.getErrorStream()));
		exitVal = p.waitFor();
		if (exitVal != 0) {
			log.error("Error in changing permissions to " + fileName);
			String line = null;
			String error = null;
			if ((line = cmdError.readLine()) != null) {
				log.error(line);
				error = line;
				while ((line = cmdError.readLine()) != null) {
					log.error(line);
					error = error + "\n" + line;
				}
				throw new IOException(error);
			}
		}
		return true;
	}
	
	public static boolean softLink(String fileName, String destDir, 
			String linkName) throws IOException, InterruptedException {
		File file = new File(fileName);
		if (!file.exists()) {
			log.error("File " + fileName + " does not exist");
			return false;
		}
		if (!file.canRead()) {
			log.error("File " + fileName + " does not have read permissions");
			return false;
		}
		
		File dir = new File(destDir);
		if (!dir.exists()) {
			log.error("Directory " + destDir + " does not exist");
			return false;
		}
		if (!dir.isDirectory()) {
			log.error("Destination directory " + destDir + " is not a directory");
			return false;
		}
		if (!dir.canWrite()) {
			log.error("Cannot write to the directory " + destDir);
			return false;
		}
		
		Runtime rt = Runtime.getRuntime();
		/*int idx = file.getAbsolutePath().lastIndexOf(File.separatorChar);
		String destFile = file.getAbsolutePath().substring(idx);
		destFile = destFile.trim(); */
		String cmd = "ln -s " + file.getAbsolutePath() + " " + linkName;
		log.info("CMD : " + cmd);
		Process p = rt.exec(cmd, null, dir);
		
		int exitVal = 0;
		BufferedReader cmdError = new BufferedReader(
				new InputStreamReader(p.getErrorStream()));
		exitVal = p.waitFor();
		if (exitVal != 0) {
			log.error("Error in creating soft link to " + fileName);
			String line = null;
			String error = null;
			if ((line = cmdError.readLine()) != null) {
				log.error(line);
				error = line;
				while ((line = cmdError.readLine()) != null) {
					log.error(line);
					error = error + "\n" + line;
				}				
				throw new IOException(error);
			}
		}		
		return true;		
	}		
}