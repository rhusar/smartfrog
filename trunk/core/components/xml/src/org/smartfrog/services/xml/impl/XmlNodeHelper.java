package org.smartfrog.services.xml.impl;

import org.smartfrog.services.xml.interfaces.LocalNode;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import nu.xom.Node;
import nu.xom.XMLException;

import java.rmi.RemoteException;

/**
 * Class to contain helper logic for a node
 */
public final class XmlNodeHelper implements XmlNode {
    /**
     * text of the message when wrapping a {@link nu.xom.XMLException} with a {@link
     * SmartFrogException}
     */
    public static final String ERROR_XML_FAULT = "XML exception when creating node or generating XML";

    public XmlNodeHelper(LocalNode owner) {
        this.owner = owner;
        ownerAsPrim = (Prim) owner;
    }

    private LocalNode owner;
    private Prim ownerAsPrim;

    /**
     * this is our underlying node.
     */
    private Node node;

    /**
     * XML data.
     */
    private String xml;

    /**
     * set the node
     *
     * @param node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * get the current node value
     * @return
     */
    public Node getNode() {
        return node;
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
     * get the last XML evaluated
     *
     * @return the XML; may be null
     */
    public String getXml() {
        return xml;
    }

    /**
     * generate XML from the doc. This always triggers a recalculate of
     * everything, then the attribute is saved. We do it this way because we
     * don't know what has changed underneath.
     *
     * @return XML of the tree
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  for smartfrog problems, and for caught
     *                                  XMLExceptions
     */
    public String toXML() throws RemoteException, SmartFrogException {
        try {
            if (node == null) {
                //demand create the node
                node = owner.createNode();
            }
            xml = node.toXML();
        } catch (XMLException e) {
            throw new SmartFrogException(XmlNodeHelper.ERROR_XML_FAULT, e, ownerAsPrim);
        }
        ownerAsPrim.sfReplaceAttribute(ATTR_XML, xml);
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
        boolean valid = ownerAsPrim.sfResolve(ATTR_VALID, true, true);
        if (!valid) {
            throw new SmartFrogLivenessException("XML fails validity test :"
                    + xml,
                    ownerAsPrim);
        }
    }
}
