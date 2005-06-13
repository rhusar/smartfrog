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
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.GenericAttribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.utils.NodeIterator;

/**
 * Representation of any element. created 21-Apr-2005 14:25:53
 */

public abstract class DocNode implements Names {


    /**
     * owner document. may be null.
     */
    CdlDocument owner;

    protected DocNode() {
    }

    protected DocNode(Element node) throws CdlXmlParsingException {
        bind(node);
    }


    public CdlDocument getOwner() {
        return owner;
    }

    public void setOwner(CdlDocument owner) {
        this.owner = owner;
    }


    /**
     * Get the parse context for this document Nodes without a document dont
     * have one of these.
     *
     * @return the parse context (or null if we dont have one)
     */
    public ParseContext getParseContext() {
        if (owner != null) {
            return owner.getParseContext();
        } else {
            return null;
        }
    }

    /**
     * the node under the system here.
     */
    private Element node;

    /**
     * get the XML node underneath
     *
     * @return
     */
    public Element getNode() {
        return node;
    }

    /**
     * set the node underneath
     *
     * @param node new value; can be null
     */
    public void setNode(Element node) {
        this.node = node;
    }

    /**
     * Iterate just over elements only valid if node!=null
     *
     * @return an iterator
     */
    public NodeIterator children() {
        assert node != null;
        return new NodeIterator(node);

    }

    /**
     * Parse from XML. The base implementation sets the {@link #node} attribute
     *
     * @throws CdlXmlParsingException
     */
    public void bind(Element element) throws CdlXmlParsingException {
        setNode(element);
    }

    /**
     * Test for an element being in the namespace
     *
     * @param e
     * @return true iff we are in the CDL namespace
     * @see #DOC_NAMESPACE
     */
    public static boolean inCdlNamespace(Element e) {
        return DOC_NAMESPACE.equals(e.getNamespaceURI());
    }

    /**
     * Test for an attribute being in the namespace
     *
     * @param a
     * @return true iff we are in the CDL namespace
     * @see #DOC_NAMESPACE
     */
    public static boolean inCdlNamespace(Attribute a) {
        return DOC_NAMESPACE.equals(a.getNamespaceURI());
    }

    public static boolean isNode(Element e, String name) {
        return inCdlNamespace(e) && name.equals(e.getLocalName());
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
        return GenericAttribute.extractCdlAttribute(node,
                attributeName,
                required);
    }


}
