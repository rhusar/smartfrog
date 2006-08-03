/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */

package org.smartfrog.test.testwar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**

 */
public class HappyServlet extends HttpServlet {

    private String property(String propname,String defval) {
        String property = System.getProperty(propname, defval);
        return property;
    }

    public static final String PROP_PORT = "db.port";
    public static final String PROP_USER = "db.user";
    public static final String PROP_PASS = "db.passwd";
    public static final String PROP_URL = "db.url";
    public static final String PROP_DRIVER = "db.driver";

    public boolean isSet(PrintWriter out, String propname) throws IOException {
        String value = property(propname,null);
        if (value == null) {
            para(out, "missing property:" + propname);
            return false;
        }
        if (value.length() == 0) {
            para(out,"empty property:" + propname);
            return false;
        }
        para(out, propname+" = \""+value+"\"");

        return true;
    }

    private void para(PrintWriter out, String text) {
        out.println("<h3>"+text + "</h3>");
    }

    /*
    * this bit of happiness logic absolutely requires that
    * 1. the db.host param is set
    * 2. the db.passwd param is set
    * 3. the db.port is set
    */

    public boolean areDatabasePropsSet(PrintWriter out) throws IOException {
        boolean driver = isSet(out, PROP_DRIVER);
        boolean url = isSet(out, PROP_URL);
        boolean pass = isSet(out, PROP_PASS);
        boolean user = isSet(out, PROP_USER);
        return url && pass && user;
    }

    private void logThrown(PrintWriter out, Throwable thrown) throws IOException {
        para(out,"Exception " + thrown.toString());
        StringWriter sw = new StringWriter();
        out.println("<pre>");
        PrintWriter pw = new PrintWriter(sw);
        thrown.printStackTrace(pw);
        pw.flush();
        out.println(sw.toString());
        out.println("</pre>");
    }


    public boolean checkDatabaseConnection(PrintWriter out) throws IOException {
        String url = property(PROP_URL,"jdbc:mysql://localhost//test");
        String user = property(PROP_USER,"root");
        String pass = property(PROP_PASS,null);
        if ("[empty]".equals(pass)) {
            pass=null;
        }
        String driver = property(PROP_DRIVER,"com.mysql.jdbc.Driver");
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

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Happiness Page</title</head><body>");
        boolean happy = true;
        String message = "<i>The database is configured </i>";
        //happy = areDatabasePropsSet(out);

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
        para(out,message);
        out.println("</body></html>");
    }
}
