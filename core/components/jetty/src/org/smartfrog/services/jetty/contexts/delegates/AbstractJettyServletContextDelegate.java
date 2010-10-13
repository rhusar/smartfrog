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
import org.mortbay.jetty.servlet.Holder;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

/**
 * Abstract base class for anything that goes into a jetty servlet context, binds to the context etc.
 * <p/>
 * This class doesn't do anything in its constructor other than set up the log,
 * and doesn't implement the delegated lifecycle methods
 * --- that is a task for the implementations.
 */

public abstract class AbstractJettyServletContextDelegate implements ServletContextComponentDelegate {
    /**
     * context within which the servlet deploys.
     */
    protected DelegateServletContext context;
    protected Prim owner;
    private static final Reference nameRef = new Reference(ServletComponent.ATTR_NAME);
    private static final Reference classNameRef = new Reference(ServletComponent.ATTR_CLASSNAME);
    protected String name = null;
    protected String className = null;
    /**
     * a log
     */
    protected Log log;
    protected Holder jettyHolder = null;
    public static final String ERROR_NO_SERVLET_CONTEXT = "No servlet context is currently live";
    protected static final Reference initParamsRef = new Reference(ServletComponent.ATTR_INIT_PARAMS);
    protected static final Reference pathSpecRef = new Reference(ServletComponent.ATTR_PATH_SPEC);
    protected static final Reference initOptionsRef = new Reference(ServletComponent.ATTR_INIT_OPTIONS);
    protected static final Reference mappingsRef = new Reference(ServletComponent.ATTR_MAPPINGS);

    public AbstractJettyServletContextDelegate(Prim owner, DelegateServletContext context) {
        log = LogFactory.getOwnerLog(owner);
        this.context = context;
        this.owner = owner;
    }

    public DelegateServletContext getContext() {
        return context;
    }

    public Prim getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public Log getLog() {
        return log;
    }


    public Holder getJettyHolder() {
        return jettyHolder;
    }

    protected void setJettyHolder(Holder jettyHolder) {
        this.jettyHolder = jettyHolder;
    }


    /**
     * Returns a the name and classname of the delegate
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "name=" + name
                + "; className=" + className;
    }
    
    /**
     * From the delegate servlet context, resolve the real servlet context. Contains an assert that the argument is not
     * null.
     *
     * @param delegateServletContext the delegate context, must not be null
     * @return the jetty context
     * @throws SmartFrogDeploymentException if there is no jetty context
     */
    protected Context resolveJettyServletContext(DelegateServletContext delegateServletContext)
            throws SmartFrogDeploymentException {
        assert delegateServletContext != null : "no DelegateServletContext parameter";
        Context servletContext;
        servletContext = delegateServletContext.getServletContext();
        if (servletContext == null) {
            throw new SmartFrogDeploymentException(ERROR_NO_SERVLET_CONTEXT);
        }
        return servletContext;
    }

    /**
     * bind the name and classname parameters
     *
     * @param prim owner
     * @throws SmartFrogResolutionException resolution failure
     * @throws RemoteException network problems
     */
    protected void bindNameAndClassname(Prim prim) throws SmartFrogResolutionException, RemoteException {
        assert prim != null : "no prim parameter";
        name = prim.sfResolve(nameRef, name, true);
        className = prim.sfResolve(classNameRef, className, true);
    }

    /**
     * Bind initialisation options from the owner prim, and set the jetty holder attribute
     * @param holder holder
     * @throws SmartFrogResolutionException resolution failure
     * @throws RemoteException network problems
     */
    protected void bindAndInitHolder(Holder holder) throws SmartFrogResolutionException, RemoteException {
        setJettyHolder(holder);
        Prim prim = getOwner();
        bindNameAndClassname(prim);
        holder.setName(name);
        holder.setClassName(className);
        //apply initialisation params from the list
        Iterable<Vector<String>> paramTuples = ListUtils.resolveStringTupleList(prim, initParamsRef, true);
        for (Vector<String> tuple : paramTuples) {
            holder.setInitParameter(tuple.firstElement(), tuple.get(1));
        }
        //apply initialisation params from the context
        ComponentDescription optionsCD = prim.sfResolve(initOptionsRef, (ComponentDescription) null, true);
        org.smartfrog.sfcore.common.Context optionsContext = optionsCD.sfContext();
        Iterator iterator = optionsContext.sfAttributes();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = optionsContext.get(key);
            holder.setInitParameter(key.toString(), value.toString());
        }
    }

    /**
     * noop
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    @Override
    public void deploy() throws SmartFrogException, RemoteException {

    }

    /**
     * start the component
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    @Override
    public void start() throws SmartFrogException, RemoteException {
        try {
            log.debug("Starting " + toString());
            getJettyHolder().start();
        } catch (Exception e) {
            throw new SmartFrogException(e);
        }
    }

    /**
     * this method is here for server-specific implementation classes,
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    @Override
    public synchronized void terminate() throws RemoteException, SmartFrogException {
        try {
            if (jettyHolder != null) {
                jettyHolder.stop();
                jettyHolder = null;
            }
        } catch (Exception e) {
            throw SmartFrogException.forward(e);
        }
    }

    protected boolean isJettyHolderStarted() {
        return jettyHolder == null || !jettyHolder.isStarted();
    }
}
