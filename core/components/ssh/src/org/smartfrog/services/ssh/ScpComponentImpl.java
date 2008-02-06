/* (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
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

    /**
     * true if files are to be fetched,
     */
    private boolean getFiles;
    /**
     * Vector of remote file names
     */
    private Vector<String> remoteFileList = null;
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
        if (localFiles.isEmpty()) {
            log.info(INFO_NO_FILES_TO_PROCESS);
        } else {
            //now that we are starting up, check the files.
            if (isGetFiles()) {
                //verify that the remote file list is not empty
                for (File file : localFiles) {
                    if (file.exists() && file.isDirectory()) {
                        throw new SmartFrogLifecycleException(ERROR_OUTPUT_IS_A_DIRECTORY + file);
                    }
                }
            } else {
                for (File file : localFiles) {
                    if (!file.exists()) {
                        throw new SmartFrogLifecycleException(ERROR_MISSING_FILE_TO_UPLOAD + file);
                    }
                    if (!file.isFile()) {
                        throw new SmartFrogLifecycleException(ERROR_NOT_A_NORMAL_FILE + file);
                    }
                }
            }
        }
        worker = new ScpWorkerThread(this,log,
                isGetFiles(), localFiles, remoteFileList);
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
        SmartFrogThread.ping(worker);
    }

    /**
     * Life cycle method for terminating the SmartFrog component.
     *
     * @param tr Termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        SmartFrogThread.requestThreadTermination(worker);
        worker=null;
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
        readTransferType();
    }

    /**
     * Read and validate the transfer type
     * @throws SmartFrogResolutionException if failed to read any attribute or a mandatory attribute is not defined.
     * @throws SmartFrogLifecycleException if the transfer type is unsupported
     * @throws RemoteException in case of network/rmi error
     */
    protected void readTransferType() throws
            SmartFrogResolutionException,
            RemoteException,
            SmartFrogLifecycleException {
        String transferType = sfResolve(TRANSFER_TYPE, "", true).toLowerCase(
                Locale.ENGLISH);
        log.debug("Transfer Type :=" + transferType);
        if (OPERATION_GET.equals(transferType)) {
            setGetFiles(true);
        } else if (OPERATION_PUT.equals(transferType)) {
            setGetFiles(false);
        } else {
            throw new SmartFrogLifecycleException(
                    UNSUPPORTED_ACTION +'"' + transferType+ '"');
        }
    }

    /**
     * Read the file lists in, and resolve the local list
     * Subclasses can override this, as long as the localFiles and remoteFiles lists
     * are full at the end of the operation, and they have the same number of files.
     * @throws SmartFrogException for resolution problems
     * @throws RemoteException network problems
     * @throws SmartFrogLifecycleException if there is a mismatch between the count of local and remote files
     */
    protected void readFileLists() throws
            SmartFrogException,
            RemoteException {
        localFiles = FileSystem.resolveFileList(this, new Reference(LOCAL_FILES),
                null, true);

        //convert the list of local files into
        Vector vector = sfResolve(REMOTE_FILES, (Vector) null, true);
        remoteFileList = new Vector<String>(vector.size());
        for (Object o : vector) {
            remoteFileList.add(o.toString());
        }
        validateFileLists();
    }

    /**
     * Check that the file lists are valid, that they have the same number
     * of elements.
     * @throws SmartFrogLifecycleException if there is a size mismatch
     */
    protected void validateFileLists() throws SmartFrogLifecycleException {
        int fileCount = localFiles.size();
        int remoteFileCount = remoteFileList.size();
        if (fileCount != remoteFileCount) {
            throw new SmartFrogLifecycleException(ERROR_FILE_COUNT_MISMATCH
                    + fileCount + ") and the remote list (" + remoteFileCount + ')');
        }
    }

    public boolean isGetFiles() {
        return getFiles;
    }

    public void setGetFiles(boolean getFiles) {
        this.getFiles = getFiles;
    }

    public Vector<String> getRemoteFileList() {
        return remoteFileList;
    }

    public void setRemoteFileList(Vector<String> remoteFileList) {
        this.remoteFileList = remoteFileList;
    }

    public Vector<File> getLocalFiles() {
        return localFiles;
    }

    public void setLocalFiles(Vector<File> localFiles) {
        this.localFiles = localFiles;
    }
}

