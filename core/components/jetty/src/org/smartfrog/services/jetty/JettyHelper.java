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


package org.smartfrog.services.jetty;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.smartfrog.services.jetty.contexts.JettyServletContextIntf;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * This helper class contains all the binding policy for use in contexts and
 * servlets. Date: 21-Jun-2004 Time: 22:02:20
 */
public class JettyHelper extends WebApplicationHelper {


    /**
     * the server
     */
    private Server httpServer;


    /**
     * a reference to our server component
     */
    private Prim serverComponent = null;


    /**
     * Error if we cannot locate an app server.
     * {@value}
     */
    public static final String ERROR_NO_APP_SERVER = "No Web Server found";

    public JettyHelper(Prim owner) {
        super(owner);
    }

    /**
     * bind to the server, cache it
     * @return the server binding
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     *
     */
    public Server bindToServer() throws SmartFrogException,
            RemoteException {
        findJettyComponent();
        httpServer = findJettyServer();
        return httpServer;
    }

    /**
     * Set the component acting as a server
     * @param serverComponent new value
     */
    public void setServerComponent(Prim serverComponent) {
        this.serverComponent = serverComponent;
    }

    /**
     * locate jetty or throw an exception
     *
     * @return the jetty binding
     * @throws SmartFrogException smartfrog problems, including no server found
     * @throws RemoteException network problems
     */
    private Server findJettyServer() throws SmartFrogException,
            RemoteException {
        assert serverComponent != null;
        Server server = null;
        server =
                (Server) serverComponent.sfResolve(JettyIntf.ATTR_JETTY_SERVER,
                        server,
                        true);
        return server;
    }

    /**
     * look for the jetty component by -looking for a server attribute
     * @throws SmartFrogResolutionException if one is not found
     * @throws RemoteException network problems
     */
    private void findJettyComponent() throws SmartFrogResolutionException,
            RemoteException {

        if (serverComponent == null) {
            //look for an attribute
            serverComponent =
                    getOwner().sfResolve(ApplicationServerContext.ATTR_SERVER,
                            serverComponent,
                            true);
        }

    }

    /**
     * save the jetty info for retrieval.
     * This is done by adding it as a (non-serializable) attribute
     *
     * @param server the jetty instance
     * @throws SmartFrogException a failure of the operation to set the server
     * @throws RemoteException network problems
     */
    public void cacheJettyServer(Server server)
            throws SmartFrogException, RemoteException {
        getOwner().sfReplaceAttribute(JettyIntf.ATTR_JETTY_SERVER, server);

    }


    /**
     * locate jettyhome
     *
     * @return jetty home or null if it is not there
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public String findJettyHome() throws SmartFrogException, RemoteException {
        assert serverComponent != null;
        String jettyhome = null;
        jettyhome =
                serverComponent.sfResolve(JettyIntf.ATTR_JETTY_HOME,
                        jettyhome,
                        false);
        return jettyhome;
    }

    /**
     * save jetty home for retrieval
     *
     * @param jettyhome
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void cacheJettyHome(String jettyhome)
            throws SmartFrogRuntimeException, RemoteException {
        getOwner().sfReplaceAttribute(JettyIntf.ATTR_JETTY_HOME, jettyhome);
    }

    /**
     * for servlets: get the servlet context.
     *
     * @param mandatory set this to true if you want an exception if there is no
     *                  context
     * @return context, or null if there is not one found
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public Context getServletContext(boolean mandatory)
            throws SmartFrogException, RemoteException {


        Context jettyContext = null;

        Prim contextImpl = findServletContext();
        if (contextImpl != null) {
            jettyContext = (Context) contextImpl.
                    sfResolve(JettyServletContextIntf.ATTR_CONTEXT);
        }
        if (mandatory && jettyContext == null) {
            throw new SmartFrogException(
                    "Could not locate "
                    +
                    JettyServletContextIntf.ATTR_CONTEXT);
        }
        return jettyContext;
    }

    /**
     * find whatever ancestor is a servlet context
     */
    /**
     * Find the servlet context.
     * This is done by resolving the owner and looking for its server attribute
     * @return the owner server, null if there is no server,
     * @throws RemoteException network trouble
     * @throws SmartFrogResolutionException failure to resolve
     */
    public Prim findServletContext() throws RemoteException, SmartFrogResolutionException {
        return getOwner().sfResolve(ApplicationServerContext.ATTR_SERVER,(Prim)null,false);
    }


    /**
     * add a listener to the server
     *
     * @param listener a listener
     */
    public void addListener(Connector listener) {
        httpServer.addConnector(listener);
    }

    /**
     * add a listener, then start it
     *
     * @param listener a listener
     * @throws SmartFrogException failue to start the listener
     */
    public void addAndStartListener(Connector listener)
            throws SmartFrogException {
        addListener(listener);
        try {
            listener.start();
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    /**
     * remove a listener
     * @param listener a listener
     */
    public void removeListener(Connector listener) {
        if (httpServer != null) {
            httpServer.removeConnector(listener);
        }
    }

    /**
     * get the server
     *
     * @return server or null if unbound.
     */
    public Server getServer() {
        return httpServer;
    }

    /**
     * terminate a context log failures but do not throw anything
     *
     * @param context context to terminate
     */
    public void terminateContext(Context context) {
        if (context != null) {
            try {
                context.stop();
            } catch (Exception ex) {
                if (getLogger().isErrorEnabled()) {
                    getLogger().error(" Interrupted on context termination ",
                            ex);
                }
            }
            if (httpServer != null) {
                httpServer.removeLifeCycle(context);
            }
        }
    }

    /**
     * terminate a listener; log trouble but continue
     *
     * @param listener a listener
     */
    public synchronized void terminateListener(Connector listener) {
        if (listener != null) {
            try {
                listener.stop();
            } catch (InterruptedException ex) {
                if (getLogger().isErrorEnabled()) {
                    getLogger().error(" Interrupted on listener termination ",
                            ex);
                }
            } catch (Exception npe) {
                    getLogger().error(
                            " Ignoring caught during Jetty teardown",
                            npe);
            }
            removeListener(listener);
        }
    }


}
