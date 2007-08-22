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
<%@ page import="org.w3c.dom.*" %>
<%@ page import="org.smartfrog.avalanche.shared.XMLHelper" %>

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
                    BootStrap bs = new BootStrap(factory);
                    try {
                        bs.ignite(hosts);
                    } catch (HostIgnitionException e) {
                        message = "Host Ignition failed, please check the hosts' settings. " + e.getMessage();
                    }
                    // STOP SMARTFROG ON SELECTED HOSTS
                } else if (pageAction.equals("stop")) {
                    for (String host : hosts) {
                        try {
                            SFAdapter.stopDaemon(host);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                    // START THE MANAGEMENT CONSOLE FOR THE SELECTED HOSTS
                } else if (pageAction.equals("console")) {
                    if (request.getRemoteHost().equals("127.0.0.1")) {
                        SFAdapter adapter = new SFAdapter(factory, scheduler);
                        for (String host : hosts) {
                            adapter.startMngConsole(host);
                        }
                    } else {
                        message = "Start sfManagementConsole using your command line.";
                    }
                    // DELETE THE SPECIFIED HOSTS FROM THE DATABASE
                } else if (pageAction.equals("delete")) {
                    try {
                        for (String host : hosts) {
                            HostType type = manager.getHost(host);
                            manager.removeHost(type);
                        }
                    } catch (DatabaseAccessException e) {
                        message = "Deletion of the selected hosts failed.";
                    }
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
    entry.appendChild(xdoc.createTextNode((message == null) ? "success" : "error"));
    root.appendChild(entry);

    // Create message-Node
    entry = xdoc.createElement("message");
    if (message != null)
        entry.appendChild(xdoc.createTextNode(message));
    root.appendChild(entry);

    // Print output
    out.clear();
    out.write(XMLHelper.XMLToString(xdoc));
%>