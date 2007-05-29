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
<%-- $Id: sfSettings.jsp 81 2006-05-30 06:09:38Z uppada $ --%>
<%@ page language="java" %>

<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>
<%@	page import="org.smartfrog.avalanche.settings.sfConfig.*"%>

<%@ include file="InitBeans.jsp" %>
  	
<%
  	String errMsg = null; 

  	SettingsManager settingsMgr = factory.getSettingsManager();
  	if( null == settingsMgr ){
  		errMsg = "Error connecting to settings database" ;
  		throw new Exception ( "Error connecting to settings database" );
  	}
  	SfConfigsType configs = settingsMgr.getSFConfigs();  

	String sfHome = configs.getSfHomeOnServer();
	String bootDir = configs.getSfBootDir();
	String sfReleaseFile = configs.getSfReleaseFile();
	String sfReleaseName = configs.getSfReleaseName();
	String sfTemplateFile = configs.getSfTemplateFile();
  	
%>

<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<%@ include file="common.jsp" %>
</head>

<body>
<script>
setNextSubtitle("Deployment Engines Page");
</script>

<form method="post" action="SaveSfSettings.jsp">
<br/>
<div align="center">
<center>
<!-- This is the page menu -->
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("sfSettings","header"
    //  "Basic Settings",
    //  	"#"
    );
  </script>
</div>

<%@ include file="Message.jsp" %>

<!-- Actual Body starts here -->
<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="sfSettingsTable">
  <thead>
    <tr class="captionRow">
      <th style="width:25%;">Property </th>
      <th>Value</th>
    </tr>
  </thead>
  <tbody>
    <tr>
	<td class="medium" align="right">SmartFrog Home:</td>
	<td class="editableFieldCell">
	  <input type="text" name="SFHome" class="medium" value="<%=(sfHome==null)?"":sfHome%>"></input>
	</td>
    </tr>
    <tr>
	<td class="medium" align="right">Configuration directory:</th>
	<td class="editableFieldCell"> 
		<input type="text" name="SFBootDir" class="medium" value="<%=(bootDir==null)?"":bootDir%>"> 
	</td>
    </tr>
    <tr>
	<td class="medium" align="right">SF Release file:</th>
	<td class="editableFieldCell"> 
		    <input type="text" name="SFReleaseFile" class="medium" value="<%=(sfReleaseFile==null)?"":sfReleaseFile%>"> 
	</td>
    </tr>
    <tr>
	<td class="medium" align="right">SF Release name:</th>
	<td class="editableFieldCell"> <input type="text" name="SFReleaseName" class="medium" value="<%=(sfReleaseName==null)?"":sfReleaseName%>"> 
	</td>
    </tr>
    <tr>
	<td class="medium" align="right">SF Template file:</th>
	<td class="editableFieldCell"> 
	<input type="text" name="SFTemplateFile" class="medium" value="<%=(sfTemplateFile==null)?"":sfTemplateFile%>"> 
	</td>
    </tr>
  </tbody>
</table>

<br/>
<div align="center" style="width: 95%;">
<div class="buttonSet">
  <div class="bWrapperUp"><div><div>
    <input align="center" type="submit" class="hpButton" name="Save" value="Save Changes">
  </div></div></div>
</div>
<div class="clearFloats"></div>
</div>

</center>
</div>
</form>
<script language="JavaScript" type="text/javascript">
	reconcileEventHandlers();
</script>
</body>

</html>
