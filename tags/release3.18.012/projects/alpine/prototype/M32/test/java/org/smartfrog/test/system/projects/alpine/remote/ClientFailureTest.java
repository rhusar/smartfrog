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
package org.smartfrog.test.system.projects.alpine.remote;

import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.transport.http.HttpTransportFault;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;

import java.util.concurrent.ExecutionException;

/**
 * created 05-Apr-2006 13:43:08
 */

public class ClientFailureTest extends RemoteTestBase {

    public ClientFailureTest(String name) {
        super(name);
    }

    public void testBadURL() throws Throwable {
        bindToAddress("http://localhost:73");
        createMessageContext("anything");
        MessageDocument request = messageCtx.createRequest();
        request.setAddressDetails(address);
        try {
            send(messageCtx);
        } catch (AlpineRuntimeException cause) {
            if (!(cause instanceof HttpTransportFault)) {
                throw cause;
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            assertNotNull("no root cause", cause);
            if (!(cause instanceof HttpTransportFault)) {
                throw cause;
            }
        }
    }

}
