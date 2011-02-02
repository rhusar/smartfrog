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

function updateHostList() {
    if (status_xml) {
        status_xml.open("GET", "host_status_get.jsp?now=" + (new Date()).getTime(), true);
        status_xml.onreadystatechange = function ()
        {
            try {
                // If everything went alright
                if (status_xml.readyState == 4) {
                    if (status_xml.status == 200) {
                        var allSelectedHosts = getSelected();

                        var xmlDocument = status_xml.responseXML;
                        var hosts = xmlDocument.getElementsByTagName("host");

                        var list = document.getElementById("hostListBody");

                        // Delete all rows
                        for (var k = list.rows.length - 1; k > -1; k--) {
                            list.deleteRow(k);
                        }

                        // Add all hosts
                        if (hosts.length != 0) {
                            for (var i = 0; i < hosts.length; i++) {
                                    var row = list.insertRow(i);
                                    row.className = ((i % 2) == 0) ? "altRowColor" : null;
                                    if (usingIE) {
                                        row.onclick = "selectRow(this, true)";
                                    } else {
                                        row.setAttribute("onclick","selectRow(this, true)");
									}

                                    // Cell: Checkbox
                                    var checkBoxCell = row.insertCell(0);
                                    checkBoxCell.className = "checkboxCell";
                                    var checkBox = document.createElement("input");
                                    checkBox.type = "checkbox";
                                    checkBox.name = "selectedHost";
                                    if (usingIE) {
                                        checkBox.onclick = "selectRow(this.parentNode.parentNode, false)";
                                    } else {
                                        checkBox.setAttribute("onclick","selectRow(this.parentNode.parentNode, false)");
                                    }

                                    checkBox.value = hosts[i].getAttribute("name");
                                    checkBoxCell.appendChild(checkBox);

                                    // Cell: Hostname
                                    var hostNameCell = row.insertCell(1);
                                    hostNameCell.className = "sorted";
                                    //var consoleLink = document.createElement("a");
				    //consoleLink.href = "http://16.181.41.84:8081/AvalancheGUI/rest/"+hosts[i].getAttribute("name")+"/3800/?responseType=HTML";
				    //consoleLink.appendChild(document.createTextNode(hosts[i].getAttribute("name")));
				    hostNameCell.appendChild(document.createTextNode(hosts[i].getAttribute("name")));
				    //hostNameCell.appendChild(consoleLink);


                                    // Cell: Manage
                                    var managementCell = row.insertCell(2);
                                    var logLink = document.createElement("a");
                                    logLink.href = "log_view.jsp?pageAction=viewSelected&hostId=" + hosts[i].getAttribute("name");
                                    logLink.appendChild(document.createTextNode("[ Log ]"));
                                    var settingsLink = document.createElement("a");
                                    settingsLink.href = "host_setup_bs.jsp?hostId=" + hosts[i].getAttribute("name");
                                    settingsLink.appendChild(document.createTextNode("[ Settings ]"));
                                    if (hosts[i].childNodes[2].firstChild.data == "true") {
                                        var vmwareLink = document.createElement("a");
                                        vmwareLink.href = "vm_actions.jsp?action=list&host=" + hosts[i].getAttribute("name");
                                        vmwareLink.appendChild(document.createTextNode("[ Virtual Machines ]"));
                                        managementCell.appendChild(vmwareLink);
                                    }
                                    managementCell.appendChild(logLink);
                                    managementCell.appendChild(settingsLink);

                                    // Cell: Information
                                    var informationCell = row.insertCell(3);
                                    informationCell.appendChild(document.createTextNode(hosts[i].childNodes[0].firstChild.data + ", " + hosts[i].childNodes[1].firstChild.data));

                                    // Cell: Status
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
                                    if (hosts[i].childNodes[2].firstChild.data == "true") {
                                        statusColour.style.backgroundColor = "#00FF00";
                                        statusMessage.appendChild(document.createTextNode("Available"));
                                    } else {
                                        statusColour.style.backgroundColor = "#FF0000";
                                        statusMessage.appendChild(document.createTextNode("Not Available"));
                                    }
                                    statusCell.appendChild(statusColour);
                                    statusCell.appendChild(statusMessage);

                                    // Cell: Message
                                    var messageCell = row.insertCell(5);
                                    var lastmsg_response = "";
                                    if (hosts[i].childNodes[3].firstChild.data != "false") {
                                        lastmsg_response = document.createElement("a");
                                        lastmsg_response.href = "log_xmpp.jsp?host=" + hosts[i].getAttribute("name");
                                        lastmsg_response.appendChild(document.createTextNode(hosts[i].childNodes[3].firstChild.data));
                                    } else {
                                        lastmsg_response = document.createTextNode("No message has been received yet.");
                                    }
                                    messageCell.appendChild(lastmsg_response);
                            }
                            select(allSelectedHosts);
                        } else {
                            var row = list.insertRow(list.rows.length);
                            var cell = row.insertCell(0);
                            cell.colSpan = 6;
                            cell.appendChild(document.createTextNode("There are no hosts in the database. To add a host, please click \"Add a host\" in the upper right corner."));
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

/* Host actions */
var action_response = "";

var action_xml = false;
action_xml = getXMLHttpRequestObject();

function ajaxHostAction(target) {
    if (action_xml) {
        action_xml.open("GET", target + "&now=" + (new Date()).getTime(), true);
        action_xml.onreadystatechange = function()
        {
            try {
                // If everything went alright
                if (action_xml.readyState == 4) {
                    if (action_xml.status == 200) {
                        var xmlDocument = action_xml.responseXML;
                        if (xmlDocument.getElementsByTagName("type")[0].firstChild.data == "error")
                            alert(xmlDocument.getElementsByTagName("message")[0].firstChild.data);
                        // Update Host List again
                        updateHostList();
                    }
                }
            } catch (e) {
                // TODO: Maybe something other?
            }
        }
        action_xml.send(null);
    }
}

/* Actually the following code is not AJAX-related in anyway.
It is used by the host_list.jsp only and so it kind of fitted in here. */
function deselectAll() {
    var selectors = document.getElementsByName("selectedHost");
    document.getElementById("allhosts").checked = false;
    for (var i = 0; i < selectors.length; i++)
    {
        selectors[i].checked = false;
        selectors[i].parentNode.parentNode.className = ((i % 2) == 0) ? "altRowColor" : null;
    }
}

function select(oldSelectors) {
    if (oldSelectors) {
        var count = 0;
        newSelectors = document.getElementsByName("selectedHost");
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
        document.getElementById("allhosts").checked = (newSelectors.length == count) && document.getElementById("allhosts").checked;
    }
}

function getSelected() {
    var selectors = document.getElementsByName("selectedHost");
    var selectedHosts = new Array();

    for (var i = 0; i < selectors.length; i++)
    {
        if (selectors[i].checked) {
            selectedHosts.push(selectors[i].value);
        }
    }
    return selectedHosts;
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

function selectAllHosts(sender) {
    var check = sender.checked;
    var selectors = document.getElementsByName("selectedHost");
    for (var i = 0; i < selectors.length; i++)
    {
        if (check ^ selectors[i].checked)
            selectors[i].click();
    }
}

function perform(action, message) {
    var selectedHosts = getSelected();

    var count = selectedHosts.length;
    if (count == 0)
    {
        alert("You must select one or more hosts for this action.");
        return;
    }

    var alertMsg = "This action will " + message + " ";
    if (count == 1)
        alertMsg += "one host."
    else
        alertMsg += count + " hosts."

    alertMsg += " Are you sure you want to continue?";

    if (confirm(alertMsg)) {
        var target = "host_actions.jsp?pageAction=" + action;
        for (var i = 0; i < selectedHosts.length; i++) {
            target = target + "&selectedHost=" + selectedHosts[i];
        }
        ajaxHostAction(target);
        deselectAll();
    }
}

/*function openConsole() {
    perform("console", "open the console for")
}*/

function deleteHosts() {
    perform("delete", "permanently delete");
}

function stopHosts() {
    perform("stop", "stop");
}

function igniteHosts() {
    perform("ignite", "ignite");
}
