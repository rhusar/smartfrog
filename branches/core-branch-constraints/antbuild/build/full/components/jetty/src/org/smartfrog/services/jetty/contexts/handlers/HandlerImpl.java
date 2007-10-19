/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.jetty.contexts.handlers;

import org.mortbay.http.HttpHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.jetty.contexts.delegates.DelegateServletContext;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * Base handler class
 * Date: 21-Jun-2004
 * Time: 22:40:14
 */
public abstract class HandlerImpl extends PrimImpl implements ServletComponent {

    /**
     * context within which the servlet deploys.
     */
    private DelegateServletContext context;


    private JettyHelper jettyHelper;

    private HttpHandler handler;
    public static final String ERROR_HANDLER_STOPPED = "Handler is stopped";
    public static final String ERROR_HANDER_UNDEFINED = "No handler";

    public HandlerImpl() throws RemoteException {
        super();
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        jettyHelper = new JettyHelper(this);
        context = (DelegateServletContext) sfResolve(ServletComponent.ATTR_SERVLET_CONTEXT,true);

    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        startHandler();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            stopHandler();
        } catch (SmartFrogException e) {
            //ignore
        }
    }

    protected void startHandler() throws SmartFrogException {
        if(handler!=null) {
            try {
                handler.start();
            } catch (Exception e) {
                throw new SmartFrogException(e);
            }
        }
    }

    protected void stopHandler() throws SmartFrogException {
        if (handler != null) {
            try {
                handler.stop();
            } catch (InterruptedException e) {
                throw new SmartFrogException(e);
            }
        }
    }

    /**
     * add a handler to the context of this smartfrog instance
     * @param handler
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     * @throws java.rmi.RemoteException
     */
    protected void addHandler(HttpHandler handler) throws SmartFrogException, RemoteException {
        context.addHandler(handler);
        this.handler=handler;
    }

    protected void removeHandler() throws SmartFrogException, RemoteException {
        if(handler!=null) {
            try {
                context.removeHandler(handler);
            } finally {
                handler=null;
            }
        }
    }



    /**
     * Liveness call in to check if this component is still alive.
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link org.smartfrog.sfcore.prim.Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if(handler==null) {
            throw new SmartFrogLivenessException(ERROR_HANDER_UNDEFINED);
        }
        if(!handler.isStarted()) {
            throw new SmartFrogLivenessException(ERROR_HANDLER_STOPPED);
        }
    }


}
