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
<%@ page contentType="text/html" language="java" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>New Test</title>
</head>
<body>
<table cellpadding="1" cellspacing="1" border="1">
<thead>
<tr><td rowspan="1" colspan="3">host_editing</td></tr>
</thead><tbody>
<tr>
	<td>open</td>
	<td>/AvalancheGUI/host_list.jsp?active=true</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=[Settings]</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Access Modes']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>exact:Avalanche: Host Access Modes Page</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Basic Settings']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>exact:Avalanche: Host Basic Settings Page</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Transfer Modes']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>exact:Avalanche: Host Transfer Modes Page</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Access Modes']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>exact:Avalanche: Host Access Modes Page</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Host Properties']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>exact:Avalanche: Host Properties Page</td>
	<td></td>
</tr>
</tbody></table>
</body>
</html>
