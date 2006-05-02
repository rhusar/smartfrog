package org.smartfrog.projects.alpine.interfaces;

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
