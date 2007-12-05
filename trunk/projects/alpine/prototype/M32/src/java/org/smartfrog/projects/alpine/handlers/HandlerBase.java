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

import org.smartfrog.projects.alpine.core.Context;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.interfaces.MessageHandler;
import org.smartfrog.projects.alpine.om.soap11.Fault;

/**
 * Base for handlers
 * created 23-Mar-2006 11:45:32
 */

public abstract class HandlerBase implements MessageHandler {
    private Context context;

    /**
     * Bind a handler to a context. This may include a SmartFrog binding, though
     * that depends upon the implementation
     *
     * @param context the context of the handler
     */

    public void bind(Context context) {
        this.context = context;
    }

    /**
     * Get the context
     *
     * @return the current context
     */
    protected Context getContext() {
        return context;
    }

    /**
     * This is called for handlers when an exception gets thrown.
     * It tells them that something else in the chain faulted.
     * It is even thrown for their own {@link #processMessage} call,
     * which can be expected to set things up properly.
     * <p/>
     * It is not calles for handlers that did not (yet) get given the message to process. These
     * are never invoked if something upstream handles it.
     *
     * @param messageContext
     * @param endpointContext
     * @param fault
     * @return the same (or potentially a different) exception. This is the exception that will be passed up.
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *          if something went wrong internally. If this happens, it is logged to the
     *          INTERNAL_ERRORS category, and then discarded. The primary fault is what is sent up the wire, not
     *          something that went wrong during processing.
     */
    public Fault faultRaised(MessageContext messageContext, EndpointContext endpointContext,
                             Fault fault) {
        return fault;
    }
}
