package org.smartfrog.services.jetty.contexts.servlets;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Enumeration;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * A Servlet class for a Jetty http server.
 *
 * @author Ritu Sabharwal
 */


public class Servlet extends PrimImpl implements JettyServlet {

    Reference nameRef = new Reference(NAME);
    Reference pathSpecRef = new Reference(PATH_SPEC);
    Reference classNameRef = new Reference(CLASSNAME);
    Reference initParamsRef = new Reference(INIT_PARAMS);

    String name = null;
    String pathSpec = null;
    String className = null;
    Vector initParams = null;

    ServletHolder holder = new ServletHolder();

    /**
     * Standard RMI constructor
     */
    public Servlet() throws RemoteException {
        super();
    }

    /**
     * sfDeploy: adds a servlet to ServetletHttpContext of jetty server
     *
     * @throws SmartFrogException In case of error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        try {
            super.sfDeploy();
            name = sfResolve(nameRef, name, true);
            pathSpec = sfResolve(pathSpecRef, pathSpec, true);
            className = sfResolve(classNameRef, className, true);
            initParams = sfResolve(initParamsRef, initParams, false);
            Prim parent = this.sfParent();
            Prim grandParent = parent.sfParent();
            ServletHttpContext cxt = (ServletHttpContext) grandParent.
                    sfResolveId("Context");
            holder = cxt.addServlet(name, pathSpec, className);
            if (initParams != null) {
                for (Enumeration en = initParams.elements();
                     en.hasMoreElements();) {
                    Vector element = (Vector) en.nextElement();
                    String key = element.firstElement().toString();
                    String value = element.lastElement().toString();
                    holder.setInitParameter(key, value);
                }
            }
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }
}
