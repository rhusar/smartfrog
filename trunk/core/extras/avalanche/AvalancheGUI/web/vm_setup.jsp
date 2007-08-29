<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater" %>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType" %>
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
    setNextSubtitle("Setup a Virtual Machine");
    -->
</script>

<form id="addVMFrm" name="addVMFrm" method="post" action="">
<div align="center">
<center>

<br/>
<table id="vmListTable" class="dataTable"
    style="width: 400px; border-collapse: collapse;">
    <caption>Basic Settings</caption>
    <tbody>

<%
    String strHost = request.getParameter("host");
    String strVM = request.getParameter("vm");
    String strTarget = "";
    String strCaption = "";
    if (strVM == null) {
        // create a new virtual machine on submit
        strTarget = "javascript:document.addVMFrm.action='vm_actions.jsp?action=create&host=" + strHost + "'; document.addVMFrm.submit();";

        strCaption = "Create VM";

        // get the active host profile
        ActiveProfileUpdater updater = new ActiveProfileUpdater();
        ActiveProfileType type = updater.getActiveProfile(strHost);
        if (type != null) {
        %>
<tr>
    <td class="medium" align="right">Master images:</td>
    <td class="medium">
        <select name="vmmasterpath">
            <%
                for (String s : type.getVmMasterCopyArray()) {
                    %>
                        <option><%=s%></option>
                    <%
                }
            %>
        </select>
    </td>
</tr>
<tr>
    <td class="medium" align="right">Name for the VM:</td>
    <td class="medium">
        <input type="text" name="vmpath" id="host" class="default" />
    </td>
</tr>

        <%
            }
    } else {
        strCaption = "Save Changes";
        strTarget = "javascript:document.addVMFrm.action='vm_actions.jsp?action=save&host=" + strHost + "'; document.addVMFrm.submit();";
        %>
    <tr>
	    <td class="medium" align="right">Path to the VM:</td>
	    <td class="medium">
            <input type="text" name="vmpath" size="30" id="host" disabled="true" value="<%= strVM %>" class="default" />
        </td>
    </tr>
    <%  // add further information here
        // like login, password, hostname
        }

    %>
    </tbody>
</table>

<br/>
<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        <!--
        oneVoiceWritePageMenu(  "VMSetup", "footer",
                                "<%=strCaption%>", "<%=strTarget%>");
        -->
    </script>
</div>

</center>
</div>
</form>

<%@ include file="footer.inc.jsp" %>