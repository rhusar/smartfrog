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

    var status_response = false;
    var status_xml = getXMLHttpRequestObject();

    function getStatus() {
        if (status_xml) {
            status_xml.open("GET", "host_status_get.jsp?now=" + (new Date).getMilliseconds());
            status_xml.onreadystatechange = function()
            {
                try {
                // If everything went alright
                if (status_xml.status == 200) {
                    if (status_xml.readyState == 4) {
                        var xmlDocument = status_xml.responseXML;
                        var statusMsgs = xmlDocument.getElementsByTagName("status");
                        for (var i = 0; i < statusMsgs.length; i++) {
                            if (statusMsgs[i].firstChild.data == "true") {
                                status_response = "<img src=\"images/status_online.gif\" alt=\"online\" style=\"height:10px;width:10px\" /> Available";
                            } else {
                                status_response = "<img src=\"images/status_offline.gif\" alt=\"offline\" style=\"height:10px;width:10px\" /> Not Available";
                            }
                            fillDivBox(statusMsgs[i].parentNode.getAttribute("name") + "_status", status_response);
                        }
                    } else {
                        // TODO: what to do, if still loading?
                    }
                } else {
                    // TODO: what to do, if file not found?
                }
                } catch (e) {
                    // TODO: everything went wrong
                }
            }
            sendRequest(status_xml);
        }
    }