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





