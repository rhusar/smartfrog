package org.smartfrog.services.jetty;

import org.mortbay.http.HashUserRealm;
import org.mortbay.http.HttpServer;
import org.mortbay.http.NCSARequestLog;
import org.mortbay.http.SocketListener;
import org.mortbay.http.ajp.AJP13Listener;
import org.mortbay.http.handler.DumpHandler;
import org.mortbay.http.handler.ForwardHandler;
import org.mortbay.http.handler.HTAccessHandler;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.MultiException;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * A wrapper for a Jetty http server.
 * @author Ritu Sabharwal
 */

public class SFJetty extends PrimImpl implements Prim {
  Reference jettyhomeRef = new Reference("jettyhome");
  Reference listenerPortRef = new Reference("listenerPort");
  Reference ajplistenerPortRef = new Reference("ajplistenerPort");
  Reference rootcontextPathRef = new Reference("rootcontextPath");
  Reference democontextPathRef = new Reference("democontextPath");
  Reference testcontextPathRef = new Reference("testcontextPath");
  Reference examplescontextPathRef = new Reference("examplescontextPath");
  //Reference rootclassPathRef = new Reference("rootclassPath");
  Reference rootwebAppRef = new Reference("rootwebApp");
  Reference demowebAppRef = new Reference("demowebApp");
  Reference testAppRef = new Reference("testApp");
 // Reference examplesclassPathRef = new Reference("examplesclassPath");
  Reference exampleswebAppRef = new Reference("exampleswebApp");
  Reference demo_resourceBaseRef = new Reference("demo_resourceBase");
  Reference demo_contextPathRef = new Reference("demo_contextPath");
  Reference demo_classPathRef = new Reference("demo_classPath");
  Reference javadoccontextPathRef = new Reference("javadoccontextPath");
  Reference javadocresourceBaseRef = new Reference("javadocresourceBase");
  Reference cgicontextPathRef = new Reference("cgicontextPath");
  Reference cgiresourceBaseRef = new Reference("cgiresourceBase");
  
  String jettyhome;
  int listenerPort;
  int ajplistenerPort;
  String rootcontextPath;
  String democontextPath;
  String testcontextPath;
  String examplescontextPath;
  String demo_resourceBase;
  String demo_contextPath;
  String demo_classPath;
 // String rootclassPath;
 // String examplesclassPath;
  String rootwebApp;
  String demowebApp;
  String testApp;
  String exampleswebApp;
  String javadoccontextPath;
  String javadocresourceBase;
  String cgicontextPath;
  String cgiresourceBase;
  
  /** The server */
  HttpServer server;
 
  SocketListener listener = new SocketListener();
  AJP13Listener ajplistener = new AJP13Listener();


  /** Standard RMI constructor */
  public SFJetty() throws RemoteException {
    super();
  }

  /**
   * Deploy the SFJetty component
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      server = new HttpServer();
      jettyhome = sfResolve(jettyhomeRef, ".", false);
      listenerPort = sfResolve(listenerPortRef, 8080, false);
      ajplistenerPort = sfResolve(ajplistenerPortRef, 8009, false);
      rootcontextPath = sfResolve(rootcontextPathRef, "/", false);
      democontextPath = sfResolve(democontextPathRef, "/jetty", false);
      testcontextPath = sfResolve(testcontextPathRef, "/cvs", false);
      examplescontextPath = sfResolve(examplescontextPathRef, "/examples", false);
      // rootclassPath = sfResolve(rootclassPathRef, "C:\\jetty\\Jetty-4.2.18\\demo\\webapps\\WEB-INF\\classses", false);
      rootwebApp = sfResolve(rootwebAppRef, jettyhome + "\\demo\\webapps\\root", false);
      demowebApp = sfResolve(demowebAppRef, jettyhome + "\\demo\\webapps\\jetty", false);
      testApp = sfResolve(testAppRef, "D:\\cvs\\forge", false);
      exampleswebApp = sfResolve(exampleswebAppRef, jettyhome + "\\demo\\webapps\\examples", false);
      // examplesclassPath = sfResolve(examplesclassPathRef, "C:\\jetty\\Jetty-4.2.18\\demo\\webapps\\examples\\WEB-INF\\classses", false);
      demo_contextPath = sfResolve(demo_contextPathRef, "/demo", false);
      demo_resourceBase = sfResolve(demo_resourceBaseRef, jettyhome + "\\demo\\docRoot", false);
      demo_classPath = sfResolve(demo_classPathRef, jettyhome + "\\demo\\servlets", false);
      javadoccontextPath = sfResolve(democontextPathRef, "/javadoc", false);
      javadocresourceBase = sfResolve(javadocresourceBaseRef, jettyhome + "\\javadoc", false);
      cgicontextPath = sfResolve(democontextPathRef, "/cgi-bin", false);
      cgiresourceBase = sfResolve(javadocresourceBaseRef, jettyhome + "\\cgi-bin", false);
      configureHttpServer();

  }

   /**
     * sfStart: starts Jetty Http server.
     * 
     * @exception  SmartFrogException In case of error while starting  
     * @exception  RemoteException In case of network/rmi error  
     */
    public synchronized void sfStart() throws SmartFrogException, 
    RemoteException {
        super.sfStart();
	 
	try {
		server.start();
    	} catch (MultiException mexp) {
        	throw new SmartFrogException(mexp);
        }
    }
    
/**
 * Configure the http server
 */
  public void configureHttpServer() throws SmartFrogException {
    try {

    listener.setPort(listenerPort);
    server.addListener(listener);
    ajplistener.setPort(ajplistenerPort);
    server.addListener(ajplistener);  

    WebApplicationContext rootwebcontext = new WebApplicationContext(rootwebApp);
    WebApplicationContext demowebcontext = new WebApplicationContext(demowebApp);
    WebApplicationContext exampleswebcontext = new WebApplicationContext(exampleswebApp);
    
    WebApplicationContext testcontext = new WebApplicationContext(testApp);
    ServletHttpContext democontext = new ServletHttpContext();
    ServletHttpContext javadoccontext = new ServletHttpContext();
    ServletHttpContext cgicontext = new ServletHttpContext();
   
    ForwardHandler fwdhandler = new ForwardHandler();
    HTAccessHandler hthandler = new HTAccessHandler();
    ServletHolder srcdefault = new ServletHolder();
    ServletHolder dump = new ServletHolder();
    ServletHolder cgi = new ServletHolder();
    
    democontext.setContextPath(demo_contextPath);
    javadoccontext.setContextPath(javadoccontextPath);
    democontext.setClassPath(demo_classPath);
    rootwebcontext.setContextPath(rootcontextPath);
    demowebcontext.setContextPath(democontextPath);
    testcontext.setContextPath(testcontextPath);
    exampleswebcontext.setContextPath(examplescontextPath);
    exampleswebcontext.getServletHandler();
    ServletHandler servlethandler = exampleswebcontext.getServletHandler();
    AbstractSessionManager sessionmanager = (AbstractSessionManager)servlethandler.getSessionManager();
    sessionmanager.setUseRequestedId(true);
    javadoccontext.setContextPath(javadoccontextPath);
    cgicontext.setContextPath(cgicontextPath);
    democontext.setResourceBase(demo_resourceBase);
    javadoccontext.setResourceBase(javadocresourceBase);
    cgicontext.setResourceBase(cgiresourceBase);
    fwdhandler.addForward("/forward/*","/dump");
    democontext.addHandler(fwdhandler);
    hthandler.setAccessFile(".htaccess");
    democontext.addHandler(hthandler);
    democontext.addHandler(new ResourceHandler());
    democontext.addHandler(new DumpHandler());
    democontext.addServlet("Invoker","/servlet/*",
		    "org.mortbay.jetty.servlet.Invoker");
    dump = democontext.addServlet("Dump","/dump/*:*.DUMP",
		    "org.mortbay.servlet.Dump");
    dump.put("InitParam","Value");
    democontext.addServlet("Session","/session/*",
		    "org.mortbay.servlet.SessionDump");
    democontext.addServlet("Dispatch","/dispatch/*",
		    "org.mortbay.servlet.RequestDispatchTest");
    democontext.addServlet("JSP","*.jsp",
		    "org.apache.jasper.servlet.JspServlet");   
    srcdefault = democontext.addServlet("SrcDefault","/src/*",
		    "org.mortbay.jetty.servlet.Default");
    srcdefault.setInitParameter("dirAllowed","true");
    srcdefault.setInitParameter("resourceBase",jettyhome + "\\src");
    javadoccontext.addHandler(new ResourceHandler());
    cgicontext.addHandler(new ResourceHandler());
    cgi = cgicontext.addServlet("Common Gateway Interface","/",
		    "org.mortbay.servlet.CGI");
    cgi.put("Path","/bin:/usr/bin:/usr/local/bin");
    cgi.put("ENV_TEST","Jetty home is" + jettyhome);
    System.out.println("Classpath " + democontext.getClassPath());
    server.addContext(rootwebcontext);
    server.addContext(demowebcontext);
    server.addContext(testcontext);
    server.addContext(exampleswebcontext);
    server.addContext(democontext);
    server.addContext(javadoccontext);
    server.addContext(cgicontext);
    
    server.addRealm(new HashUserRealm("Jetty Demo Realm",jettyhome + "\\etc\\demoRealm.properties"));
    server.addRealm(new HashUserRealm("Example Form-Based Authentication Area", jettyhome + "\\etc\\examplesRealm.properties"));
    NCSARequestLog requestlog = new NCSARequestLog();
    requestlog.setFilename(jettyhome + "\\logs\\yyyy_mm_dd.request.log");
    requestlog.setBuffered(false);
    requestlog.setRetainDays(90);
    requestlog.setAppend(true);
    requestlog.setExtended(true);
    requestlog.setLogTimeZone("GMT");
    String[] paths = {"/jetty/images/*", "/demo/images/*","*.css"};
    requestlog.setIgnorePaths(paths);
    server.setRequestLog(requestlog);
   } catch (Exception ex) {
        //lots of different things get thrown here
	    throw SmartFrogException.forward(ex);
   }
  }

 /**
 * Termination phase
 */
  public void sfTerminateWith(TerminationRecord status) {
   server.removeListener(listener);
   server.removeListener(ajplistener);
    try {
      server.stop();
    } catch (InterruptedException ie) {
	   Logger.log(" Interrupted on server termination " + ie);
	   ie.printStackTrace();
    }
    super.sfTerminateWith(status);
  }
}
