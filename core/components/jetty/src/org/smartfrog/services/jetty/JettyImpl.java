/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jetty;

import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.thread.BoundedThreadPool;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.jetty.contexts.delegates.DelegateServletContext;
import org.smartfrog.services.jetty.contexts.delegates.DelegateWebApplicationContext;
import org.smartfrog.services.www.JavaEnterpriseApplication;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * A wrapper for a Jetty http server.
 *
 * @author Ritu Sabharwal
 */

public class JettyImpl extends PrimImpl implements JettyIntf {

    private final Reference jettyhomeRef = new Reference(ATTR_JETTY_HOME);

    /** Jetty home path */
    private String jettyHome;


    /** A jetty helper */
    private JettyHelper jettyHelper = new JettyHelper(this);

    /** The Http server */
    private Server server;
    private JettyToSFLifecycle<Server> serverBridge = new JettyToSFLifecycle<Server>(SERVER, null);

    /** log pattern. {@value} */
    public static final String LOG_PATTERN = "yyyy_mm_dd.request.log";
    /** log subdirectory. {@value} */
    public static final String LOG_SUBDIR = "/logs/";

    /** Error string raised when EARs are deployed {@value} */
    public static final String ERROR_EAR_UNSUPPORTED = "Jetty does not support EAR files";
    private static final String SERVER = "server";


    /**
     * Standard RMI constructor
     *
     * @throws RemoteException In case of network/rmi error
     */

    public JettyImpl() throws RemoteException {
    }

    /**
     * Get the server.
     *
     * @return the server or null if not currently deployed.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Configure and deploy the Jetty component
     *
     * There's a good example on the mortbay site
     * on how to set Jetty up to match the base configuration; what we have here is not that dissimilar, only
     * configurable via .sf files.
     * @see  <a href="http://jetty.mortbay.org/xref/org/mortbay/jetty/example/LikeJettyXml.html">Example</a>
     * @throws SmartFrogException In case of error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        //create the server and store in in our bridge
        server = new Server();
        serverBridge = new JettyToSFLifecycle<Server>(SERVER, server);
        server.setStopAtShutdown(sfResolve(ATTR_STOP_AT_SHUTDOWN, false, true));

        //create the pool
        BoundedThreadPool pool = new BoundedThreadPool();
        pool.setMaxThreads(sfResolve(ATTR_MAXTHREADS, 0, true));
        pool.setMinThreads(sfResolve(ATTR_MINTHREADS, 0, true));
        pool.setMaxIdleTimeMs(sfResolve(ATTR_MAXIDLETIME, 0, true));
        server.setThreadPool(pool);

        //tune the response policy
        server.setSendServerVersion(sfResolve(ATTR_SEND_SERVER_VERSION, false, true));
        server.setSendDateHeader(sfResolve(ATTR_SEND_DATE_HEADER, false, true));


        //set the jetty helper up
        jettyHelper.cacheJettyServer(server);
        jettyHome = sfResolve(jettyhomeRef, jettyHome, true);
        jettyHelper.cacheJettyHome(jettyHome);


        //this holds all the server contexts
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        //now look at logging; add one if needed
        RequestLogHandler logHandler=null;
        if (sfResolve(ATTR_ENABLE_LOGGING, false, true)) {
            logHandler = configureLogging();
        }

        //the 404 handler has to come after the contexts
        DefaultHandler raise404 = new DefaultHandler();

        Handler[] handlerArray;
        if(logHandler!=null) {
            handlerArray=new Handler[] {contexts, raise404,logHandler};
        } else {
            handlerArray = new Handler[]{contexts, raise404};
        }
        server.setHandlers(handlerArray);
    }


    /**
     * sfStart: starts Jetty Http server.
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        serverBridge.start();
/*
        try {
            serverBridge.getLifecycle().join();
        } catch (InterruptedException e) {
            throw SmartFrogException.forward("Failed to start "+serverBridge,e);
        }
*/
    }



    /**
     * Create a log handler...this is not bound to the server yet
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     * @return the log handler
     */
    public RequestLogHandler configureLogging() throws SmartFrogException, RemoteException {
        String logDir = FileSystem.lookupAbsolutePath(this, JettyIntf.ATTR_LOGDIR, jettyHome, null, true, null);
        String logPattern = sfResolve(JettyIntf.ATTR_LOGPATTERN, "", true);

        NCSARequestLog requestlog = new NCSARequestLog();
        requestlog.setFilename(logDir + File.separatorChar + logPattern);
        //commented out as this is deprecated/ignored.
        requestlog.setRetainDays(sfResolve(ATTR_LOG_KEEP_DAYS,0,true));
        requestlog.setAppend(sfResolve(ATTR_LOG_APPEND, false, true));
        requestlog.setExtended(sfResolve(ATTR_LOG_APPEND, false, true));
        requestlog.setLogTimeZone(sfResolve(ATTR_LOG_TZ, "", true));
        Vector pathV = null;
        pathV = sfResolve(ATTR_LOGIGNOREPATHS, pathV, true);
        String[] paths = new String[pathV.size()];
        int counter = 0;
        for (Object path : pathV) {
            String pathValue = path.toString();
            paths[counter++] = pathValue;
            sfLog().info("Ignoring path " + pathValue);
        }

        requestlog.setIgnorePaths(paths);
        //bind the log to the server by way of a handler
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestlog);
        return requestLogHandler;
    }

    /**
     * Termination phase.
     * Shut down the server, logging any errors that happen on the way
     * */
    public synchronized void sfTerminateWith(TerminationRecord status) {

        try {
            serverBridge.stop();
        } catch (InterruptedException ie) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Interrupted on server termination ", ie);
            }
        } catch (Exception ie) {
            sfLog().error("while terminating Jetty server", ie);
        }
        super.sfTerminateWith(status);
    }

    /**
     * liveness test verifies the server is started
     *
     * @param source caller
     * @throws SmartFrogLivenessException the server is  not started
     * @throws RemoteException            network trouble
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        serverBridge.sfPing(source);
    }

    /**
     * deploy a web application. Deploys a web application identified by the component passed as a parameter; a
     * component of arbitrary type but which must have the mandatory attributes identified in {@link
     * org.smartfrog.services.www.JavaWebApplication}; possibly even extra types required by the particular application
     * server.
     *
     * @param webApplication the web application. this must be a component whose attributes include the mandatory set of
     *                       attributes defined for a JavaWebApplication component. Application-server specific
     *                       attributes (both mandatory and optional) are also permitted
     * @return an entry
     * @throws SmartFrogException errors thrown by the delegate
     * @throws RemoteException    network trouble
     */
    public JavaWebApplication deployWebApplication(Prim webApplication)
            throws RemoteException, SmartFrogException {

        DelegateWebApplicationContext delegate = new DelegateWebApplicationContext(this, webApplication);
        delegate.deploy(webApplication);
        return delegate;
    }

    /**
     * Deploy an EAR file -not supported
     *
     * @param enterpriseApplication the application
     * @return an entry referring to the application
     * @throws SmartFrogException always
     * @throws RemoteException    network trouble
     */
    public JavaEnterpriseApplication deployEnterpriseApplication(Prim enterpriseApplication)
            throws RemoteException, SmartFrogException {
        throw new SmartFrogException(ERROR_EAR_UNSUPPORTED);
    }

    /**
     * Deploy a servlet context. This can be initiated with other things
     *
     * @param servletContext the servlet context
     * @return a token referring to the application
     * @throws RemoteException    network trouble
     * @throws SmartFrogException on any other problem
     */
    public ServletContextIntf deployServletContext(Prim servletContext) throws RemoteException, SmartFrogException {

        DelegateServletContext delegate = new DelegateServletContext(this, null,servletContext);
        delegate.deploy();
        return delegate;
    }

}
