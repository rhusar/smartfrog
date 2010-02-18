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
<%@ page language="java" contentType="text/html" %>
<%@ include file="header.inc.jsp"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>

<% SettingsManager settMgr = factory.getSettingsManager();
  	if( null == settMgr ){
  		throw new Exception ( "Error connecting to settings database" );
  	}
  	SettingsType defSettings = settMgr.getDefaultSettings(); %>

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

<br/>

<div align="center">
<center>

<div align="center" style="width: 95%;">
  <script language="javascript" type="text/javascript">
      <!--
    oneVoiceWritePageMenu("SystemSettings", "header");
      -->
  </script>
</div>


<%@ include file="message.inc.jsp" %>

<form method="post" action="system_settings_save.jsp">

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="osTable">
        <caption>Supported Operating Systems</caption>
        <thead>
            <tr class="captionRow">
                <th>Name</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% String[] oses = defSettings.getOsArray();
            for (String os : oses) { %>
            <tr>
                <td>
                    <input type="text" name="os" value="<%=os%>" />
                </td>
                <td>
                    <a href="">Delete</a>
                </td>
            </tr>
            <% } %>
        </tbody>
</table>
<input type="button" name="os_add" value="Add Operating System" onclick="addRow1(getElementById('osTable'), 'os')"/>

<br/><br/>

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="platformTable">
        <caption>Supported Platforms</caption>
        <thead>
            <tr class="captionRow">
                <th>Platform</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% String[] platforms = defSettings.getPlatformArray();
            for (String platform : platforms) { %>
            <tr>
                <td>
                    <input type="text" name="platform" value="<%=platform%>" />
                </td>
                <td>
                    <a href="">Delete</a>
                </td>
            </tr>
            <% } %>
        </tbody>
</table>
<input type="button" name="platform_add" value="Add Platform" onclick="addRow1(getElementById('platformTable'),'platform')"/>

<br/><br/>

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="archTable">
        <caption>Supported Architectures</caption>
        <thead>
            <tr class="captionRow">
                <th>Architecture</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% String[] archs = defSettings.getArchArray();
            for (String arch : archs) { %>
            <tr>
                <td>
                    <input type="text" name="platform" value="<%=arch%>" />
                </td>
                <td>
                    <a href="">Delete</a>
                </td>
            </tr>
            <% } %>
        </tbody>
</table>
<input type="button" name="arch_add" value="Add Platform" onclick="addRow1(getElementById('archTable'),'arch')"/>

<br/><br/>

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="accessModeTable">
        <caption>Supported Access Modes</caption>
        <thead>
            <tr class="captionRow">
                <th>Name</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% SettingsType.AccessMode[] accessmodes =  defSettings.getAccessModeArray();
            for (SettingsType.AccessMode accessmode : accessmodes) { %>
            <tr>
                <td>
                    <input type="text" name="platform" value="<%=accessmode.getName()%>" />
                </td>
                <td>
                    <a href="">Delete</a>
                </td>
            </tr>
            <% } %>
        </tbody>
</table>
<input type="button" name="access_add" value="Add Access Mode" onclick="addRow1(getElementById('accessModeTable'),'accessMode')"/>

<br/><br/>

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="transferModeTable">
        <caption>Supported Transfer Modes</caption>
        <thead>
            <tr class="captionRow">
                <th>Name</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% SettingsType.DataTransferMode[] transfermodes =  defSettings.getDataTransferModeArray();
            for (SettingsType.DataTransferMode transfermode : transfermodes) { %>
            <tr>
                <td>
                    <input type="text" name="transferMode" value="<%= transfermode.getName() %>" />
                </td>
                <td>
                    <a href="">Delete</a>
                </td>
            </tr>
            <% } %>
        </tbody>
</table>
<input type="button" name="transfer_add" value="Add Transfer Mode" onclick="addRow1(getElementById('transferModeTable'),'transferMode')"/>

<br/><br/>

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="systemPropertiesTable">
        <caption>System Properties</caption>
        <thead>
            <tr class="captionRow">
                <th>Name</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% String[] properties =  defSettings.getSystemPropertyArray();
            for (String property : properties) { %>
            <tr>
                <td>
                    <input type="text" name="systemProperty" value="<%= property %>" />
                </td>
                <td>
                    <a href="">Delete</a>
                </td>
            </tr>
            <% } %>
        </tbody>
</table>
<input type="button" name="property_add" value="Add Property" onclick="addRow1(getElementById('systemPropertiesTable'),'systemProperty')"/>

<br/><br/>

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="deploymentEngineTable">
        <caption>Supported Deployment Engines</caption>
        <thead>
            <tr class="captionRow">
                <th>Name</th>
                <th>Class</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% SettingsType.DeploymentEngine[] engines =  defSettings.getDeploymentEngineArray();
            for (SettingsType.DeploymentEngine engine : engines) { %>
            <tr>
                <td>
                    <input type="text" name="engine" value="<%=engine.getName()%>"/>
                </td>
                <td>
                    <input type="text" name="class" value="<%=engine.getClass1()%>"/>
                </td>
                <td>
                    <a href="">Delete</a>
                </td>
            </tr>
            <% } %>
        </tbody>
</table>
<input type="button" name="engine_add" value="Add Engine" onclick="addRow2(getElementById('deploymentEngineTable'),'engine.name', 'engine.class')"/>

<br/><br/>

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="actionTable">
        <caption>Supported Actions</caption>
        <thead>
            <tr class="captionRow">
                <th>Name</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <% SettingsType.Action[] actions = defSettings.getActionArray();
            for (SettingsType.Action action : actions) { %>
            <tr>
                <td>
                    <input type="text" name="action" value="<%= action.getName() %>" />
                </td>
                <td>
                    <a href="">Delete</a>
                </td>
            </tr>
            <% } %>
        </tbody>
</table>
<input type="button" name="action_add" value="Add Action" onclick="addRow1(getElementById('actionTable'),'action')"/>
</form>

<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        <!--
        oneVoiceWritePageMenu(  "SystemSettings", "footer",
                                "Save Changes", "javascript:document.form[0].submit()");
        -->
    </script>
</div>

</center>
</div>

<%@ include file="footer.inc.jsp" %>