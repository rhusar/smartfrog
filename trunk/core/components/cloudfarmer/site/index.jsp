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
    <li><a href="/mombasa-portlet/cluster/view.do" id="cluster.root">Cluster Links</a></li>
    <li><a href="/mombasa-portlet/cluster/list.do" id="cluster.list">List the cluster</a></li>
</ul>

<h2>Workflow</h2>
<ul>
    <li><a href="/mombasa-portlet/view.do" id="workflow.root">Workflow Links</a></li>
    <li><a href="/mombasa-portlet/workflowList.do" id="workflow.list">List the workflow</a></li>
</ul>

<h2>Other</h2>
<ul>
    <li><a href="/happy" id="happy.root">Happy pages</a></li>
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