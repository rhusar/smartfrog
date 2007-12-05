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
import org.cddlm.client.callbacks.CallbackServer;
import org.cddlm.client.common.ServerBinding;
import org.cddlm.client.console.Listen;
import org.smartfrog.services.cddlm.generated.api.callbacks.DeploymentNotificationSoapBindingStub;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleEventRequest;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleStateEnum;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Date: 16-Sep-2004 Time: 22:37:57
 */
public class ListenTest extends DeployingTestBase {

    Listen listen;
    URI application;
    public static final int TIMEOUT_SECONDS = 60;


    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        listen = new Listen(getBinding(), getOut());
    }


    public void testServer() throws Exception {
        CallbackServer server = new CallbackServer();
        server.start();
        String url = server.getCallbackURL();
        try {
            assertPageExists(url);
            assertPageExists(url + "?wsdl");
        } finally {
            server.stop();
        }
    }

    public void testUnboundLoopback() throws Exception {
        CallbackServer server = new CallbackServer();
        server.start();
        String url = server.getCallbackURL();
        try {
            DeploymentNotificationSoapBindingStub callback = new DeploymentNotificationSoapBindingStub(
                    new URL(url), null);
            callback.setTimeout(2 * 60 * 1000);
            LifecycleEventRequest message;
            message = new LifecycleEventRequest();
            boolean result = callback.notification(message);
            assertFalse("Expected failure", result);
        } finally {
            server.stop();
        }
    }

    public void testBoundLoopback() throws Exception {
        CallbackServer server = new CallbackServer();
        server.start();
        String url = server.getCallbackURL();
        String key = CallbackServer.addMapping(listen);
        try {
            DeploymentNotificationSoapBindingStub callback = new DeploymentNotificationSoapBindingStub(
                    new URL(url), null);
            callback.setTimeout(2 * 60 * 1000);
            LifecycleEventRequest message;
            message = new LifecycleEventRequest();
            message.setIdentifier(key);
            boolean result = callback.notification(message);
            assertTrue("Expected successful dispatch", result);
            assertTrue("Expected a received message",
                    listen.getMessageCount() > 0);
        } finally {
            server.stop();
        }
    }


    private void assertPageExists(String remoteURL) throws IOException {
        URL source = new URL(remoteURL);
        URLConnection connection = source.openConnection();
        connection.connect();
        if (connection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int response = httpConnection.getResponseCode();
            assertEquals("Got error code " + response + " at " + remoteURL,
                    200, response);
        }
    }

    public void NotestListenInit() throws Exception {
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
        listen = new UndeployingListener(getBinding(), getOut(), uri);
        listen.setTimeout(TIMEOUT_SECONDS);
        listen.execute();
        assertTrue("message received", listen.getMessageCount() > 0);
        assertTerminationMessageReceived();
    }

    private void assertTerminationMessageReceived() {
        LifecycleEventRequest message = listen.getLastMessage();
        assertNotNull(message);
        ApplicationStatusType status = message.getStatus();
        assertNotNull(status);
        assertNotNull(status.getStateInfo());
        assertTrue(status.getStateInfo().indexOf(UNDEPLOY_REASON) >= 0);
        assertEquals(LifecycleStateEnum.terminated, status.getState());
        assertNotNull(message.getTimestamp());
    }

    /**
     * thread that executes listener in a separate thread from the rest of the
     * test
     */
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
                fault = e;
            }
        }
    }


    public static class UndeployingListener extends Listen {


        public UndeployingListener(ServerBinding binding,
                PrintWriter out,
                URI uri) {
            super(binding, out);
            setUri(uri);
        }

        /**
         * after starting the server, but before waiting, we begin to wait
         *
         * @throws IOException
         */
        protected void aboutToWait() throws IOException {
            ApplicationStatusType status = lookupApplicationStatus(uri);
            terminate(uri, UNDEPLOY_REASON);
        }

        /**
         * we only wait for one message, not an indefinate amount
         *
         * @param millis
         * @throws InterruptedException
         */
        protected void sleep(int millis) throws InterruptedException {
            blockForMessages(millis);
        }

    }
}
