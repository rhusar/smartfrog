<%@ include file="/html/mombasa-portlet/init.jsp" %>

<p>
The host has been added, though it may take time for it to be live.
</p>

<p>
Host Count: <bean:write name="hostcount"/>
</p>
<table border="2">
  <tr bgcolor="grey">
    <th>Hostname</th>
  </tr>
  <logic:iterate id="host"
                 name="hosts"
                 property="list"
                 type="com.hp.hpl.thor.services.mombasa.model.HostInstance">
    <tr>
      <td>
        <bean:write name="host" property="hostname"/>
      </td>
    </tr>
  </logic:iterate>
</table>





