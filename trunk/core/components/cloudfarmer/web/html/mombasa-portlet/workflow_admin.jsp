<%@ include file="/html/mombasa-portlet/init.jsp" %>
<jsp:include page="/html/mombasa-portlet/header.jsp"/>

<h2>
  Hadoop Workflow Administration
</h2>

<logic:messagesPresent>
  <span class="portlet-msg-error">
  <html:errors/>
  </span>
</logic:messagesPresent>

<html:form action="/mombasa-portlet/workflowAdmin/process"
           method="post"
           focus="hostname">

  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        Hostname
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="workflowAdminForm" property="hostname"/>
      </td>
    </tr>
    <tr>
      <td>
        Port
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="workflowAdminForm" property="port"/>
      </td>
    </tr>

  </table>

  <br/>

  <html:submit>Connect to Server</html:submit>

</html:form>

<table border="0" width="100%">
  <tr>
    <td>
      <html:link
          page="/html/mombasa-portlet/workflow_server_happy.jsp">Check workflow server</html:link>
    </td>
    <td>
      <html:link
          page="/html/mombasa-portlet/happy.jsp">Portlet Happiness Test</html:link>
    </td>
  </tr>
</table>

<jsp:include page="/html/mombasa-portlet/footer.jsp"/>


