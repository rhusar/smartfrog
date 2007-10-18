/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Aug 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.javawscore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;


/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFDeployToTomcat extends PrimImpl implements Prim {
	private final String GLOBUS_LOCATION = "globusLocation"; 
	private final String TOMCAT_DIR = "tomcatDir";
	private final String TOMCAT_VER = "tomcatVersion";
	
	private final String TARGET = "target";
	private final String WEBAPPNAME = "WebAppName";
	private final String CONN_CLASS = "connClassName";
	private final String PORT = "port";
	private final String MAXTHREADS = "maxThreads";
	private final String MIN_SPARETHREADS = "minSpareThreads";
	private final String AUTOFLUSH = "autoFlush";
	private final String DISABLE_UPLDTIMEOUT = "disableUploadTimeout";
	private final String SCHEME = "scheme";
	private final String ENABLE_LOOKUPS = "enableLookups";
	private final String ACCEPT_COUNTS = "acceptCounts";
	private final String DEBUG = "debug";
	
	
	private final String VALVE_CLASS = "valveClassName";
	
	private final String SHDTERMINATE = "shouldTerminate";
	
	private String globusLocation, tomcatDir, tomcatVersion;
	private String target;
	private Vector webAppName;
	private Properties props = null;
	private Vector connClassName, port, maxThreads;
	private Vector minSpareThreads, autoFlush, disableUploadTimeout;
	private Vector scheme, enableLookups, acceptCounts;
	private Vector debug, valveClassName;
	
	private Hashtable connAttrs, valveAttrs;
	boolean shouldTerminate = true;
	private static Log log = LogFactory.getLog(SFDeployToTomcat.class);

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFDeployToTomcat() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		
		connAttrs = new Hashtable();
		valveAttrs = new Hashtable();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		try {
			// mandatory attributes
			globusLocation = 
				(String)sfResolve(GLOBUS_LOCATION, globusLocation, true);
			tomcatDir = (String)sfResolve(TOMCAT_DIR, tomcatDir, true);
			tomcatVersion = (String)sfResolve(TOMCAT_VER, tomcatVersion, true);
			
			// optional attributes
			readOptionalAttributes();
		}catch (ClassCastException e) {
			sfLog().err("Unable to resolve Component",e);
			throw new SmartFrogException("Unable to resolve Component",e);
		}
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		
		DeployToTomcat wscore = 
			new DeployToTomcat(globusLocation, tomcatDir);
		
		if (webAppName != null) {
			props = new Properties();
			props.setProperty((String)webAppName.get(0), (String)webAppName.get(1));
		}
		
		try {
			sfLog().info("Starting deploying application in tomcat ...");
			wscore.deployIntoTomcat(target, props);
		
		sfLog().info("Finished deploying application in tomcat.");
		
		sfLog().info("Configuring the deployment...");
		
			wscore.editXMLFiles(tomcatVersion, connAttrs, valveAttrs);
		} catch (WSCoreException tce) {
			sfLog().info("WSCoreException : " + tce);
			throw new SmartFrogException(tce.toString());
		}
		sfLog().info("Tomcat XML files configured.");
		
		
/*            log.info("Normal termination :" + sfCompleteNameSafe());
            TerminationRecord termR = new TerminationRecord("normal", 
            		"Deployed WS Core into Tomcat : ",sfCompleteName());
            TerminatorThread terminator = new TerminatorThread(this,termR);
            terminator.start();
            */
			if (shouldTerminate) {
				TerminationRecord tr = new TerminationRecord("normal", "Terminating ...", sfCompleteName());
				sfTerminate(tr);
			}			
	}	

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}
	
	private void readOptionalAttributes() throws SmartFrogException, RemoteException{
		target = (String)sfResolve(TARGET, target, false);
		webAppName = (Vector)sfResolve(WEBAPPNAME, webAppName, false);
		
		connClassName = (Vector)sfResolve(CONN_CLASS, connClassName, false);
		connAttrs.put(
				(String)connClassName.get(0), (String)connClassName.get(1));
		
		port = (Vector)sfResolve(PORT, port, false);
		connAttrs.put((String)port.get(0), (String)port.get(1));
		
		maxThreads = (Vector)sfResolve(MAXTHREADS, maxThreads, false);
		connAttrs.put((String)maxThreads.get(0), (String)maxThreads.get(1));
		
		minSpareThreads = 
			(Vector)sfResolve(MIN_SPARETHREADS, minSpareThreads, false);
		connAttrs.put(
				(String)minSpareThreads.get(0), (String)minSpareThreads.get(1));
		
		autoFlush = (Vector)sfResolve(AUTOFLUSH, autoFlush, false);
		connAttrs.put((String)autoFlush.get(0), (String)autoFlush.get(1));
		
		disableUploadTimeout = 
			(Vector)sfResolve(DISABLE_UPLDTIMEOUT, disableUploadTimeout, false);
		connAttrs.put(
				(String)disableUploadTimeout.get(0), (String)disableUploadTimeout.get(1));
			
		scheme = (Vector)sfResolve(SCHEME, scheme, false);
		connAttrs.put((String)scheme.get(0), (String)scheme.get(1));
		
		enableLookups = (Vector)sfResolve(ENABLE_LOOKUPS, enableLookups, false);
		connAttrs.put((String)enableLookups.get(0), (String)enableLookups.get(1));
		
		acceptCounts = (Vector)sfResolve(ACCEPT_COUNTS, acceptCounts, false);
		connAttrs.put((String)acceptCounts.get(0), (String)acceptCounts.get(1));
		
		debug = (Vector)sfResolve(DEBUG, debug, false);
		connAttrs.put((String)debug.get(0), (String)debug.get(1));
		
		valveClassName = (Vector)sfResolve(VALVE_CLASS, valveClassName, false);
		valveAttrs.put((String)valveClassName.get(0), (String)valveClassName.get(1));
		
		shouldTerminate = sfResolve(SHDTERMINATE, true, false);
		//shouldTerminate = true;
	}
}