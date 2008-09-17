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
package org.smartfrog.projects.alpine.interfaces;

import org.smartfrog.projects.alpine.core.EndpointContext;

/**
 * A handler factory provides instances of handlers.
 * <p/>
 * By using the factory pattern, we can have different ways of configuring
 * handlers which is independent of the core Alpine runtime.
 * created 02-May-2006 13:24:14
 */


public interface MessageHandlerFactory {

    /**
     * Create a new handler for this context, or return an instance of an existing one. In the latter's case,
     * the handler must be thread-safe.
     *
     * @param context endpoint for which the handler is being created
     * @return the handler
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    MessageHandler createHandler(EndpointContext context);
}
