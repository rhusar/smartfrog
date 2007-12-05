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
<%@	page import="java.util.*"%>
<%@ include file="InitBeans.jsp" %>

<%
    String errMsg = null; 
    ModulesManager manager = factory.getModulesManager();

    if (null == manager) {
        errMsg = "Error in opening Modules database";
        throw new Exception(errMsg);
    }
 

    String moduleId = request.getParameter("moduleId");
    String pageAction = request.getParameter("pageAction");
    
    javax.servlet.RequestDispatcher dispatcher = null ; 
    
    if ( pageAction.equals("delMod")){
	// find and delete modules.. 
	String []modules = request.getParameterValues("selectedModule");
	if( null != modules ){
	    for( int i=0;i<modules.length;i++){
		manager.removeModule(modules[i]);
	    }
	}
    }

    if( null == moduleId ) {
	// nothing to save
    }else{
	if( pageAction != null ){
	    if( pageAction.equals("addMod")) {
		// save module basics
		String vendor = request.getParameter("vendor");
		String description = request.getParameter("description");
		
		ModuleType mod = manager.getModule(moduleId);
		
		if( null == mod ){
		    mod = manager.newModule(moduleId);
		    mod.setVendor(vendor);
		    mod.setDescription(description);
		    
		    manager.setModule(mod);
		}else{
		    session.setAttribute("message",
			"Module-id \"" + moduleId + "\" already exists");
		}
	    }else if( pageAction.equals("setMod")) {
		// save module basics
		String vendor = request.getParameter("vendor");
		String description = request.getParameter("description");
		
		ModuleType mod = manager.getModule(moduleId);
		if( null != mod ){
		    // module already existed
		    mod.setVendor(vendor);
		    mod.setDescription(description);
		    
		    manager.setModule(mod);
		}else{
		    session.setAttribute("message",
		    	"Module-id \"" + moduleId + "\" not found");
		}
	    }else if ( pageAction.equals("addModVer")){
		ModuleType mod = manager.getModule(moduleId);
		if( mod != null ){
		    String versionNum = request.getParameter("newVersion");
		    if( null != versionNum ) {
			VersionType version = null;
			VersionType []versions = mod.getVersionArray();
			for( int j=0;j<versions.length;j++){
			    if( versionNum.equals(versions[j].getNumber() )){
				version = versions[j];
				break;
			    }
			}
			if( null == version ){
			    VersionType ver = mod.addNewVersion();
			    ver.setNumber(versionNum);
			    manager.setModule(mod);
			}else{
			    session.setAttribute("message", "Version \"" +
			    	versionNum + "\" already exists");
			}
		    }
		}else{
		    session.setAttribute("message",
		    	"Module-id \"" + moduleId + "\"not found");
		}
	    }else if( pageAction.equals("delModVer") ){
		ModuleType mod = manager.getModule(moduleId);
		if( mod != null ){
		    String []selectedVersions =
		    	request.getParameterValues("selectedVersion");
		    if( null != selectedVersions ) {
			for( int i=0;i<selectedVersions.length;i++){
			    VersionType []versions = mod.getVersionArray();
			    for( int j=0;j<versions.length;j++){
				if( selectedVersions[i].equals(versions[j].getNumber()) ){
				    mod.removeVersion(j);
				    break;
				}
			    }
			}
			manager.setModule(mod);
		    }
		}
	    }else if( pageAction.equals("addDistro" ) ){
		ModuleType mod = manager.getModule(moduleId);
		if( mod != null ){
		    String verNum = request.getParameter("version");
		    VersionType version = null;
		    if( null != verNum ){
			VersionType []versions = mod.getVersionArray();
			for( int j=0;j<versions.length;j++){
			    if( verNum.equals(versions[j].getNumber() )){
				version = versions[j];
				break;
			    }
			}
			if( null != version ){
			    // add distro 
			    String disId = request.getParameter("id");
			    String os = request.getParameter("os");
			    String platform = request.getParameter("platform");
			    String arch = request.getParameter("arch");
			    
			    if( null != disId ){
				DistributionType distro = null ;
				DistributionType []distros =
					version.getDistributionArray();
				for( int j=0;j<distros.length;j++){	
				    if( distros[j].getId().equals(disId)){
					distro = distros[j];
					break;
				    }
				}
				if( null == distro ){
				    distro = version.addNewDistribution();
				    distro.setId(disId);
				    PlatformSelectorType pst =
					    distro.addNewPlatformSelector();
				    pst.setOs(os);
				    pst.setPlatform(platform);
				    pst.setArch(arch);
				    manager.setModule(mod);
				}else{
				    session.setAttribute("message",
				    	"Distribution-id \"" + disId + 
						"\" already exists");
				}
			    }else{
				session.setAttribute("message",
					"Null distribution-id");
			    }
			}
		    }
		}
	    }else if( pageAction.equals("delDistro" ) ){
		ModuleType mod = manager.getModule(moduleId);
		if( mod != null ){
		    String verNum = request.getParameter("version");
		    VersionType version = null;
		    if( null != verNum ){
			VersionType []versions = mod.getVersionArray();
			for( int j=0;j<versions.length;j++){
			    if( verNum.equals(versions[j].getNumber() )){
				version = versions[j];
				break;
			    }
			}
			String []selectedDistros =
				request.getParameterValues("selectedDistro");
			if( null != version ){
			    for( int i=0;i<selectedDistros.length;i++){
				DistributionType []distros =
					version.getDistributionArray();
				for( int j=0;j<distros.length;j++){	
				    if( selectedDistros[i].equals(distros[j].getId())){
					version.removeDistribution(j);
					break;
				    }
				}
			    }
			}
			manager.setModule(mod);
		    }
		}
	    }else if( pageAction.equals("addAction")){
		ModuleType mod = manager.getModule(moduleId);
		DistributionType distro = null ;
		if( mod != null ){
		    String verNum = request.getParameter("version");
		    VersionType version = null;
		    if( null != verNum ){
			VersionType []versions = mod.getVersionArray();
			for( int j=0;j<versions.length;j++){
			    if( verNum.equals(versions[j].getNumber() )){
				version = versions[j];
				break;
			    }
			}
			String distroId = request.getParameter("distroId");
			if( null != distroId && null != version){
			    DistributionType []distros =
			    	version.getDistributionArray();
			    for( int j=0;j<distros.length;j++){	
				if( distros[j].getId().equals(distroId)){
				    distro = distros[j];
				    break;
				}
			    }
			}
			String actionName = request.getParameter("actionTitle");
			String engine = request.getParameter("engine");
			String configFile = request.getParameter("configFile");
			
			if( null != actionName && null != configFile ){
			    // TODO : check if action already exists
			    ActionType newAction = distro.addNewAction();
			    newAction.setEngine(engine);
			    newAction.setName(actionName);
			    newAction.setConfiguration(configFile);
			    
			    manager.setModule(mod);
			}else{
			    session.setAttribute("message", 
			    	"Invalid values: Action name:" + actionName +
					", URL: " + configFile);
			}
		    }
		}
	    }else if (pageAction.equals("saveActionArgs")){
		ModuleType mod = manager.getModule(moduleId);
		DistributionType distro = null ;
		if( mod != null ){
		    String verNum = request.getParameter("version");
		    VersionType version = null;
		    if( null != verNum ){
			VersionType []versions = mod.getVersionArray();
			for( int j=0;j<versions.length;j++){
			    if( verNum.equals(versions[j].getNumber() )){
				version = versions[j];
				break;
			    }
			}
			String distroId = request.getParameter("distroId");
			if( null != distroId && null != version){
			    DistributionType []distros =
			    	version.getDistributionArray();
			    for( int j=0;j<distros.length;j++){	
				if( distros[j].getId().equals(distroId)){
				    distro = distros[j];
				    break;
				}
			    }
			}
			String title  = request.getParameter("actionTitle");
			ActionType action = null ;
			// get to action now 
			if( null != distro && null != title){
			    ActionType []actions = distro.getActionArray();
			    for( int j=0;j<actions.length;j++){	
				if( title.equals(actions[j].getConfiguration())){
				    action = actions[j] ;
				    break;
				}
			    }
			    
			    if( null != action ){
				Enumeration e = request.getParameterNames();
				while(e.hasMoreElements()){
				    String arg = (String)e.nextElement();
				    String prefix = "action.argument.";
				    if( arg.startsWith(prefix) ){
					String value = request.getParameter(arg);
					String attrName =
					    arg.substring(prefix.length(), 
					    arg.length());
					ActionType.Argument[] actionArgs = 
					    action.getArgumentArray();
					boolean set = false ; 
					for( int i = 0;i< actionArgs.length;i++){
					    if( actionArgs[i].getName().equals(attrName) ){
						actionArgs[i].setValue(value);
						set = true;
						break;
					    }
					}
					if( !set ) {
					    // attribute didnt exist - add a
					    // new one.
					    ActionType.Argument newArg =
					    	action.addNewArgument();
					    newArg.setName(attrName);
					    newArg.setValue(value);
					}
				    }
				}
				manager.setModule(mod);
			    }
			}else{
			    session.setAttribute("message",
			    	"Invalid values: distroId, title:"
				+ distroId + ", " + title);
			}
		    }
		}
	    }
	    else if( pageAction.equals("delAction")){
		ModuleType mod = manager.getModule(moduleId);
		DistributionType distro = null ;
		if( mod != null ){
		    String verNum = request.getParameter("version");
		    VersionType version = null;
		    if( null != verNum ){
			VersionType []versions = mod.getVersionArray();
			for( int j=0;j<versions.length;j++){
			    if( verNum.equals(versions[j].getNumber() )){
				version = versions[j];
				break;
			    }
			}
			String distroId = request.getParameter("distroId");
			if( null != distroId && null != version){
			    DistributionType []distros =
				    version.getDistributionArray();
			    for( int j=0;j<distros.length;j++){	
				if( distros[j].getId().equals(distroId)){
				    distro = distros[j];
				    break;
				}
			    }
			}
			String []selectedActions =
				request.getParameterValues("selectedAction");
			if( null != selectedActions ){
			    for( int i=0;i<selectedActions.length;i++){
				ActionType []actions = distro.getActionArray();
				for( int j=0;j<actions.length;j++){	
				    if( selectedActions[i].equals(actions[j].getConfiguration())){
					distro.removeAction(j);
					break;
				    }
				}
			    }
			    manager.setModule(mod);
			}
		    }
		}
	    }
	}

    }
    if( pageAction.equals("addMod") || pageAction.equals("delMod")) {
	    // forward to the next page
	    dispatcher = request.getRequestDispatcher("module_list.jsp");
	   // response.sendRedirect("module_list.jsp");
	} else if( pageAction.equals("addModVer") || pageAction.equals("delModVer")) {
		// forward to the next page
	   System.out.println("REDIRECTING"); 
	    dispatcher =
	        request.getRequestDispatcher("module_version_list.jsp?moduleId="
			+ moduleId);
	   // response.sendRedirect("module_version_list.jsp?moduleId="+ moduleId);
	}else if( pageAction.equals("addDistro") || pageAction.equals("delDistro")) {
		String version = request.getParameter("version");
	    dispatcher = 
	    	request.getRequestDispatcher("module_version_view.jsp?moduleId="
	    	+ moduleId + "&&version=" + version);
       // response.sendRedirect("module_version_view.jsp?moduleId="+ moduleId + "&version=" + request.getParameter("version"));
	}else if(pageAction.equals("addAction") || pageAction.equals("delAction")) {
		String version = request.getParameter("version");
	    String distroId = request.getParameter("distroId");
	    dispatcher = 
		request.getRequestDispatcher("module_distro_action.jsp?moduleId="
			+ moduleId + "&&version="+version
			+ "&&distroId="+distroId);
     //   response.sendRedirect("module_distro_action.jsp?moduleId="+ moduleId + "&version=" + request.getParameter("version") + "&distroId="+ request.getParameter("distroId"));
	} else {
		dispatcher = request.getRequestDispatcher("module_list.jsp");
      //  response.sendRedirect("module_list.jsp");
    }
    if( null == dispatcher ){
	    dispatcher = request.getRequestDispatcher("ListModules.jsp");
    }
 dispatcher.forward(request, response);
%>
