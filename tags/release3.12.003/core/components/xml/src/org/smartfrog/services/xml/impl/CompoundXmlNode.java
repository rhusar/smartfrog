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

import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Attribute;
import nu.xom.Element;
import org.smartfrog.services.xml.interfaces.LocalNode;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * Most of this class is an ugly cut and paste of {@link SimpleXmlNode}, now
 * with compound support added in. Oh, for multiple inheritance :)
 */
public abstract class CompoundXmlNode extends CompoundImpl implements XmlNode,
        LocalNode {

    /**
     * most of the work is delegated to the helper
     */
    protected XmlNodeHelper helper = new XmlNodeHelper(this);

    public CompoundXmlNode() throws RemoteException {
    }

    /**
     * set the node
     *
     * @param node
     */
    public void setNode(Node node) {
        helper.setNode(node);
    }

    /**
     * set the XML
     *
     * @param xml
     */
    public void setXml(String xml) {
        helper.setXml(xml);
    }

    /**
     * Get the node underneath. Will be null until the node is created.
     *
     * @return the node
     */
    public Node getNode() {
        return helper.getNode();
    }

    /**
     * Get the parent node
     * @return the node cast to a ParentNode
     */
    public ParentNode getParentNode() {
        return (ParentNode) helper.getNode();
    }

    /**
     * Get the parent element
     * @return the node cast to an Element
     */
    public Element getParentElement() {
        return (Element) helper.getNode();
    }
    /**
     * get the last XML evaluated
     *
     * @return the XML; may be null
     */
    public String getXml() {
        return helper.getXml();
    }

    /**
     * generate XML from the doc. This always triggers a recalculate of
     * everything, then the attribute is saved. We do it this way because we
     * don't know what has changed underneath.
     *
     * @return XML of the tree
     * @throws RemoteException
     * @throws SmartFrogException for smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public String toXML() throws RemoteException, SmartFrogException {
        return helper.toXML();
    }

    /**
     * Validate the XML, post-generation. Default implementation checks the
     * <code>valid</code> attribute and fails if it is false.
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void validate() throws SmartFrogException,
            RemoteException {
        helper.validate();
    }

    /**
     * After calling the superclass (and so deploying all our children), we
     * generate the XML
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();

        //xmlify ourselves (no children)
        toXML();

        //now we add our children
        addChildren();

        //xmlify ourselves again
        toXML();
    }

    /**
     * subclasses must implement their child processing logic here.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    protected abstract void addChildren() throws SmartFrogException,
            RemoteException;


    /**
     * add a child to this node.
     *
     * @param node child node
     */
    public void appendChild(LocalNode node) {
        Node xomNode = node.getNode();
        if(xomNode instanceof Attribute) {
            Attribute attribute=(Attribute) xomNode;
            getParentElement().addAttribute(attribute);
        } else {
            getParentNode().appendChild(xomNode);
        }
    }

    /**
     * cast a prim to a {@link LocalNode} and add.
     *
     * @param nodeAsPrim the node as a prim
     * @throws SmartFrogDeploymentException if of the wrong type
     */
    public void appendChild(Prim nodeAsPrim)
            throws SmartFrogDeploymentException {
        if (!(nodeAsPrim instanceof LocalNode)) {
            throw new SmartFrogDeploymentException("not an XML tyoe",
                    nodeAsPrim);
        }
        //cast and append
        LocalNode node = (LocalNode) nodeAsPrim;
        appendChild(node);
    }
}

