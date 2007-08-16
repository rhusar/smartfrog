<%-- /**
 (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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
 */ --%>
<%@ page contentType="text/html" language="java" %>
<%@ include file="header.inc.jsp" %>
<%@ page import="org.smartfrog.avalanche.server.HostManager" %>
<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater" %>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType" %>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.MessageType" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.DateFormat" %>

<script language="JavaScript" type="text/javascript">
    <!--
    setNextSubtitle("XMPP Message Log");
    -->
</script>

<br/>

<div align="center">
<center>
<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        <!--
        oneVoiceWritePageMenu("XmppReader", "header");
        -->
    </script>
</div>

<%
    String errMsg = null;
    String rowClass = "";

    String hostName = request.getParameter("host");
    ActiveProfileUpdater updater = new ActiveProfileUpdater();
    ActiveProfileType type = null;
    MessageType[] messages = null;

    if (hostName != null) {
        hostName = hostName.trim().toLowerCase();
        if (!hostName.equals("")) {
            type = updater.getActiveProfile(hostName);
        }
    }

    if (type != null) {
        hostName = hostName.trim().toLowerCase();
        type = updater.getActiveProfile(hostName);

        if ((request.getParameter("clear") != null) && request.getParameter("clear").equals("true")) {
            type.setMessagesHistoryArray(null);
            updater.storeActiveProfile(type);
            type = updater.getActiveProfile(hostName);
        }

        messages = type.getMessagesHistoryArray();
%>

<p style="text-align:left;">XMPP Message Viewer &gt; <a href="?">Host List</a> &gt; <%=hostName%></p>
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="hostListTable">
    <caption>Messages from <%=hostName%>
    </caption>
    <thead>
        <tr class="captionRow">
            <th>Time</th>
            <th>Message</th>
        </tr>
    </thead>
    <tbody>
        <%
            if (messages.length != 0) {
                MessageType msg = null;
                for (int i = messages.length-1; i >= 0; i--) {
                    rowClass = ((i % 2) == 0) ? "class=\"altRowColor\"" : "";
                    String time = "";
                    String text = "";
                    msg = messages[i];
                    time = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Long.parseLong(msg.getTime())));
                    text = msg.getMsg();
        %>
        <tr <%= rowClass %>>
            <td style="width:300px;"><%= time %>
            </td>
            <td><%= text %>
            </td>
        </tr>
        <% }
        } else { %>
        <td colspan="2">No messages were received from this host. Please check back later.</td>
        <% } %>
    </tbody>
</table>
<a href="?host=<%= hostName %>&clear=true">Delete messages</a> <a href="host_status_feed.jsp?host=<%= hostName %>">Get messages as RSS feed</a>
<% } else { %>

<p style="text-align:left;">XMPP Message Viewer &gt; Host List</p>
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="hostListTable">
    <caption>Hosts</caption>
    <thead>
        <tr class="captionRow">
            <th>Host</th>
            <th>Messages</th>
            <th>Last Message</th>
            <th>Date/Time</th>
        </tr>
    </thead>
    <tbody>
        <% HostManager manager = factory.getHostManager();
            if (null == manager) {
                errMsg = "Error connecting to hosts database";
                throw new Exception("Error connecting to hosts database");
            }

            String[] hostNames = manager.listHosts();
            if (hostNames.length != 0) {
                int count = 0;
                String messageCount = null;
                String lastMsgText = null;
                String lastMsgTime = null;
                MessageType lastMsg = null;

                for (String host : hostNames) {
                    rowClass = ((count++ % 2) == 0) ? "class=\"altRowColor\"" : "";
                    type = updater.getActiveProfile(host);

                    if (type.getMessagesHistoryArray().length != 0) {
                        messageCount = String.valueOf(type.getMessagesHistoryArray().length);
                        lastMsg = type.getMessagesHistoryArray(type.getMessagesHistoryArray().length - 1);
                        lastMsgText = lastMsg.getMsg();
                        lastMsgTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Long.parseLong(lastMsg.getTime())));
                    } else {
                        messageCount = "0";
                        lastMsgText = "No message received yet.";
                        lastMsgTime = "";
                    }
        %>
        <tr <%= rowClass %>>
            <td><a href="?host=<%= host %>"><%= host %>
            </a></td>
            <td><%= messageCount %> Message<% if (!messageCount.equals("1")) { %>s<% } %></td>
            <td><%= lastMsgText %>
            </td>
            <td><%= lastMsgTime %>
            </td>
        </tr>
        <% }
        } else { %>
        <td colspan="4">There are no active hosts in the database. To add a host, please click <a
                href="host_setup_bs.jsp">here</a>.
        </td>
        <% } %>
    </tbody>
</table>
<a href="host_status_feed.jsp">Get host status as RSS feed</a>
<% } %>

</center>
</div>

<%@ include file="footer.inc.jsp" %>