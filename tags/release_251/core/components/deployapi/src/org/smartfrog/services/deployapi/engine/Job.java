<%@ page isErrorPage="true" %>
<html>
<head>
<title>Compiere Error</title>
</head>
<body>
<h1>Compiere Error</h1>
<p>Sorry, an error occured. If the error persists, please inform us.</p>
<p>Error Message:</p>
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
