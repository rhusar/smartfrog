/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.transport.DirectExecutor;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test base for remote alpine client stuff.
 */
public abstract class RemoteTestBase extends TestCase {

    protected static final Log log = LogFactory.getLog(RemoteTestBase.class);

    protected String endpoint;
    protected URL endpointURL;

    protected AlpineEPR epr;
    protected TransmitQueue queue;
    protected Executor executor;
    /**
     * timeout. {@value}
     */
    protected static final int TIMEOUT = 30000;
    protected MessageContext messageCtx;
    protected AddressDetails address;
    /**
     * path under alpine to echo {@value}
     */
    public static final String ECHO_PATH = "/echo/";

    /**
     * path under alpine to echo {@value}
     */
    public static final String WSA_PATH = "/wsa/";
    public static final String ENDPOINT = "endpoint";
    public static final int CONNECTION_TIMEOUT_MILLISECONDS = 10000;

    /**
     * Constructs a test case with the given name.
     */
    protected RemoteTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        selectEndpoint();
        executor = createExecutor();
        queue = new TransmitQueue(executor);
    }

    /**
     * ovveride point: get the endpoint and bind to it.
     *
     * @throws Exception
     */
    protected void selectEndpoint() throws Exception {
        String target = System.getProperty(ENDPOINT);
        if (target == null) {
            throw new Exception("No endpoint property '" + ENDPOINT+"'");
        }
        String path = getEndpointName();
        bindToAddress(target + path);
    }

    /**
     * bind to a target; set up the endpoint, endpointURL and epr fields from the target URL
     *
     * @param target
     * @throws MalformedURLException
     */
    protected void bindToAddress(String target) throws MalformedURLException {
        log.info("Binding to " + target);
        endpoint = target;
        endpointURL = new URL(target);
        epr = new AlpineEPR(target);
    }

    /**
     * something subclasses must return. Defaults to {@link #ECHO_PATH}
     *
     * @return the path of the actual endpoint.
     */
    protected String getEndpointName() {
        return ECHO_PATH;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public URL getEndpointURL() {
        return endpointURL;
    }

    public AlpineEPR getEpr() {
        return epr;
    }

    /**
     * create a new executor. default impl does a direct one for ease of debug
     *
     * @return
     */
    protected Executor createExecutor() {
        return new DirectExecutor();
    }

    public TransmitQueue getQueue() {
        return queue;
    }

    public Executor getExecutor() {
        return executor;
    }

    /**
     * Send a soap message (Blocking).  Wait for TIMEOUT ms before giving up
     *
     * @param messageCtx
     * @throws InterruptedException
     */
    protected Transmission send(MessageContext messageCtx)
            throws InterruptedException, ExecutionException,
            TimeoutException,
            IOException {
        return send(messageCtx, TIMEOUT);
    }

    /**
     * Send a soap message (Blocking).  Wait for TIMEOUT ms before giving up
     *
     * @param messageCtx
     * @throws InterruptedException
     */
    protected Transmission send(MessageContext messageCtx, long timeout) throws InterruptedException, TimeoutException,
            ExecutionException, IOException {
        Transmission tx = new Transmission(messageCtx);
        return send(tx, timeout);
    }

    protected Transmission send(Transmission tx, long timeout) throws
            InterruptedException, TimeoutException, IOException,
            ExecutionException {
        getQueue().transmit(tx);
        Future<?> result = tx.getResult();
        try {
            result.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            //nested ioes are rethrown
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            //runtime exceptions are stripped out and rethrown
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            //anything else is sent nested.
            throw e;
        }
        return tx;
    }

    protected void createMessageContext(String action) {
        messageCtx = new MessageContext();
        address = new AddressDetails();
        address.setTo(getEpr());
        address.setAction(action);
        messageCtx.put(HttpConnectionParams.SO_TIMEOUT,CONNECTION_TIMEOUT_MILLISECONDS);
    }
}
