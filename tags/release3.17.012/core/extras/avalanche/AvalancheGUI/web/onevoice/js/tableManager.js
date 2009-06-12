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


function TableManager() {
	// a global var holding a roster of all scrollableTables onscreen. Used in windowResize, for the following reason.  If a tableis scrolled horizontally, and the window is widened, you basically need this to realign the columns as the table is sort of unscrollbarring itself.  onscroll on the div will not fire for us, although you might think it should. 
	this.scrollableTableIds = new Array();
}


TableManager.prototype.init = function() {
	tableNodeCollection = window.document.getElementsByTagName("table");

	// dealing with scrollingTables
	for (var i=0; i<tableNodeCollection.length; i++ ) {
		if (tableNodeCollection[i].className.indexOf("scrollingTable")!=-1) {
			var tableId = tableNodeCollection[i].getAttribute('id');
			// file it away for use by the windowResize function
			this.scrollableTableIds[this.scrollableTableIds.length] = tableId;
			// this is the big function that aligns the divs with the table columns
			this.resizeTableColumns(tableId);
			// a nice way to leverage anonymous functions to allow us to keep event handler code out of the html. A nice side effect is that event handlers arent in place until the page is loaded, at which point they generally also become safe to run.
			eval("document.getElementById(tableId + '_scrollingTableDiv').onscroll=function() {ourTableManager.slideColumns('"+tableId+"')}");
		}
	}

	// dealing with tables that have 'select all' checkboxes.
	inputNodeCollection = window.document.getElementsByTagName("input");
	for (var i=0; i<inputNodeCollection.length; i++ ) {
		var chk;
		if (inputNodeCollection[i].getAttribute('tableid')) {
			chk = inputNodeCollection[i];
			var tableId = chk.getAttribute("tableid");
			if (tableId) {
				eval("chk.onclick = function(mozEvent) {ourTableManager.tableCheckboxToggleAll(mozEvent,this,'"+ tableId +"');};");
			}
			else alert('error: table has checkbox but checkbox has no tableid');
		}
		if (inputNodeCollection[i].getAttribute('rowselector')=="yes") { 

			//  if this is loading from a refresh rather than a load, or if an application has been implemented too quickly, radio or checkbox state might be different from the classnames as they came down from the server. 
			if (((inputNodeCollection[i].checked) || (inputNodeCollection[i].value=="1")) && (inputNodeCollection[i].parentNode.parentNode.className.indexOf("rowHighlight")==-1)) {
				appendClassName(inputNodeCollection[i].parentNode.parentNode, "rowHighlight");
			}
			else if (((!inputNodeCollection[i].checked) && (inputNodeCollection[i].value!="1")) && (inputNodeCollection[i].parentNode.parentNode.className.indexOf("rowHighlight")!=-1)) {
				removeClassName(inputNodeCollection[i].parentNode.parentNode,"rowHighlight");				
			}

			// both of these result in the same data going into tableRowHighlightToggle, although the event will have different srcElement/currentTarget.  
			inputNodeCollection[i].onclick = function(mozEvent) {ourTableManager.tableRowHighlightToggle(mozEvent,this.parentNode.parentNode,true);};

			var checkingForPropertyViewTable = inputNodeCollection[i].parentNode;
			var isPropertyViewTable = false;
			while (checkingForPropertyViewTable.tagName!="TABLE") {
				checkingForPropertyViewTable = checkingForPropertyViewTable.parentNode;
			}
			if (checkingForPropertyViewTable.className.indexOf("propertyViewTable")!=-1)  isPropertyViewTable = true;
			inputNodeCollection[i].parentNode.parentNode.onclick = function(mozEvent) {return ourTableManager.tableRowHighlightToggle(mozEvent,this,true);};
			if (isPropertyViewTable) {
				
				if (!document.all) {	
					// an elegant way to disable ctrl-click on these elements for Mozilla browsers. 
					// IE unfortunately has to use less subtle methods, disabling all ctrl-selection by setting document.onselectstart to return false if CTRL key is pressed. This occurs in global.js if the TableManager is loaded. 
					inputNodeCollection[i].parentNode.parentNode.onmousedown = function(mozEvent) {return false;};
				}
			}
		}
	}
	// this is a failsafe to resize the scrollingTables if they blew out their containers during all the column resizing
	this.windowResize();
}

/*  SET OF FUNCTIONS PERTAINING TO SELECTION */

// triggered by onclick on the master checkbox itself.  
TableManager.prototype.tableCheckboxToggleAll = function(mozEvent,masterCheckbox,uniqueTableId) {
	var tableNode = document.getElementById(uniqueTableId);
	
	
	inputNodeCollection = tableNode.getElementsByTagName("input");
	
	for (var i=0; i<inputNodeCollection.length; i++ ) {
		// only want checkboxes, not textfields and radios
		if (inputNodeCollection[i].getAttribute('type') == "checkbox")  {
			// careful not to toggle the masterCheckbox itself. 
			if (masterCheckbox != inputNodeCollection[i]) {
				// the checkbox is a rowSelector and not just some editable property checkbox.
				if (inputNodeCollection[i].getAttribute("rowselector")=="yes") {
					if (masterCheckbox.checked != inputNodeCollection[i].checked) {
						this.tableRowHighlightToggle(mozEvent,inputNodeCollection[i].parentNode.parentNode, false);
					}
				}
			}
		}
	}
}
// called by onclick on the table row.  This function will only be attached as an onclick handler to a row if either it contains a rowselector form element, or if the parent table is of class "propertyViewTable" 
TableManager.prototype.tableRowHighlightToggle = function(mozEvent,rowElement,checkOrigination) {
	var eventSource = getEventOriginator(mozEvent);

	// a list of various elements within tree rows upon which clicks should not toggle highlight state. 
	if ((eventSource.tagName=="IMG") ||
		(eventSource.tagName=="A") || ((eventSource.tagName=="DIV") && (eventSource.className=="treeControl"))) {
		return true;		
	}

	// the table node is used by the radio elements, which need to circle back onclick and change highlighting on other rows.
	// the other place it is used is in the Property View table, where presence of that "propertyViewTable" classname on the table must change the clicking interaction. 
	var tableNode = rowElement.parentNode;
	while (tableNode.tagName!= "TABLE") { tableNode = tableNode.parentNode }
	
	var isPropertyViewTable = (tableNode.className.indexOf("propertyViewTable")!=-1);


	// we will deal with state changes to the rowselector form elements.  (not highlighting, which is dealt with later in this function )
	inputNodeCollection = rowElement.getElementsByTagName("input");
	
	// obtaining a reference to the rowSelector element within the row.
	var ourRowSelectorFormElement;
	for (var i=0; i<inputNodeCollection.length; i++ ) {
		if (inputNodeCollection[i].getAttribute("rowselector")=="yes") {
			ourRowSelectorFormElement = inputNodeCollection[i];
		}
	}
	// only deal with the form element change if the click is not coming from the formElement itself. (in which case it will have taken care of its own state)
	if ((eventSource.tagName!="INPUT") || ( ( eventSource.tagName=="INPUT") && (eventSource.getAttribute("tableid")))) {
		// deals with cases where the click came from the row
		
		if (ourRowSelectorFormElement.getAttribute("type") == "checkbox") {
			// manually flip the checkbox. 
			ourRowSelectorFormElement.checked = !ourRowSelectorFormElement.checked;
		}
		if (ourRowSelectorFormElement.getAttribute("type") == "radio") {
			// manually flip the radio. 
			ourRowSelectorFormElement.checked = !ourRowSelectorFormElement.checked;
		}
		else if (ourRowSelectorFormElement.getAttribute("type") == "hidden") {
			if (ourRowSelectorFormElement.value == "1") ourRowSelectorFormElement.value=0;
			else ourRowSelectorFormElement.value = "1";
		}
	}



	// Dealing with the highlighting changes. 
	// class "propertyViewTable" tables have a significantly different interaction. 
	if (isPropertyViewTable) {
		var ctrlKey = (document.all) ? window.event.ctrlKey :mozEvent.ctrlKey;
		if (!ctrlKey) {
			var inputNodeCollection = tableNode.getElementsByTagName("INPUT");

			// go through all the inputs
			for (var j=0;j<inputNodeCollection.length;j++) {

				// only interested in rowselector, type="hidden" inputs. 
				if ((inputNodeCollection[j].getAttribute("type")=="hidden") && (inputNodeCollection[j].getAttribute("rowselector")=="yes")) {

					var tableRowNode = inputNodeCollection[j].parentNode;
					while (tableRowNode.tagName!= "TR") { tableRowNode = tableRowNode.parentNode; }
					
					// since CTRL is not pressed, we remove highlighting from all rows except the one clicked on.
					if (inputNodeCollection[j]!=ourRowSelectorFormElement)  {
						removeClassName(tableRowNode,"rowHighlight");
						inputNodeCollection[j].value = "0";
					}
					else {
						appendClassName(tableRowNode, "rowHighlight");	
					}
				}
			}
		}
		// Ctrl Key is pressed, which now allows multiple selection. In this case we only change highlighting for the row the user has clicked on.
		else {
			if (rowElement.className.indexOf("rowHighlight")!=-1) {
				removeClassName(rowElement,"rowHighlight");
				rowElement.value = "0";
			}
			else {
				appendClassName(rowElement,"rowHighlight");
				rowElement.value = "1";
			}
		}
	}
	
	// dealing with highlighting changes on radio or checkbox type Selectable Tables.  
	
	
	else {
		// if the rowselector is checked, highlight the row 
		if (ourRowSelectorFormElement.checked) {
			// dealing with checkboxes and hidden form fields. 
			if ((ourRowSelectorFormElement.getAttribute("type") == "checkbox") || (ourRowSelectorFormElement.getAttribute("type") == "hidden"))  {	
				appendClassName(rowElement,"rowHighlight");	
			}
			// dealing with radio buttons
			else {
				var inputNodeCollection = tableNode.getElementsByTagName("INPUT");
				for (var j=0;j<inputNodeCollection.length;j++) {

					if (inputNodeCollection[j].getAttribute("type")=="radio") {
						var tableRowNode = inputNodeCollection[j].parentNode.parentNode;

						if (!inputNodeCollection[j].checked)  {
							
							removeClassName(tableRowNode,"rowHighlight");	
						}
						else {
							appendClassName(tableRowNode,"rowHighlight");	
						}
					}
				}
			}
		}
		else {
			removeClassName(rowElement,"rowHighlight");
		}
	}
}

/*  SET OF FUNCTIONS FOR SCROLLING TABLES */

// Although in most cases of window resizing it's not necessary to recalculate column positions, if a table is partially scrolled horizontally, resizing the window can effectively scroll the div, and unfortunately in IE6 this does not trigger onscroll, so this amounts to a patch.
TableManager.prototype.windowResize = function() {
	// cut down the size of the scrolling divs, so that their containers have an opportunity to contract.
	// this is done through classnames liquidHeight and liquidWidth, on the div around a ScrollingTable. 
	// two passes are made through.   On the first pass, heights and widths are temporarily set to lower values. 
	// this gives containers on the page the opportunity to contract. 
	for (var i=0; i<this.scrollableTableIds.length; i++) {
		var visualHeaderScrollingDiv	= document.getElementById(this.scrollableTableIds[i]+"_headerDiv");
		var tableScrollingDiv		= document.getElementById(this.scrollableTableIds[i]+"_scrollingTableDiv");
		
		if (tableScrollingDiv.className.indexOf("liquidHeight")!=-1) {
			tableScrollingDiv.style.height = Math.max(20,this.getContainersAvailableHeight(tableScrollingDiv) - visualHeaderScrollingDiv.offsetHeight-100);
		}
		if (tableScrollingDiv.className.indexOf("liquidWidth")!=-1) {
			var availableWidth = this.getContainersAvailableWidth(tableScrollingDiv);
			visualHeaderScrollingDiv.style.width = parseInt(availableWidth/2);
			tableScrollingDiv.style.width = parseInt(availableWidth/2)
		}
	}
	// now since all scrolling tabledivs have been shrunk a bit, we are guaranteed that containers have an opportunity to shrink.  (the troublesome case requiring these two loops to be seperate, is the case where a container has more than one scrollingTable in them.)
	// proceeding with setting widths and heights to actually match containers
	for (var i=0; i<this.scrollableTableIds.length; i++) { 
		var visualHeaderScrollingDiv	= document.getElementById(this.scrollableTableIds[i]+"_headerDiv");
		var tableScrollingDiv		= document.getElementById(this.scrollableTableIds[i]+"_scrollingTableDiv");

		if (tableScrollingDiv.className.indexOf("liquidHeight")!=-1) {
			tableScrollingDiv.style.height = Math.max(20,this.getContainersAvailableHeight(tableScrollingDiv) - visualHeaderScrollingDiv.offsetHeight);
		}
		if (tableScrollingDiv.className.indexOf("liquidWidth")!=-1) {
			var availableWidth = this.getContainersAvailableWidth(tableScrollingDiv);
			visualHeaderScrollingDiv.style.width = availableWidth;
			tableScrollingDiv.style.width = availableWidth;
		}
		// you have to call this onresize, as there are a couple hard-to-find resize cases where it does effectively induce scrolling on the 
		// div, but not all browsers will fire onscroll.	Fortunately it's not a very expensive operation. 
		this.slideColumns(this.scrollableTableIds[i]);
	}
}
// checking tableScrollingDiv.parent's offsetWidth returns a number that doesnt account for the parent's border and padding.  This function attempts to determine that.
TableManager.prototype.getContainersAvailableWidth = function(element) {
	var parent = element.offsetParent;
	var paddingAndBorder =0; 
	if (document.all)  {
		if (parseInt(parent.currentStyle.paddingLeft)) {
			paddingAndBorder += parseInt(parent.currentStyle.paddingLeft);
		}
		if (parseInt(parent.currentStyle.paddingRight)) {
			paddingAndBorder += parseInt(parent.currentStyle.paddingRight);
		}
		if (parseInt(parent.currentStyle.borderLeftWidth)) {
			paddingAndBorder += parseInt(parent.currentStyle.borderLeftWidth);
		}
		if (parseInt(parent.currentStyle.borderRightWidth)) {
			paddingAndBorder += parseInt(parent.currentStyle.borderRightWidth);
		}
	}
	else {
		paddingAndBorder =	
			parseInt(document.defaultView.getComputedStyle(parent	, '').getPropertyValue("padding-left")) +
			parseInt(document.defaultView.getComputedStyle(parent	, '').getPropertyValue("padding-right")) +
			parseInt(document.defaultView.getComputedStyle(parent	, '').getPropertyValue("border-left-width")) +
			parseInt(document.defaultView.getComputedStyle(parent	, '').getPropertyValue("border-right-width"));
	}
	if (isNaN(paddingAndBorder)) paddingAndBorder = 0;
	return Math.max(0,parent.offsetWidth - paddingAndBorder);
}
// checking tableScrollingDiv.parent's offsetHeight returns a number that doesnt account for the parent'sborder and padding.  This function attempts to determine that.
TableManager.prototype.getContainersAvailableHeight = function(element) {
	var parent = element.offsetParent;
	var paddingAndBorder = 0; 
	if (document.all)  {
		if (parseInt(parent.currentStyle.paddingTop)) {
			paddingAndBorder += parseInt(parent.currentStyle.paddingTop);
		}
		if (parseInt(parent.currentStyle.paddingBottom)) {
			paddingAndBorder += parseInt(parent.currentStyle.paddingBottom);
		}
		if (parseInt(parent.currentStyle.borderTopWidth)) {
			paddingAndBorder += parseInt(parent.currentStyle.borderTopWidth);
		}
		if (parseInt(parent.currentStyle.borderBottomWidth)) {
			paddingAndBorder += parseInt(parent.currentStyle.borderBottomWidth);
		}
	}
	else {
		paddingAndBorder =	
			parseInt(document.defaultView.getComputedStyle(parent	, '').getPropertyValue("padding-left")) +
			parseInt(document.defaultView.getComputedStyle(parent	, '').getPropertyValue("padding-right")) +
			parseInt(document.defaultView.getComputedStyle(parent	, '').getPropertyValue("border-left-width")) +
			parseInt(document.defaultView.getComputedStyle(parent	, '').getPropertyValue("border-right-width"));
	}
	if (isNaN(paddingAndBorder)) paddingAndBorder = 0;
	return Math.max(1,parent.offsetHeight - paddingAndBorder);
}

// this is only called once from init, and doesnt need to run again.  
// Note however, it runs once per ScrollingTable
TableManager.prototype.resizeTableColumns = function(tableId) {

	var tableDummyRow					= document.getElementById(tableId+"_dummyRow");
	var visualHeaderDivParent	= document.getElementById(tableId+"_headerDiv");
	var visualHeaderDiv				= visualHeaderDivParent.childNodes[0];
	var table								= document.getElementById(tableId);
	var tableScrollingDiv		= document.getElementById(tableId+"_scrollingTableDiv");
	
	// this will basically take the table's and headerDiv's 100% width styles, at whatever integer values they got at page load, and cast them as pixels.
	// note that the table value in particular will change later, as any long labels in visualHeaderDivs get factored into the column layout.
	table.style.width = table.offsetWidth;
	// and we need to give the visualHeadeDiv's parent a fixed width too, because without that, it's overflow properties wont work, (thanks w3c). 
	// Thus, if we waited until the end to do this, the little width assignments to it's visualHeader children would cause it to blow out its container.
	visualHeaderDivParent.style.width = visualHeaderDivParent.offsetWidth;

	//	you need to set this to a high number, or else the visualHeaderDiv will start to wrap.
	visualHeaderDiv.style.width = 2*table.offsetWidth +"px";
	var maxScreenDivHeight = 0;
	for (var i=0; i<visualHeaderDiv.childNodes.length; i++) {
		if (visualHeaderDiv.childNodes[i].nodeType==1)  {       
			maxScreenDivHeight = Math.max(maxScreenDivHeight,visualHeaderDiv.childNodes[i].offsetHeight);
		}
	}
	visualHeaderDiv.style.height = maxScreenDivHeight+"px";
	// need two extra pixels on the first one to get even.  One just cause the header border design has them one over,  and the other because the first headerDiv has no border, so needs one px extra width to line up.
	var extraWidthForFirstHeader = 2;
	// for the moment however, only IE really needs this.
	if (!document.all) extraWidthForFirstHeader = 0;
	// the purpose of this loop is to normalize the widths of table columns with their respective header divs. 
	for (var i=0; i<tableDummyRow.childNodes.length; i++) {
		var tableColumnWidth      = tableDummyRow.childNodes[i].offsetWidth;
		var visualHeaderDivWidth  = visualHeaderDiv.childNodes[i].offsetWidth;

		// skip over text/whitespace nodes
		if (tableDummyRow.childNodes[i].nodeType==1)  { 
			// the case where the visual Header is wider than the table cells
			
			if (visualHeaderDivWidth > tableColumnWidth) {
				
				// we need to bump up the whole table's width property each time a column is changed. This will always increase the size of the table, never decrease it. background: if this is not done until the end, then some of the columns along the way would have refused to accept the larger widths, and those assignments would actually fail. Although IE might do ok if this is omitted, mozilla does need it.
				var widthIncrement  = visualHeaderDivWidth - tableColumnWidth;
				table.style.width   = table.offsetWidth + widthIncrement + "px";
				
				// set the width of the div's within the column header cells.  background: if you try the straightforward approach of setting the width of the cells, the tableElement's constant desire to balance things against cell contents will occasionally rebuff you. This way, since you're making assignments to cell contents, the table is forced to listen. 
				tableDummyRow.childNodes[i].childNodes[0].style.width =   (visualHeaderDivWidth - 10)+"px";

				// However, a belt-and-suspenders approach is necessary, to prevent some strange behaviour (triggered sometimes by inputs or images), where the dummy element's expansion actually causes the table column to expand by that and THEN SOME.  I dont see how it could be useful, and i think a genuine bug, however this assignment to the TD seems to correct for it. 
				tableDummyRow.childNodes[i].style.width =   (visualHeaderDivWidth - 10) +"px";
			}
			// the other, much easier case, where the table columns are larger than the header divs.  
			// easier cause we just make the header div wider.
			else {	
				// everyone except IE gets a pixel removed, to account for the border. 
				visualHeaderDiv.childNodes[i].style.width = tableDummyRow.childNodes[i].offsetWidth + extraWidthForFirstHeader - ((document.all)?0:1)+ "px";
			}
			extraWidthForFirstHeader = 0;
			// this is just to make sure the header div's all have borders that extend all the way down. 
			visualHeaderDiv.childNodes[i].style.height = maxScreenDivHeight +"px";
//			alert(maxScreenDivHeight +" ," + visualHeaderDiv.childNodes[i].offsetHeight)
		}
	}
}


// this function is triggered by onscroll on the scrolling div.  the onscroll event handler is attached during init
// what will happen is this:   visualHeaderDiv is a very long div, cropped within the shorter visualDivParent, courtesy of its overflow property. 
// this function will look at the scrolling of the tableScrollingDiv, and use that value to slide visualHeaderDiv within it's parent, sort of like a big sliderule. 
TableManager.prototype.slideColumns = function(tableId) {
	var visualHeaderDivParent	= document.getElementById(tableId+"_headerDiv");
	var slidingColumnHeaders = visualHeaderDivParent.childNodes[0];
	var scrollingDiv	= document.getElementById(tableId+"_scrollingTableDiv");
	slidingColumnHeaders.style.left = -scrollingDiv.scrollLeft;
	var tableScrollingDiv		= document.getElementById(tableId+"_scrollingTableDiv");
	visualHeaderDivParent.style.width = tableScrollingDiv.offsetWidth;
}


function tableManager_windowResize() {
	ourTableManager.windowResize();
}
