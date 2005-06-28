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
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.generate.GenerateContext;
import org.smartfrog.sfcore.languages.cdl.generate.ToSmartFrog;
import org.smartfrog.sfcore.languages.cdl.utils.AttributeIterator;
import org.smartfrog.sfcore.languages.cdl.utils.NodeIterator;
import org.smartfrog.sfcore.languages.cdl.utils.XmlUtils;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * Extended element with a backpointer to the element
 */
public class ElementEx extends Element implements ToSmartFrog {


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
        for (Node child : nodes()) {
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

    /**
     * Write something to a smartfrog file. Parent elements should delegate to
     * their children as appropriate.
     * <p/>
     * The Base class delegates to children and otherwise does nothing
     *
     * @param out
     * @throws java.io.IOException
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     */
    public void toSmartFrog(GenerateContext out) throws IOException,
            CdlException {
        //printNodeAsSFComment(out);
        //printAttributesToSmartFrog(out);
        printChildrenToSmartFrog(out);
    }

    /**
     * print our node value as a comment
     */
    protected void printNodeAsSFComment(GenerateContext out) {
        out.commentln(getQualifiedName());
        String value = getValue();
        if (value != null && value.length() > 0) {
            String v = value.replace("\n", " ");
            out.commentln("value:" + v);
        }
    }

    /**
     * get the value of anode, print its value
     * @param out
     * @param key
     * @param includeEmptyStrings
     * @param trim
     */
    protected void printValueToSF(GenerateContext out,
            String key,
            boolean includeEmptyStrings,
            boolean trim) {

        String value = getTextValue();
        if(trim) {
            value=value.trim();
        }
        if(value.length()==0 && !includeEmptyStrings) {
            return;
        }
        //replace the # statement with the 0x23, decimal 35 value
        value = value.replace("#", "\u0023");
        out.printTuple(key,value);
    }


    protected void printValueToSF(GenerateContext out) {
        printValueToSF(out,"value",false,true);
    }



    /**
     * print out all the children to smartfrog
     *
     * @param out
     * @throws IOException
     * @throws CdlException
     */
    public void printChildrenToSmartFrog(GenerateContext out)
            throws IOException,
            CdlException {
        for (Node node : nodes()) {
            if (node instanceof ToSmartFrog) {
                ToSmartFrog sfwriter = (ToSmartFrog) node;
                sfwriter.toSmartFrog(out);
            }
        }
    }

    /**
     * print out our attribute as name "value"; pairs, with a leading underscore
     * on each. If the local namespace matches that of us, no namespace info is
     * included.
     *
     * @param out
     * @throws IOException
     * @throws CdlException
     */
    public void printAttributesToSmartFrog(GenerateContext out)
            throws IOException,
            CdlException {
        for (Attribute attr : attributes()) {
            out.printAttribute(attr);
        }
    }

    /**
     * Get the immediate text value of an element. That is -the concatenation
     * of all direct child text elements. This string is not trimmed.
     * @return a next string, which will be empty "" if there is no text
     */ 
    public String getTextValue() {
        StringBuilder builder=new StringBuilder();
        for (Node n:nodes()) {
            if (n instanceof Text) {
                Text text=(Text) n;
                builder.append(text.getValue());
            }
        }
        return builder.toString();
    }
}
