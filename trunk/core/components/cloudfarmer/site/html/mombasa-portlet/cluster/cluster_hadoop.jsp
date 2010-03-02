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

<h1>Hadoop Actions</h1>

<table 
  class="wide" cellpadding="0" cellspacing="0" >
  <thead>
    <tr class="header">
      <th>Action</th>
      <th class="padding"></th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td class="action">
        <html:link
            action="/mombasa-portlet/cluster/add" styleId="add">
          Add Hadoop nodes
        </html:link>
      </td>
      <td class="padding"></td>
      <td>
        Add and configure Hadoop nodes
      </td>
    </tr>
    <tr>
      <td class="action">
        <html:link href="/hadoop-site.xml"
            styleId="hadoop-site">
          hadoop-site.xml 
        </html:link>
      </td>
      <td class="padding"></td>
      <td>
        View the current Hadoop configuration. This link only works once a Hadoop master has been deployed
      </td>
    </tr>
  </tbody>
</table>

