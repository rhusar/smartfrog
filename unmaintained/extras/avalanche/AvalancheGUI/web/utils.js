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


//======================================================================
// Function: setNextSubtitle()
//
// This function should be called when writing a page in the
// body frame. It writes the subtitle text for the page in a hidden
// field in the parent document. The new value is expected to be
// published once the frame has been loaded.
//
// Arguments: subtitle text
//======================================================================
function setNextSubtitle(subtitle) {
    var objStNext = parent.document.getElementById('subtitle');

    if (objStNext != null) {
        objStNext.innerHTML = "(" + subtitle + ")";
        document.title = "Avalanche: " + subtitle;
    }
}

//======================================================================
// Function: utSelectColor() [Internal]
//======================================================================
function utSelectColor(obj) {
    obj.style.background = "#CC99FF";
}

//======================================================================
// Function: utUnselectColor() [Internal]
//======================================================================
function utUnselectColor(obj) {
    obj.style.background = "#CCFFFF";
}

//======================================================================
// Function: utWriteMenuItem()
//======================================================================
function utWriteMenuItem(itemText, itemURL) {
    var d = document;

    d.writeln("<td>\n");

    if (itemURL == "") {
        d.writeln(" \
<span class=\"headerMenuItem\" style=\"background: #CC99FF\"> \
    <a href=\"#\">" + itemText + "</a> \
</span> \
	");
    }
    else {
        d.writeln(" \
<span class=\"headerMenuItem\" onMouseOver=\"utSelectColor(this)\" \
	    onMouseOut=\"utUnselectColor(this)\"> \
   <a href=\"" + itemURL + "\">" + itemText + "</a> \
</span>\n \
	");
    }
    d.writeln("</td>\n");
}

//======================================================================
// Function: oneVoiceUtWriteMenuItem()
//======================================================================
function oneVoiceUtWriteMenuItem(itemText, itemURL) {
    var d = document;

    var x = " \
<div class=\"bWrapperUp\"> \
  <div> \
    <div> \
      <input type=\"button\" class=\"hpButton\" \
	  onclick=\"" + itemURL + "\" value=\"" + itemText + "\" /> \
    </div> \
  </div> \
</div> \
";
    d.writeln(x);
}

//======================================================================
// Function: writePageMenu()
//
// This function writes the menu bar for a page, including a Help menu
// item. The menu bar is followed by a standard page help space. 
//======================================================================
/*function writePageMenu(pageId) {
    var d = document;
    d.writeln(" \
<table> \
<tbody> \
<tr> \
    ");

    for (var i = 1; i < writePageMenu.arguments.length; i++) {
        var itemText = writePageMenu.arguments[i++];
        var itemURL = writePageMenu.arguments[i];

        utWriteMenuItem(itemText, itemURL);
    }
    utWriteMenuItem("Help...", "javascript:doHelp('" + pageId + "');");

    d.writeln(" \
</tr> \
</tbody> \
</td></tr> \
</table> \
    ");

    writeHelpSpace();
}*/

//======================================================================
// Function: oneVoiceWritePageMenu()
//
// This function writes the menu bar for a page, including a Help menu
// item. The menu bar is followed by a standard page help space. 
//======================================================================
function oneVoiceWritePageMenu(pageId, loc)
{
    var d = document;
    d.writeln("<div class=\"buttonSet\">\n");
    if (loc == "header") {
        oneVoiceUtWriteMenuItem("Help...",
                "javascript:doHelp('" + pageId + "');");
    }

    for (var i = 2; i < oneVoiceWritePageMenu.arguments.length; i++) {
        var itemText = oneVoiceWritePageMenu.arguments[i++];
        var itemURL = oneVoiceWritePageMenu.arguments[i];

        oneVoiceUtWriteMenuItem(itemText, itemURL);
    }

    d.writeln("</div><div class=\"clearFloats\"></div>");

    if (loc == "header") {
        writeHelpSpace();
    }
}

//======================================================================
// Function: writeHelpSpace()
//
// Writes the HTML to create an empty help space.
//======================================================================
function writeHelpSpace()  {
    var d = document;
    
    var x = "<div align=\"center\">";
    x = x + "<div id=\"helpspace\" style=\"width: 90%; padding: 4px; margin-top: 10px; \
	margin-bottom: 10px; font-size: 9pt; \
	background-color: #CCE6FF; \
	display: none; border: 1px solid blue;\">";
    x = x + "</div></div>";

    d.writeln(x);
}

//======================================================================
// Function: doHelp()
//
// Opens the help space and displays the help text for the supplied
// page-id. If the helps space is already visible, this function 
// hides it, allowing the Help button to act as a toggle.
//======================================================================
function doHelp(pageId) {
    var obj = document.getElementById("helpspace");

    // If the help space is already visible, hide it and return:
    if (obj != null) {
        if (obj.style.display != "none")
        {
            dontHelp();
            return;
        }
        
       obj.style.display = "";
       obj.innerHTML = " \
                        <div id=\"helptext\" align=\"left\"></div> \
                        <br/> \
                        <center> \
                        <input type=\"button\" value=\"Close Help\" onClick=\"javascript:dontHelp()\" style=\"font-size: 9pt;\" /> \
                        </center> \
                        ";

        getHelp(pageId);
    }
}

//======================================================================
// Function: dontHelp()
//
// Hides the help space.
//======================================================================
function dontHelp() {
    var obj = document.getElementById("helpspace");
    obj.style.display = "none";
}

//======================================================================
// Function: setLocation()
//
// Invokes a new page
//======================================================================
function setLocation(loc) {
    window.location = loc;
}


function setBody(url) {
    document.getElementById('bodyPaneFrame').src = url;
}

 function toggle(divId)
 {
   var state = document.getElementById(divId).style.display ;
   if ( state == "none" )
   {
     document.getElementById(divId).style.visibility = "block";
   }else{
     document.getElementById(divId).style.visibility = "none";
   }
 }