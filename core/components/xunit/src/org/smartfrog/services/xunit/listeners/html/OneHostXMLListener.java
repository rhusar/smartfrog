/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.xunit.listeners.html;

import org.smartfrog.services.xunit.serial.LogEntry;
import org.smartfrog.services.xunit.serial.TestInfo;
import org.smartfrog.services.xunit.serial.ThrowableTraceInfo;
import org.smartfrog.services.xunit.listeners.xml.XmlListener;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;

/**
 * This class listens to tests on a single host. The XML Listener forwards stuff
 * to it. It is not a component; it is a utility class that components use, so
 * as to log different test suites to different files.
 * <p/>
 * Important: close the file after use. The finalizer does not clean up; it
 * merely throws an assertion failure if it is called while the file is open.
 */
public class OneHostXMLListener implements XmlListener {

    /**
     * file we save to
     */
    protected File destFile;

    /**
     * name of host
     */
    protected String hostname;
    protected String processname;
    protected String suitename;
    protected Date startTime;
    protected String preamble;

    protected OutputStream outstream = null;
    protected Writer out = null;


    protected int testCount, errorCount, failureCount;

    /**
     * transient cache of tests.
     * We only really buffer during listening, but cache it in case wierd
     * race conditions or multiple sources complicate our lives.
     */
    protected HashMap<String , TestInfo > tests;

    protected static final String ENCODING = "UTF8";
    protected static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
    protected static final String ERROR_OUTPUT_FILE_OPEN = "output file still open";

    protected static final String ROOT_TAG = "testsuite";
    protected static final String ROOT_ATTRS = "";

    protected static final String ROOT_CLOSE = "</" + ROOT_TAG + ">\n";

    public static final String ERROR_LISTENER_CLOSED = "Attemped to log to a closed listener";


    /**
     * Listen to stuff coming from a single host. opens the file for future
     * messages
     *
     * @param hostname  hostname of the (possibly remote system)
     * @param processname
     * @param suitename name of the test suite
     * @param destFile  destination file
     * @param startTime timestamp (in UTC) of the start of the tests
     * @param preamble  any text to include before the root element, like PI and
     *                  comments
     * @throws IOException if there is trouble opening the file.
     */
    public OneHostXMLListener(String hostname,
                              String processname, String suitename, File destFile,
                              Date startTime,
                              String preamble) throws IOException {
        //create our new directory
        this.destFile = destFile;
        this.hostname = hostname;
        this.processname = processname;
        this.suitename = suitename;
        this.startTime = startTime;
        this.preamble = preamble;
        tests=new HashMap<String, TestInfo>();
    }

    /**
     * all the cleanup routine does is assert that we were not still open. it
     * does not close the file itself, because this should not be done in the
     * finalizer.
     *
     * @throws Throwable
     */
    protected void finalize() throws Throwable {
        assert outstream == null: ERROR_OUTPUT_FILE_OPEN;
        assert out == null: ERROR_OUTPUT_FILE_OPEN;
        super.finalize();
    }

    /**
     * open the file for writing
     *
     * @throws IOException
     */
    public void open() throws IOException {
        assert !isOpen(): "file is already open";
        destFile.getParentFile().mkdirs();
        outstream = new BufferedOutputStream(new FileOutputStream(destFile));
        out = new OutputStreamWriter(outstream, ENCODING);
        //XML declaration whose encoding had better match
        writeDocumentHeader();
        flush();
    }

    protected void writeDocumentHeader() throws IOException {
        out.write(XML_DECLARATION);
        out.write("\n");
        //preamble if supplied
        if (preamble != null) {
            out.write(preamble);
            out.write("\n");
        }
        //write the root tag
        out.write("<");
        out.write(ROOT_TAG);
        out.write(" " + ROOT_ATTRS + "\n");
        out.write(attr("hostname", hostname));
        out.write(attr("suitename", suitename));
        out.write(attr("utc", startTime.getTime()));
        out.write(attr("started", startTime.toString()));
        out.write(">\n");
    }


    /**
     * close the file
     *
     * @throws IOException IO trouble
     */
    public void close() throws IOException {
        if (!isOpen()) {
            //harmless to close an already closed file.
            return;
        }

        writeDocumentTail();

        try {
            out.close();
        } catch (Exception ignored) {

        }
        try {
            outstream.close();
        } catch (IOException ignored) {

        }
        out = null;
        outstream = null;
        destFile = null;
    }

    /**
     * Write the tail of the document out
     * @throws IOException  IO trouble
     */
    protected void writeDocumentTail() throws IOException {
        write("summary",
                attr("tests", testCount)
                +
                attr("failures", failureCount)
                + attr("errors", errorCount),
                null, false);

        out.write(ROOT_CLOSE);
    }

    /**
     * check for errors
     *
     * @return true iff we have an output stream and it is open
     */
    public boolean isHappy() {
        return out != null;
    }

    /**
     * test for the file being open
     *
     * @return true iff the file is not null
     */
    public boolean isOpen() {
        return out != null;
    }

    /**
     * Flush the output stream. Harmless if the file is closed.
     * @throws IOException  IO trouble
     */
    protected void flush() throws IOException {
        if(isOpen())
            out.flush();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return destFile != null ? destFile.toString() : super.toString();
    }


    /**
     * end this test suite. After calling this, caller should discard all
     * references; they may no longer be valid. <i>No further methods may be
     * called</i>
     * @throws SmartFrogException containing a nested IOException
     */
    public void endSuite() throws SmartFrogException {
        try {
            close();
            tests=null;
        } catch (IOException e) {
            throw SmartFrogException.forward(e);
        }
    }

    /**
     * An error occurred.
     */
    public void addError(TestInfo test) {
        errorCount++;
        tests.put(test.getText(),test);
    }

    /**
     * A failure occurred.
     */
    public void addFailure(TestInfo test)  {
        failureCount++;
        tests.put(test.getText(), test);
    }

    /**
     * A test ended.
     */
    public void endTest(TestInfo test) throws SmartFrogException {
        testCount++;
        TestInfo cached=tests.get(test.getText());
        String type;
        if(cached==null) {
            type="pass";
        } else {
            //it was a failure of some type. Lets copy the
            //duration info and and set the type
            //of the element appopriately.
            type="fail";
            cached.setEndTime(test.getEndTime());
            test=cached;
        }
        add(type,test);
    }

    /**
     * A test started.
     */
    public void startTest(TestInfo test) {
        //do nothing
    }

    /**
     * Log an event
     * @param event
     * @throws RemoteException
     */
    public void log(LogEntry event) throws RemoteException {
        String tag="log-"+event.levelToText();
        String attrs;
        attrs= attr("t",event.getTimestamp())+
                attr("h",event.getHostname());
        try {
            write(tag,attrs,event.getText(), true);
        } catch (IOException e) {
            throw new RemoteException("wrapped fault",e);

        }
    }

    /**
     * add a test.
     *
     * @param tag  element name
     * @param test test result to log
     */
    protected void add(String tag, TestInfo test) throws SmartFrogException {
        if (!isOpen()) {
            //bail out on a closed operation
            throw new SmartFrogException(ERROR_LISTENER_CLOSED);
        }
        String entry = toXML(tag, test);
        try {
            out.write(entry);
        } catch (IOException e) {
            throw SmartFrogException.forward(e);
        }
    }


    /**
     * Write a tag with a string body
     * This triggers a flush of the file, so the
     * file is saved as we go along. 
     * @param tag   element name
     * @param attrs attributes (can be null)
     * @param body  text body (can be null)
     */
    protected void write(String tag,
            String attrs,
            String body,
            boolean escape) throws IOException {
        String x = element(tag, attrs, body, escape);
        out.write(x);
        out.flush();
    }


    /**
     * nest a string into XML
     *
     * @param tag element name
     * @param attrs attribute string (can be null)
     * @param body body (can be null)
     * @param escape true if the body should be escaped
     * @return a new element
     */
    protected String element(String tag, String attrs, String body, boolean escape) {
        StringBuffer buf = new StringBuffer();
        buf.append('<');
        buf.append(tag);
        if (attrs != null) {
            buf.append(" ");
            buf.append(attrs);
            buf.append(" ");
        }
        if (body == null) {
            buf.append("/>\n");
        } else {
            buf.append(">\n");
            if (escape) {
                buf.append(escape(body, false));
            } else {
                buf.append(body);
            }
            buf.append("\n</");
            buf.append(tag);
            buf.append(">\n");
        }
        return buf.toString();
    }


    /**
     * create an attribute. The string is escaped.
     *
     * @param name attribute name
     * @param value attribute value
     * @return a new attribute/value assignment string
     */
    protected String attr(String name, String value) {
        if(value==null) {
            return name+"=\"\"";
        } else {
            return name + "=\"" + escape(value, true) + "\" ";
        }
    }


    /**
     * create an attribute
     *
     * @param name attribute name
     * @param value attribute value
     * @return a new attribute
     */
    protected String attr(String name, long value) {
        return name + "=\"" + Long.toString(value) + "\" ";
    }

    /**
     * escape a string
     *
     * @param text         text to escape
     * @param doublequotes flag to escape double quotes (for attributes)
     * @return an escaped string
     */
    protected String escape(String text, boolean doublequotes) {
        if (text == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(text.length());
        for (int i = 0; i < text.length(); i++) {
            buf.append(escapeChar(text.charAt(i), doublequotes));
        }
        return buf.toString();
    }

    /**
     * escape a single char
     *
     * @param c character in
     * @param doublequotes should doublequotes be escaped?
     * @return a possibly escaped char sequence
     */
    protected String escapeChar(char c, boolean doublequotes) {
        switch (c) {
            case '"':
                return doublequotes ? "&quot;" : "\"";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '&':
                return "&amp;";
                //cr, lf and tab
            case '\n':
                return "\n";
            case '\r':
                return "\r";
            case '\t':
                return "\t";
            default:
                if (c < 32) {
                    //illegal
                    return "#";
                }
                return Character.toString(c);
        }
    }

    /**
     * XML-ify
     *
     * @param tag tag to use for the test
     * @param test the test
     * @return the XML version of the test
     */
    protected String toXML(String tag, TestInfo test) {
        String body = element("text", null,test.getText(),true);
        if(test.getFault()!=null) {
            body=body+'\n'+toXML(test.getFault());
        }
        String classname = attr("classname", test.getName());
        String duration = attr("duration", Long.toString(test.getDuration()));
        return element(tag,
                classname + " " + duration,
                body,
                false);
    }


    /**
     * return a fault tag
     *
     * @param fault the cause of the failure
     * @return empty string if fault is null, else an xml declaration
     */
    protected String toXML(ThrowableTraceInfo fault) {
        String result;
        if (fault == null) {
            result="";
        } else {
            StringBuffer buf = new StringBuffer();
            String cause = toXML(fault.getCause());
            String classname = element("classname", null, fault.getClassname(), true);
            String message = element("message", null, fault.getMessage(), true);
            String localmessage = element("localizedMessage",
                    null,
                    fault.getLocalizedMessage(),
                    true);
            StackTraceElement[] stack = fault.getStack();
            StringBuffer stackTrace = new StringBuffer();
            for (StackTraceElement frame : stack) {
                StringBuffer attrs = new StringBuffer();
                attrs.append(attr("classname", frame.getClassName()));
                attrs.append(attr("method", frame.getMethodName()));
                attrs.append(attr("file", frame.getFileName()));
                attrs.append(attr("line", Integer.toString(frame.getLineNumber())));
                attrs.append(attr("native", Boolean.toString(frame.isNativeMethod())));
                stackTrace.append(element("frame", attrs.toString(), null, false));
            }
            buf.append(classname);
            buf.append(message);
            buf.append(localmessage);
            buf.append(element("stack", null, stackTrace.toString(), false));
            buf.append(cause);
            result=element("fault", null, buf.toString(), false);
        }
        return result;
    }

    /**
     * equality test does hostname and dir
     *
     * @param that other instance
     * @return true for a match
     */
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof OneHostXMLListener)) {
            return false;
        }

        final OneHostXMLListener oneHostXMLListener = (OneHostXMLListener) that;

        if (!destFile.equals(oneHostXMLListener.destFile)) {
            return false;
        }

        return true;
    }

    /**
     * hashcode is hashcode of the filename
     *
     * @return a hash code
     */
    public int hashCode() {
        return destFile.toString().hashCode();
    }

    /**
     * get the filename of this
     *
     * @return the filename used
     */
    public String getFilename() {
        return destFile.getAbsolutePath();
    }

    /**
     * Write a line
     * @param message text message
     * @throws IOException io trouble
     */
    protected void writeln(String message) throws IOException {
        out.write(message);
        out.write('\n');
    }

    /**
     * Enter an element
     * @param element name
     * @throws IOException io trouble
     */
    protected void enter(String element) throws IOException {
        enter(element,null);
    }

    /**
     * enter an element
     * @param element element name
     * @param attributes attribute string
     * @throws IOException IO trouble
     */
    protected void enter(String element,String attributes) throws IOException {
        if(attributes!=null) {
            writeln("<"+element+" "+attributes+">");
        } else {
            writeln("<"+element + ">");
        }
    }

    /**
     * Exit a named element
     * @param element name
     * @throws IOException io trouble
     */
    protected void exit(String element) throws IOException {
        writeln("</" + element + ">");
    }

}
