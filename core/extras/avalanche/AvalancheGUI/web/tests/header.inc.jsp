<%-- /** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org */ --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String subtitle = request.getParameter("subtitle");
    if (subtitle != null) {
        // subtitle is set
        if (subtitle.trim().equals("")) {
            // subtitle is empty
            subtitle = "empty subtitle";
        }
    } else {
        subtitle = "subtitle not set";
    }
%>
<!-- Check for SmartFrog 'frog'-link -->
<tr>
	<td>clickAndWait</td>
	<td>//img[@alt='SmartFrog']</td>
	<td></td>
</tr>
<!-- Check for SmartFrog textlink -->
<tr>
    <td>click</td>
	<td>link=Avalanche Deployment System</td>
	<td></td>
</tr>
<!-- Check for correct subtitle -->
<tr>
	<td>assertTitle</td>
	<td>exact:Avalanche: <%=subtitle%></td>
	<td></td>
</tr>
<!-- Check for menu -->
<tr>
	<td>waitForTextPresent</td>
	<td>Software Resources List Modules Module Groups</td>
	<td></td>
</tr>
<tr>
	<td>waitForTextPresent</td>
	<td>Hosts List Hosts List Active Hosts Host Groups</td>
	<td></td>
</tr>
<tr>
	<td>waitForTextPresent</td>
	<td>Configuration Supported Actions Deployment Engines System Settings</td>
	<td></td>
</tr>
<tr>
	<td>waitForTextPresent</td>
	<td>Reports View Logs</td>
	<td></td>
</tr>