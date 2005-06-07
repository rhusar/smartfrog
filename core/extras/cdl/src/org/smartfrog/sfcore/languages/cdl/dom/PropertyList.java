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

import org.smartfrog.sfcore.languages.cdl.CdlParsingException;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

import nu.xom.Element;
import nu.xom.Node;

/**
 * created 21-Apr-2005 14:26:55
 */

public class PropertyList extends DocNode implements ToSmartFrog {

    /**
     * Our name.
     * Only toplevel elements can have a qname
     */
    public QName name;

    /**
     * Name of the template that we extend.
     * Null if we do not extend anything
     */
    public QName extendsName;

    /**
     * And the resolved extension
     * Null if extendsName==null;
     */
    public PropertyList extendsResolved;

    /**
     * child list
     */
    private List<DocNode> children = new LinkedList<DocNode>();

    /**
     * Error text for testing
     */
    public static final String ERROR_LOWLEVEL_NAMED = "low-level PropertyList elements cannot be given names";


    public PropertyList() {
    }

    public PropertyList(Element element) throws CdlParsingException {
        bind(element);
    }


    /**
     * Parse from XML
     *
     * @throws CdlParsingException
     */
    public void bind(Element element) throws CdlParsingException {
        super.bind(element);
        //run through all our child elements and processs them
        for(Node child:children()) {
            if(child instanceof Element) {
                children.add(createNodeFromElement((Element)child));
            }
        }

    }

    /**
     * create the appropriate node for an element type
     * @return
     */
    DocNode createNodeFromElement(Element element) throws CdlParsingException {
        if(Documentation.isA(element)) {
            return new Documentation(element);
        }
        if(Expression.isA(element)) {
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
     * @return
     */
    public ListIterator<DocNode> childIterator() {
        return children.listIterator();
    }


    /**
     * Assert that we are valid as toplevel.
     */
    public void validateToplevel() throws CdlParsingException {

    }

    /**
     * validate lowerlevel nodes
     */
    public void validateLowerLevel() throws CdlParsingException {
        CdlParsingException.assertValid(name==null,
                ERROR_LOWLEVEL_NAMED);
    }

    /**
     * Test for a propertylist instance name
     * @param testName
     * @return
     */
    public boolean isNamed(QName testName) {
        return testName.equals(name);
    }
}
