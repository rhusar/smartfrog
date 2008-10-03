/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Attribute;
import nu.xom.Element;
import org.smartfrog.services.xml.java5.iterators.AttributeIterator;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.GenericAttribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

/**
 * Representation of any element. created 21-Apr-2005 14:25:53 The node stored
 * inside may be of Element, ElementEx or some subclass.
 */

public abstract class DocNode extends ElementEx implements Names {

    protected DocNode(String name) {
        super(name);
    }

    protected DocNode(String name, String uri) {
        super(name, uri);
    }

    protected DocNode(Element element) {
        super(element);
    }


    public DocumentNode getDocumentNode() {
        return (DocumentNode) getDocument();
    }

    /**
     * backtrack up the to get our owner. Null if there is none at this point.
     * @return owner document or null for no owner
     */
    public CdlDocument getOwner() {
        DocumentNode documentNode = getDocumentNode();
        return documentNode!=null?documentNode.getOwner():null;
    }


    /**
     * Get the parse context for this document Nodes without a document dont
     * have one of these.
     *
     * @return the parse context (or null if we dont have one)
     */
    public ParseContext getParseContext() {
        if (getOwner() != null) {
            return getOwner().getParseContext();
        } else {
            return null;
        }
    }


    /**
     * get the XML node underneath
     *
     * @return
     * @deprecated
     */
    public ElementEx getNode() {
        return this;
    }


    /**
     * Test for an element being in the namespace
     *
     * @param e
     * @return true iff we are in the CDL namespace
     * @see #CDL_NAMESPACE
     */
    public static boolean inCdlNamespace(Element e) {
        return inCdlNamespace(e.getNamespaceURI());
    }

    public static boolean inCdlNamespace(String namespace) {
        return CDL_NAMESPACE.equals(namespace);
    }

    /**
     * Test for an attribute being in the namespace
     *
     * @param a
     * @return true iff we are in the CDL namespace
     * @see #CDL_NAMESPACE
     */
    public static boolean inCdlNamespace(Attribute a) {
        return inCdlNamespace(a.getNamespaceURI());
    }

    public static boolean isNode(Element e, String name) {
        return inCdlNamespace(e) && name.equals(e.getLocalName());
    }

    /**
     * check that a (namespace,location) tuple refers to something in the CDL
     * namespace with an expected name
     *
     * @param namespace
     * @param localname
     * @param expected
     * @return true for a match
     */
    public static boolean isNode(String namespace,
            String localname,
            String expected) {
        return inCdlNamespace(namespace) && expected.equals(localname);
    }

    /**
     * Iterate over the attributes
     *
     * @return a new iterator that is also iterable
     */
    public AttributeIterator attributes() {
        return new AttributeIterator(this);
    }

    /**
     * Get an attribute in the CDL namespace
     *
     * @param attributeName attribute to get
     * @param required      flag set to true if needed
     * @return the string value of the attribute
     * @throws CdlXmlParsingException
     */
    public Attribute extractCdlAttribute(String attributeName,
            boolean required)
            throws CdlXmlParsingException {
        return GenericAttribute.extractCdlAttribute(this,
                attributeName,
                required);
    }


    /**
     * Test for an elemeent having a child
     *
     * @param namespace
     * @param local
     * @return
     */
    public boolean hasAttribute(String namespace, String local) {
        return getAttribute(local, namespace) != null;
    }


}
