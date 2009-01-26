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

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;

/**
 * created 23-Mar-2006 16:23:07
 */

public class BaseProgress implements ProgressFeedback {

    /**
     * About to start the transmission
     *
     * @param tx      transmission
     * @param message message
     * @return true if the transmission should go ahead
     */
    public boolean begin(Transmission tx, MessageDocument message) {
        return true;
    }

    /**
     * A timer tick
     *
     * @param tx             transmission
     * @param message        message actually being sent/received
     * @param byteDone       number of bytes sent/received
     * @param bytesPredicted bytes expected (or -1 for no idea whatsoever)
     * @return true if the operation should continue
     */
    public boolean tick(Transmission tx, MessageDocument message, long byteDone, long bytesPredicted) {
        return true;
    }

    /**
     * Notification of end of transmission
     *
     * @param tx         transmission
     * @param message    message
     * @param successful flag to mark if it was a success or not
     */
    public void end(Transmission tx, MessageDocument message, boolean successful) {

    }

    /**
     * public static instance for dropping all feedback
     */
    public static final ProgressFeedback EMPTY_PROGRESS=new BaseProgress();
}
