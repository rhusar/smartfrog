/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.jetty.handler.AbstractHandlerContainer;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.HashSessionManager;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletMapping;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.resource.Resource;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.jetty.JettyImpl;
import org.smartfrog.services.jetty.JettyToSFLifecycle;
import org.smartfrog.services.jetty.internal.ExtendedErrorHandler;
import org.smartfrog.services.jetty.internal.ExtendedResourceHandler;
import org.smartfrog.services.jetty.internal.ExtendedSecurityHandler;
import org.smartfrog.services.jetty.internal.ExtendedServletHandler;
import org.smartfrog.services.www.FilterComponent;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a helper servlet context; it gets stuff delegated to it. It is remotable, but not a Prim-derived class.
 * Destroying this class does not destroy the servlet.
 */
public class DelegateServletContext extends DelegateApplicationContext implements ServletContextIntf {

    private Reference contextPathRef = new Reference(ATTR_CONTEXT_PATH);
    private Reference resourceBaseRef = new Reference(ATTR_RESOURCE_BASE);
    private Reference resourcePackageRef = new Reference(ATTR_RESOURCE_PACKAGE);
    private String contextPath;
    private String resourceBase;
    private String resourcePackage;
    private String absolutePath;
    private final Prim owner;
    /**
     * a log
     */
    private final Log log;
    private ResourceHandler resourceHandler;
    private HandlerCollection handlerSet;
    private JettyToSFLifecycle<HandlerCollection> handlerLifecycle;


    /**
     * Constructor
     *
     * @param server      server that is creating this
     * @param declaration the servlet declaration
     */
    public DelegateServletContext(JettyImpl server, Prim declaration) {
        super(server, null);
        owner = declaration;
        log = LogFactory.getOwnerLog(declaration);
    }

    /**
     * Get the context cast to a servlet context
     *
     * @return the servlet context of jetty
     */
    public final Context getServletContext() {
        return getContext();
    }

    /**
     * do all deployment short of starting the thing
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    @Override
    public void deploy() throws SmartFrogException, RemoteException {
        super.deploy();
        JettyHelper jettyHelper = new JettyHelper(owner);

        jettyHelper.setServerComponent(getServer());
        //context path attribute
        contextPath = owner.sfResolve(contextPathRef, (String) null, true);
        absolutePath = WebApplicationHelper.deregexpPath(contextPath);
        owner.sfReplaceAttribute(ATTR_ABSOLUTE_PATH, absolutePath);
        //hostnames
        String address = jettyHelper.getIpAddress();
        owner.sfReplaceAttribute(ATTR_HOST_ADDRESS, address);
    }


    /**
     * start: create and deploy this context
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public void start() throws SmartFrogException, RemoteException {
        super.start();
        resourceBase = FileSystem.lookupAbsolutePath(owner, resourceBaseRef, null, null, false, null);
        if (resourceBase != null) {
            FileSystem.requireFileToExist(resourceBase, false, 0);
        } else {
            resourcePackage = owner.sfResolve(resourcePackageRef, "", true);
        }
        HashSessionManager sessionManager = new HashSessionManager();
        SessionHandler sessionHandler = new SessionHandler(sessionManager);
        //to get resources seen before the other bits of the tree, we patch the handlerSet.
        Context ctx = new Context(
                null,                           //parent
                sessionHandler,                 //sessions
                //security;
                new ExtendedSecurityHandler(log),
                //servlets
                new ExtendedServletHandler(owner, log),
                //error handler
                new ExtendedErrorHandler(log));
        setContext(ctx);

        handlerSet = new HandlerCollection();

        resourceHandler = new ExtendedResourceHandler();
        Resource baseResource;
        String source;
        if (resourceBase != null) {
            source = "location " + resourceBase;
            try {
                baseResource = Resource.newResource(resourceBase);
            } catch (IOException e) {
                throw new SmartFrogDeploymentException("Failed to create a resource from " + resourceBase
                        + " : " + e,
                        e);
            }
        } else {
            source = "package " + resourcePackage;
            baseResource = Resource.newClassPathResource(resourcePackage);
        }
        log.info("Deploying " + contextPath + " from " + source);
        resourceHandler.setBaseResource(baseResource);
        //configure the context
        ctx.setBaseResource(baseResource);
        ctx.setContextPath(contextPath);


        //then patch in the servlet context *afterwards*
        handlerSet.addHandler(getContext());
        //add the resources
        handlerSet.addHandler(resourceHandler);
        handlerLifecycle = new JettyToSFLifecycle<HandlerCollection>("handlers", handlerSet);


        ContextHandlerCollection contextHandler = getServerContextHandler();
        if (contextHandler == null) {
            throw new SmartFrogLifecycleException("Cannot start " + this + " as the server is not yet deployed");
        }
        contextHandler.addHandler(handlerSet);

        //now read in the options
        //apply initialisation params from the context
        ComponentDescription optionsCD = owner.sfResolve(ATTR_OPTIONS, (ComponentDescription) null, true);
        org.smartfrog.sfcore.common.Context optionsContext = optionsCD.sfContext();
        Iterator iterator = optionsContext.sfAttributes();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = optionsContext.get(key);
            ctx.setAttribute(key.toString(), value.toString());
        }


        log.debug("Starting Jetty servlet context");
        handlerLifecycle.start();
        if (log.isDebugEnabled()) {
            dumpHandlers(getServerContextHandler().getHandlers());
        }
    }

    /**
     * Dump our handler chain, make things meaningful. This is a recursive function and logs to debug
     *
     * @param handlers handlers to dump.
     */
    private void dumpHandlers(Handler[] handlers) {

        for (Handler handler : handlers) {
            log.debug(handler.toString());
            if (handler instanceof ServletHandler) {
                ServletHandler sh = (ServletHandler) handler;
                ServletMapping[] servletMappings = sh.getServletMappings();
                if (servletMappings != null) {
                    for (ServletMapping mapping : servletMappings) {
                        log.debug(mapping.toString());
                    }
                } else {

                }
            } else {
                if (handler instanceof AbstractHandlerContainer) {
                    AbstractHandlerContainer hc = (AbstractHandlerContainer) handler;
                    dumpHandlers(hc.getChildHandlers());
                }
            }
        }
    }

    /**
     * undeploy a the servlet by stopping it and removing it from the server context handler
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public void terminate() throws RemoteException, SmartFrogException {
        if (handlerLifecycle != null) {
            try {
                if (log.isDebugEnabled()) {
                    Throwable t = new Throwable("stack trace");
                    log.info("Terminating Jetty servlet context", t);
                }
                handlerLifecycle.wrappedStop();
                ContextHandlerCollection handlers = getServerContextHandler();
                if (handlers != null) {
                    handlers.removeHandler(handlerSet);
                } else {
                    //do nothing, the server is not alive any more
                }
//            } catch (IllegalStateException ex) {
//              throw SmartFrogException.forward(ex);
            } finally {
                context = null;
                handlerSet = null;
                handlerLifecycle = null;
            }
        }
    }

    public HandlerCollection getHandlers() {
        return (HandlerCollection) getContext().getHandler();
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getResourceBase() {
        return resourceBase;
    }

    public String getContextPath() {
        return contextPath;
    }


    protected HandlerCollection getHandlerSet() {
        return handlerSet;
    }


    protected ResourceHandler getResources() {
        return resourceHandler;
    }

    /**
     * Add a mime mapping
     *
     * @param extension extension to map (no '.')
     * @param mimeType  mimetype to generate
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    @Override
    public void addMimeMapping(String extension, String mimeType) throws RemoteException, SmartFrogException {
        log.debug("Adding mime mapping " + extension + " maps to " + mimeType);
        MimeTypes mimes = getServletContext().getMimeTypes();
        mimes.addMimeMapping(extension, mimeType);
    }

    /**
     * Remove a mime mapping for an extension
     *
     * @param extension extension to unmap
     * @return true if the unmapping was successful
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    @Override
    public boolean removeMimeMapping(String extension) throws RemoteException, SmartFrogException {
        log.info("removing mime mapping " + extension);
        Map mimeMap = getServletContext().getMimeTypes().getMimeMap();
        if (mimeMap != null) {
            return (mimeMap.remove(extension) != null);
        } else {
            return false;
        }
    }

    /**
     * add a servlet
     *
     * @param servletDeclaration component declaring the servlet
     * @return the delegate that implements the servlet binding
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    @Override
    public ServletContextComponentDelegate addServlet(ServletComponent servletDeclaration)
            throws RemoteException, SmartFrogException {
        JettyServletDelegate servletDelegate = new JettyServletDelegate(this, (Prim) servletDeclaration);
        return servletDelegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContextComponentDelegate addFilter(FilterComponent declaration)
            throws RemoteException, SmartFrogException {
        return new JettyFilterDelegate(this, (Prim) declaration);
    }

    /**
     * add a handler to the server
     *
     * @param handler handler
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    public void addHandler(Handler handler) throws SmartFrogException,
            RemoteException {
        getHandlers().addHandler(handler);
    }

    /**
     * remove a handler. The handler should be stopped first, though we do try and do it ourselves
     *
     * @param handler handler
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    public void removeHandler(Handler handler) throws SmartFrogException, RemoteException {
        try {
            if (handler.isStarted()) {
                handler.stop();
            }
        } catch (Exception e) {
            log.info(e);
        }
        //remove the handler
        getHandlers().removeHandler(handler);

    }


}
