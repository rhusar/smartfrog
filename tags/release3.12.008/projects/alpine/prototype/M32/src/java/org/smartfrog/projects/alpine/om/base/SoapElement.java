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
package org.smartfrog.projects.alpine.om.base;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;
import org.smartfrog.projects.alpine.interfaces.ValidateXml;
import org.smartfrog.projects.alpine.xmlutils.AttributeIterator;
import org.smartfrog.projects.alpine.xmlutils.BaseElementsIterator;
import org.smartfrog.projects.alpine.xmlutils.NodeIterator;
import org.smartfrog.projects.alpine.xmlutils.NodesIterator;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;

import javax.xml.namespace.QName;

/**
 * Extended element with a backpointer to the element
 */
public class SoapElement extends Element implements ValidateXml, SoapConstants {

    /**
     * The prefix to use when creating a qname that has none.
     * {@value}
     */
    protected static final String PREFIX = "prefix";


    public SoapElement(String name) {
        super(name);
    }

    public SoapElement(QName qname) {
        super(qname.getLocalPart(), qname.getNamespaceURI());
    }

    /**
     * Create an element with the text contents. If the text parameter is
     * null, nothing is added
     *
     * @param qname name of the element
     * @param text  optional text to add
     */
    public SoapElement(QName qname, String text) {
        super(qname.getLocalPart(), qname.getNamespaceURI());
        if (text != null) {
            appendChild(text);
        }
    }

    public SoapElement(QName qname, Node child) {
        super(qname.getLocalPart(), qname.getNamespaceURI());
        appendChild(child);
    }

    public SoapElement(String name, String uri) {
        super(name, uri);
    }

    public SoapElement(String name, String uri, String text) {
        super(name, uri);
        appendChild(text);
    }

    public SoapElement(Element element) {
        super(element);
    }

    /**
     * <p/>
     * Creates a very shallow copy of the element with the same name and
     * namespace URI, but no children, attributes, base URI, or namespace
     * declaration. This method is invoked as necessary by the {@link
     * nu.xom.Element#copy() copy} method and the {@link
     * nu.xom.Element#Element(nu.xom.Element) copy constructor}. </p>
     * <p/>
     * <p/>
     * Subclasses should override this method so that it returns an instance of
     * the subclass so that types are preserved when copying. This method should
     * not add any attributes, namespace declarations, or children to the
     * shallow copy. Any such items will be overwritten. </p>
     *
     * @return an empty element with the same name and namespace as this
     *         element
     */
    protected Element shallowCopy() {
        return new SoapElement(getQualifiedName(), getNamespaceURI());
    }

    /**
     * Iterate just over elements
     *
     * @return an iterator
     */
    public NodeIterator nodes() {
        return new NodeIterator(this);
    }

    /**
     * get our attributes
     *
     * @return an attribute iterator
     */
    public AttributeIterator attributes() {
        return new AttributeIterator(this);
    }


    /**
     * Get the child elements
     *
     * @return an iterator over all child elements
     */
    public BaseElementsIterator<Element> elements() {
        Elements childElements = getChildElements();
        return new BaseElementsIterator<Element>(childElements);
    }

    /**
     * Get the child elements of the given name
     *
     * @param name name of the elements
     * @return iterator over the elements
     */
    public BaseElementsIterator<Element> elements(QName name) {
        return XsdUtils.elements(this, name);
    }

    /**
     * Get the child elements in a given namespace
     *
     * @param namespace name of the elements
     * @return an iterator over all elements in the namespace
     */
    public BaseElementsIterator<Element> elements(String namespace) {
        return XsdUtils.elements(this,namespace);
    }

    /**
     * Get the QName of an element
     *
     * @return the element's qualified name
     */
    public QName getQName() {
        return XsdUtils.makeQName(this);
    }

    /**
     * Extract the first child element of this node
     *
     * @return the element or null for no child elements
     * @see XsdUtils#getFirstChildElement(nu.xom.Element)
     */
    public final Element getFirstChildElement() {
        return XsdUtils.getFirstChildElement(this);
    }

    /**
     * <p/>
     * Returns the first child element with the specified nqame or null
     * null. </p>
     *
     * @param name the name of the element to return
     * @return the first child element with the specified name or null if there is no such
     *         element
     */
    public final Element getFirstChildElement(QName name) {
        return getFirstChildElement(name.getLocalPart(), name.getNamespaceURI());
    }

    /**
     * <p/>
     * Returns the first child element with the specified nqame or null null. </p>
     *
     * @param name the name of the element to return
     * @return the first child element with the specified name or null if there is no such element
     */
    public final Elements getChildElements(QName name) {
        return getChildElements(name.getLocalPart(), name.getNamespaceURI());
    }

    /**
     * Test for an element having a full name matching the qname
     *
     * @param testName the name to look for
     * @return true iff local name and namespace URIs match.
     */
    public boolean isNamed(QName testName) {
        return XsdUtils.isNamed(this, testName);
    }


    /**
     * Get the immediate text value of an element. That is -the concatenation
     * of all direct child text elements. This string is not trimmed.
     *
     * @return a next string, which will be empty "" if there is no text
     */
    public String getTextValue() {
        return XsdUtils.getTextValue(this);
    }

    /**
     * Validate the Xml. Throw {@link InvalidXmlException} if invalid.
     */
    public void validateXml() {
        for (Node child : nodes()) {
            if (child instanceof ValidateXml) {
                ValidateXml validation = (ValidateXml) child;
                validation.validateXml();
            }
        }
    }

    /**
     * Apply an XPath query to a node
     *
     * @param path    xpath query
     * @param context context for prefix evaluation
     * @return an iterator over all nodes that match the path
     */
    public NodesIterator xpath(String path, XPathContext context) {
        return XsdUtils.xpath(this, path, context);
    }

    /**
     * Apply an XPath query to a node
     *
     * @param path xpath query
     * @return an iterator over all nodes that match the path
     */
    public NodesIterator xpath(String path) {
        Nodes nodes = query(path);
        NodesIterator it = new NodesIterator(nodes);
        return it;
    }

    /**
     * Remove a child element; do nothing if it is absent
     *
     * @param name qname of the element
     * @return the detached element or null for no match
     */
    public Element removeChildElement(QName name) {
        Element child = getFirstChildElement(name);
        if (child != null) {
            removeChild(child);
            return child;
        }
        return null;
    }

    /**
     * Add an element, or, if it exists, replace it with the new one
     *
     * @param newElement the new element
     * @return any old element, or null if there was none
     */
    public Element addOrReplaceChild(Element newElement) {
        Element child = getFirstChildElement(newElement.getLocalName(), newElement.getNamespaceURI());
        if (child != null) {
            replaceChild(child, newElement);
            return child;
        } else {
            appendChild(newElement);
            return null;
        }
    }

    /**
     * copy all the child elements and attributes from a node
     *
     * @param that the source of the data
     */
    public void copyChildrenFrom(Element that) {
        for (Attribute a : new AttributeIterator(that)) {
            appendChild(a.copy());
        }
        for (Node n : new NodeIterator(that)) {
            appendChild(n.copy());
        }
    }

    /**
     * Attach a qname to the text of a node, setting up namespaces
     * If the qname has no prefix, we make one up with the value of
     * {@link #PREFIX}
     * @param qname qname to append to the text of the element
     */
    public void appendQName(QName qname) {
        String prefix = qname.getPrefix();
        if(prefix.length()==0) {
            //make up a prefix
            prefix=PREFIX;
        }
        addNamespaceDeclaration(prefix, qname.getNamespaceURI());
        appendChild(prefix + ":" + qname.getLocalPart());

    }

    /**
     * Add a new namespace declaration if it is not there
     * @param prefix the prefix
     * @param xmlns the namespace
     */
    public void addNewNamespace(String prefix, String xmlns) {
        if(getNamespaceURI(prefix)==null) {
            addNamespaceDeclaration(prefix,xmlns);
        }
    }

    /**
     * Test for the node being in the SOAP1.1 namespace
     * @return true iff this is a SOAP1.1 node
     */
    public boolean isSoap11() {
        return URI_SOAP11.equals(getNamespaceURI());
    }

    /**
     * Test for the node being in the SOAP1.2 namespace
     * @return true iff this is a SOAP1.2 node
     */
    public boolean isSoap12() {
        return URI_SOAP12.equals(getNamespaceURI());
    }

}
