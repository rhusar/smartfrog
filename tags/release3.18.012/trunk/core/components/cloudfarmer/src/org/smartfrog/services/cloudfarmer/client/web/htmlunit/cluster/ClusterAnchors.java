/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.cloudfarmer.client.web.htmlunit.cluster;

/**
 * Created 02-Dec-2009 13:00:27
 */


public interface ClusterAnchors {
    
    /*
            action="/mombasa-portlet/cluster/list" name="listHosts">List Hosts</html:link>
    </td>
    <td>
      <html:link
          action="/mombasa-portlet/cluster/listRoles" name="listRoles">List Roles</html:link>
    </td>
    <td>
      <html:link
          action="/mombasa-portlet/cluster/add" name="add">Add a Host</html:link>
    </td>
    <td>
      <html:link
          action="/mombasa-portlet/cluster/admin" name="admin">Administration</html:link>
     */
    
    String LIST_HOSTS ="listHosts";
    /** {@value} */
    String LIST_ROLES = "listRoles";
    /** {@value} */
    String ADD_HOST="add";
    /** {@value} */
    String ADMIN="admin";
    /** {@value} */
    //String="";
}
