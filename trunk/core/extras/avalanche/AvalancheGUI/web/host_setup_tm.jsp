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
<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.core.host.*"%>

<%
    String errMsg = null; 
    HostManager manager = factory.getHostManager();
    
    if( null == manager ){
	errMsg = "Error connecting to hosts database" ;
	throw new Exception ( "Error connecting to hosts database" );
    }
    
    SettingsManager settingsMgr = factory.getSettingsManager();
    SettingsType defSettings = settingsMgr.getDefaultSettings();  
    
    SettingsType.DataTransferMode sysTransferModes[] =
		 defSettings.getDataTransferModeArray();
    
    HostType host = null;
    String hostId = request.getParameter("hostId");

    if (hostId != null) {
        hostId = hostId.trim().toLowerCase();
        if (!hostId.equals("")) {
            host = manager.getHost(hostId);
        }
    }

    if (host != null) {
    String modeStr = "" ;
    for( int i=0; i < sysTransferModes.length; i++){
	if ( i != sysTransferModes.length-1 )
	    modeStr += "\""+sysTransferModes[i].getName() + "\""+",";
	else
	    modeStr += "\"" +sysTransferModes[i].getName() +"\"";
    }

    String site = "host_save.jsp?action=tm&next=";
%>

<script language="JavaScript" type="text/javascript">
 <!--
function submit(target){
    document.addHostFrm.action = "<%= site %>" + target + "&hostId=<%= host.getId() %>";
    document.addHostFrm.submit();
}

function addRowInTable(table)
{
    var modes = new Array (<%=modeStr%>);
    var len = table.rows.length ;

    var newRow = document.createElement("tr");
    var idx = "" + (len + 1) ;

    var col1 = document.createElement("td");
    col1.setAttribute('class', 'medium');
    col1.setAttribute('align', 'center');

    var d2 = document.createElement("select");
    d2.setAttribute('name', 'mode.type.' + idx );

    for( var i=0; i < modes.length; i++){
	var opt = document.createElement("option");
	opt.appendChild(document.createTextNode(modes[i]) );
	d2.appendChild(opt)
    }
    col1.appendChild(d2);

    var col2 = document.createElement("td");
    col2.setAttribute('class', 'medium');
    var d3 = document.createElement("input");
    d3.setAttribute('type', 'text');
    d3.setAttribute('name', 'mode.userName.' + idx);
    col2.appendChild(d3);

    var col3 = document.createElement("td");
    col3.setAttribute('class', 'medium');
    var d4 = document.createElement("input");
    d4.setAttribute('type', 'password');
    d4.setAttribute('name', 'mode.password.' + idx);
    col3.appendChild(d4);

    var col4 = document.createElement("td");
    col4.setAttribute('class', 'medium');
    col4.setAttribute('align', 'center');
    var d5 = document.createElement("input");
    d5.setAttribute('type', 'radio');
    d5.setAttribute('name', 'defaultAccessMode');
    col4.appendChild(d5);

    newRow.appendChild(col1);
    newRow.appendChild(col2);  
    newRow.appendChild(col3);  
    newRow.appendChild(col4);  
    table.getElementsByTagName("tbody")[0].appendChild(newRow);
}

setNextSubtitle("Host Transfer Modes Page");
    -->
</script>

<form id="addHostFrm" name="addHostFrm" method="post" action="<%= site %>env&hostId=<%= host.getId() %>">

<!-- This is the page menu -->
<br>

<%@ include file="host_setup_menu.inc.jsp" %>

<!-- Actual Body starts here -->
<br/>
<center>

<table id="transferModeTable" class="dataTable" 
	style="width: 500px; border-collapse: collapse">
    <caption>Transfer modes for host <%=hostId %></caption>
<tbody>
    <tr>
	<td class="medium"> Transfer Mode </td>
	<td class="medium"> User Name </td>
	<td class="medium"> Password </td>
	<td class="medium"> Default </td>
    </tr>	
<%
    HostType.TransferModes transferModes = host.getTransferModes();
		
    if (null != transferModes){
	DataTransferModeType modes[] = transferModes.getModeArray();
	for( int i=0;i<modes.length;i++){
	    String modeName = modes[i].getType();
	    String userName = modes[i].getUser();
	    String password = modes[i].getPassword();
%>

    <tr>
	<td class="medium" align="center">
	    <select name="<%=("mode.type." + i)%>">
<%
	    for( int j=0;j<sysTransferModes.length;j++){
%>	
		<option<%=(sysTransferModes[j].getName().equals(modeName)) ?
		    " selected" : ""%>><%=sysTransferModes[j].getName()%>
		</option>
<%
	    }
%>
	    </select>
	</td>
	<td class="medium">
	    <input name="<%=("mode.userName." + i)%>" type="text" value="<%=userName%>">
	</td>
	<td class="medium">
	    <input name="<%=("mode.password." + i)%>" type="password" value="<%=password%>">
	</td>
	<td class="medium" align="center">
<%
	    if( modes[i].getIsDefault() ){
%>		
	    <p><input type="radio" value="<%=modeName%>" name="defaultTransferMode" checked></p>
<%
	    } else {
%>
	    <input type="radio" value="<%=modeName%>" name="defaultTransferMode">
<%
	    }
%>
	</td>
    </tr>

<%
        }
    }
%>
</tbody>
</table>

<br/>
<input type='button' value='Add a Transfer Mode' class="btn" 
	onclick="javascript:addRowInTable(getElementById('transferModeTable'))">
<input type='submit' name='save' value='Save Changes' class="btn" onClick="submit('env')">
</center>
</form>
<%
    } else {
        response.sendRedirect("host_setup_bs.jsp");
    }
%>

<%@ include file="footer.inc.jsp"%>
