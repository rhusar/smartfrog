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

import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.AttributeNames;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.BadParameterException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.rmi.ConnectException;

/**
 * Created 02-Oct-2009 12:57:12
 */

public abstract class AbstractStrutsAction extends Action implements ClusterRequestAttributes {
    protected static Log LOG = LogFactory.getLog(" com.hp.hpl.thor.services.mombasa.struts.action");
    protected Log log = LogFactory.getLog(" com.hp.hpl.thor.services.mombasa.struts.action."
            + getActionName());

    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the name of the action
     */
    protected String getActionName() {
        return "AbstractMombasaAction";
    }

    protected ActionForward success(ActionMapping mapping) {
        return mapping.findForward(ACTION_SUCCESS);
    }

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
     * This is a funny in that it tries to get the attribute from any of request/session or portlet preferences It
     * doesnt care which, only that it can be found.
     *
     * @param request http request
     * @param key     key to retrieve on
     * @return
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

    public static PortletRequest extractPortletRequest(HttpServletRequest request) {
        return (PortletRequest) request.getAttribute("javax.portlet.request");
    }

    public PortletConfig extractPortletConfig(HttpServletRequest request) {
        PortletConfig cfg = (PortletConfig) request.getAttribute("javax.portlet.config");
        return cfg;
    }

    /**
     * retrieves a value from the portlet space
     *
     * @param request request
     * @param key     key
     * @param defVal  default value, can be null
     * @return the value or the default value
     */
    protected String getPortletPrefsValue(HttpServletRequest request, String key, String defVal) {
        PortletRequest pr = extractPortletRequest(request);
        PortletPreferences prefs = pr.getPreferences();
        return prefs.getValue(key, defVal);
    }

    /**
     * Set a value in the portlet preferences
     *
     * @param request request to work with
     * @param key     key to set
     * @param value   value
     * @throws ReadOnlyException  cannot set an RO property
     * @throws ValidatorException trouble storing
     * @throws IOException        trouble storing
     */
    protected void setPortletPrefsValue(HttpServletRequest request, String key, String value)
            throws ReadOnlyException, ValidatorException, IOException {
        PortletRequest pr = extractPortletRequest(request);
        PortletPreferences prefs = pr.getPreferences();
        prefs.setValue(key, value);
        prefs.store();
    }

    /**
     * Get the portlet request
     *
     * @param request incoming request
     * @return the portlet request
     */
    public static PortletRequest getPortletRequest(HttpServletRequest request) {
        return (PortletRequest) request.getAttribute("javax.portlet.request");
    }

    public static PortletSession getPortletSession(HttpServletRequest request) {
        PortletRequest pr = getPortletRequest(request);
        return pr.getPortletSession(true);
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
            request.setAttribute(ATTR_CLUSTER_MASTER_HOSTNAME, "");
        }
    }
}
