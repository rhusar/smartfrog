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

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpListener;
import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.services.jetty.contexts.JettyServletContextIntf;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.net.InetAddress;
import java.rmi.RemoteException;

/**
 * This helper class contains all the binding policy for use in contexts and
 * servlets. Date: 21-Jun-2004 Time: 22:02:20
 */
public class JettyHelper extends WebApplicationHelper {


    /**
     * the server
     */
    private HttpServer httpServer;


    /**
     * a reference to our server component
     */
    private Prim serverComponent = null;

    /**
     * Name of the interface of jetty component we look for.
     * <p/>
     * {@value}
     */
    public static final String JETTY_INTERFACE_NAME = "org.smartfrog.services.jetty.JettyIntf";
    //
    /**
     * Name of the servlet interface of jetty component we look for.
     * <p/>
     * {@value}
     */
    public static final String JETTY_SERVLET_INTERFACE = "org.smartfrog.services.jetty.contexts.JettyServletContextIntf";

    /**
     * max depth to recurse down
     */

    private static final int MAX_PARENT_DEPTH = 99999;

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
     */
    public HttpServer bindToServer() throws SmartFrogException,
            RemoteException {
        findJettyComponent();
        httpServer = findJettyServer();
        return httpServer;
    }

    public void setServerComponent(Prim serverComponent) {
        this.serverComponent = serverComponent;
    }

    /**
     * locate jetty
     *
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     */
    private HttpServer findJettyServer() throws SmartFrogException,
            RemoteException {
        assert serverComponent != null;
        HttpServer server = null;
        server =
                (HttpServer) serverComponent.sfResolve(JettyIntf.ATTR_JETTY_SERVER,
                        server,
                        true);
        return server;
    }

    /**
     * look for the jetty component by -looking for a server attribute
     * @throws SmartFrogResolutionException
     * @throws RemoteException
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
     * save the jetty info for retrieval
     *
     * @param server
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void cacheJettyServer(HttpServer server)
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
    public ServletHttpContext getServletContext(boolean mandatory)
            throws SmartFrogException, RemoteException {


        ServletHttpContext jettyContext = null;

        Prim contextImpl = findServletContext();
        if (contextImpl != null) {
            jettyContext = (ServletHttpContext) contextImpl.
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
    public Prim findServletContext() throws RemoteException, SmartFrogResolutionException {
        return getOwner().sfResolve(ApplicationServerContext.ATTR_SERVER,(Prim)null,false);
    }


    /**
     * add a listener to the server
     *
     * @param listener
     */
    public void addListener(HttpListener listener) {
        httpServer.addListener(listener);
    }

    /**
     * add a listener, then start it
     *
     * @param listener
     * @throws SmartFrogException
     */
    public void addAndStartListener(HttpListener listener)
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
     * @param listener
     */
    public void removeListener(HttpListener listener) {
        if (httpServer != null) {
            httpServer.removeListener(listener);
        }
    }

    /**
     * get the server
     *
     * @return server or null if unbound.
     */
    public HttpServer getServer() {
        return httpServer;
    }

    /**
     * terminate a context log failures but do not throw anything
     *
     * @param context
     */
    public void terminateContext(HttpContext context) {
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
                httpServer.removeContext(context);
            }
        }
    }

    /**
     * terminate a listener; log trouble but continue
     *
     * @param listener
     */
    public synchronized void terminateListener(HttpListener listener) {
        if (listener != null) {
            try {
                listener.stop();
            } catch (InterruptedException ex) {
                if (getLogger().isErrorEnabled()) {
                    getLogger().error(" Interrupted on listener termination ",
                            ex);
                }
            } catch (NullPointerException npe) {
                if (getLogger().isErrorEnabled()) {
                    getLogger().warn(
                            " Dropping NPE caught during Jetty teardown",
                            npe);
                }
            }
            removeListener(listener);
        }
    }


}
