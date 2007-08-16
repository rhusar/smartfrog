/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.shared.xmpp;

import org.smartfrog.avalanche.shared.handlers.MessageHandler;
import org.smartfrog.avalanche.shared.jms.MessageListener;
import org.smartfrog.avalanche.shared.MonitoringEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class logs in as a user on the xmpp server, and receives messages and 
 * notifications from clients. 
 * If xmpp server is not running when it starts it tries connect repeatedly until success. 
 * if login fails it returns error. 
 * @author sanjaydahiya
 * @deprecated Unused!
 */
public class ListenerThread implements Runnable {

	private Map handlers ; 
	MessageListener listener ;
	boolean stopThread = false ;
	
	protected void init(){
		listener = new MessageListener();
		// add listener specific configurations here
	}
	
	public void addHandler(int eventType, MessageHandler handler){
		Object o = null; 
		if( (o = handlers.get(new Integer(eventType))) != null ){
			List list = (List)o;
			list.add(handler);
		}
	}
	public void tryStop(){
		stopThread = true ; 
	} 
	/**
	 * blocking call on JMS. waits for 1000 ms each time to avoid infinite 
	 * waits for shutdown
	 */
	protected void nextMessage(){
		try{
			MonitoringEvent event = listener.receive();
			// pass on to handlers. 
			// DB status update handler 
			// log handler
			Object o = handlers.get(new Integer(event.getMessageType())) ;
			if( null != o){
				System.out.println("Received message from JMS .");
				List lst = (List)o;
				Iterator itor = lst.iterator();
				while(itor.hasNext()){
					MessageHandler h = (MessageHandler)itor.next();
					h.handleEvent(event) ;
				}
			}
		}catch(Exception e){
			// log and continue for now
			e.printStackTrace();
		}
	}

	public void run() {
		
		try{
			init();
			// TODO: From context listener use ServerSetup.  
	//		addHandler( MonitoringConstants.MODULE_STATE_CHANGED, new ActiveProfileUpdateHandler());
	//		addHandler( MonitoringConstants.MODULE_INFO, new ActiveProfileUpdateHandler());
	//		addHandler( MonitoringConstants.MODULE_INFO, new ActiveProfileUpdateHandler());
			while(!stopThread){
				//
				nextMessage();
			}
			listener.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
