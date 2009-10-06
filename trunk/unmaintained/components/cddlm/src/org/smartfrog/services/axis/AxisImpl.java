/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.axis;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.components.threadpool.ThreadPool;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.axis.utils.XMLUtils;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.rmi.RemoteException;

/**
 * The Axis component creates an instance of {@link SimpleAxisServer}, which
 * starts the default services, especially something that listens for
 * new deployment requests.
 *  Note that SimpleAxisServer is <i>not</i> designed to be instantiated
 * in multiple isolated instances in the same VM, as it uses static structures for
 * its axis server, thread pool, etc. It is not even clear that Axis is designed to run
 * multiple times in the same JVM/classloader.
 * <p/>
 * It may look like there is Apache code in here, from Axis' AutoRegisterServlet, but
 * that is because I wrote the Apache servlet; the registration code is pasted in from
 * the program I wrote from which I later extracted the AutoRegisterServlet.
 * <p/>
 * created 02-Mar-2004 17:28:31
 */

public class AxisImpl extends PrimImpl implements Axis {


    /**
     * our axis server. This will contain a pool of threads to service requests
     */
    private SimpleAxisServer axis;

    /**
     * name of a WSDDFile to use
     */
    private String wsddResource;

    /**
     * port to listen to
     */
    private int port = 8080;

    /**
     * our log
     */
    private Log log;

    /**
     * max number of threads
     */
    private int threads = ThreadPool.DEFAULT_MAX_THREADS;

    /**
     * max no. of sessions
     */
    private int sessions = SimpleAxisServer.MAX_SESSIONS_DEFAULT;
    /**
     * default path to services
     */
    protected static final String DEFAULT_AXIS_SERVICE_PATH = "/axis/services/";


    /**
     * ctor is needed to throw an exception from a parent
     *
     * @throws RemoteException
     */

    public AxisImpl() throws RemoteException {
        SmartFrogHostedEndpoint.setOwner(this);
    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = sfGetApplicationLog();
    }

    /**
     * Start deployment. we deploy before calling our parent, because
     * we want to
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        assert log != null;
        assert axis == null;
        //get stuff from the configuration
        port = sfResolve(Axis.PORT, 8080, false);
        threads = sfResolve(Axis.THREADS, threads, false);
        sessions = sfResolve(Axis.SESSIONS, sessions, false);
        log.info("Running Axis on port " + port);
        log.info(" max threads=" + threads + " sessions=" + sessions);
        axis = new SimpleAxisServer(threads, sessions);
        String servicePath = DEFAULT_AXIS_SERVICE_PATH;
        sfReplaceAttribute(Axis.SERVICE_PATH, servicePath);

        try {
            //register the resouce
            wsddResource = sfResolve(Axis.WSDD_RESOURCE, (String) null, false);
            if (wsddResource != null) {
                log.info("registering WSDD " + wsddResource);
                registerResource(wsddResource);
            }

            //run the service
            ServerSocket ss = null;
            ss = new ServerSocket(port);
            axis.setServerSocket(ss);
            axis.start();

        } catch (Exception e) {
            log.error("when stating the server",e);
            stopAxis();
            //io trouble binding, axis itself being trouble
            throw SmartFrogLifecycleException.forward(e);
        }
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *          component is terminated
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
    }

    /**
     * shut down axis
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        stopAxis();
    }

    /**
     * stop axis. Sets the axis property to null.
     * If there is trouble here, we log the error, instead of throwing it,
     * and set axis to null as usual.
     */
    private synchronized void stopAxis() {
        if (axis == null) {
            return;
        }
        try {
            log.info("stopping axis");
            axis.stop();
        } catch (Exception e) {
            //we cannot raise trouble on shutdown, but we can log it.
            log.error("while stopping axis", e);
        } finally {
            axis = null;
        }
    }


    /**
     * get deployment from the server
     *
     * @return
     */
    private WSDDDeployment getDeployment() {
        assert axis != null;
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
     * register an open stream, which we close afterwards
     *
     * @param instream
     * @throws SmartFrogException
     */
    public void registerStream(InputStream instream) throws SmartFrogException {
        try {
            Document doc = XMLUtils.newDocument(instream);
            WSDDDocument wsddDoc = new WSDDDocument(doc);
            deployOrUndeploy(wsddDoc);
        } catch (Exception e) {
            throw SmartFrogException.forward(e);
        } finally {
            try {
                instream.close();
            } catch (IOException ignored) {
                ignore(ignored);
            }
        }
    }

    /**
     * deploy or undeploy a WSDD doc to a local JVM
     *
     * @param wsddDoc
     * @throws SmartFrogException if anything went wrong
     */
    private void deployOrUndeploy(WSDDDocument wsddDoc) throws ConfigurationException, SmartFrogException {
        try {
            WSDDDeployment deployment;
            deployment = getDeployment();
            if (deployment != null) {
                wsddDoc.deploy(deployment);
            } else {
                throw new SmartFrogException("Failed to get Axis deployment system");
            }
        } catch (ConfigurationException e) {
            throw SmartFrogException.forward(e);
        }
    }

    /**
     * ignore any exception, but log it at a low level
     *
     * @param ignored what to ignore
     */
    private void ignore(Exception ignored) {
        if (log.isDebugEnabled()) {
            log.debug("ignoring ", ignored);
        }
    }

    /**
     * register a resource
     *
     * @param resourcename name of resource on the classpath
     * @throws SmartFrogException
     */
    public void registerResource(String resourcename)
            throws SmartFrogException, RemoteException {
        ComponentHelper helper = new ComponentHelper(this);
        registerStream(helper.loadResource(resourcename));
    }

    /**
     * parse the string, which must contain a valid XML document,
     * and then register it, an action which can cover deploy/and or undeploy,
     * depending on the payload.
     * Because the JDOM classes serialize, this is a remotable interface
     *
     * @param wsdd
     */
    /*
    public void registerWSDDDocument(Document wsdd) throws SmartFrogException {
        DOMOutputter outputter = new DOMOutputter();
        try {
            Document DomDoc = outputter.output(wsdd);
            WSDDDocument wsddDoc = new WSDDDocument(DomDoc);
            deployOrUndeploy(wsddDoc);
        } catch (JDOMException e) {
            throw SmartFrogException.forward(e);
        } catch (ConfigurationException e) {
            throw SmartFrogException.forward(e);
        }
    }
    */
    /**
     * this is an override point for subcomponents. The usual aim is to check the
     * type of parts being deployed, and so be fussier than a classic Compound instance.
     * If an exception is thrown in this method, it terminates deployment of the parent, so do
     * not use lightly. To skip deployment of a component, just return false.
     * <p/>
     * For example, to not deploy any children from an enumeration of nested components, return false from
     * all queries.
     * <p/>
     * The base implementation of this method always returns true: deploy all components
     *
     * @param key       the attribute name of the component
     * @param component what is to deploy
     * @return true if this component is to be deployed, false if not, an exception for noisy failure
     * @throws org.smartfrog.sfcore.common.SmartFrogDeploymentException
     *
     * @throws java.rmi.RemoteException
     */
    /*
    protected boolean sfTestForDeployment(Object key, ComponentDescription component)
            throws SmartFrogDeploymentException, RemoteException {
        if(component instanceof AxisService) {
            return true;
        } else {
            log.warn("Ignoring child component "+key);
            return false;
        }
    }
    */
}
