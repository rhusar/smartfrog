package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.TestSuite;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;

/**
 * This is a listener of tests
 * Implement the {@link XmlListenerFactory} interface and so provide a component
 * for XML logging. Note that we are only a factory; the listening is done by
 * {@link OneHostXMLListener }
 */
public class XmlListenerComponent
        extends AbstractXmlListenerComponent implements XmlListenerFactory {

    /**
     * construct a base interface
     *
     * @throws RemoteException
     */
    public XmlListenerComponent() throws RemoteException {
    }


    /**
     * Override point; create a new XML listener
     * @param hostname
     * @param destFile
     * @param suitename
     * @param start
     * @return
     * @throws IOException
     */
    protected OneHostXMLListener createNewSingleHostListener(String hostname,
                                                             File destFile,
                                                             String suitename,
                                                             Date start) throws
            IOException {
        return new OneHostXMLListener(hostname,
                destFile,
                suitename,
                start,
                preamble);
    }


}
