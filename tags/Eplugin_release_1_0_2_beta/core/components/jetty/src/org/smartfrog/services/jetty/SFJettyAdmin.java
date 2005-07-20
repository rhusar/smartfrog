/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

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
    Reference listenerPortRef = new Reference(LISTENER_PORT);
    Reference httpserverHostRef = new Reference(HTTP_SERVER_HOST);
    Reference contextPathRef = new Reference(CONTEXT_PATH);

    int listenerPort = 8081;
    String httpserverHost=null;
    String contextPath;

    /**
     * The server
     */
    HttpServer server;

    /**
     * The Socket listener
     */
    SocketListener listener = new SocketListener();

    /**
     * Realm context
     */
    ServletHttpContext realmcontext = new ServletHttpContext();

    /**
     * User realm
     */
    HashUserRealm admin_realm = new HashUserRealm("Admin Realm");

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
            realmcontext.setRealmName("Admin Realm");
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
