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
	<td>assertElementPresent</td>
	<td>//img[@alt='SmartFrog']</td>
	<td></td>
</tr>
<!-- Check for SmartFrog textlink -->
<tr>
	<td>assertText</td>
	<td>link=Avalanche Deployment System</td>
	<td>Avalanche Deployment System</td>
</tr>
<!-- Check for correct subtitle -->
<tr>
	<td>assertTitle</td>
	<td>exact:Avalanche: <%=subtitle%></td>
	<td></td>
</tr>
<tr>
	<td>assertText</td>
	<td>subtitle</td>
	<td>(<%=subtitle%>)</td>
</tr>
<!-- Is menu reachable? -->
<tr>
	<td>assertElementPresent</td>
	<td>menu1_trigger</td>
	<td></td>
</tr>
<tr>
	<td>assertElementPresent</td>
	<td>menu2_trigger</td>
	<td></td>
</tr>
<tr>
	<td>assertElementPresent</td>
	<td>menu3_trigger</td>
	<td></td>
</tr>
<tr>
	<td>assertElementPresent</td>
	<td>menu4_trigger</td>
	<td></td>
</tr>
<!-- Check menu for correctness -->
<!-- Menu: Modules -->
<tr>
	<td>assertText</td>
	<td>menu1_trigger</td>
	<td>Software Resources List Modules Module Groups</td>
</tr>
<tr>
	<td>verifyText</td>
	<td>link=List Modules</td>
	<td>List Modules</td>
</tr>
<tr>
	<td>verifyText</td>
	<td>link=Module Groups</td>
	<td>Module Groups</td>
</tr>
<!-- Menu: Host -->
<tr>
	<td>assertText</td>
	<td>menu2_trigger</td>
	<td>Hosts List Hosts Host Groups</td>
</tr>
<tr>
	<td>assertText</td>
	<td>link=List Hosts</td>
	<td>List Hosts</td>
</tr>
<tr>
	<td>assertText</td>
	<td>link=Host Groups</td>
	<td>Host Groups</td>
</tr>
<!-- Menu: Settings -->
<tr>
	<td>assertText</td>
	<td>menu3_trigger</td>
	<td>Configuration Supported Actions Deployment Engines System Settings</td>
</tr>
<tr>
	<td>assertText</td>
	<td>link=Supported Actions</td>
	<td>Supported Actions</td>
</tr>
<tr>
	<td>assertText</td>
	<td>link=Deployment Engines</td>
	<td>Deployment Engines</td>
</tr>
<tr>
	<td>assertText</td>
	<td>link=System Settings</td>
	<td>System Settings</td>
</tr>
<!-- Menu: Logs -->
<tr>
	<td>assertText</td>
	<td>menu4_trigger</td>
	<td>Reports View Logs View XMPP Messages</td>
</tr>
<tr>
	<td>assertText</td>
	<td>link=View Logs</td>
	<td>View Logs</td>
</tr>
<tr>
	<td>assertText</td>
	<td>link=View XMPP Messages</td>
	<td>View XMPP Messages</td>
</tr>