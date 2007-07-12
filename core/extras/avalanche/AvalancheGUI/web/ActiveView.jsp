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
<%-- $Id: ActiveView.jsp 81 2006-05-30 06:09:38Z uppada $ --%>
<%@ page language="java" %>

<%@	page import="org.smartfrog.avalanche.server.modules.*"%>
<%@	page import="org.smartfrog.avalanche.core.activeHostProfile.*"%>

<%@ include file="InitBeans.jsp" %>

<%
  	String errMsg = null; 

  	ActiveProfileManager apm = factory.getActiveProfileManager();
  	
  	if( null == apm ){
  		errMsg = "Error connecting to active profiles database" ;
  		throw new Exception ( "Error connecting to active profiles database" );
  	}

  	String pageAction = request.getParameter("pageAction");
	String []targetHosts = new String[0];

  	if( null == pageAction ){
  	  	targetHosts = apm.listProfiles() ;
  	}else if( pageAction.equals("viewAll")){
  	  	targetHosts = apm.listProfiles() ;
  	}else if ( pageAction.equals("viewSelected")){
  	  	targetHosts = request.getParameterValues("hostId");
  	  	if( null == targetHosts){
  	  	  		targetHosts = new String[0];
  	  	}
  	}
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
     document.getElementById(divId).style.visibility = "block";
   }else{
     document.getElementById(divId).style.visibility = "none";
   }
 }   

function submit(target){
	document.moduleListFrm.action = target ;
	document.moduleListFrm.submit();
}
</script>

<body>
<script>
setNextSubtitle("Active View Page");
</script>

<!-- This is the page menu -->
<br/>
<div align="center">
<center>
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("ActiveView","header"
    //  "All Hosts",
    //  	"ActiveView.jsp"
    );
  </script>
</div>

<%@ include file="Message.jsp" %>

<!-- Actual Body starts here -->
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="hostListTable" >
    <thead>
	<tr class="captionRow"> 
	    <th>Host ID</th>
	    <th>Modules </th>
	    <th>Last Action</th>
	    <th>Current State</th>
	    <th>Last Updated</th>
	</tr>
    </thead>
    <tbody>
<%
        String rowClass = "";
	for( int i=0;i<targetHosts.length;i++ ){
	  rowClass = rowClass == "" ? "class='altRowColor'" : "";
%>
	<tr <%=rowClass %>>
	    <td><%=targetHosts[i] %></td>
	    <td class="data"></td>
	    <td class="data"></td>
	    <td class="data"></td>
	    <td class="data"></td>
	</tr>

<% 
	ActiveProfileType profile = apm.getProfile(targetHosts[i]);

	if( null != profile ){

	    ModuleStateType[] moduleStates =  profile.getModuleStateArray();
	    for( int j= 0;j<moduleStates.length;j++){
		String logFile = moduleStates[j].getLogFile();
		String st = moduleStates[j].getState();
		if( st.length() > 100 ){
			st = st.substring(0,100);
		}
%>
<tr <%=rowClass %>>
	<td></td>
	<td><%=moduleStates[j].getId() %></td>
	<td><%=moduleStates[j].getLastAction() %></td>
<%
		if( null != logFile ){
%>			
	<td><a href="LogReader.jsp?filePath=<%=moduleStates[j].getLogFile() %>"><%=moduleStates[j].getState() %></a>
	</td>
<%
		}else{
%>			
<td  style="background:red;">	<%=st + "..." %></td>
<%
		}
%>
	    <td><%=moduleStates[j].getLastUpdated() %></td>
</tr>
<%
	    }
	}
    }
%>  		
    </tbody>
</table>
</center>
</div>

</body>

</html>
