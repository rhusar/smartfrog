package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

/**
 * a request to allocate machines a role. It also holds the request status/outcome
 */
public class RoleAllocationRequest {
    public String role;
    public int currentCount;
    public int min;
    public int max;
    private volatile boolean started;
    private volatile boolean finished;
    private volatile long startTime;
    private volatile long finishTime;

    /**
     * any exception
     */
    private volatile Throwable thrown;
    /**
     * hosts allocated
     */
    private HostInstanceList hosts;
    private Object callbackData;

    public RoleAllocationRequest(String role,
                                 int currentCount,
                                 int min,
                                 int max,
                                 Object callbackData) {
        this.role = role;
        this.currentCount = currentCount;
        this.min = min;
        this.max = max;
        this.callbackData = callbackData;
    }

    @Override
    public String toString() {
        return
                "role " + role +
                        " range [" + min +
                        " -" + max +
                        "]"
                        + (thrown != null ? (", thrown=" + thrown) : "")
                        + (hosts != null ? (", hosts=" + hosts) : "");
    }

    public String getRole() {
        return role;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    public Throwable getThrown() {
        return thrown;
    }

    /**
     * Log that the request has started
     */
    void requestStarted() {
        started = true;
        startTime = System.currentTimeMillis();
    }

    /**
     * Log that the request succeeded, here are the hosts
     *
     * @param hostList list of hosts
     */
    synchronized void requestSucceeded(HostInstanceList hostList) {
        finished();
        hosts = hostList;
    }

    private void finished() {
        finished = true;
        finishTime = System.currentTimeMillis();
    }


    /**
     * Log that the request failed
     *
     * @param cause the cause of the failure
     */
    synchronized void requestFailed(Throwable cause) {
        finished();
        thrown = cause;
    }

    /**
     * Get a list of hosts, will be null if the request has not finished
     *
     * @return the host list
     */
    public HostInstanceList getHosts() {
        return hosts;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }
}
