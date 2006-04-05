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

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.transport.http.HttpTransportFault;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


/**
 * Tests that the echo testpoint is functional. This uses the client API, so tests that too
 * created 05-Apr-2006 12:08:01
 */

public class EchoTest extends RemoteTestBase {

    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        createMessageContext("echo");
    }

    public EchoTest(String name) {
        super(name);
    }

    public void testEmptyEcho() throws Throwable {
        MessageDocument request = messageCtx.createRequest();
        request.setAddressDetails(address);
        send(messageCtx);
    }

}
