package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * An individual role allocation has succeeded or failed.
 * This call is made in the same thread as the worker requesting the hosts; anything slow here blocks the next request,
 * if there is a series of them
 */

public interface RoleAllocationCompleted {

    /**
     * The request succeeded
     * @param request the request has succeeded
     * @throws IOException IO problems
     * @throws SmartFrogException other problems
     */
    public void allocationSucceeded(RoleAllocationRequest request) throws IOException, SmartFrogException;

    /**
     * The request succeeded
     * @param request the request has succeeded
     * @throws IOException IO problems
     * @throws SmartFrogException other problems
     */
    public void allocationFailed(RoleAllocationRequest request) throws IOException, SmartFrogException;
}
