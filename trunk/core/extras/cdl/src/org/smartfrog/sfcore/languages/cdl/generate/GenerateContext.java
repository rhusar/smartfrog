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

import nu.xom.Attribute;
import nu.xom.Element;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.HashMap;

/**
 * This file handles the generation created 23-Jun-2005 11:06:50
 */

public class GenerateContext {

    /**
     * output stream
     */
    private PrintWriter out;

    private File destFile;

    private int depth = 0;

    private String prefix = "";

    private Formatter formatter;

    private HashMap<String, String> names;

    private HashMap<String, String> extenders;

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * the default charset for this doc is defined here. {@value}
     */
    public static final String DEFAULT_CHARSET = "US-ASCII";
    public static final String COMPONENT_SFSYSTEM = "sfConfig";

    /**
     * name of the dest file
     */
    private String destFilename;
    public static final int INDENT = 1;
    public static final String COMPONENT_CONFIGURATION = "configuration";
    public static final String CDL_COMPONENT_FILE = "/org/smartfrog/services/cddlm/cdl/components.sf";
    public static final String ATTRIBUTE_PREFIX = "a_";
    public static final String ELEMENT_PREFIX = "_";
    public static final String SF_COMPONENTS_NAMESPACE = "http://smartfrog.org/services/cdl/2005/06";
    private static final String DEFAULT_BASE_COMPONENT = "CmpComponent";

    public GenerateContext() {
        initHashMaps();
    }


    /**
     * These are things we know how to extend specially, so when generating them
     * we extend to the smartfrog things
     * <p/>
     * This is all a bit of a hack, because it doesnt track expansion properly;
     * it is driven by the name of the element, not what they extended
     * <p/>
     * we would catch &lt;cmp:OnInitialized /&gt; but not &lt;something
     * cdl:extends="cmp:OnInitialized" /&gt;
     */
    private static String known_extenders[] =
            {
                "_cmp_OnInitalized",
                "_cmp_OnRunning",
                "_cmp_OnFailed",
                "_cmp_OnTerminated",
                "_cmp_OnFault",
                "_cmp_OnChange",
                "_cmp_sequence",
                "_cmp_reverse",
                "_cmp_flow",
                "_cmp_wait",
                "_cmp_switch"
            };


    protected void initHashMaps() {
        //namespaces
        names = new HashMap<String, String>();
        names.put(Constants.XML_CDL_NAMESPACE, "cdl");
        names.put(Constants.CMP_NAMESPACE, "cmp");
        names.put(SF_COMPONENTS_NAMESPACE, "sf");


        //known extensions
        extenders = new HashMap<String, String>();
        for (int i = 0; i < known_extenders.length; i++) {
            extenders.put(known_extenders[i], known_extenders[i]);
        }


    }

    public void begin(File dest) throws IOException {
        destFile = dest;
        destFilename = destFile.getAbsolutePath();
        out = new PrintWriter(destFile, DEFAULT_CHARSET);
        formatter = new Formatter(out);
        setDepth(0);
        //pull in CDL includes
        hashInclude(CDL_COMPONENT_FILE);

    }

    /**
     * End generation
     *
     * @throws IOException
     */
    public void end() throws IOException {
        boolean hadError = out.checkError();
        close();
        if (hadError) {
            reportError();
        }
    }


    /**
     * Error check.
     *
     * @throws IOException
     */
    public void checkForErrors() throws IOException {

        if (out != null && out.checkError()) {
            reportError();
        }
    }

    /**
     * report any errors on the output stream
     *
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
                out = null;
            } catch (Exception e) {
                log.warn("when closing " + destFile, e);
            }
        }
    }


    /**
     * set the depth of nesting, and create an appropriate prefix line
     *
     * @param value
     */
    private void setDepth(int value) {
        depth = value;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            builder.append(" ");
        }
        prefix = builder.toString();
    }

    public void hashInclude(String resource) {
        println("#include \"%s\";", resource);
    }

    public void enter(String componentName, String extending) {
        String text = componentName + " extends " + extending + " {";
        println(text);
        setDepth(depth + INDENT);
    }

    public void enter(String componentName) {
        enter(componentName,DEFAULT_BASE_COMPONENT);
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
     * Get the default base template for componet declarations 
     * @return the (non-empty, non-null base template);
     */ 
    public String getDefaultBaseComponent() {
        return DEFAULT_BASE_COMPONENT;
    }

    /**
     * varargs println
     *
     * @param format
     * @param args
     */
    public void println(String format, Object... args) {
        out.print(prefix);
        formatter.format(format, args);
        formatter.flush();
        out.println();
    }

    public void printTuple(String name, String value) {
        println("%s \"%s\";", name, value);
    }

    public void printImport(String name) {
        println("#import  \"%s\"", name);
    }



    public String namespaceToPrefix(String namespace, String prefix) {
        if ("".equals(namespace)) {
            return "";
        }
        //look for a mapping
        String mapping = names.get(namespace);
        if (mapping != null) {
            return mapping;
        }
        //now an empty node
        if (prefix != null) {
            mapping = sanitize(prefix);
            if (names.get(mapping) != null) {
                mapping = makeUniquePrefix(prefix);
            }
        } else {
            mapping = makeUniquePrefix(DEFAULT_PREFIX);
        }
        names.put(namespace, mapping);
        return mapping;
    }

    public static final String DEFAULT_PREFIX = "p";

    private String makeUniquePrefix(String start) {
        int count = 0;
        String newstr;
        do {
            newstr = start + count;
            count++;
        } while (names.get(newstr) == null);
        return newstr;
    }

    private static final String INVALID =
            " /#-$\\";
    private static final String VALID =
            ".......";


    /**
     * turn a potentially dirty string into a safe one
     *
     * @param input
     * @return
     */
    public String sanitize(CharSequence input) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int index = INVALID.indexOf(c);
            if (index >= 0) {
                c = VALID.charAt(index);
            }
            builder.append(c);
        }
        return builder.toString();
    }


    /**
     * Get the attribute name
     *
     * @param attr
     * @return a_${namespacePrefix}_uri
     */
    String convertAttributeName(Attribute attr) {
        return makeSfKey(attr.getNamespaceURI(),
                attr.getNamespacePrefix(),
                attr.getLocalName(),
                ATTRIBUTE_PREFIX);
    }

    /**
     * turn an element name into something that we can work with
     * @param element
     * @return
     */
    public String convertElementName(Element element) {
        return makeSfKey(element.getNamespaceURI(),
                element.getNamespacePrefix(),
                element.getLocalName(),
                ELEMENT_PREFIX);
    }

    private String makeSfKey(String namespaceURI,
            String namespacePrefix,
            String localName, String keyPrefix) {
        String prefix = namespaceToPrefix(namespaceURI,
                namespacePrefix);
        String localname = sanitize(localName);
        return keyPrefix + prefix + "_" + localname;
    }

    /**
     * Enter, using an element name
     *
     * @param element
     */
    public void enter(Element element) {
        enter(convertElementName(element));
    }

    /**
     * Print an attribute out
     *
     * @param attr
     */
    public void printAttribute(Attribute attr) {
        String name = convertAttributeName(attr);
        String value = attr.getValue();
        printTuple(name, value);
    }

    /**
     * print a comment line. any occurence of * / will be intercepted and
     * stripped.
     *
     * @param comment
     */
    public void commentln(String comment) {
        println("//" + comment);
    }


}
