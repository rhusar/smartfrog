/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.logging;


/**
 * LogAsyncThread implements the worker thread that invokes methods
 * queued in a LogAsyncQueue. This is used by LogImplAsyncWrapper.
 */
public class LogAsyncThread extends Thread {

    private boolean                  active;
    private LogAsyncQueue.LogRequest request;
    private LogAsyncQueue            queue;

    /**
     * Constructor
     * @param queue queue of methods to be invoked
     */
    public LogAsyncThread(LogAsyncQueue queue) {
        super();
        this.queue = queue;
    }

    /**
     * Run method for the thread
     */
    public void run() {
        active = true;
        while( active ) {
            request = queue.dequeueLogRequest();
            if( request != null ) {
                try {
                    request.invoke();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /**
     * Terminate the thread
     */
    public void terminate() {
        active = false;
        this.interrupt();
    }
}