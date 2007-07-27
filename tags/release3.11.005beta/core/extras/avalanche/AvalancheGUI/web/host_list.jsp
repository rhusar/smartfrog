<% /**
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
*/ %>
<%@ page language="java" %>

<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.core.host.*" %>

<% /* Include header and set title */ %>
<%@ include file="header.inc.jsp"%>

<script type="text/javascript" language="JavaScript">
    <!--
    setNextSubtitle("List Hosts Page");
    -->
</script>

<form id='hostListFrm' name='hostListFrm' method='post' action="">
    <!-- This is the page menu -->
    <br/>

    <div align="center">
        <center>
            <div align="center" style="width: 95%;">
                <script language="JavaScript" type="text/javascript">
                    <!--
                    oneVoiceWritePageMenu("ListHost", "header", "Add a host", "javascript:window.location.href='host_setup_bs.jsp'");
                    -->
                </script>
            </div>

            <%@ include file="Message.jsp" %>

            <!-- Actual Body starts here -->
            <table border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="hostListTable">
                <caption>Hosts</caption>
                <thead>
                    <tr class="captionRow">
                        <th class="checkboxCell"><input type="checkbox" /></th>
                        <th>Host ID</th>
                        <th>Manage</th>
                        <th>Platform</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        HostManager manager = factory.getHostManager();
                        String[] hosts = manager.listHosts();
                        String rowClass = "";
                        if (hosts.length == 0) {
                    %>
                        <tr><td/><td/><td/><td/></tr>
                    <%
                        } else
                        for (int i = 0; i < hosts.length; i++) {
                            HostType h = null;
                            String os = "";
                            String arch = "";
                            boolean sfError = false;
                            h = manager.getHost(hosts[i]);
                            os = h.getPlatformSelector().getOs();
                            arch = h.getPlatformSelector().getArch();

                            String URLhostid = "hostId=" + hosts[i];
                            rowClass = (rowClass.equals(""))? "class='altRowColor'" : "";
                    %>
                    <tr <%=rowClass %>>
                        <td class="checkboxCell"><input type="checkbox" name="selectedHost" value="<%=hosts[i]%>" /></td>
                        <td><%=hosts[i]%></td>
                        <td>
                            <table cellspacing="0" cellpadding="0">
                                <tr>
                                    <td>
                                        <a href="host_list_active.jsp?pageAction=viewSelected&<%=URLhostid%>"> [Logs] </a>
                                    </td>
                                    <td>
                                        <a href="host_setup_bs.jsp?<%=URLhostid%>"> [Settings] </a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td><%=os%>, <%=arch %>
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
