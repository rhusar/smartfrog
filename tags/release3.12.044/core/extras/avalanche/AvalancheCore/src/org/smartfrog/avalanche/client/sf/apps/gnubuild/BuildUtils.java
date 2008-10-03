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
package org.smartfrog.avalanche.client.sf.apps.gnubuild;

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
	private String installerDir = null;
	private File instDir;
	private Runtime rt = null;
	private static final Log log = LogFactory.getLog(BuildUtils.class);
	
	/**
	 * 
	 */
	public BuildUtils(String installerDir) {
		super();
		// TODO Auto-generated constructor stub
		this.installerDir = new String(installerDir);
		this.installerDir = installerDir.replace('\\', File.separatorChar);
		this.installerDir = installerDir.replace('/', File.separatorChar);
		
		instDir = new File(installerDir);		
		rt = Runtime.getRuntime();
	}
	
	public void configure(Properties confOptions, 
			String envp[]) throws GNUBuildException {
		if (!instDir.exists()) {
			log.error("The directory " + installerDir + " does not exist");
			throw new GNUBuildException("The directory " + installerDir + 
					" does not exist");
		}
		if (!instDir.isDirectory()) {
			log.error(installerDir + " is not a directory");
			throw new GNUBuildException(installerDir + 
					" is not a directory");
		}
		if (!instDir.canRead()) {
			log.error("The directory " + installerDir + 
					" does not have read permissions");
			throw new GNUBuildException("The directory " + installerDir + 
					" does not have read permissions");
		}
		if (!instDir.canWrite()) {
			log.error("The directory " + installerDir + 
					" does not have write permissions");
			throw new GNUBuildException("The directory " + installerDir + 
					" does not have write permissions");
		}
		
		String cmd = new String(installerDir + File.separatorChar 
				+ "configure");
		File f = new File(cmd);
		if (!f.exists()) {
			// This is to make sure if configure is not present then config is used
			cmd = installerDir + File.separatorChar + "config";
		}
		Enumeration e = confOptions.propertyNames();
		String options = null;
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String value = confOptions.getProperty(key);
			if (value.length() != 0) 
				options = " " + key + "=" + value + " ";
			else
				options = " " + key + " ";
		
			cmd = cmd + options;			
		}
		
		log.info("Command : " + cmd);
		
		boolean success = true;
		BufferedReader cmdError = null;
		BufferedReader cmdOutput = null;
		try {
			Process p = rt.exec(cmd, envp, instDir);
			
			/*String line = null;
			while ((line = cmdError.readLine()) != null) {
				log.info("CmdError : " + line);
			}
			while ((line = cmdOutput.readLine()) != null) {
				log.info("CmdOutput : " + line);
			}*/
			cmdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));		
			cmdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			StreamGobbler errorGobbler = 
				new StreamGobbler(p.getErrorStream(), "ERROR");
			
			StreamGobbler outputGobbler = 
				new StreamGobbler(p.getInputStream(), "OUTPUT");
			
			errorGobbler.start();
			outputGobbler.start();
			
			int exitVal = 0;
			exitVal = p.waitFor();
			//log.info("Exit Value : " + exitVal);
			if (exitVal != 0) {
				log.error("Error in running configure");
				throw new GNUBuildException("Error in running configure");
			}
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GNUBuildException(ie);
		} catch (IOException ioe) {
			success = false;
			log.error(ioe);
			throw new GNUBuildException(ioe);
		}finally {
			try {
				cmdError.close();
				cmdOutput.close();
			}catch (IOException ioe) {
				success = false;
				log.error(ioe);
				throw new GNUBuildException(ioe);
			}			
		}		
	}
	
	public void make() throws GNUBuildException {
		String targets[] = null;
		make(targets, null);
	}
	
	public void make(String target) throws GNUBuildException {
		String targets[] = {target};
		make(targets, null);
	}
	
	public void make(String envp[]) throws GNUBuildException {
		String targets[] = null;
		make(targets, envp);
	}
	
	public void make(String targets[], String envp[]) throws GNUBuildException {
		String cmd = "make";
		
		if (targets != null) {
			for(int i=0; i<targets.length; i++) {
				cmd = cmd + " " + targets[i];
			}			
		}
		
		log.info("cmd : " + cmd);
		
		if (!instDir.exists()) {
			log.error("The directory " + installerDir + " does not exist");
			throw new GNUBuildException("The directory " + installerDir + 
					" does not exist");
		}
		if (!instDir.isDirectory()) {
			log.error(installerDir + " is not a directory");
			throw new GNUBuildException(installerDir + " is not a directory");
		}
		if (!instDir.canRead()) {
			log.error("The directory " + installerDir + 
					" does not have read permissions");
			throw new GNUBuildException("The directory " + installerDir + 
					" does not have read permissions");
		}
		if (!instDir.canWrite()) {
			log.error("The directory " + installerDir + 
					" does not have write permissions");
			throw new GNUBuildException("The directory " + installerDir + 
					" does not have write permissions");
		}

		boolean success = true;
		BufferedReader cmdError = null;
		BufferedReader cmdOutput = null;
		String line = null;
		try {
			Process p = rt.exec(cmd, envp, new File(installerDir));			
			cmdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));		
			cmdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			
			StreamGobbler errorGobbler = 
				new StreamGobbler(p.getErrorStream(), "ERROR");
			
			StreamGobbler outputGobbler = 
				new StreamGobbler(p.getInputStream(), "OUTPUT");
			
			errorGobbler.start();
			outputGobbler.start();
			
			int exitVal = 0;
			exitVal = p.waitFor();
			log.info("Exit Value : " + exitVal);
			if (exitVal != 0) {
				log.error("Error in installation");
				throw new GNUBuildException("Error in installation");
			}
		} catch (InterruptedException ie) {
			success = false;
			log.error(ie);
			throw new GNUBuildException(ie);
		} catch(IOException ioe) {
			success = false;
			log.error(ioe);
			throw new GNUBuildException(ioe);
		} finally {
			try {
				cmdError.close();
			} catch (IOException ioex){
				success = false;
				log.error(ioex);
				throw new GNUBuildException(ioex);
			}
		}		
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
		props.setProperty("--prefix", "/home/sandya/GTTest");
		
			
		try {
			//bld.make("distclean");
			//bld.configure(props, null);
			bld.make();
		} catch (GNUBuildException gbe) {
			log.error(gbe);
		}
		//bld.make("install"); 
	}

static class StreamGobbler extends Thread {
	private InputStream is;
	private String type;
	private static final Log log = LogFactory.getLog(StreamGobbler.class);
	
	StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}
	
	public void run() {
		String line=null;
		try {
			InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            if (type.equalsIgnoreCase("ERROR")) {
            	while ( (line = br.readLine()) != null)
            		log.error(line);			
            }
            if (type.equalsIgnoreCase("OUTPUT")) {
            	while ( (line = br.readLine()) != null)
            		log.info(line);
            }
		} catch(IOException ioe) {
			log.error(line + ioe);
		}
	}
}

}
