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
package org.smartfrog.services.cloudfarmer.api;

import java.io.Serializable;

/**
 * Information about a cluster role 
 */

public final class ClusterRoleInfo implements Serializable, Cloneable {

    private String name;
    private String description = "";
    private String longDescription = "";
    private Range roleSize = Range.NO_LIMITS;
    private Range recommendedSize = Range.NO_LIMITS;

    public ClusterRoleInfo() {
    }

    public ClusterRoleInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setRoleSize(int min, int max) {
        roleSize = new Range(min, max);
    }

    public void setRecommendedSize(int min, int max) {
        recommendedSize = new Range(min, max);
    }

    public void setRoleSize(Range roleSize) {
        this.roleSize = roleSize;
    }

    public void setRecommendedSize(Range recommendedSize) {
        this.recommendedSize = recommendedSize;
    }

    public Range getRoleSize() {
        return roleSize;
    }

    public Range getRecommendedSize() {
        return recommendedSize;
    }

    
    
    public boolean isInRange(int proposed) {
        return roleSize.isInRange(proposed);
    }

    public boolean isInRecommendedRange(int proposed) {
        return recommendedSize.isInRange(proposed);
    }

    @Override
    public String toString() {
        return "ClusterRoleInfo{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", longDescription='" + longDescription + '\'' +
                ", roleSize=" + roleSize +
                ", recommendedSize=" + recommendedSize +
                '}';
    }

    /**
     * {@inheritDoc}
     * @return a clone
     * @throws CloneNotSupportedException
     */
    @Override
    public ClusterRoleInfo clone()  {
        try {
            return (ClusterRoleInfo) super.clone();
        } catch (CloneNotSupportedException noClone) {
            //not going to happen.
            throw new RuntimeException(noClone);
        }
    }

}
