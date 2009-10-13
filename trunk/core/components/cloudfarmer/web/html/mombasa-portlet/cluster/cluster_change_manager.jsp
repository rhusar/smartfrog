<%@ include file="/html/mombasa-portlet/init.jsp" %>

<p>Add a named machine to the Hadoop Cluster</p>

<html:form action="/mombasa-portlet/cluster/change_manager/process" method="post" focus="name">

  <table border="0" cellpadding="0" cellspacing="0">

    <tr>
      <td>
        Controller
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:select name="clusterChangeManagerForm" property="controller">
          <html:option value="0">Physical Host List</html:option>
          <html:option value="1">HP Cells</html:option>
          <html:option value="2">OpenCirus</html:option>
          <html:option value="3">Amazon EC2 API</html:option>
        </html:select>
      </td>
    </tr>

    <tr>
      <td>
        URL
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="clusterChangeManagerForm" property="url" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        Username
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="clusterChangeManagerForm" property="username" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        Password
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:password name="clusterChangeManagerForm" property="password" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        <html:submit>
          Change
        </html:submit>
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:reset>
          Reset
        </html:reset>
      </td>
    </tr>

  </table>


</html:form>




