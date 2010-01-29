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

import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.LocalSmartFrogDescriptor;
import org.smartfrog.services.cloudfarmer.client.common.AbstractEndpoint;
import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.MasterWorkerRoles;
import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.hadoop.descriptions.TemplateNames;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.ClusterControllerBusyException;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.FarmerNotLiveException;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.UnimplementedException;
import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The cluster controller is whatever gives us new clusters
 */
public abstract class ClusterController extends AbstractEndpoint implements Iterable<HostInstance>, TemplateNames {

    private HostInstanceList hosts;
    private Map<String, HostInstance> hostMap;
    //hosts by role
    private HashMap<String, ClusterRoleInfo> roles;
    private transient volatile HostCreationThread workerThread;
    private volatile Throwable workerThreadException;

    private static final int INITIAL_HOSTLIST_CAPACITY = 1;
    private static final int FARMER_AVAILABILITY_SLEEP_MILLIS = 500;
    private static final String STATUS_NOT_STARTED = "not yet started";

    /**
     * Basic constructor clears the host list
     */
    protected ClusterController() {
        clearHostList();
    }

    /**
     * Bind to the URL
     *
     * @param baseURL the URL for the controller
     */
    protected ClusterController(String baseURL) {
        super(baseURL);
        clearHostList();
    }

    /**
     * Bind to the controller. If this fails, the controller must be considered invalid. The base class calls
     * startCluster();
     *
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    public void bind() throws IOException, SmartFrogException {
        startCluster();
    }

    /**
     * Call to start the cluster if it is not already live
     *
     * @throws IOException        something went wrong
     * @throws SmartFrogException something different went wrong
     */
    protected void startCluster() throws IOException, SmartFrogException {

    }


    /**
     * Call to stop the cluster. This may be a no-op, it may shut down the entire cluster. Some infrastructures require
     * this to release allocations.
     *
     * @throws IOException        something went wrong
     * @throws SmartFrogException something different went wrong
     */
    protected void stopCluster() throws IOException, SmartFrogException {

    }


    /**
     * Get the description -can be displayed in the view
     *
     * @return an end-user description
     */
    public abstract String getDescription();

    /**
     * Clear the host list
     */
    protected synchronized void clearHostList() {
        hosts = new HostInstanceList(INITIAL_HOSTLIST_CAPACITY);
        hostMap = new HashMap<String, HostInstance>(INITIAL_HOSTLIST_CAPACITY);
        roles = new HashMap<String, ClusterRoleInfo>(2);
    }

    /**
     * Replace the existing host list with a new one. Nothing is copied over; if you want to preserve the application
     * list, do it first.
     *
     * @param list new list of hosts
     */
    protected synchronized void replaceHostList(HostInstanceList list) {
        hosts = list;
        hostMap = new HashMap<String, HostInstance>(list.size());
        for (HostInstance instance : list) {
            hostMap.put(instance.getId(), instance);
        }
    }

    /**
     * replace the roles
     *
     * @param rolemap new role map
     */
    protected synchronized void replaceRoles(HashMap<String, ClusterRoleInfo> rolemap) {
        roles = rolemap;
    }

    /**
     * Get a clone of the roles
     *
     * @return a map
     */
    public synchronized Map<String, ClusterRoleInfo> getRoleMap() {
        return (Map<String, ClusterRoleInfo>) roles.clone();
    }

    /**
     * Get the list of roles
     *
     * @return a list of roles, no specific order.
     */
    public synchronized List<ClusterRoleInfo> getRoles() {
        if (roles == null) {
            return new ArrayList<ClusterRoleInfo>(0);
        }
        List<ClusterRoleInfo> roleList = new ArrayList<ClusterRoleInfo>(roles.size());
        for (ClusterRoleInfo role : roles.values()) {
            roleList.add(role);
        }
        return roleList;
    }

    /**
     * Look up a role by name
     *
     * @param name role name
     * @return the role or null for no match
     */

    public ClusterRoleInfo getRole(String name) {
        if (roles == null) {
            return null;
        }
        return roles.get(name);
    }

    /**
     * Get a list of the hosts. The list is a clone, no need to worry about synchronization problems, though hosts may
     * have been deleted by the time you get to them
     *
     * @return the cloned list
     */
    public synchronized HostInstanceList getHosts() {
        return (HostInstanceList) hosts.clone();
    }

    /**
     * Get the master
     *
     * @return the master or null
     */
    public synchronized HostInstance getMaster() {
        return hosts.getMaster();
    }

    /**
     * Test for the cluster having a master
     *
     * @return true iff the cluster has a master
     */
    public boolean hasMaster() {
        return getMaster() != null;
    }

    /**
     * Get the number of hosts
     *
     * @return the count of hosts
     */
    public synchronized int getHostCount() {
        return hosts.size();
    }

    /**
     * Get an iterator over a clone of the host list.
     *
     * @return a new iterator.
     */
    public Iterator<HostInstance> iterator() {
        return getHosts().iterator();
    }

    /**
     * Return the number of task slots for this cluster. Subclasses can change how this is calculated
     *
     * @return a default value, or something cleverer
     */
    public int getTaskSlots() {
        return TASK_SLOTS;
    }

    /**
     * Refreshes the host list
     *
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    public abstract void refreshHostList() throws IOException, SmartFrogException;


    /**
     * @param role role of these hosts
     * @param min  minimum number
     * @param max  maximum number
     * @return the allocated machines
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    public HostInstanceList createHosts(String role, int min, int max)
            throws IOException, SmartFrogException {
        throw new UnimplementedException("Cannot add hosts to this cluster");
    }


    /**
     * Create a new host
     *
     * @param hostname      a hostname
     * @param largeInstance the instance
     * @param descriptor    descriptor to deploy on the host
     * @return a new host
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    public HostInstance createHost(String hostname,
                                   boolean largeInstance,
                                   LocalSmartFrogDescriptor descriptor)
            throws IOException, SmartFrogException {
        throw new UnimplementedException("Cannot add a host to this cluster");
    }


    /**
     * Override point
     *
     * @return true iff hosts can be added through createHost
     */
    public boolean canCreateHost() {
        return false;
    }

    /**
     * Override point
     *
     * @return true iff hosts can be manually added
     */
    public boolean canAddNamedHost() {
        return false;
    }

    /**
     * Add a host to the system. <p/> The base class validates against {@link #canAddNamedHost()} and the current
     * cluster state, then creates a stub instance, which can be replaced with a different one, if desired. It does not
     * add it to the host list.
     *
     * @param hostname a hostname
     * @param isMaster is the node a master
     * @param isWorker is the node a worker
     * @return the instance
     * @throws IOException            network trouble
     * @throws SmartFrogException     SF trouble
     * @throws UnimplementedException if you cannot add hosts to this manager
     */
    public HostInstance addNamedHost(String hostname, boolean isMaster, boolean isWorker)
            throws IOException, SmartFrogException {
        if (!canAddNamedHost()) {
            throw new UnimplementedException("Cannot add a host to this cluster");
        }
        validateRoles(isMaster, isWorker);
        return new HostInstance(hostname);
    }


    /**
     * Delete a host
     *
     * @param hostID a host ID
     * @return true if the request has been queued
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    public boolean deleteHost(String hostID)
            throws IOException, SmartFrogException {
        return false;
    }

    /**
     * Look up a host by ID
     *
     * @param hostID ID
     * @return the host information or null for no match
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    public synchronized HostInstance lookupHost(String hostID)
            throws IOException, SmartFrogException {
        return hostMap.get(hostID);
    }

    /**
     * Look up a host in the list by name. This does not trigger a refresh of the list
     *
     * @param name host name to look for
     * @return the instance
     */
    public synchronized HostInstance lookupHostByName(String name) {
        for (HostInstance host : hosts) {
            if (host.hostname.equals(name)) {
                return host;
            }
        }
        return null;
    }

    /**
     * Return all hosts in the specific role
     *
     * @param role role to search for
     * @return a possibly empty list of hosts
     */
    public synchronized HostInstanceList lookupHostsByRole(String role) {
        HostInstanceList hostsInRole = new HostInstanceList(hosts.size());
        for (HostInstance host : hosts) {
            if (role.equals(host.getRole())) {
                hostsInRole.add(host);
            }
        }
        return hostsInRole;
    }

    /**
     * Add a new host to the hosts list
     *
     * @param hi instance
     */
    protected void addHostInstance(HostInstance hi) {
        synchronized (this) {
            hosts.add(hi);
            hostMap.put(hi.getId(), hi);
        }
    }

    /**
     * Remove an instance from the list
     *
     * @param hi the host instance. Can be null.
     * @return true if it was removed.
     */
    protected synchronized boolean removeHostInstance(HostInstance hi) {
        if (hi != null) {
            hosts.remove(hi);
            hostMap.remove(hi.getId());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shut down the cluster
     *
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    public abstract void shutdownCluster() throws IOException, SmartFrogException;

    @Override
    public String toString() {
        return getDescription();
    }


    /**
     * Validate the roles of this node
     *
     * @param isMaster is the node to be a master
     * @param isWorker is the node to be a worker
     * @throws SmartFrogException if the cluster does not support the role at this time
     */
    protected void validateRoles(boolean isMaster, boolean isWorker) throws SmartFrogException {
        HostInstance master = getMaster();
        boolean hasMaster = master != null;
        if (isMaster && hasMaster) {
            throw new SmartFrogException("Cluster already has the master " + master.hostname
                    + " proposed values master=" + isMaster + " worker=" + isWorker);
        }
        if (isWorker && !isMaster && !hasMaster) {
            throw new SmartFrogException("Cluster has no master"
                    + " proposed values master=" + isMaster + " worker=" + isWorker);
        }
        if (isMaster && isWorker) {
            throw new SmartFrogException("A node cannot be both a master and a worker");
        }
    }

    /**
     * Based on the role, choose what to install on the target host. There is no waiting for the host to be up here;
     * that work has to be done in some other mechanism.
     *
     * @param host     target host
     * @param isMaster is the node to be a master
     * @param isWorker is the node to be a worker
     * @return the workflow
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    protected Workflow installRole(HostInstance host, boolean isMaster, boolean isWorker)
            throws IOException, SmartFrogException {
        String resource;
        String masterName;
        HostInstance master = getMaster();
        boolean hasMaster = master != null;
        HostRole role = HostRole.getRole(isMaster, isWorker);
        String rolename;
        switch (role) {
            case master:
                resource = HADOOP_MASTER_SF;
                rolename = MasterWorkerRoles.MASTER;
                if (hasMaster) {
                    throw new SmartFrogException("Cluster already has the master " + master.hostname);
                }
                masterName = host.getHostname();
                break;
            case worker:
                resource = HADOOP_WORKER_SF;
                rolename = MasterWorkerRoles.MASTER;
                if (!hasMaster) {
                    throw new SmartFrogException("Cluster has no master");
                }
                masterName = master.getHostname();
                break;
            case none:
            default:
                masterName = "";
                resource = null;
                rolename = "";
        }
        if (resource == null) {
            //nothing to deploy
            return null;
        } else {
            String appname = role.toString();
            return installApplication(host, appname, resource, masterName, rolename);
        }
    }

    /**
     * Install the application
     *
     * @param host       target host
     * @param appname    application name
     * @param resource   resource name (must be on classpath)
     * @param masterName hostname for the master
     * @param roleName   name of the role
     * @return the workflow
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    protected Workflow installApplication(HostInstance host,
                                          String appname,
                                          String resource,
                                          String masterName,
                                          String roleName)
            throws IOException, SmartFrogException {
        ///set the binding values
        System.setProperty(BINDING_MASTER_HOSTNAME, masterName);
        System.setProperty(BINDING_TASKTRACKER_SLOTS, "" + getTaskSlots());

        //load the CD -so that any binding problem shows up early
        LocalSmartFrogDescriptor localApp = new LocalSmartFrogDescriptor();
        localApp.parseResource(resource);
        localApp.throwParseExceptionIfNeeded();

        //bind to the daemon
        RemoteDaemon daemon = host.bindToDaemon();

        //create the workflow
        Workflow workflow = daemon.createWorkflow(appname, localApp.getComponentDescription());


        host.setApplication(workflow);
        host.setRole(roleName);
        return workflow;
    }

    /**
     * Refresh the role list
     *
     * @throws IOException        io problems
     * @throws SmartFrogException SF problems
     */
    public abstract void refreshRoleList() throws IOException, SmartFrogException;


    /**
     * Query the farmer to see if it is live.
     *
     * @return true if the service considers itself available. If not, it can return false or throw an exception.
     * @throws IOException        something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public boolean isFarmerAvailable() throws IOException, SmartFrogException {
        return true;
    }

    /**
     * Caller can return diagnostics text for use in bug reports
     *
     * @return a short description (e.g. name)
     * @throws IOException        something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public String getRemoteDescription() throws IOException, SmartFrogException {
        return "Cluster Controller";
    }

    /**
     * Caller can return diagnostics text for use in bug reports, use \n between lines and expect this printed as
     * preformatted text (with all angle brackets stripped)
     *
     * @return a diagnostics text string.
     * @throws IOException        something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public String getDiagnosticsText() throws IOException, SmartFrogException {
        return getRemoteDescription();
    }

    /**
     * Queue a request to create a set of hosts, with the allocation request
     *
     * @param allocations                the list of allocation actions to perform
     * @param farmerAvailabilityTimeout  time to give up on the farmer
     * @param clusterAllocationCompleted callback for completion
     * @param callbackData               any data to include with the callback
     * @return the worker thread that is doing the allocation
     * @throws IOException                    network trouble
     * @throws SmartFrogException             SF trouble
     * @throws ClusterControllerBusyException if the controller is busy
     */
    public synchronized HostCreationThread asyncCreateHosts(RoleAllocationRequestList allocations,
                                                            long farmerAvailabilityTimeout,
                                                            ClusterAllocationCompleted clusterAllocationCompleted,
                                                            Object callbackData)
            throws IOException, SmartFrogException {
        //quick check for the farmer to raise any exception here and now
        isFarmerAvailable();
        //now look for the worker, and bail out if it is live
        if (isWorkerThreadWorking()) {
            throw new ClusterControllerBusyException("The cluster controller is busy with an earlier request, "
                    + "its last status was " + workerThread.getStatus());
        }
        workerThread = new HostCreationThread(allocations,
                farmerAvailabilityTimeout,
                clusterAllocationCompleted,
                callbackData);
        workerThread.start();
        return workerThread;
    }


    /**
     * Get the worker thread, may be null
     *
     * @return the worker
     */
    public HostCreationThread getWorkerThread() {
        return workerThread;
    }

    /**
     * Do we have a worker thread?
     *
     * @return true iff there's a thread
     */
    public final boolean hasWorkerThread() {
        return workerThread != null;
    }

    /**
     * Test to see if there is a worker thread that is live
     *
     * @return true iff there is a worker thread that is not finished
     */
    public synchronized boolean isWorkerThreadWorking() {
        return hasWorkerThread() && !workerThread.isFinished();
    }

    /**
     * Get any worker thread extension
     *
     * @return any exception in the worker thread
     */
    public Throwable getWorkerThreadException() {
        return workerThreadException;
    }

    /**
     * This thread creates host asynchronously, and can call callbacks afterwards, to perform the installation stages
     */
    public class HostCreationThread extends SmartFrogThread {
        private RoleAllocationRequestList allocationRequests;
        private HostInstanceList hostList = new HostInstanceList();
        private long farmerAvailabilityTimeout;
        private Object callbackData;
        private ClusterAllocationCompleted clusterAllocationCompleted;
        private String status;
        private volatile boolean started;
        private volatile boolean finished;
        private volatile long startTime;
        private volatile long finishTime;
        private StatusEvents statusEvents = new StatusEvents();

        /**
         * create a thread
         *
         * @param allocationRequests         request queue
         * @param farmerAvailabilityTimeout  timeout for the farmer
         * @param clusterAllocationCompleted callback for completion
         * @param callbackData               any data to include with the callback
         */
        public HostCreationThread(RoleAllocationRequestList allocationRequests,
                                  long farmerAvailabilityTimeout,
                                  ClusterAllocationCompleted clusterAllocationCompleted,
                                  Object callbackData) {
            this.allocationRequests = allocationRequests;
            this.farmerAvailabilityTimeout = farmerAvailabilityTimeout;
            this.callbackData = callbackData;
            this.clusterAllocationCompleted = clusterAllocationCompleted;
            status = STATUS_NOT_STARTED;
        }

        public String getStatus() {
            return status;
        }

        private void updateStatus(boolean error, String newStatus) {
            status = newStatus;
            statusEvents.addEvent(error, newStatus);
            if(error) {
                log.error(newStatus);
            } else {
                log.info(newStatus);
            }
        }

        /**
         * Get a copy of the list of status events. This may be empty, but never null
         *
         * @return the list of events. Cloned for thread safety
         */
        public StatusEvents getStatusEvents() {
            return statusEvents.clone();
        }

        /**
         * Log that the request has started
         */
        private void started() {
            started = true;
            startTime = System.currentTimeMillis();
        }

        private void finished() {
            finishTime = System.currentTimeMillis();
        }


        public long getStartTime() {
            return startTime;
        }

        public long getFinishTime() {
            return finishTime;
        }

        @Override
        public String toString() {
            return status;
        }

        /**
         * allocate the requests in sequence
         *
         * @throws Throwable on any failure
         */
        @SuppressWarnings({"ProhibitedExceptionDeclared"})
        @Override
        public void execute() throws Throwable {
            started();
            try {
                waitForFarmerAvailable();
                try {
                    for (RoleAllocationRequest request : allocationRequests) {
                        requestHosts(request, clusterAllocationCompleted);
                    }
                } catch (Throwable throwable) {
                    //failure, notify and rethrow
                    updateStatus(true, "Request failed " + throwable);
                    if (clusterAllocationCompleted != null) {
                        clusterAllocationCompleted
                                .allocationFailed(allocationRequests, 
                                        getHosts(), 
                                        throwable, 
                                        callbackData);
                    }
                    throw throwable;
                }
                updateStatus(false, "Completed cluster requests");
                //notify of success
                if (clusterAllocationCompleted != null) {
                    clusterAllocationCompleted.allocationSucceeded(allocationRequests, 
                            getHosts(), 
                            callbackData);
                }
            } finally {
                finished();
            }
        }

        private void requestHosts(RoleAllocationRequest request,
                                  ClusterAllocationCompleted completedCallback) throws IOException, SmartFrogException {
            updateStatus(false, "Requesting hosts " + request);
            request.requestStarted();
            try {
                HostInstanceList newhosts = createHosts(request.role, request.min, request.max);
                request.requestSucceeded(newhosts);
                addHosts(newhosts);
                updateStatus(false, "Got " + newhosts.size() + " - " + newhosts);
                if (completedCallback != null) {
                    completedCallback.allocationRequestSucceeded(request, newhosts);
                }
            } catch (IOException e) {
                requestFailed(e, request);
                throw e;
            } catch (SmartFrogException e) {
                requestFailed(e, request);
                throw e;
            }
        }

        /**
         * Process a request failure
         *
         * @param e       exception
         * @param request request to update
         */
        private void requestFailed(Throwable e, RoleAllocationRequest request) {
            updateStatus(true, "Request failed: " + request + ":" + e);
            request.requestFailed(e);
        }

        /**
         * Wait for the farmer
         *
         * @throws Throwable on any failure
         */
        private void waitForFarmerAvailable() throws Throwable {
            if (!isFarmerAvailable()) {
                updateStatus(false, "Waiting for Farmer for up to " + farmerAvailabilityTimeout + "mS");
                long timeout = System.currentTimeMillis() + farmerAvailabilityTimeout;
                try {
                    while (!isFarmerAvailable() && System.currentTimeMillis() < timeout) {
                        Thread.sleep(FARMER_AVAILABILITY_SLEEP_MILLIS);
                    }
                } catch (Throwable e) {
                    notifyFarmerAvailabilityException(false, e);
                    throw e;
                }
                if (!isFarmerAvailable()) {
                    String message = "Failed to create hosts -"
                            + FarmerNotLiveException.ERROR_NOT_LIVE
                            + " after " + farmerAvailabilityTimeout + " milliseconds";
                    updateStatus(true, message);
                    FarmerNotLiveException liveException = new FarmerNotLiveException(message);
                    notifyFarmerAvailabilityException(true, liveException);
                    throw liveException;
                }
            }
        }

        /**
         * Notify the cluster callback if the check for farmer availability failed
         *
         * @param timedout did the farmer time out
         * @param thrown   any exception thrown, can be null
         * @throws SmartFrogException trouble
         * @throws IOException        trouble
         */
        private void notifyFarmerAvailabilityException(boolean timedout, Throwable thrown)
                throws SmartFrogException, IOException {
            updateStatus(true, thrown == null ? FarmerNotLiveException.ERROR_NOT_LIVE : thrown.toString());
            if (clusterAllocationCompleted != null) {
                clusterAllocationCompleted
                        .farmerAvailabilityFailure(timedout, farmerAvailabilityTimeout, thrown, callbackData);
            }
        }

        /**
         * set the thrown exception in this class and in the owner
         *
         * @param thrown the exception to record as thrown
         */
        @Override
        public void setThrown(Throwable thrown) {
            super.setThrown(thrown);
            workerThreadException = thrown;
        }

        /**
         * Get a cloned copy of the list. Why cloned? to stop problems when new hosts get added to the list
         *
         * @return a new list of hosts
         */
        public synchronized HostInstanceList getHostList() {
            return (HostInstanceList) hostList.clone();
        }

        /**
         * Process the addition of some new hosts, by appending them to the list this is synchronized
         *
         * @param newhosts new hosts to add
         */
        private synchronized void addHosts(HostInstanceList newhosts) {
            hostList.addAll(newhosts);
        }

        /**
         * Get the list of allocation Requests
         *
         * @return the request list
         */
        public List<RoleAllocationRequest> getAllocationRequests() {
            return allocationRequests;
        }

        public Object getCallbackData() {
            return callbackData;
        }
    }


}
