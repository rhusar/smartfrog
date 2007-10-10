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
package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.servlet.Context;
import org.smartfrog.services.jetty.JettyImpl;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.rmi.RemoteException;

/**
 * Abstract class for representing a delegated context
 */
public abstract class DelegateApplicationContext
        implements ApplicationServerContext {
    public static final String ERROR_NULL_CONTEXT = "Null context";
    public static final String ERROR_NOT_RUNNING = "Not started";

    /**
     *
     * @param server jetty sever
     * @param context context
     */
    protected DelegateApplicationContext(JettyImpl server, Context context) {
        this.server = server;
        this.context = context;
    }

    protected DelegateApplicationContext() {
    }

    /**
     * the server
     */
    private JettyImpl server;

    /**
     * The actual context
     */
    private Context context;


    /**
     * Get the server that created this
     *
     * @return the server
     */
    public JettyImpl getServer() {
        return server;
    }

    /**
     * Get the context
     *
     * @return the context; will be null if not running
     */
    public Context getContext() {
        return context;
    }

    /**
     * set the context
     *
     * @param context the jetty context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Do nothing in our deploy operation
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void deploy() throws SmartFrogException, RemoteException {

    }

    /**
     * start: deploy this context
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public void start() throws SmartFrogException, RemoteException {
        if (context != null) {
            getServer().getServer().addHandler(context);
            try {
                context.start();
            } catch (RemoteException ex) {
                throw ex;
            } catch (Exception ex) {
                throw SmartFrogException.forward(ex);
            }
        }
    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException In case of liveness failure
     * @throws RemoteException    In case of network/rmi error
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {
        if (context == null) {
            throw new SmartFrogLivenessException(ERROR_NULL_CONTEXT);
        }
        if (!context.isStarted()) {
            throw new SmartFrogLivenessException(ERROR_NOT_RUNNING);
        }
    }

    /**
     * undeploy a context.
     * If the server is already stopped, this the
     * undeployment is skipped without an error. The context field is
     * set to null, to tell the system to skip this in future.
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    In case of network/rmi error
     *
     */
    public void terminate() throws RemoteException, SmartFrogException {
        if (context != null) {
            try {
                Server httpServer = getServer().getServer();
                if (httpServer != null) {
                    httpServer.removeLifeCycle(context);
                } else {
                    //do nothing, the server is not alive any more
                }
            } catch (IllegalStateException ex) {
                throw SmartFrogException.forward(ex);
            } finally {
                context = null;
            }
        }
    }
}
