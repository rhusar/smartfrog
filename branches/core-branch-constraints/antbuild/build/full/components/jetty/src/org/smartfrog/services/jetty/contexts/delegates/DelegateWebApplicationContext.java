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

import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.jetty.SFJetty;
import org.smartfrog.services.jetty.contexts.JettyWebApplicationContext;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.rmi.RemoteException;

/**
 * This represents a web application that has been deployed on a jetty system
 */
public class DelegateWebApplicationContext extends DelegateApplicationContext
        implements JettyWebApplicationContext {

    private Reference contextPathRef = new Reference(ATTR_CONTEXT_PATH);
    private Reference requestIdRef = new Reference(ATTR_REQUEST_ID);

    private String contextPath = null;
    private String webApp = null;
    private boolean requestId = false;


    private WebApplicationContext application = new WebApplicationContext();


    public DelegateWebApplicationContext(SFJetty server, Prim declaration) {
        super(server, null);
    }

    public DelegateWebApplicationContext() {
    }

    /**
     * at deploy time, do everything except starting the component
     * @param declaration
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void deploy(Prim declaration)
            throws SmartFrogException, RemoteException {
        super.deploy();
        webApp =
                FileSystem.lookupAbsolutePath(declaration,
                        ATTR_FILE,
                        null,
                        null,
                        true,
                        null);
        //sanity check
        File webappFile = new File(webApp);
        if (!webappFile.exists()) {
            throw new SmartFrogDeploymentException(ERROR_FILE_NOT_FOUND +
                    webappFile);
        }
        //request ID
        requestId = declaration.sfResolve(requestIdRef, requestId, false);

        contextPath = declaration.sfResolve(contextPathRef,
                (String) null,
                true);
        String absolutePath = contextPath;
        declaration.sfReplaceAttribute(ATTR_ABSOLUTE_PATH, absolutePath);
        application.setContextPath(contextPath);
        application.setWAR(webApp);
        ServletHandler servlethandler = application.getServletHandler();
        AbstractSessionManager sessionmanager = (AbstractSessionManager)
                servlethandler.getSessionManager();
        sessionmanager.setUseRequestedId(requestId);
        setContext(application);
    }


}
