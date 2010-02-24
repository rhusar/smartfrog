<%--
/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
--%>

<%@ include file="/html/mombasa-portlet/cluster/init.jsp" %>

<table class="wide" cellpadding="0" cellspacing="0">
  <tr class="header">
    <th>Action</th>
    <th class="padding"></th>
    <th>Description</th>
  </tr>
  <tbody>
    <tr>
      <td class="action">
        <html:link
            action="/mombasa-portlet/cluster/diagnostics">Cluster Diagnostics</html:link>
      </td>
      <td class="padding"></td>
      <td>
        This displays cloudfarmer-specific diagnostic text 
      </td>
    </tr>
  <tr>
    <td class="action">
      <html:link
          action="/mombasa-portlet/cluster/terminate">Shut down the cluster</html:link>
    </td>
    <td class="padding"></td>
    <td>
      This will shut down all the Hadoop processes then delete any VMs, totally destroying
      the cluster.
    </td>
  </tr>
  <tr>
    <td class="action">
      <html:link
          action="/mombasa-portlet/cluster/change_manager">Change Manager</html:link>
    </td>
    <td class="padding"></td>
    <td>
      This can switch the web UI to a different cloudfarmer instance
     </td>
   </tr>
  </tbody>
</table>


