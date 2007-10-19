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
<%@ page import="org.smartfrog.avalanche.server.*" %>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*" %>
<%@ page import="org.smartfrog.avalanche.settings.sfConfig.*" %>
<%@ include file="InitBeans.jsp" %>

<%

    SettingsManager settingsMgr = factory.getSettingsManager();
    SfConfigsType configs = settingsMgr.getSFConfigs();

    String pageAction = request.getParameter("pageAction");

    javax.servlet.RequestDispatcher dispatcher = null;
    String title = request.getParameter("title");

    if (pageAction != null) {
        if (pageAction.equals("addAction")) {
            // if already exists its an error
            SfDescriptionType desc = null;
            SfDescriptionType[] descs = configs.getSfDescriptionArray();
            for (int i = 0; i < descs.length; i++) {
                if (descs[i].getTitle().equals(title)) {
                    desc = descs[i];
                    break;
                }
            }
            if (null == desc) {
                String action = request.getParameter("sfAction");
                String url = request.getParameter("url");

                if (null != title && null != url && null != action) {
                    // check if the config file is accessible
                    boolean flag = true;
                    try {
                        SFAdapter.getSFAttributes(url);
                    } catch (Exception e) {
                        flag = false;
                        session.setAttribute("message", "Error loading URL \""
                                + url + "\", Msg: " + e.getMessage());
                    }

                    // dont create if invalid URL
                    if (flag) {
                        desc = configs.addNewSfDescription();
                        desc.setUrl(url);
                        desc.setAction(action);
                        desc.setTitle(title);

                        settingsMgr.setSfConfigs(configs);
                        /*SfDescriptionType[] descs1 = configs.getSfDescriptionArray();
                  String t = null;
                  for( int i=0;i<descs1.length;i++){
                      t = descs1[i].getTitle();
                  }*/
                    }
                } else {
                    session.setAttribute("message", "Invalid values, Title: "
                            + title + ", URL: " + url + ", Action: " + action);
                }
            } else {
                session.setAttribute("message",
                        "Action title \"" + title + "\" already exists");
            }

            response.sendRedirect("sf_action.jsp?title=" + title);
        } else if (pageAction.equals("setActionArgs")) {
            SfDescriptionType desc = null;
            SfDescriptionType[] descs = configs.getSfDescriptionArray();
            for (int i = 0; i < descs.length; i++) {
                if (descs[i].getTitle().equals(title)) {
                    desc = descs[i];
                    break;
                }
            }
            if (null != desc) {
                String[] selectedArgs =
                        request.getParameterValues("selectedArg");
                java.util.Enumeration params = request.getParameterNames();

                // delete old values from XML
                while (desc.sizeOfArgumentArray() > 0) {
                    desc.removeArgument(0);
                }

                while (params.hasMoreElements()) {
                    String param = (String) params.nextElement();
                    String t = "argument.name.";
                    if (param.startsWith(t)) {
                        String argName = request.getParameter(param);

                        String suf = param.substring(t.length(), param.length());
                        String description =
                                request.getParameter("argument.description." + suf);
                        String value =
                                request.getParameter("argument.defaultValue." + suf);

                        // set only if checked in the page
                        for (int k = 0; k < selectedArgs.length; k++) {
                            if (selectedArgs[k].equals(argName)) {
                                SfDescriptionType.Argument arg =
                                        desc.addNewArgument();

                                arg.setName(argName);
                                arg.setValue(value);
                                arg.setDescription(description);
                            }
                        }
                    }
                }
                settingsMgr.setSfConfigs(configs);
            } else {
                session.setAttribute("message",
                        "Could not locate configuration for \"" + title + "\"");
            }
            response.sendRedirect("sf_action.jsp");
        } else if (pageAction.equals("delAction")) {
            String[] selectedTitles =
                    request.getParameterValues("selectedAction");
            SfDescriptionType desc = null;

            for (int i = 0; i < selectedTitles.length; i++) {
                SfDescriptionType[] descs = configs.getSfDescriptionArray();
                for (int j = 0; j < descs.length; j++) {
                    if (descs[j].getTitle().equals(selectedTitles[i])) {
                        configs.removeSfDescription(j);
                        break;
                    }
                }
            }
            settingsMgr.setSfConfigs(configs);
            response.sendRedirect("sf_action.jsp");
        } else if (pageAction.equals("setActionLibs")) {
            SfDescriptionType desc = null;
            SfDescriptionType[] descs = configs.getSfDescriptionArray();
            for (int i = 0; i < descs.length; i++) {
                if (descs[i].getTitle().equals(title)) {
                    desc = descs[i];
                    break;
                }
            }
            if (null != desc) {
                // set classpaths
                String[] classpaths = request.getParameterValues("classpath");
                if (null != classpaths) {
                    desc.setClassPathArray(classpaths);
                    settingsMgr.setSfConfigs(configs);
                }
            } else {
                session.setAttribute("message", "Action \"" + title +
                        "\"not found");
            }
            response.sendRedirect("sf_action.jsp");
        }
    } else {
        response.sendRedirect("sf_action.jsp");
    }
%>
