package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletMapping;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

/**
 */
public class JettyServletDelegate
        implements ServletContextComponentDelegate, ServletComponent {

    /**
     * default inititialisaion order {@value}
     */
    public static final int DEFAULT_INIT_ORDER = -1;
    /**
     * context within which the servlet deploys.
     */
    private DelegateServletContext context;

    private Prim owner;

    private Reference nameRef = new Reference(ServletComponent.ATTR_NAME);
    private Reference pathSpecRef = new Reference(ServletComponent.ATTR_PATH_SPEC);
    private Reference classNameRef = new Reference(ServletComponent.ATTR_CLASSNAME);
    private Reference initParamsRef = new Reference(ServletComponent.ATTR_INIT_PARAMS);

    private String name = null;
    private String pathSpec = null;
    private String className = null;
    private Vector initParams = null;

    private ServletHolder holder = null;
    private String absolutePath;
    /**
     * a log
     */
    private Log log;

    /**
     * Create the delegate and configure the {@link org.mortbay.jetty.servlet.ServletHttpContext} of Jetty
     * that is the real context
     *
     * @param context sevlet context
     * @param owner owner component
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public JettyServletDelegate(DelegateServletContext context, Prim owner)
            throws SmartFrogException, RemoteException {
        this.context = context;
        this.owner = owner;
        log = LogFactory.getOwnerLog(owner);
        bind(owner, context);
    }

    /**
     * Read in state, bind to the owner
     * @param ctx sevlet context
     * @param prim owner component
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    private void bind(Prim prim, DelegateServletContext ctx) throws RemoteException, SmartFrogException {
        try {
            name = prim.sfResolve(nameRef, name, true);
            pathSpec = prim.sfResolve(pathSpecRef, pathSpec, true);
            className = prim.sfResolve(classNameRef, className, true);

            Context servletContext;
            servletContext = ctx.getServletContext();

            holder = servletContext.addServlet(pathSpec, className);
            holder.setDisplayName(name);

            //get and apply init order
            int initOrder = prim.sfResolve(ATTR_INIT_ORDER,
                    DEFAULT_INIT_ORDER,
                    false);
            holder.setInitOrder(initOrder);

            //apply initialisation params
            initParams = prim.sfResolve(initParamsRef, initParams, false);
            if (initParams != null) {
                for (Enumeration en = initParams.elements();
                     en.hasMoreElements();) {
                    Vector element = (Vector) en.nextElement();
                    String key = element.firstElement().toString();
                    String value = element.lastElement().toString();
                    holder.setInitParameter(key, value);
                }
            }

            //update our path attribute
            String ancestorPath = ctx.getAbsolutePath();
            absolutePath = WebApplicationHelper.deregexpPath(JettyHelper.concatPaths(
                    ancestorPath,
                    pathSpec));
            prim.sfReplaceAttribute(ServletContextIntf.ATTR_ABSOLUTE_PATH,
                    absolutePath);

            //extract mappings
            Vector mappings = null;
            mappings = prim.sfResolve(ATTR_MAPPINGS, mappings, false);
            if (mappings != null) {
                String[] pathSpecs=new String[mappings.size()];
                int counter=0;
                for (Object mapping : mappings) {
                    pathSpecs[counter++]= mapping.toString();
                }
                ServletHandler servletHandler = servletContext.getServletHandler();
                ServletMapping servletMapping = new ServletMapping();
                servletMapping.setPathSpecs(pathSpecs);
                servletMapping.setServletName(name);
                servletHandler.addServletMapping(servletMapping);
            }

        } catch (RemoteException ex) {
            throw ex;
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
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
    public String toString() {
        return "name=" + name
                + "; className=" + className
                + "; pathSpec=" + pathSpec
                + "; absolutePath=" + absolutePath;
    }

    /**
     * noop
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void deploy() throws SmartFrogException, RemoteException {

    }

    /**
     * start the component
     *
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void start() throws SmartFrogException, RemoteException {
        try {
            log.info("Starting servlet on jetty; "
                    + toString());
            holder.start();
        } catch (Exception e) {
            throw new SmartFrogException(e);
        }
    }

    /**
     * this method is here for server-specific implementation classes,
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void terminate() throws RemoteException, SmartFrogException {
        try {
            if (holder != null) {
                holder.stop();
                holder = null;
            }
        } catch (Exception e) {
            throw SmartFrogException.forward(e);
        }
    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException smartfrog problems
     * @throws RemoteException network problems
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {
        if (holder == null || !holder.isStarted()) {
            throw new SmartFrogLivenessException("Servlet " +
                    name +
                    " is not running under" + getAbsolutePath());
        }
    }
}
