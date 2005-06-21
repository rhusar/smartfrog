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

import nu.xom.Element;
import nu.xom.Node;
import org.smartfrog.sfcore.languages.cdl.utils.NodeIterator;
import org.smartfrog.sfcore.languages.cdl.utils.XmlUtils;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

import javax.xml.namespace.QName;

/**
 * Extended element with a backpointer to the element
 */
public class ElementEx extends Element {


    public ElementEx(String name) {
        super(name);
    }

    public ElementEx(String name, String uri) {
        super(name, uri);
    }

    public ElementEx(Element element) {
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
        return new ElementEx(getQualifiedName(), getNamespaceURI());
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
     * Parse from XML.
     * The base implementation binds all children
     *
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException
     */
    public void bind() throws CdlXmlParsingException {
        //recurse through children, binding them
        for(Node child:nodes()) {
            if(child instanceof ElementEx) {
                ElementEx ex=(ElementEx) child;
                ex.bind();
            }
        }
    }

    /**
     * Get the QName of an element
     * @return
     */
    public QName getQName() {
        return XmlUtils.makeQName(this);
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
}
