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

<p>Change the type or remote address of the cluster manager</p>

<html:form action="/mombasa-portlet/cluster/change_manager/process" method="post" focus="controller">

  <table cellpadding="0" cellspacing="0" width="80%">
   <tbody>
    <tr>
      <td>
        Controller
      </td>
      <td class="padding"></td>
      <td>
        <html:select name="clusterChangeManagerForm" property="controller">
          <html:option value="0">Physical Host List</html:option>
          <html:option value="1">HP Cells</html:option>
          <html:option value="2">OpenCirus</html:option>
          <html:option value="3">Amazon EC2 API</html:option>
        </html:select>
      </td>
    </tr>

    <tr>
      <td>
        URL
      </td>
      <td class="padding"></td>
      <td>
        <html:text name="clusterChangeManagerForm" property="url" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        Username
      </td>
      <td class="padding"></td>
      <td>
        <html:text name="clusterChangeManagerForm" property="username" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        Password
      </td>
      <td class="padding"></td>
      <td>
        <html:password name="clusterChangeManagerForm" property="password" size="23"/>
      </td>
    </tr>

    <tr>
      <td>
        <html:submit>
          Change
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
  For cells, use http://localhost/farmer/cellModel/farmer </br>
  or use http://localhost/mombasa/farmer/cellModel/farmer </br>
  For mock, use http://localhost/farmer 


</html:form>




