/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Nov 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BuildUtils extends BuildHelper {
	String installDir = null;
	File instDir;
	Runtime rt = null;
	private static final Log log = LogFactory.getLog(BuildUtils.class);
	
	/**
	 * 
	 */
	public BuildUtils(String dir) {
		super();
		// TODO Auto-generated constructor stub
		installDir = new String(dir);
		installDir = installDir.replace('\\', File.separatorChar);
		installDir = installDir.replace('/', File.separatorChar);
		
		instDir = new File(installDir);		
		rt = Runtime.getRuntime();
	}
	
	public boolean configure(Properties confOptions, String envp[]) {
		if (!instDir.exists()) {
			log.error("The directory " + installDir + " does not exist");
			return false;
		}
		if (!instDir.isDirectory()) {
			log.error(installDir + " is not a directory");
			return false;
		}
		if (!instDir.canRead()) {
			log.error("The directory " + installDir + " does not have read permissions");
			return false;
		}
		if (!instDir.canWrite()) {
			log.error("The directory " + installDir + " does not have write permissions");
			return false;
		}
		
		String cmd = new String("/home/sandya/gt4.0.1-all-source-installer/configure");
		Enumeration e = confOptions.propertyNames();
		String options = null;
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String value = confOptions.getProperty(key);
			options = " " + key + "=" + value + " ";
			cmd = cmd + options;			
		}
		
		//log.info("Command : " + cmd);
		
		boolean success = true;
		BufferedReader cmdError = null;
		BufferedReader cmdOutput = null;
		try {
			Process p = rt.exec(cmd, envp, instDir);
			cmdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));		
			cmdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = cmdError.readLine()) != null) {
				log.info("CmdError : " + line);
			}
			while ((line = cmdOutput.readLine()) != null) {
				log.info("CmdOutput : " + line);
			}			
		}catch (IOException ioe) {
			success = false;
			log.error(ioe);
		}finally {
			try {
				cmdError.close();
				cmdOutput.close();
			}catch (IOException ioe) {
				success = false;
				log.error(ioe);
			}
			
		}		
		return success;
	}
	
	public boolean make() {
		String targets[] = null;
		return make(targets);
	}
	
	public boolean make(String target) {
		String targets[] = {target};
		return make(targets);
	}
	
	public boolean make(String targets[]) {
		String cmd = "make";
		
		if (targets != null) {
			for(int i=0; i<targets.length; i++) {
				cmd = cmd + " " + targets[i];
			}			
		}
		
		if (!instDir.exists()) {
			log.error("The directory " + installDir + " does not exist");
			return false;
		}
		if (!instDir.isDirectory()) {
			log.error(installDir + " is not a directory");
			return false;
		}
		if (!instDir.canRead()) {
			log.error("The directory " + installDir + " does not have read permissions");
			return false;
		}
		if (!instDir.canWrite()) {
			log.error("The directory " + installDir + " does not have write permissions");
			return false;
		}

		boolean success = true;
		BufferedReader cmdError = null;
		BufferedReader cmdOutput = null;
		String line = null;
		try {
			Process p = rt.exec(cmd, null, new File(installDir));			
			cmdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));		
			cmdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			
			StreamGobbler errorGobbler = 
				new StreamGobbler(p.getErrorStream(), "ERROR");
			
			StreamGobbler outputGobbler = 
				new StreamGobbler(p.getInputStream(), "OUTPUT");
			
			errorGobbler.start();
			outputGobbler.start();
			
			int exitVal = p.waitFor();
			/*while ((line = cmdError.readLine()) != null) {
				log.info("CmdError : " + line);
				success = false;
			}	
			
			while ((line = cmdOutput.readLine()) != null) {
				log.info("CmdOutput : " + line);
			}*/
		} catch (InterruptedException ie) {
			success = false;
			log.error(ie);
		} catch(IOException ioe) {
			success = false;
			log.error(ioe);			
		} finally {
			try {
				cmdError.close();
			} catch (IOException ie){
				success = false;
				log.error(ie);
			}
		}				
		return success;
	}
	
	/**
	 * @param args
	 */
	public static void main(String args[]) {
		BuildUtils bld = new BuildUtils("/home/sandya/gt4.0.1-all-source-installer");
		
		//System.setProperty("GLOBUS_LOCATION", "/home/sandya/globus401");							
		bld.addEnvVariable("ANT_HOME", "/opt/apache-ant-1.6.1");
		bld.add2Path(System.getProperty("ANT_HOME")+"/bin");
		/*String envp[] = {
				"GLOBUS_LOCATION=/home/sandya/GlobusTest",
				"JAVA_HOME="+bld.javaHome,
				"ANT_HOME="+System.getProperty("ANT_HOME"),
				"PATH="+bld.sysPath
		};*/
		
		Properties props = new Properties();		
		props.setProperty("--prefix", "/home/sandya/globus401");
		
		bld.make("distclean");
		bld.configure(props, null);	
		bld.make();
		bld.make("install");
	}
}

class StreamGobbler extends Thread {
	InputStream is;
	String type;
	private static Log log = LogFactory.getLog(StreamGobbler.class);
	
	StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}
	
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                log.info(line);			
		} catch(IOException ioe) {
			log.error(ioe);
		}
	}
}
