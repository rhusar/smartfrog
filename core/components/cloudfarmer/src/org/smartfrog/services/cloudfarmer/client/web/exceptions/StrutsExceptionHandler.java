package org.smartfrog.services.cloudfarmer.client.web.exceptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ExceptionConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * See JETTY-761 for the need for this
 * @link 
 */
public class StrutsExceptionHandler extends org.apache.struts.action.ExceptionHandler {

    protected static final Log log = LogFactory.getLog(StrutsExceptionHandler.class);
    public static final String STRUTS_EXCEPTION = "strutsException";
    public static final String STRUTS_REQUEST_URI = "strutsRequestURI";

    @Override
    public ActionForward execute(final Exception exception,
                                 final ExceptionConfig config,
                                 final ActionMapping mapping,
                                 final ActionForm form,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response) throws ServletException {
        String requestURI = request.getRequestURI();
        log.error("Failed to process " + requestURI + " due to " + exception, exception);
        request.setAttribute(STRUTS_EXCEPTION, exception);
        request.setAttribute(STRUTS_REQUEST_URI, requestURI);
        return new ActionForward(config.getPath());
    }
}
