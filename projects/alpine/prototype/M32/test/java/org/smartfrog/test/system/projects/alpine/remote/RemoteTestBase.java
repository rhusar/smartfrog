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

import java.net.URL;
import java.net.MalformedURLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;

import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.transport.DirectExecutor;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.core.MessageContext;

/**
 * Test base for remote alpine client stuff.
 */
public abstract class RemoteTestBase extends TestCase  {

    protected String endpoint;
    protected URL endpointURL;

    protected AlpineEPR epr;
    protected TransmitQueue queue;
    protected Executor executor;
    protected static final int TIMEOUT = 30000;
    protected MessageContext messageCtx;
    protected AddressDetails address;

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
        String target =System.getProperty(getEndpointPropertyName());
        if(target ==null) {
            throw new Exception("No endpoint property "+getEndpointPropertyName());
        }

        bindToAddress(target);
        executor = createExecutor();
        queue = new TransmitQueue(executor);
    }

    /**
     * bind to a target; set up the endpoint, endpointURL and epr fields from the target URL
     * @param target
     * @throws MalformedURLException
     */
    protected void bindToAddress(String target) throws MalformedURLException {
        endpoint=target;
        endpointURL=new URL(target);
        epr=new AlpineEPR(target);
    }

    private String getEndpointPropertyName() {
        return "endpoint";
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
     * @param messageCtx
     * @throws InterruptedException
     */
    protected Transmission send(MessageContext messageCtx) throws InterruptedException, ExecutionException,
            TimeoutException {
        return send(messageCtx,TIMEOUT);
    }

    /**
     * Send a soap message (Blocking).  Wait for TIMEOUT ms before giving up
     * @param messageCtx
     * @throws InterruptedException
     */
    protected Transmission send(MessageContext messageCtx,long timeout) throws InterruptedException, TimeoutException,
            ExecutionException {
        Transmission tx=new Transmission(messageCtx);
        getQueue().transmit(tx);
        Future<?> result = tx.getResult();
        result.get(timeout, TimeUnit.MILLISECONDS);
        return tx;
    }

    protected void createMessageContext(String action) {
        messageCtx = new MessageContext();
        address = new AddressDetails();
        address.setTo(getEpr());
        address.setAction(action);
    }
}
