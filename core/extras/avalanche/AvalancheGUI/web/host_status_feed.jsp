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

<%@ page language="java" contentType="application/rss+xml" %>
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

    // Create output document
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document xdoc = db.newDocument();

    // Create RSS root
    Element rssRoot = xdoc.createElement("rss");
    rssRoot.setAttribute("version", "2.0");
    xdoc.appendChild(rssRoot);

    // Create CHANNEL root
    Element channelRoot = xdoc.createElement("channel");
    rssRoot.appendChild(channelRoot);

    String title = "";
    String link = "";
    String description = "";

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
        // Get all hosts
        HostManager manager = factory.getHostManager();
        String[] hosts = manager.listHosts();

        title = "Avalanche Host Status Feed";
        link = pathToAvalancheWebApp + "/host_list.jsp?active=true";
        description = "This feed shows the status of all hosts known to Avalanche.";

        XMLHelper.addRSSChannelInformation(xdoc, channelRoot, title, link, description);

        // For each host
        for (String host : hosts) {
            try {
                // Query ActiveProfile
                type = updater.getActiveProfile(host);
                if (type.getMessagesHistoryArray().length != 0) {
                    MessageType msg = type.getMessagesHistoryArray(type.getMessagesHistoryArray().length - 1);
                    lastMsgText = msg.getMsg();
                    lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Long.parseLong(msg.getTime())));
                } else {
                    lastMsgText = "No message received yet.";
                    lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date());
                }
                link = pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + host;

                XMLHelper.addRSSItem(xdoc, channelRoot, host + ": " + type.getHostState(), link, link, lastMsgTime, "Last Message: " + lastMsgText + "\nReceived on: " + lastMsgTime);
            } catch (Exception e) {
                // do nothing
            }
        }
    } else {
        title = "Avalanche: " + hostName + " Status";
        link = pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + hostName;
        description = "This feed shows the status of " + hostName;

        XMLHelper.addRSSChannelInformation(xdoc, channelRoot, title, link, description);

        type = updater.getActiveProfile(hostName);
        MessageType[] messages = type.getMessagesHistoryArray();

        if (messages.length != 0) {
            for (int i = messages.length - 1; i >= 0; i--) {
                MessageType msg = messages[i];
                lastMsgText = msg.getMsg();
                lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Long.parseLong(msg.getTime())));

                XMLHelper.addRSSItem(xdoc, channelRoot, lastMsgText, link, link, lastMsgTime, lastMsgText);
            }
        } else {
            lastMsgText = "No message received yet.";
            lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date());
            link = pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + hostName;

            XMLHelper.addRSSItem(xdoc, channelRoot, lastMsgText, link, link, lastMsgTime, lastMsgText);
        }
    }

    // Print output
    out.clear();
    out.write(XMLHelper.XMLToString(xdoc));
%>