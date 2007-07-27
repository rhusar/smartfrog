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

import nu.xom.Document;
import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.core.ContextConstants;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;

/**
 * Handle a message by logging it at the info level
 * created 10-Apr-2006 14:05:09
 */

public class LogCurrentMessageHandler extends HandlerBase {

    private static final Log log = LogFactory.getLog(LogCurrentMessageHandler.class);

    /**
     * Message handler
     *
     * @param messageContext
     * @param endpointContext
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public void processMessage(MessageContext messageContext, EndpointContext endpointContext) {
        //log the sender request
        String address = (String) messageContext.get(ContextConstants.REQUEST_REMOTE_ADDRESS);
        if (address != null) {
            log.info("Message "+
                    (messageContext.isProcessed()?"to":"from")
                    +" IP address " + address);
        }
        MessageDocument currentMessage = messageContext.getCurrentMessage();
        if (currentMessage == null) {
            log.error("There is no current message");
        } else {
            MessageDocument document = messageContext.getCurrentMessage();
            log.info(XsdUtils.printToString(document));
        }
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
        log.info("Fault thrown " + fault);
        Document doc = new Document((Element) fault.copy());
        String contents = XsdUtils.printToString(doc) + "\n";
        log.info(contents);
        return fault;
    }
}
