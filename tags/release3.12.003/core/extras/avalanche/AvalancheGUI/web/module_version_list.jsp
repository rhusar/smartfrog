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
<%@ include file="header.inc.jsp"%>
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>

<%
    String errMsg = null; 
    ModulesManager modulesMgr = factory.getModulesManager();
    
    if( null == modulesMgr ){
	errMsg = "Error connecting to Modules database" ;
	throw new Exception ( "Error connecting to Modules database" );
    }
    
    String moduleId = request.getParameter("moduleId");
%>

<script language="JavaScript" type="text/javascript">
    <!--
function toggle(divId) {
    var state = document.getElementById(divId).style.display ;
    if ( state == "none" ) {
	document.getElementById(divId).style.display = "block";
    } else {
	document.getElementById(divId).style.display = "none";
    }
}   

function submit(target){
    document.moduleListFrm.action = target ;
    document.moduleListFrm.submit();
}

setNextSubtitle("Module Versions Page");

oneVoiceWritePageMenu("ModuleVersions","header");
    -->
</script>

<br/>

<form id="moduleListFrm" name="moduleListFrm" method="post" action="">

<%@ include file="message.inc.jsp" %>

<!-- Actual Body starts here -->
<br/><br/>
<div align="center">

<table id="hostListTable" class="dataTable" 
	style="width: 200px; border-collapse: collapse;">
<caption>
Module: <a href="module_view.jsp?moduleId=<%=moduleId %>"><%=moduleId %></a>
</caption>
    <tbody>
	<tr> 
	    <td class="medium">Select</td>
	    <td class="medium">Version</td>
	</tr>
<%
	ModuleType m = modulesMgr.getModule(moduleId);
	VersionType versions[] = m.getVersionArray();
	for( int j=0;j<versions.length;j++){
		String verNum = versions[j].getNumber();
%>			
	<tr>
	    <td class="medium" align="center">
		<input type="checkbox" name="selectedVersion" 
			value="<%=verNum%>">
	    </td>
	    <td class="medium">
		<a href="module_version_view.jsp?moduleId=<%=moduleId%>&&version=<%=verNum %>">
		  <%=verNum %>
		</a>
	    </td>
	</tr>
<%
	}
%>
		
    </tbody>
</table>

<div style="width: 250px;">
<br>
<script language="JavaScript" type="text/javascript">
oneVoiceWritePageMenu("ModuleVersions","",
  "Delete selected",
  	"javascript:submit('module_save.jsp?moduleId=<%=moduleId%>&&pageAction=delModVer')",
  "Add",
  	"javascript:toggle('newVersion')"
);
</script>
</div>
</div>
</form>

<center>
<div id="newVersion" style="display:none">

<form method="post" action="module_save.jsp?moduleId=<%=moduleId%>&&pageAction=addModVer">
<table>
<tr>
    <td class="medium">
	Version Identifier:
    </td> 
    <td class="medium">	
	<input type="text" name="newVersion">
    </td> 
</tr>
</table>
    <br>
    <input type="submit" value="Add Version" name="AddDistro" class="btn">
</form>
</div>

</center>

<%@ include file="footer.inc.jsp"%>
