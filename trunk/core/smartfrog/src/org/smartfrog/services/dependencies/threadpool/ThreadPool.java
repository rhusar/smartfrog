package org.smartfrog.services.dependencies.threadpool;

/**
 * Interface to a threadpool object
 * Provides the methods of the a threadpool object.
 * <p/>
 * This includes a methods to register and deregister an object implementing the Runnable interface.
 * Registring an onbject causes it to be placed in a queue of obejcts to be run by a thread when
 * one is available.
 * <p/>
 * The interface also provides methods to query aspects such as the length of the queue, the number
 * of threads currently owned by the pool, and the number (if any) currently used or free.
 * <p/>
 * The interface also provides methods to set the minimum number of free threads to be kept
 * and the maximum number of threads that may be owned by the threadpool.
 */
public interface ThreadPool {
    /**
     * register a Runnable to be allocated a thread
     *
     * @param run The instances of a Runnable
     */
    public void addToQueue(Runnable run);

    /**
     * Remove a runnable from the registered Runnable jobs
     *
     * @param run the Runnable to remove
     * @return true if successful, fales if it did not exist or was already allocated
     */
    public boolean removeFromQueue(Runnable run);

    /**
     * get the length of the currently unallocated Runnables
     *
     * @return the queue length
     */
    public int queueLength();

    /**
     * Obtan the number of threads owned by the pool
     *
     * @return the number of threads
     */
    public int threads();

    /**
     * Obtain the number of free threads
     *
     * @return the number of free threads
     */
    public int threadsFree();

    /**
     * Get the setting for the maximum number of threads that this pool may allocated
     *
     * @return the number of threads
     */
    public int getMaxThreads();

    /**
     * Get the maximum number of free threads that this threadpool can keep
     *
     * @return the number of threads
     */
    public int getMaxFreeThreads();
}
