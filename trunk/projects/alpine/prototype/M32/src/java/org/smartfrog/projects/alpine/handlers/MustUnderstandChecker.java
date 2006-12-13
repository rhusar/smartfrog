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

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.om.soap11.Header;
import org.smartfrog.projects.alpine.faults.MustUnderstandFault;
import nu.xom.Element;

/**
 * This handler checks that at this point in the receipt, all MU attributes in all headers
 * of the inbound message are clear
 * created 22-Mar-2006 11:53:18
 */

public class MustUnderstandChecker extends HandlerBase {

    /**
    * Message handler
    *
    * @param messageContext context containing the headers and the SOAP Version
    * @param endpointContext the endpoint context
    * @throws MustUnderstandFault for a failure
    *
    */
    public void processMessage(MessageContext messageContext, EndpointContext endpointContext) {
        for(Element header:messageContext.getRequest().getEnvelope().getHeaders()) {
            checkOneElement(messageContext,header);
        }
    }

    private void checkOneElement(MessageContext messageContext,Element header) {
        if(Header.isMustUnderstand(header, messageContext.getSoapNamespace())) {
            throw new MustUnderstandFault(messageContext.getSoapNamespace(),
                    messageContext.getRole(),
                    header);
        }
    }
}
