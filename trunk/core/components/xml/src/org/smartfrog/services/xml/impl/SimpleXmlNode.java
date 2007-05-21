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
     * @throws RemoteException In case of network/rmi error
     */
    public SimpleXmlNode() throws RemoteException {
    }

    /**
     * set the node
     *
     * @param node the node
     */
    public void setNode(Node node) {
        helper.setNode(node);
    }

    /**
     * set the XML
     *
     * @param xml the content as xml
     */
    public void setXml(String xml) {
        helper.setXml(xml);
    }

    /**
     * Get the node underneath.
     * Will be null until the node is created.
     *
     * @return the node or null
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
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public String toXML() throws RemoteException, SmartFrogException {
        return helper.toXML();
    }

    /**
     * Validate the XML, post-generation. Default implementation checks the
     * <code>valid</code> attribute and fails if it is false.
     *
     * @throws SmartFrogException for smartfrog problems, and for caught
     *                            XMLExceptions
     * @throws RemoteException In case of network/rmi error
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

    /**
     * Equality test.
     * Uses XML logic to compare
     * @param o other instance to compare against
     * @return true if there is a match
     */
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
