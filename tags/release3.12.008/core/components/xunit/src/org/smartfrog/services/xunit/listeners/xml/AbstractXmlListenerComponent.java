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

package org.smartfrog.services.xunit.listeners.xml;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.services.xunit.listeners.html.OneHostXMLListener;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;

/**
 */
public abstract class AbstractXmlListenerComponent extends PrimImpl
        implements XmlListenerFactory {
    private Log log;
    protected ComponentHelper helper = new ComponentHelper(this);
    protected String outputDir;
    protected String preamble;
    protected boolean useHostname;
    /**
     * mapping of suite to file
     */
    private HashMap<String, String> testFiles = new HashMap<String, String>();
    protected String suffix = ".xml";
    private boolean useProcessname;

    protected AbstractXmlListenerComponent() throws RemoteException {
    }

    /**
     * add a mapping of suite to file.
     * </p>
     * If the suitename is already registered, a warning is output, but the method still continues.
     *
     * @param hostname hostname (ignored in the base class)
     * @param suitename suite to use
     * @param xmlFilename the XML filename being created
     */
    protected synchronized void addMapping(String hostname,String suitename, String xmlFilename) {
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
        return testFiles.get(suitename);
    }

    /**
     * map from a test suite name to a filename
     *
     * @param hostname host name
     * @param suitename test suite
     * @return name of output file, or null for no match
     * @throws RemoteException network problems
     */
    public String lookupFilename(String hostname,
                                 String suitename) throws RemoteException {
        return getMapping(suitename);
    }

    /**
     * work out the output dir
     *
     * @return the dir that output is in
     * @throws SmartFrogResolutionException if it is not specified
     * @throws RemoteException network problems
     */
    protected String lookupOutputDir() throws SmartFrogResolutionException,
            RemoteException {
        String out = FileSystem.lookupAbsolutePath(this,
                XmlListenerFactory.ATTR_OUTPUT_DIRECTORY,
                null,
                null,
                true,
                null);
        return out;
    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException network problems
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = helper.getLogger();
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException network problems
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        outputDir = lookupOutputDir();
        File destDir = new File(outputDir);
        destDir.mkdirs();
        if (!destDir.exists()) {
            throw new SmartFrogInitException(
                    "Unable to create destination directory " + destDir);
        }
        preamble = sfResolve(XmlListenerFactory.ATTR_PREAMBLE, (String) null, false);
        useHostname = sfResolve(XmlListenerFactory.ATTR_USE_HOSTNAME, true, true);
        useProcessname = sfResolve(XmlListenerFactory.ATTR_USE_PROCESSNAME, true, true);
        suffix = sfResolve(XmlListenerFactory.ATTR_SUFFIX, suffix, false);
        log.info("output dir is " + outputDir
                + "; hostname=" + useHostname
                +" ; useProcessname="
                +useProcessname);
        log.info("preamble is " + (preamble != null ? preamble : "(undefined)"));
    }

    /**
     * Start listening to a test suite
     *
     * @param suite     the test suite that is about to run. May be null,
     *                  especially during testing.
     * @param hostname  name of host
     * @param processname name of the process
     * @param suitename name of test suite
     * @param timestamp start timestamp (UTC)
     * @return a listener to talk to
     * @throws RemoteException network problems
     * @throws SmartFrogException code problems
     */
    public TestListener listen(TestSuite suite,
                               String hostname,
                               String processname,
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
        if (useProcessname) {
            destDir = new File(destDir, processname);
        }

        String outputFile = suitename + suffix;
        File destFile = new File(destDir, outputFile);
        log.info("Recording tests to " + destFile);
        String destpath = destFile.getAbsolutePath();
        addMapping(hostname, suitename, destpath);
        if (suite != null) {
            //set the absolute path of the file
            log.debug(
                    "Setting " +
                    XmlListener.ATTR_FILE +
                    "attribute on test suite");
            ((Prim)suite).sfReplaceAttribute(XmlListener.ATTR_FILE,
                    destpath);
        }

        try {
            Date start = new Date(timestamp);
            OneHostXMLListener xmlLog;
            xmlLog = createNewSingleHostListener(hostname, 
                destFile, 
                processname, 
                suitename, 
                start);
            xmlLog.open();
            return xmlLog;
        } catch (IOException e) {
            throw SmartFrogException.forward("Failed to open ", e);
        }
    }

    /**
     * Something for implementations to implement to create a listener for a single host.
     *
     * @param hostname hostname
     * @param destFile destination file
     * @param processname name of the process
     * @param suitename name of the suite
     * @param start timestamp the tests started
     * @return a new XML listener
     * @throws IOException if the file cannot be created
     */
    protected abstract OneHostXMLListener createNewSingleHostListener(String hostname,
                                                                      File destFile,
                                                                      String processname,
                                                                      String suitename,
                                                                      Date start) throws
            IOException;
}
