/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */package org.smartfrog.projects.alpine.interfaces;

import org.smartfrog.projects.alpine.core.Context;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.om.soap11.Fault;

/**
 * A message handler
 */
public interface MessageHandler {

    /**
     * Bind a handler to a context. This may include a SmartFrog binding, though
     * that depends upon the implementation
     *
     * @param context the context of the handler
     */
    public void bind(Context context);

    /**
     * Message handler
     *
     * @param messageContext
     * @param endpointContext
     * @throws AlpineRuntimeException
     */
    public void processMessage(MessageContext messageContext, EndpointContext endpointContext);

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
     * @throws AlpineRuntimeException if something went wrong internally. If this happens, it is logged to the
     *                                INTERNAL_ERRORS category, and then discarded. The primary fault is what is sent up the wire, not
     *                                something that went wrong during processing.
     */
    public Fault faultRaised(MessageContext messageContext, EndpointContext endpointContext,
                             Fault fault);
}
