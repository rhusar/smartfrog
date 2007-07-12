<!-- /**
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
*/
-->
<%@ page language="java" %>

<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.modules.*"%>
  	
<%@ include file="InitBeans.jsp" %>

<%
  	String errMsg = null; 
  	ModulesManager manager = factory.getModulesManager();
  	
  	if( null == manager ){
  		errMsg = "Error connecting to modules database" ;
  		throw new Exception ( "Error connecting to modules database" );
  	}
  	
  	String moduleId = request.getParameter("moduleId");
  	String versionNumber = request.getParameter("version");

%>

<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<%@ include file="common.jsp" %>
</head>

<script language="javascript">
 function toggle(divId)
 {
   var state = document.getElementById(divId).style.display ;
   if ( state == "none" )
   {
     document.getElementById(divId).style.display = "block";
   }else{
     document.getElementById(divId).style.display = "none";
   }
 }   

function submit(target){
	document.moduleListFrm.action = target ;
	document.moduleListFrm.submit();
}
</script>

<body>
<script>
setNextSubtitle("Module Distributions Page");
</script>


<br/>
<form id='moduleListFrm' name='moduleListFrm' method='post'>

<!-- This is the page menu -->
<script>
oneVoiceWritePageMenu("ModuleDistros","header");
</script>

<%@ include file="Message.jspjsp" %>
<!-- Actual Body starts here -->
<div align="center">
<br/><br/>
<table id="hostListTable" class="dataTable" style="width: 300px; border-collapse: collapse;">
<caption>
Module: <a href="ViewModule.jsp?moduleId=<%=moduleId %>"><%=moduleId %></a> &gt;&gt; <a href="ModuleVersions.jsp?moduleId=<%=moduleId %>&&version=<%=versionNumber %>"><%=versionNumber %></a>
</caption>
    <tbody>
	<tr> 
	</tr> 
	<tr> 
	    <td class="data">Select</td>
	    <td class="data">Distribution Details</td>
	</tr>
<%
	ModuleType m = manager.getModule(moduleId);
	VersionType versions[] = m.getVersionArray();
	VersionType version = null ;
	for( int j=0;j<versions.length;j++){
		if( versionNumber.equals(versions[j].getNumber()) ){
			version = versions[j];
			break;
		}
	}

	DistributionType []distros = version.getDistributionArray();
	for( int i=0;i<distros.length;i++){
		String id = distros[i].getId();
		PlatformSelectorType ps = distros[i].getPlatformSelector();
		String os = (ps.getOs()==null)?"":ps.getOs();
		String plaf = (ps.getPlatform()==null)?"":ps.getPlatform();
		String arch = (ps.getArch()==null)?"":ps.getArch();
%>			
	<tr>
	    <td class="data" align="center">
		<input type="checkbox" name="selectedDistro" value="<%=id%>">
	    </td>
	    <td class="data">
<a href="DistroActions.jsp?moduleId=<%=moduleId %>&&version=<%=versionNumber %>&&distroId=<%=id %>"><%=id %></a>
	<br/>(OS=<%=os %>,Arch=<%=arch %>,Platform=<%=plaf %>)
	    </td>
	</tr>
<%
		}
%>
	
</tbody>
</table>

<div style="width: 250px;">
<br>
<script>
oneVoiceWritePageMenu("ModuleDistros","",
  "Delete selected",
  	"javascript:submit('SaveModule.jsp?pageAction=delDistro&&moduleId=<%=moduleId%>&&version=<%=versionNumber%>')",
  "Add",
  	"javascript:toggle('newDistro')"
);
</script>
</div>

</div>
</form>

<center>
<div id="newDistro" style="display:none">

<form method="post" action="SaveModule.jsp?pageAction=addDistro&&moduleId=<%=moduleId%>&&version=<%=version.getNumber()%>">

<table>
<tr>
	<th class="data">
		Distribution ID  
	</th> 
	<td class="data">	
		<input type="text" name="id"></input>(unique)
	</td>
</tr>
<tr>	
	<th class="data">
		Operating system  
	</th>
	<td class="data"> 
		<input type="text" name="os"></input>
	</td>
</tr>
<tr>	
	<th class="data">
	Platform 
	</th>
	<td class="data">
		<input type="text" name="platform"></input>
	</td>
</tr>
<tr>
	<th class="data">	
		Architecture : 
	</th>
	<td class="data">
		<input type="text" name="arch"></input>
	</td>
</tr>
</table>
	<br>
	<input type="submit" value="Add" name="AddDistro" class="btn">
</form>
</div>

</center>

<script language="JavaScript" type="text/javascript">
        reconcileEventHandlers();
</script>
</body>
</body>

</html>
