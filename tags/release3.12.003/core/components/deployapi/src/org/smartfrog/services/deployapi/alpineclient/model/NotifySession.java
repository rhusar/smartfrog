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
package org.smartfrog.services.deployapi.alpineclient.model;

import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import nu.xom.Element;

/**
 * This posts a WS-N Notification event to the queue.
 * created 05-Oct-2006 16:27:32
 */

public class NotifySession extends WsrfSession {

    /**
     * construct a new session
     *
     * @param endpoint
     * @param validating
     * @param queue
     */
    public NotifySession(AlpineEPR endpoint, boolean validating, TransmitQueue queue) {
        super(endpoint, validating, queue);
    }


    /**
     * End the subscription, extract the EPR. This is validated
     *
     * @param tx
     * @return
     */
    public MessageDocument endNotify(Transmission tx) {
        tx.blockForResult(getTimeout());
        MessageDocument response = tx.getResponse();
        return response;
    }
}
