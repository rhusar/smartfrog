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
package org.smartfrog.services.jetty.contexts.servlets;

import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.jetty.contexts.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A Servlet class for a Jetty http server.
 *
 * @author Ritu Sabharwal
 */


public class JettyServletImpl extends PrimImpl implements JettyServlet {

    Reference nameRef = new Reference(ATTR_NAME);
    Reference pathSpecRef = new Reference(ATTR_PATH_SPEC);
    Reference classNameRef = new Reference(ATTR_CLASSNAME);
    Reference initParamsRef = new Reference(ATTR_INIT_PARAMS);

    String name = null;
    String pathSpec = null;
    String className = null;
    Vector initParams = null;

    ServletHolder holder = null;

    JettyHelper jettyHelper = new JettyHelper(this);
    /**
     * default inititialisaion order {@value}
     */
    public static final int DEFAULT_INIT_ORDER =-1;

    /**
     * Standard RMI constructor
     */
    public JettyServletImpl() throws RemoteException {
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


            ServletHttpContext context;
            context = jettyHelper.getServletContext(true);

            holder = context.addServlet(name, pathSpec, className);
            
            //get and apply init order
            int initOrder = sfResolve(ATTR_INIT_ORDER,
                    DEFAULT_INIT_ORDER,
                    false);
            holder.setInitOrder(initOrder);

            //apply initialisation params
            initParams = sfResolve(initParamsRef, initParams, false);
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
            Prim ancestor = jettyHelper.findServletContextAncestor();
            String ancestorPath = ancestor.sfResolve(ServletContextIntf.ATTR_ABSOLUTE_PATH,"",true);
            String absolutePath = jettyHelper.deregexpPath(ancestorPath + pathSpec);
            sfReplaceAttribute(ATTR_ABSOLUTE_PATH, absolutePath);
            

        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    /**
     * Start the component
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        try {
            holder.start();
        } catch (Exception e) {
            throw new SmartFrogException(e);
        }
    }

    /**
     * Terminate a servlet
     *
     * @param status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (holder != null) {
            holder.stop();
            holder = null;
        }
    }

    /**
     * Liveness test raises an exception if the servlet is not running
     * @param source
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void sfPing(Object source) throws SmartFrogLivenessException,
            RemoteException {
        if (holder == null || !holder.isStarted()) {
            throw new SmartFrogLivenessException("Servlet " +
                    name +
                    " is not running");
        }
    }
}
