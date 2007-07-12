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
<%-- $Id: ListHosts.jsp 81 2006-05-30 06:09:38Z uppada $ --%>
<%@ page language="java" %>

<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.core.host.*"%>
<%@	page import="org.smartfrog.avalanche.server.engines.sf.*"%>

<%@ include file="InitBeans.jspjsp" %>

<%
  	String errMsg = null; 
  	HostManager manager = factory.getHostManager();

  	if( null == manager ){
  		errMsg = "Error connecting to hosts database" ;
  		throw new Exception ( "Error connecting to hosts database" );
  	}
  	
  	String []hosts = manager.listHosts();

%>

<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<%@ include file="common.jspjsp" %>
</head>
<script language="javascript">

function submit(target){
	document.hostListFrm.action = target;
	document.hostListFrm.submit();
}

function doDeleteHosts(target){
    var selectors = document.getElementsByName("selectedHost");
    var selectedHosts = new Array();

    for (var i = 0; i < selectors.length; i++)
    {   if (selectors[i].checked)
    	   selectedHosts.push(selectors[i]);
    }

    var count = selectedHosts.length;
    if (count == 0)
    {   alert("You must select one or more hosts for this action.");
        return;
    }
  
    var alertMsg = "This action will permanently delete ";
    if (count == 1)
    	alertMsg += "one host."
    else
    	alertMsg += count + " hosts."
    alertMsg += " Are you sure you want to continue?";
    if (confirm(alertMsg))
        submit(target);
}
</script>

<body>
<script>
setNextSubtitle("List Hosts Page");
</script>


<form id='hostListFrm' name='hostListFrm' method='post'>
<!-- This is the page menu -->
<br/>
<div align="center">
<center>
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("ListHosts","header",
      "Add a host",
  	"javascript:submit('HostBS.jsp')"
    );
  </script>
</div>

<%@ include file="Message.jspjsp" %>
<!-- Actual Body starts here -->
<table border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="hostListTable">
        <caption>Hosts</caption>
        <thead>
	  <tr class="captionRow">
	    <th class="checkboxCell"><input type="checkbox" tableid="hostListTable"></th>
    	    <th>Host ID</th>
    	    <th>Manage</th>
    	    <th>Platform</th>
	  </tr>
	</thead>
	<tbody>
<%
      String rowClass = "";
      for( int i = 0; i < hosts.length; i++ ){
	  HostType h = null;
	  String os = "";
	  String arch = "";
	  boolean sfError = false;
	  h = manager.getHost(hosts[i]);
	  os = h.getPlatformSelector().getOs();
	  arch = h.getPlatformSelector().getArch();

	  String URLhostid = "hostId=" + hosts[i];
	  rowClass = rowClass == "" ? "class='altRowColor'" : "";
%>  		
      <tr <%=rowClass %>>
	<td class="checkboxCell"><input type="checkbox" rowselector="yes" 
	    name="selectedHost" value="<%=hosts[i]%>"></input></td>
        <td><%=hosts[i]%></td>
	<td>
	  <table cellspacing="0" cellpadding="0">
	    <tr>
	      <td>
		<a href="ActiveView.jsp?pageAction=viewSelected&&<%=URLhostid%>"> [Logs] </a>
              </td>
              <td>
                <a href="javascript:submit('HostBS.jsp?<%=URLhostid%>')"> [Settings] </a>
              </td>
            </tr>
          </table>
    	</td>
    	<td><%=os%>, <%=arch %></td>
      </tr>
<%
	  }
%>
</tbody>
</table>

<br/>
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("ListHosts","footer",
      "Delete selected hosts",
  	"javascript:doDeleteHosts('DeleteHosts.jsp')",
      "Stop Avalanche on selected hosts",
  	"javascript:submit('IgniteHost.jsp?pageAction=unIgnite')",
      "Ignite selected hosts",
  	"javascript:submit('IgniteHost.jsp?pageAction=ignite')"
    );
  </script>
</div>
</center>
</div>
</form>
<script language="JavaScript" type="text/javascript">
        reconcileEventHandlers();
</script>
</body>

</html>
