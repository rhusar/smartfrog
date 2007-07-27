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
import org.smartfrog.avalanche.client.sf.apps.gt4.build.GT4Exception;
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;
import org.smartfrog.avalanche.client.sf.apps.utils.TxtFileHelper;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SigningPolicy extends SecurityConstants {
	private String hash = null;
	private static Log log = LogFactory.getLog(SigningPolicy.class); 

	public SigningPolicy(String hash) {
		super();
		// TODO Auto-generated constructor stub
		this.hash = hash;
	}
	
	private String createPolicyFile() throws GT4SecurityException {
		InputStream src = getClass().getResourceAsStream(signingPolicyTmpl);
		if (src == null) {
			log.error("Cannot find the template file " + signingPolicyTmpl);
			throw new GT4SecurityException("Cannot find the template file " + 
					signingPolicyTmpl);
		}
		
		InputStream fis;
		File policyFile;
		try {
			fis = getClass().getResourceAsStream(signingPolicyTmpl);
			policyFile = new File(certificateDir + File.separatorChar + 
					hash + ".signing_policy"); 
			DiskUtils.fCopy(fis, policyFile);
		} catch (FileNotFoundException fnfe) {
			log.error("Error in creating signing policy file", fnfe);
			throw new GT4SecurityException(fnfe);
		} catch (IOException ioe) {
			log.error("Error in creating signing policy file", ioe);
			throw new GT4SecurityException(ioe);
		}
		
		return policyFile.getAbsolutePath();
	}
		
	public void getPolicyFile(String caSubject, String policy) 
				throws GT4SecurityException {
		if (null == caSubject) {
			log.error("CA subject cannot be null");
			throw new GT4SecurityException("CA Subject is null");
		}
		
		String policyFile = createPolicyFile();
		if (null == policy) {
			//log.info("Length of caSubject : " + caSubject.length());
			//log.info("CN index = " + caSubject.indexOf("CN="));
			int from = caSubject.indexOf("/O=");
			int to = caSubject.indexOf("CN=");
			policy = caSubject.substring(from, to) + "*";
		}
	
		TxtFileHelper txtHelper;
		try {
			txtHelper = new TxtFileHelper(policyFile);
			txtHelper.replaceString("CA_SUBJECT_NAME", caSubject);
			txtHelper.replaceString("CA_SIGNING_POLICY", policy);
			FileUtils.chgPermissions(policyFile, "644");
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4SecurityException(ie);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);
		}
	}	
	
	public static void main(String args[]) 
		throws FileNotFoundException, IOException, GT4Exception, GT4SecurityException {
		File certFile = new File("/tmp/58245f0b.0");
		String caCert = FileUtils.file2String(certFile);
		CAUtils utils = new CAUtils(caCert);
		SigningPolicy policy = new SigningPolicy("58245f0b");
		policy.getPolicyFile(utils.getCASubject(), null);		
	}
}