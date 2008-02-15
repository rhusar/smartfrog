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
<%@ page errorPage="error.jsp" %>
<%@ include file="InitBeans.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Avalanche</title>

    <meta http-equiv="CACHE-CONTROL" content="NO-CACHE" />
    <meta http-equiv="EXPIRES" content="0" />
    <meta http-equiv="CONTENT-TYPE" content="text/html;charset=utf-8" />

    <link rel="stylesheet" type="text/css" href="onevoice/css/default.css" />
    <link rel="stylesheet" type="text/css" href="onevoice/css/blue_theme.css" />
    <link rel="alternate" type="application/rss+xml" title="Avalanche: Host Status" href="host_status_feed.jsp">

    <script language="JavaScript" type="text/javascript" src="navigation.js"></script>
    <script language="JavaScript" type="text/javascript" src="ajax.js"></script>
    <script language="JavaScript" type="text/javascript" src="help_get_ajax.js"></script>
    <script language="JavaScript" type="text/javascript" src="utils.js"></script>

    <script language="JavaScript" type="text/javascript" src="onevoice/js/tableManager.js"></script>
    <script language="JavaScript" type="text/javascript" src="onevoice/js/buttonManager.js"></script>
    <script language="JavaScript" type="text/javascript" src="onevoice/js/dropdownMenuManager.js"></script>
    <script language="JavaScript" type="text/javascript" src="onevoice/js/tabManager.js"></script>
    <script language="JavaScript" type="text/javascript" src="onevoice/js/transferBoxManager.js"></script>
    <script language="JavaScript" type="text/javascript" src="onevoice/js/global.js"></script>

</head>

<body>

<!-- Page title banner -->
<table border="0" cellpadding="0" cellspacing="0" width="100%" class="applicationMastheadLarge">
    <tr>
        <td class="mastheadIcon" width="42">
            <a href="main.jsp"><img src="images/frog.gif" width="40" height="40" border="0" alt="SmartFrog"/></a>
        </td>
        <td class="mastheadTitle">
            <h1><a href="main.jsp" style="text-decoration:none;">Avalanche Deployment System</a></h1>
            <h2><i><span id="subtitle" style="font-size:10pt;color:lightcyan;" /></i></h2>
        </td>
        <td width="307">
            <div class="mastheadPhoto" style="padding-right:0;">
            <img src="images/group.png" width="157" height="57" border="0" alt="SmartFrog" /></div>
        </td>
    </tr>
</table>

<hr/>


<%@ include file="navigation.inc.jsp" %>