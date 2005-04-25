package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.TestSuite;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;

/**
 * Implement the {@link XmlListenerFactory} interface and so provide a component
 * for XML logging. Note that we are only a factory; the listening is done by
 * {@link OneHostXMLListener }
 */
public class XmlListenerComponent extends PrimImpl
        implements XmlListenerFactory {

    private Log log;
    private ComponentHelper helper = new ComponentHelper(this);
    private String outputDir;
    private String preamble;
    private boolean useHostname;

    /**
     * mapping of suite to file
     */
    private HashMap testFiles = new HashMap();

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
        outputDir = lookupOutputDir();
        preamble = sfResolve(PREAMBLE, (String) null, false);
        useHostname = sfResolve(USE_HOSTNAME, true, true);
        log.info("output dir is " + outputDir + "; hostname=" + useHostname);
        log.info("preamble is " + preamble != null ? preamble : "(undefined)");
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
        log = helper.getLogger();

    }


    /**
     * bind to a caller
     *
     * @param suite
     * @param hostname  name of host
     * @param suitename name of test suite
     * @param timestamp start timestamp (UTC)
     * @return a session ID to be used in test responses
     */
    public TestListener listen(TestSuite suite, String hostname,
            String suitename,
            long timestamp) throws RemoteException,
            SmartFrogException {
        if (suitename == null && "".equals(suitename)) {
            throw new SmartFrogException(
                    "Test suite must be named for XML exporting");
        }

        File destDir = new File(outputDir);
        if (useHostname) {
            destDir = new File(destDir, hostname);
        }

        String outputFile = suitename + ".xml";
        File destFile = new File(destDir, outputFile);
        log.info("XmlFile=" + destFile);
        String destpath = destFile.getAbsolutePath();
        addMapping(suitename, destpath);
        if (suite != null) {
            //set the absolute path of the file
            log.info(
                    "Setting " +
                    XmlListener.ATTR_XMLFILE +
                    "attribute on test suite");
            suite.sfReplaceAttribute(XmlListener.ATTR_XMLFILE,
                    destpath);
        }

        try {
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
     * add a mapping of suite to file
     *
     * @param suitename
     * @param xmlFilename
     */
    private synchronized void addMapping(String suitename, String xmlFilename) {
        if (getMapping(suitename) != null) {
            log.warn("A suite called " +
                    suitename
                    + " exists; its output will be overwritten");
        }
        testFiles.put(suitename, xmlFilename);
    }

    /**
     * thread-safe accessor to the suite-file mapping
     *
     * @param suitename suite to lookup
     * @return absolute path of the output file, or null for no mapping.
     */
    private synchronized String getMapping(String suitename) {
        return (String) testFiles.get(suitename);
    }

    /**
     * map from a test suite name to a filename
     *
     * @param suitename test suite
     * @return name of output file, or null for no match
     * @throws RemoteException
     */
    public String lookupFilename(String suitename) throws RemoteException {
        return getMapping(suitename);
    }


    /**
     * work out the output dir
     *
     * @return the dir that output is in
     * @throws SmartFrogResolutionException if it is not specified
     * @throws RemoteException
     */
    private String lookupOutputDir() throws SmartFrogResolutionException,
            RemoteException {
        String out = FileSystem.lookupAbsolutePath(this,
                OUTPUT_DIRECTORY,
                null,
                null,
                true,
                null);
        return out;
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
