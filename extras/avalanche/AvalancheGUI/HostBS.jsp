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
<%-- $Id: HostBS.jsp 81 2006-05-30 06:09:38Z uppada $ --%>
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
  	
  	String hostId = request.getParameter("hostId");
  	String os = null; 
  	String plaf = null ;
  	String arch = null ;
  	
  	if( null != hostId ){
	  	HostType host = manager.getHost(hostId);
	  	if( null != host ){
	  		PlatformSelectorType ps = host.getPlatformSelector();
	  		if( null != ps ){
		  		os 	 = ps.getOs();
		  		plaf = ps.getPlatform();
		  		arch = ps.getArch();
		  	}
	  	}
	}
	
	

%>

<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<%@ include file="common.jsp" %>
</head>
<script language="javascript">

function submit(target){
	var hid = document.getElementById("hostId");
	if(null != hid){
		if( hid.value == null || hid.value == ""){
			alert("Please enter valid Host Id");
		}
	}else{
		document.addHostFrm.action = target ;
		var hostId = <%=(hostId!=null)?("\""+hostId+"\""):null%> ;
		if( hostId != null )
			document.addHostFrm.action = target + "&&hostId=" + hostId ;
			
		document.addHostFrm.submit();
	}
}
</script>

<body>
<script>
setNextSubtitle("Host Basic Settings Page");
</script>

<%	if( null != hostId ){ %>
<form id='addHostFrm' name='addHostFrm' method='post' action='SaveHost1.jsp?action=bs&&next=am&&hostId=<%=hostId %>'>
<%}     else{ %>
<form id='addHostFrm' name='addHostFrm' method='post' action='SaveHost1.jsp?action=bs&&next=am'>
<%      } %>

<!-- This is the page menu -->
<br/>
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("HostBS","header",
      "Host Properties",
  	"javascript:submit('SaveHost1.jsp?action=bs&&next=props')",
      "Transfer Modes",
  	"javascript:submit('SaveHost1.jsp?action=bs&&next=tm')",
      "Access Modes",
  	"javascript:submit('SaveHost1.jsp?action=bs&&next=am')",
      "Basic Settings",
  	""
    );
  </script>
</div>

<!-- Actual Body starts here -->
<br/>
<div align="center">
<table id="hostListTable" class="dataTable" 
    style="width: 400px; border-collapse: collapse;">
    <caption>Basic Settings</caption>
    <tbody>
	<tr> 
	    <td class="medium" align="right">Host:</td> 
	    <td class="medium"> 
	<%
		if( null == hostId) {
	%>	
		    <input type="text" name="hostId" size="30" id='hostId'>
	<%
		}else{
	%>
		    <%=hostId%>
	<%
		}
	%>

	    </td>
	</tr>  			

	<tr> 
		<td class="medium" align="right">Operating System:</td> 
		<td class="medium">  
		<select name="os">
	<%
		String oses[] = defSettings.getOsArray();
		for( int i=0;i<oses.length;i++){
	%>	
		    <option<%=((os!=null)&&os.equals(oses[i]))?" selected":""%>>			<%=oses[i]%>
		    </option>
	<%
		}
	%>
		</select>
		</td>
	</tr>  			
	<tr>
	    <td class="medium" align="right">Platform:</td>
	    <td class="medium">  
		<select name="platform">
		<%
		    String plafs[] = defSettings.getPlatformArray();
		    for( int i=0;i<plafs.length;i++){
		%>	
			<option<%=((plaf!=null)&&plaf.equals(plafs[i]))?" selected":""%>>
			    <%=plafs[i]%>
			</option>
		<%
		    }
		%>
		</select>
		</td>
	</tr>  	
	<tr>		
	    <td class="medium" align="right">Architecture:</td>
	    <td class="medium">  
		<select name="arch">
	<%
		    String archs[] = defSettings.getArchArray();
		    for( int i=0;i<archs.length;i++){
	%>	
			<option<%=((arch!=null)&&arch.equals(archs[i]))?" selected":""%>>
			    <%=archs[i]%>
			</option>
	<%
		    }
	%>
		</select>
		</td>
	</tr>  	
    </tbody>
</table>
<br/>
<input type='submit' name='save' value='Save Changes' class="btn">
</div>
</form>
<script language="JavaScript" type="text/javascript">
        reconcileEventHandlers();
</script>
</body>

</html>
