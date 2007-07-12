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
<%-- $Id: Main.jsp 64 2006-02-21 15:44:25Z jem $ --%>
<%@ page language="java" %>
<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<title>Avalanche</title>
<%@ include file="common.jspjsp" %>
    <script language="javascript">
        function setBody(url) {
            document.getElementById('bodyPaneFrame').src = url;
        }

        //======================================================================
        // Function: writeSubtitle()
        //
        // This function should be invoked each time the body frame is loaded.
        // It copies the value from the hidden "subtitle_next" field into the
        // subtitle field and into the document's title property, for display
        // in the browser's title bar. Individual pages should set the
        // "subtitle_next" field by calling the common function setNextTitle().
        //======================================================================
        function writeSubtitle()
        {
            var objSt = parent.document.getElementById('subtitle');
            var objStNext = parent.document.getElementById('subtitle_next');

            var subtitle = objStNext.value;

            if (objSt != null && objStNext != null)
            {
                objSt.innerHTML = (subtitle == "") ? "" : "(" + subtitle + ")";
                document.title = "Avalanche" +
                                 ((subtitle == "") ? "" : ": " + subtitle);
            }
            setNextSubtitle("");
        }

    </script>
</head>

<body>
<table border="0" cellpadding="0" cellspacing="0" width="100%" class="applicationMastheadLarge">
    <tr>
        <td class="mastheadIcon" width="42"><img src="images/frog.gif" width="40" height="40" border="0" alt="HP">
        </td>
        <td class="mastheadTitle">
            <h1>Avalanche<BR>
                Deployment System
                <i><span id="subtitle" name="subtitle"
                         style="font-size: 10pt;">
        </span></i>
                <input type="hidden" id="subtitle_next" name="subtitle_next" value=""></td>
        <td width="307">
            <div class="mastheadPhoto" style="padding-right:0px;"><img src="images/group.png" width="157" height="57"
                                                                       border="0" alt=""></div>
        </td>
    </tr>
</table>
<!-- <table style="border-collapse: collapse; padding: 0;">
<tr><td height="10px"></td></tr>
<tr>
    <td rowspan="2" valign="middle"><img src="images/hp_logo.jpg"></td> 
    <td class="pageHead">&nbsp;&nbsp;Grid Resource Integration Toolkit</td>
</tr>
<tr>
    <td class="pageHead">
	<i>&nbsp;&nbsp;Avalanche Deployment System
	    <span id="subtitle" name="subtitle"
		style="color: blue; font-size: 10pt;">
	    </span>
	</i>
	<input type="hidden" id="subtitle_next" name="subtitle_next" value="">
    <td>
</tr>
</table>
-->

<hr/>

<!-- 
	width="15%" height="90%" 
	marginwidth="1" marginheight="1" 
	style="border-right-style: solid; border-right-color: aqua;
		 border-right-width: 1;"
	align="left"
<iframe id="sidePane" name="sidePane" 
	width="100%" height="80px" 
	scrolling="no" 
        frameborder="0" 
	src="SidePane.jsp">
    Error! Your browser either does not support inline frames or is 
    currently configured not to display inline frames.
</iframe>
-->

<%@ include file="SidePane.jspjsp" %>
<!--
	width="84%" height="90%"
	marginwidth="1" marginheight="1"
	scrolling="yes"
	align="right" 
-->
<iframe id="bodyPaneFrame" name="bodyPane"
	width="100%" height="100%"
	border="0" frameborder="0" 
	onLoad="writeSubtitle();"
	src="InitPage.jsp">
    Error! Your browser either does not support inline frames or is
    currently configured not to display inline frames.
</iframe>

</body>
</html>
