/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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


var helptext = false;
var XMLHttpRequestObject = false;

// If Mozilla, Firefox, Safari
if (window.XMLHttpRequest) {
    XMLHttpRequestObject = new XMLHttpRequest();
    XMLHttpRequestObject.overrideMimeType("text/xml");
    // Else if Microsoft Browser
} else if (window.ActiveXObject) {
    XMLHttpRequestObject = new
            ActiveXObject("Microsoft.XMLHTTP");
}

function getHelp(pageId) {

    if (XMLHttpRequestObject) {
        XMLHttpRequestObject.open("GET", "help_get.jsp?id=" + pageId + "&now=" + (new Date).getMilliseconds());

        XMLHttpRequestObject.onreadystatechange = function()
        {
            // If everything went alright
            if (XMLHttpRequestObject.readyState == 4 && XMLHttpRequestObject.status == 200) {
                var xmlDocument = XMLHttpRequestObject.responseXML;
                helptext = xmlDocument.getElementsByTagName("message")[0].firstChild.data;
            }
            printHelp();
        }

        XMLHttpRequestObject.send(null);
    }
}

function printHelp() {
    var targetDiv = false;
    targetDiv = document.getElementById("helptext");

    // If targetDiv is found
    if (targetDiv) {
        // Print intro
        targetDiv.innerHTML = "<center><b>Help<b></center><p>";
        // ... and text
        if (helptext) {
            targetDiv.innerHTML = targetDiv.innerHTML + helptext;
        } else {
            targetDiv.innerHTML = targetDiv.innerHTML + "There is no help for this topic. Sorry!";
        }
        targetDiv.innerHTML = targetDiv.innerHTML + "</p>";
    }
}