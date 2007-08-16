<%-- /** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org */ --%>
<%@ page contentType="text/html" language="java" %>
<%@ page import="org.apache.xerces.parsers.DOMParser" %>
<%@ page import="org.w3c.dom.*" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>host_adding</title>
</head>
<body>
<table cellpadding="1" cellspacing="1" border="1">
<thead>
    <tr>
        <td rowspan="1" colspan="3">host_adding</td>
    </tr>
</thead>
<tbody>
<%
    String xmlHostname = null, xmlOs = null, xmlArch = null, xmlVendor = null,
            xmlUsername = null, xmlPassword = null, xmlJavaHome = null, xmlAvalancheHome = null;

    // Get new parser
    DOMParser parser = new DOMParser();
    try {
        // Access the help file
        // TODO: Clear if this is correct.
        parser.parse(getServletConfig().getServletContext().getRealPath("tests/testsetup.xml"));

        // Access the document
        Document doc = parser.getDocument();
        NodeList nodes = doc.getElementsByTagName("host");
        Node entry = null;

        for (int i = 0; i < nodes.getLength(); i++) {
            entry = nodes.item(i);
            // Check each node if it is the right entry
            if (entry.getAttributes().getNamedItem("use").getNodeValue().equals("true")) {
            xmlHostname = entry.getAttributes().getNamedItem("name").getNodeValue();

            for (int j = 0; j < entry.getChildNodes().getLength(); j++) {
                Node childNode = entry.getChildNodes().item(j);

                if (childNode.getNodeName().equals("basic")) {
                    xmlOs = childNode.getAttributes().getNamedItem("os").getNodeValue();
                    xmlVendor = childNode.getAttributes().getNamedItem("vendor").getNodeValue();
                    xmlArch = childNode.getAttributes().getNamedItem("arch").getNodeValue();
                }

                if (childNode.getNodeName().equals("credentials")) {
                    xmlUsername = childNode.getAttributes().getNamedItem("username").getNodeValue();
                    xmlPassword = childNode.getAttributes().getNamedItem("password").getNodeValue();
                }


                if (childNode.getNodeName().equals("environment")) {
                    if (childNode.getAttributes().getNamedItem("name").getNodeValue().equals("AVALANCHE_HOME")) {
                        xmlAvalancheHome = childNode.getAttributes().getNamedItem("value").getNodeValue();
                    }
                    if (childNode.getAttributes().getNamedItem("name").getNodeValue().equals("JAVA_HOME")) {
                        xmlJavaHome = childNode.getAttributes().getNamedItem("value").getNodeValue();
                    }
                }
            }
%>
<tr>
    <td>open</td>
    <td>/AvalancheGUI/host_list.jsp</td>
    <td></td>
</tr>
<tr>
    <td>clickAndWait</td>
    <td>//input[@value='Add a host']</td>
    <td></td>
</tr>
<!--Setting up basic data-->
<jsp:include page="header.inc.jsp?subtitle=Host Basic Settings Page"></jsp:include>
<jsp:include page="host_setup_actions.inc.jsp"></jsp:include>
<tr>
    <td>type</td>
    <td>hostId</td>
    <td><%= xmlHostname %>
    </td>
</tr>
<tr>
    <td>select</td>
    <td>os</td>
    <td>label=<%= xmlOs %>
    </td>
</tr>
<tr>
    <td>select</td>
    <td>platform</td>
    <td>label=<%= xmlVendor %>
    </td>
</tr>
<tr>
    <td>select</td>
    <td>arch</td>
    <td>label=<%= xmlArch %>
    </td>
</tr>
<tr>
    <td>clickAndWait</td>
    <td>save</td>
    <td></td>
</tr>
<!--Editing access modes-->
<jsp:include page="header.inc.jsp?subtitle=Host Access Modes Page"></jsp:include>
<jsp:include page="host_setup_actions.inc.jsp"></jsp:include>
<tr>
    <td>click</td>
    <td>//input[@value='Add an Access Mode']</td>
    <td></td>
</tr>
<tr>
    <td>type</td>
    <td>mode.userName.2</td>
    <td><%= xmlUsername %>
    </td>
</tr>
<tr>
    <td>type</td>
    <td>mode.password.2</td>
    <td><%= xmlPassword %>
    </td>
</tr>
<tr>
    <td>clickAndWait</td>
    <td>save</td>
    <td></td>
</tr>
<!--Editing transfer modes-->
<jsp:include page="header.inc.jsp?subtitle=Host Transfer Modes Page"></jsp:include>
<jsp:include page="host_setup_actions.inc.jsp"></jsp:include>
<tr>
    <td>click</td>
    <td>//input[@value='Add a Transfer Mode']</td>
    <td></td>
</tr>
<tr>
    <td>type</td>
    <td>mode.userName.2</td>
    <td><%= xmlUsername %>
    </td>
</tr>
<tr>
    <td>type</td>
    <td>mode.password.2</td>
    <td><%= xmlPassword %>
    </td>
</tr>
<tr>
    <td>clickAndWait</td>
    <td>save</td>
    <td></td>
</tr>
<!--Editing enviroment variables-->
<jsp:include page="header.inc.jsp?subtitle=Host Properties Page"></jsp:include>
<jsp:include page="host_setup_actions.inc.jsp"></jsp:include>
<tr>
    <td>select</td>
    <td>argument.name.2</td>
    <td>label=JAVA_HOME</td>
</tr>
<tr>
    <td>type</td>
    <td>argument.value.2</td>
    <td><%= xmlJavaHome %>
    </td>
</tr>
<tr>
    <td>click</td>
    <td>//input[@value='Add a Property']</td>
    <td></td>
</tr>
<tr>
    <td>select</td>
    <td>argument.name.3</td>
    <td>label=AVALANCHE_HOME</td>
</tr>
<tr>
    <td>type</td>
    <td>argument.value.3</td>
    <td><%= xmlAvalancheHome %>
    </td>
</tr>
<tr>
    <td>clickAndWait</td>
    <td>save</td>
    <td></td>
</tr>
<!--Basic Settings page again-->
<jsp:include page="header.inc.jsp?subtitle=Host Basic Settings Page"></jsp:include>
<jsp:include page="host_setup_actions.inc.jsp"></jsp:include>
<tr>
    <td>verifyText</td>
    <td>//div[2]/table/tbody/tr[1]/td[2]</td>
    <td><%= xmlHostname %>
    </td>
</tr>

<%
            }
        }
    } catch (Exception e) {
        // TODO: Improve exception handling.
        out.println(e);
    }
%>

</tbody>
</table>
</body>
</html>
