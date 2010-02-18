package org.smartfrog.services.xunit.utils;

/**
 * This only interrupts a thread; it does nothing to kill a running thread.
 */
public class InterruptingWatchdogTimer implements Runnable {

    private Thread target;

    private long timeout;

    private volatile boolean didInterrupt=false;
    private Thread watcher;

    public InterruptingWatchdogTimer(Thread target, long timeout) {
        this.target = target;
        this.timeout = timeout;
    }

    public InterruptingWatchdogTimer(long timeout) {
        target = Thread.currentThread();
        this.timeout = timeout;
    }

    public synchronized boolean beginWatch() {
        if(watcher!=null) {
            return false;
        }
        watcher = new Thread(this);
        watcher.start();
        return true;
    }

    public synchronized void endWatch() {
        try {
            if(watcher!=null) {
                watcher.interrupt();
            }
        } finally {
            watcher=null;
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take
     * any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        try {
            Thread.sleep(timeout);
            //ok, time to wake the target.
            if(target.isAlive()) {
                target.interrupt();
            }
            didInterrupt=true;
        } catch (InterruptedException ignored) {

        }
    }

    public boolean isDidInterrupt() {
        return didInterrupt;
    }

}
