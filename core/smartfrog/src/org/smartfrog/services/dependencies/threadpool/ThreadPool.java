/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.dependencies.threadpool;

import java.util.concurrent.Future;

import org.smartfrog.services.orchcomponent.model.OrchComponentModel;

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
    public Future<?> addToQueue(Runnable run);
    
    //public Future<?> removeFromQueue(Future<?> task);
    
}
