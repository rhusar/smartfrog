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
import nu.xom.XMLException;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.compound.CompoundImpl;

import java.rmi.RemoteException;

/**
 * Most of this class is an ugly cut and paste of {@link SimpleXmlNode}, now
 * with compound support added in. Blech.
 */
public abstract class CompoundXmlNode extends CompoundImpl implements XmlNode {

    /**
     * this is our underlying node.
     */
    private Node node;

    /**
     * XML data.
     */
    private String xml;

    public CompoundXmlNode() throws RemoteException {
    }

    /**
     * set the node
     *
     * @param node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * set the XML
     *
     * @param xml
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * Get the node underneath. Will be null until the node is created.
     *
     * @return
     */
    public Node getNode() {
        return node;
    }

    /**
     * get the last XML evaluated
     *
     * @return the XML; may be null
     */
    public String getXml() {
        return xml;
    }

    /**
     * create a node of the appropriate type. This is called during deployment;
     *
     * @return a new node
     *
     * @throws nu.xom.XMLException if needed
     */
    protected abstract Node createNode() throws RemoteException,
            SmartFrogException;

    /**
     * generate XML from the doc. This always triggers a recalculate of
     * everything, then the attribute is saved. We do it this way because we
     * don't know what has changed underneath.
     *
     * @return XML of the tree
     *
     * @throws RemoteException
     * @throws SmartFrogException for smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public String toXML() throws RemoteException, SmartFrogException {
        try {
            if (node == null) {
                //demand create the node
                node = createNode();
            }
            xml = node.toXML();
        } catch (XMLException e) {
            throw new SmartFrogException(SimpleXmlNode.ERROR_XML_FAULT,
                    e,
                    this);
        }
        this.sfReplaceAttribute(ATTR_XML, xml);
        validate();
        return xml;
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
        boolean valid = sfResolve(ATTR_VALID, true, true);
        if (!valid) {
            throw new SmartFrogLivenessException("XML fails validity test :"
                    + xml,
                    this);
        }
    }

}
