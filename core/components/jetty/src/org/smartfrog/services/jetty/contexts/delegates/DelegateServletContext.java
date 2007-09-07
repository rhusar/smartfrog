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

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.jetty.SFJetty;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import java.io.File;
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
    private Reference classPathRef = new Reference(ATTR_CLASSPATH);
    private String contextPath;
    private String resourceBase;
    private String absolutePath;
    /**
     * a log
     */
    private Log log;

    /**
     * Get the context cast to a servlet context
     * @return the servlet context of jetty
     */
    public final ServletHttpContext getServletContext() {
        return (ServletHttpContext)getContext();
    }

    public DelegateServletContext(SFJetty server, HttpContext context) {
        super(server, context);
    }

    public DelegateServletContext() {
    }

    /**
     * do all deployment short of starting the thing
     * @param declaration the description to deploy
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void deploy(Prim declaration) throws SmartFrogException, RemoteException {
        log = LogFactory.getOwnerLog(declaration);
        JettyHelper jettyHelper = new JettyHelper(declaration);
        ServletHttpContext context = new ServletHttpContext();
        setContext(context);
        jettyHelper.setServerComponent(getServer());
        String jettyhome = jettyHelper.findJettyHome();
        //context path attribute
        contextPath = declaration.sfResolve(contextPathRef, (String)null, true);
        resourceBase = declaration.sfResolve(resourceBaseRef, (String) null, true);
        absolutePath = WebApplicationHelper.deregexpPath(contextPath);
        declaration.sfReplaceAttribute(ATTR_ABSOLUTE_PATH, absolutePath);
        //hostnames
        String address = jettyHelper.getIpAddress();
        declaration.sfReplaceAttribute(ATTR_HOST_ADDRESS, address);

        //resource base is absolute or relative to jettyhome
        if (!new File(resourceBase).exists()) {
            resourceBase = jettyhome.concat(resourceBase);
        }
        //classpath stuff.
        //REVISIT: what does this bring to the table?
        String classPath = declaration.sfResolve(classPathRef, (String) null, false);
        if (classPath != null) {
            if (!new File(classPath).exists()) {
                classPath = jettyhome+classPath;
            }
            log.info("Jetty classpath="+classPath);
            context.setClassPath(classPath);
        }
        //configure the context
        log.debug("Jetty resource base ="+resourceBase);
        context.setResourceBase(resourceBase);
        log.debug("context path =" + contextPath);
        context.setContextPath(contextPath);
        context.addHandler(new ResourceHandler());
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

    /**
     * Add a mime mapping
     *
     * @param extension extension to map (no '.')
     * @param mimeType  mimetype to generate
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void addMimeMapping(String extension, String mimeType) throws RemoteException, SmartFrogException {
        getServletContext().setMimeMapping(extension, mimeType);
        log.info("Adding mime mapping "+extension+" maps to "+mimeType);
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
        Map mimeMap = getServletContext().getMimeMap();
        log.info("removing mime mapping " + extension);
        return (mimeMap.remove(extension) != null);
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
    public void addHandler(HttpHandler handler) throws SmartFrogException,
            RemoteException {
        ServletHttpContext context = getServletContext();
        context.addHandler(handler);
    }

    /**
     * remove a handler. The handler should be stopped first, though we do try
     * and do it ourselves
     * @param handler handler
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    public void removeHandler(HttpHandler handler) throws SmartFrogException, RemoteException {
        ServletHttpContext context = getServletContext();
        try {
            if(handler.isStarted()) {
                handler.stop();
            }
        } catch (InterruptedException ignore) {
            //ignore
        }
        context.removeHandler(handler);

    }


}
