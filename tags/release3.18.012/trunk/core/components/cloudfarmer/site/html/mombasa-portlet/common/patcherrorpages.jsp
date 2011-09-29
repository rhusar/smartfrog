<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="org.smartfrog.services.cloudfarmer.client.web.exceptions.StrutsExceptionHandler" %>

<%!
  Log log = LogFactory.getLog(StrutsExceptionHandler.class);
%>

<%
  //see http://jira.codehaus.org/browse/JETTY-761
  //and http://forums.sun.com/thread.jspa?threadID=488673
  // This attribute is NOT set when calling HttpResponse#setStatus and then
  // explicitly incuding this error page using RequestDispatcher#include()
  // So: only set by HttpResponse#sendError()
  Integer origStatus = (Integer) request.getAttribute("javax.servlet.error.status_code");
  if (origStatus != null) {
    String origMessage = (String) request.getAttribute("javax.servlet.error.message");
    if (origMessage != null) {
      response.reset();
      response.setContentType("text/html");
      response.setStatus(origStatus, origMessage); // deprecated, but works...
    }
  }
  Throwable t = (Throwable) request.getAttribute("org.apache.struts.action.EXCEPTION");
  if (t != null) {
    log.error("Reporting JSP exception: " + t, t);
  }
%>
