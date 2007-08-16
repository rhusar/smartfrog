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

/* Status update */
var status_response = "";
var lastmsg_response = "";

var status_xml = false;
status_xml = getXMLHttpRequestObject();

function getStatus() {
    if (status_xml) {
        status_xml.open("GET", "host_status_get.jsp?now=" + (new Date()).getTime(), true);
        status_xml.onreadystatechange = function ()
        {
            try {
                // If everything went alright
                if (status_xml.readyState == 4) {
                    if (status_xml.status == 200) {
                        var xmlDocument = status_xml.responseXML;
                        var statusMsgs = xmlDocument.getElementsByTagName("status");
                        var lastMsgs = xmlDocument.getElementsByTagName("lastmsg");
                        for (var i = 0; i < statusMsgs.length; i++) {
                            if (statusMsgs[i].firstChild.data == "true") {
                                status_response = "<div style=\"float:left;height:10px;width:10px;background-color:#00FF00\"></div><div style=\"float:left;\">&nbsp;Available</div>";
                            } else {
                                status_response = "<div style=\"float:left;height:10px;width:10px;background-color:#FF0000\"></div><div style=\"float:left;\">&nbsp;Not&nbsp;Available</div>";
                            }
                            if (lastMsgs[i].firstChild.data != "false") {
                                lastmsg_response = "<a href=\"log_xmpp.jsp?host=" + statusMsgs[i].parentNode.getAttribute("name") + "\">" +  lastMsgs[i].firstChild.data + "</a>";
                            } else {
                                lastmsg_response = "No message has been received yet.";
                            }
                            fillDivBox(statusMsgs[i].parentNode.getAttribute("name") + "_status", status_response);
                            fillDivBox(statusMsgs[i].parentNode.getAttribute("name") + "_msg", lastmsg_response);
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
                    }
                }
            } catch (e) {
                // TODO: Maybe something other?
                alert(e);
            }
        }
        action_xml.send(null);
    }
}

/* Actually the following code is not AJAX-related in anyway.
It is used by the host_list.jsp only and so it kind of fitted in here. */
function delectAll() {
    var selectors = document.getElementsByName("selectedHost");
    document.getElementById("allhosts").checked = false;
    for (var i = 0; i < selectors.length; i++)
    {
        selectors[i].checked = false;
        selectors[i].parentNode.parentNode.className = ((i % 2) == 0) ? "altRowColor" : null;
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
        if (action == "delete") {
            window.location = target;
        } else {
            ajaxHostAction(target);
            delectAll();
        }
    }
}

function openConsole() {
    perform("console", "open the console for")
}

function deleteHosts() {
    perform("delete", "permanently delete");
}

function stopHosts() {
    perform("stop", "stop");
}

function igniteHosts() {
    perform("ignite", "ignite");
}
