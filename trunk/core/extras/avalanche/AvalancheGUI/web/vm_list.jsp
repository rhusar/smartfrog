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

<%
    String strHost = request.getParameter("host");
%>

<script language="JavaScript" type="text/javascript">
    <!--
    setNextSubtitle("<%="Setup Virtual Machines for " + strHost%>");
    -->
</script>

<form id="vmListFrm" name="vmListFrm" method="post" action="">

<!-- This is the page menu -->
<br/>

<div align="center">
<center>
    <div align="center" style="width: 95%;">
        <script language="JavaScript" type="text/javascript">
            oneVoiceWritePageMenu("VMList", "header",
                    "Create a virtual machine",
                    "javascript:window.location.href='vm_setup.jsp?host=<%=strHost%>'",
                    "Start VMWare Server Service",
                    "javascript:window.location.href='vm_actions.jsp?action=startvmwareservice&host=<%=strHost%>'",
                    "Stop VMWare Server Service",
                    "javascript:window.location.href='vm_actions.jsp?action=stopvmwareservice&host=<%=strHost%>'"
                    );
        </script>
    </div>

    <%@ include file="message.inc.jsp" %>
    <!-- Actual Body starts here -->
    <table border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="vmListTable">
        <caption>Virtual Machines</caption>
        <thead id="vmListHeader">
            <tr class="captionRow">
                <th class="checkboxCell"><input id="allvms" onclick="" type="checkbox"/></th>
                <th class="sorted">Virtual Machine ID</th>
                <th>Manage</th>
                <th>Last Command</th>
                <th>Response</th>
            </tr>
        </thead>
        <tbody id="vmListBody"/>
    </table>

    <br/>

    <div align="center" style="width: 95%;">
        <script language="JavaScript" type="text/javascript">
            <!--
            oneVoiceWritePageMenu("VMList", "footer",
                        "Start selected VMs",
                        "javascript:perform('start')",
                        "Suspend selected VMs",
                        "javascript:perform('suspend')",
                        "Stop selected VMs",
                        "javascript:perform('stop')",
                        "Get state of selected Vms",
                        "javascript:perform('getstate')",
                        "Delete selected VMs",
                        "javascript:perform('delete')");
            -->
        </script>
    </div>

</center>
</div>
</form>

<script type="text/javascript" language="JavaScript" src="vm_list_ajax.js"></script>
<script language="JavaScript" type="text/javascript">
    <!--
    window.setInterval("updateVmList(\"<%=strHost%>\")", 5000);

    updateVmList("<%=strHost%>");
    -->
</script>

<%@ include file="footer.inc.jsp" %>