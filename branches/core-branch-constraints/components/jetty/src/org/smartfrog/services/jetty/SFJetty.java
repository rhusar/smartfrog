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

import org.mortbay.http.HttpServer;
import org.mortbay.http.NCSARequestLog;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.jetty.contexts.delegates.DelegateServletContext;
import org.smartfrog.services.jetty.contexts.delegates.DelegateWebApplicationContext;
import org.smartfrog.services.www.JavaEnterpriseApplication;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.rmi.RemoteException;

/**
 * A wrapper for a Jetty http server.
 *
 * @author Ritu Sabharwal
 */

public class SFJetty extends CompoundImpl implements Compound, JettyIntf {

    protected Reference jettyhomeRef = new Reference(ATTR_JETTY_HOME);

    /**
     * Jetty home path
     */
    protected String jettyhome;


    /**
     * A jetty helper
     */
    protected JettyHelper jettyHelper = new JettyHelper(this);

    /**
     * The Http server
     */
    protected HttpServer server;

    /**
     * flag to turn logging on.
     */
    protected boolean enableLogging = false;

    protected String logDir;
    protected String logPattern;

    /**
     * log pattern.
     * {@value}
     */
    public static final String LOG_PATTERN = "yyyy_mm_dd.request.log";
    /**
     * log subdirectory.
     * {@value}
     */
    public static final String LOG_SUBDIR = "/logs/";

    /**
     * Error string raised in liveness checks.
     * {@value}
     */
    public static final String LIVENESS_ERROR_SERVER_NOT_STARTED = "Server is not started";
    public static final String ERROR_EAR_UNSUPPORTED = "Jetty does not support EAR files";


    /**
     * Standard RMI constructor
     */
    public SFJetty() throws RemoteException {
        super();
    }

    /**
     * Get the server.
     *
     * @return the server or null if not currently deployed.
     */
    public HttpServer getServer() {
        return server;
    }

    /**
     * Deploy the SFJetty component and publish the information
     *
     * @throws SmartFrogException In case of error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        try {
            super.sfDeploy();
            server = new HttpServer();
            jettyHelper.cacheJettyServer(server);
            jettyhome = sfResolve(jettyhomeRef, jettyhome, true);
            jettyHelper.cacheJettyHome(jettyhome);
            enableLogging = sfResolve(ATTR_ENABLE_LOGGING, enableLogging, true);

            if (enableLogging) {
                logDir = FileSystem.lookupAbsolutePath(this, JettyIntf.ATTR_LOGDIR, jettyhome, null, true, null);
                logPattern = sfResolve(JettyIntf.ATTR_LOGPATTERN, "", false);
                configureLogging();
            }

        } catch (Exception ex) {
            throw SmartFrogDeploymentException.forward(ex);
        }
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
        try {
            server.start();
        } catch (Exception mexp) {
            throw SmartFrogException.forward(mexp);
        }
    }

    /**
     * Configure the http server
     */
    public void configureLogging() throws SmartFrogException {
        try {
            if (enableLogging) {
                NCSARequestLog requestlog = new NCSARequestLog();
                requestlog.setFilename(logDir + File.separatorChar + logPattern);
                //commented out as this is deprecated/ignored.
                //requestlog.setBuffered(false);
                requestlog.setRetainDays(90);
                requestlog.setAppend(true);
                requestlog.setExtended(true);
                //todo: make options
                requestlog.setLogTimeZone("GMT");
                String[] paths = {"/jetty/images/*",
                        "/demo/images/*", "*.css"};
                requestlog.setIgnorePaths(paths);
                server.setRequestLog(requestlog);
            }
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    /**
     * Termination phase
     */
    public void sfTerminateWith(TerminationRecord status) {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (InterruptedException ie) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error(" Interrupted on server termination ", ie);
            }
        }
        super.sfTerminateWith(status);
    }

    /**
     * liveness test verifies the server is started
     *
     * @param source
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (server == null || !server.isStarted()) {
            throw new SmartFrogLivenessException(LIVENESS_ERROR_SERVER_NOT_STARTED);
        }
    }

    /**
     * deploy a web application.
     * Deploys a web application identified by the component passed as a parameter; a component of arbitrary
     * type but which must have the mandatory attributes identified in
     * {@link org.smartfrog.services.www.JavaWebApplication};
     * possibly even extra types required by the particular application server.
     *
     * @param webApplication the web application. this must be a component whose attributes include the
     *                       mandatory set of attributes defined for a JavaWebApplication component. Application-server specific attributes
     *                       (both mandatory and optional) are also permitted
     * @return an entry
     * @throws java.rmi.RemoteException on network trouble
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  on any other problem
     */
    public JavaWebApplication deployWebApplication(Prim webApplication)
            throws RemoteException, SmartFrogException {

        DelegateWebApplicationContext delegate = new DelegateWebApplicationContext(this, webApplication);
        delegate.deploy(webApplication);
        return delegate;
    }

    /**
     * Deploy an EAR file
     *
     * @param enterpriseApplication
     * @return an entry referring to the application
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public JavaEnterpriseApplication deployEnterpriseApplication(Prim enterpriseApplication) throws RemoteException, SmartFrogException {
        throw new SmartFrogException(ERROR_EAR_UNSUPPORTED);
    }

    /**
     * Deploy a servlet context. This can be initiated with other things
     *
     * @param servlet
     * @return a token referring to the application
     * @throws java.rmi.RemoteException on network trouble
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  on any other problem
     */
    public ServletContextIntf deployServletContext(Prim servlet) throws RemoteException, SmartFrogException {

        DelegateServletContext delegate = new DelegateServletContext(this, null);
        delegate.deploy(servlet);
        return delegate;
    }

}
