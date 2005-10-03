/*
* Copyright (c) 2005, The Regents of the University of California, through
* Lawrence Berkeley National Laboratory (subject to receipt of any required
* approvals from the U.S. Dept. of Energy). All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* (1) Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 
* (2) Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
* 
* (3) Neither the name of the University of California, Lawrence Berkeley
* National Laboratory, U.S. Dept. of Energy nor the names of its contributors
* may be used to endorse or promote products derived from this software without
* specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
* 
* You are under no obligation whatsoever to provide any bug fixes, patches, or
* upgrades to the features, functionality or performance of the source code
* ("Enhancements") to anyone; however, if you choose to make your Enhancements
* available either publicly, or directly to Lawrence Berkeley National
* Laboratory, without imposing a separate written license agreement for such
* Enhancements, then you hereby grant the following license: a non-exclusive,
* royalty-free perpetual license to install, use, modify, prepare derivative
* works, incorporate into other computer software, distribute, and sublicense
* such enhancements or derivative works thereof, in binary and source code
* form.
*/
package org.smartfrog.services.deployapi.binding;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalAddException;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.WellformednessException;
import nu.xom.XMLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Early EXPERIMENTAL XOM interface to Stax. Probably still buggy and
 * incomplete. In particular, needs more work on DTD, entities, external
 * references.
 * <p/>
 * Also might want to expose more public methods, and/or add some parameters.
 *
 * @author whoschek.AT.lbl.DOT.gov
 */
public class NuxStaxBuilder {

    private static Log log= LogFactory.getLog(NuxStaxBuilder.class);
    
    private final NodeFactory factory;

    private final XMLInputFactory staxFactory;

    private static final HashMap attrTypes = createAttributeTypes();

    private static final Nodes NONE = new Nodes();

    public static final String ERROR_NULL_INPUT_STREAM = "input stream must not be null";
    public static final String ERROR_NULL_READER = "reader must not be null";


    public NuxStaxBuilder() {
        this(new NodeFactory());
    }

    public NuxStaxBuilder(NodeFactory factory) {
        this(factory, createStaxFactory());
    }

    // TODO: make this public?
    private NuxStaxBuilder(NodeFactory factory, XMLInputFactory staxFactory) {
        if (staxFactory == null) {
            throw new IllegalArgumentException("staxFactory must not be null");
        }
        if (factory == null) {
            factory = new NodeFactory();
        }
        this.factory = factory;
        this.staxFactory = staxFactory;
//          log.debug("stax factory=" + staxFactory.getClass().getName());
    }

    private static XMLInputFactory createStaxFactory() {
        XMLInputFactory staxFactory = XMLInputFactory.newInstance();
        staxFactory.setProperty("javax.xml.stream.isNamespaceAware", "true");
        try {
            staxFactory.setProperty("javax.xml.stream.isCoalescing", "true");
        } catch (IllegalArgumentException e) {
            /*
                * the property may or may not be honoured. maybe we should write
                * our own impl for coalescing of adjacent Texts, to be safe and
                * clean?
                */
            ; // optional; for the moment we can live with that
        }

        // TODO: try using underlying Stax impls in some order of preference? if so which order? (Woodstox, Sun, BEA)
//		System.setProperty("javax.xml.stream.XMLInputFactory", "com.sun.xml.stream.ZephyrParserFactory");
//		System.setProperty("javax.xml.stream.XMLOutputFactory", "com.sun.xml.stream.ZephyrWriterFactory");
//		System.setProperty("javax.xml.stream.XMLEventFactory", "com.sun.xml.stream.events.ZephyrEventFactory");
        return staxFactory;
    }

    // TODO: add baseURI parameter?
    public Document build(InputStream input) throws XMLStreamException {
        if (input == null) {
            throw new IllegalArgumentException(ERROR_NULL_INPUT_STREAM);
        }

        return build(staxFactory.createXMLStreamReader(input));
    }

    public Document build(XMLStreamReader reader) throws XMLStreamException {
        if (reader == null) {
            throw new IllegalArgumentException(ERROR_NULL_READER);
        }

        Document doc = factory.startMakingDocument();
        boolean hasRootElement = false;
        int i = 0;

        while (reader.next() != XMLStreamConstants.END_DOCUMENT) {
            Nodes nodes = readDocumentChild(reader, doc);
            for (int j = 0; j < nodes.size(); j++) {
                Node node = nodes.get(j);
                if (node instanceof Element) { // replace fake root with real root
                    if (hasRootElement && node != doc.getRootElement()) {
                        throw new IllegalAddException(
                                "Factory returned multiple root elements");
                    }
                    doc.setRootElement((Element) node);
                    hasRootElement = true;
                } else {
                    doc.insertChild(node, i);
                }
                i++;
            }
        }

        if (!hasRootElement) {
            throw new WellformednessException(
                    "Factory attempted to remove the root element");
        }
        factory.finishMakingDocument(doc);
        return doc;
    }

    private Nodes readDocumentChild(XMLStreamReader reader, Document doc)
            throws XMLStreamException {

        switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT: {
                Element root = readElementStart(reader, true);
                if (root == null) {
                    throw new NullPointerException(
                            "Factory failed to create root element.");
                }
                doc.setRootElement(root);
                addNamespaceDeclarations(reader, root);
                addAttributes(reader, root);
                readElement(root, reader);
                return factory.finishMakingElement(root);
            }
            case XMLStreamConstants.END_ELEMENT:
                throw new WellformednessException("Multiple root elements");
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                return factory.makeProcessingInstruction(
                        reader.getPITarget(), reader.getPIData());
            case XMLStreamConstants.CHARACTERS:
                return NONE; // ignore text in prolog/epilog
            case XMLStreamConstants.COMMENT:
                return factory.makeComment(reader.getText());
            case XMLStreamConstants.SPACE:
                return NONE; // ignore text in prolog/epilog
            case XMLStreamConstants.START_DOCUMENT:
                return NONE; // has already been handled previously
            case XMLStreamConstants.END_DOCUMENT:
                throw new IllegalStateException("unreachable");
            case XMLStreamConstants.CDATA:
                return NONE; // ignore text in prolog/epilog
            case XMLStreamConstants.ATTRIBUTE:
                throw new WellformednessException(
                        "Illegal attribute in prolog/epilog");
            case XMLStreamConstants.NAMESPACE:
                throw new WellformednessException(
                        "Illegal namespace declaration in prolog/epilog");
            case XMLStreamConstants.DTD:
                return NONE; // FIXME
            case XMLStreamConstants.ENTITY_DECLARATION:
                return NONE; // FIXME
            case XMLStreamConstants.NOTATION_DECLARATION:
                return NONE; // FIXME
            case XMLStreamConstants.ENTITY_REFERENCE:
                return NONE; // FIXME
            default:
                throw new XMLException("Unrecognized Stax event type: "
                        + reader.getEventType());
        }
    }

    /** Iterative pull parser reading an entire element subtree. */
    private void readElement(Element current, XMLStreamReader reader)
            throws XMLStreamException {

        final ArrayList stack = new ArrayList();
        stack.add(current);

        while (true) {
            Nodes nodes = null;
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    Element elem = readElementStart(reader, false);
                    stack.add(elem); // even if it's null
                    if (elem != null) {
                        current.appendChild(elem);
                        addNamespaceDeclarations(reader, elem);
                        addAttributes(reader, elem);
                        current = elem; // recurse down
                    }
                    continue;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    Element elem = (Element) stack.remove(stack.size() - 1);
                    if (elem == null) {
                        continue; // skip element
                    }
                    ParentNode parent = elem.getParent();
                    if (parent == null) {
                        throwTamperedWithParent();
                    }
                    if (parent instanceof Document) {
                        return; // we're done with the root element
                    }

                    current = (Element) parent; // recurse up
                    nodes = factory.finishMakingElement(elem);

                    if (nodes.size() == 1 &&
                            nodes.get(0) == elem) { // same node? (common case)
                        continue; // optimization: no need to remove and then readd same element
                    }

                    if (current.getChildCount() - 1 <
                            0) {
                        throwTamperedWithParent();
                    }
                    current.removeChild(current.getChildCount() - 1);
                    break;
                }
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    nodes = factory.makeProcessingInstruction(
                            reader.getPITarget(), reader.getPIData());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    nodes = factory.makeText(reader.getText());
                    break;
                case XMLStreamConstants.COMMENT:
                    nodes = factory.makeComment(reader.getText());
                    break;
                case XMLStreamConstants.SPACE:
                    nodes = factory.makeText(reader.getText());
                    break;
                case XMLStreamConstants.CDATA:
                    nodes = factory.makeText(reader.getText());
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    nodes = factory.makeText(reader.getText());
                default:
                    throw new XMLException("Unrecognized Stax event type: "
                            + reader.getEventType());
            }

            appendNodes(current, nodes);
        }
    }

    private Element readElementStart(XMLStreamReader reader, boolean isRoot) {
        String prefix = reader.getPrefix();
        String qname = reader.getLocalName();
        String namespaceURI = reader.getNamespaceURI();
        if (namespaceURI != null) {
            if (prefix != null && prefix.length() > 0) {
                qname = prefix + ":" + qname;
            }
        }

        return isRoot ?
                factory.makeRootElement(qname, namespaceURI) :
                factory.startMakingElement(qname, namespaceURI);
    }

    private static void appendNodes(Element elem, Nodes nodes) {
        if (nodes != null) {
            int size = nodes.size();
            for (int i = 0; i < size; i++) {
                Node node = nodes.get(i);
                if (node instanceof Attribute) {
                    elem.addAttribute((Attribute) node);
                } else {
                    elem.insertChild(node, elem.getChildCount());
                }
            }
        }
    }

    private static void throwTamperedWithParent() {
        throw new XMLException("Factory has tampered with a parent pointer " +
                "of ancestor-or-self in finishMakingElement()");
    }

    private void addNamespaceDeclarations(XMLStreamReader reader,
                                          Element elem) {
        int count = reader.getNamespaceCount();
        for (int i = 0; i < count; i++) {
            String prefix = reader.getNamespacePrefix(i);
            if (prefix == null) {
                prefix = "";
            }

            String uriInScope = elem.getNamespaceURI(prefix);
            String uri = reader.getNamespaceURI(i);
            if (uriInScope == null || !uriInScope.equals(uri)) {
                elem.addNamespaceDeclaration(prefix, uri);
            }
        }
    }

    private void addAttributes(XMLStreamReader reader, Element elem) {
        int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String prefix = reader.getAttributePrefix(i);
            String qname = reader.getAttributeLocalName(i);
            if (prefix != null && prefix.length() > 0) {
                qname = prefix + ":" + qname;
            }
            String namespaceURI = reader.getAttributeNamespace(i);
            String value = reader.getAttributeValue(i);
            Attribute.Type type = convertAttributeType(reader.getAttributeType(i));

            appendNodes(elem,
                    factory.makeAttribute(qname, namespaceURI, value, type));
        }
    }

    private static Attribute.Type convertAttributeType(String staxType) {
        if (staxType != null && staxType.length() > 0) {
            Attribute.Type xomType = (Attribute.Type) attrTypes.get(staxType);
            if (xomType != null) {
                return xomType;
            }
        }
        return Attribute.Type.UNDECLARED;
    }

    private static HashMap createAttributeTypes() {
        HashMap typeMappings = new HashMap();
        typeMappings.put("CDATA", Attribute.Type.CDATA);
        typeMappings.put("cdata", Attribute.Type.CDATA);
        typeMappings.put("ID", Attribute.Type.ID);
        typeMappings.put("id", Attribute.Type.ID);
        typeMappings.put("IDREF", Attribute.Type.IDREF);
        typeMappings.put("idref", Attribute.Type.IDREF);
        typeMappings.put("IDREFS", Attribute.Type.IDREFS);
        typeMappings.put("idrefs", Attribute.Type.IDREFS);
        typeMappings.put("ENTITY", Attribute.Type.ENTITY);
        typeMappings.put("entity", Attribute.Type.ENTITY);
        typeMappings.put("ENTITIES", Attribute.Type.ENTITIES);
        typeMappings.put("entities", Attribute.Type.ENTITIES);
        typeMappings.put("NMTOKEN", Attribute.Type.NMTOKEN);
        typeMappings.put("nmtoken", Attribute.Type.NMTOKEN);
        typeMappings.put("NMTOKENS", Attribute.Type.NMTOKENS);
        typeMappings.put("nmtokens", Attribute.Type.NMTOKENS);
        typeMappings.put("NOTATION", Attribute.Type.NOTATION);
        typeMappings.put("notation", Attribute.Type.NOTATION);
        typeMappings.put("ENUMERATED", Attribute.Type.ENUMERATION);
        typeMappings.put("enumerated", Attribute.Type.ENUMERATION);
        return typeMappings;
    }

}

