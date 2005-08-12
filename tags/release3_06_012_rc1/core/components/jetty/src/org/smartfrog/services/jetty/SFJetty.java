/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.services.jetty;

import org.mortbay.http.HttpServer;
import org.mortbay.http.NCSARequestLog;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.rmi.RemoteException;

/**
 * A wrapper for a Jetty http server.
 * @author Ritu Sabharwal
 */

public class SFJetty extends CompoundImpl implements Compound,JettyIntf {

    protected Reference jettyhomeRef = new Reference(ATTR_JETTY_HOME);

    /**
     * Jetty home path
     */
    protected String jettyhome;


    protected JettyHelper jettyHelper = new JettyHelper(this);

    /**
     * The Http server
     */
    protected HttpServer server;

    /**
     * flag to turn logging on.
     */
    protected boolean enableLogging=false;

    protected String logDir;
    protected String logPattern;
    public static final String LOG_PATTERN = "yyyy_mm_dd.request.log";
    public static final String LOG_SUBDIR = "/logs/";

    /**
     * Error string raised in liveness checks.
     * {@value}
     */
    public static final String LIVENESS_ERROR_SERVER_NOT_STARTED = "Server is not started";


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
        enableLogging=sfResolve(ATTR_ENABLE_LOGGING,enableLogging,true);

        if(enableLogging) {
            logDir=FileSystem.lookupAbsolutePath(this,JettyIntf.ATTR_LOGDIR,jettyhome,null,true,null);
            logPattern=sfResolve(JettyIntf.ATTR_LOGPATTERN,"",false);
            configureLogging();
        }
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
  public void configureLogging() throws SmartFrogException {
      try {
          if(enableLogging) {
              NCSARequestLog requestlog = new NCSARequestLog();
              requestlog.setFilename(logDir+File.separatorChar+logPattern);
              //commented out as this is deprecated/ignored.
              //requestlog.setBuffered(false);
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
            throw new SmartFrogLivenessException(LIVENESS_ERROR_SERVER_NOT_STARTED);
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
