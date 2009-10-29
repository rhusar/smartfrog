/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cloudfarmer.api;

import java.io.Serializable;

/**
 * Serializable representation of a cluster node
 */
public final class ClusterNode implements Serializable, Cloneable {

    /**
     * Whether this cluster node is in fact a compound of cluster nodes
     */
    private boolean compound = false;

    /**
     * Vector of cluster nodes, not null if compound is true
     */
    private ClusterNode[] children;

    /**
     * The Id of this node. This should be treated as opaque, and is only of interest to the Farmer
     */
    private String id;

    /**
     * The internal hostname
     */
    private String hostname;


    /**
     * Is this node externally visible
     */
    private boolean externallyVisible;

    /**
     * The external hostname
     */
    private String externalHostname;

    /**
     * Any details (can be null)
     */
    private String details;
    /**
     * The role of the node
     */
    private String role;
    
    /**
     * Current status of the instance
     */
    private String state;  
    
    public ClusterNode() {
    }

    public boolean isCompound() {
        return compound;
    }

    public void setCompound(final boolean compound) {
        this.compound = compound;
    }
    
    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }
    
    public ClusterNode[] getChildren() {
        return children;
    }

    public void setChildren(final ClusterNode[] children) {
        this.children = children;
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

    public boolean isExternallyVisible() {
        return externallyVisible;
    }

    public void setExternallyVisible(boolean externallyVisible) {
        this.externallyVisible = externallyVisible;
    }

    public String getExternalHostname() {
        return externalHostname;
    }

    public void setExternalHostname(String externalHostname) {
        this.externalHostname = externalHostname;
    }

    /**
     * check for being in a role; the node role can be null
     * @param roleToCheck the non-null role to check
     * @return true iff the two role strings are equal
     */
    public boolean isInRole(String roleToCheck) {
        return roleToCheck.equals(role);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * The equality test compares node ID only
     *
     * @param o other object
     * @return true iff the other object is a ClusterNode with the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClusterNode that = (ClusterNode) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    /**
     * Hash code comes from the ID
     *
     * @return the hash code's ID
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException as the signature demands it.
     * @see Cloneable
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * {@inheritDoc}
     *
     * @return a string listing
     */
    @Override
    public String toString() {
        return "Node " + id
                + " @ " + hostname
                + (externallyVisible ?
                (" and " + externalHostname) : "")
                + " [" + role + ']'
                + (details != null ? details : "");
    }
}
