/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 23, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.ca;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.Properties;

/**
 * @author root
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFCAInstallation extends PrimImpl implements Prim {
	private final String INSTALLERDIR = "installerDir";
	private final String INSTALLATIONDIR = "installationDir";
	private final String CADIR = "caDirectory";
	private final String UNIQUESUB = "uniqueSubject";
	private final String COUNTRY = "country"; 
	private final String STATE = "state";
	private final String LOC = "location";
	private final String ORG = "organization";
	private final String ORGUNIT = "orgUnit";
	private final String COMMONNAME = "commonName";
	private final String EMAILADDR = "emailAddr";
	
	private final String PASSWD = "passwd";
	private final String VALIDITY = "validity";
	
	private final String SHDTERMINATE = "shouldTerminate";
	
	private String installerDir, uniqueSubject, country;
	private String installationDir, caDirectory;
	private String state, location, organization, orgUnit;
	private String commonName, emailAddr, passwd, validity;
	private boolean shouldTerminate;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFCAInstallation() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		try {
			// mandatory attributes
			installerDir = 
				(String)sfResolve(INSTALLERDIR, installerDir, true);
			installationDir = 
				(String)sfResolve(INSTALLATIONDIR, installationDir, true);
			caDirectory = 
				(String)sfResolve(CADIR, caDirectory, true);
			uniqueSubject = (String)sfResolve(UNIQUESUB, uniqueSubject, true);
			country = (String)sfResolve(COUNTRY, country, true);
			state = (String)sfResolve(STATE, state, true);
			location = (String)sfResolve(LOC, location, true);
			organization = (String)sfResolve(ORG, organization, true);
			orgUnit = (String)sfResolve(ORGUNIT, orgUnit, true);
			commonName = (String)sfResolve(COMMONNAME, commonName, true);
			emailAddr = (String)sfResolve(EMAILADDR, emailAddr, true);
			passwd = (String)sfResolve(PASSWD, passwd, true);
			validity = (String)sfResolve(VALIDITY, validity, true);
			
			// optional attribute
			shouldTerminate = sfResolve(SHDTERMINATE, true, false);
			//shouldTerminate = true;
		}catch (ClassCastException e) {
			sfLog().err("Unable to resolve Component",e);
			throw new SmartFrogException("Unable to resolve Component",e);
		}
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		InstallCA ca = new InstallCA(installerDir, installationDir, caDirectory);
		if ((country.length() != 2) || (state.length() != 2)) {
			sfLog().err("Need two letter country/state code....");
			sfLog().err("Please provide two letter country/state code");
			throw new SmartFrogException("Need two letter country/state code....");
		}
		
		try {
			ca.buildFrmOpenssl();
			//ca.buildFrmGlobus();
		} catch (CAException cae) {
			sfLog().err("Error in building CA", cae);
			throw new SmartFrogException("Error in building CA", cae);
		}
		
		Properties props = new Properties();
		
		props.setProperty("dir", caDirectory);
		props.setProperty("unique_subject", uniqueSubject);
		props.setProperty("C", country);
		props.setProperty("ST", state);
		props.setProperty("L", location);
		props.setProperty("O", organization);
		props.setProperty("OU", orgUnit);
		props.setProperty("CN", commonName);
		props.setProperty("emailAddress", emailAddr);
		
		try {
			ca.configureCA(props);
			ca.generateCaCert(passwd, validity);
		} catch (CAException cae) {
			sfLog().err("Error in installing CA", cae);
			throw new SmartFrogException("Error in installing CA", cae);
		}
		
		sfLog().info("Sucessfully installed CA");
		
		// terminate synchronously
		TerminationRecord tr = TerminationRecord.normal("Terminating ...", sfCompleteName());
		sfTerminate(tr);
		
	}	

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
