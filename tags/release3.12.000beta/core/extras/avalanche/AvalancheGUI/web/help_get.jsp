<%-- /** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

For more information: www.smartfrog.org */ --%>

<%@ page language="java" contentType="text/xml" %>
<%@ page import="org.apache.xerces.parsers.DOMParser" %>
<%@ page import="javax.xml.parsers.*" %>
<%@ page import="javax.xml.transform.*" %>
<%@ page import="javax.xml.transform.stream.*" %>
<%@ page import="javax.xml.transform.dom.*" %>
<%@ page import="java.io.*" %>
<%@ page import="org.w3c.dom.*" %>

<%
    String pageId = request.getParameter("id");
    String text = null;

    if (pageId != null) {
        pageId = pageId.trim();

        // Get new parser
        DOMParser parser = new DOMParser();
        try {
            // Access the help file
            // TODO: Clear if this is correct.
            parser.parse(getServletConfig().getServletContext().getRealPath("help_texts.xml"));

            // Access the document
            Document doc = parser.getDocument();
            NodeList nodes = doc.getElementsByTagName("page");
            Node entry = null;

            // File is ok - but help might be not available.
            text = "There is no help for this topic. Sorry!";

            for (int i = 0; i < nodes.getLength(); i++) {
                // Check each node if it is the right entry
                entry = nodes.item(i);
                if (entry.getAttributes().getNamedItem("id").getNodeValue().equals(pageId)) {
                    text = entry.getFirstChild().getNodeValue();
                }
            }
        } catch (Exception e) {
            // TODO: Improve exception handling.
        }
    }

    // Create output document
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document xdoc = db.newDocument();

    // Create root object
    Element root = xdoc.createElement("response");
    xdoc.appendChild(root);

    // Create type-Node
    Element entry = xdoc.createElement("type");
    entry.appendChild(xdoc.createTextNode(((text!=null)?"success":"error")));
    root.appendChild(entry);

    // Create message-Node
    entry = xdoc.createElement("message");
    entry.appendChild(xdoc.createTextNode(((text!=null)?text:"Sorry, but there is no help on this topic.")));
    root.appendChild(entry);

    // Convert DOM to XML string
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    Transformer trans = TransformerFactory.newInstance().newTransformer();
    trans.setOutputProperty(OutputKeys.INDENT, "yes");
    trans.transform(new DOMSource(xdoc), result);
    String xmlString = sw.toString();
    sw.close();

    // Print output
    out.clear();
    out.write(xmlString);
%>