<%@ include file="/html/mombasa-portlet/init.jsp" %>

<tiles:useAttribute id="text" name="text" classname="java.lang.String" ignore="true"/>
<div>
<%= text %>
</div>
<div>
</div>
<table border="2">
  <tr bgcolor="grey">
    <th>Hostname</th>
    <th style="padding-left: 10px;"></th>
    <th>Application</th>
  </tr>
  <logic:iterate id="host"
                 name="hosts"
                 property="list"
                 type="com.hp.hpl.thor.services.mombasa.model.HostInstance">
    <tr>
      <td>
        <html:link page="/portlet_action/mombasa-portlet/cluster/viewhost"
            paramId="hostid" paramName="host" paramProperty="id">
          <bean:write name="host" property="hostname"/>
        </html:link>
      </td>
      <td style="padding-left: 10px;"></td>
      <td><bean:write name="host" property="application"/></td>
      <td style="padding-left: 10px;"></td>
      <td>
        <logic:notEmpty name="host" property="application">
          <logic:notEmpty name="host" property="application.description">
            <bean:write name="host" property="application.description"/>
          </logic:notEmpty>
        </logic:notEmpty>
      </td>
    </tr>
  </logic:iterate>
</table>
<div>

</div>
<table border="0">
  <tr >
    <td>Hosts</td>
    <td style="padding-left: 10px;"></td>
    <td><bean:write name="hostcount"/></td>
  </tr>  
  <tr >
    <td>Master</td>
    <td style="padding-left: 10px;"></td>
    <td><bean:write name="cluster.master.hostname"/></td>
  </tr>  
  <tr >
    <td>Controller</td>
    <td style="padding-left: 10px;"></td>
    <td><bean:write name="cluster.controller" property="description"/></td>
  </tr>  
  
  
  
</table>





