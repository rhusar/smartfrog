package org.smartfrog.services.deployapi.transport.endpoints;

import org.smartfrog.projects.alpine.interfaces.MessageHandler;
import org.smartfrog.projects.alpine.core.Context;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.handlers.HandlerBase;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;

/**
 * This is the WSRF message handler
 */
public class WsrfHandler extends HandlerBase implements MessageHandler {

    /**
     * Bind a handler to a context. This may include a SmartFrog binding, though
     * that depends upon the implementation
     *
     * @param context the context of the handler
     */
    public void bind(Context context) {

    }

    /**
     * Message handler
     *
     * @param messageContext
     * @param endpointContext
     *
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public void processMessage(MessageContext messageContext,
                               EndpointContext endpointContext) {
        MessageDocument request = messageContext.getRequest();
        request.getAddressDetails();
        

    }
}
