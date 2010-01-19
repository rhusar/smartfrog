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
package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.MasterWorkerRoles;
import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created 01-Sep-2009 13:40:33
 */

public final class HostInstance implements Serializable {

    public String id;

    public String hostname;

    public String externalHostname;
    
    public String role;
    
    public boolean canDelete;

    private Workflow application;

    private RemoteDaemon daemon;
    
    private String details;

    private ClusterNode clusterNode;
    
    public HostInstance() {
    }

    public HostInstance(String hostname) {
        this.hostname = hostname;
        id = hostname;
    }

    public HostInstance(String id, String hostname, boolean canDelete) {
        this.id = id;
        this.hostname = hostname;
        this.canDelete = canDelete;
    }
    
    public HostInstance(String id, ClusterNode node, boolean canDelete) {
        this(id, node.getHostname(), canDelete);
        clusterNode = node;
        role = node.getRole();
        details = node.getDetails();
        externalHostname = node.getExternalHostname();
    }

    /**
     * {@inheritDoc}
     *
     * @param o object to comare
     * @return true if the objects are considered equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HostInstance that = (HostInstance) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public String toString() {
        return "HostInstance{" +
                "id='" + id + '\'' +
                ", hostname='" + hostname + '\'' +
                ", role='" + role + '\'' +
                ", canDelete=" + canDelete +
                '}';
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getExternalHostname() {
        return externalHostname;
    }

    public void setExternalHostname(String externalHostname) {
        this.externalHostname = externalHostname;
    }

    public String getState() {
        return clusterNode != null? 
                clusterNode.getState():
                "";
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isMaster() {
        return MasterWorkerRoles.MASTER.equals(role);
    }


    public boolean isWorker() {
        return MasterWorkerRoles.WORKER.equals(role);
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ClusterNode getClusterNode() {
        return clusterNode;
    }

    /**
     * Bind to the SF daemon on the remote machine
     *
     * @return the daemon binding
     * @throws IOException        any networking problem
     * @throws SmartFrogException SF problems
     */
    public RemoteDaemon bindToDaemon() throws IOException, SmartFrogException {
        if (daemon != null) {
            return daemon;
        }
        daemon = new RemoteDaemon("http://" + hostname);
        daemon.bindOnDemand();
        return daemon;
    }


    public RemoteDaemon getDaemon() {
        return daemon;
    }

    public synchronized Workflow getApplication() {
        return application;
    }

    public synchronized void setApplication(Workflow application) {
        this.application = application;
    }

    public String getApplicationName() {
        if (application != null) {
            return application.getName();
        } else {
            return null;
        }
    }

    /**
     * If there is an application, this terminates it
     *
     * @throws IOException on any problem
     */
    public synchronized void terminateApplication() throws IOException {
        if (application != null) {
            try {
                application.terminate(true, "host removal");
            } finally {
                application = null;
            }
        }
    }
}
