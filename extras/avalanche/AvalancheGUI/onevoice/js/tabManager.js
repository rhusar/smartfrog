/*
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

function TabManager() {

}

TabManager.prototype.init = function() {
	var divs = document.getElementsByTagName("DIV");
	for (var i=0;i<divs.length;i++) {
		if (( divs[i].className=="tabOn")|| ( divs[i].className=="tabOff"))  {
			// passes clicks from the div's themselves down into the link inside. This makes the tabs behave in a manner more consistent with application tabs. 
			if ( divs[i].className=="tabOff") {
				// a note on this click() method.   If you should find, on some new release of Mozilla, that anchorElements or other elements dont seem to have a click method or are throwing errors, then this might mean that a certain patch for mozilla's DOM has stopped working. (The patch in question concerns the fact that Mozilla does not currently give link elements a click method and that one must be then created for it.)
				divs[i].onclick = function() {this.childNodes[0].click();};
			}

			// min-width and min-height css doesnt work on ie6, so this goes in and does it by hand. 
			// note that there is currently an assumption that the second level tab divs are the IMMEDIATE children of the "secondaryTabSet" div. 
			if (document.all) {
				
				if (divs[i].parentNode.className=="secondaryTabSet") {
					if (divs[i].className == "tabOn") {
						if (divs[i].offsetWidth < 83) divs[i].style.width = "60px";
					}
					else if (divs[i].className == "tabOff") {
						if (divs[i].offsetWidth < 85) divs[i].style.width = "62px";
					}
				}
				else {
					if (divs[i].className == "tabOn") {
						if (divs[i].offsetWidth < 111) divs[i].style.width = "88px";
					}
					else if (divs[i].className == "tabOff") {
						if (divs[i].offsetWidth < 113) divs[i].style.width = "90px";
					}
				}
			}
		}
	}
}


