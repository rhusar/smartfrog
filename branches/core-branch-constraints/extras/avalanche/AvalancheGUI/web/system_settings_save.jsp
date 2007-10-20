<%-- /**
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
*/ --%>
<%@ page language="java" %>
<%@ include file="InitBeans.jsp" %>
<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.settings.xdefault.*" %>

<%  SettingsManager sett = factory.getSettingsManager();
    if (null == sett) {
        session.setAttribute("error_msg", "Error connecting to settings database.");
        throw new Exception("Error connecting to settings database");
    }
    SettingsType defSettings = SettingsType.Factory.newInstance();

    java.util.Enumeration params = request.getParameterNames();

    String[] oses = request.getParameterValues("os");
    defSettings.setOsArray(oses);

    String[] plafs = request.getParameterValues("platform");
    defSettings.setPlatformArray(plafs);

    String[] archs = request.getParameterValues("arch");
    defSettings.setArchArray(archs);

    String[] modes = request.getParameterValues("accessMode");
    if (null != modes) {
        for (String mode1 : modes) {
            SettingsType.AccessMode newMode = defSettings.addNewAccessMode();
            newMode.setName(mode1);
        }
    }
    String[] dModes = request.getParameterValues("dataTransferMode");
    if (null != dModes) {
        for (String dMode : dModes) {
            SettingsType.DataTransferMode newMode = defSettings.addNewDataTransferMode();
            newMode.setName(dMode);
        }
    }

    String[] actions = request.getParameterValues("action");
    if (null != actions) {
        for (String action : actions) {
            SettingsType.Action newAction = defSettings.addNewAction();
            newAction.setName(action);
        }
    }

    String[] props = request.getParameterValues("prop");
    defSettings.setSystemPropertyArray(props);

    while (params.hasMoreElements()) {
        String param = (String) params.nextElement();

        if (param.startsWith("engine.name")) {
            String suf = param.substring(11, param.length());

            String engine = request.getParameter(param);
            String eclassParam = "engine.class" + suf;
            String engineClass = request.getParameter(eclassParam);

            SettingsType.DeploymentEngine eng =
                    defSettings.addNewDeploymentEngine();
            eng.setName(engine);
            eng.setClass1(engineClass);
        }
    }

    sett.setDefaultSettings(defSettings);
    response.sendRedirect("system_settings.jsp");
%>
