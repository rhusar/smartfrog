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
<%@ page import="javax.xml.transform.*" %>
<%@ page import="javax.xml.transform.stream.*" %>
<%@ page import="javax.xml.transform.dom.*" %>
<%@ page import="java.io.*" %>
<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.MessageType"%>

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

    Element entry = null;
    Element subentry = null;

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

        // Title node
        entry = xdoc.createElement("title");
        entry.appendChild(xdoc.createTextNode("Avalanche Host Status Feed"));
        channelRoot.appendChild(entry);

        // Link node
        entry = xdoc.createElement("link");
        entry.appendChild(xdoc.createTextNode(pathToAvalancheWebApp + "/host_list.jsp?active=true"));
        channelRoot.appendChild(entry);

        // Description node
        entry = xdoc.createElement("description");
        entry.appendChild(xdoc.createTextNode("This feed shows the status of all hosts known to Avalanche."));
        channelRoot.appendChild(entry);

        // lastBuildDate node
        entry = xdoc.createElement("lastBuildDate");
        entry.appendChild(xdoc.createTextNode(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date())));
        channelRoot.appendChild(entry);

        // language node
        entry = xdoc.createElement("language");
        entry.appendChild(xdoc.createTextNode("en-gb"));
        channelRoot.appendChild(entry);

        // For each host
        for (String host : hosts) {
            String lastMsgText = "";
            String lastMsgTime = "";

            try {
                // Query ActiveProfile
                type = updater.getActiveProfile(host);
                if (type.getMessagesHistoryArray().length != 0) {
                    MessageType msg = type.getMessagesHistoryArray(type.getMessagesHistoryArray().length-1);
                    lastMsgText = msg.getMsg();
                    lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Long.parseLong(msg.getTime())));
                } else {
                    lastMsgText = "No message received yet.";
                    lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date());
                }

                // Create <item> node
                entry = xdoc.createElement("item");

                // Set title node beneath <item>
                subentry = xdoc.createElement("title");
                subentry.appendChild(xdoc.createTextNode(host + ": " + type.getHostState()));
                entry.appendChild(subentry);

                // Set link node beneath <item>
                subentry = xdoc.createElement("link");
                subentry.appendChild(xdoc.createTextNode(pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + host));
                entry.appendChild(subentry);

                // Set guid node beneath <item>
                subentry = xdoc.createElement("guid");
                subentry.appendChild(xdoc.createTextNode(pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + host));
                entry.appendChild(subentry);

                // Set pubDate beneath <item>
                subentry = xdoc.createElement("pubDate");
                subentry.appendChild(xdoc.createTextNode(lastMsgTime));
                entry.appendChild(subentry);

                // Set last message node beneath <host>
                subentry = xdoc.createElement("description");
                subentry.appendChild(xdoc.createTextNode("Last Message: " + lastMsgText + "\nReceived on: " +lastMsgTime));
                entry.appendChild(subentry);

                // Append <host> node to hostlist
                channelRoot.appendChild(entry);
            } catch (Exception e) {
                // do nothing
            }
        }
    } else {
        // Title node
        entry = xdoc.createElement("title");
        entry.appendChild(xdoc.createTextNode("Avalanche: " + hostName + " Status"));
        channelRoot.appendChild(entry);

        // Link node
        entry = xdoc.createElement("link");
        entry.appendChild(xdoc.createTextNode(pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + hostName));
        channelRoot.appendChild(entry);

        // Description node
        entry = xdoc.createElement("description");
        entry.appendChild(xdoc.createTextNode("This feed shows the status of " + hostName));
        channelRoot.appendChild(entry);

        // lastBuildDate node
        entry = xdoc.createElement("lastBuildDate");
        entry.appendChild(xdoc.createTextNode(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date())));
        channelRoot.appendChild(entry);

        // language node
        entry = xdoc.createElement("language");
        entry.appendChild(xdoc.createTextNode("en-gb"));
        channelRoot.appendChild(entry);

        type = updater.getActiveProfile(hostName);
        MessageType[] messages = type.getMessagesHistoryArray();

        String lastMsgText = "";
        String lastMsgTime = "";

        if (messages.length != 0) {
            for (int i = messages.length-1; i >= 0; i--) {

                MessageType msg = messages[i];
                lastMsgText = msg.getMsg();
                lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Long.parseLong(msg.getTime())));

                // Create <item> node
                entry = xdoc.createElement("item");

                // Set title node beneath <item>
                subentry = xdoc.createElement("title");
                subentry.appendChild(xdoc.createTextNode(lastMsgText));
                entry.appendChild(subentry);

                // Set link node beneath <item>
                subentry = xdoc.createElement("link");
                subentry.appendChild(xdoc.createTextNode(pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + hostName));
                entry.appendChild(subentry);

                // Set guid node beneath <item>
                subentry = xdoc.createElement("guid");
                subentry.appendChild(xdoc.createTextNode(pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + hostName));
                entry.appendChild(subentry);

                // Set pubDate beneath <item>
                subentry = xdoc.createElement("pubDate");
                subentry.appendChild(xdoc.createTextNode(lastMsgTime));
                entry.appendChild(subentry);

                // Set last message node beneath <host>
                subentry = xdoc.createElement("description");
                subentry.appendChild(xdoc.createTextNode(lastMsgText));
                entry.appendChild(subentry);

                // Append <host> node to hostlist
                channelRoot.appendChild(entry);
             }
        } else {
            lastMsgText = "No message received yet.";
            lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date());

            // Create <item> node
            entry = xdoc.createElement("item");

            // Set title node beneath <item>
            subentry = xdoc.createElement("title");
            subentry.appendChild(xdoc.createTextNode(lastMsgText));
            entry.appendChild(subentry);

            // Set link node beneath <item>
            subentry = xdoc.createElement("link");
            subentry.appendChild(xdoc.createTextNode(pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + hostName));
            entry.appendChild(subentry);

            // Set guid node beneath <item>
            subentry = xdoc.createElement("guid");
            subentry.appendChild(xdoc.createTextNode(pathToAvalancheWebApp + "/log_xmpp.jsp?host=" + hostName));
            entry.appendChild(subentry);

            // Set pubDate beneath <item>
            subentry = xdoc.createElement("pubDate");
            subentry.appendChild(xdoc.createTextNode(lastMsgTime));
            entry.appendChild(subentry);

            // Set last message node beneath <host>
            subentry = xdoc.createElement("description");
            subentry.appendChild(xdoc.createTextNode(lastMsgText));
            entry.appendChild(subentry);

            // Append <host> node to hostlist
            channelRoot.appendChild(entry);
        }
    }

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