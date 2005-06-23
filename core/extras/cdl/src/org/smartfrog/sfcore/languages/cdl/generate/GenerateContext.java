/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.generate;

import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.languages.cdl.Constants;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.util.Formatter;
import java.util.HashMap;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/**
 * This file handles the generation
 * created 23-Jun-2005 11:06:50
 */

public class GenerateContext {

    /**
     * output stream
     */
    private PrintWriter out;

    private File destFile;

    private int depth=0;

    private String prefix="";

    private Formatter formatter;

    private HashMap<String,String> names;

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * the default charset for this doc is defined here.
     * {@value}
     */
    public static final String DEFAULT_CHARSET = "US-ASCII";
    public static final String COMPONENT_SFSYSTEM = "sfSystem";

    /**
     * name of the dest file
     */
    private String destFilename;
    public static final int INDENT = 1;
    public static final String COMPONENT_CONFIGURATION = "configuration";

    public GenerateContext() {
        initHashMap();
    }


    protected void initHashMap() {
        names = new HashMap<String, String>();
        names.put(Constants.XML_CDL_NAMESPACE,"cdl");
        names.put(Constants.CMP_NAMESPACE, "cmp");
    }

    public void begin(File dest) throws IOException {
        destFile=dest;
        destFilename = destFile.getAbsolutePath();
        out = new PrintWriter(destFile, DEFAULT_CHARSET);
        formatter=new Formatter(out);
        setDepth(0);
    }

    /**
     * End generation
     * @throws IOException
     */
    public void end() throws IOException {
        boolean hadError=out.checkError();
        close();
        if(hadError) {
            reportError();
        }
    }


    /**
     * Error check.
     * @throws IOException
     */
    public void checkForErrors() throws IOException {

        if (out!=null && out.checkError()) {
            reportError();
        }
    }

    /**
     * report any errors on the output stream
     * @throws IOException
     */
    private void reportError() throws IOException {

        throw new IOException("Something went wrong with writing to " +
                destFilename);
    }

    /**
     * close the file; print and swallow any exception
     */
    public void close() {
        if (out != null) {
            try {
                out.close();
                out=null;
            } catch (Exception e) {
                log.warn("when closing " + destFile, e);
            }
        }
    }


    /**
     * set the depth of nesting, and create an appropriate
     * prefix line
     * @param value
     */
    private void setDepth(int value) {
        depth=value;
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<depth;i++) {
            builder.append(" ");
        }
        prefix=builder.toString();
    }

    public void enter(String componentName,String extending) {
        String text=componentName+" extends "+extending+" {";
        println(text);
        setDepth(depth+INDENT);
    }

    public void enter(String componentName) {
        String text = componentName + " {";
        println(text);
        setDepth(depth + INDENT);
    }

    public void leave() {
        setDepth(depth - INDENT);
        println("}");
    }

    public void println(String line) {
        out.print(prefix);
        out.println(line);
    }

    public void println() {
        out.println();
    }

    /**
     * varargs println
     * @param format
     * @param args
     */
    public void println(String format,Object ... args) {
        out.print(prefix);
        formatter.format(format,args);
        formatter.flush();
        out.println();
    }

    public void printTuple(String name,String value) {
        println("%s \"%s\"",name,value);
    }

    public void printImport(String name) {
        println("#import  \"%s\"", name);
    }



    /**
     * Get the attribute name
     *
     * @param attr
     * @return TODO: add namespace support
     */
    String convertAttributeName(Attribute attr) {
        return "a_" + attr.getLocalName();
    }

    public String namespaceToPrefix(String namespace,String prefix) {
        if("".equals(namespace)) {
            return "";
        }
        //look for a mapping
        String mapping = names.get(namespace);
        if(mapping!=null) {
            return mapping;
        }
        //now an empty node
        if(prefix!=null) {
            mapping = sanitize(prefix);
            if(names.get(mapping)!=null) {
                mapping= makeUniquePrefix(prefix);
            }
        } else {
            mapping=makeUniquePrefix(DEFAULT_PREFIX);
        }
        names.put(namespace, mapping);
        return mapping;
    }

    public static final String DEFAULT_PREFIX="p";

    private String makeUniquePrefix(String start) {
        int count=0;
        String newstr;
        do {
            newstr=start+count;
            count++;
        } while(names.get(newstr) == null);
        return newstr;
    }

    private static final String INVALID =
            " /#-$\\";
    private static final String VALID =
            ".......";


    /**
     * turn a potentially dirty string into a safe one
     * @param input
     * @return
     */
    public String sanitize(CharSequence input) {
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<input.length();i++) {
            char c=input.charAt(i);
            int index=INVALID.indexOf(c);
            if(index>=0) {
                c=VALID.charAt(index);
            }
            builder.append(c);
        }
        return builder.toString();
    }
    
    /**
     * create a smartfrog name from a component This is a string that is a valid
     * SF name. no spaces, colons or other forbidden stuff, and it includes the
     * qname if needed.
     * <p/>
     * If there is a weakness in this algorithm, it is that it is neither
     * complete nor unique. Better to have unique names in the firstplace,
     * maybe.
     * <p/>
     * A big troublespot is qnames. Things would be simpler if they were not
     * there, or aliased to something. but they are always incorporated, if
     * present.
     *
     * @return a safer string.
     */
    public String getSfName(Element element) {
        String source;
        if (element.getNamespaceURI().length() > 0) {
            source = element.getNamespaceURI() + "/" + element.getLocalName();
        } else {
            source = element.getLocalName();
        }
        String dest = source.replace("/", ".");
        dest = dest.replace("\\", ".");
        dest = dest.replace("#", "_");
        char firstchar = dest.charAt(0);
        if (firstchar >= '0' && firstchar <= '9') {
            //somebody started an element with a number
            dest = "_" + dest;
        }
        return dest;
    }

    public String convertElementName(Element element) {
        String prefix = namespaceToPrefix(element.getNamespaceURI(),
                element.getNamespacePrefix());
        String localname=sanitize(element.getLocalName());
        return "_"+prefix+"_"+localname;
    }

    /**
     * Enter, using an element name
     * @param element
     */
    public void enter(Element element) {
        enter(convertElementName(element));
    }
    /**
     * Print an attribute out
     * @param attr
     */
    public void printAttribute(Attribute attr) {
        String name=convertAttributeName(attr);
        String value=attr.getValue();
        printTuple(name,value);
    }

    /**
     * print a comment line.
     * any occurence of * / will be intercepted and stripped.
     * @param comment
     */
    public void commentln(String comment) {
        println("//"+comment);
    }



}
