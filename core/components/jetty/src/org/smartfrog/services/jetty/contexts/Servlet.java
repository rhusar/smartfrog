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
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.rmi.RemoteException;


/**
 * A ServletHttp context class for a Jetty http server.
 *
 * @author Ritu Sabharwal
 */

public class Servlet extends CompoundImpl implements ServletContextIntf {
    Reference contextPathRef = new Reference(ATTR_CONTEXT_PATH);
    Reference resourceBaseRef = new Reference(ATTR_RESOURCE_BASE);
    Reference classPathRef = new Reference(ATTR_CLASSPATH);

    String jettyhome = ".";
    String contextPath = "/";
    String resourceBase = "";
    String classPath = null;
    String mapfromPath;
    String maptoPath;


    JettyHelper jettyHelper = new JettyHelper(this);

    HttpServer server = null;

    ServletHttpContext context;

    /**
     * Standard RMI constructor
     */
    public Servlet() throws RemoteException {
        super();
    }

    /**
     * Deploy the ServletHttpContext
     *
     * @throws SmartFrogException In case of error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        context = new ServletHttpContext();
        sfAddAttribute(ATTR_CONTEXT, context);
        server = jettyHelper.bindToServer();
        jettyhome = jettyHelper.findJettyHome();
        contextPath = sfResolve(contextPathRef, contextPath, true);
        resourceBase = sfResolve(resourceBaseRef, resourceBase, true);
        if (!new File(resourceBase).exists()) {
            resourceBase = jettyhome.concat(resourceBase);
        }
        classPath = sfResolve(classPathRef, classPath, false);
        if (classPath != null) {
            if (!new File(classPath).exists()) {
                classPath = jettyhome.concat(classPath);
            }
        }
        super.sfDeploy();
    }

    /**
     * sfStart: adds the ServletHttpContext to the jetty server
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        addcontext(contextPath, resourceBase, classPath);
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
    public void addcontext(String contextPath, String resourceBase, String
            classPath) throws RemoteException {
        context.setContextPath(contextPath);
        context.setResourceBase(resourceBase);
        context.setClassPath(classPath);
        context.addHandler(new ResourceHandler());
    }

}
