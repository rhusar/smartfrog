/* (C) Copyright 2004-2008 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.xunit.listeners.antxml;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import org.smartfrog.services.xunit.listeners.xml.FileListener;
import org.smartfrog.services.xunit.serial.LogEntry;
import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.services.xunit.serial.TestInfo;
import org.smartfrog.services.xunit.serial.ThrowableTraceInfo;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * This class listens to tests on a single host. The XML Listener forwards stuff
 * to it. It is not a component; it is a utility class that components use, so
 * as to log different test suites to different files.
 * <p/>
 */
public class AntXmlListener implements FileListener, XMLConstants {

    private Document document;

    /**
     * file we save to
     */
    private File destFile;

    /**
     * name of host
     */
    private String hostname;
    /**
     * name of the process
     */
    private String processname;
    /**
     * name of the test suite
     */
    private String suitename;
    /**
     * when did the test start
     */
    private Date startTime;
    /**
     * when did the test finish
     */
    private Date finishTime;

    /**
     * store summary stats here, rather than duplicate code
     */
    private Statistics stats = new Statistics();

    /**
     * This is built up as we go along
     */
    private Element root, stdout, stderr;
    private SimpleDateFormat dateFormat;


    public AntXmlListener(String hostname,
                          File destFile,
                          String processname,
                          String suitename,
                          Date startTime) throws RemoteException {
        this.destFile = destFile;
        this.hostname = hostname;
        this.processname = processname;
        this.suitename = suitename;
        this.startTime = startTime;
    }


    /**
     * get the filename of this
     *
     * @return the filename used
     *
     * @throws RemoteException network trouble
     */
    public String getFilename() throws RemoteException {
        return destFile.getAbsolutePath();
    }

    /**
     * Open the listener. This can be a no-op, or it can open a file and throw
     * An exception on demand
     *
     * @throws IOException for IO trouble
     * @throws RemoteException network trouble
     */
    public synchronized void open() throws IOException, RemoteException {
        root = new Element(TESTSUITES);
        document = new Document(root);
        stdout = new Element(SYSTEM_OUT);
        stderr = new Element(SYSTEM_OUT);
        maybeAddAttribute(root, HOSTNAME, hostname);
        maybeAddAttribute(root, ATTR_NAME, suitename);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        dateFormat.setTimeZone(gmt);
        dateFormat.setLenient(true);
        String timestamp = dateFormat.format(startTime);
        maybeAddAttribute(root, TIMESTAMP, timestamp);
    }

    /**
     * close the file. This call must be harmless if the file is already closed
     *
     * @throws IOException IO trouble
     * @throws RemoteException network trouble
     */
    public synchronized void close() throws IOException, RemoteException {
        if (document == null) {
            return;
        }
        //this is where we actually save the file to disk.
        try {
            buildRootAttributes();
            addLogEntries();
            save();
        } finally {
            //cleanup
            document = null;
            stdout = null;
            stderr = null;
        }
    }

    /**
     * Add the statistics to the root node; replace any that existed testsuite
     * errors="0" failures="0" hostname="k2" name="org.smartfrog.services.filesystem.csvfiles.test.system.CsvReaderTest"
     * tests="6" time="20.077" timestamp="2008-04-15T09:05:50">
     */
    private synchronized void buildRootAttributes() {
        String fullname = hostname;
        if (processname != null) {
            hostname += '-' + processname;
        }
        maybeAddAttribute(root, "hostname", fullname);
        maybeAddAttribute(root, "name", suitename);
        double duration = 0;
        if (finishTime != null) {
            duration = (finishTime.getTime() - startTime.getTime()) / 1000.0;
        }
        maybeAddAttribute(root, "timestamp", dateFormat.format(startTime));
        maybeAddAttribute(root, "time", Double.toString(duration));
        addAttribute(root, "tests", stats.getTestsRun());
        addAttribute(root, "errors", stats.getErrors());
        addAttribute(root, "failures", stats.getFailures());
        addPropertiesToRoot();
    }

    /**
     * Add the properties to the root. I'm unsure about this for security reasons...maybe we will present other facts
     * under this element, to sneak them in to existing reports.
     */
    private void addPropertiesToRoot() {
        addElement(root,"properties","");
    }

    /**
     * test for the file being open
     *
     * @return true iff we are building up a document
     *
     * @throws RemoteException network trouble
     */
    public boolean isOpen() throws RemoteException {
        return document != null;
    }

    /**
     * Teased out into a class for overriding if need be
     *
     * @param out the output stream
     *
     * @return a configured serializer
     */
    protected Serializer createSerializer(OutputStream out) {
        Serializer ser = new Serializer(out);
        return ser;
    }

    /**
     * Write the document
     *
     * @throws IOException for IO trouble
     */
    protected void save() throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(destFile));
            Serializer ser = createSerializer(out);
            ser.write(document);
            out.flush();
            out.close();
            out = null;
        } finally {
            FileSystem.close(out);
        }
    }


    /**
     * end this test suite. After calling this, caller should discard all
     * references; they may no longer be valid. <i>No further methods may be
     * called</i>
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public void endSuite() throws RemoteException, SmartFrogException {
        finishTime = new Date(System.currentTimeMillis());
    }

    /**
     * A test started.
     *
     * @param test test that started
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public void startTest(TestInfo test)
            throws RemoteException, SmartFrogException {
        stats.incTestsStarted();
    }

    /**
     * An error occurred.
     *
     * @param test test that errored
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public void addError(TestInfo test)
            throws RemoteException, SmartFrogException {
        stats.incErrors();
        recordResult(ERROR, test);
    }

    /**
     * A failure occurred.
     *
     * @param test test that failed
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public void addFailure(TestInfo test)
            throws RemoteException, SmartFrogException {
        stats.incFailures();
        recordResult(FAILURE, test);
    }

    /**
     * A test ended.
     *
     * @param test test that ended
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public void endTest(TestInfo test)
            throws RemoteException, SmartFrogException {
        stats.incTestsRun();
        recordResult("end", test);
    }

    /**
     * Log an event
     *
     * @param event what happened
     *
     * @throws RemoteException on network trouble
     */
    public void log(LogEntry event) throws RemoteException {
        String text = event.logString();
        Element target;
        target = (event.getLevel() == LogEntry.LOG_LEVEL_STDERR) ? stderr : stdout;
        target.appendChild(text);
        stats.incLoggedMessages();
    }

    /**
     * Add an element to the root node
     *
     * @param name element name
     * @param text the text
     *
     * @return the element
     */
    private Element addElement(String name, String text) {
        return addElement(root, name, text);
    }

    /**
     * Add  an element
     *
     * @param parent parent element
     * @param name   element name
     * @param text   the text
     *
     * @return the element
     */
    private Element addElement(Element parent, String name, String text) {
        Element nested = new Element(name);
        nested.appendChild(text);
        if (text != null) {
            parent.appendChild(nested);
        }
        return nested;
    }

    /**
     * add an element if the text is not null
     *
     * @param parent parent element
     * @param name   element name
     * @param text   text
     *
     * @return the element or null
     */
    private Element maybeAddElement(Element parent, String name, String text) {
        if (text != null) {
            return addElement(parent, name, text);
        } else {
            return null;
        }
    }

    /**
     * Add an attribute if the value is not null. If the attribute exists, it is
     * replaced
     *
     * @param elt   element to use
     * @param name  attribute name
     * @param value attribute value (optional)
     *
     * @return the attribute or null
     */
    private Attribute maybeAddAttribute(Element elt,
                                        String name,
                                        String value) {
        if (value != null) {
            Attribute a = new Attribute(name, value);
            elt.addAttribute(a);
            return a;
        } else {
            return null;
        }
    }


    /**
     * Add an attribute. If the attribute exists, it is replaced
     *
     * @param elt   element to use
     * @param name  attribute name
     * @param value attribute value (optional)
     *
     * @return the attribute or null
     */
    private Attribute addAttribute(Element elt,
                                   String name,
                                   int value) {
        Attribute a = new Attribute(name, Integer.toString(value));
        elt.addAttribute(a);
        return a;
    }


    /* This is what a single, complex test case looks like.
    The fault's message goes in the message attribute, the nested text contains that and the stack

    <testcase classname="org.smartfrog.services.restlet.test.system.testwar.TestwarTest" name="testErrorPage" time="1.594">
       <failure message="Test failed
   (unknown) -TestCompletedEvent at Mon Dec 10 15:18:08 GMT 2007 alive: true
   status:
   Termination Record: HOST morzine:rootProcess:testErrorPage:tests,  type: abnormal,  description: error in starting next component: exception SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,    source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,    path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,    depth: 0,    Reference not found, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included,    cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,       cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,          source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,          path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,          depth: 0,          Reference not found,       SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),       primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations,       primContext: included,       reference: HOST morzine:rootProcess:testErrorPage:tests:operations,       primContext: included, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests, primContext: included, cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,    cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,    source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,    path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,    depth: 0,    Reference not found, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included,       cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,          cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,             source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,             path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,             depth: 0,             Reference not found,          SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),          primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations,          primContext: included,          reference: HOST morzine:rootProcess:testErrorPage:tests:operations,          primContext: included,    SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),    primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests,    primContext: included,    reference: HOST morzine:rootProcess:testErrorPage:tests,    primContext: included,  cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,    source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,    path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,    depth: 0,    Reference not found, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included,    cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,       cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,          source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,          path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,          depth: 0,          Reference not found,       SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),       primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations,       primContext: included,       reference: HOST morzine:rootProcess:testErrorPage:tests:operations,       primContext: included, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests, primContext: included, cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,    cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,    source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,    path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,    depth: 0,    Reference not found, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included,       cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,          cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,             source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,             path(60) ,             Reference not found,          SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),          primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations,          primContext: included,          reference: HOST morzine:rootProcess:testErrorPage:tests:operations,          primContext: included,    SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),    primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests,    primContext: included,    reference: HOST morzine:rootProcess:testErrorPage:tests,    primContext: included
   test that error page of the WAR returns the error we want,
           and that the restlet client can be controlled to ask for different error codes
   succeeded:false
   forcedTimeout:false
   skipped:falsetest that error page of the WAR returns the error we want,
           and that the restlet client can be controlled to ask for different error codes
   " type="junit.framework.AssertionFailedError">junit.framework.AssertionFailedError: Test failed
   (unknown) -TestCompletedEvent at Mon Dec 10 15:18:08 GMT 2007 alive: true
   status:
   Termination Record: HOST morzine:rootProcess:testErrorPage:tests,  type: abnormal,  description: error in starting next component: exception SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,    source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,    path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,    depth: 0,    Reference not found, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included,    cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,       cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,          source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,          path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,          depth: 0,          Reference not found,       SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),       primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations,       primContext: included,       reference: HOST morzine:rootProcess:testErrorPage:tests:operations,       primContext: included, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests, primContext: included, cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,    cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,    source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,    path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,    depth: 0,    Reference not found, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included,       cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,          cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,             source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,             path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,             depth: 0,             Reference not found,          SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),          primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations,          primContext: included,          reference: HOST morzine:rootProcess:testErrorPage:tests:operations,          primContext: included,    SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),    primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests,    primContext: included,    reference: HOST morzine:rootProcess:testErrorPage:tests,    primContext: included,  cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,    source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,    path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,    depth: 0,    Reference not found, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included,    cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,       cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,          source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,          path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,          depth: 0,          Reference not found,       SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),       primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations,       primContext: included,       reference: HOST morzine:rootProcess:testErrorPage:tests:operations,       primContext: included, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests, primContext: included, cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,    cause: SmartFrogDeploymentException: unnamed component. SmartFrogLifecycleException:: [sfStart] Failed to create a new child., cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,    source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,    path(60): HOST morzine:rootProcess:testErrorPage:tests:operations:get ,    depth: 0,    Reference not found, SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT), primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included, reference: HOST morzine:rootProcess:testErrorPage:tests:operations, primContext: included,       cause: SmartFrogLifecycleException:: [sfStart] Failed to create a new child.,          cause: SmartFrogResolutionException:: Unresolved Reference: HERE connectTimeout,             source: HOST morzine:rootProcess:testErrorPage:tests:operations:get,             path(60) ,             Reference not found,          SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),          primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests:operations,          primContext: included,          reference: HOST morzine:rootProcess:testErrorPage:tests:operations,          primContext: included,    SmartFrog 3.12.015dev (2007-12-10 14:32:19 GMT),    primSFCompleteName: HOST morzine:rootProcess:testErrorPage:tests,    primContext: included,    reference: HOST morzine:rootProcess:testErrorPage:tests,    primContext: included
   test that error page of the WAR returns the error we want,
           and that the restlet client can be controlled to ask for different error codes
   succeeded:false
   forcedTimeout:false
   skipped:falsetest that error page of the WAR returns the error we want,
           and that the restlet client can be controlled to ask for different error codes

       at org.smartfrog.test.DeployingTestBase.conditionalFail(DeployingTestBase.java:298)
       at org.smartfrog.test.DeployingTestBase.completeTestDeployment(DeployingTestBase.java:249)
       at org.smartfrog.test.DeployingTestBase.runTestsToCompletion(DeployingTestBase.java:275)
       at org.smartfrog.test.DeployingTestBase.expectSuccessfulTestRunOrSkip(DeployingTestBase.java:365)
       at org.smartfrog.services.restlet.test.system.testwar.TestwarTest.testErrorPage(TestwarTest.java:41)
   </failure>
    */
    /**
     * Record the outcome of a test
     *
     * @param type test type (used in element name of the fault)
     * @param test test result info
     */
    private void recordResult(String type, TestInfo test) {

        Element testcase = addElement(TESTCASE, null);
        maybeAddAttribute(testcase,
                ATTR_NAME,
                test.getName());
        maybeAddElement(testcase,
                "description",
                test.getDescription());
        maybeAddElement(testcase,
                HOSTNAME,
                test.getHostname());
        maybeAddElement(testcase,
                "process",
                test.getProcessName());
        maybeAddElement(testcase,
                "url",
                test.getUrl());
        //duration
        maybeAddAttribute(testcase,
                ATTR_TIME,
                "" + test.getDuration() / 1000.0);
        //also: tags, messages

        //process the fault
        ThrowableTraceInfo fault = test.getFault();
        if (fault != null) {
            Element thrown = addElement(testcase, type, fault.toString());
            maybeAddAttribute(thrown,
                    ATTR_MESSAGE,
                    fault.getMessage());
            maybeAddAttribute(thrown,
                    ATTR_TYPE,
                    fault.getClassname());
        }
    }

    /**
     * append the log elements if they are not already in the document
     */
    void addLogEntries() {
        if (stdout.getParent() == null) {
            root.appendChild(stdout);
        }
        if (stderr.getParent() == null) {
            root.appendChild(stderr);
        }
    }
}
