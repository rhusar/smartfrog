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
<%@	page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType"%>
<%@	page import="org.smartfrog.avalanche.core.activeHostProfile.ModuleStateType"%>
<%@	page import="org.smartfrog.avalanche.core.activeHostProfile.ModuleStateType"%>
<%@ include file="InitBeans.jsp" %>

<%
    String errMsg = null; 
	String []hosts = request.getParameterValues("selectedModule");
	String[] hostlist = new String[hosts.length];
   ActiveProfileManager apm = factory.getActiveProfileManager();
     String pageAction = request.getParameter("pageAction");
	  String pageAction1 = request.getParameter("pageAction1");
	  
	  String []targetHosts = new String[0];
		
    javax.servlet.RequestDispatcher dispatcher = null ; 
	
	if ( pageAction.equals("dellog")){
		
		String[] modulelist = new String[hosts.length];
		String[] datelist = new String[hosts.length];
		for(int i=0;i<hosts.length;i++){
			String[] temp = new String[3];
			temp = hosts[i].split(",");
			hostlist[i] = temp[0];
			modulelist[i] = temp[1];
			
			datelist[i] = temp[2];
		}
		
	// find and delete modules.. 
	for( int i=0;i<hostlist.length;i++){
			
		ActiveProfileType profile = apm.getProfile(hostlist[i]);
                ModuleStateType[] states = profile.getModuleStateArray();
                ModuleStateType state = null;
				ArrayList<ModuleStateType> array = new ArrayList<ModuleStateType>();
				for (int k = 0; k < states.length ; k++){
					array.add(states[k]);
				}
				Iterator<ModuleStateType> itr = array.iterator();
              for (int k = 0; k < states.length ; k++){
					state = (ModuleStateType)itr.next();
                    String mId = state.getId();
                    String ver = state.getVersion();
                    String ins = state.getInstanceName();
					String t = state.getLastUpdated();
					
                    if (modulelist[i].equals(mId) && datelist[i].equals(t)) {
                        itr.remove();
						break;
                    }
					

                }
				ModuleStateType[] statesArray = new ModuleStateType[array.size()];
				array.toArray(statesArray);
				profile.setModuleStateArray(statesArray);
				apm.setProfile(profile);
    }
	
	}
	String urlString = "log_view.jsp?pageAction=";
	if(  pageAction.equals("dellog")) {
	    // forward to the next page
		if( null == pageAction1 ){
			urlString+="viewAll";
  	  	targetHosts = apm.listProfiles() ;
  		}else if( pageAction1.equals("viewAll")){
		  	targetHosts = apm.listProfiles() ;
			urlString+="viewAll";
  		}else if ( pageAction1.equals("viewSelected")){
  	  	targetHosts =(String[]) request.getParameterValues("targetHosts");
		urlString+="viewSelected&hostId="+targetHosts[0] ;
		if( null == targetHosts){
  	  	  		targetHosts = new String[1];
				//targetHosts[0] = "avala-prj-2.hpl.hp.com";
  	  	}
  	}
	
	dispatcher = request.getRequestDispatcher(urlString);
	//request.setParameter("hostId",targetHosts);
	   // response.sendRedirect("module_list.jsp");
	} 
    if( null == dispatcher ){
	    dispatcher = request.getRequestDispatcher("log_view.jsp");
    }
 dispatcher.forward(request, response);
%>
