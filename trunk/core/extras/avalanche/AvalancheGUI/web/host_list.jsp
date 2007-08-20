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

<%@ page language="java" contentType="text/html" %>
<%@ include file="header.inc.jsp" %>
<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.core.host.*" %>

<%
    String errMsg = null;
    HostManager manager = factory.getHostManager();

    if (null == manager) {
        errMsg = "Error connecting to hosts database";
        throw new Exception("Error connecting to hosts database");
    }

    boolean boolListActiveHosts = false;
    try {
        boolListActiveHosts = Boolean.parseBoolean(request.getParameter("active").trim());
    } catch (Exception e) {
        // TODO: Add something useful here or forget about it
    }

    String[] hosts = manager.listHosts();
    String rowClass = "";

%>

<script language="JavaScript" type="text/javascript">
    <!--
    setNextSubtitle("List <% if (boolListActiveHosts) { %>Active <% } %>Hosts Page");
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

    <%@ include file="Message.jsp" %>
    <!-- Actual Body starts here -->
    <table border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="hostListTable">
        <caption><% if (boolListActiveHosts) { %>Active<% } %> Hosts</caption>
        <thead>
            <tr class="captionRow">
                <th class="checkboxCell"><input id="allhosts" type="checkbox" tableid="hostListTable" /></th>
                <th>Host ID</th>
                <th>Manage</th>
                <th>Platform</th>
                <% if (boolListActiveHosts) { %>
                <th>Status</th>
                <th>Recent Message</th>
                <% } %>
            </tr>
        </thead>
        <tbody>
            <%
                if (hosts.length != 0) {
                int count = 0;
                for (String host : hosts) {
                    rowClass = ((count++%2)==0)?"class=\"altRowColor\"":"";
                    HostType h = null;
                    String os = "";
                    String arch = "";

                    try {
                        h = manager.getHost(host);
                        os = h.getPlatformSelector().getOs();
                        arch = h.getPlatformSelector().getArch();
                    } catch (NullPointerException e) {
                        // ugly patch for xindice bug
                        // TODO: Sort out.
                        os = "Error !!";
                    } catch (Exception e) {
                        // do nothing
                    }
            %>
            <tr <%=rowClass %>>
                <td class="checkboxCell">
                    <input type="checkbox" rowselector="yes"
                           name="selectedHost" value="<%= host %>"></input>
                </td>
                <td><%= host %>
                </td>
                <td>
                    <a href="log_view.jsp?pageAction=viewSelected&hostId=<%= host %>">[Logs]</a>&nbsp;
                    <a href="host_setup_bs.jsp?hostId=<%= host %>">[Settings]</a>
                </td>
                <td><%=os%>, <%=arch %>
                </td>
                <% if (boolListActiveHosts) { %>
                <td>
                    <div id="<%= host %>_status" style=""></div>
                </td>
                <td>
                    <div id="<%= host %>_msg" style=""></div>
                </td>
                <% } %>
            </tr>
            <%
                }
                } else {
            %>
                <td colspan="<% if (boolListActiveHosts) { %>6<% } else {%>4<% } %>">There are no hosts in the database. To add hosts, please click on the "Add a host" button.</td>
            <%
                }
            %>
        </tbody>
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

<% if (boolListActiveHosts) { %>
<script type="text/javascript" language="JavaScript" src="host_list_ajax.js"></script>
<script language="JavaScript" type="text/javascript">
    <!--
    window.setInterval("getStatus()", 5000);

    getStatus();
    -->
</script>
<% } %>

<%@ include file="footer.inc.jsp" %>