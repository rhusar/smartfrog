/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.shared.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.ProviderManager;
import org.smartfrog.avalanche.shared.handlers.MessageHandler;
import org.smartfrog.avalanche.shared.MonitoringEvent;
import org.smartfrog.avalanche.shared.MonitoringEventDefaultImpl;
import org.smartfrog.avalanche.shared.XMPPEventExtension;
import org.smartfrog.avalanche.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private List handlers = new ArrayList();

	public EventListener() {
		super();
	}

	public void processPacket(Packet p) {
        log.info("Processing packet from " + p.getFrom() + " to " + p.getTo());
        PacketExtension pe = p.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
		log.info(pe);
		// convert XML to AvalancheEvent and call handler chain on the event. 
		// see if a new thread is need to call event handlers. 
		try{
			Document doc = XMLUtils.loadFromString(pe.toXML(), false);
			MonitoringEvent event = fromXML(doc);
            log.info("Dispatching to all registered handlers.");
            for (Iterator it = handlers.iterator(); it.hasNext();) {
                Object handler = (Object) it.next();
                MessageHandler h = (MessageHandler) handler;
                h.handleEvent(event);
            }
        }catch(SAXException e){
			// discard event
			log.error("XMPP listener, malformed message " , e);
		}catch(IOException e){
			// discard event
			log.error("XMPP listener read error " , e);
		}
	}
	
	/**
	 * Adds a message handler, this method doesnt check for duplicate handlers
	 * so multiple instances of same handler can be submitted with different configurations.
	 * Handlers should be added at system startup only.  
	 * @param handler
	 */
	public void addHandler(MessageHandler handler){
		handlers.add(handler);
	}
	
	public void setup(){
		// register extension provider first 
		ProviderManager.addExtensionProvider(XMPPEventExtension.rootElement, XMPPEventExtension.namespace, new XMPPEventExtension());
	}
	
	public static class XMPPPacketFilter implements PacketFilter{
		public boolean accept(Packet p) {
			log.info("Received Packet: " + p.toXML());
			if ( p.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace) != null){
                log.info("Accepting packet.");
                return true;
			}
			Iterator itor = p.getExtensions();
			while(itor.hasNext()){
				PacketExtension ext = (PacketExtension)itor ;
				log.info("Received Packet Extension : " + ext.getElementName());
				log.info("Received Packet Extension NS : " + ext.getNamespace());
			}
            log.info("Discarding packet.");
            return false;
		}
	}
	/**
	 * Returns null if the document is not valid event. 
	 * @param doc
	 * @return
	 */
	protected static MonitoringEvent fromXML(Document doc){
		MonitoringEvent event = null ;
		Element e = (Element)doc.getElementsByTagName("event").item(0);
		
		// TODO: Add proper error handling for malformed events
		Element mid = (Element)e.getElementsByTagName("moduleId").item(0);
		Element insName = (Element)e.getElementsByTagName("instanceName").item(0);
		Element host = (Element)e.getElementsByTagName("host").item(0);
		Element moduleState = (Element)e.getElementsByTagName("moduleState").item(0);
		Element messageType = (Element)e.getElementsByTagName("messageType").item(0);
		Element msg = (Element)e.getElementsByTagName("msg").item(0);
		Element lastAction = (Element)e.getElementsByTagName("lastAction").item(0);
		Element timestamp = (Element)e.getElementsByTagName("timestamp").item(0);
		
		event = new MonitoringEventDefaultImpl();
		event.setModuleId(mid.getFirstChild().getNodeValue());
		event.setInstanceName(insName.getFirstChild().getNodeValue());
		event.setHost(host.getFirstChild().getNodeValue());
		event.setModuleState(moduleState.getFirstChild().getNodeValue());
		event.setMessageType(Integer.parseInt(messageType.getFirstChild().getNodeValue()));
		event.setMsg(msg.getFirstChild().getNodeValue());
		event.setLastAction(lastAction.getFirstChild().getNodeValue());
		event.setTimestamp(timestamp.getFirstChild().getNodeValue());
		
		return event ;
	}
}
