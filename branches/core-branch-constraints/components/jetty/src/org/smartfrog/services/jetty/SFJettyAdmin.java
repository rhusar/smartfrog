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

import org.mortbay.http.BasicAuthenticator;
import org.mortbay.http.HashUserRealm;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.SecurityHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * A wrapper for a Jetty http server for admin configurations
 *
 * @author Ritu Sabharwal
 */

public class SFJettyAdmin extends PrimImpl implements JettyAdminIntf {
    private Reference listenerPortRef = new Reference(LISTENER_PORT);
    private Reference httpserverHostRef = new Reference(HTTP_SERVER_HOST);
    private Reference contextPathRef = new Reference(CONTEXT_PATH);

    private int listenerPort = 8081;
    private String httpserverHost=null;
    private String contextPath;

    /**
     * The server
     */
    private HttpServer server;

    /**
     * The Socket listener
     */
    private SocketListener listener = new SocketListener();

    /**
     * Realm context
     */
    private ServletHttpContext realmcontext = new ServletHttpContext();

    /**
     * User realm
     */
    private HashUserRealm admin_realm = new HashUserRealm(ADMIN_REALM_NAME);
    private static final String ADMIN_REALM_NAME = "Admin Realm";

    /**
     * Standard RMI constructor
     */
    public SFJettyAdmin() throws RemoteException {
        super();
    }

    /**
     * Deploy the SFJettyAdmin component
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
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
            server = new HttpServer();
            listenerPort = sfResolve(listenerPortRef, listenerPort, true);
            httpserverHost = sfResolve(httpserverHostRef, httpserverHost,
                    false);
            contextPath = sfResolve(contextPathRef, "/", false);
            configureHttpServer();
        } catch (Exception ex) {
            throw SmartFrogDeploymentException.forward(ex);
        }
        try {
            server.start();
        } catch (Exception mexp) {
            throw new SmartFrogException(mexp);
        }
    }

    /**
     * Configure the http server for admin configurations
     */
    public void configureHttpServer() throws SmartFrogException {
        try {
            listener.setPort(listenerPort);
            listener.setHost(httpserverHost);
            server.addListener(listener);
            admin_realm.put("admin", "admin");
            admin_realm.addUserToRole("admin", "server-administrator");
            server.addRealm(admin_realm);
            realmcontext.setContextPath(contextPath);
            realmcontext.setRealmName(ADMIN_REALM_NAME);
            realmcontext.setAuthenticator(new BasicAuthenticator());
            realmcontext.addHandler(new SecurityHandler());
            realmcontext.addSecurityConstraint("/",
                    new SecurityConstraint("Admin",
                            "server-administrator"));
            realmcontext.addServlet("Debug", "/Debug/*",
                    "org.mortbay.servlet.Debug");
            realmcontext.addServlet("Admin", "/",
                    "org.mortbay.servlet.AdminServlet");
            realmcontext.setAttribute("org.mortbay.http.HttpServer",
                    realmcontext.getHttpServer());
            server.addContext(realmcontext);
            server.setAnonymous(true);
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    /**
     * Termination phase
     */
    public void sfTerminateWith(TerminationRecord status) {
        server.removeListener(listener);
        server.removeContext(realmcontext);
        try {
            server.stop();
        } catch (InterruptedException ie) {
          if (sfLog().isErrorEnabled()){
            sfLog().error(" Interrupted on server termination " , ie);
          }
//          Logger.log(" Interrupted on server termination " , ie);
        }
        super.sfTerminateWith(status);
    }
}
