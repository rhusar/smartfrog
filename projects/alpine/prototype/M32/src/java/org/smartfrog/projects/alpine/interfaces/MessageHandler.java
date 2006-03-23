package org.smartfrog.projects.alpine.interfaces;

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.core.Context;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;

/**
 * A message handler
 */
public interface MessageHandler {

    /**
     * Bind a handler to a context. This may include a SmartFrog binding, though
     * that depends upon the implementation
     * @param context the context of the handler
     */
    public void bind(Context context) ;

    /**
     * Message handler
     * @param messageContext
     * @param endpointContext
     * @throws AlpineRuntimeException
     */ 
    public void processMessage(MessageContext messageContext,EndpointContext endpointContext) ;
}
