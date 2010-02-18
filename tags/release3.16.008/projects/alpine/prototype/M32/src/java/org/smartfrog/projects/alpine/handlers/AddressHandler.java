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
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;

/**
 * An address handler that extracts WS-A information from the request and validates that the stuff is present
 * created 23-Mar-2006 14:50:25
 */

public class AddressHandler extends HandlerBase {

    /**
     * handle an address event
     * @param messageContext incoming message
     * @param endpointContext endpoint info
     */
    public void processMessage(MessageContext messageContext, EndpointContext endpointContext) {
        MessageDocument request = messageContext.getRequest();
        request.bindAddressing();
        request.getAddressDetails().validate();
    }
}
