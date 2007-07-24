/* (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

For more information: www.smartfrog.org */

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
    <a href="' + URL + '">' + text + '</a></li> \
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