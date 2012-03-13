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
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.core.activeHostProfile.*"%>
<%@ include file="header.inc.jsp"%>
<%! String pageAction1 = "" ; %>
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
		pageAction1= "pageAction1="+"viewAll";
  	  	targetHosts = apm.listProfiles() ;
  	}else if( pageAction.equals("viewAll")){
		pageAction1= "pageAction1="+pageAction;
  	  	targetHosts = apm.listProfiles() ;
  	}else if ( pageAction.equals("viewSelected")){
  	  	targetHosts = request.getParameterValues("hostId");
		
		
  	  	if( null == targetHosts){
  	  	  		targetHosts = new String[0];
  	  	}
		pageAction1= "pageAction1="+pageAction+"&"+"targetHosts="+targetHosts[0];
  	}
	
%>

<script language="javascript" type="text/javascript">
    <!--   

function submit(target){
	document.moduleListFrm.action = target ;
	document.moduleListFrm.submit();
}
function deleteModule() {
        var selectors = document.getElementsByName("selectedModule");
        var selectedModules = new Array();

        for (var i = 0; i < selectors.length; i++)
        {
            if (selectors[i].checked)
                selectedModules.push(selectors[i]);
        }

        var count = selectedModules.length;
        if (count == 0)
        {
            alert("You must select one or more modules for this action.");
            return;
        }

        var alertMsg = "This action will delete ";
        if (count == 1)
            alertMsg += "one modules."
        else
            alertMsg += count + " modules."

        alertMsg += " Are you sure you want to continue?";

        if (confirm(alertMsg)) {
			
			document.moduleListFrm.action = "log_save.jsp?pageAction=dellog"+"&"+"<%=pageAction1%>";
            document.moduleListFrm.submit();
        }
		
    }

setNextSubtitle("Active View Page");
    -->
</script>

<!-- This is the page menu -->
<br/>

<div align="center">
<center>
<form id="moduleListFrm" name="moduleListFrm" method="post" action="log_save.jsp">
<div align="center" style="width: 95%;">
  <script language="javascript" type="text/javascript">
      <!--
    oneVoiceWritePageMenu("ActiveView","header");
      -->
  </script>
</div>

<%@ include file="message.inc.jsp" %>

<!-- Actual Body starts here -->
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="hostListTable" >
    <thead>
	<tr class="captionRow"> 
	    <th>Host ID</th>
	    <th>Modules </th>
	    <th>Last Action</th>
	    <th>Current State</th>
	    <th>Last Updated</th>
		<th>     </th>
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
	<td><a href="log_reader.jsp?fileName=<%=moduleStates[j].getLogFile()%>&host=<%=targetHosts[i]%>"><%=moduleStates[j].getState() %></a>
	<br><br>
<%
	if (moduleStates[j].getReportPath() != null){
%>
		<a href="log_reader.jsp?fileName=<%=moduleStates[j].getLogFile()%>&reportPath=<%=moduleStates[j].getReportPath()%>&host=<%=targetHosts[i]%>">View Report</a>
<%
	}
%>
	</td>
	
<%
		}else{
%>			
<td  style="background:red;">	<%=st + "..." %></td>
<%
		}
%>
	    <td><%=moduleStates[j].getLastUpdated() %></td>
		<td class="checkboxCell">
                <input type="checkbox" rowselector="yes"
                       name="selectedModule" value="<%=targetHosts[i]%>,<%=moduleStates[j].getId()%>,<%=moduleStates[j].getLastUpdated() %>">
                </input>
            </td>
</tr>
<%
	    }
	}
    }
%>  		
    </tbody>
</table>
 <br/>

            <div align="center" style="width: 95%;">
                <script type="text/javascript" language="JavaScript">
                    oneVoiceWritePageMenu("ModulesList", "footer",
                            "Delete selected modules",
                            "javascript:deleteModule()"
                            );
                </script>

            </div>
	</form>
</center>

</div>

<%@ include file="footer.inc.jsp"%>
