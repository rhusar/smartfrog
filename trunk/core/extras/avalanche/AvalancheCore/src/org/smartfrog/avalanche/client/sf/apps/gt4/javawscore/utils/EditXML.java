/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Aug 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.javawscore.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * Provides methods to edit the given xml file.
 */
public class EditXML extends XMLUtils {
	private String fileName;
	Document doc = null;

	private static Log log = LogFactory.getLog(EditXML.class);
	
	/**
	 * @param xmlFileName
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public EditXML(String xmlFileName) 
		throws IOException, SAXException, ParserConfigurationException {
		super();
		fileName = new String(xmlFileName);
		log.info("File name in EditXML : " + fileName);
		doc = XMLUtils.load(fileName, false);
		log.info("After XMLUtils.load()");
	}
	
	/**
	 * Creates an element with attribute names and values defined in the Hashtable
	 * @param tagName
	 * @param attrs
	 * @return 
	 */
	public Element createElementWithAttrs(String tagName, Hashtable attrs) {
		if (null == attrs) {
			log.error("No attributes defined.");
			return null;
		}
		Element element = doc.createElement(tagName);
		Enumeration e = attrs.keys();
		
		while (e.hasMoreElements()) {
			String name = (String)e.nextElement();
			String value = (String)attrs.get(name);
			element.setAttribute(name, value);
		}
		return element;
	}
	
	/**
	 * Creates an element with the given tag name and a value  
	 * @param tagName
	 * @param nodeValue
	 * @return
	 */
	public Element createElement(String tagName, String nodeValue) {
		Element element = doc.createElement(tagName);
		
		if (nodeValue == null) {
			return element;
		}
		else {
			element.setNodeValue(nodeValue);
			return element;
		}
	}
	
	/**
	 * Returns the first reference of Element with tagName
	 * @param tagName
	 * @return
	 */
	public Element getElementByTagName(String tagName) {
		NodeList list = doc.getElementsByTagName(tagName);		
		Element element = (Element)list.item(0);		
		
		return element;
	}

	/**
	 * Returns an element with tagName and whose attribute name=attrValue
	 * @param tagName
	 * @param attrValue
	 * @return
	 */
	public Element getElementByTagNameAttrName(String tagName, String attrValue) {
		NodeList list = doc.getElementsByTagName(tagName);
		
		for (int i=0; i<list.getLength(); i++) {
			Element element = (Element)list.item(i);
			String elementName = element.getTagName();
			String value = element.getAttribute("name");
			if ((elementName.equals(tagName) && (value.equals(attrValue)))) {
				return element;
			}
		}
		return null;
	}
	
	public Element getElementByTagNameAttrName(String tagName, String attrName, String attrValue) {
		NodeList list = doc.getElementsByTagName(tagName);
		
		for (int i=0; i<list.getLength(); i++) {
			Element element = (Element)list.item(i);
			String elementName = element.getTagName();
			String value = element.getAttribute(attrName);
			if ((elementName.equals(tagName) && (value.equals(attrValue)))) {
				return element;
			}
		}
		return null;
	}
	
	/**
	 * Adds a child node as the first child to a parent node.
	 * If the first node has the same name as child node, then it 
	 * is replaced.
	 * @param parent
	 * @param child
	 */
	public void addFirstChild(Element parent, Element child) {
		Node firstChild = parent.getFirstChild();
		if (child.getNodeName() == firstChild.getNodeName()) {
			parent.replaceChild(child, firstChild);
		}
		else {
			parent.insertBefore(child, parent.getFirstChild());			
		}
		
	}
	
	/**
	 * Adds a child node as the last child to a parent node
	 * @param parent
	 * @param child
	 */
	public void addLastChild(Element parent, Element child) {
		Node lastChild = parent.getLastChild();
		if (child.getNodeName() == lastChild.getNodeName()) {
			parent.replaceChild(child, lastChild);			
		}
		else {
			parent.appendChild(child);			
		}		
	}
	
	public void removeNode(Element ele) {
		Node parent = ele.getParentNode();
		parent.removeChild(ele);
	}
	
	/**
	 * Adds a child node before the last child of the parent node.
	 * @param parent
	 * @param child
	 */
	public void addBeforeLastChild(Element parent, Element child) {
		Node lastChild = parent.getLastChild();
		Node beforeLastChild = lastChild.getPreviousSibling();
		if (child.getNodeName() == beforeLastChild.getNodeName()) {
			parent.replaceChild(child, beforeLastChild);
		}
		else {
			parent.insertBefore(child, lastChild);			
		}		
	}
	
	/**
	 * Adds the following node to the parent node <Service name="Standalone-Tomcat">
	 * <Connector
	 * 	className="org.apache.catalina.connector.http.HttpConnector"
	 * 	port="8443" minProcessors="5" maxProcessors="75"
	 * 	authenticate="true" secure="true" scheme="https"
	 * 	enableLookups="true" acceptCount="10" debug="0">
	 * 		<Factory
	 * 			className="org.globus.tomcat.catalina.net.HTTPSServerSocketFactory"
	 * 			proxy="/path/to/proxy/file"
	 * 			cert="/path/to/certificate/file"
	 * 			key="/path/to/private/key/file"
	 * 			cacertdir="/path/to/ca/certificates/directory"/>
	 * </Connector>
	 * All the attribute name/values should be defined by the Hashtables
	 * This method is valid only for Tomcat version 4.1.x   
	 * @param connAttrs
	 * @return
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	/*public boolean addConnector(Hashtable connAttrs) 
				throws FileNotFoundException, TransformerException {
		Element parentElement = getElementByTagName("Service");
		if (parentElement == null) {
			log.error("The element 'Service' is not found. " +
					"Please check the server.xml file");
			return false;			
		}
		
		Element connElement = createElementWithAttrs("Connector", connAttrs);
		
		Hashtable factoryAttrs = new Hashtable();
		factoryAttrs.put("className", 
				"org.globus.tomcat.catalina.net.HTTPSServerSocketFactory");
		Element factoryElement = createElementWithAttrs("Factory", factoryAttrs);
		addFirstChild(connElement, factoryElement);
		addFirstChild(parentElement, connElement);
				
		return true;
	}*/
	
	/**
	 * Commits changes done to the XML document to the physical file
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	public void commitChanges() throws FileNotFoundException, TransformerException {
		File file= new File(fileName);
		OutputStream outstream= new FileOutputStream(fileName);
		docToStream(doc,outstream);
	}
	
	public static void main(String args[]) {		
		try {
			EditXML xml = new EditXML("/home/sandya/server.xml");
			
			Hashtable connAttrs = new Hashtable();
			connAttrs.put("className", "org.apache.catalina.connector.http.HttpConnector");
			connAttrs.put("port", "8443");
			connAttrs.put("minProcessors", "5");
			connAttrs.put("maxProcessors", "75");
			connAttrs.put("authenticate", "true");
			connAttrs.put("secure", "true");
			connAttrs.put("scheme", "https");
			connAttrs.put("enableLookups", "true");
			connAttrs.put("acceptCount", "10");
			connAttrs.put("debug", "0");
			
			//Hashtable factoryAttrs = new Hashtable();
			//factoryAttrs.put("className", "org.globus.tomcat.catalina.net.HTTPSServerSocketFactory");
			
			//xml.addConnector(connAttrs);
			
			Hashtable valueAttrs = new Hashtable();
			valueAttrs.put("className", "org.globus.tomcat.catalina.valves.HTTPSValve");
			Element engine = xml.getElementByTagNameAttrName("Engine", "Standalone");
			Element value = xml.createElementWithAttrs("Value", valueAttrs);
			xml.addFirstChild(engine, value);
			xml.commitChanges(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
