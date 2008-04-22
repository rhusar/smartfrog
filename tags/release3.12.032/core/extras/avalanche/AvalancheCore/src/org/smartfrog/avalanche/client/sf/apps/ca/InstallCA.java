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
package org.smartfrog.avalanche.client.sf.apps.ca;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.gnubuild.BuildUtils;
import org.smartfrog.avalanche.client.sf.apps.gnubuild.GNUBuildException;
import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.CheckPrereqs;
import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.PrereqException;
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
public class InstallCA extends CAConstants {
	private String installerDir = null;
	private static final Log log = LogFactory.getLog(InstallCA.class);
			
	/**
	 * 
	 */
	public InstallCA(String installerDir, String installationDir, String caDirectory) {
		super(caDirectory, installationDir);
		// TODO Auto-generated constructor stub
		this.installerDir = new String(installerDir);						
	}
	
	public void checkPreReqs() throws CAException {
		CheckPrereqs preReq = new CheckPrereqs();
		
		try {
			// C Compiler
			preReq.checkCmd("cc", "--version");
						
			// MAKE
			preReq.checkCmd("make", null, "gnu");
			
			// Perl
			preReq.checkCmd("perl");							
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new CAException(ie);
		} catch (PrereqException pe) {
			log.error(pe);
			throw new CAException(pe);
		}		
	}
	
	
	/**
	 * Builds Openssl from Globus Installer packages.
	 * Installs Openssl in the directory installDir.
	 * Usually, installDir is GLOBUS_LOCATION
	 * @param installDir
	 * @return
	 */
	public void buildFrmGlobus() throws CAException {
		BuildUtils buildUtils = new BuildUtils(installerDir);
		
		if (!FileUtils.createDir(opensslDir)) {
			log.error("Cannot create directory " + opensslDir);
			throw new CAException("Cannot create directory " + opensslDir + 
					"... Please check log file for more information");
		}
		
		Properties props = new Properties();
		props.setProperty("--prefix", opensslDir);
		try {
			log.info("Starting configure in InstallCA.buildFrmGlobus");
			buildUtils.configure(props, null);
			buildUtils.make("gsi-openssh");
			buildUtils.make("install");
		} catch (GNUBuildException gbe) {
			log.error("Error while installing CA", gbe);
			throw new CAException("Error while installing CA", gbe);
		}
		
		log.info("Successfully built openssl from Globus installer");
	}
	
	public void buildFrmOpenssl() throws CAException {
		BuildUtils buildUtils = new BuildUtils(installerDir);
		
		if (!FileUtils.createDir(opensslDir)) {
			log.error("Cannot create directory " + opensslDir);
			throw new CAException("Cannot create directory " + opensslDir + 
			"... Please check log file for more information");
		}
		
		Properties props = new Properties();
		props.setProperty("--prefix", opensslDir);
		try {
			buildUtils.configure(props, null);
			buildUtils.make();
			buildUtils.make("install");
		} catch (GNUBuildException gbe) {
			log.error("Error while installing CA");
			throw new CAException("Error while installing CA", gbe);
		}
		
		log.info("Successfully build openssl");
	}
	
	private boolean createDirStruct() 
			throws FileNotFoundException, IOException {
		boolean success = true;
		char separator = File.separatorChar;
		
		/* mkdir $dir
		 * mkdir $dir/certs
		 * mkdir $dir/crl
		 * mkdir $dir/newcerts
		 * mkdir $dir/private
		 * echo "01" > $dir/serial
		 * touch $dir/index.txt
		 */
				
		TxtFileHelper txt = new TxtFileHelper(confFile);
		String dirValue = null;
		if ((dirValue=txt.getValue(CAConstants.dir, CAConstants.separator, 
				CAConstants.comment)) == null) {
			log.error("The value for 'dir' is not found in config file.");
			log.error("CA cannot be installed.");
			return false;
		}
		
		dirValue = dirValue.replace('\\', File.separatorChar);
		dirValue = dirValue.replace('/', File.separatorChar);
		if (!FileUtils.createDir(dirValue))
			return false;			
				
		String d = dirValue + separator + "crl";
		if (!FileUtils.createDir(d))
			return false;
		
		if (!FileUtils.createDir(dirValue + separator + "newcerts"))
			return false;
		
		if (!FileUtils.createDir(dirValue + separator + "private"))
			return false;
	
		if (FileUtils.createFile(dirValue + separator + "serial")) {
			File file = new File(dirValue + separator + "serial");
			FileUtils.writeString2File("01", file);		
		}
		else {
			return false;
		}
			
		if (!FileUtils.createFile(dirValue + separator + "index.txt"))
			return false;
		
		return true;		
	}
	
	public void configureCA(Properties props) 
		throws CAException {
		
		File confLoc = new File(confFile);
		InputStream fis = null;
		
		try {
			log.info("Template file : " + confFileTmpl);
			fis = getClass().getResourceAsStream(confFileTmpl);
			DiskUtils.fCopy(fis, confLoc);
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);			
		}
		
		Enumeration confKeys = props.keys();
		TxtFileHelper txtEdit = new TxtFileHelper(confFile);
		
		try {
			while (confKeys.hasMoreElements()) {
				String key = (String)confKeys.nextElement();
				String value = props.getProperty(key);
				if (key.equals("emailAddress"))
					txtEdit.changeValueAfter("=", key, value, 3);
				else
					txtEdit.changeValueAfter("=", key, value);
			}			
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		}
		
		//Create the necessary dir structure for CA
		try {
			createDirStruct();
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		}
		
		gridSecurityConf(props);
	}
	
	public void gridSecurityConf(Properties props) throws CAException {
		try {
			InputStream fis = getClass().getResourceAsStream(gridConfTmpl);
			File gridSecConf = new File(gridConfFile);
			DiskUtils.fCopy(fis, gridSecConf);
			
			TxtFileHelper txt = new TxtFileHelper(gridConfFile);
			
			String hostBase = hostBase = "\"" + "ou=" + props.getProperty("OU") + ", o=" + 
								props.getProperty("O") + "\"";
			String userBase = new String(hostBase);
			String caName = "\"" + props.getProperty("CN") + "\"";
			String emailAddr = "\"" + props.getProperty("emailAddress") + "\"";
			txt.changeValueAfter("=", "SETUP_GSI_HOST_BASE_DN", hostBase);
			txt.changeValueAfter("=", "SETUP_GSI_USER_BASE_DN", userBase);
			txt.changeValueAfter("=", "SETUP_GSI_CA_NAME", caName);
			txt.changeValueAfter("=", "SETUP_GSI_CA_EMAIL", emailAddr);
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		}
	}
	
	public void generateCaCert(String passout, String validity) 
					throws CAException {
		Runtime rt = Runtime.getRuntime();
		
		TxtFileHelper txt = new TxtFileHelper(confFile);
		String dirValue = null;
		try {
			if ((dirValue=txt.getValue(CAConstants.dir, CAConstants.separator, 
					CAConstants.comment)) == null) {
				log.error("The value for 'dir' is not found in config file.");
				log.error("CA cannot be installed.");
				throw new CAException("The value for 'dir' is not found in config file.");
			}
		} catch (FileNotFoundException fnfe) {
			log.error("Error : ", fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error("Error :", ioe);
			throw new CAException(ioe);
		}
		
		File configFile = new File(confFile);
		char sepChar = File.separatorChar;
		dirValue = dirValue.replace('\\', sepChar);
		dirValue = dirValue.replace('/', sepChar);
		File certDir = new File(dirValue);
		if (!FileUtils.checkDir(certDir)) {
			throw new CAException("Error in accessing " + dirValue + "... " +
					"Please check log file for more information");
		}
		
		String cmd = new String(opensslDir + sepChar + 
				"bin" + sepChar + "openssl");
		String privateKey = dirValue + sepChar + "private" + 
				sepChar + "cakey.pem";
		String publicKey = dirValue + sepChar + CAConstants.caCertFile;
		
		cmd = cmd + " req -new -x509 -keyout " + privateKey + " -out " + publicKey +
				" -days " + validity + " -passout " + "pass:" + passout + 
				" -config " + confFile;
		log.info("Cmd : " + cmd);
		
		BufferedReader cmdError = null;
		int exitVal = 0;
		try {
			Process p = rt.exec(cmd, null, certDir);
			cmdError = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			exitVal = p.waitFor();
			if (exitVal != 0) {
				log.error("Error in generating CA certificates...");
				String line = null;
				String error = null;
				if ((line = cmdError.readLine()) != null) {
					log.error(line);
					error = line;
					while ((line = cmdError.readLine()) != null) {
						log.error(line);
						error = error + "\n" + line;
					}				
					throw new CAException(error);
				}
			}
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new CAException(ie);
		}
		
	}
	
	/*public boolean generateHashkey(String cmd, String dir) {
		Runtime rt = Runtime.getRuntime();
		
		char sepChar = File.separatorChar;
		dir = dir.replace('\\', sepChar);
		dir = dir.replace('/', sepChar);		
		File certDir = new File(dir);
		if (!FileUtils.checkDir(certDir)) {
			return false;
		}
		
		BufferedReader cmdOutput = null;
		BufferedReader cmdError = null;
		String line;
		try {
			cmd = cmd + " x509 -in " + caCertFile + " -hash -noout";
			Process p = rt.exec(cmd, null, certDir);
			cmdOutput = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			cmdError = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			
			if (cmdError != null) {
				while ((line = cmdError.readLine()) != null) {
					log.error(line);
				}
				return false;
			}			
			if (cmdOutput == null) {
				log.error("Hash key is not generated");
            	return false;				
			}
			
			if ((line = cmdOutput.readLine()) == null) {
				log.error("Error in reading hashkkey");
				return false;
			}
			String hashFile = dir + sepChar + caHashFile;
			if (!FileUtils.createFile(hashFile)) {
				return false;
			}
			
			File hFile = new File(hashFile);
			if (!FileUtils.writeString2File(line, hFile)) {
				return false;
			}				
		} catch (IOException ioe) {
			log.error(ioe);
			return false;
		}
        	
		return true;		
	} */
	
	public static void main(String args[]) throws  CAException {
		InstallCA ca = new InstallCA("/home/sandya/gt4.0.1-all-source-installer","", "");
		
		ca.checkPreReqs();
		//ca.buildFrmGlobus();
		//ca.buildFrmOpenssl()
	
		
		/*Properties props = new Properties();
		
		props.setProperty("unique_subject", "yes");
		props.setProperty("C", "IN");
		props.setProperty("ST", "KA");
		props.setProperty("L", "BLR");
		props.setProperty("O", "HP");
		props.setProperty("OU", "MISL");
		props.setProperty("CN", "HP_CA");
		props.setProperty("emailAddress", "uppada@india.hp.com");
				
		ca.configureCA(props);
		ca.generateCaCert("sandya", "365");
		ca.gridSecurityConf( props);*/
	}
}