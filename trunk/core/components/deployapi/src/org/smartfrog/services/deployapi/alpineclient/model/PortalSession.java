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

import org.smartfrog.projects.alpine.transport.Session;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.services.deployapi.client.SystemEndpointer;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.system.Constants;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_ELEMENT_LOOKUPSYSTEM_REQUEST;

import java.rmi.RemoteException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.io.IOException;

import nu.xom.Element;
import nu.xom.Document;

/**
 * created 10-Apr-2006 17:07:57
 */

public class PortalSession extends SubsidiarySession {

    /**
     * Package scoped constructor.
     * @param endpoint
     * @param validating
     * @param queue
     */
    public PortalSession(AlpineEPR endpoint, boolean validating, TransmitQueue queue) {
        super(endpoint, validating,queue);
    }

    public Transmission startLookupSystem(String id) {
        Element resid = XomHelper.apiElement("ResourceId", id);
        Element request;
        request = XomHelper.apiElement(API_ELEMENT_LOOKUPSYSTEM_REQUEST, resid);
        return queue(Constants.API_PORTAL_OPERATION_LOOKUPSYSTEM,request);
    }

    public SystemSession endLookupSystem(Transmission tx) throws TimeoutException, ExecutionException, IOException,
            InterruptedException {
        Future<?> result = tx.getResult();
        MessageDocument response = tx.blockForResult(getTimeout());
        Element payload = response.getPayload();
        return new SystemSession(this,payload);
    }

}
