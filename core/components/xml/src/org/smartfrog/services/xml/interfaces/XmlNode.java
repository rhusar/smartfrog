package org.smartfrog.services.xml.interfaces;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * XML node class
 */
public interface XmlNode extends Remote {

    static final String ATTR_XML = "xml";
    static final String ATTR_VALID = "valid";

    /**
     * generate XML from the doc.
     *
     * @return XML of the document
     */
    String toXML() throws RemoteException, SmartFrogException;

    /**
     * Validate the XML, post-generation. Default implementation checks the
     * <code>valid</code> attribute and fails if it is false.
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    void validate() throws SmartFrogException,
            RemoteException;
}
