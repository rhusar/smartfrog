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
    
    String hostId = request.getParameter("hostId");
    
    String pageAction = request.getParameter("action");
    
    if( pageAction != null && pageAction.equals("bs") ){
	    // save basic settings
	    String os = request.getParameter("os");
	    String plaf = request.getParameter("platform");
	    String arch = request.getParameter("arch");
	    
	    if( null != hostId && ! hostId.trim().equals("")){
		HostType host = manager.getHost(hostId.trim());

		if ( null == host ){
		    host = manager.newHost(hostId);
		    PlatformSelectorType pst = host.addNewPlatformSelector();
		    pst.setOs(os);
		    pst.setPlatform(plaf);
		    pst.setArch(arch);
		}else{
		    PlatformSelectorType pst = host.getPlatformSelector();
		    if( null == pst) {
			pst = host.addNewPlatformSelector();
		    }
		    pst.setOs(os);
		    pst.setPlatform(plaf);
		    pst.setArch(arch);
		}
		
		manager.setHost(host);
	    }
     }else if( pageAction != null && pageAction.equals("am") ){	
	if( null != hostId && ! hostId.trim().equals("")){
	    HostType host = manager.getHost(hostId.trim());
     
	    // save access modes for the host
	    HostType.AccessModes modes =
	    	HostType.AccessModes.Factory.newInstance();
	    // overwrite existing hosts
	    java.util.Enumeration params = request.getParameterNames();
	    
	    String defaultAccessMode =
	    	request.getParameter("defaultAccessMode");
	    
	    while(params.hasMoreElements()){
		String param = (String)params.nextElement();
		String s = "mode.userName." ;
		if(param.startsWith(s) ){
		    // its access Mode 
		    String idx = param.substring(s.length(),param.length());
		    
		    String type = request.getParameter("mode.type." + idx);
		    String userName = request.getParameter(param);
		    String password = 
		    	request.getParameter("mode.password."+ idx);
			    
		    AccessModeType mode = modes.addNewMode();
		    mode.setType(type);
		    mode.setUser(userName);
		    mode.setPassword(password);
		    //if( defaultAccessMode != null &&
		    		//type.equals(defaultAccessMode) ){
		    // FIXME: Bad patch, just set last one default first time,
		    // there is only one so it should work out. 
		    // issue with javascripting. 
			mode.setIsDefault(true);
		    //}
		}
	    }		 
	    // TODO:check if someone deleted host by now.
	    host.setAccessModes(modes);
	    manager.setHost(host);
	}
     }else if( pageAction != null && pageAction.equals("tm") ){
	if( null != hostId && ! hostId.trim().equals("")){
	    HostType host = manager.getHost(hostId.trim());
     
	    HostType.TransferModes transferModes =
	    	HostType.TransferModes.Factory.newInstance();
	    // save access modes for the host
	    // overwrite existing hosts
	    java.util.Enumeration params = request.getParameterNames();
	    
	    String defaultAccessMode =
	    	request.getParameter("defaultTransferMode");
	    
	    while(params.hasMoreElements()){
		String param = (String)params.nextElement();
		String s = "mode.userName." ;
	       if(param.startsWith(s) ){
		    // its access Mode 
		    String idx = param.substring(s.length(),param.length());
		    
		    String type = request.getParameter("mode.type." + idx);
		    String userName = request.getParameter(param);
		    String password = 
			    request.getParameter("mode.password." + idx);
			    
		    DataTransferModeType mode = transferModes.addNewMode();
		    mode.setType(type);
		    mode.setUser(userName);
		    mode.setPassword(password);
		    //if( defaultAccessMode != null &&
			    //type.equals(defaultAccessMode) ){
		    // FIXEME : same as access mods. 
			mode.setIsDefault(true);
		    //}
		}
	    }		 
	    host.setTransferModes(transferModes);
	    manager.setHost(host);
	}
    }else if( pageAction != null && pageAction.equals("props") ){
	if( null != hostId && ! hostId.trim().equals("")){
	    HostType host = manager.getHost(hostId.trim());
     
	    java.util.Enumeration params = request.getParameterNames();
	    ArgumentType args = ArgumentType.Factory.newInstance();
	    while(params.hasMoreElements()){
		String param = (String)params.nextElement();
		String s = "argument.name." ;
		if(param.startsWith(s) ){
		    // its access Mode 
		    String idx = param.substring(s.length(),param.length());
		    
		    String name = request.getParameter(param);
		    String value =
		    	request.getParameter("argument.value." + idx);

		    ArgumentType.Argument arg = args.addNewArgument();
		    arg.setName(name);
		    arg.setValue(value);
		}
	    }		 
	    host.setArguments(args);
	    manager.setHost(host);
	}
    }
     
    String next = request.getParameter("next");
    if( null == next ) 
	    next = "am" ; 
	    
    javax.servlet.RequestDispatcher dispatcher = null ; 
    if( next.equals("am")){
	// forward to the next page
	dispatcher = request.getRequestDispatcher("HostAM.jsp?hostId=" +
			hostId);
    }else if ( next.equals("tm")){
	// forward to the next page
	dispatcher = request.getRequestDispatcher("HostTM.jsp?hostId=" +
			hostId);
    }else if ( next.equals("props")){
	// forward to the next page
	dispatcher = request.getRequestDispatcher("HostProps.jsp?hostId=" +
			hostId);
    }else if ( next.equals("bs")){
	// forward to the next page
	dispatcher = request.getRequestDispatcher("HostBS.jsp?hostId=" +
			hostId);
    }
    dispatcher.forward(request, response);
%>
