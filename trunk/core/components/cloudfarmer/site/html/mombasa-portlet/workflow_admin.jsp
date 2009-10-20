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
          page="/html/mombasa-portlet/happy.jsp">Web application Happiness Test</html:link>
    </td>
  </tr>
</table>

<jsp:include page="/html/mombasa-portlet/footer.jsp"/>


