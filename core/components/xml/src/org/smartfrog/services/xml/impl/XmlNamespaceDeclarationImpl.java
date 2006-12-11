package org.smartfrog.services.xml.impl;

import nu.xom.Element;
import org.smartfrog.services.xml.interfaces.XmlNamespaceDeclaration;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * This class does nothing other than add its declaration to an XML element when
 * told to do so.
 */
public class XmlNamespaceDeclarationImpl extends PrimImpl
        implements XmlNamespaceDeclaration {

    public XmlNamespaceDeclarationImpl() throws RemoteException {
    }

    /**
     * look up our namespace declaration and add it to the XML element
     *
     * @param element element to add
     * @throws SmartFrogResolutionException For smartfrog problems
     * @throws RemoteException In case of network/rmi error
     */
    public void addDeclaration(Element element)
            throws SmartFrogResolutionException, RemoteException {
        String prefix = sfResolve(ATTR_PREFIX, (String) null, true);
        String namespace = sfResolve(ATTR_NAMESPACE, (String) null, false);
        element.addNamespaceDeclaration(prefix, namespace);
    }
}
