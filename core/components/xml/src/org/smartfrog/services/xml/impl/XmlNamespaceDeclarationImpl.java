package org.smartfrog.services.xml.impl;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.services.xml.interfaces.XmlNamespaceDeclaration;

import java.rmi.RemoteException;

import nu.xom.Element;

/**
 * This class does nothing other than add its declaration to an XML element when told to do so.
 */
public class XmlNamespaceDeclarationImpl extends PrimImpl implements XmlNamespaceDeclaration {

    public XmlNamespaceDeclarationImpl() throws RemoteException {
    }

    /**
     * look up our namespace declaration and add it to the XML element
     * @param element
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public void addDeclaration(Element element) throws SmartFrogResolutionException, RemoteException {
        String prefix = sfResolve(ATTR_PREFIX, (String) null, true);
        String namespace = sfResolve(ATTR_NAMESPACE, (String) null, false);
        element.addNamespaceDeclaration(prefix,namespace);
    }
}
