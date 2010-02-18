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


package org.cddlm.client.callbacks;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cddlm.generated.api.callbacks.DeploymentNotificationEndpoint;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleEventRequest;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Date: 16-Sep-2004 Time: 20:40:23
 */
public class CallbackServer {

    private static Log log = LogFactory.getLog(CallbackServer.class);

    public static final String ENDPOINT_PATH_ON_SERVER = "/axis/services/callbacks";

    public CallbackServer() {
    }

    public CallbackServer(int port, String wsddResource) {
        this.port = port;
        this.wsddResource = wsddResource;
    }

    /**
     * our axis server. This will contain a pool of threads to service requests
     */
    private SimpleAxisServer axis;

    private static Hashtable mapping = new Hashtable();

    ServerSocket serverSocket = null;


    /**
     * name of a WSDDFile to use
     */
    private String wsddResource = CALLBACK_DEPLOYMENT_DESCRIPTOR;

    public static final int CALLBACK_PORT = 5051;

    public static final String CALLBACK_DEPLOYMENT_DESCRIPTOR = "org/cddlm/client/callbacks/deploy.wsdd";
    /**
     * port to listen to
     */
    private int port = CALLBACK_PORT;

    private int threads = 4;

    public synchronized void start() throws Exception {
        assert axis == null;
        axis = new SimpleAxisServer(threads);
        //run the service
        serverSocket = new ServerSocket(port);
        axis.setServerSocket(serverSocket);
        axis.start();
        if (wsddResource != null) {
            registerResource(wsddResource);
        }
    }

    public synchronized void stop() {
        if (axis != null) {
            axis.stop();
            axis = null;
        }
    }


    /**
     * register an open stream, which we close afterwards
     *
     * @param instream
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     *
     * @throws IOException
     */
    public void registerStream(InputStream instream) throws SAXException,
            ParserConfigurationException, IOException {
        try {
            Document doc = XMLUtils.newDocument(instream);
            WSDDDocument wsddDoc = new WSDDDocument(doc);
            WSDDDeployment deployment;
            deployment = getDeployment();
            if (deployment != null) {
                wsddDoc.deploy(deployment);
            }
        } finally {
            instream.close();
        }
    }

    /**
     * register a resource
     *
     * @param resourcename
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public void registerResource(String resourcename)
            throws SAXException, ParserConfigurationException, IOException {
        log.info("registering resource " + wsddResource);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(
                resourcename);
        if (in == null) {
            throw new FileNotFoundException(resourcename);
        }
        registerStream(in);
    }

    /**
     * get deployment
     *
     * @return
     * @throws org.apache.axis.AxisFault
     */
    private WSDDDeployment getDeployment() throws AxisFault {
        WSDDDeployment deployment;
        AxisEngine engine = axis.getAxisServer();
        EngineConfiguration config = engine.getConfig();
        if (config instanceof WSDDEngineConfiguration) {
            deployment = ((WSDDEngineConfiguration) config).getDeployment();
        } else {
            deployment = null;
        }
        return deployment;
    }

    /**
     * creates a mapping, returns an identifier for use in lookup calls
     *
     * @return
     */
    public static String addMapping(DeploymentNotificationEndpoint callback) {
        String key = makeKey(callback);
        mapping.put(key, callback);
        return key;
    }

    //counter used for unique keys
    private static int counter = 0;

    private static synchronized String makeKey(
            DeploymentNotificationEndpoint callback) {
        counter++;
        String key = counter + "/" + callback.hashCode();
        return key;
    }

    public static boolean removeMapping(String key) {
        return mapping.remove(key) != null;
    }


    /**
     * handle a callback message by mapping it to an instance and dispatching.
     *
     * @param callback
     * @return false if the lookup failed, otherwise whatever the dispatched
     *         method wanted.
     * @throws RemoteException
     */
    public static boolean processCallback(LifecycleEventRequest callback)
            throws RemoteException {
        String key = callback.getIdentifier();
        if (key == null) {
            return false;
        }
        DeploymentNotificationEndpoint handler = (DeploymentNotificationEndpoint) mapping.get(
                key);
        if (handler == null) {
            return false;
        }
        return handler.notification(callback);
    }

    public String getCallbackURL() {
        assert serverSocket != null;
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        // serverSocket.getInetAddress().getHostName();
        assert hostName != null;
        String url = "http://" +
                hostName +
                ":" +
                port +
                ENDPOINT_PATH_ON_SERVER;
        return url;
    }
}
