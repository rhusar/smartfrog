<%@ include file="/html/mombasa-portlet/init.jsp" %>

<p>Add a named machine to the Hadoop Cluster</p>

<html:form action="/mombasa-portlet/cluster/add_named/process" method="post" focus="name">


  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        Name
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:text name="clusterAddNamedForm" property="name" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        Hadoop worker
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <html:checkbox name="clusterAddNamedForm" property="worker" />
      </td>
    </tr>

    <tr>
      <td>
        Hadoop master
      </td>
      <td style="padding-left: 10px;"></td>
      <td>
        <logic:notEmpty name="cluster.master">
          <bean:write name="cluster.master.hostname"/>
        </logic:notEmpty>
        <logic:empty name="cluster.master">
          <html:checkbox name="clusterAddNamedForm" property="master"/>
        </logic:empty>
      </td>
    </tr>

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




