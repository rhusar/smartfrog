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

<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.core.host.*"%>
  
<%@ include file="InitBeans.jsp" %>
  	
<% 
  	String errMsg = null; 
  	HostManager manager = factory.getHostManager();
  	
  	if( null == manager ){
  		errMsg = "Error connecting to hosts database" ;
  		throw new Exception ( "Error connecting to hosts database" );
  	}
  	
  	SettingsManager settingsMgr = factory.getSettingsManager();
  	SettingsType defSettings = settingsMgr.getDefaultSettings();  
  	
	String []sysProps = defSettings.getSystemPropertyArray();
	
  	String hostId = request.getParameter("hostId");
  	String os = null; 
  	String plaf = null ;
  	String arch = null ;
  	
  	HostType host = manager.getHost(hostId);
  	if( null != hostId ){
	  	if( null != host ){
	  		PlatformSelectorType ps = host.getPlatformSelector();
	  		if( null != ps ){
		  		os 	 = ps.getOs();
		  		plaf = ps.getPlatform();
		  		arch = ps.getArch();
		  	}
	  	}
	}
	String propStr = "" ;
	for( int i=0;i<sysProps.length;i++){
		if ( i != sysProps.length-1 )
			propStr += "\""+sysProps[i] + "\""+",";
		else
			propStr += "\"" +sysProps[i] +"\"";
	}
	
%>

<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<%@ include file="common.jsp" %>
</head>

<script language="javascript">
function submit(target){
	document.addHostFrm.action = target ;
	var hostId = <%=(hostId!=null)?("\""+hostId+"\""):null%> ;
	if( hostId != null )
		document.addHostFrm.action = target + "&&hostId=" + hostId ;
		
	document.addHostFrm.submit();
}

function addRowInTable(table)
{
    var modes = new Array (<%=propStr%> );
    var len = table.rows.length ;

    var newRow = document.createElement("tr");
    var idx = "" + (len + 1) ;

    var col1 = document.createElement("td");
    col1.setAttribute('class', 'medium');

    var d2 = document.createElement("select");
    d2.setAttribute('name', 'argument.name.' + idx );

    for( var i=0;i<modes.length;i++){
	var opt = document.createElement("option");
	opt.appendChild(document.createTextNode(modes[i]) );
	d2.appendChild(opt)
    }
    col1.appendChild(d2);

    var col2 = document.createElement("td");
    col2.setAttribute('class', 'medium');
    var d3 = document.createElement("input");
    d3.setAttribute('type', 'text');
    d3.setAttribute('name', 'argument.value.' + idx);
    col2.appendChild(d3);

    newRow.appendChild(col1);
    newRow.appendChild(col2);  
    table.getElementsByTagName("tbody")[0].appendChild(newRow);
}
</script>

<body>
<script>
setNextSubtitle("Host Properties Page");
</script>

<form id='addHostFrm' name='addHostFrm' method='post' action='SaveHost1.jsp?action=props&&next=bs&&hostId=<%=hostId %>'>

<!-- This is the page menu -->
<br>
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("HostProps","header",
      "Host Properties",
	    "",
      "Transfer Modes",
	    "javascript:submit('SaveHost1.jsp?action=props&&next=tm')",
      "Access Modes",
	    "javascript:submit('SaveHost1.jsp?action=props&&next=am')",
      "Basic Settings",
	    "javascript:submit('SaveHost1.jsp?action=props&&next=bs')"
    );
  </script>
</div>

<!-- Actual Body starts here -->
<br/>
<center>

<table id="argumentTable" class="dataTable" 
	style="width: 500px; border-collapse: collapse">
<caption>Host properties for <%=hostId %></caption>
<thead>
    <tr>
	<td class="medium"> Property </td>
	<td class="medium"> Value </td>
    </tr>	
</thead>

<tbody>
<%
    ArgumentType argType = host.getArguments();
    
    if( null != argType ){
	ArgumentType.Argument args[] = argType.getArgumentArray();

	for( int i=0;i<args.length;i++){
		String name  = args[i].getName();
		String value = args[i].getValue();
%>

<tr>
    <td class="medium">
	<select name="<%=("argument.name." + i)%>">
	    <option value="">Choose...</option>
<%
    for( int j=0; j<sysProps.length; j++){
%>	
	    <option<%=(sysProps[j].equals(name))?" selected":""%>>
		<%=sysProps[j]%>
	    </option>
<%
    }
%>
	</select>
    </td>
    <td class="medium">
	<input name="<%=("argument.value." + i)%>" type="text"
		value="<%=value%>">
    </td>
</tr>

<%
		    }
    }
%>
</tbody>
</table>
<br/>
<script language="javascript">
    if (document.getElementById('argumentTable').rows.length < 3)
    {   addRowInTable(document.getElementById('argumentTable'));
    }
</script>
<input type='button' value='Add a Property' class="btn" 
	onclick="javascript:addRowInTable(getElementById('argumentTable'))">
<input type='submit' name='save' value='Save Changes' class="btn">
</center>
</form>
<script language="JavaScript" type="text/javascript">
        reconcileEventHandlers();
</script>
</body>

</html>
