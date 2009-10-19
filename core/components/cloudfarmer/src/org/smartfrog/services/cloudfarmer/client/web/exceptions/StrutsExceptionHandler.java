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
 * 
 */
public class StrutsExceptionHandler extends org.apache.struts.action.ExceptionHandler {

    protected static final Log log = LogFactory.getLog(StrutsExceptionHandler.class);

    @Override
    public ActionForward execute(final Exception exception,
                                 final ExceptionConfig config,
                                 final ActionMapping mapping,
                                 final ActionForm form,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response) throws ServletException {
        log.error("Failed to process " + request.getRequestURI() + " due to " + exception, exception);
        request.setAttribute("strutsException", exception);
        return new ActionForward(config.getPath());
    }
}
