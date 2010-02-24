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
<div align="center">
<html:link
    action="/mombasa-portlet/cluster/add_dynamic/status">Update Status</html:link>
</div>
--%>


<h3>Operation status: 
<bean:write name="farmer.work.status"/>
</h3>
<logic:notEmpty name="farmer.work.status.events">
  <table cellpadding="0" cellspacing="0">
   <tbody>
    <tr class="header" >
      <th>Level</th>
      <th class="padding"></th>
      <th>Message</th>
      <th class="padding"></th>
    </tr>
    <logic:iterate id="event"
                   property="list"
                   name="farmer.work.status.events"
                   type="org.smartfrog.services.cloudfarmer.client.web.model.cluster.StatusEvent">
      <tr>
        <td class="eventLevel">
          <bean:write name="event" property="level"/>
        </td>
        <td class="padding"></td>
        <td class="eventText">
          <bean:write name="event" property="message"/>
        </td>
      </tr>
    </logic:iterate>
   </tbody>
  </table>
</logic:notEmpty>


<table class="navigation" cellpadding="0" cellspacing="0">
  <tbody>
    <tr class="navigation">
      <td>
        <html:link
            action="/mombasa-portlet/cluster/add_dynamic/status">Update Status</html:link>
      </td>
    </tr>
  </tbody>
</table>






