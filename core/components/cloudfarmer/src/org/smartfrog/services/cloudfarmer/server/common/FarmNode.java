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
package org.smartfrog.services.cloudfarmer.server.common;

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;

/**
 * This is a class about a farm node that is expected to stay on the server, be extensible and hence not final; it will
 * be stored in the farm node map
 */

public class FarmNode {

    public ClusterNode clusterNode;

    public ClusterRoleInfo roleInfo;

    public Object extraData;


    public FarmNode(ClusterNode clusterNode, ClusterRoleInfo roleInfo, Object extraData) {
        this.clusterNode = clusterNode;
        this.extraData = extraData;
        setRoleInfo(roleInfo);
    }

    public FarmNode() {
        clusterNode = new ClusterNode();
    }

    public ClusterNode getClusterNode() {
        return clusterNode;
    }

    public void setClusterNode(ClusterNode clusterNode) {
        this.clusterNode = clusterNode;
    }

    public ClusterRoleInfo getRoleInfo() {
        return roleInfo;
    }

    public void setRoleInfo(ClusterRoleInfo roleInfo) {
        this.roleInfo = roleInfo;
        if (roleInfo != null) {
            clusterNode.setRole(roleInfo.getName());
        } else {
            clusterNode.setRole("");
        }
    }

    public String getId() {
        return clusterNode.getId();
    }

    public void setId(String id) {
        clusterNode.setId(id);
    }

    public String getHostname() {
        return clusterNode.getHostname();
    }

    public void setHostname(String hostname) {
        clusterNode.setHostname(hostname);
    }

    public boolean isExternallyVisible() {
        return clusterNode.isExternallyVisible();
    }

    public void setExternallyVisible(boolean externallyVisible) {
        clusterNode.setExternallyVisible(externallyVisible);
    }

    public String getExternalHostname() {
        return clusterNode.getExternalHostname();
    }

    public void setExternalHostname(String externalHostname) {
        clusterNode.setExternalHostname(externalHostname);
    }


    public String getDetails() {
        return clusterNode.getDetails();
    }

    public void setDetails(String details) {
        clusterNode.setDetails(details);
    }

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }

    @Override
    public String toString() {
        return "FarmNode " + clusterNode;
    }

    public boolean isFree() {
        return roleInfo == null;
    }

    public void free() {
        roleInfo = null;
    }

    public boolean isInRole(String role) {
        return roleInfo != null && roleInfo.getName().equals(role);
    }
}
