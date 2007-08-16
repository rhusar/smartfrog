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
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@ include file="header.inc.jsp" %>

<%
  	String errMsg = null; 

  	ModulesManager modulesMgr = factory.getModulesManager();
  	
  	if( null == modulesMgr ){
  		errMsg = "Error connecting to Modules database" ;
  		throw new Exception ( "Error connecting to Modules database" );
  	}
  	
  	String moduleId = request.getParameter("moduleId");
  	String vendor = ""; 
  	String description = "" ;
  	
  	if( null != moduleId ){
	  	ModuleType module = modulesMgr.getModule(moduleId);
	  	if( null != module ){
		  	vendor = module.getVendor();
		  	description = module.getDescription();
	  	}
	}
%>

<script language="JavaScript" type="text/javascript">
    <!--

function submit(target){
	var hid = document.getElementById("moduleId");
	if(null != hid){
		if( hid.value == null || hid.value == ""){
			alert("Please enter valid Host Id");
		}
	}else{
		document.addModuleFrm.action = target ;
		var mId = <%=(moduleId!=null)?("\""+moduleId+"\""):null%> ;
		if( mId != null )
			document.addHostFrm.action = target + "&hostId=" + mId ;
			
		document.addModuleFrm.submit();
	}
}

setNextSubtitle("Add Module Basic Settings Page");
    -->
</script>

<br>
<div align="center">
<center>
<!-- This is the page menu -->

<div align="center" style="width: 95%;">
  <script language="JavaScript" type="text/javascript">
    oneVoiceWritePageMenu("AddModuleBS","header",
      "Configure Versions",
  	"javascript:setLocation('module_version_list.jsp?moduleId=<%=moduleId %>')"
    );
  </script>
</div>

<%@ include file="Message.jsp" %>

<!-- Actual Body starts here -->
<form name='addModuleFrm' method='post' action='module_save.jsp?pageAction=setMod&moduleId=<%=moduleId%>'>
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="addModuleTable">
  <caption>Module Basic Settings</caption>
  <tbody>
    <tr> 
      <td style="width: 15%;" class="medium" align="right"><b>Module Id:</b></td> 
      <td class="data"> 
<%
    if( null == moduleId) {
%>	
        <input type="text" name="moduleId" size="30" id='moduleId'></input>
<%
    }else{
%>
      <%=moduleId%>
<%
    }
%>

      </td>
    </tr>  			
    <tr> 
      <td style="width: 15%;" class="medium" align="right"><b>Vendor:</b></td> 
      <td class="data">  
	<input type="text" name="vendor" size="30" value="<%=vendor %>"></input>
      </td>
    </tr>  			
    <tr>
      <td style="width: 15%;" class="medium" align="right" valign="top"><b>Description:</b></td>
      <td class="data">  
	<textarea rows="5" name="description" cols="30"><%=description %></textarea>
      </td>
    </tr>  	
  </tbody>
</table>
<br/>
<div style="width:95%;">
<div class="buttonSet">
  <div class="bWrapperUp" style="margin-top:10px;"><div><div>
    <input type='submit' name='save' value='Save Changes' class="hpButton"></input>
  </div></div></div>
</div>
</div>


</form>
</center>
</div>

<%@ include file="footer.inc.jsp"%>
