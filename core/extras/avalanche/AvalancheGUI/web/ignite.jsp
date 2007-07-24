<!-- /**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

For more information: www.smartfrog.org
*/
-->
<%@ page language="java" %>
<%@ include file="header.inc.jsp" %>
<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*" %>
<%@ page import="org.smartfrog.avalanche.server.engines.*" %>



<%
    HostManager manager = factory.getHostManager();
    if (null == manager) {
        throw new Exception("Error connecting to hosts database");
    } else {
        String pageAction = request.getParameter("pageAction");
        String[] hosts = request.getParameterValues("selectedHost");

        if (pageAction != null)
            if (!hosts.equals(null) && (hosts.length != 0)) {
                    if (pageAction.equals("ignite")) {
%>
<h1>Host Ignition in process...</h1>
<%
                        BootStrap bs = new BootStrap(factory, setup);
                        try {
                            bs.ignite(hosts);
                        } catch (HostIgnitionException e) {

                        }
                    } else if (pageAction.equals("stop")) {
%>
<h1>Stopping SmartFrog in process...</h1>
<%
                        for (int i = 0; i < hosts.length; i++) {
                            try {
                                SFAdapter.stopDaemon(hosts[i]);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    }

            }
        }

    // redirect
    javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("list_hosts_active.jsp");
    dispatcher.forward(request, response);

%>

<%@ include file="footer.inc.jsp" %>
