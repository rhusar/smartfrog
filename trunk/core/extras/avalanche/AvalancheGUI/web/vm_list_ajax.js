/**
 (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 For more information: www.smartfrog.org
 */

/* Tell browsers apart */
var usingIE = document.all;

if (usingIE) {
    document.getElementsByName = function(name) {
        var allElements = document.getElementsByTagName('*');
        var wantedElements = []
        for (var i = 0; i < allElements.length; i++) {
            var att = allElements[i].getAttribute('name');
            if (att == name) {
                wantedElements.push(allElements[i]);
            }
        }
        return wantedElements;
    }
}

/* Status update */
var status_xml = false;
status_xml = getXMLHttpRequestObject();

function updateVmList(inHost) {
    if (status_xml) {
        status_xml.open("GET", "vm_status_get.jsp?host=" + inHost + "&now=" + (new Date()).getTime(), true);
        status_xml.onreadystatechange = function ()
        {
            try {
                // If everything went alright
                if (status_xml.readyState == 4) {
                    if (status_xml.status == 200) {
                        var allSelectedVMs = getSelected();

                        var xmlDocument = status_xml.responseXML;
                        var vms = xmlDocument.getElementsByTagName("vm");

                        var list = document.getElementById("vmListBody");

                        // Delete all rows
                        for (var k = list.rows.length - 1; k > -1; k--) {
                            list.deleteRow(k);
                        }

                        // Add all virtual machines
                        if (vms.length != 0) {
                            for (var i = 0; i < vms.length; i++) {
                                var row = list.insertRow(i);
                                row.className = ((i % 2) == 0) ? "altRowColor" : null;
                                if (usingIE) {
                                    row.onclick = "selectRow(this, true)";
                                } else {
                                    row.setAttribute("onclick", "selectRow(this, true)");
                                }

                                // Cell: Checkbox
                                var checkBoxCell = row.insertCell(0);
                                checkBoxCell.className = "checkboxCell";
                                var checkBox = document.createElement("input");
                                checkBox.type = "checkbox";
                                checkBox.name = "selectedVM";
                                if (usingIE) {
                                    checkBox.onclick = "selectRow(this.parentNode.parentNode, false)";
                                } else {
                                    checkBox.setAttribute("onclick", "selectRow(this.parentNode.parentNode, false)");
                                }
                                checkBox.value = vms[i].childNodes[0].firstChild.data;
                                checkBoxCell.appendChild(checkBox);

                                // Cell: vmname
                                var hostNameCell = row.insertCell(1);
                                hostNameCell.className = "sorted";
                                hostNameCell.appendChild(document.createTextNode(vms[i].childNodes[0].firstChild.data));

                                // Cell: Manage
                                var managementCell = row.insertCell(2);
                                var setupLink = document.createElement("a");
                                setupLink.href = "vm_setup.jsp?&host=" + inHost + "&vm=" + vms[i].childNodes[0].firstChild.data;
                                setupLink.appendChild(document.createTextNode("[ Setup ]"));
                                managementCell.appendChild(setupLink);

                                // Cell: Last Command
                                var lastCommandCell = row.insertCell(3);
                                lastCommandCell.appendChild(document.createTextNode(vms[i].childNodes[1].firstChild.data));

                                // Cell: Response
                                var statusCell = row.insertCell(4);
                                var statusColour = document.createElement("div");
                                if (usingIE) {
                                    statusColour.style.styleFloat = "left";
                                } else {
                                    statusColour.setAttribute("style", "float:left;");
                                }
                                statusColour.style.display = "block";
                                statusColour.style.height = "10px";
                                statusColour.style.width = "10px";
                                var statusMessage = document.createElement("div");
                                statusMessage.setAttribute("style", "float:left;");
                                statusMessage.style.styleFloat = "left";
                                statusMessage.style.display = "block";
                                if (vms[i].childNodes[2].firstChild.data == "success") {
                                    statusColour.style.backgroundColor = "#00FF00";
                                } else {
                                    statusColour.style.backgroundColor = "#FF0000";
                                }
                                statusMessage.appendChild(document.createTextNode(vms[i].childNodes[2].firstChild.data));
                                statusCell.appendChild(statusColour);
                                statusCell.appendChild(statusMessage);
                            }
                            select(allSelectedVMs);
                        } else {
                            var row = list.insertRow(list.rows.length);
                            var cell = row.insertCell(0);
                            cell.colSpan = 6;
                            cell.appendChild(document.createTextNode("There are no virtual machines for this host in the database. Maybe avalanche isn't running on " + inHost + "?"));
                        }
                    }
                }
            } catch (e) {
                // TODO: everything went wrong
            }
        }
        status_xml.send(null);
    }
}

function select(oldSelectors) {
    if (oldSelectors) {
        var count = 0;
        var newSelectors = document.getElementsByName("selectedVM");
        for (var i = 0; i < newSelectors.length; i++)
        {
            for (var j = 0; j < oldSelectors.length; j++) {
                if (newSelectors[i].value == oldSelectors[j]) {
                    newSelectors[i].parentNode.parentNode.className = newSelectors[i].parentNode.parentNode.className + " rowHighlight";
                    newSelectors[i].checked = true;
                    count++;
                }
            }
        }
        document.getElementById("allvms").checked = (newSelectors.length == count) && document.getElementById("allvms").checked;
    }
}

function getSelected() {
    var selectors = document.getElementsByName("selectedVM");
    var selectedVMs = new Array();

    for (var i = 0; i < selectors.length; i++)
    {
        if (selectors[i].checked) {
            selectedVMs.push(selectors[i].value);
        }
    }
    return selectedVMs;
}

function selectRow(sender, isRow) {
    var checkbox = sender.firstChild.firstChild;
    var test = !checkbox.checked;
    if (isRow) {
        if (test) {
            sender.className = sender.className + " rowHighlight";
            checkbox.checked = true;
        } else {
            sender.className = sender.className.substr(0, sender.className.lastIndexOf(" "));
            checkbox.checked = false;
        }
    } else {
        checkbox.checked = test;
    }
}

function selectAllVMs(sender) {
    var check = sender.checked;
    var selectors = document.getElementsByName("selectedVM");
    for (var i = 0; i < selectors.length; i++)
    {
        if (check ^ selectors[i].checked)
            selectors[i].click();
    }
}

function perform(action) {
    var selectedVMs = getSelected();

    if (selectedVMs.length == 0)
    {
        alert("You must select one or more virtual machines for this action.");
        return;
    }

    var target = "vm_actions.jsp?redirect=no&action=" + action;
    for (var i = 0; i < selectedVMs.length; i++) {
        target = target + "&selectedVM=" + selectedVMs[i];
    }

    var action_xml = getXMLHttpRequestObject();
    action_xml.open("GET", target + "&now=" + (new Date()).getTime(), true);
    action_xml.send(null);
}