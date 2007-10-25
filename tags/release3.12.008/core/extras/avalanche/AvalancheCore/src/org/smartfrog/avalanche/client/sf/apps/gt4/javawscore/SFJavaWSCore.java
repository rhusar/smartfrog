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

import java.io.File;
import java.rmi.RemoteException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFJavaWSCore extends PrimImpl implements Prim {
	private final String GLOBUS_LOCATION = "globusLocation";
	private final String BUILDFILE = "buildFile";
	private final String SHDTERMINATE = "shouldTerminate";
	
	private String globusLocation, buildFile;
	boolean shouldTerminate = true;
	private static Log log = LogFactory.getLog(SFJavaWSCore.class);

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFJavaWSCore() throws RemoteException {
		super();
	}
	
	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		try {
			// mandatory attributes
			globusLocation = 
				(String)sfResolve(GLOBUS_LOCATION, globusLocation, true);
			buildFile = (String)sfResolve(BUILDFILE, buildFile, true);
			
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
		
		JavaWSCore wscore = new JavaWSCore(globusLocation, buildFile);
		File f = new File(buildFile);
		if ( !f.exists()){
			throw new SmartFrogException("Error! Build file doesnt exist : " + buildFile);
		}
		if (!f.isFile()) {
			throw new SmartFrogException("Error! Build file " + buildFile +
					" is not a file");
		}
		
		sfLog().info("Staring GT4 WSCore build from sources buiildFile : " + buildFile);
		try {
			wscore.buildFromSource(null);	
		} catch (WSCoreException be) {
			sfLog().info("Failed to build GT4 WSCore");
			throw new SmartFrogException("Build Failed : " + be.toString());			
		}		
		sfLog().info("Finished GT4 WSCore build from sources");
		log.info("Normal termination :" + sfCompleteNameSafe());
		/*TerminationRecord termR = new TerminationRecord("normal", 
	            		"Installed Java WS Core : ",sfCompleteName());
	      TerminatorThread terminator = new TerminatorThread(this,termR);
	      terminator.start();
	      */
	        	    
	    // terminate synchronously
		TerminationRecord tr = new TerminationRecord("normal", "Terminating ...", sfCompleteName());
		sfTerminate(tr);
	}	

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
