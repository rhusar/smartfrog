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

import org.apache.axis.message.MessageElement;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.DocType;
import nu.xom.Node;
import nu.xom.converters.DOMConverter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Code to convert from Xom to a message element graph.
 * @see nu.xom.converters.DOMConverter
 * LGPL code from that is in here (also LGPL)
 * created Sep 13, 2004 3:37:44 PM
 */

public class XomAxisHelper {

    /**
     * brute force recursive serialisation of a DOM document.
     * Yes, very ugly :(
     * @param elt element to copy
     * @return
     */

    public static MessageElement convert(org.w3c.dom.Element elt) {
        MessageElement dest=new MessageElement(elt);
        return dest;
    }



    /**
     * convert a Xom document to message elements
     * @param doc
     * @return
     */
    public static MessageElement convert(Document doc, DOMImplementation impl) {
        org.w3c.dom.Document w3doc = DOMConverter.convert(doc, impl);
        org.w3c.dom.Element documentElement = w3doc.getDocumentElement();
        return convert(documentElement);
    }

    /**
     * use the JAXP APIs to locate and bind to a parser
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


}
