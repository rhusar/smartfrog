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
<%@	page import="org.smartfrog.avalanche.server.engines.sf.*"%>
<%@	page import="org.smartfrog.avalanche.server.engines.*"%>

<%@ include file="InitBeans.jspjsp" %>

<%
  	HostManager manager = factory.getHostManager();
  	if( null == manager ){
  		throw new Exception ( "Error connecting to hosts database" );
  	}
  	String pageAction = request.getParameter("pageAction");
  	String []hostsToIgnite = null;
  	if( pageAction != null && pageAction.equals("ignite") ){
	  	hostsToIgnite = request.getParameterValues("selectedHost");
%>
<html>
<head> Hosts to be ignited </head>
<body><%=hostsToIgnite[0]%></body>
</html>
<%
	  	// call ignition adapter with factory
	  	if( null != hostsToIgnite ){
		  	BootStrap bs = new BootStrap(factory, setup);
		  	try{
			  	bs.ignite(hostsToIgnite);
			}catch(HostIgnitionException e){
			}
	  	}
	}else if( pageAction != null && pageAction.equals("unIgnite") ){
	  	String [] hosts = request.getParameterValues("selectedHost");
	  	if( null != hosts ){
		  	for( int i=0;i<hosts.length;i++){
			  	try{
				  	SFAdapter.stopDaemon(hosts[i]) ;
			  	}catch(Throwable t){
				  		t.printStackTrace();
			  	}
		  	}
	  	}
	}

	// redirect 
	javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("ListHostsActive.jsp");
	dispatcher.forward(request, response);
	
%> 
