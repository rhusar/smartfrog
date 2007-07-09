/*
 * Created on Feb 23, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.exec;

import org.smartfrog.avalanche.client.sf.exec.simple.StartComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFStopContainer extends PrimImpl implements Prim {
	private static final String COMPNAME="componentName";
	private static final String COMPPATH="componentPath";
	private static final String ENV = "env";
	private static final String PARAMETERS = "parameters";
	private static final String PROXYCERT = "proxyCert";
	
	private String componentPath;
	private String componentName;
	private String envp, parameters, proxyCert;
	StartComponent appStart;
	
	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFStopContainer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		try{
			sfLog().info("In sfDeploy....");
			componentName= (String)sfResolve(COMPNAME);
			componentPath=(String)sfResolve(COMPPATH);
			envp = (String)sfResolve(ENV);
			parameters = (String)sfResolve(PARAMETERS);
			proxyCert = (String)sfResolve(PROXYCERT);
			
			parameters = parameters.replaceAll(",", " ");
			
			if (envp.length() != 0) {
				String path = "PATH=$PATH:/usr/bin:/bin:/usr/local/bin:/usr/sbin";
				envp = envp + "," + path;				
			}				
			if (parameters.length() != 0) 
				componentPath = componentPath + " " + parameters + " ";
			
			sfLog().info("command : " + componentPath);
			if (proxyCert.length() != 0) {
				envp = envp + "," + "X509_USER_PROXY=" + proxyCert;
			}
			
			sfLog().info("envp : " + envp);
			appStart=new StartComponent(componentName,componentPath, envp);
		}catch(ClassCastException e){
			sfLog().error("Unable to resolve Component",e);
			throw new SmartFrogException("Unable to resolve Component",e);
		}
		
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
		
		TerminationRecord tr = new TerminationRecord("Normal", "Completed executing command... ",
				sfCompleteName());
		sfTerminate(tr);
	}	
	

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
