/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/
package org.smartfrog.services.xml.impl;

import nu.xom.Node;
import nu.xom.XMLException;
import org.smartfrog.services.xml.interfaces.LocalNode;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * Class to contain helper logic for a node
 */
public final class XmlNodeHelper implements XmlNode {
    /**
     * text of the message when wrapping a {@link nu.xom.XMLException} with a
     * {@link SmartFrogException}
     */
    public static final String ERROR_XML_FAULT = "XML exception when creating node or generating XML";

    public XmlNodeHelper(LocalNode owner) {
        this.owner = owner;
        ownerAsPrim = (Prim) owner;
    }

    /**
     * owning node
     */
    private LocalNode owner;

    /**
     * our owner as a prim
     */
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
     * @param node the new node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * get the current node value
     *
     * @return the current node
     */
    public Node getNode() {
        return node;
    }

    /**
     * set the XML
     *
     * @param xml xml string
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
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public String toXML() throws RemoteException, SmartFrogException {
        try {
            if (node == null) {
                //demand create the node
                node = owner.createNode();
            }
            xml = node.toXML();
        } catch (XMLException e) {
            throw new SmartFrogException(XmlNodeHelper.ERROR_XML_FAULT,
                    e,
                    ownerAsPrim);
        }
        ownerAsPrim.sfReplaceAttribute(ATTR_XML, xml);
        validate();
        return xml;
    }

    /**
     * Validate the XML, post-generation. Default implementation checks the
     * <code>valid</code> attribute and fails if it is false.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
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

    /**
     * equality test compares node values
     * @param o
     * @return true if they are equal
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XmlNodeHelper)) {
            return false;
        }

        final XmlNodeHelper xmlNodeHelper = (XmlNodeHelper) o;

        if (node != null ?
                !node.equals(xmlNodeHelper.node) :
                xmlNodeHelper.node != null) {
            return false;
        }

        return true;
    }

    /**
     * hash code is derived from the node
     * @return hascode of the node
     */
    public int hashCode() {
        return (node != null ? node.hashCode() : 0);
    }


    /**
     * mape from an XMLException (extending RuntimeException) into a
     * SmartFrogException, which can then be thrown.
     * @param xmle XML exception
     * @return an instantiated and configured SmartFrogException.
     */
    public static SmartFrogException handleXmlException(XMLException xmle) {
        SmartFrogException sfe=new SmartFrogException(xmle);
        return sfe;
    }
}
