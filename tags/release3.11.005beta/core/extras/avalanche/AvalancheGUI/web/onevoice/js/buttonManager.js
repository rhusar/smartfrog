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


function ButtonManager() {

}


ButtonManager.prototype.getWrapperReference = function(btnWrapperObj) {
	var	ob = btnWrapperObj;
	var className = ob.className;
	while (className.indexOf("bWrapper")==-1) {
		ob = ob.parentNode;
		className = ob.className;
	}
	return ob;
}

ButtonManager.prototype.hpButtonOver = function(btnWrapperObj) {
	if (!this.getButtonChild(btnWrapperObj).disabled) {
		var ob = this.getWrapperReference(btnWrapperObj);
		if (ob.className.indexOf("bEmphasized")!=-1) {
			ob.className = "bWrapperOver bEmphasized";
		}
		else {
			ob.className = "bWrapperOver";
		}
	}
}

ButtonManager.prototype.hpButtonUp = function(btnWrapperObj) {
	if (!this.getButtonChild(btnWrapperObj).disabled) {
		var ob = this.getWrapperReference(btnWrapperObj);
		if (ob.className.indexOf("bEmphasized")!=-1) {
			ob.className = "bWrapperUp bEmphasized";
		}
		else {
			ob.className = "bWrapperUp";
		}

	}
}

ButtonManager.prototype.hpButtonDown = function(btnWrapperObj) {
	var ob = this.getWrapperReference(btnWrapperObj);
	var childButton = this.getButtonChild(btnWrapperObj);
	if (!childButton.disabled) {
		if (ob.className.indexOf("bEmphasized")!=-1) {
			ob.className = "bWrapperDown bEmphasized";
		}
		else {
			ob.className = "bWrapperDown";
		}
		childButton.focus();
	}
}

ButtonManager.prototype.disableButton = function(btnObj) {
	btnObj.disabled = true;
	var wrapper = this.getWrapperReference(btnObj);
	var wrapper = this.getWrapperReference(btnObj);
	wrapper.onmousedown= null;
	wrapper.onmouseup=  null;
	wrapper.onmouseover=  null;
	wrapper.onmouseout=  null;
	if (wrapper.className.indexOf("bEmphasized")!=-1) {
		wrapper.className = "bWrapperDisabled bEmphasized";
	}
	else {
		wrapper.className = "bWrapperDisabled";
	}
}
ButtonManager.prototype.enableButton = function(btnObj) {
	btnObj.disabled = false;
	var wrapper = this.getWrapperReference(btnObj);
	if (wrapper.className.indexOf("bEmphasized")!=-1) {
		wrapper.className = "bWrapperUp bEmphasized";
	}
	else {
		wrapper.className = "bWrapperUp";
	}
	var wrapper = this.getWrapperReference(btnObj);
	wrapper.onmousedown= function() {ourButtonManager.hpButtonDown(this);}
	wrapper.onmouseup= function() {ourButtonManager.hpButtonOver(this);}
	wrapper.onmouseover= function() {ourButtonManager.hpButtonOver(this);}
	wrapper.onmouseout= function() {ourButtonManager.hpButtonUp(this);}
}
ButtonManager.prototype.disableButtonById = function(btnId) {
	var btnObj = document.getElementById(btnId)
	this.disableButton(btnObj);
}
ButtonManager.prototype.enableButtonById = function(btnId) {
	var btnObj = document.getElementById(btnId)
	this.enableButton(btnObj);
}
ButtonManager.prototype.getButtonChild = function(divObj) {
	var obj = divObj
	while ((obj.childNodes[0]!=null) && ((obj.tagName!="BUTTON") && (obj.tagName!="INPUT"))  &&  (obj.tagName!="A") && (obj.tagName!="SUBMIT") ) {
		obj = obj.childNodes[0];	
	}
	return obj;
}


ButtonManager.prototype.init  = function() {
	var divs = document.getElementsByTagName("DIV");
	for (var i=0;i<divs.length;i++) {
		if (divs[i].className.indexOf("bWrapper")!=-1) {
				
			// some button styles might have two nested divs, making a 2px border, or three.  therefore we have to be careful when we look down inside the divs looking for the buttons. 
			var button = this.getButtonChild(divs[i])[0];

			// looking up to see if we're in a vertical button set. 
			var lookingForVerticalButtonSet = divs[i];
			while ((lookingForVerticalButtonSet) && (lookingForVerticalButtonSet.className!="verticalButtonSet")) {
				lookingForVerticalButtonSet = lookingForVerticalButtonSet.parentNode;
			}
			if (lookingForVerticalButtonSet) {
				button.style.width = "100%";
			}
			/* DOES NOT WORK WITH FIREFOX?
			else if ((!button.getAttribute('disableminimumwidth')) && (button.className.indexOf("shrinkWrapButton")==-1)){

				
				// setting widths based on which size button this is to be. 
				// tempting to use CSS property min-width and  accept that it only works on mozilla. However mozilla forces align left in this case, which does more harm than good to the design. 
				if (button.className.indexOf("hpButtonSmall")!=-1) {				
					if (button.offsetWidth < 47) button.style.width = "47px";
				}
				else if (button.className.indexOf("hpButtonVerySmall")!=-1) {
					// these are tiny buttons with no minimum width, like the help button on the pagetitle bar. 
				}

				// catchall 
				//  This line causes some mozilla problems with the vertical buttons btw, although it is no longer used by them. 
				else if (button.offsetWidth < 83) button.style.width = "83px";
			}

			if (!button.disabled) {
				divs[i].onmousedown= function() {ourButtonManager.hpButtonDown(this);}
				divs[i].onmouseup= function() {ourButtonManager.hpButtonOver(this);}
				divs[i].onmouseover= function() {ourButtonManager.hpButtonOver(this);}
				divs[i].onmouseout= function() {ourButtonManager.hpButtonUp(this);}
				


				// this effectively transfers the onclick up to the level of the div. 
				// this makes the entire div+button structure effectively into the 'button', and moreover covers cases when the user clicks only one of the the right or bottom borders. 
				if (button.onclick) {	
					eval("divs[i].onclick = function() {var hardCodedFunction = "+button.onclick+" ;return hardCodedFunction();};");
					button.onclick = function(event) {};
				}
				

				var buttonChildren = divs[i].getElementsByTagName("BUTTON");
				var inputChildren = divs[i].getElementsByTagName("INPUT");
				for (var j=0;j<buttonChildren.length;j++) {

					buttonChildren[j].onfocus = function() {ourButtonManager.hpButtonDown(this);}
					buttonChildren[j].onblur = function() {ourButtonManager.hpButtonUp(this);}
				}
				for (var j=0;j<inputChildren.length;j++) {
					inputChildren[j].onfocus = function() {ourButtonManager.hpButtonDown(this);}
					inputChildren[j].onblur = function() {ourButtonManager.hpButtonUp(this);}
				}
			}
			else {
				if (divs[i].className.indexOf("bEmphasized")!=-1) {
					divs[i].className = "bWrapperDisabled bEmphasized";
				}
				else {
					divs[i].className = "bWrapperDisabled";
				}
			}*/
		}
	}
}

