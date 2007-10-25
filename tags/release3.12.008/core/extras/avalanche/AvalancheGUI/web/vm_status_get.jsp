<%-- /*
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

<%@ page language="java" contentType="text/xml" %>
<%@ include file="InitBeans.jsp" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.parsers.*" %>
<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater"%>
<%@ page import="org.smartfrog.avalanche.shared.XMLHelper" %>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.VmStateType"%>
<%@ page import="org.smartfrog.avalanche.server.ServerSetup" %>

<%@ include file="init_hostmanager.inc.jsp"%>
<% String[] hosts = manager.listHosts();

    // Be able to query ActiveProfile
    ActiveProfileUpdater updater = new ActiveProfileUpdater();
    ActiveProfileType type = null;

    // Create output document
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document xdoc = db.newDocument();

    // Create root object
    Element root = xdoc.createElement("vmlist");
    xdoc.appendChild(root);

    String strHost = request.getParameter("host");

    Element entry = null;
    Element subentry = null;

    if (strHost != null) {
        // only list virtual machines of specified host
        try {
            type = updater.getActiveProfile(strHost);

            if (type.getHostState().equals("Available")) {
                // Create <host name="xxx"> node
                entry = xdoc.createElement("host");
                entry.setAttribute("name", strHost);

                for (VmStateType vmt : type.getVmStateArray()) {
                    // create a vm element
                    subentry = xdoc.createElement("vm");

                    XMLHelper.addTextNode(xdoc, subentry, "vmpath", vmt.getVmPath());
                    XMLHelper.addTextNode(xdoc, subentry, "vmcmd", vmt.getVmLastCmd());
                    XMLHelper.addTextNode(xdoc, subentry, "vmresponse", vmt.getVmResponse());

                    entry.appendChild(subentry);
                }

                // Append <host> node to hostlist
                root.appendChild(entry);
            }
        } catch (Exception e) {

        }
    } else {
        // For each host
        for (String host : hosts) {
            try {
                // Query ActiveProfile
                type = updater.getActiveProfile(host);

                if (type.getHostState().equals("Available")) {
                    // Create <host name="xxx"> node
                    entry = xdoc.createElement("host");
                    entry.setAttribute("name", host);

                    for (VmStateType vmt : type.getVmStateArray()) {
                        // create a vm element
                        subentry = xdoc.createElement("vm");

                        XMLHelper.addTextNode(xdoc, subentry, "vmpath", vmt.getVmPath());
                        XMLHelper.addTextNode(xdoc, subentry, "vmcmd", vmt.getVmLastCmd());
                        XMLHelper.addTextNode(xdoc, subentry, "vmresponse", vmt.getVmResponse());

                        entry.appendChild(subentry);
                    }

                    // Append <host> node to hostlist
                    root.appendChild(entry);
                }
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    // Print output
    out.clear();
    out.write(XMLHelper.XMLToString(xdoc));
%>