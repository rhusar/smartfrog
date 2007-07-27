/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jul 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.javawscore.utils;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.domapi.XPathEvaluatorImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathNSResolver;
import org.w3c.dom.xpath.XPathResult;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;

/**
 * @author sanjay
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
/**
 * @author sanjay, May 1, 2005
 *
 * TODO 
 */
public class XMLUtils {
	/**
	 * 
	 */
	public XMLUtils() { 
		super();
		// TODO Auto-generated constructor stub
	}
	
	
/**
 * Load XML file and return the root document.
 * @param fileName
 * @param validate
 * @return
 * @throws SAXException
 * @throws java.io.IOException
 */
public static Document load(String fileName, boolean validate)throws SAXException, 
		java.io.IOException {
	
	System.out.println("Before creating DOMParser object");
	DOMParser parser = new DOMParser();
	System.out.println("After creating DOMParser object");
	// set features
	if( validate){

			parser.setFeature("http://xml.org/sax/features/validation", true);
			parser.setFeature("http://apache.org/xml/features/validation/schema", true);
			parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
	
	}
	
	System.out.println("Before parsing file " + fileName);
	parser.parse(fileName);
	System.out.println("After parsing file " + fileName);
	Document document = parser.getDocument();
	System.out.println("After getting document");
	return document ;    
}


/**
 * Load XML file and return the root document.
 * @param is
 * @param validate
 * @return
 * @throws SAXException
 * @throws java.io.IOException
 */
public static Document load(InputStream is, boolean validate)throws SAXException, 
	java.io.IOException {

	DOMParser parser = new DOMParser();
	
	// set features
	if( validate){
	
			parser.setFeature("http://xml.org/sax/features/validation", true);
			parser.setFeature("http://apache.org/xml/features/validation/schema", true);
			parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
	
	}	
	InputSource src = new InputSource(is);
	parser.parse(src);
	Document document = parser.getDocument(); 
	is.close();
	return document ;    
}

public static Document loadFromString(String xmlString, boolean validate) 
	throws SAXException, java.io.IOException{
	
	DOMParser parser = new DOMParser();
	
	if( validate){
		parser.setFeature("http://xml.org/sax/features/validation", true);
		parser.setFeature("http://apache.org/xml/features/validation/schema", true);
		parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

	}
	InputSource src = new InputSource(new StringReader(xmlString)); 
	parser.parse(src);
	Document document = parser.getDocument(); 
	return document ;
}

public static Node getElementByXPath(Node node, String xpath){
	XPathEvaluator evaluator = new XPathEvaluatorImpl(
						node.getOwnerDocument());
	XPathNSResolver resolver = evaluator.createNSResolver(node);
	XPathResult result = (XPathResult)evaluator.evaluate(xpath, 
						node, 
						resolver, 
						XPathResult.ORDERED_NODE_ITERATOR_TYPE,
						null);	
	Node n = result.iterateNext();
	return n;
}

public static ArrayList getElementsByXPath(Node node, String xpath){
	XPathEvaluator evaluator = new XPathEvaluatorImpl(
						node.getOwnerDocument());
	XPathNSResolver resolver = evaluator.createNSResolver(node);
	XPathResult result = (XPathResult)evaluator.evaluate(xpath, 
						node, 
						resolver, 
						XPathResult.ORDERED_NODE_ITERATOR_TYPE,
						null);	
	Node n ;
	ArrayList resultSet = new ArrayList();

        while (( n = result.iterateNext())!= null){
		resultSet.add(n);
	}
	return resultSet;
}

public static Document newDocument() throws ParserConfigurationException{
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setValidating(true);
	factory.setNamespaceAware(true);
	factory.setIgnoringElementContentWhitespace(false);
	factory.setCoalescing(false);
	
	DocumentBuilder builder = factory.newDocumentBuilder();

	Document doc = builder.newDocument();
	return doc;
}

public static void printDocument(Node doc, Writer out) throws TransformerException{
     TransformerFactory transformerFactory =
             TransformerFactory.newInstance();
     Transformer transformer =
            transformerFactory.newTransformer();

     DOMSource origDocSource = new DOMSource(doc);
     StreamResult origResult = new StreamResult(out);
     transformer.transform(origDocSource, origResult);
}

/**
 * @param doc
 * @param out
 * @throws TransformerException
 */
public static void docToStream(Node doc, java.io.OutputStream out) throws TransformerException{
    TransformerFactory transformerFactory =
             TransformerFactory.newInstance();
     Transformer transformer =
            transformerFactory.newTransformer();

     DOMSource origDocSource = new DOMSource(doc);
     StreamResult origResult = new StreamResult(out);
     transformer.transform(origDocSource, origResult);
}

public static void main(String []args) throws Exception{
	String filename= args[0]; 
	XMLUtils.load(filename, true);
	System.out.println("Parsing successful");
}

}
