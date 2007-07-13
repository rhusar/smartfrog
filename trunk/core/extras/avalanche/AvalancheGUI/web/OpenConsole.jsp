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
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*"%>

<%@ include file="InitBeans.jsp" %>

<%
  	String errMsg = null;
  	HostManager manager = factory.getHostManager();

  	if( null == manager ){
  		errMsg = "Error connecting to hosts database" ;
  		throw new Exception ( "Error connecting to hosts database" );
  	}

	/*String targetHost = request.getParameter("hostId");

  	if ( null != targetHost) {
	System.out.println("HOSTNMAE===" + targetHost);
	}*/
    SFAdapter adapter = new SFAdapter(factory, scheduler);
	String []selectedHosts = request.getParameterValues("selectedHost");
  	if( null != selectedHosts ){
	  	for( int i=0;i<selectedHosts.length;i++){
		//	System.out.println(selectedHosts[i]);
			adapter.startMngConsole(selectedHosts[i]);
	  	}
  	}
  	// forward to the host listing
  	javax.servlet.RequestDispatcher dispatcher =
		request.getRequestDispatcher("ListHostsActive.jsp");
	dispatcher.forward(request, response);
%>
