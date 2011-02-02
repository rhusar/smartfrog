<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
If you can see this text in your browser, the JSP page is not being executed
--%>
<html>
<head><title>Mombasa: Cluster and workflow manager</title></head>

<body>
<h1>Mombasa</h1>

Mombasa is a manager of virtual clusters
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

<p><a href="/happy" id="happy.root">Happy pages</a></p>
</body>
</html>