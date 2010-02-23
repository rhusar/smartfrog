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


<%--
This is driven by the "host" value, which maps to a 
org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance instance
--%>

<h2><bean:write name="host" property="hostname"/></h2>

<table cellpadding="0" cellspacing="0">
 <tbody>
   <tr class="header">
    <th colspan="3" >
      Details 
    </th>
   </tr>
  <tr>
    <td>Hostname</td>
    <td class="padding"></td>
    <td><bean:write name="host" property="hostname"/></td>
  </tr>
  <tr>
    <td>External Hostname</td>
    <td class="padding"></td>
    <td><bean:write name="host" property="externalHostname"/></td>
  </tr>
    <tr>
      <td>Role</td>
      <td class="padding"></td>
      <td>
        <html:link action="/mombasa-portlet/cluster/listInRole"
                   paramId="role" paramName="host" paramProperty="role">
          <bean:write name="host" property="role"/>
        </html:link>
      
      </td>
    </tr>
   <logic:notEmpty name="host" property="application">
     <tr>
       <td>Application</td>
       <td class="padding"></td>
       <td><bean:write name="host" property="application"/></td>
     </tr>
   </logic:notEmpty>
   <tr>
     <td>State</td>
     <td><bean:write name="host" property="state"/></td>
   </tr>
   <logic:notEmpty name="host" property="application">
     <tr>
      <td>Description</td>
      <td>
          <logic:notEmpty name="host" property="application.description">
            <bean:write name="host" property="application.description"/>
          </logic:notEmpty>
      </td>
     </tr>
   </logic:notEmpty>
   <tr class="header">
    <th colspan="3" >
      Links 
    </th>
  </tr>

    <logic:iterate id="link"
         name="host"
         property="links"
         type="org.smartfrog.services.cloudfarmer.api.NodeLink">
      <tr>
        <td class="padding"></td>
        <td class="padding"></td>
        <td>
          <a href="<bean:write name="link" property="externalLink"/>" target="_blank">
              <bean:write name="link" property="name"/>
          </a>
        </td>
      </tr>
    </logic:iterate>
   <tr class="header">
     <th colspan="3" >
       Actions 
     </th>
   </tr>

<%--  <tr>
    <td colspan="2"  >
      <html:link paramId="hostid" paramName="host" paramProperty="id"
          action="/mombasa-portlet/cluster/host_ping">Ping the host
      </html:link>
    </td>
  </tr>--%>
  <tr>
    <td colspan="3" >
      <html:link paramId="hostid" paramName="host" paramProperty="id"
          action="/mombasa-portlet/cluster/delete">Delete the host
      </html:link>
    </td>
  </tr>
 </tbody>
</table>

