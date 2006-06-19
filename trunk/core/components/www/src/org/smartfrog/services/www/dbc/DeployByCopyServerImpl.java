/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.dbc;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.www.JavaEnterpriseApplication;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * created 19-Jun-2006 15:53:07
 */

public class DeployByCopyServerImpl extends PrimImpl implements DeployByCopyServer, Runnable {

    private List filesToCopy = new ArrayList();
    private File destDir;
    private ComponentDescription startup;
    private ComponentDescription shutdown;
    private Prim startupPrim;
    private Prim shutdownPrim;
    private ComponentHelper helper;
    private Thread thread;
    private Throwable caughtException;
    /**
     * special file that is fed in to the queue to tell the thread to terminate
     * {@value}
     */
    private static File END_QUEUE_MARKER = new File("****END_QUEUE_MARKER****");

    public DeployByCopyServerImpl() throws RemoteException {
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        helper = new ComponentHelper(this);

        //bind to and create the destination directory
        destDir = new File(FileSystem.lookupAbsolutePath(this, ATTR_DEPLOY_DIR,
                null, null, false, null));
        destDir.getParentFile().mkdirs();

        //resolve deploy and start the startup prim
        startup = sfResolve(ATTR_START_COMPONENT, startup, false);
        if (startup != null) {
            startupPrim = helper.deployComponentDescription(ATTR_START_COMPONENT,
                    this,
                    startup,
                    null);
            startupPrim.sfDeploy();
            startupPrim.sfStart();
        }

        //resolve and deploy but do not start the shutdown prim.
        //this leaves it ready to do cleanup
        shutdown = sfResolve(ATTR_SHUTDOWN_COMPONENT, shutdown, false);
        if (shutdown != null) {
            shutdownPrim = helper.deployComponentDescription(ATTR_SHUTDOWN_COMPONENT,
                    this,
                    shutdown,
                    null);
            shutdownPrim.sfDeploy();
        }

        //begin deploying things.
        startWorkerThread();
    }


    /**
     * handle liveness check by throwing any fault received in the worker thread, then
     *
     * @param source
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (caughtException != null) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(caughtException);
        }
        //ping the child
        if (startupPrim != null) {
            startupPrim.sfPing(this);
        }
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        //close down the thread
        addFileToCopy(END_QUEUE_MARKER);

        //kill the startup component
        if (startupPrim != null) {
            try {
                startupPrim.sfTerminate(status);
            } catch (RemoteException e) {
                LogFactory.sfGetProcessLog().ignore("When terminating the startup component", e);
            }
            startupPrim = null;
        }

        //run the shutdown component
        if (shutdownPrim != null) {
            try {
                shutdownPrim.sfDeploy();
                shutdownPrim.sfStart();
                shutdownPrim.sfTerminate(status);
            } catch (RemoteException e) {
                LogFactory.sfGetProcessLog().ignore("When running the shutdown component", e);
            } catch (SmartFrogException e) {
                LogFactory.sfGetProcessLog().ignore("When running the shutdown component", e);
            }
            shutdownPrim = null;
        }

    }

    public synchronized void addFileToCopy(File file) {
        filesToCopy.add(file);
        filesToCopy.notify();
    }

    public File getDestDir() {
        return destDir;
    }

    public Prim getStartupPrim() {
        return startupPrim;
    }

    public Prim getShutdownPrim() {
        return shutdownPrim;
    }

    public JavaWebApplication deployWebApplication(Prim webApplication)
            throws RemoteException, SmartFrogException {
        return new DbcWarDelegate(this, webApplication);
    }

    public JavaEnterpriseApplication deployEnterpriseApplication(Prim enterpriseApplication)
            throws RemoteException, SmartFrogException {
        return new DbcEarDelegate(this, enterpriseApplication);
    }

    public ServletContextIntf deployServletContext(Prim servlet) throws RemoteException, SmartFrogException {
        throw new SmartFrogException("Servlet contexts are not supported");
    }

    /**
     * Start the component in a new thread. Synchronized.
     *
     * @throws SmartFrogException
     */
    private synchronized void startWorkerThread() throws SmartFrogException {
        if (thread != null) {
            throw new SmartFrogException("We are already running!");
        }
        thread = new Thread(this);
        thread.run();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
        } catch (Exception e) {
            caughtException = e;
        } finally {
            //end of life
            thread = null;
        }
    }


}
