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
     * @return An Object corresponding to the Runnable
     */
    public Object addToQueue(Runnable run);

    /**
     * Remove a runnable from the registered Runnable jobs
     *
     * @param run the task to remove
     * @return true if successful, fales if it did not exist or was already allocated
     */
    public boolean removeFromQueue(Object task);
}
