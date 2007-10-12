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

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.BasicAuthenticator;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.servlet.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * A wrapper for a Jetty HTTP server for admin configurations
 *
 * *Revisit this and try and reimplement from SF itself. Its a good use case of the servlet mappings*
 *
 * @author Ritu Sabharwal
 */

public class SFJettyAdmin extends PrimImpl implements JettyAdminIntf {
    private final Reference listenerPortRef = new Reference(LISTENER_PORT);
    private final Reference httpserverHostRef = new Reference(HTTP_SERVER_HOST);
    private final Reference contextPathRef = new Reference(CONTEXT_PATH);

    private int listenerPort = 8081;
    private String httpserverHost=null;
    private String contextPath;

    /**
     * The server
     */
    private Server server;

    /**
     * The Socket listener
     */
    private SocketConnector listener = new SocketConnector();

    /**
     * Realm context
     */
    private Context realmcontext = new Context();

    /**
     * User realm
     */
    private HashUserRealm admin_realm = new HashUserRealm(ADMIN_REALM_NAME);
    private static final String ADMIN_REALM_NAME = "Admin Realm";

    /**
     * Constructor
     * @throws RemoteException    In case of network/rmi error
     */
    public SFJettyAdmin() throws RemoteException {
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
            server = new Server();
            listenerPort = sfResolve(listenerPortRef, listenerPort, true);
            httpserverHost = sfResolve(httpserverHostRef, httpserverHost,
                    false);
            contextPath = sfResolve(contextPathRef, "/", false);
            configureServer();
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
     * @throws SmartFrogException In case of error while starting
     */
    public void configureServer() throws SmartFrogException {
        try {
            listener.setPort(listenerPort);
            listener.setHost(httpserverHost);
            server.addConnector(listener);
            admin_realm.put("admin", "admin");
            admin_realm.addUserToRole("admin", "server-administrator");
            server.addUserRealm(admin_realm);
            realmcontext.setContextPath(contextPath);
            realmcontext.setDisplayName(ADMIN_REALM_NAME);
            SecurityHandler security=new SecurityHandler();
            security.setAuthenticator(new BasicAuthenticator());
            realmcontext.addHandler(security);
            Constraint constraint=new Constraint("Admin",
                    "server-administrator");
            ConstraintMapping[] constraints=new ConstraintMapping[1];
            constraints[0]=new ConstraintMapping();
            constraints[0].setConstraint(constraint);
            constraints[0].setPathSpec("/");
            security.setConstraintMappings(constraints);
            realmcontext.addServlet(
                    "org.mortbay.servlet.Debug", "/Debug/*").setDisplayName("Debug");
            realmcontext.addServlet(
                    "org.mortbay.servlet.AdminServlet", "/").setDisplayName("Admin");
            realmcontext.setAttribute("org.mortbay.http.Server",
                    realmcontext.getServer());
            server.addLifeCycle(realmcontext);
            //server.setAnonymous(true);
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    /**
     * Termination phase. shut the server and the listener
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        server.removeConnector(listener);
        server.removeLifeCycle(realmcontext);
        try {
            server.stop();
        } catch (Exception ie) {
          if (sfLog().isErrorEnabled()){
            sfLog().error(" Interrupted on server termination " , ie);
          }
        }
        super.sfTerminateWith(status);
    }
}
