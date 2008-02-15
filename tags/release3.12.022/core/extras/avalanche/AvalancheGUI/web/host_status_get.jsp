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
<%@ page import="org.smartfrog.avalanche.core.host.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.parsers.*" %>
<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType"%>
<%@ page import="org.smartfrog.avalanche.shared.XMLHelper" %>

<%@ include file="init_hostmanager.inc.jsp"%>
<%  String[] hosts = manager.listHosts();

    // Be able to query ActiveProfile
    ActiveProfileUpdater updater = new ActiveProfileUpdater();
    ActiveProfileType type = null;

    // Create output document
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document xdoc = db.newDocument();

    // Create root object
    Element root = xdoc.createElement("hostlist");
    xdoc.appendChild(root);

    Element entry = null;
    Element subentry = null;
    // For each host
    for (String host : hosts) {
        HostType h = null;
        String os = "";
        String arch = "";
        String lastMsg = "";
        boolean active = false;

        try {
            // Get HostType object of current host
            h = manager.getHost(host);
            // Extract OS and Architecture
            os = h.getPlatformSelector().getOs();
            arch = h.getPlatformSelector().getArch();

            // Query ActiveProfile
            type = updater.getActiveProfile(host);
            active = type.getHostState().equals("Available");
            if (type.getMessagesHistoryArray().length != 0) {
                lastMsg = type.getMessagesHistoryArray(type.getMessagesHistoryArray().length - 1).getMsg();
            } else {
                lastMsg = "false";
            }

            // Create <host name="xxx"> node
            entry = xdoc.createElement("host");
            entry.setAttribute("name", host);

            // Set OS node beneath <host>
            XMLHelper.addTextNode(xdoc, entry, "os", os);

            // Set Architecture node beneath <host>
            XMLHelper.addTextNode(xdoc, entry, "arch", arch);

            // Set status node beneath <host>
            XMLHelper.addTextNode(xdoc, entry, "status", Boolean.toString(active));

            // Set last message node beneath <host>
            XMLHelper.addTextNode(xdoc, entry, "lastmsg", lastMsg);

            // Append <host> node to hostlist
            root.appendChild(entry);
        } catch (Exception e) {
            // do nothing
        }
    }

    // Print output
    out.clear();
    out.write(XMLHelper.XMLToString(xdoc));
%>