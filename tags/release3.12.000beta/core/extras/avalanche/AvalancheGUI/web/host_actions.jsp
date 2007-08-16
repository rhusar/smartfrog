<%-- /**
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

<%@ page contentType="text/xml" language="java" %>
<%@ include file="InitBeans.jsp" %>
<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*" %>
<%@ page import="org.smartfrog.avalanche.server.engines.*" %>
<%@ page import="org.smartfrog.avalanche.core.host.HostType" %>
<%@ page import="javax.xml.parsers.*" %>
<%@ page import="javax.xml.transform.*" %>
<%@ page import="javax.xml.transform.stream.*" %>
<%@ page import="javax.xml.transform.dom.*" %>
<%@ page import="java.io.*" %>
<%@ page import="org.w3c.dom.*" %>

<%
    HostManager manager = factory.getHostManager();
    String message = null;
    if (null == manager) {
        throw new Exception("Error connecting to hosts database");
    } else {

        // Get the pageAction and selectedHosts parameters
        String pageAction = request.getParameter("pageAction");
        String[] hosts = request.getParameterValues("selectedHost");

        // if the pageAction parameter is set
        if (pageAction != null)
            // and the hosts are defined
            if ((hosts != null) && (hosts.length != 0)) {
                // take an appropriate action
                // IGNITE SELECTED HOSTS
                if (pageAction.equals("ignite")) {
                    BootStrap bs = new BootStrap(factory, setup);
                    try {
                        bs.ignite(hosts);
                    } catch (HostIgnitionException e) {

                    }
                // STOP SMARTFROG ON SELECTED HOSTS
                } else if (pageAction.equals("stop")) {
                    for (int i = 0; i < hosts.length; i++) {
                        try {
                            SFAdapter.stopDaemon(hosts[i]);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                // START THE MANAGEMENT CONSOLE FOR THE SELECTED HOSTS
                } else if (pageAction.equals("console")) {
                    if (request.getRemoteHost().equals("127.0.0.1")) {
                        SFAdapter adapter = new SFAdapter(factory, scheduler);
                        for (int i = 0; i < hosts.length; i++) {
                            adapter.startMngConsole(hosts[i]);
                        }
                    } else {
                        message = "Start sfManagementConsole using your command line.";
                    }
                // DELETE THE SPECIFIED HOSTS FROM THE DATABASE
                } else if (pageAction.equals("delete")) {
                    for (int i = 0; i < hosts.length; i++) {
                        HostType host = manager.getHost(hosts[i]);
                        manager.removeHost(host);
                    }
                    response.sendRedirect("host_list.jsp?active=true");
                }
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
    entry.appendChild(xdoc.createTextNode((message==null)?"success":"error"));
    root.appendChild(entry);

    // Create message-Node
    entry = xdoc.createElement("message");
    if (message != null)
        entry.appendChild(xdoc.createTextNode(message));
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