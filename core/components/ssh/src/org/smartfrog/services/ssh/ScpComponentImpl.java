/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Vector;

/**
 * SmartFrog component to upload/download files to/from a remote machine over SSH. It is a wrapper around jsch-0.1.14
 *
 * @author Ashish Awasthi
 * @see <a href="http://www.jcraft.com/jsch/">jsch</a>
 */
public class ScpComponentImpl extends AbstractSSHComponent implements ScpComponent {

    private static final boolean DIRECTORIES_SUPPORTED=false;
    private static final String GET = "get";
    private static final String PUT = "put";
    /**
     * Type of transfer. "get": download, "put": upload
     */
    private String transferType = "get";
    /**
     * true if files are to be fetched,
     */
    private boolean getFiles;
    /**
     * Vector of remote file names
     */
    private Vector remoteFileList = null;
    /**
     * Vector of local file names
     */
    private Vector<File> localFiles = null;
    /**
    /**
     * The worker thread is here
     */
    private ScpWorkerThread worker;
    public static final String ERROR_OUTPUT_IS_A_DIRECTORY = "output file bound to a directory: ";
    public static final String ERROR_MISSING_FILE_TO_UPLOAD = "Missing file to upload: ";
    public static final String ERROR_NOT_A_NORMAL_FILE = "Not a normal file:";
    public static final String INFO_NO_FILES_TO_PROCESS = "No files to process";
    public static final String ERROR_FILE_COUNT_MISMATCH = "Mismatch between the number of elements in the local file list (";
    public static final String UNSUPPORTED_ACTION = "Unsupported action:";

    /**
     * Constructs an instance  object.
     *
     * @throws RemoteException in case of network/emi error
     */
    public ScpComponentImpl() throws RemoteException {
    }

    /**
     * Deploys ScpImpl component and reads SmartFrog attributes and .
     *
     * @throws SmartFrogException in case of error in deploying or reading the attributes
     * @throws RemoteException in case of network/emi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        readSFAttributes();
    }

    /**
     * Connects to remote host over SSH and uploads/downloads files.
     *
     * @throws SmartFrogException in case of error while connecting to remote host or executing scp command
     * @throws RemoteException in case of network/emi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {

        super.sfStart();
        readFileLists();
        if (localFiles.size() == 0) {
            log.info(INFO_NO_FILES_TO_PROCESS);
        } else {
            //now that we are starting up, check the files.
            if (getFiles) {
                //verify that the remote file list is not empty
                for (File file : localFiles) {
                    if (!DIRECTORIES_SUPPORTED && file.exists() && file.isDirectory()) {
                        throw new SmartFrogLifecycleException(ERROR_OUTPUT_IS_A_DIRECTORY + file);
                    }
                }
            } else {
                for (File file : localFiles) {
                    if (!file.exists()) {
                        throw new SmartFrogLifecycleException(ERROR_MISSING_FILE_TO_UPLOAD + file);
                    }
                    if (!DIRECTORIES_SUPPORTED && !file.isFile()) {
                        throw new SmartFrogLifecycleException(ERROR_NOT_A_NORMAL_FILE + file);
                    }
                }
            }
        }
        worker = new ScpWorkerThread();
        worker.start();
    }


    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        synchronized(this) {
            if(worker!=null) {
                worker.ping(true);
            }
        }
    }

    /**
     * Life cycle method for terminating the SmartFrog component.
     *
     * @param tr Termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        
        if(worker!=null) {
            worker.haltOperation();
        }
        super.sfTerminateWith(tr);

    }

    /**----------------SmartFrog Life Cycle Methods End ---------------------*/

    /**
     * Reads SmartFrog attributes.
     *
     * @throws SmartFrogResolutionException if failed to read any attribute or a mandatory attribute is not defined.
     * @throws SmartFrogLifecycleException if the action is unsupported
     * @throws RemoteException in case of network/rmi error
     */
    private void readSFAttributes() throws SmartFrogException, RemoteException {


        transferType = sfResolve(TRANSFER_TYPE, transferType, true).toLowerCase(Locale.ENGLISH);
        log.debug("Transfer Type :=" + transferType);
        if (GET.equals(transferType)) {
            getFiles = true;
        } else if (PUT.equals(transferType)) {
            getFiles = false;
        } else {
            throw new SmartFrogLifecycleException(
                    UNSUPPORTED_ACTION +'"' + transferType+"\"");
        }
    }

    /**
     * Read the file lists in, and resolve the local list
     * @throws SmartFrogResolutionException for resolution problems
     * @throws RemoteException network problems
     * @throws SmartFrogLifecycleException if there is a mismatch between the count of local and remote files
     */
    private void readFileLists() throws SmartFrogResolutionException, RemoteException, SmartFrogLifecycleException {
        remoteFileList = sfResolve(REMOTE_FILES, remoteFileList, true);
        Vector locals = null;
        Reference localsRef = new Reference(LOCAL_FILES);
        locals = sfResolve(localsRef, locals, true);

        //convert the list of local files into
        int fileCount = locals.size();
        int remoteFileCount = remoteFileList.size();

        if (fileCount != remoteFileCount) {
            throw new SmartFrogLifecycleException(ERROR_FILE_COUNT_MISMATCH
                    + fileCount + ") and the remote list (" + remoteFileCount + ")");
        }
        localFiles = FileSystem.resolveFileList(locals,null,this,localsRef);
    }

    /**
     * This is the worker thread we use to do our work
     */
    private class ScpWorkerThread extends SmartFrogThread {

        private AbstractScpOperation operation;

        public synchronized void haltOperation() {
            if(operation!=null) {
                operation.haltOperation();
            }
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run object, then that <code>Runnable</code>
         * object's <code>run</code> method is called; otherwise, this method does nothing and returns. <p> Subclasses
         * of <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        public void run() {
            try {
                try {
                    if (localFiles.size() > 0) {
                        // open ssh session
                        logDebugMsg("Getting SSH Session");
                        Session newsession = openSession();
                        setSession(newsession);
                        if (getFiles) {
                            log.info("Going to start scp to download files");
                            ScpFrom scpFrom = new ScpFrom(log);
                            operation = scpFrom;
                            scpFrom.doCopy(getSession(), remoteFileList, localFiles);
                        } else {
                            log.info("Going to start scp to upload files");
                            ScpTo scpTo = new ScpTo(log);
                            operation = scpTo;
                            scpTo.doCopy(getSession(), remoteFileList, localFiles);
                        }
                    } else {
                        log.info("Skipping scp operation: no files");
                    }
                    TerminationRecord termR = new TerminationRecord("normal",
                            "SSH Session to "+getConnectionDetails()+" finished: ", sfCompleteName());
                    new ComponentHelper(ScpComponentImpl.this).targetForWorkflowTermination(termR);
                } catch (JSchException e) {
                    String errortext = "When connecting to " + getConnectionDetails();
                    log.error(errortext,e);
                    if (e.getMessage().indexOf(SESSION_IS_DOWN) >= 0) {
                        throw new SmartFrogLifecycleException(TIMEOUT_MESSAGE
                                + getConnectionDetails(), e);
                    } else {
                        throw new SmartFrogLifecycleException(errortext,e);
                    }
                } finally {
                    operation = null;
                }
            } catch (Throwable thrown) {
                setThrown(thrown);
                TerminationRecord record = TerminationRecord.abnormal("SCP failed to connect to "
                        + getConnectionDetails(),sfCompleteNameSafe(),thrown);
                sfTerminate(record);
            }

        }
    }
}

