<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
If you can see this, the JSP page is not being executed
--%>
<html>
<head><title>Mombasa</title></head>

<body>
<h1>Mombasa</h1>

Mombasa is the way to see the elephants
<h2>Cluster</h2>
<ul>
    <li><a href="/html/mombasa-portlet/cluster/cluster_nav.jsp">Workflow Links</a></li>
    <li><a href="/mombasa-portlet/cluster/list.do">List the cluster</a></li>
</ul>

<h2>Workflow</h2>
<ul>
    <li><a href="/html/mombasa-portlet/nav.jsp">Cluster Links</a></li>
    <li><a href="/html/mombasa-portlet/cluster/cluster_nav.jsp">Workflow Links</a></li>
    <li><a href="/mombasa-portlet/workflowList.do">List the cluster</a></li>
</ul>

<h2>Other</h2>
<ul>
    <li><a href="/index.jsp">Index.jsp</a></li>
</ul>
<h2>Errors</h2>
<ul>
    <li><a href="/error.jsp">Error.jsp</a></li>
    <li><a href="/error.jsp?status=400">Error 400</a></li>
    <li><a href="/error.jsp?status=403">Error 403</a></li>
    <li><a href="/error.jsp?status=404">Error 404</a></li>
    <li><a href="/error.jsp?status=500">Error 500</a></li>
</ul>
</body>
</html>