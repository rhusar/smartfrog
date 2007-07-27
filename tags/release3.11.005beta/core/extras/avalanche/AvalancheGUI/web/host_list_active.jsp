<% /*
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
*/ %>
<%-- $Id: ListHostsActive.jsp 478 2007-03-26 07:01:38Z ritu $ --%>
<%@ page language="java" %>
<%@ include file="header.inc.jsp" %>
<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.core.host.*" %>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*" %>

<script language="JavaScript" type="text/javascript">
    <!--
    setNextSubtitle("List Active Hosts Page");
    -->
</script>

<form id="hostListFrm" name="hostListFrm" method="post" action="">

<!-- This is the page menu -->
<br/>

<div align="center">
<center>
<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        oneVoiceWritePageMenu("ListActiveHost", "header",
                "Add a host",
                "javascript:window.location.href='host_setup_bs.jsp'"
                );
    </script>
</div>

<%@ include file="Message.jsp" %>
<!-- Actual Body starts here -->
<table border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="hostListTable">
    <caption>Active Hosts</caption>
    <thead>
        <tr class="captionRow">
            <th class="checkboxCell"><input type="checkbox" tableid="hostListTable"></th>
            <th>Host ID</th>
            <th>Manage</th>
            <th>Platform</th>
            <th>Status</th>
        </tr>
    </thead>
    <tbody>
        <%
            String errMsg = null;
            HostManager manager = factory.getHostManager();

            if (null == manager) {
                errMsg = "Error connecting to hosts database";
                throw new Exception("Error connecting to hosts database");
            }

            String[] hosts = manager.listHosts();

            String rowClass = "";
            SFAdapter adapter = new SFAdapter(factory);
            for (int i = 0; i < hosts.length; i++) {
                rowClass = rowClass.length()==0? "class='altRowColor'" : "";
                HostType h = null;
                String os = "";
                String arch = "";
                boolean sfError = false;
                boolean state = false;

                String URLhostid = "hostId=" + hosts[i];

                try {
                    h = manager.getHost(hosts[i]);
                    os = h.getPlatformSelector().getOs();
                    arch = h.getPlatformSelector().getArch();

                    try {
                        state = adapter.isActive(hosts[i]);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        sfError = true;
                    }

                } catch (NullPointerException e) {
                    // ugly patch for xindice bug
                    os = "Error !!";
                } catch (Exception e) {
                    // do nothing
                }
        %>
        <tr <%=rowClass %>>
            <td class="checkboxCell">
                <input type="checkbox" rowselector="yes"
                       name="selectedHost" value="<%=hosts[i]%>"></input>
            </td>
            <td><%=hosts[i]%>
            </td>
            <td>
                <table cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <a href="log_view.jsp?pageAction=viewSelected&<%=URLhostid%>">
                                [Logs]
                            </a>
                        </td>
                        <td class="data">
                            <a href="host_setup_bs.jsp?<%=URLhostid%>">
                                [Settings]
                            </a>
                        </td>
                    </tr>
                </table>
            </td>
            <td><%=os%>, <%=arch %>
            </td>
            <td>
                <%=state ? "Available" : "Not Available"%>
            </td>
        </tr>
        <%
            }
        %>
    </tbody>
</table>

<br/>

<%@ include file="host_actions.inc.jsp"%>

</center>
</div>
</form>

<%@ include file="footer.inc.jsp" %>