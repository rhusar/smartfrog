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


<p>Add hosts to form a Hadoop cluster; every machine will become a datanode and job tracker.</p>

<html:form action="/mombasa-portlet/cluster/add_dynamic/process" method="post" focus="minWorkers">


  <table cellpadding="0" cellspacing="0">
   <tbody>
    <tr>
      <td class="fieldname">
        Minimum number of workers
      </td>
      <td class="padding"></td>
      <td class="field">
        <html:text name="clusterAddDynamicForm" property="minWorkers" size="23"/>
      </td>
    </tr>

    <tr>
      <td class="fieldname">
        Maximum number of workers
      </td>
      <td class="padding"></td>
      <td class="field">
        <html:text name="clusterAddDynamicForm" property="maxWorkers" size="23"/>
      </td>
    </tr>
    
    <tr>
      <td class="action">
        <html:submit>
          Add
        </html:submit>
      </td>
      <td class="padding"></td>
      <td class="action">
        <html:reset>
          Reset
        </html:reset>
      </td>
    </tr>
   </tbody>
  </table>

<p>If a master node is needed, it will be added automatically</p>
</html:form>




