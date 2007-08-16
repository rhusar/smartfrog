/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.client.monitor.xmpp;

import org.jivesoftware.smack.packet.IQ;
import org.smartfrog.avalanche.shared.MonitoringEvent;
import org.smartfrog.avalanche.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @deprecated Unused.
 */
public class AvalancheEventIQ extends IQ implements MonitoringEvent {
	
	private String moduleId ;
	private String host ;
	private String moduleState ;
	private String msg ;
	private Map propertyBag ;
	private String instanceName = null;
	private int messageType ; 
	private String timestamp ; 
	private String lastAction ; 

	public String getChildElementXML() {
		return null;
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public String getHost() {
		return host;
	}

	public String getModuleState() {
		return moduleState;
	}

	public int getMessageType() {
		return messageType;
	}

	public String getMsg() {
		return msg;
	}

	public Map getPropertyBag() {
		return propertyBag;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getLastAction() {
		return lastAction;
	}

	public void setModuleId(String id) {
		this.moduleId = id ;
	}

	public void setHost(String h) {
		this.host = h;
	}

	public void setModuleState(String state) {
		this.moduleState = state ; 
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName ; 
	}

	public void setMessageType(int type) {
		this.messageType = type ; 
	}

	public void setMsg(String m) {
		this.msg = m ;
	}

	public void setTimestamp(String t) {
		this.timestamp = t;
	}

	public void addToPropertyBag(String key, String value) {
		propertyBag.put(key, value);
	}

	public void setLastAction(String action) {
		this.lastAction = action ;
	}

	public String toXML() {
		StringWriter sw = new StringWriter();
		try{
			XMLUtils.printDocument(toXMLDoc(), sw);
		}catch(TransformerException e){
			e.printStackTrace();
		}
		return sw.toString();
	}
	
	public Document toXMLDoc(){
		Document doc = null ;
		try{
			doc = XMLUtils.newDocument() ;
		}catch(ParserConfigurationException e){
			// should never happen
			e.printStackTrace();
		}
		
		Node midNode = doc.createElement("moduleId");
		midNode.appendChild(doc.createTextNode(moduleId)) ;
		doc.appendChild(midNode);
		
		Node iNameNode = doc.createElement("instanceName");
		iNameNode.appendChild(doc.createTextNode(instanceName)) ;
		doc.appendChild(iNameNode);
		
		Node hostNode = doc.createElement("host");
		hostNode.appendChild(doc.createTextNode(host)) ;
		doc.appendChild(hostNode);
		
		Node mStateNode = doc.createElement("moduleState");
		mStateNode.appendChild(doc.createTextNode(moduleState)) ;
		doc.appendChild(mStateNode);
		
		Node mTypeNode = doc.createElement("messageType");
		mTypeNode.appendChild(doc.createTextNode(""+messageType)) ;
		doc.appendChild(mTypeNode);
		
		Node msgNode = doc.createElement("msg");
		msgNode.appendChild(doc.createTextNode(msg)) ;
		doc.appendChild(msgNode);
		
		Node actionNode = doc.createElement("lastAction");
		actionNode.appendChild(doc.createTextNode(lastAction)) ;
		doc.appendChild(actionNode);
		
		Node tsNode = doc.createElement("timestamp");
		tsNode.appendChild(doc.createTextNode(timestamp)) ;
		doc.appendChild(tsNode);
		
		return doc; 
	}

}
