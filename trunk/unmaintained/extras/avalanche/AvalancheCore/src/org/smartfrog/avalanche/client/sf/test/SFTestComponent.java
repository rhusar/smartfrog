/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.sf.test;

import org.smartfrog.avalanche.client.sf.anubis.SFModuleStateManager;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.services.xmpp.MonitoringEvent;
import org.smartfrog.services.xmpp.MonitoringEventDefaultImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;


/**
 * Anubis server should be deployed before running this application. 
 * @author sanjaydahiya
 *
 */
public class SFTestComponent extends PrimImpl implements Prim {
	private static final String MSG = "msg";
	
	String msg ;
	String moduleId ;
	String instanceName ;
	
	MonitoringEvent event = null ;
	SFModuleStateManager stateManager = null ;	
	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFTestComponent() throws RemoteException {
		super();
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		event.setModuleState("SFStart");
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		msg = (String)sfResolve(MSG, "testMessage" , false);	
		moduleId = (String)sfResolve(MonitoringEvent.MODULEID,"TestID", false);	
		instanceName = (String)sfResolve(MonitoringEvent.INSTANCE_NAME, "testInstance", false);	
		
		// anubis reference
		stateManager = (SFModuleStateManager)sfResolve("moduleStateManager");
		// create a unique provider for this ..
		if( null == stateManager){
			sfLog().err("Module State manager could not be located");
		}else{
			// first create an Event
			event = new MonitoringEventDefaultImpl();
			event.setModuleId(moduleId);
			event.setInstanceName(instanceName);
			event.setLastAction((String)sfResolve(MonitoringEvent.ACTION_NAME, "testAction", false)) ;
			event.setMessageType(MonitoringConstants.MODULE_STATE_CHANGED);
			event.setModuleState("Initializing");
			event.setMsg("Initializing component : SFTestComponent") ;
			try{
				event.setHost(InetAddress.getLocalHost().getHostName());
			}catch(UnknownHostException shouldNeverHappen){
				shouldNeverHappen.printStackTrace();
			}
			stateManager.setModuleState(event);
			}
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		try{
			event.setModuleState("Terminating");
			event.setMsg("Finishing component : SFTestComponent") ;
			stateManager.setModuleState(event);
			
			stateManager.removeModule(moduleId) ;
		}catch(Exception e){
			sfLog().err("" + e);
		}
		super.sfTerminateWith(status);
	}

}

