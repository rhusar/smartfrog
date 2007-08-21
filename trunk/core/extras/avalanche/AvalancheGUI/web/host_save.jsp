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
<%@ page import="org.smartfrog.avalanche.core.module.*" %>
<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.core.host.*" %>
<h1>Saving host data...</h1>
<%@ include file="footer.inc.jsp" %>

<%
    String errMsg = null;
    HostManager manager = factory.getHostManager();
    if (null == manager) {
        errMsg = "Error connecting to hosts database";
        throw new Exception("Error connecting to hosts database");
    }

    HostType host = null;

    // Retrieve parameters
    String hostId = request.getParameter("hostId");
    String pageAction = request.getParameter("action");

    if ((hostId != null) && (pageAction != null)) {
        // Strip off spaces
        hostId = hostId.trim().toLowerCase();
        pageAction = pageAction.trim().toLowerCase();

        if (!hostId.equals("") && !pageAction.equals("")) { // Basic settings
            if (pageAction.equals("bs")) {
                // Retrieve parameters and save them
                String os = request.getParameter("os");
                String plaf = request.getParameter("platform");
                String arch = request.getParameter("arch");

                try {
                    // Host specific data
                    PlatformSelectorType pst = null;

                    // Get host from database
                    host = manager.getHost(hostId);

                    if (host == null) {
                        // Create new entry in database and a new XMPP account
                        host = manager.newHost(hostId);
                        // Save data to the database
                        pst = host.addNewPlatformSelector();
                    } else {
                        // Get data from database
                        pst = host.getPlatformSelector();
                        if (null == pst) {
                            pst = host.addNewPlatformSelector();
                        }
                    }

                    // Update the dataset
                    pst.setOs(os);
                    pst.setPlatform(plaf);
                    pst.setArch(arch);

                    // Save the changes
                    // Save the changes made
                    manager.setHost(host);
                } catch (Exception e) {

                }
            } else {
                // Look up in database
                host = manager.getHost(hostId);
                if (host != null) {
                    if (pageAction.equals("am")) {
                        // save access modes for the host
                        HostType.AccessModes modes =
                                HostType.AccessModes.Factory.newInstance();
                        // overwrite existing hosts
                        java.util.Enumeration params = request.getParameterNames();

                        String defaultAccessMode =
                                request.getParameter("defaultAccessMode");

                        while (params.hasMoreElements()) {
                            String param = (String) params.nextElement();
                            String s = "mode.userName.";
                            if (param.startsWith(s)) {
                                // its access Mode
                                String idx = param.substring(s.length(), param.length());

                                String type = request.getParameter("mode.type." + idx);
                                String userName = request.getParameter(param);
                                String password =
                                        request.getParameter("mode.password." + idx);

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
                    } else if (pageAction.equals("tm")) {
                        HostType.TransferModes transferModes = HostType.TransferModes.Factory.newInstance();
                        // save access modes for the host
                        // overwrite existing hosts
                        java.util.Enumeration params = request.getParameterNames();

                        String defaultAccessMode =
                                request.getParameter("defaultTransferMode");

                        while (params.hasMoreElements()) {
                            String param = (String) params.nextElement();
                            String s = "mode.userName.";
                            if (param.startsWith(s)) {
                                // its access Mode
                                String idx = param.substring(s.length(), param.length());

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
                    } else if (pageAction.equals("env")) {
                        java.util.Enumeration params = request.getParameterNames();
                        ArgumentType args = ArgumentType.Factory.newInstance();
                        while (params.hasMoreElements()) {
                            String param = (String) params.nextElement();
                            String s = "argument.name.";
                            if (param.startsWith(s)) {
                                // its access Mode
                                String idx = param.substring(s.length(), param.length());

                                String name = request.getParameter(param);
                                String value =
                                        request.getParameter("argument.value." + idx);

                                ArgumentType.Argument arg = args.addNewArgument();
                                arg.setName(name);
                                arg.setValue(value);
                            }
                        }
                        host.setArguments(args);
                    }
                    // Save the changes made
                    manager.setHost(host);
                } else {
                    // Go and create a host
                    response.sendRedirect("host_setup_bs.jsp");
                }
            }
            // Go to the next page
            response.sendRedirect("host_setup_" + ((request.getParameter("next") == null) ? "am" : request.getParameter("next")) + ".jsp?hostId=" + request.getParameter("hostId"));
        } else {
// Go and create a host
            session.setAttribute("error_msg", "Please enter a valid host name.");
            response.sendRedirect("host_setup_bs.jsp");
        }
    } else {
// Go and create a host
        session.setAttribute("error_msg", "Please enter a valid host name.");
        response.sendRedirect("host_setup_bs.jsp");
    }


%>
