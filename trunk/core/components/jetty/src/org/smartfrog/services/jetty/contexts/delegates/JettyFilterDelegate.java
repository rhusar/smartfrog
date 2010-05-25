package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.FilterComponent;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.ListUtils;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created 24-May-2010 17:59:25
 */

public class JettyFilterDelegate extends AbstractJettyServletContextDelegate
        implements FilterComponent {

    /**
     * default inititialisation order {@value}
     */

    private String pathSpec = null;

    private FilterHolder holder = null;
    private String absolutePath;

    /**
     * Create the delegate and configure the {@link org.mortbay.jetty.servlet.Context} of Jetty that is the real
     * context
     *
     * @param context sevlet context
     * @param owner   owner component
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    public JettyFilterDelegate(DelegateServletContext context, Prim owner)
            throws SmartFrogException, RemoteException {
        super(owner, context);
        bind(owner, context);
    }

    /**
     * Read in state, bind to the owner
     *
     * @param ctx  sevlet context
     * @param prim owner component
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    private void bind(Prim prim, DelegateServletContext ctx) throws RemoteException, SmartFrogException {
        Context servletContext = resolveJettyServletContext(ctx);
        pathSpec = prim.sfResolve(pathSpecRef, pathSpec, true);

        holder = new FilterHolder();

        bindAndInitHolder(holder);


        //update our path attribute
        String ancestorPath = ctx.getAbsolutePath();
        absolutePath = WebApplicationHelper.deregexpPath(JettyHelper.concatPaths(
                ancestorPath,
                pathSpec));
        prim.sfReplaceAttribute(ApplicationServerContext.ATTR_ABSOLUTE_PATH,
                absolutePath);
        int dispatches = 0;
        //add the filter
        servletContext.addFilter(holder, pathSpec, dispatches);
/*
        ServletHandler servletHandler = servletContext.getServletHandler();
        ServletHolder resolvedHolder = servletHandler.getServlet(name);
        if (resolvedHolder == null) {
            //oops. no servlets, make a list
            StringBuilder message = new StringBuilder("Failed to register the servlet with jetty.");
            ServletHolder[] holders = servletHandler.getServlets();
            for (ServletHolder entry : holders) {
                message.append("\n\"");
                message.append(entry.getDisplayName());
                message.append("\" ");
                message.append(entry.getClassName());
            }
            throw new SmartFrogDeploymentException(message.toString());
        }
*/

        //now start it up if the context is already live.
        if (servletContext.isStarted()) {
            start();
        }

    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "name=" + name
                + "; className=" + className
                + "; pathSpec=" + pathSpec
                + "; absolutePath=" + absolutePath;
    }

    /**
     * start the component
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException smartfrog problems
     * @throws RemoteException            network problems
     */
    @Override
    public void ping() throws SmartFrogLivenessException, RemoteException {
        if (isJettyHolderStarted()) {
            throw new SmartFrogLivenessException("Filter " +
                    name +
                    " is not running under" + getAbsolutePath());
        }
    }
}
