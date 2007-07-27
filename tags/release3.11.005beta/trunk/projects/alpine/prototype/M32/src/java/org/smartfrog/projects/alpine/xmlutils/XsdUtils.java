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
package org.smartfrog.projects.alpine.xmlutils;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.XPathContext;
import nu.xom.Elements;
import org.smartfrog.projects.alpine.faults.ServerException;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * XML Schema helper stuff.
 * It also contains static things to work on Xom Elements, methods that dont rely on the Elements
 * being any particular subclass of Element.
 * created 26-May-2005 15:35:48
 */

public final class XsdUtils {

    private XsdUtils() {
    }

    /**
     * Test for a string being true against the XSD types
     *
     * @param value string to parse
     * @return true iff the string value matches the XSD boolean types "true" or "1", CASE-SENSITIVE
     * @link http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean
     */
    public static boolean isXsdBooleanTrue(String value) {
        return "true".equals(value) || "1".equals(value);
    }

    /**
     * Test for a string being true against the XSD types
     *
     * @param value string to parse
     * @return true iff the string value matches the XSD boolean types "false" or "0", CASE-SENSITIVE
     * @link http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean
     */
    public static boolean isXsdBooleanFalse(String value) {
        return "false".equals(value) || "0".equals(value);
    }


    /**
     * Convert a date to an iso timestamp
     * @param timestamp
     * @return an ISO time formatted string
     */
    public static String toIsoTime(Date timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(timestamp);
    }

    /**
     * Convert from a string triple. Prefix may be null.
     *
     * @param namespace namespace
     * @param local     local name
     * @param prefix    optional prefix
     * @return a QName that matches the source QualifiedName.
     */
    public static QName makeQName(String namespace,
                                  String local,
                                  String prefix) {
        QName dest;
        if (prefix != null) {
            dest = new QName(namespace,
                    local,
                    prefix);

        } else {
            dest = new QName(namespace, local);
        }
        return dest;
    }

    /**
     * map from, say tns:something to 'tns'
     *
     * @param string the string to extract the prefix from
     * @return the prefix or null for no namespace
     */
    public static String extractNamespacePrefix(String string) {
        int offset = string.indexOf(':');
        if (offset >= 0) {
            return string.substring(0, offset);
        } else {
            return null;
        }
    }

    /**
     * map from, say tns:something to 'something'
     * This is useful in Xom element factories.
     *
     * @param string the string to examine
     * @return everything following the : or the whole string if one is not
     *         there
     */
    public static String extractLocalname(String string) {
        int offset = string.indexOf(':');
        if (offset >= 0) {
            return string.substring(offset + 1);
        } else {
            return string;
        }
    }

    /**
     * Get the qname of an element
     *
     * @param element element to examine
     * @return a dqname from the parts of an element
     */
    public static QName makeQName(Element element) {
        return new QName(element.getNamespaceURI(), element.getLocalName());
    }

    /**
     * Resolve a xsd:qname string relative to an element.
     * If this looks like the OMElement.resolveQName, its because I wrote both of them side-by-side
     *
     * @param element                  element to resolve from
     * @param qname                    qname as prefix:localname string
     * @param defaultToParentNameSpace flag to indicate that no prefix implies defaulting to the
     *                                 parent xmlns.
     * @return the namespace or null for no match
     */
    public static QName resolveQName(Element element, String qname, boolean defaultToParentNameSpace) {
        int colon = qname.indexOf(':');
        if (colon < 0) {
            if (defaultToParentNameSpace) {
                //get the parent ns and use it for the child
                String namespace = element.getNamespaceURI();
                return new QName(namespace, qname, element.getNamespacePrefix());
            } else {
                //else things without no prefix are local.
                return new QName(qname);
            }
        }
        String prefix = qname.substring(0, colon);
        String local = qname.substring(colon + 1);
        if (local.length() == 0) {
            //empty local, exit accordingly
            return null;
        }
        if(prefix.length()==0) {
            //shortened prefix implies local
            return new QName(local);
        }
        String namespace = element.getNamespaceURI(prefix);
        if (namespace == null) {
            //no matching namespace
            return null;
        }
        return new QName(namespace, local, prefix);
    }

    /**
     * Extract the first child element of an element
     * @param element the element to scan
     * @return the element or null for no child elements
     */
    public static Element getFirstChildElement(Element element) {
        int c = element.getChildCount();
        for (int i = 0; i < c; i++) {
            Node n = element.getChild(i);
            if (n instanceof Element) {
                return (Element) n;
            }
        }
        //no match
        return null;
    }

    /**
     * Test for an element having a full name matching the qname
     * @param  element element to search
     * @param testName expected name
     * @return true iff local name and namespace URIs match.
     */
    public static boolean isNamed(Element element, QName testName) {
        return element.getLocalName().equals(testName.getLocalPart()) &&
                element.getNamespaceURI().equals(testName.getNamespaceURI());
    }

    /**
     * Get the immediate text value of an element. That is -the concatenation
     * of all direct child text elements. This string is not trimmed.
     * @param  element element to search
     * @return a next string, which will be empty "" if there is no text
     */
    public static String getTextValue(Element element) {
        StringBuilder builder = new StringBuilder();
        for (Node n : new NodeIterator(element)) {
            if (n instanceof Text) {
                Text text = (Text) n;
                builder.append(text.getValue());
            }
        }
        return builder.toString();
    }

    /**
     * Apply an XPath query to a node
     * @param  element element to search
     * @param path    xpath query
     * @param context context for prefix evaluation
     * @return an iterator over all nodes that match the path
     */
    public static NodesIterator xpath(Element element, String path, XPathContext context) {
        Nodes nodes = element.query(path, context);
        NodesIterator it = new NodesIterator(nodes);
        return it;
    }

    /**
     * Print an element to a string, for debug purposes
     * This is not cheap, as the graph is duplicated and pasted into a new document.
     * @param element the element to copy and print
     * @return a formatted, indented, wrapped, version of the message
     */
    public static String printToString(Element element) {
        if(element==null) {
            return "";
        }
        Document doc = new Document((Element) element.copy());
        return XsdUtils.printToString(doc);
    }

    /**
     * Print a document to a string, for debug purposes
     *
     * @param document doc to print
     * @return a formatted, indented, wrapped, version of the message
     */
    public static String printToString(Document document) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            Serializer serializer = new Serializer(baos);
            serializer.setMaxLength(80);
            serializer.setIndent(2);
            serializer.write(document);
            serializer.flush();
            return baos.toString("UTF-8");
        } catch (IOException e) {
            throw new ServerException("Unable to serialize document", e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * Get the child elements in a given namespace
     *
     * @param  element element to search
     * @param namespace name of the elements
     * @return an iterator over the child elements
     */
    public static BaseElementsIterator<Element> elements(Element element, String local,String namespace) {
        Elements childElements = element.getChildElements(local, namespace);
        return new BaseElementsIterator<Element>(childElements);
    }

    /**
     * Get the child elements in a given namespace
     *
     * @param  element element to search
     * @param namespace name of the elements
     * @return an iterator over the child elements
     */
    public static BaseElementsIterator<Element> elements(Element element, String namespace) {
        return elements(element, "",namespace);
    }

    /**
     * Get the child elements of the given name
     *
     * @param  element element to search
     * @param name name of the elements
     * @return iterator over the elements
     */
    public static BaseElementsIterator<Element> elements(Element element, QName name) {
        return elements(element, name.getLocalPart(), name.getNamespaceURI());
    }

    /**
     * copy the nodes in the Elements type into a java5 typed list. There is no cloning.
     * @param in element list
     * @return a list representation.
     */
    public static List<Element> makeList(Elements in) {
        List<Element> out=new ArrayList<Element>(in.size());
        for(int i=0;i<in.size();i++) {
            out.add(in.get(i));
        }
        return out;
    }
}
