package org.smartfrog.services.xml.impl;

import nu.xom.Node;
import nu.xom.XMLException;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * This node contains whatever
 */
public abstract class SimpleXmlNode extends PrimImpl implements XmlNode {

    /**
     * this is our underlying node.
     */
    private Node node;

    /**
     * XML data.
     */
    private String xml;

    /**
     * text of the message when wrapping a {@link XMLException} with a {@link
     * SmartFrogException}
     */
    public static final String ERROR_XML_FAULT = "XML exception when creating node or generating XML";

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
     * @throws XMLException if needed
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
            throw new SmartFrogException(ERROR_XML_FAULT, e, this);
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
