<%@ include file="/html/mombasa-portlet/init.jsp" %>
<div class="separator"></div>

<table border="0" width="100%">
  <tr>
    <td>
      <html:link
          page="/portlet_action/mombasa-portlet/workflowList">List Workflows</html:link>
    </td>
    <td>
      <html:link
          page="/portlet_action/mombasa-portlet/submitMRJob/view">Queue a MapReduce Job</html:link>
    </td>
    <td>
      <html:link
          page="/portlet_action/mombasa-portlet/submitTool/view">Queue a Tool Job</html:link>
    </td>
    <td>
      <html:link
          page="/portlet_action/mombasa-portlet/queueWorkflow/view">Queue a Workflow</html:link>
    </td>
    <td>
      <html:link
          page="/portlet_action/mombasa-portlet/workflowAdmin/view">Administration</html:link>
    </td>
  </tr>
</table>


