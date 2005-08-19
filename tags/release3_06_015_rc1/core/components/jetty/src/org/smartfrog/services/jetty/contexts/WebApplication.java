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
package org.smartfrog.services.jetty.contexts;

import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.rmi.RemoteException;

/**
 * A WebApplication context class for jetty server
 *
 * @author Ritu Sabharwal
 */


public class WebApplication extends PrimImpl
        implements JettyWebApplicationContext {
    private Reference contextPathRef = new Reference(ATTR_CONTEXT_PATH);
    private Reference webAppRef = new Reference(ATTR_WARFILE);
    private Reference requestIdRef = new Reference(ATTR_REQUEST_ID);
    private JettyHelper jettyHelper = new JettyHelper(this);
    private String jettyhome = ".";
    private String contextPath = "/";
    private String webApp = null;
    private boolean requestId = false;

    private HttpServer server = null;

    private WebApplicationContext context = new WebApplicationContext();

    /**
     * Standard RMI constructor
     */
    public WebApplication() throws RemoteException {
        super();
    }

    /**
     * Deploy the WebApplication context
     *
     * @throws SmartFrogException In case of error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

    }

    /**
     * sfStart: adds the WebApplication context to the jetty server
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        server = jettyHelper.bindToServer();
        jettyhome = jettyHelper.findJettyHome();
        contextPath = sfResolve(contextPathRef, contextPath, true);
        //fetch the webapp reference by doing filename resolution
        //if the file exists, it does not need to be anywhere
        webApp = sfResolve(webAppRef, webApp, false);
        if (webApp != null) {
            if (!new File(webApp).exists()) {
                File webAppFile = new File(jettyhome, webApp);
                webApp = webAppFile.getAbsolutePath();
            }
        }

        //no webapp? look for the warfile
        if (webApp == null) {
            webApp =
                    FileSystem.lookupAbsolutePath(this,
                            ATTR_WARFILE,
                            null,
                            null,
                            true,
                            null);
        }
        //sanity check
        File webappFile = new File(webApp);
        if (!webappFile.exists()) {
            throw new SmartFrogDeploymentException("Web application " +
                    webappFile +
                    " was not found");
        }
        //request ID
        requestId = sfResolve(requestIdRef, requestId, false);
        addWarContext(contextPath, webApp, requestId);
        server.addContext(context);
        try {
            context.start();
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    /**
     * Termination phase
     */
    public void sfTerminateWith(TerminationRecord status) {
        jettyHelper.terminateContext(context);
        super.sfTerminateWith(status);
    }

    /**
     * Add the context to the http server
     *
     * @throws RemoteException In case of network/rmi error
     */
    public void addWarContext(String contextpath,
            String webApp,
            boolean requestId)
            throws RemoteException {
        context.setContextPath(contextPath);
        context.setWAR(webApp);
        ServletHandler servlethandler = context.getServletHandler();
        AbstractSessionManager sessionmanager = (AbstractSessionManager)
                servlethandler.getSessionManager();
        sessionmanager.setUseRequestedId(requestId);
    }
}
