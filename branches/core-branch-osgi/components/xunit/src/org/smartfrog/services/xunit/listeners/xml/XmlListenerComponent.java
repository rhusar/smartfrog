package org.smartfrog.services.xunit.listeners.xml;

import org.smartfrog.services.xunit.listeners.html.OneHostXMLListener;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * This is a listener of tests
 * Implement the {@link XmlListenerFactory} interface and so provide a component
 * for XML logging. Note that we are only a factory; the listening is done by
 * {@link org.smartfrog.services.xunit.listeners.html.OneHostXMLListener }
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
     * @param processname
     * @param suitename
     * @param start
     * @return
     * @throws IOException
     */
    protected OneHostXMLListener createNewSingleHostListener(String hostname,
                                                             File destFile,
                                                             String processname, String suitename,
                                                             Date start) throws
            IOException {
        return new OneHostXMLListener(hostname,
                processname,
                suitename,
                destFile,
                start,
                preamble);
    }


}
