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

package org.smartfrog.projects.alpine.om;

import nu.xom.NodeFactory;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Document;
import org.smartfrog.projects.alpine.interfaces.NamespaceNodeFactory;

/**
 * A node factory that returns elements all of the right type.
 * Anything we dont recognise is a propertylist.
 *
 */
public class ExtendedNodeFactory extends NodeFactory {

    private final static Nodes EMPTY = new Nodes();

    /**
     * Make a new element
     * @param fullname this comes in with a prefix: on it, which we will
     * need to strip off
     * @param namespace
     * @return
     */
    public Element startMakingElement(String fullname, String namespace) {

        String name=fullname;
        int colon = name.indexOf(':');
        if(colon>=0) {
            name=name.substring(colon+1);
        }
    return null;
    }

    public Nodes finishMakingElement(Element element) {
        return super.finishMakingElement(element);
    }

    /**
     * <p/>
     * Creates a new element in the specified namespace with the specified name.
     * The builder calls this method to make the root element of the document.
     * </p>
     * <p/>
     * <p/>
     * Subclasses may change the name, namespace, content, or other
     * characteristics of the element returned. The default implementation
     * merely calls <code>startMakingElement</code>. However, when subclassing,
     * it is often useful to be able to easily distinguish between the root
     * element and a non-root element because the root element cannot be
     * detached. Therefore, subclasses must not return null from this method.
     * Doing so will cause a <code>NullPointerException</code>. </p>
     *
     * @param name      the qualified name of the element
     * @param namespace the namespace URI of the element
     *
     * @return the new root element
     */
    public Element makeRootElement(String name, String namespace) {

        return null;
    }


    /**
     * SOAP says these are forbidden. We just strip them, in an act of silent forgiveness.
     * 
     * @return the nodes to be added to the tree
     */
    public Nodes makeProcessingInstruction(String target, String data) {
        return EMPTY;
    }




}
