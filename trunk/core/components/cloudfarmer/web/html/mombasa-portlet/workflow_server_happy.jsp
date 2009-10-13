<%@ page import="java.io.IOException,
                 java.io.InputStream"
         session="false" %>
<%@ include file="/html/mombasa-portlet/init.jsp" %>
<jsp:include page="/html/mombasa-portlet/header.jsp"/>

<h2>Happy Workflow Server</h2>

<%!

  /* This code is from Happyaxis.jsp, which was originally my work; its been around for a while though and now holds
   * the Apache license
  */

  /*
  * Copyright 2002,2004 The Apache Software Foundation.
  * 
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  *      http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

  /*
  * Happiness tests for axis. These look at the classpath and warn if things
  * are missing. Normally addng this much code in a JSP page is mad
  * but here we want to validate JSP compilation too, and have a drop-in
  * page for easy re-use
  * @author Steve 'configuration problems' Loughran
  * @author dims
  * @author Brian Ewins
  */
  /*
  * Happiness tests for axis2. These look at the classpath and warn if things
  * are missing. Normally addng this much code in a JSP page is mad
  * but here we want to validate JSP compilation too, and have a drop-in
  * page for easy re-use
  */


  /**
   * test for a class existing
   * @param classname class name to look for
   * @return class iff present
   */
  Class classExists(String classname) {
    try {
      return Class.forName(classname);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  /**
   * test for resource on the classpath
   * @param resource resource to look for
   * @return true iff present
   */
  boolean resourceExists(String resource) {
    boolean found;
    InputStream instream = getClass().getResourceAsStream(resource);
    found = instream != null;
    if (instream != null) {
      try {
        instream.close();
      } catch (IOException e) {
      }
    }
    return found;
  }


  /**
   * probe for a class, print an error message is missing
   * @param out stream to print stuff
   * @param category text like "warning" or "error"
   * @param classname class to look for
   * @param jarFile where this class comes from
   * @param errorText extra error text
   * @param homePage where to d/l the library
   * @return the number of missing classes
   * @param description any description
   * @throws IOException on problems
   */
  int probeClass(JspWriter out,
                 String category,
                 String classname,
                 String jarFile,
                 String description,
                 String errorText,
                 String homePage) throws IOException {
    try {
      Class clazz = classExists(classname);
      if (clazz == null) {
        String url = "";
        if (homePage != null) {
          url = "<br>  See <a href=" + homePage + ">" + homePage + "</a>";
        }
        out.write("<p>" + category + ": could not find class " + classname
            + " from file <b>" + jarFile
            + "</b><br>  " + errorText
            + url
            + "<p>");
        return 1;
      } else {
        String location = getLocation(out, clazz);
        if (location == null) {
          out.write("Found " + description + " (" + classname + ")<br>");
        } else {
          out.write("Found " + description + " (" + classname + ") at " + location + "<br>");
        }
        return 0;
      }
    } catch (NoClassDefFoundError ncdfe) {
      String url = "";
      if (homePage != null) {
        url = "<br>  See <a href=" + homePage + ">" + homePage + "</a>";
      }
      out.write("<p>" + category + ": could not find a dependency"
          + " of class " + classname
          + " from file <b>" + jarFile
          + "</b><br> " + errorText
          + url
          + "<br>The root cause was: " + ncdfe.getMessage()
          + "<br>This can happen e.g. if " + classname + " is in"
          + " the 'common' classpath, but a dependency like "
          + " activation.jar is only in the webapp classpath."
          + "<p>");
      return 1;
    } catch (Throwable thrown) {
      out.write("<p>" + category + ": could not load the class"
          + classname
          + " from file <b>" + jarFile
          + "</b><br> " + errorText
          + "<br>The root cause was: " + thrown.toString()
          + "<p>");
      return 1;
    }

  }

  /**
   * get the location of a class
   * @param out outstream
   * @param clazz class to probe
   * @return the jar file or path where a class was found
   */

  String getLocation(JspWriter out,
                     Class clazz) {
    try {
      java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
      String location = url.toString();
      if (location.startsWith("jar")) {
        url = ((java.net.JarURLConnection) url.openConnection()).getJarFileURL();
        location = url.toString();
      }

      if (location.startsWith("file")) {
        java.io.File file = new java.io.File(url.getFile());
        return file.getAbsolutePath();
      } else {
        return url.toString();
      }
    } catch (Throwable t) {
    }
    return "an unknown location";
  }

  /**
   * a class we need if a class is missing
   * @param out stream to print stuff
   * @param classname class to look for
   * @param jarFile where this class comes from
   * @param description any description
   * @param errorText extra error text
   * @param homePage where to d/l the library
   * @throws IOException when needed
   * @return the number of missing libraries (0 or 1)
   */
  int needClass(JspWriter out,
                String classname,
                String jarFile,
                String description,
                String errorText,
                String homePage) throws IOException {
    return probeClass(out,
        "<b>Error</b>",
        classname,
        jarFile,
        description,
        errorText,
        homePage);
  }

  /**
   * print warning message if a class is missing
   * @param out stream to print stuff
   * @param classname class to look for
   * @param jarFile where this class comes from
   * @param description any description
   * @param errorText extra error text
   * @param homePage where to d/l the library
   * @throws IOException when needed
   * @return the number of missing libraries (0 or 1)
   */
  int wantClass(JspWriter out,
                String classname,
                String jarFile,
                String description,
                String errorText,
                String homePage) throws IOException {
    return probeClass(out,
        "<b>Warning</b>",
        classname,
        jarFile,
        description,
        errorText,
        homePage);
  }

  /**
   * probe for a resource existing,
   * @param out outstream
   * @param resource resource wanted
   * @param errorText text to print on failure
   * @throws Exception
   */
  int wantResource(JspWriter out,
                   String resource,
                   String errorText) throws Exception {
    if (!resourceExists(resource)) {
      out.write("<p><b>Warning</b>: could not find resource " + resource
          + "<br>"
          + errorText);
      return 0;
    } else {
      out.write("found " + resource + "<br>");
      return 1;
    }
  }


  /**
   *  get servlet version string
   * @return the servlet version
   */

  public String getServletVersion() {
    ServletContext context = getServletConfig().getServletContext();
    int major = context.getMajorVersion();
    int minor = context.getMinorVersion();
    return Integer.toString(major) + '.' + Integer.toString(minor);
  }


  /**
   *
   * @param out outstream
   * @param prefix pattern to match on
   * @throws IOException on failures
   */
  void dumpSystemProperties(JspWriter out, String prefix) throws IOException {
    Enumeration e = null;
    try {
      e = System.getProperties().propertyNames();
    } catch (Exception se) {
    }
    if (e != null) {
      out.write("<pre>");
      for (; e.hasMoreElements();) {
        String key = (String) e.nextElement();
        if (key.startsWith(prefix)) {
          out.write(key + "=" + System.getProperty(key) + "\n");
        }
      }
      out.write("</pre><p>");
    } else {
      out.write("System properties are not accessible<p>");
    }
  }

  String getServerInfo() {
    return getServletConfig().getServletContext().getServerInfo();
  }

%>

<%
  /*
  dumpSystemProperties(out,"java.");
  */
%>

<jsp:include page="/html/mombasa-portlet/footer.jsp"/>
