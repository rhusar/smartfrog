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

<%@ page language="java" %>
<%@ include file="header.inc.jsp" %>

<script language="JavaScript" type="text/javascript">
    <!--
    setNextSubtitle("List Hosts");
    -->
</script>

<form id="hostListFrm" name="hostListFrm" method="post" action="">

<!-- This is the page menu -->
<br/>

<div align="center">
<center>
    <div align="center" style="width: 95%;">
        <script language="JavaScript" type="text/javascript">
            oneVoiceWritePageMenu("HostList", "header",
                    "Add a host",
                    "javascript:window.location.href='host_setup_bs.jsp'"
                    );
        </script>
    </div>

    <%@ include file="message.inc.jsp" %>
    <!-- Actual Body starts here -->
    <table border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="hostListTable">
        <caption>Hosts</caption>
        <thead id="hostListHeader">
            <tr class="captionRow">
                <th class="checkboxCell"><input id="allhosts" type="checkbox" tableid="hostListTable" /></th>
                <th class="sorted">Host ID</th>
                <th>Manage</th>
                <th>Platform</th>
                <th>Status</th>
                <th>Recent Message</th>
            </tr>
        </thead>
        <tbody id="hostListBody" />
    </table>

    <br/>

<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        <!--
        oneVoiceWritePageMenu(  "ListHosts", "footer",
                                "Delete selected hosts",
                                "javascript:deleteHosts()",
                                "Stop SmartFrog on selected hosts",
                                "javascript:stopHosts()",
                                "Start SmartFrog Console",
                                "javascript:openConsole()",
                                "Ignite selected hosts",
                                "javascript:igniteHosts()"); 
        -->
    </script>
</div>

</center>
</div>
</form>

<script type="text/javascript" language="JavaScript" src="host_list_ajax.js"></script>
<script language="JavaScript" type="text/javascript">
    <!--
    window.setInterval("getStatus()", 5000);

    getStatus();
    -->
</script>

<%@ include file="footer.inc.jsp" %>