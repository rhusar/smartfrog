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
package org.smartfrog.services.xml.impl;

import nu.xom.Element;
import nu.xom.Node;
import org.smartfrog.services.xml.interfaces.XmlElement;
import org.smartfrog.services.xml.interfaces.LocalNode;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * Probably the most complex of all the nodes, the element implementation
 */
public class XmlElementImpl extends CompoundXmlNode implements XmlElement {

    public XmlElementImpl() throws RemoteException {
    }

    /**
     * create a node of the appropriate type. This is called during deployment;
     *
     * @return a new node
     *
     * @throws nu.xom.XMLException if needed
     */
    public Node createNode() throws RemoteException, SmartFrogException {
        String localname = sfResolve(ATTR_LOCALNAME, (String) null, true);
        String namespace = sfResolve(ATTR_NAMESPACE, (String) null, false);
        Element element = new Element(localname, namespace);
        return element;
    }

    /**
     * Get the node as an element
     * @return the node typecast to an element
     */
    public Element getElement() {
        return (Element) getNode();
    }

    /**
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    protected void addChildren() throws SmartFrogException, RemoteException {
        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
            Object elem = e.nextElement();
            if (elem instanceof LocalNode) {
                LocalNode node = (LocalNode) elem;
                appendChild(node);
            } else if(elem instanceof XmlNamespaceDeclarationImpl) {
                //namespaces are special
                XmlNamespaceDeclarationImpl declaration = (XmlNamespaceDeclarationImpl) elem;
                declaration.addDeclaration(getElement());
            }
        }
    }

    /**
     * add all children as a node
     */
    /*
    protected void addAllChildNodes() {
        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
            Object elem = e.nextElement();
            if (elem instanceof LocalNode) {
                LocalNode node = (LocalNode) elem;
                appendChild(node);
            }
        }
    }
    */
}
