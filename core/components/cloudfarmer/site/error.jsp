<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
This page exists to make sure that JSP is really being parsed and handled properly
--%>
<%
    String codestr = request.getParameter("status");
    int code = 200;
    if (codestr != null) {
        code = Integer.valueOf(codestr);
    }
    response.setStatus(code);
%>
<html>
<head><title>JSP Error code</title></head>
<body>
<h1>Page status = <%= code %></h1>

<ul>
    <li><a href="error.jsp?status=400">Error 400</a> </li>
    <li><a href="error.jsp?status=403">Error 403</a> </li>
    <li><a href="error.jsp?status=404">Error 404</a> </li>
    <li><a href="error.jsp?status=500">Error 500</a> </li>
</ul>

</body>
</html>