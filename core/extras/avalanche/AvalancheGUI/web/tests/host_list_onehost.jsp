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
<title>host_list_onehost</title>
</head>
<body>
<table cellpadding="1" cellspacing="1" border="1">
<thead>
<tr><td rowspan="1" colspan="3">host_list_onehost</td></tr>
</thead><tbody>
<tr>
	<td>open</td>
	<td>/AvalancheGUI/main.jsp</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=List Active Hosts</td>
	<td></td>
</tr>
<jsp:include page="header.inc.jsp?subtitle=List Active Hosts Page"></jsp:include>
<tr>
	<td>assertText</td>
	<td>//caption</td>
	<td>Active Hosts</td>
</tr>
<tr>
	<td>assertTable</td>
	<td>hostListTable.1.1</td>
	<td>avala-prj-1.hpl.hp.com</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//center/table/tbody/tr/td[3]</td>
	<td>[Logs] [Settings]</td>
</tr>
<tr>
	<td>assertTable</td>
	<td>hostListTable.1.3</td>
	<td>linux, IA64</td>
</tr>
<tr>
	<td>assertTable</td>
	<td>hostListTable.0.4</td>
	<td>Status</td>
</tr>
<!--Test selection of checkboxes.-->
<tr>
	<td>click</td>
	<td>//input[@type='checkbox']</td>
	<td></td>
</tr>
<tr>
	<td>assertValue</td>
	<td>selectedHost</td>
	<td>on</td>
</tr>
<tr>
	<td>click</td>
	<td>//input[@type='checkbox']</td>
	<td></td>
</tr>
<tr>
	<td>assertValue</td>
	<td>selectedHost</td>
	<td>off</td>
</tr>

</tbody></table>
</body>
</html>
