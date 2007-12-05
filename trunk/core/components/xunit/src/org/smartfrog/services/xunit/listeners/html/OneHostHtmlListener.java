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
package org.smartfrog.services.xunit.listeners.html;

import org.smartfrog.services.xunit.serial.LogEntry;
import org.smartfrog.services.xunit.serial.TestInfo;
import org.smartfrog.services.xunit.serial.ThrowableTraceInfo;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * An extension of the OneHostXMLListener that puts out XHTML instead of XML
 * created 08-Jun-2006 13:22:19
 */

public class OneHostHtmlListener extends OneHostXMLListener {

    private String cssURL;

    private String cssData;

    private String title;

    public OneHostHtmlListener(String title,
                               String hostname,
                               String processname,
                               File destFile,
                               String suitename,
                               Date startTime,
                               String preamble,
                               String cssURL, String cssData)
            throws IOException {
        super(hostname, processname, suitename, destFile, startTime, preamble);
        this.title=title;
        this.cssURL=cssURL;
        this.cssData=cssData;

    }

    private String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     * @throws IOException IO trouble
     */
    protected void writeDocumentHeader() throws IOException {
        writeln(XML_DECLARATION);
        //a strict HTML 1.1 document
        writeln("<!DOCTYPE\n"
                + " html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n"
                + " \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        //preamble if supplied
        if (preamble != null) {
            writeln(preamble);
        }
        //write the root tag
        writeln("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
        String fullTitle = getTitle()
                + " suite " + suitename
                + " on " +hostname
                + " started " + startTime.toString();
        enter("head");
        write("title", null, fullTitle, true);
        if(cssURL!=null && cssURL.length()>0) {
            write("link",
                    attr("rel","stylesheet")
                    + attr("href", cssURL)
                    + attr("type", "text/css"),
                    null,
                    false);
        } else {
            //no url, pump out the data if it is present
            if(cssData!=null && cssData.length()>0) {
                write("style",
                        attr("type", "text/css"),
                        cssData,
                        true);
            }

        }
        exit("head");
        enter("body");
        write("h1", style("title"), fullTitle, true);
        enter("div",style("toc"));
        write("a", "href='#summary'", "summary", false);
        exit("div");
    }

    /**
     * {@inheritDoc}
     * @throws IOException IO trouble
     */

    protected void writeDocumentTail() throws IOException {
        writeSummary();


        exit("body");
        exit("html");
    }

    /**
     * {@inheritDoc}
     * @throws IOException IO trouble
     */

    private void writeSummary() throws IOException {
        enter("div",style("summary") + attr("id", "summary"));
        writeln(div("summary-title","Test Summary"));
        enter("table", style("summary-table"));

        enter("tr");
        write("td",null,"Tests",false);
        write("td", null, Integer.toString(testCount), false);
        exit("tr");

        int successes=testCount-errorCount-failureCount;
        int percentage=0;
        if(testCount>0) {
            percentage = (successes*100)/testCount;
        }
        enter("tr");
        write("td", style("td-success"), "Successes", false);
        write("td", null, Integer.toString(successes), false);
        exit("tr");

        enter("tr");
        write("td", style("td-success"), "Percentage Successes", false);
        write("td", null, Integer.toString(percentage), false);
        exit("tr");

        enter("tr");
        write("td", style("td-failures"), "Failures", false);
        write("td", null, Integer.toString(failureCount), false);
        exit("tr");

        enter("tr");
        write("td", style("td-errors"), "Errors", false);
        write("td", null, Integer.toString(errorCount), false);
        exit("tr");


        enter("tr");
        write("td", null, "Started", false);
        write("td", null, startTime.toString(), true);
        exit("tr");

        enter("tr");
        write("td", null, "Finished", false);
        write("td", null, startTime.toString(), true);
        exit("tr");


        enter("tr");
        write("td", null, "Host", false);
        write("td", null, hostname, true);
        exit("tr");

        exit("table");
        exit("div");
    }

    /**
     * {@inheritDoc}
     * @param event event
     * @throws RemoteException network trouble
     */
    public void log(LogEntry event) throws RemoteException {

        String type = style(event);
        try {
            write("div",style(type), event.getText(), true);
        } catch (IOException e) {
            throw new RemoteException("wrapped fault",e);

        }
    }

    private String style(LogEntry event) {
        String type="log-"+event.levelToText();
        return type;
    }

    /**
     * create a division; escape the text
     *
     * @param style style of the division
     * @param text text to write
     */
    protected String div(String style, String text) {
        return div(style,null, text, true);
    }

    /**
     * create a division; escape the text
     *
     * @param style style of the division
     * @param attrs optional list of attributes
     * @param text text to write
     * @param escape does the next need escaping
     * @return the division
     */
    protected String div(String style, String attrs,String text,boolean escape) {
        String attributes = style(style);
        if(attrs!=null) {
            attributes+=attrs;
        }
        return element("div", attributes, text, escape);
    }

    /**
     * Create a style attribute
     * @param style style name
     * @return a string of the form class="style"
     */
    protected String style(String style) {
        return attr("class", style);
    }

    /**
     * {@inheritDoc}
     * @param tag element name
     * @param test test
     * @return xml variant
     */
    protected String toXML(String tag, TestInfo test) {
        StringBuffer body=new StringBuffer();
        enter(body,"div",style("testblock"));
        body.append(div(test.getOutcome(),
                test.getName()));
        body.append(div("test-duration","duration " +
                ""+ test.getDuration()/1000.0
                +"s"
                ));
        body.append(div("test-text",test.getText()));
        if(test.getFault()!=null) {
            body.append(toXML(test.getFault()));
        }
        //now do the log
        for(LogEntry entry:test.getMessages()) {
            String log="["+entry.levelToText()+"]"
                    +entry.getText();
            body.append(div(style(entry),log));
        }
        //exit
        exit(body, "div");
        return body.toString();
    }




    /**
    * return a fault tag
    *
    * @param fault fault cause
    * @return empty string if fault is null, else an xml declaration
    */
    protected String toXML(ThrowableTraceInfo fault) {
        String result;
        if (fault == null) {
            result = "";
        } else {
            StringBuffer buf = new StringBuffer();
            enter(buf,"div", style("faultblock"));
            enter(buf,"table",null);
            enter(buf, "tr", null);
            enter(buf, "td", style("fault"));
            buf.append("Exception");
            exit(buf,"td");
            enter(buf, "td", style("fault"));
            buf.append(fault.getClassname());
            exit(buf, "td");
            exit(buf, "tr");
            enter(buf, "tr", null);
            enter(buf, "td", style("fault-message"));
            buf.append("Message");
            exit(buf, "td");
            enter(buf, "td", style("fault-message"));
            buf.append(escape(fault.getMessage(),false));
            exit(buf, "td");
            exit(buf, "tr");
            enter(buf, "tr", null);
            enter(buf, "td", attr("colspan","2"));
            StackTraceElement[] stack = fault.getStack();
            for (StackTraceElement frame : stack) {
                buf.append(div("fault-frame",
                        escape(frame.toString(), false)));
            }
            exit(buf, "td");
            exit(buf, "tr");
            exit(buf, "table");
            //stop recursive output with a low level pointer equality test
            if(fault.getCause()!=null && fault.getCause()!=fault) {
                buf.append(toXML(fault.getCause()));
            }
            exit(buf,"div");
            result = buf.toString();
        }
        return result;
    }

    /**
     * Enter an element
     * @param buf buffer
     * @param element element name
     * @param attrs attributes
     */
    protected void enter(StringBuffer buf, String element, String attrs) {
        buf.append("<");
        buf.append(element);
        if(attrs!=null) {
            buf.append(' ');
            buf.append(attrs);
        }
        buf.append(">\n");
    }

    /**
     * Exit an element
     * @param buf buffer 
     * @param element element name
     */
    protected void exit(StringBuffer buf, String element) {
        buf.append("</");
        buf.append(element);
        buf.append(">\n");
    }
}
