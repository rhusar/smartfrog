<%
    //see http://jira.codehaus.org/browse/JETTY-761
    //and http://forums.sun.com/thread.jspa?threadID=488673
    // This attribute is NOT set when calling HttpResponse#setStatus and then
    // explicitly incuding this error page using RequestDispatcher#include()
    // So: only set by HttpResponse#sendError()
    Integer origStatus = (Integer)request.getAttribute("javax.servlet.error.status_code");
    if(origStatus != null) {
        String origMessage = (String)request.getAttribute("javax.servlet.error.message");
        if(origMessage != null) {
            response.reset();
            response.setContentType("text/html");
            response.setStatus(origStatus, origMessage); // deprecated, but works...
        }
    }
%>
