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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created 02-Sep-2009 16:25:15
 */

public final class HostInstanceList extends ArrayList<HostInstance> {

    public HostInstanceList(int initialCapacity) {
        super(initialCapacity);
    }

    public HostInstanceList() {
    }

    public HostInstanceList(Collection<? extends HostInstance> c) {
        super(c);
    }

    public HostInstanceList(ClusterNode[] clusterNodes) {
        super(clusterNodes.length);
        importNodes(clusterNodes);
    }


    /**
     * for struts integration
     *
     * @return list of host instances
     */
    public List<HostInstance> getList() {
        return this;
    }

    /**
     * Get a list of all hosts in a role
     *
     * @param role role to search for
     * @return the list of hosts in that role, may be empty
     */
    public List<HostInstance> getListInRole(String role) {
        List<HostInstance> results = new ArrayList<HostInstance>();
        for (HostInstance instance : this) {
            if (role.equals(instance.getRole())) {
                results.add(instance);
            }
        }
        return results;
    }

    public HostInstance getMaster() {
        List<HostInstance> masters = getListInRole(MasterWorkerRoles.MASTER);
        if (masters.size() > 0) {
            return masters.get(0);
        } else {
            return null;
        }
    }

    public void importNodes(ClusterNode[] clusterNodes) {
        synchronized (this) {
            for (ClusterNode node : clusterNodes) {
                HostInstance instance = new HostInstance(node.getId(), node, true);
                add(instance);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (HostInstance instance : this) {
            builder.append(instance.hostname).append(' ');
        }
        return builder.toString();
    }
}
