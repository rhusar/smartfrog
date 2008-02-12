/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/

package org.smartfrog.services.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

import java.util.Map;
import java.util.HashMap;

/**
 * XMPP/Jabber extention for Avalanche Events. Implements standard Avalanche  MonitoringEvent 
 * interface so it can be used with xmpp/jabber without changing rest of Avalanche system. 
 * @author sanjaydahiya
 *
 */
public class XMPPEventExtension implements PacketExtension, MonitoringEvent {
	public static final String namespace = "http://smartfrog.org/avalanche/core/Event/05";
	public static final String rootElement = "event" ; 
	private String moduleId = "None";
	private String host = "None";
	private String moduleState = "None";
	private String msg = "None";
	private Map<String, String> propertyBag = new HashMap<String, String>();
	private String instanceName = "None";
	private int messageType = -1; 
	private String timestamp = "None";
	private String lastAction = "None";

	/**
	 * Default constructor
	 *
	 */
	public XMPPEventExtension(){
		
	}
	
	/** 
	 * Copy constructor. 
	 * @param e
	 */
	public XMPPEventExtension(MonitoringEvent e){
		moduleId = e.getModuleId();
		host = e.getHost();
		moduleState = e.getModuleState();
		msg = e.getMsg();
		propertyBag = e.getPropertyBag();
		instanceName = e.getInstanceName();
		messageType = e.getMessageType();
		timestamp = e.getTimestamp();
		lastAction = e.getLastAction();
	}
	
	public String getElementName() {
		return rootElement;
	}

	public String getNamespace() {
		return namespace;
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

	public Map<String, String> getPropertyBag() {
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
		String xml =   "<" + rootElement + " xmlns=\"" + namespace + "\">" +
                "<moduleId>"	+	moduleId 	+	"</moduleId>" +
                "<instanceName>"+	instanceName	+"</instanceName>" +
                "<host>"		+	host			+"</host>" +
                "<moduleState>"+	moduleState 	+ "</moduleState>" +
                "<messageType>"+	messageType	+"</messageType>" +
                "<msg>"		+	msg+"</msg>" +
                "<lastAction>"	+	lastAction 	+"</lastAction>" +
                "<timestamp>" 	+ 	timestamp 	+"</timestamp>";

        // dump the propery bag
        xml += "<propertyBag>";
        for (String s : propertyBag.keySet()) {
            xml += "<" + s + ">" + propertyBag.get(s) + "</" + s + ">";
        }
        xml += "</propertyBag>";

        xml +=  "</event>" ;
		
		return xml;		
	}
	
	public String toString(){
		String result = "XMPPEventExtension[moduleId="+ moduleId +
		",host=" + host +
		",instanceName=" + instanceName +
		",msg=" + msg +
		",lastAction=" + lastAction +
		",moduleState=" + moduleState +
		",timestamp=" + timestamp;

        // dump the propertybag
        result += ",propertybag:";
        for (String s : propertyBag.keySet()) {
            result += s + "=" + propertyBag.get(s) + " ";
        }

        result += "]";

        return result;
    }

/*	
	public Document toXMLDoc(){
		Document doc = null ;
		try{
			doc = XMLUtils.newDocument() ;
		}catch(ParserConfigurationException e){
			// should never happen
			e.printStackTrace();
		}
		
		Node rootNode = doc.createElementNS(namespace, rootElement);
		doc.appendChild(rootNode);
		
		Node midNode = doc.createElement("moduleId");
		midNode.appendChild(doc.createTextNode(moduleId)) ;
		rootNode.appendChild(midNode);
		
		Node iNameNode = doc.createElement("instanceName");
		iNameNode.appendChild(doc.createTextNode(instanceName)) ;
		rootNode.appendChild(iNameNode);
		
		Node hostNode = doc.createElement("host");
		hostNode.appendChild(doc.createTextNode(host)) ;
		rootNode.appendChild(hostNode);
		
		Node mStateNode = doc.createElement("moduleState");
		mStateNode.appendChild(doc.createTextNode(moduleState)) ;
		rootNode.appendChild(mStateNode);
		
		Node mTypeNode = doc.createElement("messageType");
		mTypeNode.appendChild(doc.createTextNode(""+messageType)) ;
		rootNode.appendChild(mTypeNode);
		
		Node msgNode = doc.createElement("msg");
		msgNode.appendChild(doc.createTextNode(msg)) ;
		rootNode.appendChild(msgNode);
		
		Node actionNode = doc.createElement("lastAction");
		actionNode.appendChild(doc.createTextNode(lastAction)) ;
		rootNode.appendChild(actionNode);
		
		Node tsNode = doc.createElement("timestamp");
		tsNode.appendChild(doc.createTextNode(timestamp)) ;
		rootNode.appendChild(tsNode);
		
		return doc; 
	}
	*/
}
