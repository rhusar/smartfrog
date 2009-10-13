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
                 type="com.hp.hpl.thor.services.mombasa.model.Workflow">
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



