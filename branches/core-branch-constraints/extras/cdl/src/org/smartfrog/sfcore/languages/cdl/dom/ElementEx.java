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
import nu.xom.Node;
import nu.xom.Text;
import org.smartfrog.services.xml.java5.NamespaceUtils;
import org.smartfrog.services.xml.java5.iterators.AttributeIterator;
import org.smartfrog.services.xml.java5.iterators.NodeIterator;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.utils.NamespaceLookup;
import org.smartfrog.sfcore.languages.cdl.utils.Namespaces;

import javax.xml.namespace.QName;
import java.util.Iterator;

/**
 * Extended element with a backpointer to the element
 */
public class ElementEx extends Element implements Iterable<Node>, NamespaceLookup {
    public static final String ERROR_NON_RESOLVABLE_QNAME_PREFIX = "No namespace defined for [";


    public ElementEx(String name) {
        super(name);
    }

    public ElementEx(String name, String uri) {
        super(name, uri);
    }

    public ElementEx(Element element) {
        super(element);
    }

    public ElementEx(QName name) {
        super((name.getPrefix().length() > 0 ?
                (name.getPrefix() + ":")
                : "")
                + name.getLocalPart(), name.getNamespaceURI());
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
        return new ElementEx(getQualifiedName(), getNamespaceURI());
    }


    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<Node> iterator() {
        return nodes();
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
     * @return
     */
    public AttributeIterator attributes() {
        return new AttributeIterator(this);
    }

    /**
     * Parse from XML. The base implementation binds all children
     *
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException
     *
     */
    public void bind() throws CdlXmlParsingException {
        //recurse through children, binding them
        for (Node child : this) {
            if (child instanceof ElementEx) {
                ElementEx ex = (ElementEx) child;
                ex.bind();
            }
        }
    }

    /**
     * Get the QName of an element
     *
     * @return
     */
    public QName getQName() {
        return NamespaceUtils.makeQName(this);
    }

    /**
     * Test for a propertylist instance name
     *
     * @param testName
     * @return
     */
    public boolean isNamed(QName testName) {
        return getLocalName().equals(testName.getLocalPart()) &&
                getNamespaceURI().equals(testName.getNamespaceURI());
    }

    /**
     * Add a new attribute in the given namespace
     *
     * @param namespace namespace URI
     * @param localname localname
     * @param value     value
     */
    public void addNewAttribute(String namespace, String localname, String value) {
        Attribute attr = new Attribute(localname, namespace, value);
        addAttribute(attr);
    }

    /**
     * Add a new attribute
     *
     * @param name  attribute QName
     * @param value attribute value
     */
    public void addNewAttribute(QName name, String value) {
        String local;
        if (name.getPrefix().length() > 0) {
            local = name.getPrefix() + ':' + name.getLocalPart();
        } else {
            local = name.getLocalPart();
        }
        Attribute attr = new Attribute(local, name.getNamespaceURI(), value);
        addAttribute(attr);
    }



    /**
     * Get the immediate text value of an element. That is -the concatenation
     * of all direct child text elements. This string is not trimmed.
     *
     * @return a next string, which will be empty "" if there is no text
     */
    public String getTextValue() {
        StringBuilder builder = new StringBuilder();
        for (Node n : this) {
            if (n instanceof Text) {
                Text text = (Text) n;
                builder.append(text.getValue());
            }
        }
        return builder.toString();
    }

    /**
     * turn a qname string into a QName value, resolving prefixes relative to here
     *
     * @param qname
     * @return a qname from the element
     * @throws IllegalArgumentException if the prefix would not resolve
     */
    public QName resolveQName(String qname) {
        String prefix;
        String namespace;
        String localname;

        localname = NamespaceUtils.extractLocalname(qname);
        prefix = NamespaceUtils.extractNamespacePrefix(qname);
        if (prefix != null) {
            namespace = getNamespaceURI(prefix);
            if (namespace == null) {
                //this is an error.
                throw new IllegalArgumentException(ERROR_NON_RESOLVABLE_QNAME_PREFIX + prefix + "]");
            }
            return new QName(namespace, localname, prefix);
        } else {
            return new QName(localname);
        }

    }

    /**
     * Test for having one or more child elements
     *
     * @return true iff there is at least one child element
     */
    public boolean hasChildElements() {
        for (Node n : this) {
            if (n instanceof Element) {
                return true;
            }
        }
        return false;
    }


    /**
     * Get the URI of a namespace
     *
     * @param prefix the prefix
     * @return the URI or null for none.
     */
    public String resolveNamespaceURI(String prefix) {
        return getNamespaceURI(prefix);
    }

    /**
     * Get a static cache of all namespaces currently in scope
     *
     * @return a map of prefixes to namespaces
     */
    public Namespaces getNamespaces() {
        return new Namespaces(this);
    }
}
