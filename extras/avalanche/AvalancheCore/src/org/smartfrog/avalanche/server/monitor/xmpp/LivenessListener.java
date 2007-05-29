/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.monitor.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import org.smartfrog.avalanche.server.monitor.handlers.HostStateChangeHandler;
import org.smartfrog.avalanche.shared.HostStateEvent;
import org.smartfrog.avalanche.shared.HostStateEventDefaultImpl;

/**
 * This class listens for Liveness status of nodes, when it receives a liveness
 * status change event, it invokes a list of handlers on the event. 
 * @author sanjaydahiya
 *
 */
public class LivenessListener implements RosterListener {
	private ArrayList handlers = new ArrayList();
	private Roster roster ;
	private static Log log = LogFactory.getLog(LivenessListener.class);
	
	/**
	 * Constructor needs to roster on which this listerner is registered. 
	 * Roster is used to check the current state of the host after state
	 * chage event is received. 
	 * @param r
	 */
	public LivenessListener(Roster r){
		roster = r ;
	}
	public void addLivenessHandler(HostStateChangeHandler handler){
		handlers.add(handler) ;
	}
	
	/**
	 * Handler must implement equals method to identify it as the
	 * unique handler, if this method is not implemented properly
	 * some other handler may get removed.  
	 * @param handler
	 */
	public void removeHandler(HostStateChangeHandler handler){
		handlers.remove(handler);
	}
	
	public void entriesAdded(Collection addresses) {

	}

	public void entriesUpdated(Collection addresses) {

	}

	public void entriesDeleted(Collection addresses) {

	}

	public void presenceChanged(String address) {
		log.debug("Presence changed event : " + address);
		
		HostStateEvent event = new HostStateEventDefaultImpl();
		// extract host name (before @ in address)
		event.setHostName(address.substring(0, address.indexOf('@')));

		Presence p = roster.getPresence(address);
		if( null == p ){
			event.setAvailable(false);
		}else{
			if( p.getType().equals(Presence.Type.AVAILABLE)){
				event.setAvailable(true);
			}else{
				event.setAvailable(false);
			}
		}
		
		Iterator it = handlers.iterator();
		while(it.hasNext()){
			((HostStateChangeHandler)it.next()).handleEvent(event);
		}
	}
}
