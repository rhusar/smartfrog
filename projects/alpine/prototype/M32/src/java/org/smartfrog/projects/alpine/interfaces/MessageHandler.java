package org.smartfrog.projects.alpine.interfaces;

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;

/**
 * A message handler
 */
public interface MessageHandler {
    
    /**
     * Message handler
     * @param messageContext
     * @param endpointContext
     * @throws AlpineRuntimeException
     */ 
    public void processMessage(MessageContext messageContext,EndpointContext endpointContext) ;
}
