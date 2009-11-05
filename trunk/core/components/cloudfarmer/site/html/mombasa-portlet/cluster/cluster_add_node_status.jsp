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

<div align="center">
<html:link
    action="/mombasa-portlet/cluster/add_dynamic/status">Update Status</html:link>
</div>


<p>Operation status</p>
<bean:write name="farmer.work.status"/>

<div align="center">
<html:link
    action="/mombasa-portlet/cluster/add_dynamic/status">Update Status</html:link>
</div>

<logic:notEmpty name="farmer.work.status.events">
  <table border="0">
    <tr bgcolor="grey">
      <th>Level</th>
      <th style="padding-left: 10px;"></th>
      <th>Message</th>
      <th style="padding-left: 10px;"></th>
    </tr>
    <logic:iterate id="event"
                   property="list"
                   name="farmer.work.status.events"
                   type="org.smartfrog.services.cloudfarmer.client.web.model.cluster.StatusEvent">
      <tr>
        <td>
          <bean:write name="event" property="level"/>
        </td>
        <td style="padding-left: 10px;"></td>
        <td>
          <bean:write name="event" property="message"/>
        </td>
      </tr>
    </logic:iterate>
  </table>
</logic:notEmpty>




