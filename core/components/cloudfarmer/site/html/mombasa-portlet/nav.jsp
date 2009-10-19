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

<%@ include file="/html/mombasa-portlet/init.jsp" %>
<div class="separator"></div>

<table border="0" width="100%">
  <tr>
    <td>
      <html:link action="/mombasa-portlet/workflowList">List Workflows</html:link>
    </td>
    <td>
      <html:link
          action="/mombasa-portlet/submitMRJob/view">Queue a MapReduce Job</html:link>
    </td>
    <td>
      <html:link
          action="/mombasa-portlet/submitTool/view">Queue a Tool Job</html:link>
    </td>
    <td>
      <html:link
          action="/mombasa-portlet/queueWorkflow/view">Queue a Workflow</html:link>
    </td>
    <td>
      <html:link
          action="/mombasa-portlet/workflowAdmin/view">Administration</html:link>
    </td>
  </tr>
</table>


