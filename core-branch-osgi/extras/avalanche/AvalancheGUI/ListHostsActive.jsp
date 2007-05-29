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
<%-- $Id: ListHostsActive.jsp 478 2007-03-26 07:01:38Z ritu $ --%>
<%@ page language="java" %>

<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.core.host.*"%>
<%@	page import="org.smartfrog.avalanche.server.engines.sf.*"%>

<%@ include file="InitBeans.jsp" %>

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
<%@ include file="common.jsp" %>
</head>
<script language="javascript">

function submit(target){
	document.hostListFrm.action = target ;
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
setNextSubtitle("List Active Hosts Page");
</script>

<form id='hostListFrm' name='hostListFrm' method='post'>

<!-- This is the page menu -->
<br/>
<div align="center">
<center>
<div align="center" style="width: 95%;">
<script>
  oneVoiceWritePageMenu("ListHostsActive","header",
    "Add a host",
  	"javascript:submit('HostBS.jsp')"
);
  </script>
</div>

<%@ include file="Message.jsp" %>
<!-- Actual Body starts here -->
<table  border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="hostListTable"> 
  <caption>Active Hosts</caption>
  <thead>
    <tr class="captionRow"> 
	<th class="checkboxCell"><input type="checkbox" tableid="hostListTable"></th>
	<th>Host ID</th>
	<th>Manage</th>
	<th>Platform</th>
	<th>Status</th>
	<!-- th>Console</th -->
    </tr>
  </thead>
<tbody>
<%
    String rowClass = "";
    SFAdapter adapter = new SFAdapter(factory);
    for( int i=0;i<hosts.length;i++ ){
	rowClass = rowClass == "" ? "class='altRowColor'" : "";
	HostType h = null;
	String os = "";
	String arch = "";
	boolean sfError = false;
	boolean state = false;

	String URLhostid = "hostId=" + hosts[i];

	try{
	    h = manager.getHost(hosts[i]);
	    os = h.getPlatformSelector().getOs();
	    arch = h.getPlatformSelector().getArch();

	    try{
		state = adapter.isActive(hosts[i]);
	    }catch(Throwable t){
		    t.printStackTrace();
		    sfError = true;
	    }

	}catch(NullPointerException e){
	    // ugly patch for xindice bug
	    os = "Error !!";
	}catch(Exception e){
	    // do nothing
	}
%>  		
    <tr <%=rowClass %>>
	<td class="checkboxCell">
	    <input type="checkbox" rowselector="yes"
		name="selectedHost" value="<%=hosts[i]%>"></input>
	</td>
	<td><%=hosts[i]%></td>
	<td>
	  <table cellspacing="0" cellpadding="0">
	    <tr>
	      <td>
	        <a href="ActiveView.jsp?pageAction=viewSelected&<%=URLhostid%>">
	        [Logs]
	        </a>
	      </td>
	      <td class="data">
	        <a href="javascript:submit('HostBS.jsp?<%=URLhostid%>')">
	        [Settings]
	        </a>
	      </td>
	    </tr>
	  </table>
	</td>
	<td><%=os%>, <%=arch %></td>
		<td>
	    	  <%=state?"Available":"Not Available"%>
		</td>
		<!-- td>
	        <a href="javascript:submit('OpenConsole.jsp?<%=URLhostid%>')">
	        [ManagementConsole]
	        </a>
	      </td>
		<td>
		<div  class="verticalButtonSet"><div><div>
	    	<input type="button" class="hpButtonSmall" title="managementConsole" 
		onclick="javascript:submit('OpenConsole.jsp?<%=URLhostid%>')"></input>
		</div></div></div>
		</td>
		<td>
		<div align="center" style="width: 95%;">
  		<script>
    			oneVoiceWritePageMenu("ListHostsActive","footer",
      			"Management interface",
  			"javascript:submit('OpenConsole.jsp?<%=URLhostid%>')");
  		</script>
		</div>
		</td-->
		<!-- td class="checkboxCell">
	    		<input type="checkbox" rowselector="yes"
			name="selectedHost" value="<%=hosts[i]%>"></input>
		</td -->
    </tr>
<%
    }
%>
</tbody>
</table>

</br>
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("ListHostsActive","footer",
      "Delete selected hosts",
  	"javascript:doDeleteHosts('DeleteHosts.jsp')",
      "Stop Avalanche on selected hosts",
  	"javascript:submit('IgniteHost.jsp?pageAction=unIgnite')",
      "Ignite selected hosts",
  	"javascript:submit('IgniteHost.jsp?pageAction=ignite')",
	"Open Management Interface of selected hosts",
  	"javascript:submit('OpenConsole.jsp')"
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
