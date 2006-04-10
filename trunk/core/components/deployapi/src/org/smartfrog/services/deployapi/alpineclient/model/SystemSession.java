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
package org.smartfrog.services.deployapi.alpineclient.model;

import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.transport.Session;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.binding.EprHelper;
import org.smartfrog.services.deployapi.system.Constants;
import org.apache.axis2.AxisFault;
import nu.xom.Element;
import nu.xom.Document;

/**
 * created 10-Apr-2006 17:08:08
 */

public class SystemSession extends SubsidiarySession {

    public SystemSession(AlpineEPR endpoint, boolean validating, TransmitQueue queue) {
        super(endpoint, validating, queue);
    }

    /**
     * Build a session from a returned reference
     * @param parent
     * @param root
     */
    public SystemSession(Session parent, Element root) {
        super(null, parent.isValidating(), parent.getQueue());
        String cachedResourceId = XomHelper.getElementValue(root,
                "api:ResourceId");
        Element address = XomHelper.getElement(root,
                "api:systemReference");
        AlpineEPR epr = new AlpineEPR(address, Constants.WS_ADDRESSING_NAMESPACE);
        setEndpoint(epr);
    }
}
}
