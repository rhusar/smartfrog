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

/* XMPP Message update */
var msg_xml = false;
msg_xml = getXMLHttpRequestObject();

function updateMsgList(host) {
    if (msg_xml) {
        var requestURL = "log_xmpp_get.jsp?"
        if (host != null) {
            requestURL += "host=" + host + "&";
        }
        requestURL += "now=" + (new Date()).getTime();
        msg_xml.open("GET", requestURL, true);

        msg_xml.onreadystatechange = function ()
        {
            try {
                // If everything went alright
                if (msg_xml.readyState == 4) {
                    if (msg_xml.status == 200) {

                        var xmlDocument = msg_xml.responseXML;
                        var isOneHost = (xmlDocument.getElementsByTagName("hosts").length == 0);
                        var hosts = xmlDocument.getElementsByTagName("host");

                        var list = document.getElementById("hostMsgListBody");

                        // Delete all rows
                        for (var k = list.rows.length - 1; k > -1; k--) {
                            list.deleteRow(k);
                        }

                        // Only 1 Host
                        if (isOneHost) {
                            if (hosts.length != 0) {
                                for (var i = 0; i < hosts[0].childNodes.length; i++) {
                                    var row = list.insertRow(i);
                                    row.className = ((i % 2) == 0) ? "altRowColor" : null;

                                    var timeCell = row.insertCell(0);
                                    timeCell.className = "sorted";
                                    timeCell.appendChild(document.createTextNode(hosts[0].childNodes[i].getAttribute("time")));

                                    var textCell = row.insertCell(1);
                                    textCell.appendChild(document.createTextNode(hosts[0].childNodes[i].getAttribute("text")));
                                }
                            } else {
                                var row = list.insertRow(list.rows.length);
                                var cell = row.insertCell(0);
                                cell.colSpan = 2;
                                cell.appendChild(document.createTextNode("It seems that the host is not valid."));
                            }
                        } else {
                            if (hosts.length != 0) {
                                for (var i = 0; i < hosts.length; i++) {
                                    var row = list.insertRow(i);
                                    row.className = ((i % 2) == 0) ? "altRowColor" : null;

                                    var hostCell = row.insertCell(0);
                                    hostCell.className = "sorted";
                                    var link = document.createElement("a");
                                    link.href = "log_xmpp.jsp?host=" + hosts[i].getAttribute("id");
                                    link.appendChild(document.createTextNode(hosts[i].getAttribute("id")));
                                    hostCell.appendChild(link);

                                    var countCell = row.insertCell(1);
                                    countCell.appendChild(document.createTextNode(hosts[i].getAttribute("msgcount") + " Message" + ((hosts[i].getAttribute("msgcount")!="1")?"s":"")));

                                    var textCell = row.insertCell(2);
                                    textCell.appendChild(document.createTextNode(hosts[i].firstChild.getAttribute("text")));

                                    var timeCell = row.insertCell(3);
                                    timeCell.appendChild(document.createTextNode(hosts[i].firstChild.getAttribute("time")));
                                }
                            } else {
                                var row = list.insertRow(list.rows.length);
                                var cell = row.insertCell(0);
                                cell.colSpan = 4;
                                cell.appendChild(document.createTextNode("There are no hosts in the database."));
                            }
                        }
                    }
                }
            } catch (e) {
                // TODO: everything went wrong
            }
        }
        msg_xml.send(null);
    }
}