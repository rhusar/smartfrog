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
<%-- $Id: SidePane.jsp 62 2006-02-10 22:52:54Z jem $ --%>
<%@ page language="java" %>
<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<title>Side Panel</title>
<%@ include file="common.jspjsp" %>

</head> 

<script language="javascript">

var menus = new Array();

function writeMenuHeader(menuId,text)
{  
    document.writeln('\
<div id="' + menuId + '" class="globalNavTrigger">' + text );
}

function writeMenuStart(parentMenu)
{   document.writeln('\
<ul id="' + parentMenu + '" class="dropdownMenu" isdropdownmenu="yes">');
}

function writeMenuItem(text,URL,menuItemID)
{   document.writeln('\
<li id="menuItemID' + menuItemID + '"> \
    <a href="javascript:parent.setBody(\'' + URL + '\')">' + text + '</a></li> \
');
}

function writeMenuEnd()
{
   document.writeln('</ul></div>');
}

function writeHelpID(menuId,text)
{
   document.writeln('\
<div align="right" id="' + menuId + '" class="globalNavTrigger">' + text +'\
</div> \
');
}

function showMenu(menuId){
    var i=0;
    
    var menu = document.getElementById(menuId + 'items');
    var state = menu.style.display;

    for (i=0; i<menus.length; i++) {
	var m = document.getElementById(menus[i] + 'items');
	m.style.display = "none"	
    }
    
    if( state == "none" ){
	menu.style.display = "block"
    }
}

function selectColor(div){
	div.style.background = "#CC99FF" ;
}

function unSelectColor(div){
	div.style.background = "#CCFFFF" ;
}

function menuHeaderSelect(div){
	div.style.background = "#6699FF";
}

function menuHeaderUnselect(div){
	div.style.background = "#CCFFFF";
}
</script>

<body>
<div style="margin-right:7px;">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td class="dropdownNavMainCell">
<script>
writeMenuHeader("menu1_trigger","Software Resources");
writeMenuStart("menu1");
writeMenuItem("List Modules","ListModules.jsp","1");
writeMenuEnd();

writeMenuHeader("menu2_trigger","Hosts");
writeMenuStart("menu2");
writeMenuItem("List Hosts","ListHosts.jsp","2");
writeMenuItem("List Active Hosts","ListHostsActive.jsp","2");
writeMenuItem("Host Groups","HostGroups.jsp","2");
writeMenuEnd();

writeMenuHeader("menu3_trigger","Configuration");
writeMenuStart("menu3");
writeMenuItem("Supported Actions","SFActions.jsp","3");
writeMenuItem("Deployment Engines","sfSettings.jsp","3");
writeMenuEnd();

writeMenuHeader("menu4_trigger","Reports");
writeMenuStart("menu4");
writeMenuItem("View Logs","ActiveView.jsp","4");
//writeMenuItem("View ToDo List","AvalancheGuiToDo.html","4");
writeMenuEnd();

// writeHelpID("menuLast_trigger","Help");
</script>
    </td>
    <td class="dropdownNavRightBorder"><div></div></td>
  </tr>
  <tr>
    <td class="dropdownNavBottomBorder" colspan="2"><div></div></td>
  </tr>
</table>
<script language="JavaScript" type="text/javascript">
         reconcileEventHandlers();
</script>
</div>
</body>

</html>
