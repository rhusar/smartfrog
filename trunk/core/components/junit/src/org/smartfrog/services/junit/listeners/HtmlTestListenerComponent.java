/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.services.junit.listeners;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.TestSuite;
import org.smartfrog.services.filesystem.FileSystem;

import java.util.HashMap;
import java.util.Date;
import java.rmi.RemoteException;
import java.io.File;
import java.io.IOException;

/**
 * This is a listener of tests
 * Implement the {@link org.smartfrog.services.junit.listeners.XmlListenerFactory} interface and so provide a component
 * for XML logging. Note that we are only a factory; the listening is done by
 * {@link org.smartfrog.services.junit.listeners.OneHostXMLListener }
 */
public class HtmlTestListenerComponent extends PrimImpl
        implements HtmlTestListenerFactory {

    private Log log;
    private ComponentHelper helper = new ComponentHelper(this);
    private String outputDir;

    /**
     * mapping of suite to file
     */
    private HashMap testFiles = new HashMap();

    /**
     * destination directory
     */
    private File destDir;

    /**
     * construct a base interface
     *
     * @throws java.rmi.RemoteException
     */
    public HtmlTestListenerComponent() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        outputDir = lookupOutputDir();
        destDir = new File(outputDir);
        if(!destDir.mkdirs()) {
            throw new SmartFrogInitException("Unable to create destination directory "+destDir);
        }
    }

    /**
     * {@inheritDoc}
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

        String outputFile = suitename + ".xml";
        File destFile = new File(destDir, outputFile);
        log.info("XmlFile=" + destFile);
        String destpath = destFile.getAbsolutePath();
        addMapping(suitename, destpath);
        if (suite != null) {
            //set the absolute path of the file
            log.info(
                    "Setting " +
                    XmlListener.ATTR_FILE +
                    "attribute on test suite");
            suite.sfReplaceAttribute(XmlListener.ATTR_FILE,
                    destpath);
        }

        try {
            Date start = new Date(timestamp);

            OneHostXMLListener xmlLog;
            xmlLog = new OneHostHtmlListener(hostname,
                    destFile,
                    suitename,
                    start,
                    null);
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
     * @throws java.rmi.RemoteException
     */
    public String lookupFilename(String suitename) throws RemoteException {
        return getMapping(suitename);
    }


    /**
     * work out the output dir
     *
     * @return the dir that output is in
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException if it is not specified
     * @throws java.rmi.RemoteException
     */
    private String lookupOutputDir() throws SmartFrogResolutionException,
            RemoteException {
        String out = FileSystem.lookupAbsolutePath(this,
                ATTR_DIRECTORY,
                null,
                null,
                true,
                null);
        return out;
    }


}
