/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GridSecurity extends SecurityConstants {
	private static Log log = LogFactory.getLog(GridSecurity.class);
	private String globusLoc = null;
	private String caCertStr = null;
	private String hashKey = null;

	/**
	 * 
	 */
	public GridSecurity(String globusLoc) {
		super();
		// TODO Auto-generated constructor stub
		this.globusLoc = globusLoc;
	}
	
	public void setup(String caCertificate, String caInfo) throws GT4SecurityException {
		/* mkdir /etc/grid-security
		 * mkdir /etc/grid-security/certificates		 
		*/
		/*if (!FileUtils.createDir(gridSecurityDir)) {
			throw new GT4SecurityException("Cannot create directory " + 
					gridSecurityDir);
		}

		if (!FileUtils.createDir(certificateDir)) {
			throw new GT4SecurityException("Cannot create directory " + 
					certificateDir);
		}*/

		initialize(caCertificate, caInfo);
		installCACert();
		createSigningPolicy();
		SSLUtils sslUtils = new SSLUtils(hashKey);
		sslUtils.hostSSLConfig();
		sslUtils.userSSLConfig();		
	}
	
	
	/**
	 * Gets CA certificate from CA and derives the hash key from
	 * CA certificate
	 * @throws GT4SecurityException
	 */
	public void initialize(String caCertificate, String caInfo) throws GT4SecurityException {
		this.caCertStr = caCertificate;
		
		if (null == caCertStr) {
			log.error("Could not get CA Certificate from the CA");
			throw new GT4SecurityException(
						"Error in getting CA Certificate");
		}
		
		CAUtils utils = new CAUtils(caCertStr);
		hashKey = utils.getCAHash();
		
		if (null == caInfo) {
			log.error("Could not get CA Information");
			throw new GT4SecurityException("Could not get CA Information");
		}
		
		gridSecConf = gridSecConf.replaceAll("__HASH__", hashKey);
		if (!FileUtils.createFile(gridSecConf)) {
			log.error("Cannot create file " + gridSecConf);
			throw new GT4SecurityException("Cannot create file " + gridSecConf);
		}
		File gridSecFile = new File(gridSecConf);
		log.info("CA INFO :" + caInfo);
		log.info("CA INFO : " + caInfo);
		if (!FileUtils.writeString2File(caInfo, gridSecFile)) {
			log.error("Error in writing grid security conf file");
		}
		
		try {
			FileUtils.chgPermissions(gridSecConf, "644");
			/*int idx = gridSecConf.lastIndexOf(File.separatorChar);
			String destFile = gridSecConf.substring(idx);
			destFile = destFile.trim();
			idx = destFile.lastIndexOf(".");
			destFile = destFile.substring(0,idx);
			log.info("DEST FILE " + destFile);
			String link = gridSecurityDir + File.separatorChar + destFile;
			DiskUtils.forceDelete(link);
			if (!FileUtils.softLink(gridSecConf, gridSecurityDir, link)) {
				log.error("Error in creating soft link...");
				throw new GT4SecurityException("Error in creating soft link " +
						" to " + gridSecConf);
			}*/
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4SecurityException(ie);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);
		}
	}
	
	public void installCACert() throws GT4SecurityException {
		File caCertFile;
		FileInputStream fis;

		try {
			File tmp = File.createTempFile("caCert", ".0");			
			FileUtils.writeString2File(caCertStr, tmp);			
			fis = new FileInputStream(tmp);
			caCertFile = new File(certificateDir + File.separatorChar + 
					hashKey + ".0"); 
			DiskUtils.fCopy(fis, caCertFile);
			FileUtils.chgPermissions(caCertFile.getAbsolutePath(), "644");
			tmp.deleteOnExit();
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new GT4SecurityException(fnfe);			
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4SecurityException(ie);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);
		}
	}
	
	public void createSigningPolicy() throws GT4SecurityException {
		createSigningPolicy(null);
	}
	
	public void createSigningPolicy(String policy) 
			throws GT4SecurityException {
		SigningPolicy sp = new SigningPolicy(hashKey);
		CAUtils utils = new CAUtils(caCertStr);
		sp.getPolicyFile(utils.getCASubject(), policy);
	}
	
	public void reqHostCert() throws GT4SecurityException {
		reqHostCert(null, null);
	}
	
	public void reqHostCert(Properties props) 
			throws GT4SecurityException {
		reqHostCert(null, props);
	}
	
	public void reqHostCert(String hostName, Properties props) 
			throws GT4SecurityException {
		try {
			if (null == hostName) {
				log.info("Host name is not provided... ");
				log.info("Getting host name from the system...");
				InetAddress addr = InetAddress.getLocalHost();
				String hostname = addr.getHostName();
			}
		} catch (UnknownHostException ue) {
			log.info(ue);
			throw new GT4SecurityException("Error getting hostname...", ue);
		}
		
		if (null == props) {
			props = new Properties();		
		}
		props.setProperty("-host", hostName);
		
		CertUtils utils = new CertUtils(globusLoc);
		//utils.gridCertReq(props);		
		utils.gridCertReq(props, null);		
	}
	
/*	public void reqUserCert(Properties props) 
			throws GT4SecurityException {
		CertUtils utils = new CertUtils(globusLoc);
		utils.gridCertReq(props);
	}
*/
	public void reqUserCert(Properties props, String user) 
			throws GT4SecurityException {
		CertUtils utils = new CertUtils(globusLoc);
		utils.gridCertReq(props, user);
	}
	
	public void installSignedCert(String signedCertStr, File destDir, 
			String signedCertName) throws GT4SecurityException {
		if (!destDir.exists()) {
			log.error("The directory " + destDir.getAbsolutePath() + 
					" does not exist");
			throw new GT4SecurityException("The directory " + destDir.getAbsolutePath() + 
					" does not exist");
		}
		if (!destDir.isDirectory()) {
			log.error("The directory " + destDir.getAbsolutePath() +
					" is not a directory");
			throw new GT4SecurityException("The directory " + destDir.getAbsolutePath() +
					" is not a directory");
		}
		if (!destDir.canWrite()) {
			log.error("The directory " + destDir.getAbsolutePath() +
					" does not have write permission");
			throw new GT4SecurityException("The directory " + destDir.getAbsolutePath() +
					" does not have write permission");
		}
		
		if (!FileUtils.createFile(destDir.getAbsolutePath() + File.separatorChar + 
				signedCertName)) {
			log.error("Error in creating file " + destDir.getPath() + 
					File.separatorChar + signedCertName);
			throw new GT4SecurityException("Error in creating file " + 
					destDir.getPath() + File.separatorChar + signedCertName);
		}
		
		File signedCertFile = new File(
				destDir.getPath() + File.separatorChar + signedCertName);
		if (!FileUtils.writeString2File(signedCertStr, signedCertFile)) {
			log.error("Error in writing file " + 
					signedCertFile.getAbsolutePath());
			throw new GT4SecurityException(
					"Error in writing file " + signedCertFile.getAbsolutePath());
		}
	}
	
	public void setContainerCredentials(String userName) throws GT4SecurityException {
		File hCert = new File(hostCert);
		File hKey = new File(hostKey);
		File secDir = new File(gridSecurityDir);
		
		if (!secDir.exists()) {
			log.error("Grid security directory " + gridSecurityDir + 
					" does not exist");
			throw new GT4SecurityException("Grid security directory " + gridSecurityDir + 
					" does not exist"); 
		}
		if (!secDir.canWrite()) {
			log.error("Grid security directory " + gridSecurityDir +
					" does not have write permissions");
			throw new GT4SecurityException("Grid security directory " + gridSecurityDir +
					" does not have write permissions");
		}
		if (!hCert.exists()) {
			log.error("Cannot create container credentials...");
			log.error("Host Certificate does not exist");
			throw new GT4SecurityException("Host certificate does not exist..." + 
					"Looks like host cerificate is not requested");
		}
		long fileLength = 0L; 
		if ((fileLength=hCert.length()) == 0L) {
			log.error("Cannot find signed host certificate... Size of host " + 
					"certificate is 0 bytes");
			throw new GT4SecurityException("Cannot find signed host certificate... Size of host " +
					"certificate is 0 bytes");
		}
		if (!hKey.exists()) {
			log.error("Cannot create container credentials...");
			log.error("Host key does not exist");
			throw new GT4SecurityException("Host key does not exist..." + 
					"Looks like host cerificate is not requested");
		}
		
		File containerKeyFile = new File(containerKey);
		File containerCertFile = new File(containercert);		
		FileInputStream fisKey, fisCert;
		try {
			fisKey = new FileInputStream(hKey);
			fisCert = new FileInputStream(hCert);
			FileUtils.createFile(containerKeyFile);
			FileUtils.createFile(containerCertFile);
			
			DiskUtils.fCopy(fisKey, containerKeyFile);
			DiskUtils.fCopy(fisCert, containerCertFile);
			FileUtils.chgPermissions(containerKeyFile.getAbsolutePath(), "400");
			FileUtils.chgPermissions(containerCertFile.getAbsolutePath(), "644");	
			FileUtils.chgOwner(containerCertFile.getAbsolutePath(), userName);
			FileUtils.chgOwner(containerKeyFile.getAbsolutePath(), userName);
		} catch(FileNotFoundException fnfe) {
			log.error("Error while creating Container Credentials", fnfe);
			throw new GT4SecurityException("Error while creating Container Credentials", fnfe);
		} catch (IOException ioe) {
			log.error("Error while creating Container Credentials", ioe);
			throw new GT4SecurityException("Error while creating Container Credentials", 
					ioe);
		} catch (InterruptedException ie) {
			log.error("Error while creating Container credentials", ie);
			throw new GT4SecurityException("Error while creating Container Credentials", 
					ie);
		}
	}
	
	public String getUserSubject(String certFilePath) throws GT4SecurityException {
		String subject = null;
		
		Runtime rt = Runtime.getRuntime();
		String cmd = globusLoc + File.separatorChar + "bin" + 
				File.separatorChar + "grid-cert-info";
		//cmd = cmd + " -subject -file " + userCert;
		cmd = cmd + " -subject -file " + certFilePath;
		
		//File uCert = new File(userCert);
		File uCert = new File(certFilePath);
		if (!uCert.exists()) {
			log.error("Cannot get user subject... " + certFilePath +
					" does not exist");
			throw new GT4SecurityException("Cannot get user subject... " + certFilePath +
			" does not exist");
		}
		if (!uCert.canRead()) {
			log.error("Cannot get user subject... " + certFilePath +
					" does not have read permissions");
			throw new GT4SecurityException("Cannot get user subject... " + certFilePath +
			" does not have read permissions");
		}
		
		Process p;
		BufferedReader cmdError = null;
		BufferedReader cmdOutput = null;
		int exitVal = 0;
		try {
			p = rt.exec(cmd, new String[] {"GLOBUS_LOCATION="+globusLoc});
			cmdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			cmdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			exitVal = p.waitFor();
			
			if (exitVal != 0) {
                log.error("Error in getting user subject");
                String err = "Error in getting user subject";
                String line = null;
                while ((line=cmdError.readLine()) != null) {
                    err = err + line;
                }
                throw new GT4SecurityException(err);
			}
			
			subject = cmdOutput.readLine();
		} catch (IOException ioe) {
			log.error("Error in getting user subject", ioe);
			throw new GT4SecurityException("Error in getting user subject", ioe);
		} catch (InterruptedException ie) {
			log.error("Error in getting user subject", ie);
			throw new GT4SecurityException("Error in getting user subject", ie);
		}
		
		if (null == subject) {
			log.error("Error in getting user subject.... " +
					" User subject is null");
			throw new GT4SecurityException("Error in getting user subject.... " +
					" User subject is null");
		}
		
		return subject;
		
	}
	
	public void updateGridMapfile(String subject, String user) throws
				GT4SecurityException {
		if ((null == subject) || (null == user)) {
			log.error("subject or user cannot be null. Pls provide appropriate" +
					" subject and user");
			throw new GT4SecurityException("subject or user cannot be null. " +
					"Pls provide appropriate subject and user");
		}

		File mapFile = new File(gridMapFile);
		
		try {
			if (!mapFile.exists()) {
				FileUtils.createFile(mapFile);
			}
			if (!mapFile.canWrite()) {
				log.error("Cannot update grid-mapfile. No write permissions");
				throw new GT4SecurityException("Cannot update grid-mapfile." +
						" No write permissions");
			}
			
			String line = "\"" + subject + "\"" + " " + user;
			FileUtils.appendString2File(mapFile, line);
		} catch (IOException ioe) {
			log.error("Error in updating grid-mapfile", ioe);
			throw new GT4SecurityException("Error in updating grid-mapfile", ioe);
		}				
	}
	
	public String getHostname() throws GT4SecurityException {
		String hostname = null;
		try {
		InetAddress addr = InetAddress.getLocalHost();       
        hostname = addr.getHostName();        
		} catch (UnknownHostException uhe) {
			log.error("Error in getting hostname", uhe);
			throw new GT4SecurityException("Error in getting hostname", uhe);
		}
		return hostname;
	}
	
	public void configureSecurity(String caCertificate, String caInfo) 
				throws GT4SecurityException {
		setup(caCertificate, caInfo);
		Properties hostProps = new Properties();
		//Properties usrProps = new Properties();
		
		String hostname = getHostname();
		if (null == hostname) {
			log.error("Hostname cannot be obtained...");
			throw new GT4SecurityException("Hostname cannot be obtained...");
		}
		
		// Request Host Certificate
		/*hostProps.setProperty("-host", hostname);		
		reqHostCert(hostname, hostProps);*/		
		
		/*// Request User Certificate
		usrProps.setProperty("-passphrase", userPassphrase);		
		reqUserCert(usrProps);
		*/		
	}
	
	public static void main(String args[]) {
		//GridSecurity sec = new GridSecurity("/home/sandya/globus401");
		//String policy = null;
		//try {
			//sec.setup();
			/*Properties props = new Properties();
			props.setProperty("-force", "");
			sec.reqHostCert("eb96133.india.hp.com", props);
			File certReq = new File("/etc/grid-security/hostcert_request.pem");
			File destDir = new File("/etc/grid-security");
			sec.getSignedCert(certReq, destDir, "hostcert.pem"); */
			//sec.setContainerCredentials();
			//String subject = sec.getUserSubject();
			//sec.updateGridMapfile(subject, System.getProperty("user.name"));
			
			/*Properties p = new Properties();
			p.setProperty("-passphrase", "iso*help");
			sec.reqUserCert(p);
			File userReq = new File("/home/sandya/.globus/usercert_request.pem");
			File userDir = new File("/home/sandya/.globus");
			*/
			//sec.getSignedCert(userReq, userDir, "usercert.pem");
			
			Vector v = new Vector();
			v.add(0,"test<GLOBUS_LOCATION>");
			v.add(1,"for");
			v.add(2,"vector");
			Enumeration e = v.elements();
			while (e.hasMoreElements()) {
				String element = (String)e.nextElement();
				String el = element.replaceAll("<GLOB_LOCATION>","/home/globus");
				
				System.out.println("Vector Element : " + el);
			}
			
			
		//} catch (GT4SecurityException gse) {
			//log.error(gse);
		//}
	}
}
