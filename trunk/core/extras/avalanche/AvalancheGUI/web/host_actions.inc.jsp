<% /**
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
*/ %>
<%@ page language="java" %>

<script type="text/javascript" language="JavaScript">
    <!--
    function perform (target, message) {
        var selectors = document.getElementsByName("selectedHost");
        var selectedHosts = new Array();

        for (var i = 0; i < selectors.length; i++)
        {
            if (selectors[i].checked)
                selectedHosts.push(selectors[i]);
        }

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
            document.hostListFrm.action = target;
            document.hostListFrm.submit();
        }
    }

    function deleteHosts() {
        perform("host_delete.jsp", "permanently delete");
    }

    function stopHosts() {
        perform("ignite.jsp?pageAction=stop", "stop");
    }

    function igniteHosts() {
        perform("ignite.jsp?pageAction=ignite", "ignite");
    }

    -->
</script>

<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        <!--
        oneVoiceWritePageMenu(  "ListHosts", "footer", 
                                "Delete selected hosts",
                                "javascript:deleteHosts()",
                                "Stop SmartFrog on selected hosts",
                                "javascript:stopHosts()",
                                "Ignite selected hosts",
                                "javascript:igniteHosts()");
        -->
    </script>
</div>