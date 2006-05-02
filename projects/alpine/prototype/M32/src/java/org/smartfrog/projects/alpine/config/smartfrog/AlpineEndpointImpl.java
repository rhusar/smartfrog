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

package org.smartfrog.projects.alpine.config.smartfrog;

import org.smartfrog.projects.alpine.core.AlpineContext;
import org.smartfrog.projects.alpine.core.ContextConstants;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.handlers.InstanceHandlerFactory;
import org.smartfrog.projects.alpine.interfaces.MessageHandlerFactory;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.WebApplicationHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * binding from smartfrog to configuring an endpoint
 */
public class AlpineEndpointImpl extends PrimImpl implements AlpineEndpoint {

    private Log log;
    private String name;
    private String getContentType;
    private String getMessage;
    private int getResponseCode;
    private String wsdlResource;
    private String path;
    private EndpointContext epx;
    private Vector handlers;
    private String role;

    public AlpineEndpointImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is started and if there is a parent the
     * deployed component is added to the heartbeat. Subclasses can override to provide additional deployment behavior.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        log = LogFactory.getOwnerLog(this);
        name = sfResolve(ATTR_NAME, "", true);
        path = sfResolve(ATTR_PATH, "", true);
        wsdlResource = sfResolve(ATTR_WSDL, "", false);
        getContentType = sfResolve(ATTR_CONTENT_TYPE, "", false);
        getMessage = sfResolve(ATTR_GET_MESSAGE, "", false);
        getResponseCode = sfResolve(ATTR_GET_RESPONSECODE, 200, false);
        handlers = sfResolve(ATTR_HANDLER_LIST, handlers, true);
        role = sfResolve(ContextConstants.ATTR_ROLE, "Server", false);
        Prim servlet = sfResolve(ATTR_SERVLET, (Prim) null, true);
        String servletPath = servlet.sfResolve(ApplicationServerContext.ATTR_ABSOLUTE_PATH, "", true);
        WebApplicationHelper helper = new WebApplicationHelper(this);
        String absolutePath = helper.concatPaths(servletPath, path);
        sfReplaceAttribute(ApplicationServerContext.ATTR_ABSOLUTE_PATH, absolutePath);
/*
        TODO: work out our full URL.
        String url;
        sfReplaceAttribute(ContextConstants.ATTR_URL, url);
*/


    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        AlpineContext context = AlpineContext.getAlpineContext();
        epx = new EndpointContext();
        putIfSet(epx, ContextConstants.ATTR_GET_MESSAGE, getMessage);
        putIfSet(epx, ContextConstants.ATTR_WSDL, wsdlResource);
        putIfSet(epx, ContextConstants.ATTR_NAME, name);
        putIfSet(epx, ContextConstants.ATTR_GET_CONTENT_TYPE, getContentType);
        epx.put(ContextConstants.ATTR_GET_RESPONSECODE, getResponseCode);
        putIfSet(epx, ContextConstants.ATTR_ROLE, role);
        copyIfSet(epx, ContextConstants.ATTR_ROLE);

        //the handler list is a list of handler factories
        List<MessageHandlerFactory> handlerList = new ArrayList(handlers.size());
        for (Object handler : handlers) {
            if (handler instanceof String) {
                //strings are there for backwards compatiblity
                handlerList.add(new InstanceHandlerFactory(this, handler.toString()));
            } else if (handler instanceof NonRemotableHandlerFactory) {
                //this is how handlers should be added.
                NonRemotableHandlerFactory metafactory = (NonRemotableHandlerFactory) handler;
                handlerList.add(metafactory.createFactory());
            } else if (handler instanceof Prim) {
                Prim prim = (Prim) handler;
                throw new SmartFrogException("Not able to convert to a handler factory", prim);
            } else {
                throw new SmartFrogException("Unknown handler type: " + handler);
            }
        }
        epx.put(ContextConstants.ATTR_HANDLERS, handlerList);

        //now register a new endpoint
        context.getEndpoints().register(path, epx);

        //and tell the world
        if (log.isInfoEnabled()) {
            log.info("Deployed Alpine endpoint " + name + " at " + path);
        }
    }

    private void copyIfSet(EndpointContext epx, String attribute)
            throws SmartFrogResolutionException,
            RemoteException {
        putIfSet(this.epx, attribute,
                sfResolve(attribute, (String) null, false));
    }

    private void putIfSet(EndpointContext epx, String key, String value) {
        if (value.length() > 0) {
            epx.put(key, value);
        }
    }


    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        AlpineContext context = AlpineContext.getAlpineContext();
        context.getEndpoints().unregister(epx);
        if (log.isInfoEnabled()) {
            log.info("Undeployed Alpine endpoint " + name + " at " + path);
        }

    }

    /**
     * Liveness call in to check if this component is still alive. This method can be overriden to check other state of
     * a component. An example is Compound where all children of the compound are checked. This basic check updates the
     * liveness count if the ping came from its parent. Otherwise (if source non-null) the liveness count is decreased
     * by the sfLivenessFactor attribute. If the count ever reaches 0 liveness failure on tha parent has occurred and
     * sfLivenessFailure is called with source this, and target parent. Note: the sfLivenessCount must be decreased
     * AFTER doing the test to correctly count the number of ping opportunities that remain before invoking
     * sfLivenessFailure. If done before then the number of missing pings is reduced by one. E.g. if sfLivenessFactor is
     * 1 then a sfPing from the parent sets sfLivenessCount to 1. The sfPing from a non-parent would reduce the count to
     * 0 and immediately fail.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (epx == null) {
            throw new SmartFrogLivenessException("Not started");
        }
    }

}
