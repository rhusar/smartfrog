<%@ include file="/html/mombasa-portlet/init.jsp" %>

<%--
This is driven by the "host" value, which maps to a host
--%>

<table border="2">
  <tr>
    <td>Hostname</td>
    <td><bean:write name="host" property="hostname"/></td>
  </tr>
  <tr>
    <td>Application</td>
    <td><bean:write name="host" property="application"/></td>
  </tr>
    <tr>
      <td>isMaster</td>
      <td><bean:write name="host" property="master"/></td>
    </tr>
    <tr>
      <td>isWorker</td>
      <td><bean:write name="host" property="worker"/></td>
    </tr>
  <tr>
    <td colspan="2">
      <b>Actions</b> 
    </td>
  </tr>
  <tr>
    <td colspan="2"  bgcolor="grey">
      <html:link paramId="hostid" paramName="host" paramProperty="id"
          page="/portlet_action/mombasa-portlet/cluster/host_ping">Ping the host
      </html:link>
    </td>
  </tr>
  <tr>
    <td colspan="2"  bgcolor="grey">
      <html:link paramId="hostid" paramName="host" paramProperty="id"
          page="/portlet_action/mombasa-portlet/cluster/host_terminate">Terminate the host
      </html:link>
    </td>
  </tr>
</table>

