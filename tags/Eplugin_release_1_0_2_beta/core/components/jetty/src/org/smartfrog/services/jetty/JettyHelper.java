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
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpListener;
import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.services.jetty.contexts.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.Vector;

/**
 * This helper class contains all the binding policy for use in contexts and servlets.
 * Date: 21-Jun-2004
 * Time: 22:02:20
 */
public class JettyHelper extends ComponentHelper {


    /**
     * the server
     */
    private HttpServer httpServer;


    /**
     * a reference to our server component
     */
    private Prim serverComponent=null;

    /**
     * Name of the interface of jetty component we look for
     * "org.smartfrog.services.jetty.JettyIntf";
     * {@value}
     */
    public static final String JETTY_INTERFACE_NAME = JettyIntf.class.getCanonicalName(); 
    //
    /**
     * Name of the servlet interface of jetty component we look for.
     * "org.smartfrog.services.jetty.contexts.ServletContextIntf";
     *  {@value}
     */
    public static final String JETTY_SERVLET_INTERFACE = ServletContextIntf.class.getCanonicalName();
    
    /**
     * max depth to recurse down
     */ 
    
    private static final int MAX_PARENT_DEPTH = 99999;

    public JettyHelper(Prim owner) {
        super(owner);
    }

    /**
     * bind to the server, cache it
     */
    public HttpServer bindToServer() throws SmartFrogException, RemoteException {
        findJettyComponent();
        httpServer=findJettyServer();
        return httpServer;
    }

    /**
     * locate jetty
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     */
    private HttpServer findJettyServer() throws SmartFrogException, RemoteException {
        assert serverComponent!=null;
        HttpServer server =null;
        server = (HttpServer) serverComponent.sfResolve(JettyIntf.JETTY_SERVER,server,true);
        return server;
    }

    /**
     * look for the jetty component by
     * -looking for a server component that implements it
     * -probing for a parent
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    private void findJettyComponent() throws SmartFrogResolutionException, RemoteException {

        if(serverComponent == null) {
            //look for an attribute first
            serverComponent = getOwner().sfResolve(JettyIntf.SERVER, serverComponent, false);
            if(serverComponent==null) {
                serverComponent=findAncestorImplementing(JETTY_INTERFACE_NAME,
                    -1);
                if ( serverComponent == null ) {
                    throw new SmartFrogResolutionException("No Web Server found");
                }
            }

        }
    }

    /**
     * save the jetty info for retrieval
     * @param server
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void cacheJettyServer(HttpServer server)
            throws SmartFrogException, RemoteException {
        getOwner().sfReplaceAttribute(JettyIntf.JETTY_SERVER, server);

    }


    /**
     * locate jettyhome
     * @return jetty home or null if it is not there
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public String findJettyHome() throws SmartFrogException, RemoteException {
        assert serverComponent != null;
        String jettyhome =null;
        jettyhome= serverComponent.sfResolve(JettyIntf.JETTY_HOME,jettyhome,false);
        return jettyhome;
    }

    /**
     * save jetty home for retrieval
     * @param jettyhome
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void cacheJettyHome(String jettyhome) throws SmartFrogRuntimeException, RemoteException {
        getOwner().sfReplaceAttribute(JettyIntf.JETTY_HOME, jettyhome);
    }

    /**
     * for servlets: get the servlet context.
     *
     * @param mandatory set this to true if you want an exception if there is no context
     * @return context, or null if there is not one found
     * @throws SmartFrogException
     * @throws RemoteException
     * @param mandatory
     */
    public ServletHttpContext getServletContext(boolean mandatory)
            throws SmartFrogException,RemoteException {


        ServletHttpContext context=null;

        Prim ancestor = findServletContextAncestor();
        if(ancestor!=null) {
            context = (ServletHttpContext) ancestor.
                    sfResolve(ServletContextIntf.ATTR_CONTEXT);
        }
        if (mandatory && context == null) {
            throw new SmartFrogException("Could not locate "
                    + ServletContextIntf.ATTR_CONTEXT + " in the hierarchy");
        }
        return context;
    }

    /**
     * find whatever ancestor is a servlet context
     */
    public Prim findServletContextAncestor() throws RemoteException {
        return findAncestorImplementing(JETTY_SERVLET_INTERFACE, MAX_PARENT_DEPTH);
    }

    /**
     * add a handler to the server
     * @param handler
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void addHandler(HttpHandler handler) throws SmartFrogException, RemoteException{
        ServletHttpContext context=getServletContext(true);
        context.addHandler(handler);
    }

    /**
     * add a listener to the server
     * @param listener
     */
    public void addListener(HttpListener listener)  {
        httpServer.addListener(listener);
    }

    /**
     * add a listener, then start it
     * @param listener
     * @throws SmartFrogException
     */
    public void addAndStartListener(HttpListener listener) throws SmartFrogException {
        addListener(listener);
        try {
            listener.start();
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    public void removeListener(HttpListener listener) {
        if(httpServer!=null) {
            httpServer .removeListener(listener);
        }
    }

    /**
     * get the server
     * @return server or null if unbound.
     */
    public HttpServer getServer() {
        return httpServer;
    }

    /**
     * terminate a context log failures but do not throw anything
     * @param context
     */
    public void terminateContext(HttpContext context) {
        if ( context != null ) {
            try {
                context.stop();
            } catch (Exception ex) {
              if (getLogger().isErrorEnabled()){
                getLogger().error(" Interrupted on context termination ", ex);
              }
            }
            if ( httpServer != null ) {
                httpServer.removeContext(context);
            }
        }
    }

    /**
     * terminate a listener; log trouble but continue
     * @param listener
     */
    public void terminateListener(HttpListener listener) {
        if ( listener != null ) {
            try {
                listener.stop();
            } catch (Exception ex) {
                if (getLogger().isErrorEnabled()){
                  getLogger().error(" Interrupted on listener termination ", ex);
                }
            }
            removeListener(listener);
        }
    }
    
    /**
     * strip any trailing * from a path and give the base bit up to where that began.
     * @param path
     * @return
     */ 
    public String deregexpPath(String path) {
        String result;
        int star=path.indexOf('*');
        if(star<0) {
            return path;
        } 
        if(star==0) {
            return "/";
        }
        result=path.substring(0,star-1);
        return result;
    }
    
    /**
     * Concatenate two paths together, inserting a '/' if needed, and ensuring
     * that there is no '//' at the join. 
     * @param path1
     * @param path2
     * @return
     */ 
    public String concatPaths(String path1,String path2) {
        StringBuffer buffer=new StringBuffer(path1.length()+path2.length()+1);
        boolean endsWithSlash=path1.endsWith("/");
        boolean beginsWithSlash = path2.startsWith("/");
        buffer.append(path1);
        if(!endsWithSlash) {
            buffer.append('/');
        }
        if(beginsWithSlash) {
            buffer.append(path2.substring(1));
        } else {
            buffer.append(path2);
        }
        return buffer.toString();
    }

    /**
     * Get the ipaddrs of the local machine
     * @return
     */ 
    public String getIpAddress() throws RemoteException {
        InetAddress deployedHost = getOwner().sfDeployedHost();
        String hostAddress = deployedHost.getHostAddress();
        return hostAddress;
    }
}
