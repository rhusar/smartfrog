package org.smartfrog.services.xml.impl;

import nu.xom.Node;
import org.smartfrog.services.xml.interfaces.LocalNode;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * This node contains whatever
 */
public abstract class SimpleXmlNode extends PrimImpl implements XmlNode,
        LocalNode {

    /**
     * most of the work is delegated to the helper
     */
    protected XmlNodeHelper helper = new XmlNodeHelper(this);

    /**
     * empty constructor
     *
     * @throws RemoteException
     */
    public SimpleXmlNode() throws RemoteException {
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
     * @return
     */
    public Node getNode() {
        return helper.getNode();
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
     * After calling the superclass, we generate the XML
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        toXML();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleXmlNode)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final SimpleXmlNode simpleXmlNode = (SimpleXmlNode) o;

        if (helper != null ?
                !helper.equals(simpleXmlNode.helper) :
                simpleXmlNode.helper != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (helper != null ? helper.hashCode() : 0);
        return result;
    }
}
