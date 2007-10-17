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
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.servlet.Context;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.jetty.JettyImpl;
import org.smartfrog.services.jetty.JettyToSFLifecycle;
import org.smartfrog.services.jetty.internal.ExtendedServletHandler;
import org.smartfrog.services.jetty.internal.ExtendedSecurityHandler;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * This is a helper servlet context; it gets stuff delegated to it.
 * It is remotable, but not a Prim-derived class.
 * Destroying this class does not destroy the servlet.
 */
public class DelegateServletContext extends DelegateApplicationContext implements ServletContextIntf {

    private Reference contextPathRef = new Reference(ATTR_CONTEXT_PATH);
    private Reference resourceBaseRef = new Reference(ATTR_RESOURCE_BASE);
    private String contextPath;
    private String resourceBase;
    private String absolutePath;
    private Prim owner;
    /**
     * a log
     */
    private Log log;
    private ResourceHandler resources;
    private HandlerCollection handlerSet;
    private JettyToSFLifecycle<HandlerCollection> handlerLifecycle;


    /**
     * Constructor
     * @param server server that is creating this
     * @param context the context
     * @param declaration the servlet declaration
     */
    public DelegateServletContext(JettyImpl server, Context context, Prim declaration) {
        super(server, context);
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
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void deploy() throws SmartFrogException, RemoteException {
        super.deploy();
        JettyHelper jettyHelper = new JettyHelper(owner);

        jettyHelper.setServerComponent(getServer());
        //context path attribute
        contextPath = owner.sfResolve(contextPathRef, (String)null, true);
        absolutePath = WebApplicationHelper.deregexpPath(contextPath);
        owner.sfReplaceAttribute(ATTR_ABSOLUTE_PATH, absolutePath);
        //hostnames
        String address = jettyHelper.getIpAddress();
        owner.sfReplaceAttribute(ATTR_HOST_ADDRESS, address);
    }


    /**
     * start: deploy this context
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException In case of network/rmi error
     */
    @Override
    public void start() throws SmartFrogException, RemoteException {
        super.start();
        resourceBase = FileSystem.lookupAbsolutePath(owner, resourceBaseRef, null, null, true, null);
        FileSystem.requireFileToExist(resourceBase, false, 0);

        //to get resources seen before the other bits of the tree, we patch the handlerSet.
        Context context = new Context(
                null,                           //parent
                null,                           //sessions
                new ExtendedSecurityHandler(),  //security; can be null
                new ExtendedServletHandler(),   //servlets
                null); //error handler
        setContext(context);

        handlerSet = new HandlerCollection();

        resources = new ResourceHandler();
        resources.setResourceBase(resourceBase);

        //configure the context
        context.setContextPath(contextPath);
        context.setResourceBase(resourceBase);
        log.info("Deploying " + contextPath + " from " + resourceBase);

        //add the resources
        handlerSet.addHandler(resources);
        //then patch in the servlet context *afterwards*
        handlerSet.addHandler(getContext());
        handlerLifecycle = new JettyToSFLifecycle<HandlerCollection>("handlers", handlerSet);


        ContextHandlerCollection contextHandler = getServerContextHandler();
        if (contextHandler == null) {
            throw new SmartFrogLifecycleException("Cannot start " + this + " as the server is not yet deployed");
        }
        log.info("Starting Jetty servlet context");
        contextHandler.addHandler(handlerSet);
        handlerLifecycle.start();
    }


    /**
     * undeploy a context. If the server is already stopped, this the undeployment is skipped without an error. The
     * context field is set to null, to tell the system to skip this in future.
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException In case of network/rmi error
     */
    @Override
    public void terminate() throws RemoteException, SmartFrogException {
        if (handlerLifecycle != null) {
            try {
                log.info("Terminating Jetty servlet context");
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
                handlerLifecycle=null;
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
        return resources;
    }

    /**
     * Add a mime mapping
     *
     * @param extension extension to map (no '.')
     * @param mimeType  mimetype to generate
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void addMimeMapping(String extension, String mimeType) throws RemoteException, SmartFrogException {
        log.info("Adding mime mapping "+extension+" maps to "+mimeType);
        MimeTypes mimes = getServletContext().getMimeTypes();
        mimes.addMimeMapping(extension,mimeType);
    }

    /**
     * Remove a mime mapping for an extension
     *
     * @param extension extension to unmap
     * @return true if the unmapping was successful
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems

     */
    public boolean removeMimeMapping(String extension) throws RemoteException, SmartFrogException {
        log.info("removing mime mapping " + extension);
        Map mimeMap = getServletContext().getMimeTypes().getMimeMap();
        if(mimeMap!=null) {
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
     * @throws RemoteException network problems
     */
    public ServletContextComponentDelegate addServlet(ServletComponent servletDeclaration) throws RemoteException, SmartFrogException {
        JettyServletDelegate servletDelegate=new JettyServletDelegate(this,(Prim)servletDeclaration);
        return servletDelegate;
    }


    /**
     * add a handler to the server
     *
     * @param handler handler
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void addHandler(Handler handler) throws SmartFrogException,
            RemoteException {
        getHandlers().addHandler(handler);
    }

    /**
     * remove a handler. The handler should be stopped first, though we do try
     * and do it ourselves
     * @param handler handler
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void removeHandler(Handler handler) throws SmartFrogException, RemoteException {
        try {
            if(handler.isStarted()) {
                handler.stop();
            }
        } catch (Exception ignore) {
            log.info(ignore);
        }
        //remove the handler
        getHandlers().removeHandler(handler);

    }


}
