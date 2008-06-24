package org.apache.hadoop.util;

import java.io.IOException;

/**
 *  A lifecycle for Hadoop components.
 */


public interface HadoopComponentLifecycle {

    /**
     * Initialize; read in and validate values.
     * @throws IOException for any initialisation failure
     */
    public void init() throws IOException;

    /**
     * Start any work (in separate threads)
     *
     * @throws IOException for any initialisation failure
     */
    public void start() throws IOException;

    /**
     * Ping: only valid when started.
     * @throws IOException for any ping failure
     */
    void ping() throws IOException;

    /**
     * Shut down. This must be idempotent and turn errors into log/warn events -do your best to clean up
     * even in the face of adversity.
     */
    void terminate();


    /**
     * Get the current state
     * @return the lifecycle state
     */
    State getLifecycleState();

    /**
     * The lifecycle state. Failure is a wierd one as it often takes a side effecting test (or an oursider) to observe
     */
    public enum State {
        CREATED,
        INITIALIZED,
        STARTED,
        FAILED,
        TERMINATED
    }
}
