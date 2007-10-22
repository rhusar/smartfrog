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
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.ChildMinder;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ParentHelper;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Implementation of deploy-by-copy.
 * created 19-Jun-2006 15:53:07
 */

public class DeployByCopyServerImpl extends PrimImpl implements DeployByCopyServer,
        ChildMinder {

    private final List/*<QueuedFile>*/ filesToCopy = new ArrayList/*<QueuedFile>*/();
    private File destDir;
    private ComponentDescription startup;
    private ComponentDescription shutdown;
    private Prim startupPrim;
    private Prim shutdownPrim;
    private ParentHelper childminder;
    private SmartFrogThread thread;
    private boolean synchronousCopy;

    /**
     * {@value}
     */
    public static final String ERROR_NO_SERVLETS = "Servlet contexts are not supported";

    /**
     * {@value}
     */
    public static final String ERROR_ALREADY_RUNNING = "The servlet is already running!";

    public DeployByCopyServerImpl() throws RemoteException {
    }

    /**
     * Startup:
     * <ol>
     * <li>Bind to the destination directory</li>
     * <li>deploy and start the <i>startup</i> component if present
     * <li>deploy but do not start the <i>shutdown</i> component if present
     * <li>start the worker thread that accepts queued copy operations
     * </li>
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException RMI problems
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        childminder = new ParentHelper(this);

        //bind to and create the destination directory
        destDir = new File(FileSystem.lookupAbsolutePath(this, ATTR_DEPLOY_DIR,
                null, null, false, null));
        destDir.mkdirs();

        //resolve deploy and start the startup prim
        startup = sfResolve(ATTR_START_COMPONENT, startup, false);
        if (startup != null) {
            startupPrim = childminder.deployComponentDescription(ATTR_START_COMPONENT,
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
            shutdownPrim = childminder.deployComponentDescription(ATTR_SHUTDOWN_COMPONENT,
                    this,
                    shutdown,
                    null);
            shutdownPrim.sfDeploy();
        }

        synchronousCopy=sfResolve(ATTR_SYNCHRONOUS_COPY,true,true);

        //begin deploying things.
        if(!synchronousCopy) {
            startWorkerThread();
        }
    }


    /**
     * handle liveness check by throwing any fault received in the worker thread, then
     * checking the health of the startup component.
     * @param source
     * @throws SmartFrogLivenessException ping failure
     * @throws RemoteException network failure
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);

        //ping the child
        if (startupPrim != null) {
            startupPrim.sfPing(this);
        }
    }

    /**
     * Termination logic.
     * At terminate time, we shutdown the copy thread,
     * then kill the startup prim.
     * If shutdown was not null, it is started and then immediately terminated (so it
     * had better do its work in the start component)
     * @param status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        //close down the thread
        queueFileToCopy(EndOfQueue.END_OF_QUEUE);
        super.sfTerminateWith(status);


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


    public File getDestDir() {
        return destDir;
    }

    public Prim getStartupPrim() {
        return startupPrim;
    }

    public Prim getShutdownPrim() {
        return shutdownPrim;
    }

    /**
     * Factory for a DBC deployment
     * @param webApplication the web application to deploy
     * @return an instance of a {@link DbcWarDelegate}
     */
    public JavaWebApplication deployWebApplication(Prim webApplication) {
        return new DbcWarDelegate(this, webApplication);
    }

    /**
     * Deploy an EAR by returning an instance of {@link DbcEarDelegate}
     * @param enterpriseApplication the EAR application to deploy
     * @return a delegate
     */
    public JavaEnterpriseApplication deployEnterpriseApplication(Prim enterpriseApplication) {
        return new DbcEarDelegate(this, enterpriseApplication);
    }

    /**
     * DBC does not support servlet deployment, so an exception gets thrown
     * @param servlet servlet to deploy
     * @return never; we always throw an exception
     * @throws SmartFrogException containing the text {@link #ERROR_NO_SERVLETS}
     */
    public ServletContextIntf deployServletContext(Prim servlet) throws SmartFrogException {
        throw new SmartFrogException(ERROR_NO_SERVLETS);
    }

    /**
     * Add a child.
     *
     * @param child child to add
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfAddChild(Liveness child) throws RemoteException {
        childminder.sfAddChild(child);
    }

    /**
     * Remove a child.
     *
     * @param child child to add
     *
     * @return Status of child removal
     *
     * @throws SmartFrogRuntimeException if failed to remove
     * @throws RemoteException In case of Remote/nework error
     */
    public boolean sfRemoveChild(Liveness child)
            throws SmartFrogRuntimeException, RemoteException {
        return childminder.sfRemoveChild(child);
    }

    /**
     * Request whether implementor contains a given child.
     *
     * @param child child to check for
     *
     * @return true is child is present else false
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public boolean sfContainsChild(Liveness child) throws RemoteException {
        return childminder.sfContainsChild(child);
    }

    /**
     * Gets an enumeration over the children of the implementor.
     *
     * @return enumeration over children
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public Enumeration sfChildren() throws RemoteException {
        return childminder.sfChildren();
    }

    /**
     * Start the component in a new thread. Synchronized.
     *
     * @throws SmartFrogException with {@link #ERROR_ALREADY_RUNNING} if a worker
     * thread is already live.
     */
    private synchronized void startWorkerThread() throws SmartFrogException {
        if (thread != null) {
            throw new SmartFrogException(ERROR_ALREADY_RUNNING,this);
        }
        thread = new SmartFrogThread(new QueueHandler());
        thread.start();
    }


    /**
     * Add a file to the copy queue
     *
     * @param file file to copy in
     */
    public void queueFileToCopy(QueuedFile file) {
        synchronized(filesToCopy) {
            filesToCopy.add(file);
            filesToCopy.notify();
        }
    }

    /**
     * Poll for the next file to copy
     * @return the next entry, or {@link EndOfQueue#END_OF_QUEUE} for the end of the queue
     */
    public QueuedFile pollNextFile() {
        synchronized(filesToCopy) {
            if(filesToCopy.isEmpty()) {
                try {
                    filesToCopy.wait();
                } catch (InterruptedException e) {
                    //interrupted? End the queue
                    return EndOfQueue.END_OF_QUEUE;
                }
            }
            return (QueuedFile) filesToCopy.remove(0);
        }
    }


    /**
     * copy the file. This may be blocking or not, depending on the settings
     *
     *
     * @param sourceFile the file to copy
     * @param destinationName short name of the destination file
     * @return the file (which may or may not be copied yet; it depends on the
     * async flag)
     *
     * @throws SmartFrogDeploymentException if the IO failed
     */
    protected QueuedFile queueCopy(File sourceFile,String destinationName) throws
            SmartFrogDeploymentException {
        if (!sourceFile.exists()) {
            throw new SmartFrogDeploymentException(
                    ApplicationServerContext.ERROR_FILE_NOT_FOUND +
                    sourceFile,
                    this);
        }
        //determine the destination file (with the target extension)
        File destFile = new File(destDir, destinationName);
        QueuedFile queuedFile = new QueuedFile(sourceFile, destFile);
        queuedFile.execute(this);
        return queuedFile;
    }

    /**
     * Worker routine for asynchronous copy actions
     */
    private class QueueHandler implements Runnable {


        /**
         * When an object implementing interface <code>Runnable</code> is used to
         * create a thread, starting the thread causes the object's <code>run</code>
         * method to be called in that separately executing thread.
         * <p/>
         * The general contract of the method <code>run</code> is that it may take
         * any action whatsoever.
         *
         * @see Thread#run()
         */
        public void run() {
            boolean finished=false;
            while(!finished) {
                QueuedFile queuedFile = pollNextFile();
                if(queuedFile instanceof EndOfQueue) {
                    finished=true;
                } else try {
                    //do the copy; this marks the file as processed
                    queuedFile.execute(DeployByCopyServerImpl.this);
                } catch (SmartFrogDeploymentException e) {
                    //log the error. which is stored in the queuedFile
                    sfLog().error(e);
                }
            }
            thread=null;
        }
    }

}
