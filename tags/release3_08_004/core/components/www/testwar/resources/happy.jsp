<%@ page import="java.io.IOException" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%--

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private String property(String propname) {
    return System.getProperty(propname);
}

    public static final String PROP_USER = "db.port";
    public static final String PROP_PASS = "db.passwd";
    public static final String PROP_URL = "db.url";
    public static final String PROP_DRIVER = "db.driver";

    public boolean isSet(JspWriter out, String propname) throws IOException {
        String value = property(propname);
        if (value == null) {
            out.println("<p>missing property:" + propname + "<p>");
            return false;
        }
        if (value.length() == 0) {
            out.println("empty property:" + propname + "<p>");
            return false;
        }
        return true;
    }

    /*
    * this bit of happiness logic absolutely requires that
    * 1. the db.host param is set
    * 2. the db.passwd param is set
    * 3. the db.port is set
    */

    public boolean areDatabasePropsSet(JspWriter out) throws IOException {
        boolean driver = isSet(out, PROP_DRIVER);
        boolean url = isSet(out, PROP_URL);
        boolean pass = isSet(out, PROP_PASS);
        boolean user = isSet(out, PROP_USER);
        return url && pass && user;
    }

    private void logThrown(JspWriter out, Throwable thrown) throws IOException {
        out.println("Exception " + thrown.toString());
        StringWriter sw = new StringWriter();
        out.println("<pre>");
        PrintWriter pw = new PrintWriter(sw);
        thrown.printStackTrace(pw);
        pw.flush();
        out.println(sw.toString());
        out.println("</pre>");
    }


    public boolean checkDatabaseConnection(JspWriter out) throws IOException {
        String url = property(PROP_URL);
        String user = property(PROP_USER);
        String pass = property(PROP_PASS);
        String driver = property(PROP_DRIVER);
        Throwable t = null;
        try {
            Class.forName(driver).newInstance();
            Connection connection;
            connection = DriverManager.getConnection(url, user, pass);
            connection.close();
            return true;
        } catch (ClassNotFoundException e) {
            t = e;
        } catch (SQLException e) {
            t = e;
        } catch (InstantiationException e) {
            t = e;
        } catch (IllegalAccessException e) {
            t = e;
        }
        logThrown(out, t);
        return false;
    }
%>

<html>
<head><title>Happiness page</title></head>

<body>
<h1>Verifying database configuration</h1></body>

<%
    boolean happy = true;
    String message = "<i>The database is configured </i><p>";
    happy = areDatabasePropsSet(out);

    if (happy) {
        happy = checkDatabaseConnection(out);
        if (!happy) {
            message = "<i>Unable to connect to the database</i>";
        }
    } else {
        message = "<i>Database configuration parameters are missing</i>";
    }
    if (!happy) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    out.write(message);

%>



</html>