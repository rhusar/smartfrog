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


package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.junit.TestInfo;
import org.smartfrog.services.junit.ThrowableTraceInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * This class listens to tests on a single host. The XML Listener forwards stuff
 * to it. It is not a component; it is a utility class that components use, so
 * as to field requests from multiple servers Date: 12-Jun-2004 Time: 00:28:44
 */
public class OneHostXMLListener implements XmlListener {

    /**
     * file we save to
     */
    private File destFile;

    /**
     * name of host
     */
    private String hostname;


    private OutputStream out = null;
    private PrintWriter xmlfile = null;

    /**
     * cache of last test failed; this is so that a failure will be logged as
     * such, not as a success
     */
    private String lastTestFailed;

    private int testCount, errorCount, failureCount;
    protected static final String ENCODING = "UTF8";
    protected static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
    protected static final String ERROR_OUTPUT_FILE_OPEN = "output file still open";

    protected static final String ROOT_TAG = "testsuite";
    protected static final String ROOT_ATTRS = "";
    protected static final String ROOT_OPEN = "<" +
            ROOT_TAG +
            " " +
            ROOT_ATTRS +
            " >\n";
    protected static final String ROOT_CLOSE = "</" + ROOT_TAG + ">\n";


    /**
     * Listen to stuff coming from a single host.
     *
     * @param hostname
     * @param dir      directory to save to
     */
    public OneHostXMLListener(String hostname, File dir) {
        assert hostname != null;
        assert dir != null;
        //create our new directory
        destFile = new File(dir, hostname + ".xml");
        destFile.mkdirs();
        this.hostname = hostname;
    }

    /**
     * all the cleanup routine does is assert that we were not still open. it
     * does not close the file itself, because this should not be done in the
     * finalizer.
     *
     * @throws Throwable
     */
    protected void finalize() throws Throwable {
        assert out == null: ERROR_OUTPUT_FILE_OPEN;
        assert xmlfile == null: ERROR_OUTPUT_FILE_OPEN;
    }

    public void open() throws IOException {
        assert out == null;
        assert xmlfile == null;
        out = new BufferedOutputStream(new FileOutputStream(destFile));
        xmlfile = new PrintWriter(new OutputStreamWriter(out, ENCODING));
        xmlfile.write(XML_DECLARATION);
        xmlfile.write(ROOT_OPEN);
    }


    public void close() throws IOException {
        assert out != null;
        assert xmlfile != null;

        write("summary",
                a("tests", testCount)
                +
                a("failures", failureCount)
                + a("errors", errorCount),
                null, false);

        xmlfile.write(ROOT_CLOSE);

        // flush and check for a fault
        if (xmlfile.checkError()) {
            throw new IOException("Error while writing " + destFile);
        }
        try {
            xmlfile.close();
        } catch (Exception e) {

        }
        try {
            out.close();
        } catch (IOException e) {

        }
    }


    /**
     * An error occurred.
     */
    public void addError(TestInfo test) throws RemoteException {
        errorCount++;
        add("error", test);
    }

    /**
     * A failure occurred.
     */
    public void addFailure(TestInfo test) throws RemoteException {
        failureCount++;
        add("failure", test);
    }

    /**
     * A test ended.
     */
    public void endTest(TestInfo test) throws RemoteException {
        testCount++;
        if (!test.getClassname().equals(lastTestFailed)) {
            add("pass", test);
        } else {
            //do nothing, we have already processed this body
        }
    }

    /**
     * A test started.
     */
    public void startTest(TestInfo test) throws RemoteException {
        //do nothing
    }

    /**
     * add a test. We work out from the test info whether or not it is a fault;
     * if it is we set the {@link #lastTestFailed} to the name of the test
     * (otherwise it is cleared)
     *
     * @param tag  element name
     * @param test test result to log
     */
    protected void add(String tag, TestInfo test) {
        boolean success = test.hasFault();
        if (!success) {
            lastTestFailed = test.getClassname();
        } else {
            lastTestFailed = null;
        }
        String entry = toXML(tag, test);
        xmlfile.write(entry);
    }


    /**
     * write a tag with a string body
     *
     * @param tag   element name
     * @param attrs attributes (can be null)
     * @param body  text body (can be null)
     */
    protected void write(String tag,
            String attrs,
            String body,
            boolean escape) {
        String x = x(tag, attrs, body, escape);
        xmlfile.write(x);
    }


    /**
     * nest a string into XML
     *
     * @param tag
     * @param attrs
     * @param body
     * @param escape
     * @return
     */
    protected String x(String tag, String attrs, String body, boolean escape) {
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
     * create an attr
     *
     * @param name
     * @param value
     * @return
     */
    protected String a(String name, String value) {
        return name + "=\"" + escape(value, true) + "\" ";
    }


    /**
     * create an attr
     *
     * @param name
     * @param value
     * @return
     */
    protected String a(String name, long value) {
        return name + "=\"" + Long.toString(value) + "\" ";
    }

    /**
     * escape a string
     *
     * @param text         text to escape
     * @param doublequotes flag to escape double quotes (for attributes)
     * @return
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
     * @param c
     * @param doublequotes
     * @return
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
     * @param tag
     * @param test
     * @return
     */
    protected String toXML(String tag, TestInfo test) {
        StringBuffer buf = new StringBuffer();
        String fault = toXML(test.getFault());
        String classname = a("classname", test.getClassname());
        String duration = a("duration", Long.toString(test.getDuration()));
        return x(tag,
                classname + " " + duration,
                fault,
                false);
    }


    /**
     * return a fault tag
     *
     * @param fault
     * @return empty string if fault is null, else an xml declaration
     */
    protected String toXML(ThrowableTraceInfo fault) {
        if (fault == null) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        String cause = toXML(fault.getCause());
        String classname = x("classname", null, fault.getClassname(), true);
        String message = x("message", null, fault.getMessage(), true);
        String localmessage = x("localizedMessage",
                null,
                fault.getLocalizedMessage(),
                true);
        StackTraceElement[] stack = fault.getStack();
        StringBuffer stackTrace = new StringBuffer();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement frame = stack[i];
            StringBuffer attrs = new StringBuffer();
            attrs.append(a("classname", frame.getClassName()));
            attrs.append(a("method", frame.getMethodName()));
            attrs.append(a("file", frame.getFileName()));
            attrs.append(a("line", Integer.toString(frame.getLineNumber())));
            attrs.append(a("native", Boolean.toString(frame.isNativeMethod())));
            stackTrace.append(x("frame", attrs.toString(), null, false));
        }
        buf.append(classname);
        buf.append(message);
        buf.append(localmessage);
        buf.append(x("stack", null, stackTrace.toString(), false));
        buf.append(cause);
        return x("fault", null, buf.toString(), false);
    }

    /**
     * equality test does hostname and dir
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OneHostXMLListener)) {
            return false;
        }

        final OneHostXMLListener oneHostXMLListener = (OneHostXMLListener) o;

        if (!hostname.equals(oneHostXMLListener.hostname)) {
            return false;
        }
        if (!destFile.equals(oneHostXMLListener.destFile)) {
            return false;
        }

        return true;
    }

    /**
     * hashcode is hashcode of hostname. Lets us search using the hashcode as
     * the 1ary key.
     *
     * @return
     */
    public int hashCode() {
        return hostname.hashCode();
    }
}
