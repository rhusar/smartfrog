<%@ include file="/html/mombasa-portlet/init.jsp" %>
<jsp:include page="/html/mombasa-portlet/header.jsp"/>
<h2>Queue a Hadoop Tool Job</h2>


<logic:messagesPresent>
  <span class="portlet-msg-error">
  <html:errors/>
  </span>
</logic:messagesPresent>

<html:form action="/mombasa-portlet/submitTool/process" method="post" focus="name">

  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        Name
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="submitToolForm" property="name" size="23"/>
      </td>
    </tr>
    <tr>
      <td>
        Tool Classname
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="submitToolForm" property="tool" size="100"/>
      </td>
    </tr>
    <tr>
      <td>
        <html:submit>
          Queue Job
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


