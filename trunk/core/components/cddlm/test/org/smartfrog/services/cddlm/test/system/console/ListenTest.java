/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.cddlm.test.system.console;

import org.apache.axis.types.URI;
import org.cddlm.client.console.Listen;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleStateEnum;
import org.smartfrog.services.cddlm.generated.api.types._lifecycleEventCallbackRequest;

import java.io.IOException;

/**
 * Date: 16-Sep-2004
 * Time: 22:37:57
 */
public class ListenTest extends DeployingTestBase  {

    Listen listen;
    URI application;
    public static final int TIMEOUT_SECONDS = 60;


    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        listen=new Listen(getBinding(), getOut());
    }


    public void testListenInit() throws Exception {
        DeploymentDescriptorType dt = createSimpleDescriptor();
        URI uri = null;
        uri = deploy(dt);
        listen.setUri(uri);
        listen.setTimeout(1);
        listen.execute();
        undeploy(uri);
    }

    public void testListenUndeploy() throws Exception {
        DeploymentDescriptorType dt = createSimpleDescriptor();
        URI uri = null;
        uri = deploy(dt);
        listen.setUri(uri);
        listen.setTimeout(TIMEOUT_SECONDS);
        ListenThread listener=new ListenThread(listen);
        new Thread(listener).start();
        undeploy(uri);
        assertTrue("message received",listen.blockForMessages(TIMEOUT_SECONDS));
        _lifecycleEventCallbackRequest message=listen.getLastMessage();
        assertNotNull(message);
        ApplicationStatusType status = message.getStatus();
        assertNotNull(status);
        assertNotNull(status.getStateInfo());
        assertTrue(status.getStateInfo().indexOf(UNDEPLOY_REASON)>=0);
        assertEquals(LifecycleStateEnum.terminated, status.getState());
        assertNotNull(message.getTimestamp());
    }

    public static class ListenThread implements Runnable {
        Listen listen;
        IOException fault;

        public ListenThread(Listen listen) {
            this.listen = listen;
        }

        public void run() {
            try {
                listen.execute();
            } catch (IOException e) {
                fault=e;
            }
        }
    }
}
