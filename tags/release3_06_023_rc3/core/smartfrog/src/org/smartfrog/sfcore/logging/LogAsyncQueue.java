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

import java.util.LinkedList;
import java.lang.reflect.Method;


/**
 * LogAsyncQueue implements a queue of log method invocations with
 * methods to enqueue and dequeue requests. This is used by LogImplAsyncWrapper.
 */
public class LogAsyncQueue {

    private LinkedList list;

    /**
     * log method invocations are queued as LogRequest objects.
     * These can be invoked by calling the invoke() method.
     */
    protected class LogRequest {
        private LogImpl  logImpl;
        private Method   method;
        private Object[] params;
        public LogRequest(LogImpl logImpl, Method method, Object[] params) {
            this.logImpl = logImpl;
            this.method  = method;
            this.params  = params;
        }
        public void invoke() {
            logImpl.invoke(method, params);
        }
        public String toString() {
            String str = "LOG=" + logImpl.toString() + " METHOD=" + method.toString() + " PARAMS=[";
            str += params[0].toString();
            for(int i=1; i<params.length; i++)
                str += ", " + params[i].toString();
            str += "]";
            return str;
        }
    }


    /**
     * construct a queue
     */
    public LogAsyncQueue() {
        list = new LinkedList();
    }


    /**
     * Add a new method to the queue
     *
     * @param logImpl - the applicable log
     * @param method - the method to invoke
     * @param params - the parameters for the method
     */
    public synchronized void enqueueLogRequest(LogImpl logImpl, Method method, Object[] params) {
        list.addLast(new LogRequest(logImpl, method, params));
        notify();
    }


    /**
     * A blocking call to obtain the next method from the queue.
     * This method blocks if the queue is empty. Note that it can
     * return null at any time. The method should be called repeatedly
     * until a value is obtained.
     *
     * @return - the method or null
     */
    public synchronized LogRequest dequeueLogRequest() {
        if( list.isEmpty() ) {
            try { wait(); }
            catch (InterruptedException ex) { }
            return null;
        } else {
            LogRequest request = (LogRequest)list.removeFirst();
            return request;
        }
    }


}