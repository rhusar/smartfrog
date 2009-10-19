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

<%
  /**
   List the workflows
   */
%>

<%@ include file="/html/mombasa-portlet/init.jsp" %>
<jsp:include page="/html/mombasa-portlet/header.jsp"/>
<div>
<%--<h2><bean:write name="remoteDaemon"/></h2>--%>
<h2>Workflow List</h2>
</div>


<table border="2">
  <tr bgcolor="grey">
    <th>Name</th>
    <th style="padding-left: 10px;"></th>
    <th>Class</th>
    <th style="padding-left: 10px;"></th>
    <th>Description</th>
  </tr>
  <logic:iterate id="workflow"
                 name="workflowList"
                 property="list"
                 type="org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow">
    <tr>
      <td><bean:write name="workflow" property="name"/></td>
      <td style="padding-left: 10px;"></td>
      <td><bean:write name="workflow" property="classname"/></td>
      <td style="padding-left: 10px;"></td>
      <td><bean:write name="workflow" property="description"/></td>
    </tr>
  </logic:iterate>
</table>


<jsp:include page="/html/mombasa-portlet/footer.jsp"/>



