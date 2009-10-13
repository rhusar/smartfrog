<%@ include file="/html/mombasa-portlet/init.jsp" %>

<p>Add Hadooop hosts </p>

<html:form action="/mombasa-portlet/cluster/add_dynamic/process" method="post" focus="name">


  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        Minimum number of workers
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="clusterAddDynamicForm" property="minWorkers" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        Maximum of workers
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="clusterAddDynamicForm" property="maxWorkers" size="23"/>
      </td>
    </tr>
    
 <%--   <tr>
      <td>
        Large size
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:checkbox name="clusterAddDynamicForm" property="large" />
      </td>
    </tr>--%>


    <tr>
      <td>
        <html:submit>
          Add
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




