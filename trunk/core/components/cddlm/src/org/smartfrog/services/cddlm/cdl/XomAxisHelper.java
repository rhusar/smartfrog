/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.services.cddlm.cdl;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Element;
import nu.xom.converters.DOMConverter;
import org.apache.axis.message.MessageElement;
import org.apache.axis.AxisFault;
import org.w3c.dom.DOMImplementation;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLAnyNamespace;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLOtherNamespace;
import org.smartfrog.services.cddlm.api.Processor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Iterator;

/**
 * Code to convert from Xom to a message element graph.
 *
 * @see nu.xom.converters.DOMConverter LGPL code from that is in here (also
 *      LGPL) created Sep 13, 2004 3:37:44 PM
 */

public class XomAxisHelper {

    /**
     * make an array from a single element
     *
     * @param element
     * @return
     */
    public static MessageElement[] toArray(MessageElement element) {
        if(element==null) {
            return null;
        }
        MessageElement[] array = new MessageElement[1];
        array[0] = element;
        return array;
    }

    /**
     * turn a list into an array, return null if empty
     * @param list
     * @return
     */
    public static MessageElement[] toArray(List list) {
        if(list==null || list.size()==0) {
            return null;
        }
        MessageElement[] array = new MessageElement[list.size()];
        Iterator it=list.iterator();
        int count=0;
        while ( it.hasNext() ) {
            MessageElement element = (MessageElement) it.next();
            array[count]=element;
            count++;
        }

        return array;
    }

    /**
     * brute force recursive serialisation of a DOM document. Yes, very ugly :(
     *
     * @param elt element to copy
     * @return
     */

    public static MessageElement convert(org.w3c.dom.Element elt) {
        MessageElement dest = new MessageElement(elt);
        return dest;
    }

    /**
     * parse a string, then convert to a message element graph
     *
     * @param string
     * @return
     * @throws SAXException
     * @throws ParsingException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static MessageElement parseAndConvert(String string)
            throws SAXException, ParsingException, IOException,
            ParserConfigurationException {
        Document document = parse(string);
        return convert(document, loadDomImplementation());

    }

    /**
     * non validating parse of a string
     *
     * @param string
     * @return
     * @throws SAXException
     * @throws ParsingException
     * @throws IOException
     */
    private static Document parse(String string) throws SAXException,
            ParsingException, IOException {
        XMLReader xerces = XmlHelper.createXmlParser(false);
        Builder builder = new Builder(xerces, false);
        Document document = builder.build(new StringReader(string));
        return document;
    }


    /**
     * convert a Xom document to message elements
     *
     * @param doc
     * @return
     */
    public static MessageElement convert(Document doc, DOMImplementation impl) {
        org.w3c.dom.Document w3doc = DOMConverter.convert(doc, impl);
        org.w3c.dom.Element documentElement = w3doc.getDocumentElement();
        return convert(documentElement);
    }

    /**
     * convert a Xom document to message elements
     *
     * @param doc
     * @return
     */
    public static MessageElement convert(Document doc)
            throws ParserConfigurationException {
        DOMImplementation impl=loadDomImplementation();
        org.w3c.dom.Document w3doc = DOMConverter.convert(doc, impl);
        org.w3c.dom.Element documentElement = w3doc.getDocumentElement();
        return convert(documentElement);
    }

    /**
     * convert a Xom element to message elements
     *
     * @param elt
     * @return
     */
    public static MessageElement convert(Element elt)
            throws ParserConfigurationException {
        Document doc=new Document(elt);
        return convert(doc);
    }

    

    /**
     * use the JAXP APIs to locate and bind to a parser
     *
     * @return
     * @throws ParserConfigurationException
     */
    public static DOMImplementation loadDomImplementation()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        return impl;
    }

    /**
     * parse a lump of XML
     *
     * @param xmlWrapper
     * @param elementNameForFaults
     * @return
     * @throws AxisFault
     */
    public static Element parse(UnboundedXMLOtherNamespace xmlWrapper,
            String elementNameForFaults) throws AxisFault {
        if (xmlWrapper == null) {
            throw Processor.raiseBadArgumentFault("Missing " +
                    elementNameForFaults);
        }

        MessageElement mes[] = xmlWrapper.get_any();
        return parse(mes, elementNameForFaults);
    }

    /**
     * parse a lump of XML
     * @param xmlWrapper
     * @param elementNameForFaults
     * @return
     * @throws AxisFault
     */
    public static Element parse(UnboundedXMLAnyNamespace xmlWrapper,
            String elementNameForFaults) throws AxisFault {
        if(xmlWrapper==null) {
            throw Processor.raiseBadArgumentFault("Missing "+elementNameForFaults);
        }

        MessageElement mes[]=xmlWrapper.get_any();
        return parse(mes, elementNameForFaults);
    }

    /**
     * parse a lump of XML
     * @param mes
     * @param elementNameForFaults
     * @return
     * @throws AxisFault
     */
    public static Element parse(MessageElement[] mes,
            String elementNameForFaults) throws AxisFault {
        if(mes.length==0) {
            throw Processor.raiseBadArgumentFault("No XML in  " + elementNameForFaults);
        }
        if (mes.length > 1) {
            throw Processor.raiseBadArgumentFault("Too much XML in  " +
                    elementNameForFaults);
        }
        MessageElement me=mes[0];
        try {
            return parse(me.getAsString()).getRootElement();
        } catch (Exception innerFault) {
            throw new AxisFault("Could not parse XML in "+elementNameForFaults,
                    innerFault);
        }
    }
}
