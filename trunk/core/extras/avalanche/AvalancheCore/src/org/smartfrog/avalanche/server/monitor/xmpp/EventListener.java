/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.monitor.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.ProviderManager;
import org.smartfrog.avalanche.shared.handlers.XMPPPacketHandler;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.XMPPEventExtensionProvider;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Implements PacketListener, this class is passed to XMPP client libraries 
 * it listens for messages and invokes a chain of handlers on the received messages. 
 * Need to work on scalability of this class, sine this is the single listener for all messages
 * on the network. 
 * 
 * @author sanjaydahiya
 *
 */
public class EventListener implements PacketListener {
	private static Log log = LogFactory.getLog(EventListener.class);
	private ArrayList<XMPPPacketHandler> handlers = new ArrayList<XMPPPacketHandler>();

	public EventListener() {
		super();
	}

	public void processPacket(Packet p) {
        log.info("Processing packet from " + p.getFrom() + " to " + p.getTo());
        log.info("Dispatching to all registered handlers.");
        for (XMPPPacketHandler h : handlers)
            h.handlePacket(p);
}
	
	/**
	 * Adds a message handler, this method doesnt check for duplicate handlers
	 * so multiple instances of same handler can be submitted with different configurations.
	 * Handlers should be added at system startup only.  
	 * @param handler
	 */
	public void addHandler(XMPPPacketHandler handler){
		handlers.add(handler);
	}
	
	public void setup(){
		// register extension provider first 
		ProviderManager.addExtensionProvider(XMPPEventExtension.rootElement, XMPPEventExtension.namespace, new XMPPEventExtensionProvider());
	}
	
	public static class XMPPPacketFilter implements PacketFilter{
		public boolean accept(Packet p) {
			log.info("Received Packet: " + p.toXML());
			if ( p.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace) instanceof XMPPEventExtension ){
                log.info("Accepting packet.");
                return true;
			}
			Iterator itor = p.getExtensions();
			while(itor.hasNext()){
				PacketExtension ext = (PacketExtension)itor.next() ;
				log.info("Received Packet Extension : " + ext.getElementName());
				log.info("Received Packet Extension NS : " + ext.getNamespace());
			}
            log.info("Discarding packet.");
            return false;
		}
	}
}
