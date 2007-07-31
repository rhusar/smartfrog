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

<%@ page contentType="text\xml" language="java" %>
<%@ page import="org.apache.xerces.parsers.DOMParser" %>
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

            // TODO: Two nested for-loops might be too expensive for that.
            for (int i = 0; i < nodes.getLength(); i++) {
                // Check each node if it is the right entry
                entry = nodes.item(i);
                NamedNodeMap map = entry.getAttributes();
                Node attribute = null;
                for (int j = 0; j < map.getLength(); j++) {
                    attribute = map.item(j);
                    if (attribute.getNodeName().equals("id") && attribute.getNodeValue().equals(pageId)) {
                        text = entry.getFirstChild().getNodeValue();
                    }
                }
            }
        } catch (Exception e) {
            // TODO: Improve exception handling.
        }
    }

    out.clear();
    out.write("<?xml version=\"1.0\" ?>\n");
    out.write("<response>\n");
    out.write("<type>" + ((text != null) ? "success" : "error") + "</type>\n");
    out.write("<message>" + ((text != null) ? text : "Help is currently not available.") + "</message>\n");
    out.write("</response>");
    out.close();

%>