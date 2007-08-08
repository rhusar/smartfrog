<%-- /** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either

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
<title>host_adding</title>
</head>
<body>
<table cellpadding="1" cellspacing="1" border="1">
<thead>
<tr><td rowspan="1" colspan="3">host_adding</td></tr>
</thead><tbody>
<tr>
	<td>open</td>
	<td>/AvalancheGUI/host_list.jsp</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Add a host']</td>
	<td></td>
</tr>
<!--Setting up basic data-->
<jsp:include page="header.inc.jsp?subtitle=Host Basic Settings Page"></jsp:include>
<tr>
	<td>type</td>
	<td>hostId</td>
	<td>avala-prj-1.hpl.hp.com</td>
</tr>
<tr>
	<td>select</td>
	<td>os</td>
	<td>label=linux</td>
</tr>
<tr>
	<td>select</td>
	<td>arch</td>
	<td>label=IA64</td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>save</td>
	<td></td>
</tr>
<!--Editing access modes-->
<jsp:include page="header.inc.jsp?subtitle=Host Access Modes Page"></jsp:include>
<tr>
	<td>click</td>
	<td>//input[@value='Add an Access Mode']</td>
	<td></td>
</tr>
<tr>
	<td>type</td>
	<td>mode.userName.2</td>
	<td>root</td>
</tr>
<tr>
	<td>type</td>
	<td>mode.password.2</td>
	<td>UgbNLfUj</td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>save</td>
	<td></td>
</tr>
<!--Editing transfer modes-->
<jsp:include page="header.inc.jsp?subtitle=Host Transfer Modes Page"></jsp:include>
<tr>
	<td>click</td>
	<td>//input[@value='Add a Transfer Mode']</td>
	<td></td>
</tr>
<tr>
	<td>type</td>
	<td>mode.userName.2</td>
	<td>root</td>
</tr>
<tr>
	<td>type</td>
	<td>mode.password.2</td>
	<td>UgbNLfUj</td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>save</td>
	<td></td>
</tr>
<!--Editing enviroment variables-->
<jsp:include page="header.inc.jsp?subtitle=Host Properties Page"></jsp:include>
<tr>
	<td>type</td>
	<td>argument.value.2</td>
	<td>/usr/java/jdk1.6.0_01</td>
</tr>
<tr>
	<td>click</td>
	<td>//input[@value='Add a Property']</td>
	<td></td>
</tr>
<tr>
	<td>select</td>
	<td>argument.name.3</td>
	<td>label=AVALANCHE_HOME</td>
</tr>
<tr>
	<td>type</td>
	<td>argument.value.3</td>
	<td>/tmp</td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>save</td>
	<td></td>
</tr>
<!--Basic Settings page again-->
<jsp:include page="header.inc.jsp?subtitle=Host Basic Settings Page"></jsp:include>
<tr>
	<td>verifyText</td>
	<td>//div[2]/table/tbody/tr[1]/td[2]</td>
	<td>avala-prj-1.hpl.hp.com</td>
</tr>

</tbody></table>
</body>
</html>
