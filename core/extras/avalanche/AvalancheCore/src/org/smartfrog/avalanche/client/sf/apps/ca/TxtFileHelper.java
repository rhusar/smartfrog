/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Dec 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.ca;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TxtFileHelper {
	private File txtFile = null;
	private static int cnt = 0;
	
	private static final Log log = LogFactory.getLog(TxtFileHelper.class);

	/**
	 * 
	 */
	public TxtFileHelper(String textFile) {
		super();
		// TODO Auto-generated constructor stub
		txtFile = new File(textFile);				
	}
	
	public boolean checkFile(File file) {
		String fileName = file.getAbsolutePath();
		if (!file.exists()) {
			log.error("File " + fileName + " does not exist");
			return false;
		}
		if (!file.isFile()) {
			log.error("File " + fileName + " is not a file");
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
	
	public boolean changeValueAfter(String after, String startStr, String newStr, int count) 
				throws FileNotFoundException, IOException {
		boolean found = false;
		if (!checkFile(txtFile)) {
			return false;
		}
		
		String fileName = txtFile.getName();
		File tmpFile = File.createTempFile(fileName, ".tmp");
		tmpFile.deleteOnExit();		
		
		if(!checkFile(tmpFile)) {
			return false;
		}
		
		FileInputStream fis = new FileInputStream(txtFile);
		FileOutputStream fos = new FileOutputStream(tmpFile);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(fis));
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(fos));
		
		String line;
		while ((line=inStream.readLine()) != null) {						
			int idx = 0;
			
			if ((line.startsWith(startStr)) && !found) {
				cnt++;
				//log.info("Count : " + cnt);
				//log.info("LINE : " + line);
				if (cnt == count) {
					idx = line.indexOf(after);
					String replaceStr  = line.substring(idx+1);
					line = line.replaceFirst(replaceStr, newStr);
					found = true;				
				}									
			}
			outStream.write(line);
			outStream.newLine();
		}
		inStream.close();
		outStream.close();
		fis.close();
		fos.close();
		
		File srcFile = new File(tmpFile.getAbsolutePath());
		File destFile = new File(txtFile.getAbsolutePath());		
		copyFile(srcFile, destFile);		
		
		fis = new FileInputStream(txtFile);
		inStream = new BufferedReader(new InputStreamReader(fis));
		
		/*while ((line=inStream.readLine()) != null) {						
			log.info(line);
		}*/
		
		inStream.close();
		fis.close();
		
		return true;		
	}
	
	public boolean changeValueAfter(String after, String startStr, String newStr)
			throws FileNotFoundException, IOException {
		boolean found = false;
		if (!checkFile(txtFile)) {
			return false;
		}

		String fileName = txtFile.getName();
		File tmpFile = File.createTempFile(fileName, ".tmp");
		tmpFile.deleteOnExit();

		if (!checkFile(tmpFile)) {
			return false;
		}

		FileInputStream fis = new FileInputStream(txtFile);
		FileOutputStream fos = new FileOutputStream(tmpFile);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(fis));
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(
				fos));

		String line;
		while ((line = inStream.readLine()) != null) {
			int idx = 0;
			if ((line.startsWith(startStr)) && !found) {
				idx = line.indexOf(after);
				String replaceStr = line.substring(idx + 1);
				line = line.replaceFirst(replaceStr, newStr);
				found = true;
			}
			outStream.write(line);
			outStream.newLine();
		}
		inStream.close();
		outStream.close();
		fis.close();
		fos.close();

		File srcFile = new File(tmpFile.getAbsolutePath());
		File destFile = new File(txtFile.getAbsolutePath());
		copyFile(srcFile, destFile);

		fis = new FileInputStream(txtFile);
		inStream = new BufferedReader(new InputStreamReader(fis));

		/*while ((line=inStream.readLine()) != null) {						
		 log.info(line);
		 }*/
		
		inStream.close();
		fis.close();

		return true;
	}
	
	public boolean insertLineAfter(String refLine, String newLine) 
			throws IOException {
		if (!checkFile(txtFile)) {
			return false;
		}

		String fileName = txtFile.getName();
		File tmpFile = File.createTempFile(fileName, ".tmp");
		tmpFile.deleteOnExit();

		if (!checkFile(tmpFile)) {
			return false;
		}

		FileInputStream fis = new FileInputStream(txtFile);
		FileOutputStream fos = new FileOutputStream(tmpFile);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(fis));
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(
				fos));

		String line;
		while ((line = inStream.readLine()) != null) {
			int idx = 0;
			if (line.equals(refLine)) {
				line = line + "\n" + newLine;				
			}
			outStream.write(line);
			outStream.newLine();
		}
		inStream.close();
		outStream.close();
		fis.close();
		fos.close();
		
		File srcFile = new File(tmpFile.getAbsolutePath());
		File destFile = new File(txtFile.getAbsolutePath());
		copyFile(srcFile, destFile);

		fis = new FileInputStream(txtFile);
		inStream = new BufferedReader(new InputStreamReader(fis));
		
		while ((line=inStream.readLine()) != null) {						
		 //log.info(line);
		}
		
		inStream.close();
		fis.close();
		
		return true;
	}
	
	public String getValue(String key, String separator, String comment) 
			throws FileNotFoundException, IOException {
		String value = null;
		
		if (!checkFile(txtFile)) {
			return null;
		}
		
		FileInputStream fis = new FileInputStream(txtFile);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		boolean found = false;
		while ((line = inStream.readLine()) != null) {			
			if ((line.startsWith(key)) && !found) {
				int beginIdx = line.indexOf(separator);
				int endIdx = line.indexOf(comment);
				if (endIdx == -1) {
					value = line.substring(beginIdx+1);					
				}
				else {
					value = line.substring(beginIdx+1, endIdx);
				}
				value = value.trim();				
				found = true;
			}
		}
		fis.close();
		inStream.close();
		
		return value;
	}
	
	public boolean removeComment(String comment, String startStr) 
			throws FileNotFoundException, IOException {
		if (!checkFile(txtFile)) {
			return false;
		}
		
		String fileName = txtFile.getName();
		File tmpFile = File.createTempFile(fileName, ".tmp");
		if(!checkFile(tmpFile)) {
			return false;
		}
		
		FileInputStream fis = new FileInputStream(txtFile);
		FileOutputStream fos = new FileOutputStream(tmpFile);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(fis));
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(fos));
		
		String line;
		String cmpStr = comment + startStr;
		cmpStr = cmpStr.replaceAll("\\s", "");
		while ((line=inStream.readLine()) != null) {
			if (line.startsWith(comment)) {
				String temp = line.replaceAll("\\s", "");
				if (temp.startsWith(cmpStr)) {
					String replaceStr  = line.substring(1);
					line = replaceStr;					
				}						
			}
			outStream.write(line);
			outStream.newLine();
		}
		inStream.close();
		outStream.close();
		fis.close();
		fos.close();
		
		File srcFile = new File(tmpFile.getAbsolutePath());
		File destFile = new File(txtFile.getAbsolutePath());		
		copyFile(srcFile, destFile);
		
		
		fis = new FileInputStream(txtFile);
		inStream = new BufferedReader(new InputStreamReader(fis));
		
		while ((line=inStream.readLine()) != null) {						
			//log.info(line);
		}
		
		return true;		
	} 
	
	public boolean deleteLine(String startStr)
			throws FileNotFoundException, IOException {
		if (!checkFile(txtFile)) {
			return false;
		}
		
		String fileName = txtFile.getName();
		File tmpFile = File.createTempFile(fileName, ".tmp");
		if(!checkFile(tmpFile)) {
			return false;
		}
		
		FileInputStream fis = new FileInputStream(txtFile);
		FileOutputStream fos = new FileOutputStream(tmpFile);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(fis));
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(fos));
		
		String line;
		String cmpStr = new String(startStr);
		cmpStr = cmpStr.replaceAll("\\s", "");
		while ((line=inStream.readLine()) != null) {
			if (line.startsWith(cmpStr)) {
				continue;
			}
			outStream.write(line);
			outStream.newLine();
		}
		inStream.close();
		outStream.close();
		fis.close();
		fos.close();
		
		File srcFile = new File(tmpFile.getAbsolutePath());
		File destFile = new File(txtFile.getAbsolutePath());		
		copyFile(srcFile, destFile);
		
		
		fis = new FileInputStream(txtFile);
		inStream = new BufferedReader(new InputStreamReader(fis));
		
		while ((line=inStream.readLine()) != null) {						
			//log.info(line);
		}
		
		return true;
		
	}
	
	public void copyFile(File src, File dest) 
				throws FileNotFoundException, IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);
		
		byte buf[] = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);			
		}
		in.close();
		out.close();
	}
	
	public boolean replaceString(String oldStr, String newStr) 
			throws IOException {
		if (!checkFile(txtFile)) {
			return false;
		}
		
		String fileName = txtFile.getName();
		File tmpFile = File.createTempFile(fileName, ".tmp");
		tmpFile.deleteOnExit();		
		
		if(!checkFile(tmpFile)) {
			return false;
		}
		
		FileInputStream fis = new FileInputStream(txtFile);
		FileOutputStream fos = new FileOutputStream(tmpFile);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(fis));
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(fos));
		
		String line;
		while ((line=inStream.readLine()) != null) {
			if ((line.indexOf(oldStr) != -1)) {
				//log.info("LINE: " + line);
				line = line.replaceAll(oldStr, newStr);
				//log.info("CHG LINE: " + line);
			}
			outStream.write(line);
			outStream.newLine();
		}
		inStream.close();
		outStream.close();
		fis.close();
		fos.close();
		
		File srcFile = new File(tmpFile.getAbsolutePath());
		File destFile = new File(txtFile.getAbsolutePath());		
		copyFile(srcFile, destFile);		
		
		fis = new FileInputStream(txtFile);
		inStream = new BufferedReader(new InputStreamReader(fis));
		
		while ((line=inStream.readLine()) != null) {						
			//log.info(line);
		}
		return true;
	}
	
	public static void main(String args[]) {
		TxtFileHelper txt = new TxtFileHelper("/home/sandya/globus-ssl.conf");
		
		try {
			//txt.changeValueAfter("=", "dir", "/home/sandya/certs");
			//txt.removeComment("#", "unique");
			txt.insertLineAfter("0.organizationName_default\t= Not Configured", "Inserted Line");
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
		} 
		
	}
}
