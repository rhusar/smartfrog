package org.smartfrog.services.jetty;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.mortbay.http.HttpServer;
import org.mortbay.http.HashUserRealm;
import org.mortbay.http.BasicAuthenticator;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.NCSARequestLog;
import org.mortbay.util.MultiException;

/**
 * A wrapper for a Jetty http server.
 * @author Ritu Sabharwal
 */

public class SFJetty extends CompoundImpl implements Compound,JettyIntf {

    protected Reference jettyhomeRef = new Reference(JETTY_HOME);

    /**
     * Jetty home path
     */
    protected String jettyhome;


    protected JettyHelper jettyHelper = new JettyHelper(this);

    /**
     * The Http server
     */
    protected HttpServer server;

    protected boolean enableLogging=false;

    /**
     * Standard RMI constructor
     */
    public SFJetty() throws RemoteException {
        super();
    }

  /**
   * Deploy the SFJetty component and publish the information
   * @exception  SmartFrogException In case of error while deploying
   * @exception  RemoteException In case of network/rmi error
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
    try {

        server = new HttpServer();
        jettyHelper.cacheJettyServer(server);
        jettyhome = sfResolve(jettyhomeRef, jettyhome, true);
        jettyHelper.cacheJettyHome(jettyhome);
        enableLogging=sfResolve(ENABLE_LOGGING,enableLogging,true);
        configureHttpServer();
        super.sfDeploy();

    } catch (Exception ex){
       throw SmartFrogDeploymentException.forward(ex);
    }
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
	   } catch (Exception mexp) {
		   throw SmartFrogException.forward(mexp);
	   }
   }

  /**
   * Configure the http server
   */
  public void configureHttpServer() throws SmartFrogException {
      try {
          if(enableLogging) {
              NCSARequestLog requestlog = new NCSARequestLog();
              requestlog.setFilename(jettyhome +
                      "/logs/yyyy_mm_dd.request.log");
              requestlog.setBuffered(false);
              requestlog.setRetainDays(90);
              requestlog.setAppend(true);
              requestlog.setExtended(true);
              requestlog.setLogTimeZone("GMT");
              String[] paths = {"/jetty/images/*",
                                "/demo/images/*", "*.css"};
              requestlog.setIgnorePaths(paths);
              server.setRequestLog(requestlog);
          }
      } catch (Exception ex) {
          throw SmartFrogException.forward(ex);
      }
  }

  /**
   * Termination phase
   */
  public void sfTerminateWith(TerminationRecord status) {
	  try {
          if(server!=null) {
		    server.stop();
          }
	  } catch (InterruptedException ie) {
            if (sfLog().isErrorEnabled()){
              sfLog().error(" Interrupted on server termination " , ie);
            }
//            Logger.log(" Interrupted on server termination " , ie);
	  }
	  super.sfTerminateWith(status);
  }

    /**
     * liveness test verifies the server is started
     * @param source
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if(server==null || !server.isStarted()) {
            throw new SmartFrogLivenessException("Server is not started");
        }
    }

    /**
     * deploy a web application.
     * Deploys a web application identified by the component passed as a parameter; a component of arbitrary
     * type but which must have the mandatory attributes identified in {@link org.smartfrog.services.www.JavaWebApplication};
     * possibly even extra types required by the particular application server.
     *
     * @param webApplication the web application. this must be a component whose attributes include the
     *                       mandatory set of attributes defined for a JavaWebApplication component. Application-server specific attributes
     *                       (both mandatory and optional) are also permitted
     * @return the context path deployed to.
     * @throws java.rmi.RemoteException on network trouble
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  on any other problem
     * @todo implement
     */
    public String DeployWebApplication(Prim webApplication)
            throws RemoteException, SmartFrogException {

        throw new SmartFrogException("not implemented : DeployWebApplication");
    }

    /**
     * undeploy a web application
     * @todo implement
     *
     * @param webApplication the web application itself;
     * @throws java.rmi.RemoteException on network trouble
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  on any other problem
     */
    public void UndeployWebApplication(Prim webApplication)
            throws RemoteException, SmartFrogException {
        throw new SmartFrogException("not implemented : UndeployWebApplication");
    }


}
