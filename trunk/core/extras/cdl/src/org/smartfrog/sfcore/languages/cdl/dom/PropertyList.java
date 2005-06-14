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
import nu.xom.Attribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.utils.XmlUtils;
import org.smartfrog.sfcore.languages.cdl.resolving.ExtendsResolver;

import javax.xml.namespace.QName;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * created 21-Apr-2005 14:26:55
 */

public class PropertyList extends DocNode implements ToSmartFrog {

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
        if(extendsAttr!=null) {
            String rawextension=extendsAttr.getValue();
            //now split that into namespace
            String prefix=XmlUtils.extractNamespacePrefix(rawextension);
            String local = XmlUtils.extractLocalname(rawextension);
            String namespace=null;
            if(prefix!=null) {
                namespace = getNode().getNamespaceURI(prefix);
                if(namespace==null) {
                    throw new CdlXmlParsingException(ERROR_UNKNOWN_NAMESPACE+prefix);
                }
            }
        }

        //run through all our child elements and processs them
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
        //else, it is not a recognised type, so we make another propertly list from it
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
     * Assert that we are valid as toplevel.
     */
    public void validateToplevel() throws CdlXmlParsingException {

    }

    /**
     * validate lowerlevel nodes
     */
    public void validateLowerLevel() throws CdlXmlParsingException {
        validateToplevel();
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


    /**
     * stringify for debugging: shows local and extends name only
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
        buffer.append(" from ");
        buffer.append(getOwner());
        return buffer.toString();
    }

    /**
     * Merge with the extension
     * @param extension
     */
    public void merge(PropertyList extension) {
        //sanity check: we are merging ourselves
        assert extension.name.equals(extendsName);
        //now apply the rules of the CDL spec, section 7.2.2
        mergeAttributes(extension);
        mergeElements(extension);
        //clear our extendsname, as we are now merged. no more extending for us.
        extendsName = null;
        //strip @cdl:extends
        Attribute extendsAttr = getExtendsAttribute();
        if(extendsAttr!=null) {
            getNode().removeAttribute(extendsAttr);
        }
    }

    private Attribute getExtendsAttribute() {
        Attribute extendsAttr= getAttribute(CDL_NAMESPACE,ATTR_EXTENDS);
        return extendsAttr;
    }

    /**
     * merge in all attributes. public for testing
     * @param extension
     */
    public void mergeAttributes(PropertyList extension) {
        //this is where we start to work at the XOM level.
        Element self=getNode();
        for(Attribute extAttr: extension.attributes()) {
            String namespace = extAttr.getNamespaceURI();
            String local= extAttr.getLocalName();
            if(!hasAttribute(namespace, local)) {
                //no match: copy the attribute
                self.addAttribute((Attribute)extAttr.copy());
            }
        }
    }

    /**
     * merge in all elements. public for testing
     *
     * @param extension
     */
    private void mergeElements(PropertyList extension) {
        //TODO
    }

    /**
     * (recursively) resolve the extends attributes of all our child nodes
     * BUGBUG: does not properly distinguish child nodes and not enter/exit them properly
     * @param resolver
     */
    public void resolveChildExtends(CdlDocument document,ExtendsResolver resolver)
            throws CdlResolutionException {
        for(DocNode node:children) {
            if(node instanceof PropertyList) {
                PropertyList list=(PropertyList) node;
                resolver.resolveExtends(document,list);
            }
        }
    }
}
