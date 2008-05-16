<%-- /**
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
*/ --%>

<%@ page contentType="text/xml" language="java" %>
<%@ include file="InitBeans.jsp" %>
<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.VmStateType"%>
<%@ page import="org.smartfrog.avalanche.server.ServerSetup"%>
<%@ page import="java.util.HashMap" %>

<%
    String strAction = request.getParameter("action");
    if (strAction != null) {
        // Be able to query ActiveProfile
        ActiveProfileUpdater updater = new ActiveProfileUpdater();
        String strHost = request.getParameter("host");
        String[] strVMPathes = request.getParameterValues("selectedVM");

        try {
            // get the active profile of this machine
            ActiveProfileType type = updater.getActiveProfile(strHost);
            if (type != null) {
                if (strAction.equals("save")) {
                    // get the vmware's profile
                    for (VmStateType vst : type.getVmStateArray()) {
                        if (vst.getVmPath().equals(strVMPathes[0])) {
                            // do changes here

                            // store the type
                            updater.storeActiveProfile(type);
                            break;
                        }
                    }
                    // optimization possible here..
                } else if (strAction.equals("create")) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("create_master", request.getParameter("vmmasterpath"));
                    map.put("create_name", strVMPathes[0]);
                    ServerSetup.sendVMCommand(strHost, null, "create", map);
                } else if (strAction.equals("delete")) {
                    for (String str : strVMPathes)
                        ServerSetup.sendVMCommand(strHost, str, "delete");
                } else if (strAction.equals("stop")) {
                    for (String str : strVMPathes)
                        ServerSetup.sendVMCommand(strHost, str, "stop");
                } else if (strAction.equals("start")) {
                    for (String str : strVMPathes)
                        ServerSetup.sendVMCommand(strHost, str, "start");
                } else if (strAction.equals("suspend")) {
                    for (String str : strVMPathes)
                        ServerSetup.sendVMCommand(strHost, str, "suspend");
                } else if (strAction.equals("list")) {
                    ServerSetup.sendVMCommand(strHost, null, "list");
                    ServerSetup.sendVMCommand(strHost, null, "getmasters");
                } else if (strAction.equals("getmasters")) {
                    ServerSetup.sendVMCommand(strHost, null, "getmasters");
                } else if (strAction.equals("getstate")) {
                    for (String str : strVMPathes)
                        ServerSetup.sendVMCommand(strHost, str, "powerstate");
                } else if (strAction.equals("gettoolsstate")) {
                    for (String str : strVMPathes)
                        ServerSetup.sendVMCommand(strHost, str, "toolsstate");
                } 
            }
        } catch (Exception e) {

        }

        out.write("WTF");

        // redirect
        if (request.getParameter("redirect") == null)
            response.sendRedirect("vm_list.jsp?host=" + strHost);
    }
%>