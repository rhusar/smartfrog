/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.ListUtils;

import java.rmi.RemoteException;
import java.util.Collection;

/**
 * A servlet within a Jetty servlet context.
 */
public class JettyServletDelegate extends AbstractJettyServletContextDelegate
        implements ServletComponent {

    /**
     * default inititialisation order {@value}
     */
    public static final int DEFAULT_INIT_ORDER = -1;

    private String pathSpec = null;

    private ServletHolder holder = null;
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
    public JettyServletDelegate(DelegateServletContext context, Prim owner)
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

        holder = new ServletHolder();

        //get and apply init order
        int initOrder = prim.sfResolve(ATTR_INIT_ORDER,
                DEFAULT_INIT_ORDER,
                false);
        if (initOrder > 0) {
            //the init order is only set if positive, because of SFOS-906.
            holder.setInitOrder(initOrder);
        }

        //apply initialisation params from the list
        bindAndInitHolder(holder);


        //update our path attribute
        String ancestorPath = ctx.getAbsolutePath();
        absolutePath = WebApplicationHelper.deregexpPath(JettyHelper.concatPaths(
                ancestorPath,
                pathSpec));
        owner.sfReplaceAttribute(ApplicationServerContext.ATTR_ABSOLUTE_PATH,
                absolutePath);

        //add the servlet
        servletContext.addServlet(holder, pathSpec);
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

        //you can only add mappings after registering the servlet
        Collection<String> mappings = ListUtils.resolveStringList(prim, mappingsRef, false);
        if (mappings != null) {
            String[] pathSpecs = new String[mappings.size()];
            int counter = 0;
            for (String mapping : mappings) {
                pathSpecs[counter++] = mapping;
            }
            ServletMapping servletMapping = new ServletMapping();
            servletMapping.setPathSpecs(pathSpecs);
            servletMapping.setServletName(name);
            servletHandler.addServletMapping(servletMapping);
        }


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
        return super.toString()
                + "; pathSpec=" + pathSpec
                + "; absolutePath=" + absolutePath;
    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException smartfrog problems
     * @throws RemoteException            network problems
     */
    @Override
    public void ping() throws SmartFrogLivenessException, RemoteException {
        if (isJettyHolderStarted()) {
            throw new SmartFrogLivenessException("Servlet " +
                    name +
                    " is not running under" + getAbsolutePath());
        }
    }

}
