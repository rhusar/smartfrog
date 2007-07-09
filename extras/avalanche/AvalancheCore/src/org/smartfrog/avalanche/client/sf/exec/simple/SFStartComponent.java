/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on May 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.exec.simple;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @author bnaveen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFStartComponent extends PrimImpl implements Prim {
	private static final String COMPNAME="componentName";
	private static final String COMPPATH="componentPath";
	private static final String ENV = "env";
	private static final String PARAMETERS = "parameters";
	
	private String componentPath;
	private String componentName;
	private String envp, parameters;
	StartComponent appStart;
	
	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFStartComponent() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		try { 
			sfLog().info("Env : "+ envp);
			sfLog().info("Starting component = " + componentName + " command = " +componentPath);
			appStart.startApplication();
			appStart.readOutput();
		} catch(IOException ioe) {
			sfLog().err("Error in executing the command");
			throw new SmartFrogException(ioe.toString());
		}
		sfLog().info("Executed the command " + componentPath + " successfully");
		
		TerminationRecord tr = new TerminationRecord("Normal", "Completed executing command... ",
				sfCompleteName());
		sfTerminate(tr);
	}
	
	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		try{
			componentName= (String)sfResolve(COMPNAME);
			componentPath=(String)sfResolve(COMPPATH);
			envp = (String)sfResolve(ENV);
			parameters = (String)sfResolve(PARAMETERS);
			System.out.println("Parameters : " + parameters);
			parameters = parameters.replaceAll(",", " ");
			System.out.println("Parameters good : " + parameters);
			
			if (envp.length() != 0) {
				String path = "PATH=$PATH:/usr/bin:/bin:/usr/local/bin:/usr/sbin";
				envp = envp + "," + path;
			}				
				
			if (parameters.length() != 0) 
				componentPath = componentPath + " " + parameters + " ";
			appStart=new StartComponent(componentName,componentPath, envp);
		}catch(ClassCastException e){
			sfLog().error("Unable to resolve Component",e);
			throw new SmartFrogException("Unable to resolve Component",e);
		}				
	}

	/* (non-Javadoc)
	 * @see org.smartfrog.sfcore.prim.PrimImpl#sfTerminateWith(org.smartfrog.sfcore.prim.TerminationRecord)
	 */
	public synchronized void sfTerminateWith(TerminationRecord status) {
	/*	try {
			//appStart.readOutput();
			//appStart.stopApplication();
		} catch(IOException ioe) {
			sfLog().err("Error : " + ioe.toString());			
		} catch(InterruptedException ie) {
			sfLog().err("Error : " + ie.toString());
		}*/
		super.sfTerminateWith(status);
	}
}
