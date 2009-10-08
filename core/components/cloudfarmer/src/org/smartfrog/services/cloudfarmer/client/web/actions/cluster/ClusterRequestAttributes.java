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
package org.smartfrog.services.cloudfarmer.client.web.actions.cluster;

/**
 * Created 02-Oct-2009 12:53:27
 */

public interface ClusterRequestAttributes {

    String ACTION_SUCCESS = "success";
    String ACTION_FAILURE = "failure";
    String ERROR = "error";
    String ATTR_HOSTS = "hosts";
    String ATTR_HOSTCOUNT = "hostcount";
    String ATTR_CLUSTER_HAS_MASTER = "cluster.hasMaster";
    String ATTR_CLUSTER_MASTER = "cluster.master";
    String ATTR_CLUSTER_CONTROLLER = "cluster.controller";
    String ATTR_CLUSTER_MASTER_HOSTNAME = "cluster.master.hostname";
    String ATTR_THROWN = "org.apache.struts.action.EXCEPTION";
    String ATTR_ERROR_MESSAGE = "errorMessage";
    String ATTR_ERROR_CAUSE = "errorCause";
    String FARM_CONTROLLER_URL = "farmControllerURL";
    String FARM_CONTROLLER = "farmController";
    String ATTR_HOSTID = "hostid";
    String ATTR_HOST = "host";
}
