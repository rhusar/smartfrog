<%@ page isErrorPage="true" %>
<html>
<head>
<title><cws:message txt='wsm_WebstoreError'/></title>
</head>
<body>
<h1><cws:message txt='wsm_WebstoreError'/></h1>
<p><cws:message txt='wsm_WebstoreErrorExplanation'/></p>
<p><cws:message txt='wsm_ErrorMessage'/></p>
<pre><font color="red">
<%= exception.getMessage() %>
</font></pre>
<h2>Stack Trace:</h2>
<%
 	java.lang.Throwable ex = exception;
	while (ex != null)
	{
		out.println("<h3>" + ex.toString() + "</h3>");
		java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(cw,true);
		ex.printStackTrace (pw);
		out.println("<pre><font color=\"red\">");
		out.println(cw.toString());
		out.println("</font></pre>");
		ex = exception.getCause();
	}
%>
<br>
</body>
</html>
