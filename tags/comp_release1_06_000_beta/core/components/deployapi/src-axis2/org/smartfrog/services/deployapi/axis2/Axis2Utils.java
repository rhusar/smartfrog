/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.axis2;

import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMFactory;
import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.om.impl.builder.StAXOMBuilder;
import org.smartfrog.services.deployapi.axis2.binding.NuxStaxBuilder;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.system.Utils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLInputFactory;

import nu.xom.Document;
import nu.xom.Element;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

/**
 * created 24-Apr-2006 13:24:46
 */

public class Axis2Utils {
    /**
     * Create an OMElement from the QName
     * @param qname element qname
     * @return
     */
    public static OMElement createOmElement(QName qname) {
        return createOmElement(
                qname.getNamespaceURI(), qname.getLocalPart(),qname.getPrefix());
    }

    /**
     * Create an om element from the infividual elements of the qname.
     * @param namespace
     * @param local
     * @param prefix
     * @return
     */
    public static OMElement createOmElement(String namespace,String local,String prefix) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement element = factory.createOMElement(local,namespace, prefix);
        return element;
    }

    /**
     * convert from an axiom graph to Xom.
     * Very efficient as it uses the StAX stuff underneath
     * @param em
     * @return
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException if needed
     */
    public static Document axiomToXom(OMElement em)  {
        try {
            Document document;
            XMLStreamReader reader = em.getXMLStreamReader();
            NuxStaxBuilder builder=new NuxStaxBuilder();
            document = builder.build(reader);
            return document;
        } catch (XMLStreamException e) {
            throw FaultRaiser.raiseInternalError("converting object models",e);
        }
    }

    /**
     * Convert a Xom graph to Axiom. The Element is detached from its parents and placed
     * at the base of a new doc in the process; this is not a zero-side-effect operation
     * @param element
     * @return an Axom equivalent
     */
    public static OMElement xomToAxiom(Element element)  {
        element.detach();
        Document document=new Document(element);
        return xomToAxiom(document);
    }

    /**
     *
     * @param document
     * @return
     */
    public static OMElement xomToAxiom(Document document) {
        try {
            byte[] buffer = XomHelper.xomToBuffer(document);
            return loadAxiomFromBuffer(buffer);
        } catch (IOException e) {
            throw FaultRaiser.raiseInternalError("doc conversion error",e);
        }
    }

    public static OMElement loadAxiomFromBuffer(byte[] buffer) {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader parser = inputFactory.createXMLStreamReader(in);
            return parseAxiomDoc(parser);
        } catch (XMLStreamException e) {
            throw FaultRaiser.raiseInternalError("Parse failure",e);
        } finally {
            Utils.close(in);
        }
    }

    public static OMElement loadAxiomFromString(String textForm) {
        try {
            StringReader stringReader = new StringReader(textForm);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader parser = inputFactory.createXMLStreamReader(stringReader);
            return parseAxiomDoc(parser);
        } catch (XMLStreamException e) {
            throw FaultRaiser.raiseInternalError("Parse failure:"+
            textForm, e);
        }
    }

    private static OMElement parseAxiomDoc(XMLStreamReader parser) {
        StAXOMBuilder builder = new StAXOMBuilder(parser);
        return builder.getDocumentElement();
    }
}
