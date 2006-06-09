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

import org.smartfrog.services.junit.data.LogEntry;
import org.smartfrog.services.junit.data.TestInfo;
import org.smartfrog.services.junit.data.ThrowableTraceInfo;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.rmi.RemoteException;

/**
 * An extension of the OneHostXMLListener that puts out XHTML instead of XML
 * created 08-Jun-2006 13:22:19
 */

public class OneHostHtmlListener extends OneHostXMLListener {

    private String cssURL;

    public OneHostHtmlListener(String title,
                               File destFile,
                               String suitename,
                               Date startTime,
                               String preamble,
                               String cssURL)
            throws IOException {
        super(title, destFile, suitename, startTime, preamble);
        this.cssURL=cssURL;
    }

    private String getTitle() {
        return super.hostname;
    }

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
        String title = getTitle()
                + " suite " + suitename
                + " started " + startTime.toString();
        enter("head");
        write("title", null, title, true);
        if(cssURL!=null) {
            write("link",
                    attr("rel","stylesheet")
                    + attr("href", cssURL)
                    + attr("type", "text/css"),
                    null,
                    false);
        }
        exit("head");
        enter("body");
        write("h1", null, title, true);
    }

    protected void writeDocumentTail() throws IOException {
        writeSummary();


        exit("body");
        exit("html");
    }

    private void writeSummary() throws IOException {
        enter("div",style("summary") + attr("id", "summary"));
        write("span",style("summary-span"),"Test Summary",false);
        enter("table", style("summary-table"));
        enter("th");
        write("td", null, "category", false);
        write("td", null, "count", false);
        exit("th");

        enter("tr");
        write("td",null,"tests",false);
        write("td", null, Integer.toString(testCount), false);
        exit("tr");

        enter("tr");
        write("td", null, "failures", false);
        write("td", null, Integer.toString(failureCount), false);
        exit("tr");

        enter("tr");
        write("td", null, "errors", false);
        write("td", null, Integer.toString(errorCount), false);
        exit("tr");

        exit("table");
        exit("div");
    }

    public void log(LogEntry event) throws RemoteException {

        String type="log-"+event.levelToText();
        try {
            write("div",style(type), event.getText(), true);
        } catch (IOException e) {
            throw new RemoteException("wrapped fault",e);

        }
    }

    /**
     * Print a p paragraph in a style
     *
     * @param style
     * @param text
     */
    protected String div(String style, String text) {
        return div(style,null, text, true);
    }

    protected String div(String style, String attrs,String text,boolean escape) {
        String attributes = style(style);
        if(attrs!=null) {
            attributes+=attrs;
        }
        return element("div", attributes, text, escape);
    }

    protected String style(String style) {
        return attr("class", style);
    }

    protected String toXML(String tag, TestInfo test) {
        StringBuffer body=new StringBuffer();
        body.append(div(test.getOutcome(),
                test.getClassname()));
        body.append(div("test-duration","duration " +
                ""+ test.getDuration()/1000.0
                +"s"
                ));
        body.append(div("test-text",test.getText()));
        if(test.getFault()!=null) {
            body.append(toXML(test.getFault()));
        }
        return body.toString();
    }




    /**
    * return a fault tag
    *
    * @param fault
    * @return empty string if fault is null, else an xml declaration
    */
    protected String toXML(ThrowableTraceInfo fault) {
        String result;
        if (fault == null) {
            result = "";
        } else {
            StringBuffer buf = new StringBuffer();
            enter(buf,"div", style("faultblock"));
            buf.append(div("fault", "Exception:"+fault.getClassname()));
            buf.append(div("fault-message", fault.getMessage()));
            StackTraceElement[] stack = fault.getStack();
            for (int i = 0; i < stack.length; i++) {
                StackTraceElement frame = stack[i];
                buf.append(div("fault-frame", frame.toString()));
            }
            buf.append(toXML(fault.getCause()));
            exit(buf,"div");
            buf.append("</div>\n");
            result = buf.toString();
        }
        return result;
    }

    protected void enter(StringBuffer buf, String element, String attrs) {
        buf.append("<");
        buf.append(element);
        if(attrs!=null) {
            buf.append(' ');
            buf.append(attrs);
        }
        buf.append(">\n");
    }

    protected void exit(StringBuffer buf, String element) {
        buf.append("</");
        buf.append(element);
        buf.append(">\n");
    }
}
