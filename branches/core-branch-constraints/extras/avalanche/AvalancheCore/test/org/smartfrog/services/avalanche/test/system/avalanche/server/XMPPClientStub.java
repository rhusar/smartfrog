/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package tests.org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.shared.xmpp.XMPPAdapter;
import org.smartfrog.avalanche.shared.MonitoringConstants;
import org.smartfrog.avalanche.shared.MonitoringEvent;
import org.smartfrog.avalanche.shared.MonitoringEventDefaultImpl;

// TODO: Test Settings _OBSOLETE_!
public class XMPPClientStub {
	XMPPAdapter adapter = new XMPPAdapter(null, true);
	String hostId = "192.168.1.102" ;
	
	public void run() throws Exception{
		// connect to xmpp server as host 
		adapter.setXmppServer("sanjay.local");
		
		adapter.setXmppUserName(hostId);
		adapter.setXmppPassword(hostId);
		adapter.setXmppListenerName("avl") ; 
		adapter.init();
		System.out.println("Connected to xmpp server "); 
		
		// send a few events for host
		MonitoringEvent event = new MonitoringEventDefaultImpl();
		event.setHost(hostId) ;
		event.setInstanceName("Testing");
		event.setLastAction("INSTALL");
		event.setMessageType(MonitoringConstants.HOST_STARTED);
		event.setTimestamp("101010");
		event.setMsg("Test Message for Host started. ");
		event.setModuleId("None");
		event.setModuleState("None");
		
		adapter.sendEvent(event);
		System.out.println("Sent Event ..  press any key to exit. "); 
		
		System.in.read();
		// send a few events for modules 
		
		// close 
		adapter.close();
	}
	
	public static void main(String[] args) throws Exception{
		XMPPClientStub client = new XMPPClientStub() ;
		client.run(); 
	}

}
