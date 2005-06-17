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
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.languages.cdl.utils.XmlUtils;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * created 21-Apr-2005 14:26:55
 */

public class PropertyList extends DocNode implements ToSmartFrog {

    protected boolean toplevel = false;

    /**
     * Our name. Only toplevel elements can have a qname
     */
    protected QName name;

    /**
     * Name of the template that we extend. Null if we do not extend anything
     */
    protected QName extendsName;

    /**
     * And the resolved extension Null if extendsName==null;
     */
    public PropertyList extendsResolved;

    /**
     * a log
     */
    protected Log log = ClassLogger.getLog(this);
    /**
     * child list
     */
    private List<DocNode> children = new LinkedList<DocNode>();
    public static final String ERROR_UNKNOWN_NAMESPACE = "Unknown namespace ";


    public PropertyList() {
    }

    public PropertyList(Element element) throws CdlXmlParsingException {
        bind(element);
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public QName getExtendsName() {
        return extendsName;
    }

    public void setExtendsName(QName extendsName) {
        this.extendsName = extendsName;
    }

    /**
     * Parse from XML
     *
     * @throws CdlXmlParsingException
     */
    public void bind(Element element) throws CdlXmlParsingException {
        //parent
        super.bind(element);

        //get our name and extends attribute
        QName prototypeName = XmlUtils.makeQName(element.getNamespaceURI(),
                element.getLocalName(),
                element.getNamespacePrefix());
        setName(prototypeName);

        //what are we extending?
        Attribute extendsAttr = getExtendsAttribute();
        if (extendsAttr != null) {
            String rawextension = extendsAttr.getValue();
            //now split that into namespace
            String prefix = XmlUtils.extractNamespacePrefix(rawextension);
            String local = XmlUtils.extractLocalname(rawextension);
            String namespace = null;
            if (prefix != null) {
                namespace = getNode().getNamespaceURI(prefix);
                if (namespace == null) {
                    throw new CdlXmlParsingException(
                            ERROR_UNKNOWN_NAMESPACE + prefix);
                }
            }
            setExtendsName(XmlUtils.makeQName(namespace, local, prefix));
        }

        //run through all our child elements and process them
        for (Node child : children()) {
            if (child instanceof Element) {
                children.add(createNodeFromElement((Element) child));
            }
        }

    }

    /**
     * create the appropriate node for an element type
     *
     * @return
     */
    private DocNode createNodeFromElement(Element element)
            throws CdlXmlParsingException {
        if (Documentation.isA(element)) {
            return new Documentation(element);
        }
        if (Expression.isA(element)) {
            return new Expression(element);
        }
        //else, it is not a recognised type, so we make another property list from it
        return new PropertyList(element);
    }


    protected void addChild(Element node) {
        //TODO
    }

    /**
     * Child elements
     *
     * @return our child list (may be null)
     */
    public List<DocNode> childDocNodes() {
        return children;
    }

    /**
     * Get an iterator over the child list
     *
     * @return
     */
    public ListIterator<DocNode> childIterator() {
        return children.listIterator();
    }


    /**
     * Test for a propertylist instance name
     *
     * @param testName
     * @return
     */
    public boolean isNamed(QName testName) {
        return testName.equals(name);
    }

    public boolean isToplevel() {
        return toplevel;
    }

    public void setToplevel(boolean toplevel) {
        this.toplevel = toplevel;
    }

    /**
     * stringify for debugging: shows local and extends name only
     *
     * @return
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Prototype : ");
        buffer.append(name);
        if (extendsName != null) {
            buffer.append(" extends ");
            buffer.append(extendsName);
        }
        if (getOwner() != null) {
            buffer.append(" from ");
            buffer.append(getOwner());
        }
        return buffer.toString();
    }

    /**
     * Merge with another context, by inherting attributes and stripping
     * extension information
     *
     * @param extension
     */
    public void mergeAttributes(PropertyList extension) {
        //sanity check: we are merging ourselves
        assert extension.name.equals(extendsName);
        //now apply the rules of the CDL spec, section 7.2.2
        inheritAttributes(extension);
        //clear our extendsname, as we are now merged. no more extending for us.
        extendsName = null;
        //strip @cdl:extends
        Attribute extendsAttr = getExtendsAttribute();
        if (extendsAttr != null) {
            getNode().removeAttribute(extendsAttr);
        }
    }

    /**
     * Get the extends attribute of the element. Prints a warning if there is an
     * attribute called extends that is not in the cdl: namespace.
     *
     * @return
     */
    private Attribute getExtendsAttribute() {
        Attribute extendsAttr = getAttribute(CDL_NAMESPACE, ATTR_EXTENDS);
        if (extendsAttr == null) {
            if (getAttribute(null, ATTR_EXTENDS) != null) {
                //this is here because I always get this wrong myself, and wanted
                //some extra diagnostics. SteveL.
                log.warn("Template " +
                        toString() +
                        " has an extends attribute, but it is " +
                        "not in the CDL namespace. This may be an error.");
            }
        }
        return extendsAttr;
    }

    /**
     * merge in all attributes. public for testing
     *
     * @param extension
     */
    public void inheritAttributes(PropertyList extension) {
        //this is where we start to work at the XOM level.
        Element self = getNode();
        for (Attribute extAttr : extension.attributes()) {
            String namespace = extAttr.getNamespaceURI();
            String local = extAttr.getLocalName();
            if (!hasAttribute(namespace, local)) {
                //no match: copy the attribute
                self.addAttribute((Attribute) extAttr.copy());
            }
        }
    }

    /**
     * get the first child element of a node
     *
     * @param namespace optional namespace
     * @param localname localname
     * @return
     * @see Element#getFirstChildElement(String, String)
     */
    private Element getFirstChildElement(String namespace, String localname) {
        if (localname != null) {
            return getNode().getFirstChildElement(namespace, localname);
        } else {
            return getNode().getFirstChildElement(localname);
        }
    }

    /**
     * Get the first child element whose name matches that of the source
     *
     * @param source
     * @return
     */
    public Element getFirstChildElement(Element source) {
        return getFirstChildElement(source.getNamespaceURI(),
                source.getLocalName());
    }

    /**
     * look up the child property list node containing this element.
     *
     * @param element element to look for
     * @return the propertly list or null for no match
     */
    public PropertyList getChildListContaining(Element element) {
        for (DocNode node : childDocNodes()) {
            if (node instanceof PropertyList) {
                PropertyList child = (PropertyList) node;
                //yes, we use reference equality here
                if (child.getNode() == element) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * look up the child property list node whose qname matches that of the one
     * passed in
     *
     * @param that property list to look for
     * @return the propertly list or null for no match
     * @see #getChildTemplateMatching(QName)
     */
    public PropertyList getChildTemplateMatching(PropertyList that) {
        QName thatName = that.getName();
        return getChildTemplateMatching(thatName);
    }


    /**
     * look up the child property list node whose qname matches that of the one
     * passed in
     *
     * @param name qname to search on
     * @return the propertly list or null for no match
     */
    public PropertyList getChildTemplateMatching(QName name) {
        for (DocNode node : childDocNodes()) {
            if (node instanceof PropertyList) {
                PropertyList child = (PropertyList) node;
                if (child.isNamed(name)) {
                    return child;
                }
            }
        }
        return null;
    }

}
