<%-- /*
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

For more information: www.smartfrog.org
*/ --%>

<%@ page language="java" contentType="text/xml" %>
<%@ include file="InitBeans.jsp" %>
<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.core.host.*" %>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.parsers.*" %>
<%@ page import="javax.xml.transform.*" %>
<%@ page import="javax.xml.transform.stream.*" %>
<%@ page import="javax.xml.transform.dom.*" %>
<%@ page import="java.io.*" %>

<%
    // Get all hosts
    HostManager manager = factory.getHostManager();
    String[] hosts = manager.listHosts();

    // Be able to query SmartFrog status
    SFAdapter adapter = new SFAdapter(factory);

    // Create output document
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document xdoc = db.newDocument();

    // Create root object
    Element root = xdoc.createElement("hostlist");
    xdoc.appendChild(root);

    Element entry = null;
    Element subentry = null;
    // For each host
    for (int i = 0; i < hosts.length; i++) {
        HostType h = null;
        String os = "";
        String arch = "";
        boolean active = false;

        try {
            // Get HostType object of current host
            h = manager.getHost(hosts[i]);

            // Extract OS and Architecture
            os = h.getPlatformSelector().getOs();
            arch = h.getPlatformSelector().getArch();

            // Query SmartFrog status on that specific host
            try {
                active = adapter.isActive(hosts[i]);
            } catch (Throwable t) {

            }
        } catch (Exception e) {
            // do nothing
        }

        // Create <host name="xxx"> node
        entry = xdoc.createElement("host");
        entry.setAttribute("name", hosts[i]);

        // Set OS node beneath <host>
        subentry = xdoc.createElement("os");
        subentry.appendChild(xdoc.createTextNode(os));
        entry.appendChild(subentry);

        // Set Architecture node beneath <host>
        subentry = xdoc.createElement("arch");
        subentry.appendChild(xdoc.createTextNode(arch));
        entry.appendChild(subentry);

        // Set status node beneath <host>
        subentry = xdoc.createElement("status");
        subentry.appendChild(xdoc.createTextNode(Boolean.toString(active)));
        entry.appendChild(subentry);

        // Append <host> node to hostlist
        root.appendChild(entry);
    }

    // Convert DOM to XML string
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    Transformer trans = TransformerFactory.newInstance().newTransformer();
    trans.setOutputProperty(OutputKeys.INDENT, "yes");
    trans.transform(new DOMSource(xdoc), result);
    String xmlString = sw.toString();

    // Print output
    out.clear();
    out.write(xmlString);
%>