package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.services.junit.TestListener;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * Implement the {@link XmlListenerFactory} interface and so provide a component
 * for XML logging. Note that we are only a factory; the listening is done by
 * {@link OneHostXMLListener }
 */
public class XmlListenerComponent extends PrimImpl
        implements XmlListenerFactory {


    /**
     * construct a base interface
     *
     * @throws RemoteException
     */
    public XmlListenerComponent() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();

    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();

    }


    /**
     * bind to a caller
     *
     * @param hostname  name of host
     * @param suitename name of test suite
     * @param timestamp start timestamp (UTC)
     * @return a session ID to be used in test responses
     */
    public TestListener listen(String hostname,
            String suitename,
            long timestamp) throws RemoteException,
            SmartFrogException {
        //only support a single file
        String outputDir = FileImpl.lookupAbsolutePath(this,
                OUTPUT_DIRECTORY,
                null,
                null,
                true,
                null);
        String preamble = sfResolve(PREAMBLE, (String) null, false);
        boolean useHostname = sfResolve(USE_HOSTNAME, true, true);
        if (suitename == null && "".equals(suitename)) {
            throw new SmartFrogException(
                    "Test suite must be named for XML exporting");
        }

        try {
            File destDir = new File(outputDir);
            if (useHostname) {
                destDir = new File(destDir, hostname);
            }

            String outputFile = suitename + ".xml";

            File destFile = new File(destDir, outputFile);
            Date start = new Date(timestamp);

            OneHostXMLListener xmlLog;
            xmlLog = new OneHostXMLListener(hostname,
                    destFile,
                    suitename,
                    start,
                    preamble);
            return xmlLog;
        } catch (IOException e) {
            throw SmartFrogException.forward("Failed to open ", e);
        }
    }


    /**
     * Provides hook for subclasses to implement usefull termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException,
            RemoteException {
        super.sfPing(source);
    }

}
