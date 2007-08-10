<%-- /**
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
*/ --%>
<%@ page language="java" %>
<%@ include file="header.inc.jsp"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>

<%
  	String errMsg = null;
  	SettingsManager settMgr = factory.getSettingsManager();
  	if( null == settMgr ){
  		errMsg = "Error connecting to settings database" ;
  		throw new Exception ( "Error connecting to settings database" );
  	}
  	SettingsType defSettings = settMgr.getDefaultSettings();
%>

<script type="text/javascript" language="JavaScript">
<!--
 function toggle(divId)
 {
   var state = document.getElementById(divId).style.visibility ;
   if ( state == "hidden" )
   {
     document.getElementById(divId).style.visibility = "visible";
   }else{
     document.getElementById(divId).style.visibility = "hidden";
   }
 }

 function addRow1(table, name)
 {
	   var len = table.rows.length ;

	   var newRow = document.createElement("tr");
	   var idx = "" + (len ) ;

	   var col1 = document.createElement("td");
	   var d2 = document.createElement("input");
	   d2.setAttribute('type', 'text');
	   d2.setAttribute('name', name );
	   col1.appendChild(d2);

	   newRow.appendChild(col1);
	   table.getElementsByTagName("tbody")[0].appendChild(newRow);
 }

 function addRow2(table, name1, name2)
 {
	   var len = table.rows.length ;

	   var newRow = document.createElement("tr");
	   var idx = "" + (len ) ;

	   var col1 = document.createElement("td");
	   var d2 = document.createElement("input");
	   d2.setAttribute('type', 'text');
	   d2.setAttribute('name', name1 + '.' + idx );
	   col1.appendChild(d2);

	   var col2 = document.createElement("td");
	   var d3 = document.createElement("input");
	   d3.setAttribute('type', 'text');
	   d3.setAttribute('name', name2 + '.' + idx );
	   col2.appendChild(d3);

	   newRow.appendChild(col1);
	   newRow.appendChild(col2);
	   table.getElementsByTagName("tbody")[0].appendChild(newRow);
 }

function addRow3(table, t2id)
 {
	   var len = table.rows.length ;

	   var newRow = document.createElement("tr");
	   var idx = "" + (len ) ;

	   var col1 = document.createElement("td");
	   var d2 = document.createElement("input");
	   d2.setAttribute('type', 'text');
	   d2.setAttribute('name', "action.name" + '.' + idx );
	   col1.appendChild(d2);

	   var col2 = document.createElement("td");

	   var t2 = document.createElement("table");
	   t2.setAttribute("id", 'actionArgumentTable' + idx);
	   var tbdy = document.createElement("tbody");
	   var r1 = document.createElement("tr");
	   var c1 = document.createElement("td");

	   r1.appendChild(c1)
	   tbdy.appendChild(r1);
	   t2.appendChild(tbdy);

		var a = document.createElement("a");
		a.setAttribute("href", "#");

		var tabName = t2id +idx;
		var fldName = 'action.' + idx+ '.argument' ;
		a.setAttribute("onclick",
			"javascript:addRow1(getElementById('" +
			 tabName + "'),"+ "'" + fldName +"')");
		a.appendChild(document.createTextNode("Add Argument"));

		var spn = document.createElement("span");
		spn.appendChild(t2);
		spn.appendChild(a);

	   col2.appendChild(spn);

	   newRow.appendChild(col1);
	   newRow.appendChild(col2);
	   table.getElementsByTagName("tbody")[0].appendChild(newRow);
 }

setNextSubtitle("System Settings Page");
-->
</script>

<br>
<div align="center">
<center>

<div align="center" style="width: 95%;">
  <script language="javascript" type="text/javascript">
    oneVoiceWritePageMenu("SystemSettings", "header");
  </script>
</div>


<%@ include file="Message.jsp" %>

<form method="post" action="system_settings_save.jsp">

<table border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="osTable">
        <caption>Supported Operating Systems</caption>
        <thead>
            <tr class="captionRow">
                <th class="checkboxCell"><input type="checkbox" tableid="hostListTable"></th>
                <th>Name</th>
                <th>Delete</th>
            </tr>
        </thead>
        <tbody>
        <% String [] oses = defSettings.getOsArray();
            for( int i = 0; i < oses.length; i++){ %>
        <tr>
            <td>
                <input type="text" name="os" value="<%=oses[i]%>"></input>
            </td>
        </tr>
        <% } %>
        </tbody>
</table>
<input type="button" name="os_add" value="Add Operating System" onclick="javascript:addRow1(getElementById('osTable'), 'os')"/>

<br/><br/>

<table id='platformTable' cellspacing="2" cellpadding="4" border="1" style="border-collapse: collapse" bordercolor="#00FFFF">
<tbody>
<tr>
	<td>
	Supported Platforms
	</td>
</tr>
<%
	String [] plafs = defSettings.getPlatformArray();
	for( int i=0;i<plafs.length;i++){
%>
<tr>
	<td>
		<input type="text" name="platform" value="<%=plafs[i]%>"></input>
	</td>
</tr>
<%
	}
%>
</tbody>
</table>
<a href="#" onclick="javascript:addRow1(getElementById('platformTable'),'platform')">Add Platform </a>
<br>
<br>

<table id='archTable' cellspacing="2" cellpadding="4" border="1" style="border-collapse: collapse" bordercolor="#00FFFF">
<tbody>
<tr>
	<td>
	Supported Architectures
	</td>
</tr>
<%
	String [] archs = defSettings.getArchArray();
	for( int i=0;i<archs.length;i++){
%>
<tr>
	<td>
		<input type="text" name="arch" value="<%=archs[i]%>"></input>
	</td>
</tr>
<%
	}
%>
</tbody>
</table>

<a href="#" onclick="javascript:addRow1(getElementById('archTable'),'arch')">Add Architecture </a>
<br><br>

<table id='accessModeTable' cellspacing="2" cellpadding="4" border="1" style="border-collapse: collapse" bordercolor="#00FFFF">
<tbody>
<tr>
	<td>
	Supported Access Modes
	</td>
</tr>
<%
	SettingsType.AccessMode []modes =  defSettings.getAccessModeArray();
	for( int i=0;i<modes.length;i++){
%>
<tr>
	<td>
		<input type="text" name="accessMode" value="<%=modes[i].getName()%>"></input>
	</td>
</tr>
<%
	}
%>
</tbody>
</table>
<a href="#" onclick="javascript:addRow1(getElementById('accessModeTable'),'accessMode')">Add Access Mode </a>
<br><br>

<table id='transferModeTable' cellspacing="2" cellpadding="4" border="1" style="border-collapse: collapse" bordercolor="#00FFFF">
<tbody>
<tr>
	<td>
	Supported Data Transfer Modes
	</td>
</tr>
<%
	SettingsType.DataTransferMode []dModes =  defSettings.getDataTransferModeArray();
	for( int i=0;i<dModes.length;i++){
%>
<tr>
	<td>
		<input type="text" name="dataTransferMode" value="<%=dModes[i].getName()%>"></input>
	</td>
</tr>
<%
	}
%>
</tbody>
</table>
<a href="#" onclick="javascript:addRow1(getElementById('transferModeTable'),'dataTransferMode')">Add Data Transfer Mode </a>
<br><br>

<table id='systemPropertiesTable' cellspacing="2" cellpadding="4" border="1" style="border-collapse: collapse" bordercolor="#00FFFF">
<tbody>
<tr>
	<td>
	Add System Property
	</td>
</tr>
<%
	String []props =  defSettings.getSystemPropertyArray();
	for( int i=0;i<props.length;i++){
%>
<tr>
	<td>
		<input type="text" name="prop" value="<%=props[i]%>"></input>
	</td>
</tr>
<%
	}
%>
</tbody>
</table>

<a href="#" onclick="javascript:addRow1(getElementById('systemPropertiesTable'),'prop')">Add System Property </a>
<br><br>

<table id='deploymentEngineTable' cellspacing="2" cellpadding="4" border="1" style="border-collapse: collapse" bordercolor="#00FFFF">
<tbody>
<tr>
	<td>
	Engine Name
	</td>
	<td>
	Adapter class
	</td>	
</tr>
<%
	SettingsType.DeploymentEngine[] engines =  defSettings.getDeploymentEngineArray();
	for( int i=0;i<engines.length;i++){
%>
<tr>
	<td>
		<input type="text" name="<%=("engine.name"+i)%>" value="<%=engines[i].getName()%>"></input>
	</td>
	<td>
		<input type="text" name="<%=("engine.class"+i)%>" value="<%=engines[i].getClass1()%>"></input>
	</td>
</tr>
<%
	}
%>
</tbody>
</table>

<a href="#" onclick="javascript:addRow2(getElementById('deploymentEngineTable'),'engine.name', 'engine.class')">
Add Deployment Engine </a>
<br>
<br>

<table id='actionsTable' cellspacing="2" cellpadding="4" border="1" style="border-collapse: collapse" bordercolor="#00FFFF">
<tbody>
<tr>
	<td>
	Action Name
	</td>
</tr>
<%
	SettingsType.Action[] actions =  defSettings.getActionArray();
	for( int i=0;i<actions.length;i++){
%>
<tr>
	<td>
		<input type="text" name="action" value="<%=actions[i].getName()%>"></input>
	</td>
</tr>
<%
	}
%>
</tbody>
</table>

<a href="#" onclick="javascript:addRow1(getElementById('actionsTable'),'action')">Add Action </a>
<br><br>



<input type="submit" name="Save" value="Save Changes"></input>
</form>

</center>
</div>

<%@ include file="footer.inc.jsp" %>