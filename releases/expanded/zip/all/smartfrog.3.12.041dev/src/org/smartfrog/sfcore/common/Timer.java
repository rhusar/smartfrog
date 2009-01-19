/* (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.common;

/**
 * Implements a simple timer. Useful classes subclass from this and override
 * the {@link #timerTick()} method to provide functional behaviour on every tick.
 *
 */
public class Timer implements Runnable {
    /** Delay between ticks in milliseconds. */
    protected long tickDelay;

    /** Holder for thread. */
    protected Thread timerThread;

    /** Flag indicating whether this timer has been stopped. */
    protected boolean stopped = false;

    /** Flag indicating that thread is currently waiting. */
    protected boolean waiting = false;

    /** Thread name. */
    protected String name= "TimerThread";

    /**
     * Constructor.
     *
     * @param delay delay between ticks
     */
    public Timer(long delay) {
        tickDelay = delay;
    }

    /**
     * Starts the timer off (ie starts the timer thread).
     */
    public synchronized void start() {
        if (tickDelay <= 0) {
            return;
        }

        if ((timerThread != null) && timerThread.isAlive()) {
            return;
        }

        stopped = false;
        timerThread = createThread(this);
        timerThread.setName(name);
        timerThread.start();
    }

    /**
     * Stops the thread indirectly by setting the stopped flag. This prevents
     * condition where terminating timer thread terminates the thread calling
     * stop since they are the same!
     */
    public void stop() {
        stopped = true;
        reschedule();
    }

    /**
     * Reschedules the timer thread. This is only done if the timer thread is
     * currently waiting.
     */
    public void reschedule() {
        if (waiting) {
            timerThread.interrupt();
        }
    }

    /**
     * Returns the stopped flag.
     *
     * @return true if timer stopped, false if running
     */
    public boolean getStopped() {
        return stopped;
    }

    /**
     * Returns the waiting flag.
     *
     * @return true if currently waiting, false if not
     */
    public boolean getWaiting() {
        return waiting;
    }

    /**
     * Gets the tick delay for this timer.
     *
     * @return tick delay for timer
     *
     * @see #setTickDelay
     */
    public long getTickDelay() {
        return tickDelay;
    }

    /**
     * Sets the tick delay, reschedules the thread if currently waiting.
     *
     * @param delay new tick delay
     *
     * @see #getTickDelay
     */
    public void setTickDelay(long delay) {
        tickDelay = delay;
        reschedule();
    }

    /**
     * Does the actual wait.
     */
    protected synchronized void timerWait() {
        try {
            waiting = true;
            wait(tickDelay);
            waiting = false;
        } catch (InterruptedException ex) {
            // ignore
        }
    }

    /**
     * Does the tick. Will call timertick, but does the stopped check first.
     * Ignores any errors during timertick.
     */
    protected synchronized void doTick() {
        if (stopped) {
            return;
        }

        try {
            timerTick();
        } catch (Exception ex) {
            //TODO: log?
            // ignore
        }
    }

    /**
     * Creates a thread on given runnable interface. Default just creaates a
     * new thread. Subclasses can override to, for example put thread in a
     * particular thread group.
     *
     * @param run runnable object
     *
     * @return thread on given interface
     */
    protected Thread createThread(Runnable run) {
        return new Thread(run);
    }

    /**
     * Subclasses should implement this message.
     */
    protected void timerTick() {
    }

    /**
     * Thread entry point.
     */
    public void run() {
        while (!stopped) {
            timerWait();
            doTick();
        }
    }

    /**
     * Sets the name
     * @param name  String name
     */
    public void setName(String name){
        this.name=name;
    }
}
