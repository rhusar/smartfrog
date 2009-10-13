<%@ page isErrorPage="true" %>
<%
  /*
  * This is the error reporting page. If an exception is attached in the attribute "org.apache.struts.action.EXCEPTION", it gets displayed
  * If an errorMessage is attached, it is printed
  */
%>
<%@ include file="/html/mombasa-portlet/init.jsp" %>
<jsp:include page="/html/mombasa-portlet/header.jsp"/>


<h2>Error: <bean:write name="errorMessage"/></h2>

<div>
The cluster controller is not running or not reachable. Please
start the controller, or correct the controller settings in the
  <html:link
      page="/portlet_action/mombasa-portlet/cluster/admin">administration tab</html:link>
</div>

<logic:present name="errorCause">
  <div>
    <bean:write name="errorCause"/>
  </div>
</logic:present>

<div>
<jsp:include page="/html/mombasa-portlet/cluster/cluster_nav.jsp" flush="true"/>
</div>

  
<logic:present name="org.apache.struts.action.EXCEPTION">
  <pre>
    <bean:write name="org.apache.struts.action.EXCEPTION"/>
  </pre>
</logic:present>

<logic:messagesPresent>
	<span class="portlet-msg-error">
	<html:errors/>
	</span>
</logic:messagesPresent>


