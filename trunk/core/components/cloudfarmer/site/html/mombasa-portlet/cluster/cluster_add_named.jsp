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



<p>Add a named machine to the cluster</p>

<html:form action="/mombasa-portlet/cluster/add_named/process" method="post" focus="name">


  <table cellpadding="0" cellspacing="0">
   <tbody>
    <tr>
      <td>
        Name
      </td>
      <td class="padding"></td>
      <td>
        <html:text name="clusterAddNamedForm" property="name" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        Worker
      </td>
      <td class="padding"></td>
      <td>
        <html:checkbox name="clusterAddNamedForm" property="worker" />
      </td>
    </tr>

    <tr>
      <td>
        Master
      </td>
     <td class="padding"></td>
      <td>
        <logic:notEmpty name="cluster.master.hostname">
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
      <td class="padding"></td>
      <td>
        <html:reset>
          Reset
        </html:reset>
      </td>
    </tr>
   </tbody>
  </table>


</html:form>




