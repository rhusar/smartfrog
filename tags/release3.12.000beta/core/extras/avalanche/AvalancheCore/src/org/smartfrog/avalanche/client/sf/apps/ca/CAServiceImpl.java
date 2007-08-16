/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Dec 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.ca;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CAServiceImpl extends CAConstants {
	private static Log log = LogFactory.getLog(CAServiceImpl.class);
	private String passPhrase = null;
	//private int bufSize = 2048;
	

	/**
	 * 
	 */
	public CAServiceImpl(String caDir, String opensslDir) {
		super(caDir, opensslDir);
		// TODO Auto-generated constructor stub	
	}
	
	public void setPassphrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}
	
	public boolean checkOpenssl() {
		String opensslCmd = new String(opensslDir + File.separatorChar + 
				"bin" + File.separatorChar + "openssl");
		File openssl = new File(opensslCmd);
		
		if (!openssl.exists()) {
			log.error(opensslCmd  + " does not exist.");
			return false;
		}
		
		return true;
	}
	
	/** Returns Hashkey and CA Public key 
	 * @return
	 */
	public String getCaCert() 
			throws CAException {
		String key = null;
		
		if (!checkOpenssl()) {
			throw new CAException("Cannot find openssl...");
		}
		
		TxtFileHelper txt = new TxtFileHelper(confFile);
		String dirValue = null;
		try {
			if ((dirValue=txt.getValue(CAConstants.dir, 
					CAConstants.separator, CAConstants.comment)) == null) {
				log.error("The value for 'dir' is not found in config file.");
				log.error("CA cannot be installed.");
				throw new CAException("The value for 'dir' is not found " +
						"in config file.");
			}
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		}
		
		char separator = File.separatorChar;
		dirValue = dirValue.replace('\\', separator);
		dirValue = dirValue.replace('/', separator);
		String caCert = dirValue + separator + caCertFile;
		File certFile = new File(caCert);
		
		if ((!certFile.exists()) && (!certFile.canRead())) {
			log.error("Cannot get CA public key");
			return null;
		}
		
		try {
			key = FileUtils.file2String(certFile);
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		}
		
		return key;		
	}
	
	public String caInfo() throws CAException {
		File file = new File(gridConfFile);
		
		String info = null;
		try {
			info = FileUtils.file2String(file);
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
		}
		
		return info;
	}
	
	public String signCert(String certReq) throws CAException {
		String signedCert = null;
		
		if (null == certReq) {
			log.error("Certificate request is null.");
			return null;
		}
		
		if (!checkOpenssl()) {
			throw new CAException("Cannot find openssl...");
		}
		
		TxtFileHelper txt = new TxtFileHelper(confFile);
		Runtime rt = Runtime.getRuntime();
		
		String reqFileName = "certReq.pem";
		String[] names = reqFileName.split("\\.");
		File reqFile = null;
		try {
			reqFile = File.createTempFile(names[0], "."+names[1]);
			if (!FileUtils.checkFile(reqFile)) {
				log.error("Cannot sign certificate");
				return null;
			}
			reqFile.deleteOnExit();
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		}
		
		if (!FileUtils.writeString2File(certReq, reqFile)) {
			log.error("Cannot sign certificate");
			return null;
		}
		
		String  reqFilePath = reqFile.getAbsolutePath();
		String outDir = null;
		String dirName = null;
		try {
			outDir = txt.getValue(CAConstants.newCertsDir, CAConstants.separator,
					CAConstants.comment);
			if (null == outDir) {
				log.error("Value for " + CAConstants.newCertsDir + 
						" is not provided " + "in the config file");
				return null;
			}
			dirName = txt.getValue(CAConstants.dir, CAConstants.separator,
					CAConstants.comment);
			if (null == dirName) {
				log.error("Value for " + CAConstants.dir + " is not provided " +
						"in the config file");
				return null;
			}
			log.info("Dir : " + dirName);
			outDir = outDir.replaceAll(CAConstants.variableDecl + CAConstants.dir, 
						dirName);
			log.info("outDir : " + outDir);
			File file = new File(outDir);
			if ((!FileUtils.checkDir(file))) {
				log.error("Cannot sign certificate");
				return null;
			}
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		}
		
		String signedCertificate = new String(outDir + File.separatorChar + 
				CAConstants.signedCert);
		String cmd = opensslDir + File.separatorChar + "bin" + 
				File.separatorChar + "openssl";
		
		/*
		 * /usr/local/grit/openssl/bin/openssl ca -batch -in ./usercert_request.pem 
		 * 			-passin pass:sandya -out /usr/local/grit/ca/newcerts/signed.pem 
		 * */
		cmd = cmd + " ca -config " + confFile + " -batch -in " + reqFilePath + " -passin pass:" + passPhrase +
					" -out " + signedCertificate;
				
		Process p;
		BufferedReader cmdError = null;
		int exitVal = 0;
		try {
			p = rt.exec(cmd);
			cmdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			exitVal = p.waitFor();
			if (exitVal != 0) {
				log.error("Error in signing certificate...");
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
		
		File signedCertFile = new File(signedCertificate);
		if ((!signedCertFile.exists()) && (!signedCertFile.isFile())) {
			log.error(signedCertificate + " does not exist or is not a file");
			return null;
		}
		if (!signedCertFile.canRead()) {
			log.error(signedCertificate + " does not have read permissions");
			return null;
		}
		
		try {
			signedCert = FileUtils.file2String(signedCertFile);
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new CAException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new CAException(ioe);
		}		
		return signedCert;
	}
	
	/*public static void main(String args[]) {
		CAServiceImpl ca = new CAServiceImpl();
		try {
			//String caCert = ca.getCaCert();
			//log.info("CA Certificate");
			//log.info(caCert);
			File reqFile = new File("/home/sandya/.globus/usercert_request.pem");
			String req = FileUtils.file2String(reqFile);
			String signedCert = ca.signCert(req, "sandya");
			GridSecurity gridSecurity = new GridSecurity("/home/sandya/globus401");
			String destDir = System.getProperty("user.home") + File.separatorChar +
						".globus" + File.separatorChar;
			File userDestDir = new File(destDir);
			gridSecurity.installSignedCert(signedCert, userDestDir, "usercert.pem");
			log.info("SIGNED : " + signedCert);
		} catch (IOException ioe) {
			log.error(ioe);			
		} catch (CAException ce) {
			log.error(ce);
		} catch (GT4SecurityException gte) {
			log.error(gte);
		}
	}*/	
}