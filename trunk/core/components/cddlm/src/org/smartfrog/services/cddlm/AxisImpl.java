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
package org.smartfrog.services.cddlm;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.components.threadpool.ThreadPool;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.cddlm.components.CommonsLogFactory;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.rmi.RemoteException;

/**
 * Axis component. Note that SimpleAxisServer is <i>not</i> designed to be instantiated
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

public class AxisImpl extends PrimImpl implements Axis, Prim {


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
     * ctor is needed to throw an exception from a parent
     *
     * @throws RemoteException
     */

    public AxisImpl() throws RemoteException {
        DeploymentEndpoint.setOwner(this);
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
        log = CommonsLogFactory.createLog(this);
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
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
        wsddResource = sfResolve(Axis.WSDD_RESOURCE, "", true);
        threads = sfResolve(Axis.THREADS, threads, false);
        sessions = sfResolve(Axis.SESSIONS, sessions, false);
        /*
        livenessPage = sfResolve(Axis.LIVENESS_PAGE, livenessPage, false);
        liveness = new LivenessPageChecker("http", "127.0.0.1", port, livenessPage);
        liveness.setFollowRedirects(true);
        liveness.setFetchErrorText(true);
        liveness.onDeploy();
        */
        log.info("Running Axis on port " + port + " with WSDD " + wsddResource);
        log.info(" max threads=" + threads + " sessions=" + sessions);
        axis = new SimpleAxisServer(threads, sessions);
        registerResource(wsddResource);
        try {
            //register the resouce

            //if we registered, run the service
            ServerSocket ss = null;
            ss = new ServerSocket(port);
            axis.setServerSocket(ss);
            axis.start();
        } catch (SmartFrogException rethrow) {
            stopAxis();
            throw rethrow;
        } catch (Exception e) {
            stopAxis();
            //io trouble binding, axis itself being trouble
            throw new SmartFrogLifecycleException("Could not start axis", e);
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
     * Provides hook for subclasses to implement usefull termination behavior.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        assert axis != null;
        stopAxis();
    }

    /**
     * stop axis. Sets the axis property to null.
     * If there is trouble here, we log the error, instead of throwing it,
     * and set axis to null as usual.
     */
    private void stopAxis() {
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
            WSDDDeployment deployment;
            deployment = getDeployment();
            if (deployment != null) {
                wsddDoc.deploy(deployment);
            } else {
                throw new SmartFrogException("Failed to get Axis deployment system");
            }
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
     * @param resourcename
     * @throws SmartFrogException
     */
    public void registerResource(String resourcename)
            throws SmartFrogException, RemoteException {
        String targetCodeBase = (String) sfResolve(SmartFrogCoreKeys.SF_CODE_BASE);

        log.info("loading " + resourcename + " using codebase of " + targetCodeBase);
        InputStream in = SFClassLoader.getResourceAsStream(resourcename, targetCodeBase, true);
        if (in == null) {
            throw new SmartFrogException("Not found: " + resourcename);
        }
        registerStream(in);
    }

}
