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
<%@ include file="/html/mombasa-portlet/cluster/init.jsp" %>

<tiles:useAttribute id="text" name="text" classname="java.lang.String" ignore="true"/>
<div>
<%= text %>
</div>
<div>
</div>
<table cellpadding="0" cellspacing="0">
 <tbody>
  <tr class="header" >
    <th>Hostname</th>
    <th class="padding"></th>
    <th>ID</th>
    <th class="padding"></th>
    <th>Role</th>
    <th class="padding"></th>
    <th>Application</th>
    <th class="padding"></th>
    <th>Description</th>
  </tr>
  <logic:iterate id="host"
                 name="hosts"
                 property="list"
                 type="org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance">
    <tr>
      <td>
        <html:link action="/mombasa-portlet/cluster/viewhost"
                   paramId="hostid" paramName="host" paramProperty="id">
          <bean:write name="host" property="hostname"/>
        </html:link>
      </td>
      <td class="padding"></td>
      <td>
        <html:link action="/mombasa-portlet/cluster/viewhost"
            paramId="hostid" paramName="host" paramProperty="id">
          <bean:write name="host" property="id"/>
        </html:link>
      </td>
      <td class="padding"></td>
      <td>
        <logic:notEmpty name="host" property="role">
          
         <html:link action="/mombasa-portlet/cluster/listInRole"
            paramId="role" paramName="host" paramProperty="role">
           <bean:write name="host" property="role"/>
         </html:link>
        </logic:notEmpty>
      </td>
      <td class="padding"></td>
      <td><bean:write name="host" property="application"/></td>
      <td class="padding"></td>
      <td>
        <logic:notEmpty name="host" property="application">
          <logic:notEmpty name="host" property="application.description">
            <bean:write name="host" property="application.description"/>
          </logic:notEmpty>
        </logic:notEmpty>
      </td>
    </tr>
  </logic:iterate>
 </tbody>
</table>
<div>

</div>
<table >
   <tbody>
  <tr >
    <td>Hosts</td>
    <td class="padding"></td>
    <td><bean:write name="hostcount"/></td>
  </tr>  
<%--  <tr >
    <td>Master</td>
    <td class="padding"></td>
    <td>    
        <logic:notEmpty name="cluster.master.hostname">
          <bean:write name="cluster.master.hostname"/>
        </logic:notEmpty>
    </td>
  </tr>  --%>
<%--
  <tr >
    <td>Controller</td>
    <td class="padding"></td>
    <td><bean:write name="cluster.controller" property="description"/></td>
  </tr>  
--%>
  </tbody>
</table>





