/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.exec.simple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 * @author bnaveen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFStopComponent extends PrimImpl implements Prim {
	private static final String COMPNAME="componentName";
	private static Log log = LogFactory.getLog(SFStopComponent.class);
	String componentName;
	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFStopComponent() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		String appStartComponent=componentName+"_StartComponent";
		try{
			//ProcessCompound pc = SFProcess.sfSelectTargetProcess(InetAddress.getLocalHost(),appStartComponent);
			//pc.sfTerminate(TerminationRecord.normal(null));
			Compound cp = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getLocalHost(),3800);
			Prim comp = (Prim)cp.sfResolve(appStartComponent);
		        comp.sfTerminate(TerminationRecord.normal(null));
			
		}catch(UnknownHostException e){
			log.error("Not able to find Local Host",e);
		}catch(Exception e){
			log.error("Unkown Exception in obtaining Root Process",e);
		}
		log.info("Stopped Application "+componentName);
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		try{
			componentName= (String)sfResolve(COMPNAME);
				
		}catch(ClassCastException e){
			log.error("Unable to resolve Component",e);
			throw new SmartFrogException("Unable to resolve Component",e);
		}
	}

}
