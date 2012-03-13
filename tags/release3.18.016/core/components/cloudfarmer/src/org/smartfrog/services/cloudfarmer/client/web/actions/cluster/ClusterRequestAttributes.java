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
 * These are struts request attributes -either parameters on those set on ongoing actions
 */

public interface ClusterRequestAttributes {

    /**
     * {@value}
     */
    String ACTION_SUCCESS = "success";
    /**
     * {@value}
     */
    String ACTION_FAILURE = "failure";

    /**
     * {@value}
     */
    String ACTION_NOT_FOUND = "notFound";
    /**
     * {@value}
     */
    String ERROR = "error";
    /**
     * {@value}
     */
    String ATTR_HOSTS = "hosts";
    /**
     * {@value}
     */
    String ATTR_HOSTCOUNT = "hostcount";
    /**
     * {@value}
     */
    String ATTR_CLUSTER_HAS_MASTER = "cluster.hasMaster";
    /**
     * {@value}
     */
    String ATTR_CLUSTER_MASTER = "cluster.master";
    /**
     * {@value}
     */
    String ATTR_CLUSTER_CONTROLLER = "cluster.controller";
    /**
     * {@value}
     */
    String ATTR_CLUSTER_MASTER_HOSTNAME = "cluster.master.hostname";
    /**
     * {@value}
     */
    String ATTR_THROWN = "org.apache.struts.action.EXCEPTION";
    /**
     * {@value}
     */
    String ATTR_ERROR_MESSAGE = "errorMessage";
    /**
     * {@value}
     */
    String ATTR_ERROR_CAUSE = "errorCause";
    /**
     * {@value}
     */
    String FARM_CONTROLLER_URL = "farmControllerURL";
    /**
     * {@value}
     */
    String FARM_CONTROLLER = "farmController";
    /**
     * {@value}
     */
    String ATTR_HOSTID = "hostid";
    /**
     * {@value}
     */
    String ATTR_HOST = "host";
    /**
     * {@value}
     */
    String ATTR_ROLE = "role";

    /**
     * {@value}
     */
    String ATTR_ROLEINFO = "roleInfo";

    /**
     * {@value}
     */
    String FARMER_DESCRIPTION = "farmer.description";
    /**
     * {@value}
     */
    String FARMER_DIAGNOSTICS_TEXT = "farmer.diagnostics.text";

    /**
     * {@value}
     */
    String ATTR_FARMER_WORKING = "farmer.working";
    /**
     * {@value}
     */
    String ATTR_FARMER_WORK_STATUS = "farmer.work.status";
    /**
     * {@value}
     */
    String ATTR_FARMER_WORK_ERROR = "farmer.work.error";
    /**
     * {@value}
     */
    String ATTR_FARMER_WORK_REQUESTS = "farmer.work.requests";
    /**
     * {@value}
     */
    String ATTR_FARMER_WORK_STATUS_EVENTS = "farmer.work.status.events";
    

}
