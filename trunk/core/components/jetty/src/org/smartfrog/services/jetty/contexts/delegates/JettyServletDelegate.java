package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

/**
 */
public class JettyServletDelegate implements ServletContextComponentDelegate, ServletComponent {

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
     * Create the delegate and configure the {@link ServletHttpContext} of Jetty that
     * is the real context
     * @param context
     * @param owner
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public JettyServletDelegate(DelegateServletContext context,Prim owner) throws SmartFrogException, RemoteException {
        this.context = context;
        this.owner = owner;
        try {
            name = owner.sfResolve(nameRef, name, true);
            pathSpec = owner.sfResolve(pathSpecRef, pathSpec, true);
            className = owner.sfResolve(classNameRef, className, true);

            ServletHttpContext servletContext;
            servletContext = context.getServletContext();

            holder = servletContext.addServlet(name, pathSpec, className);

            //get and apply init order
            int initOrder = owner.sfResolve(ATTR_INIT_ORDER,
                    DEFAULT_INIT_ORDER,
                    false);
            holder.setInitOrder(initOrder);

            //apply initialisation params
            initParams = owner.sfResolve(initParamsRef, initParams, false);
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
            String ancestorPath = context.getAbsolutePath();
            absolutePath = JettyHelper.deregexpPath(JettyHelper.concatPaths(ancestorPath, pathSpec));
            owner.sfReplaceAttribute(ServletContextIntf.ATTR_ABSOLUTE_PATH, absolutePath);

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
     * start the component
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void start() throws SmartFrogException, RemoteException {
        try {
            holder.start();
        } catch (Exception e) {
            throw new SmartFrogException(e);
        }
    }

    /**
     * this method is here for server-specific implementation classes,
     *
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void undeploy() throws RemoteException, SmartFrogException {
        if (holder != null) {
            holder.stop();
            holder = null;
        }
    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {
        if (holder == null || !holder.isStarted()) {
            throw new SmartFrogLivenessException("Servlet " +
                    name +
                    " is not running");
        }
    }
}
