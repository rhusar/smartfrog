/** (C) Copyright 2004-2007 Hewlett-Packard Development Company, LP

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

import nu.xom.Document;
import nu.xom.Element;
import org.smartfrog.services.xunit.listeners.xml.FileListener;
import org.smartfrog.services.xunit.serial.TestInfo;
import org.smartfrog.services.xunit.serial.LogEntry;
import org.smartfrog.services.xunit.serial.ThrowableTraceInfo;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import nu.xom.Attribute;
import org.smartfrog.services.xunit.serial.Statistics;

/**
 * This class listens to tests on a single host. The XML Listener forwards stuff
 * to it. It is not a component; it is a utility class that components use, so
 * as to log different test suites to different files.
 * <p/>
 * The way the Ant tasks work is they use Xom
 */
public class SingleProcessAntXmlListener implements FileListener,XMLConstants {

    private Document document;

    /**
     * file we save to
     */
    private File destFile;
    private String filename;

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
     * store summary stats here, rather than duplicate code
     */
    Statistics stats=new Statistics();
    
    /**
     * This is built up as we go along
     */
    private Element root;
    
    private List<LogEntry> logEntries;

    public SingleProcessAntXmlListener(String hostname,
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
        document=new Document(root);
        logEntries=new ArrayList<LogEntry>();
        maybeAddAttribute(root, XMLConstants.HOSTNAME, hostname);
        maybeAddAttribute(root, XMLConstants.ATTR_NAME, suitename);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        sdf.setTimeZone(gmt);
        sdf.setLenient(true);
        String timestamp = sdf.format(startTime);
        maybeAddAttribute(root, XMLConstants.TIMESTAMP, timestamp);
    }

    /**
     * close the file. This call  must be harmless if the file is already closed
     *
     * @throws IOException IO trouble
     * @throws RemoteException network trouble
     */
    public synchronized void close() throws IOException, RemoteException {
        if(document==null) {
            return;
        }
        //this is where we actually save the file to disk.
        try {
            
            //TODO: save the file
            
        } finally {
            //cleanup
            document=null;
            logEntries=null;
        }
        
    }

    /**
     * test for the file being open
     *
     * @return true iff we are building up a document
     *
     * @throws RemoteException network trouble
     */
    public boolean isOpen() throws RemoteException {
        return document!=null;
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
        recordResult(XMLConstants.ERROR, test);
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
        recordResult(XMLConstants.FAILURE, test);
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
        logEntries.add(event);
        stats.incLoggedMessages();
    }
    
    private Element addElement(String name, String output) {
        return addElement(root,name,output);
    }
    
     private Element addElement(Element parent,String name, String output) {
        Element nested = new Element(name);
        nested.appendChild(output);
        if(output!=null) {
            parent.appendChild(nested);
        }
        return nested;
    }
     
     private Element maybeAddElement(Element parent,String name, String text) {
        if(text!=null) {
            Element nested = new Element(name);
            nested.appendChild(text);
            parent.appendChild(nested);
            return nested;
        } else {
            return null;
        }
    }
    
    
    private Attribute maybeAddAttribute(Element elt, String name, String value) {
        if(value!=null) {
            Attribute a=new Attribute(name,value);
            elt.addAttribute(a);
            return a;
        } else {
            return null;
        }
    }

    /**
     * Record the outcome of a test
     * @param type test type (used in element name of the fault)
     * @param test test result info
     */
    private void recordResult(String type, TestInfo test) {

        Element testcase = addElement(TESTCASE, null);
        maybeAddAttribute(testcase, 
                XMLConstants.ATTR_NAME,
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
                XMLConstants.ATTR_TIME,
                "" + test.getDuration() / 1000.0);
        //also: tags, messages

      
        //process the fault
        ThrowableTraceInfo fault = test.getFault();
        if (fault != null) {
            StackTraceElement[] stack = fault.getStack();
            StringBuilder trace = new StringBuilder();
            for (StackTraceElement elt : stack) {
                trace.append(elt.toString());
                trace.append('\n');
            }
            Element thrown = addElement(testcase, type, trace.toString());
            maybeAddAttribute(thrown, 
                    XMLConstants.ATTR_MESSAGE, 
                    fault.getMessage());
            maybeAddAttribute(thrown, 
                    XMLConstants.ATTR_TYPE, 
                    fault.getClassname());
        }
    }    

}
