package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.smartfrog.services.www.FilterComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * Created 24-May-2010 17:59:25
 */

public class JettyFilterDelegate extends AbstractJettyServletContextDelegate
        implements FilterComponent {

    private String pattern;

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

        FilterHolder holder = new FilterHolder();

        bindAndInitHolder(holder);


        pattern = prim.sfResolve(ATTR_PATTERN, pattern, true);

        //build the dispatch mask
        boolean dispatchRequest = prim.sfResolve(ATTR_DISPATCH_REQUEST, true, true);
        boolean dispatchForward = prim.sfResolve(ATTR_DISPATCH_FORWARD, true, true);
        boolean dispatchInclude = prim.sfResolve(ATTR_DISPATCH_INCLUDE, true, true);
        boolean dispatchError   = prim.sfResolve(ATTR_DISPATCH_ERROR, true, true);
        int dispatches = Handler.DEFAULT;
        
        if (dispatchRequest) {
            dispatches |= Handler.REQUEST;
        }
        if (dispatchForward) {
            dispatches |= Handler.FORWARD;
        }
        if (dispatchInclude) {
            dispatches |= Handler.INCLUDE;
        }
        if (dispatchError) {
            dispatches |= Handler.ERROR;
        }

        //add the filter
        servletContext.addFilter(holder, pattern, dispatches);
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


    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return super.toString()
                + "; pattern=" + pattern;
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
                    " is not running under" + pattern);
        }
    }
}
