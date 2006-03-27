/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.transport;

import org.smartfrog.projects.alpine.om.soap11.MessageDocument;

/**
 * This interface is for things that want to do progress displays and offer the ability for
 * end users to cancel the build. Just because operations are async does not mean
 * that you should not be able to stop them.
 *
 * Important: there are no guarantees as to what thread things are called in. however,
 * there will be no reentrant calling of these things.
 *
 * When something is cancelled, a ProgressCancelledFault is thrown up. If the operations
 * choose to throw one of their own, they get to add a meaningful message
 *
 * created 23-Mar-2006 15:55:47
 */


public interface ProgressFeedback {

    /**
     * About to start the transmission
     * @param tx transmission
     * @param message message
     * @return true if the transmission should go ahead
     */
    boolean begin(Transmission tx, MessageDocument message);

    /**
     * A timer tick
     * @param tx transmission
     * @param message message actually being sent/received
     * @param byteDone number of bytes sent/received
     * @param bytesPredicted bytes expected (or -1 for no idea whatsoever)
     * @return true if the operation should continue
     * @throws ProgressCancelledFault if you want to give a meaningful message
     */
    boolean tick(Transmission tx, MessageDocument message,long byteDone, long bytesPredicted) ;


    /**
     * Notification of end of transmission
     * @param tx transmission
     * @param message message
     * @param successful flag to mark if it was a success or not
     */
    void end(Transmission tx, MessageDocument message, boolean successful);

}
