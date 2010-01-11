/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.actions.cluster;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.BadParameterException;
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.AttributeNames;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.rmi.ConnectException;

/**
 * Created 02-Oct-2009 12:57:12
 */

public abstract class AbstractStrutsAction extends Action implements ClusterRequestAttributes {
    protected static Log LOG = LogFactory.getLog("org.smartfrog.services.cloudfarmer.client.web.action");
    protected Log log = LogFactory.getLog("org.smartfrog.services.cloudfarmer.client.web."
            + getActionName());

    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the name of the action
     */
    protected String getActionName() {
        return "AbstractMombasaAction";
    }

    /**
     * Handle by the success handler
     * @param mapping mapping object
     * @return the success handler to use
     */
    protected ActionForward success(ActionMapping mapping) {
        return mapping.findForward(ACTION_SUCCESS);
    }

    /**
     * Handle a failure by looking for the failure action
     * @param request incoming request
     * @param mapping mapping object
     * @param message error message
     * @return the next action handler
     */
    protected ActionForward failure(HttpServletRequest request, ActionMapping mapping, String message) {
        request.setAttribute(AttributeNames.ATTR_ERROR_MESSAGE, message);
        return mapping.findForward(ClusterRequestAttributes.ACTION_FAILURE);
    }

    /**
     * Report a failure
     *
     * @param request request
     * @param mapping mapping handler
     * @param message the message to include
     * @param thrown  what was thrown
     * @return the next handler
     */
    protected ActionForward failure(HttpServletRequest request, ActionMapping mapping, String message,
                                    Throwable thrown) {
        log.error(message, thrown);
        request.setAttribute(AttributeNames.ATTR_THROWN, thrown);
        request.setAttribute(AttributeNames.ATTR_ERROR_CAUSE, thrown);
        return failure(request, mapping, message);
    }

    /**
     * Report a failure to connect to a cluster controller
     *
     * @param request request
     * @param mapping mapping handler
     * @param message the message to include
     * @param thrown  what was thrown
     * @return the next handler
     */
    protected ActionForward bindFailure(HttpServletRequest request, ActionMapping mapping, String message,
                                        Throwable thrown) {
        log.error(message, thrown);
        request.setAttribute(ATTR_THROWN, thrown);
        request.setAttribute(ATTR_ERROR_MESSAGE, message);
        String cause = "";
        if (thrown instanceof ConnectException) {
            cause = "SmartFrog is not running at the target URL, or is not reachable";
        } else if (thrown instanceof SmartFrogResolutionException) {
            cause = "A cluster farmer component is not running at the target URL";
        } else if (thrown instanceof java.net.UnknownHostException) {
            cause = "The hostname of the cluster manager is not known. Check the URL, and the hosts tables/DNS";
        }
        request.setAttribute(ATTR_ERROR_CAUSE, cause);
        return mapping.findForward(AttributeNames.ACTION_BIND_FAILURE);
    }

    /**
     * Log an unimplemented operation
     *
     * @param request request
     * @param mapping mapping handle
     * @return the next handler
     */
    protected ActionForward unimplemented(HttpServletRequest request, ActionMapping mapping) {
        return failure(request, mapping, "unimplemented");
    }

    /**
     * Convert a param to an attribute; return the value (which may be null)
     *
     * @param request       request name
     * @param parameter     name to retrieve on
     * @param attributeName name to set for the attribute
     * @param required      trigger an exception if the parameter is missing
     * @return the value or null if not found and required==false
     * @throws BadParameterException the parameter is bad or missing
     */
    protected static String parameterToAttribute(HttpServletRequest request,
                                                 String parameter,
                                                 String attributeName,
                                                 boolean required) throws BadParameterException {
        String val = request.getParameter(parameter);
        if (val != null) {
            request.setAttribute(attributeName, val);
        } else {
            if (required) {
                throw new BadParameterException(new ActionMessage("error.missingParameter", parameter).toString());
            }
        }
        return val;
    }

    /**
     * Retrieve attribute state, may integrate with persistent configuration mechanisms
     * @param request http request
     * @param key     key to retrieve on
     * @return the value
     */
    protected static Object getAttributeFromRequestState(HttpServletRequest request, String key) {
        Object o = request.getAttribute(key);
        if (o == null) {
            o = request.getSession().getAttribute(key);
        }
        return o;
    }

    protected static void setAttribute(HttpServletRequest request, String key, Object value) {
        request.setAttribute(key, value);
        request.getSession().setAttribute(key, value);
    }

    protected static void removeAttribute(HttpServletRequest request, String key) {
        request.removeAttribute(key);
        request.getSession().removeAttribute(key);
    }

    /**
     * Get an option from the servlet context, extracted from the request
     * @param request request
     * @param key key to look for
     * @param defval default value
     * @return the value
     */
    protected Object getContextOption(HttpServletRequest request, String key, Object defval) {
        ServletContext context = request.getSession().getServletContext();
        Object o = context.getAttribute(key);
        return o == null ? defval : o;
    }

    /**
    * Add the {@link #ATTR_CLUSTER_HAS_MASTER} boolean, and, if that is true the {@link #ATTR_CLUSTER_MASTER} and
    * {@link #ATTR_CLUSTER_MASTER_HOSTNAME} attributes,
    *
    * @param request    request to manipulate
    * @param controller cluster controller
    */
    protected void addMasterAttributes(HttpServletRequest request, ClusterController controller) {
        request.setAttribute(ATTR_CLUSTER_CONTROLLER, controller);
        HostInstance master = controller.getMaster();
        boolean hasMaster = master != null;
        request.setAttribute(ATTR_CLUSTER_HAS_MASTER, hasMaster);
        if (hasMaster) {
            request.setAttribute(ATTR_CLUSTER_MASTER, master);
            request.setAttribute(ATTR_CLUSTER_MASTER_HOSTNAME, master.getHostname());
        } else {
            request.setAttribute(ATTR_CLUSTER_MASTER_HOSTNAME, "(no Hadoop master node)");
        }
    }
}
