/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.monitor.xmpp;

import org.smartfrog.avalanche.shared.MonitoringConstants;
import org.smartfrog.avalanche.shared.MonitoringEvent;
import org.smartfrog.avalanche.shared.MonitoringEventDefaultImpl;
import org.smartfrog.avalanche.shared.xmpp.XMPPAdapter;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Calendar;

/**
 * XMPP based event component. This can be looked up by other 
 * components and used for sending events. 
 * Smartfrog descriptor for this component should provide xmpp server details. 
 * This component acts as a singleton used by other components. SendMsg method is synchronized
 * in this version, so only one  
 * @author sanjaydahiya
 *
 */
public class SFAvalancheEventService extends PrimImpl implements Prim {
	
	private String xmppServer ;
	private int xmppServerPort = XMPPAdapter.default_xmpp_port;
	private boolean useSSL = true;
	private String xmppListenerUserId ;
	private XMPPAdapter adapter ;
	
	public SFAvalancheEventService() throws RemoteException {
		super();
	}

	/**
	 * Get the attributes from system properties, also if attributes are present in the descriptor 
	 * the values from descriptors will override system properties. 
	 */
	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

        // check system properties
		xmppServer = System.getProperty("AVALANCHE_EVENT_SERVER");
		String p = System.getProperty("AVALANCHE_EVENT_SERVER_PORT");

        if( null == xmppServer ){
			throw new SmartFrogException("Avalanche server name not specified"); 
		}
        adapter = new XMPPAdapter(xmppServer, useSSL);
		
		if( null != p ){
			xmppServerPort = Integer.parseInt(p);
			adapter.setXmppServerPort(xmppServerPort);
		}
		
		xmppListenerUserId = (String)sfResolve("avalancheListenerUserId", true);
		adapter.setXmppListenerName(xmppListenerUserId);

		// xmpp username is ip address of host 
		try{
			String userName = InetAddress.getLocalHost().getHostName().toLowerCase();
			//TODO: currently uname and pwd are same for sending events
			// change it 
			adapter.setXmppUserName(userName);
			adapter.setXmppPassword(userName);
		}catch(UnknownHostException e){
			// should never happen. 
			throw new SmartFrogException(e);
		}
		
		try{
			sfLog().info("Starting XMPP Client Adapter, server = " + xmppServer 
					+ " this host = " + adapter.getXmppUserName());

            adapter.init();
            adapter.login();

            // send an event to server allowing to subscribe for Roter notification.
			MonitoringEvent event = new MonitoringEventDefaultImpl();
			event.setHost(InetAddress.getLocalHost().getHostName());
			event.setInstanceName("None");
			event.setLastAction("None");
			event.setMessageType(MonitoringConstants.HOST_STARTED);
			event.setTimestamp(""+Calendar.getInstance().getTimeInMillis());
			event.setMsg("Host started. ");
			event.setModuleId("None");
			event.setModuleState("None");
			
			adapter.sendEvent(event);
			
		}catch(Exception e){
			sfLog().error("Failed to initialize XMPP Client : "+ e);
			adapter.close(); 
			// terminate the component abnormally
			TerminationRecord tr = TerminationRecord.abnormal("Error connecting to XMPP event server", sfCompleteName());
			
			sfTerminate(tr) ;
		}
		
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
	}

    /**
     * Called upon the daemon's termination?!
     * @param arg0
     */
    public void sfTerminate(TerminationRecord arg0) {
		sfLog().info("Closing XMPP Client Adapter");
		adapter.close();
		super.sfTerminate(arg0);
	}
	
	
}
