<%@ include file="/html/mombasa-portlet/init.jsp" %>
<jsp:include page="/html/mombasa-portlet/header.jsp"/>
<h2>Queue a Hadoop Workflow</h2>


<logic:messagesPresent>
  <span class="portlet-msg-error">
  <html:errors/>
  </span>
</logic:messagesPresent>

<html:form action="/mombasa-portlet/queueWorkflow/process"
           method="post"
           enctype="multipart/form-data"
           focus="name">

  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        Name
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="submitWorkflowForm" property="name" size="23"/>
      </td>
    </tr>
    <tr>
      <td>
        File
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:file name="submitWorkflowForm" property="file"/>
      </td>
    </tr>
    <tr>
      <td>
        Code
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:textarea name="submitWorkflowForm" property="conf" rows="10" cols="100"/>
      </td>

    </tr>
    <tr>
      <td>
        <html:submit>
          Queue Workflow
        </html:submit>
      </td>
      <td style="padding-left: 10px;"></td>

      <td>
        <html:reset>
          Reset Form
        </html:reset>
      </td>
    </tr>
  </table>

</html:form>


<jsp:include page="/html/mombasa-portlet/footer.jsp"/>


