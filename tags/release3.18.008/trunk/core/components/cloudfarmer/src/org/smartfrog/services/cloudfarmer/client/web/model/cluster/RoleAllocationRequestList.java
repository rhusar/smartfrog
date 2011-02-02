package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import java.util.ArrayList;

/**
 *
 */
public class RoleAllocationRequestList extends ArrayList<RoleAllocationRequest> {

    public RoleAllocationRequestList(int initialCapacity) {
        super(initialCapacity);
    }

    public RoleAllocationRequestList() {
    }


    /**
     * Find a named request or return null
     * @param role role to look for
     * @return the request or null
     */
    public RoleAllocationRequest getInRole(String role) {
        for (RoleAllocationRequest request: this) {
            if (request.getRole().equals(role)) {
                return request;
            }
        }
        return null;
    }
}
