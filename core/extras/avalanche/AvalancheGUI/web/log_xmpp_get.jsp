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
<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.parsers.*" %>
<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.MessageType"%>
<%@ page import="org.smartfrog.avalanche.shared.XMLHelper" %>

<%
    // Be able to query ActiveProfile
    ActiveProfileUpdater updater = new ActiveProfileUpdater();
    ActiveProfileType type = null;
    MessageType[] messages = null;

    // Create output document
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document xdoc = db.newDocument();

    String lastMsgText = "";
    String lastMsgTime = "";

    String hostName = request.getParameter("host");
    if (hostName != null) {
        hostName = hostName.trim().toLowerCase();
        if (hostName.equals("") || (updater.getActiveProfile(hostName) == null)) {
            hostName = null;
        }
    }

    if (hostName == null) {

        // Create  root
        Element root = xdoc.createElement("hosts");
        xdoc.appendChild(root);

        // Get all hosts
        HostManager manager = factory.getHostManager();
        String[] hosts = manager.listHosts();

        // For each host
        for (String host : hosts) {
            try {
                // Query ActiveProfile
                type = updater.getActiveProfile(host);
                messages = type.getMessagesHistoryArray();

                if (messages.length != 0) {
                    MessageType msg = type.getMessagesHistoryArray(messages.length - 1);
                    lastMsgText = msg.getMsg();
                    lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Long.parseLong(msg.getTime())));
                } else {
                    lastMsgText = "No message received yet.";
                    lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date());
                }

                Element hostRoot = xdoc.createElement("host");
                hostRoot.setAttribute("id", host);
                hostRoot.setAttribute("msgcount", String.valueOf(messages.length));
                root.appendChild(hostRoot);

                Element messageRoot = xdoc.createElement("msg");
                messageRoot.setAttribute("time", lastMsgTime);
                messageRoot.setAttribute("text", lastMsgText);
                hostRoot.appendChild(messageRoot);
            } catch (Exception e) {
                // do nothing
            }
        }
    } else {
        type = updater.getActiveProfile(hostName);
        messages = type.getMessagesHistoryArray();

        Element hostRoot = xdoc.createElement("host");
        hostRoot.setAttribute("id", hostName);
        hostRoot.setAttribute("msgcount", String.valueOf(messages.length));
        xdoc.appendChild(hostRoot);

        if (messages.length != 0) {
            for (int i = messages.length - 1; i >= 0; i--) {
                MessageType msg = messages[i];
                lastMsgText = msg.getMsg();
                lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Long.parseLong(msg.getTime())));

                Element messageRoot = xdoc.createElement("msg");
                messageRoot.setAttribute("time", lastMsgTime);
                messageRoot.setAttribute("text", lastMsgText);
                hostRoot.appendChild(messageRoot);
            }
        } else {
            lastMsgText = "No message received yet.";
            lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date());

            Element messageRoot = xdoc.createElement("msg");
            messageRoot.setAttribute("time", lastMsgTime);
            messageRoot.setAttribute("text", lastMsgText);
            hostRoot.appendChild(messageRoot);
        }
    }

    // Print output
    out.clear();
    out.write(XMLHelper.XMLToString(xdoc));
%>