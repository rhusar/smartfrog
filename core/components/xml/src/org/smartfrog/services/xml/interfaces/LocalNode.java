package org.smartfrog.services.xml.interfaces;

import nu.xom.Node;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * This is not a remote interface; it is a local interface that both
 * {@link org.smartfrog.services.xml.impl.SimpleXmlNode} and
 * {@link org.smartfrog.services.xml.impl.CompoundXmlNode} implement to
 * look similar
 */
public interface LocalNode {
    void setNode(Node node);

    void setXml(String xml);

    Node getNode();

    String getXml();

    /**
     * create a node of the appropriate type. This is called during deployment;
     *
     * @return a new node
     *
     * @throws nu.xom.XMLException if needed
     */
    Node createNode() throws RemoteException,
            SmartFrogException;
}
