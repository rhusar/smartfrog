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
<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater" %>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType" %>

<script type="text/javascript" language="JavaScript" src="log_xmpp_ajax.js"></script>
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

<%@ include file="message.inc.jsp"%>

<%  String rowClass = "";

    String hostName = request.getParameter("host");
    ActiveProfileUpdater updater = new ActiveProfileUpdater();
    ActiveProfileType type = null;

    if (hostName != null) {
        hostName = hostName.trim().toLowerCase();
        if (!hostName.equals("")) {
            type = updater.getActiveProfile(hostName);
        }
    }

    // Valid host?
    if (type != null) {
        hostName = hostName.trim().toLowerCase();
        type = updater.getActiveProfile(hostName);

        if ((request.getParameter("clear") != null) && request.getParameter("clear").equals("true")) {
            type.setMessagesHistoryArray(null);
            updater.storeActiveProfile(type);
            type = updater.getActiveProfile(hostName);
        }
%>

<p style="text-align:left;">XMPP Message Viewer &gt; <a href="?">Host List</a> &gt; <%=hostName%></p>
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="hostMsgList">
    <caption>Messages from <%=hostName%>
    </caption>
    <thead id="hostMsgListHeader">
        <tr class="captionRow">
            <th>Time</th>
            <th>Message</th>
        </tr>
    </thead>
    <tbody id="hostMsgListBody" />
</table>
<br/>
<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        <!--
        oneVoiceWritePageMenu(  "XmppReader", "footer",
                                "RSS", "javascript:window.location='host_status_feed.jsp?host=<%= hostName %>'",
                                "Delete messages", "javascript:window.location='log_xmpp.jsp?host=<%= hostName %>&clear=true'");
        -->
    </script>
</div>
<script language="JavaScript" type="text/javascript">
    <!--
    window.setInterval("updateMsgList('<%= hostName %>')", 5000);

    updateMsgList('<%= hostName %>');
    -->
</script>
<% } else { %>

<p style="text-align:left;">XMPP Message Viewer &gt; Host List</p>
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="hostMsgList">
    <caption>Hosts</caption>
    <thead id="hostMsgListHeader">
        <tr class="captionRow">
            <th>Host</th>
            <th>Messages</th>
            <th>Last Message</th>
            <th>Date/Time</th>
        </tr>
    </thead>
    <tbody id="hostMsgListBody" />
</table>

<br/>
<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        <!--
        oneVoiceWritePageMenu(  "XmppReader", "footer",
                                "RSS", "javascript:window.location='host_status_feed.jsp'");
        -->
    </script>
</div>

<script language="JavaScript" type="text/javascript">
    <!--
    window.setInterval("updateMsgList()", 5000);

    updateMsgList();
    -->
</script>
<% } %>

</center>
</div>

<%@ include file="footer.inc.jsp" %>