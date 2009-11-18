package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * This callback is called after all requests have finished
 */
public interface ClusterAllocationCompleted {

    /**
     * The farmer was not available
     * @param timedOut did the request time out
     * @param timeout what was the wait specified
     * @param exception if it didn't time out, what was the exception. Will be null for a timeout
     * @param extraData extra data passed with the request
     * @throws IOException IO problems
     * @throws SmartFrogException other problems
     */
    void farmerAvailabilityFailure(boolean timedOut, long timeout, Throwable exception, Object extraData)
         throws IOException, SmartFrogException;

    /**
     * The request succeeded
     *
     * @param requests the list of allocation requests
     * @param hosts allocated hosts
     * @param extraData extra data passed with the request
     * @throws IOException IO problems
     * @throws SmartFrogException other problems
     */
    void allocationSucceeded(RoleAllocationRequestList requests,
                                    HostInstanceList hosts,
                                    Object extraData) throws IOException, SmartFrogException;

    /**
     * The request failed
     *
     * @param requests the list of allocation requests
     * @param hosts allocated hosts
     * @param failureCause cause of failure
     * @param extraData extra data passed with the request
     * @throws IOException IO problems
     * @throws SmartFrogException other problems
     */
    void allocationFailed(RoleAllocationRequestList requests,
                                 HostInstanceList hosts,
                                 Throwable failureCause,
                                 Object extraData) throws IOException, SmartFrogException;

    /**
     * A single role request has succeeded
     * @param request the request that just succeeded
     * @param newhosts the new hosts
     * @throws IOException IO problems
     * @throws SmartFrogException other problems
     */
    void allocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException;
}
