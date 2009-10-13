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
<jsp:include page="/html/mombasa-portlet/footer.jsp"/>

<logic:present name="org.apache.struts.action.EXCEPTION">
  <bean:write name="org.apache.struts.action.EXCEPTION"/>
</logic:present>


<table border="2">
  <tr bgcolor="grey">
    <th>Error</th>
  </tr>
  <logic:iterate id="parseerror"
                 name="com.hp.hpl.thor.services.mombasa.struts.errorList"
                 type="list">
    <tr>
      <td><bean:write name="parseerror"/></td>
    </tr>
  </logic:iterate>
</table>