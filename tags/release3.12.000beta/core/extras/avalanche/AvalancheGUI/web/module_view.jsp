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
<%@ include file="header.inc.jsp" %>
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>

<%
  	String errMsg = null; 
  	ModulesManager moduleMgr = factory.getModulesManager();
  	
  	if( null == moduleMgr ){
  		errMsg = "Error connecting to Modules database" ;
  		throw new Exception ( "Error connecting to Modules database" );
  	}
  	String moduleId = request.getParameter("moduleId");
  	if( null == moduleId){
  		errMsg = "Null ModuleId" ;
  		throw new Exception ( errMsg );
  	}
  	ModuleType module = moduleMgr.getModule(moduleId);

%>

<script language="javascript" type="text/javascript">
 <!--
function selectColor(div){
	div.style.background = "#CC99FF" ;
}
function unSelectColor(div){
	div.style.background = "#CCFFFF" ;
}

function submit(formId, target){
	var form = document.getElementById(formId);
	form.action = target ;
	form.submit();
}

setNextSubtitle("View Module Page");
    -->
</script>

<br/>
<div align="center">
<center>

<!-- This is the page menu -->
<div align="center" style="width: 95%;">
  <script language="JavaScript" type="text/javascript">
    oneVoiceWritePageMenu("ViewModule","header",
      "Edit",
  	"javasrcript:setLocation('module_edit.jsp?moduleId=<%=moduleId %>')"
    );
  </script>
</div>

<%@ include file="Message.jsp" %>

<!-- Actual Body starts here -->
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="moduleTable" >
  <caption>Module Information</caption>
  <tbody>
    <tr> 
      <td style="width: 15%;" class="medium" align="right"><b>Module Id:</b></td>
      <td class="medium"><%=moduleId %></td>
    </tr>
    <tr>
      <td style="width: 15%;" class="medium" align="right"><b>Vendor:</b></td>
      <td class="medium"><%=module.getVendor() %></td>
    </tr>
    <tr>
      <td style="width: 15%;" class="medium" align="right"><b>Description:</b></td>
      <td class="medium"><%=module.getDescription() %></td>
    </tr>	
  </tbody>
</table>
 
<table border="0" cellpadding="0" cellspacing="0" class="dataTable">
  <caption>Version List</caption>
  <thead>
    <tr class="captionRow">
      <th style="width: 10%;">Version</th>
      <th style="width: 20%;">Platform:<br>(O/S, Architecture)</th>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody>
<%
    String rowClass = "";
    VersionType []versions= module.getVersionArray();
    for (int i=0; i < versions.length; i++) {
      rowClass = rowClass == "" ? "class='altRowColor'" : "";
      String strVersionNumber = versions[i].getNumber();
%>
    <tr <%=rowClass %>>
      <td class="medium"><a href="module_version_view.jsp?moduleId=Test&version=<%= strVersionNumber %>"><%= strVersionNumber %></a></td>
<%
      DistributionType []distros = versions[i].getDistributionArray();
	for (int j=0; j < distros.length; j++) {
	  PlatformSelectorType ps = distros[j].getPlatformSelector();
	  String os = "";
	  String platform = "";
	  String arch = "";

	  if( null != ps) {
	    os = ps.getOs();
	    platform = ps.getPlatform();
	    arch = ps.getArch();
	  }

%>
      <td class="medium"><%=os %>, <%=arch %> </td>
      <td class="medium"> 
	<form id="moduleListFrm<%=i %>" name="moduleListFrm"
	    method="post">
<%
	  ActionType []actions = distros[j].getActionArray();
	  String dId= distros[j].getId();
	    for( int k=0;k<actions.length;k++){
	      String actionName = actions[k].getName();
	      String title = actions[k].getConfiguration();
%>
	  <span class="headerMenuItem">
	     <!-- onMouseOver="selectColor(this)"
	     onMouseOut="unSelectColor(this)"> -->
	     <a href="javascript:submit('moduleListFrm<%=i %>','host_select.jsp?moduleId=<%=moduleId%>&version=<%= strVersionNumber %>&distroId=<%=dId%>&action=<%=title %>')"><%=actionName %></a>&nbsp;&nbsp;
	  </span>
<%
	    }
%>
	</form>
      </td>				
<%
	}
	if (distros.length == 0) {
%>
      <td colspan="99" class="data" align="center">
        No actions for this module.
      </td>
<%
	}
    }
%>
    </tr>
    </tbody>
</table>
</center>
</div>

<%@ include file="footer.inc.jsp"%>
