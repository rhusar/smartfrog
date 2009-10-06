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
function NavigationControlManager() {
	
}
NavigationControlManager.prototype.init = function() {
	var divs = document.getElementsByTagName("DIV");
	for (var i=0;i<divs.length;i++) {
		if (divs[i].className=="navigationControlSet") {
			var navigationControls = divs[i].getElementsByTagName("DIV")
			for (var j=0;j<navigationControls.length;j++) {
				navigationControls[j].childNodes[0].onclick = function(){ourNavigationControlManager.highlightItem(this);};
			}
		}
	}
}

NavigationControlManager.prototype.highlightItem = function(obj) {

	var lookingForWrapper = obj
	while ((lookingForWrapper) && (lookingForWrapper.className!="navigationControlSet")) {
		lookingForWrapper = lookingForWrapper.parentNode;
	}
	
	if (lookingForWrapper) {
		var navigationControls = lookingForWrapper.getElementsByTagName("DIV");
		for (var i=0;i<navigationControls.length;i++) {
			if (navigationControls[i]==obj.parentNode) {
				navigationControls[i].className = "navigationControlOn";
			}
			else {			
				navigationControls[i].className ="navigationControlOff";
			}
		}
	}
}

