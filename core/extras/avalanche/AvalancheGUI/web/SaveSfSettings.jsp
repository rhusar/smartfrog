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

<%@	page import="org.smartfrog.avalanche.server.*"%>
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
  	
	String sfHome = request.getParameter("SFHome");
	String bootDir = request.getParameter("SFBootDir");
	String sfReleaseFile = request.getParameter("SFReleaseFile");
	String sfReleaseName = request.getParameter("SFReleaseName");
	String sfTemplateFile = request.getParameter("SFTemplateFile");
		
	configs.setSfBootDir(bootDir);
	configs.setSfHomeOnServer(sfHome);
	configs.setSfReleaseFile(sfReleaseFile);
	configs.setSfReleaseName(sfReleaseName);
	configs.setSfTemplateFile(sfTemplateFile);
		
  	settingsMgr.setSfConfigs(configs);

	javax.servlet.RequestDispatcher dispatcher = null ; 
  	
  	if( null == dispatcher ){
		dispatcher = request.getRequestDispatcher("sfSettings.jsp");
  	}
	dispatcher.forward(request, response);
%>
