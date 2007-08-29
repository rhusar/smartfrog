/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.sf.apps.gt4.prereqs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CheckCommand {
	Runtime rt = null;
	BufferedReader cmdError = null;
	BufferedReader cmdOutput = null;

	private static Log log = LogFactory.getLog(CheckCommand.class);

	public CheckCommand() {
		rt = Runtime.getRuntime();
	}

	public int cmdInPath(String cmd, String options) 
			throws IOException, InterruptedException {
		String chkCmd = cmd + " " + options;
		boolean success = true;
		
		int exitVal = 0;
		try {
			Process p = rt.exec(chkCmd);
			cmdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			cmdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			/*String line = null;
			while ((line=cmdError.readLine()) != null) {
				log.error("ERROR: " + line);				
			}
			while ((line=cmdOutput.readLine()) != null) {
				log.info("OUTPUT: " + line);				
			}*/
			
			exitVal = p.waitFor();
		//	System.out.println(cmd + " found in path");
		} catch (IOException ioe) {
			throw new IOException("Error occured while executing " + 
					chkCmd);
		}
		
		//System.out.println("Exit value for " + cmd + " is :" + exitVal);
		return exitVal;
	}
	
	public int cmdInDir(String directory, String cmd, String options) 
			throws IOException, InterruptedException {
		boolean success = true;
		
		char separator = File.separatorChar;
		directory = directory.replace('\\', File.separatorChar);
		directory = directory.replace('/', File.separatorChar);
		
		File dir = new File(directory);
		if (!dir.exists()) {
			log.error("The directory " + directory + " does not exist");
			throw new IOException("The directory " + directory + 
					" does not exist");
			//return false;
		}
		if (!dir.isDirectory()) {
			log.error(directory + " is not a directory");
			throw new IOException(directory + " is not a directory");
			//return false;
		}
		if (!dir.canRead()) {
			log.error("Directory " + directory + " is not readable");
			throw new IOException("Directory " + directory + 
					" is not readable");
			//return false;
		}
		
		cmd = directory + separator + "bin" + separator + cmd;
		return cmdInPath(cmd, options);		
	}

	public static void main(String args[]) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String chkCmd = "pg_ctl --version";
		String line;
		Process p = rt.exec(chkCmd);
		BufferedReader cmdOutput = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		System.out.println("test");
		while ((line = cmdOutput.readLine()) != null) {
			System.out.println(line);
		}

		//log.info(line);
		cmdOutput.close();
	}
}
