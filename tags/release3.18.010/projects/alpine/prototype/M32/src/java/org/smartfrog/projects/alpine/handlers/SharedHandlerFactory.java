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
package org.smartfrog.projects.alpine.handlers;

import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.interfaces.MessageHandler;
import org.smartfrog.projects.alpine.interfaces.MessageHandlerFactory;

/**
 * This handler returns the same shared instance for every message. It is useful
 * for shared handlers that are stateless and re-entrant.
 * created 02-May-2006 13:27:48
 */

public class SharedHandlerFactory implements MessageHandlerFactory {

    private MessageHandler sharedInstance;

    public SharedHandlerFactory(MessageHandler sharedInstance) {
        this.sharedInstance = sharedInstance;
    }

    /**
     * Create a new handler for this context, or return an instance of an existing one. In the latter's case,
     * the handler must be thread-safe.
     *
     * @param context endpoint for which the handler is being created
     * @return the handler
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public MessageHandler createHandler(EndpointContext context) {
        return sharedInstance;
    }

}
