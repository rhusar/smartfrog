/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This is the worker thread we use to move files. After execution,
 * it will terminate the hosting component, if so requested.
 */
class ScpWorkerThread extends SmartFrogThread implements ScpProgressCallback {

    private ScpComponentImpl owner;
    private LogSF log;
    private List<File> localFiles;
    private List<String> remoteFiles;
    private boolean getFiles;
    private AbstractScpOperation operation;
    private int counter=0;

    /**
     * Create a worker thread. This does not start the transfer
     *
     * @param owner owner component
     * @param log log to log to
     * @param getFiles flag to indicate whether files are to be got or put
     * @param localFiles list of local files to get/put
     * @param remoteFiles list of remote files to get/out
     */
    ScpWorkerThread(ScpComponentImpl owner,
                            LogSF log,
                            boolean getFiles, List<File> localFiles,
                            List<String> remoteFiles
    ) {
        this.owner = owner;
        this.log = log;
        this.localFiles = localFiles;
        this.remoteFiles = remoteFiles;
        this.getFiles = getFiles;
    }

    private synchronized void setOperation(AbstractScpOperation operation) {
        this.operation = operation;
    }


    /**
     * Request termination on a thread that polls its {@link
     * #terminationRequested} field, and/or blocks on the {@link
     * #terminationRequestNotifier} object
     */
    public synchronized void requestTermination() {
        haltOperation();
        super.requestTermination();
    }

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
    public void execute() throws Throwable {
            try {
                if (!localFiles.isEmpty()) {
                    // open ssh session
                    Session newsession = owner.openSession();
                    if (getFiles) {
                        log.info("Going to start scp to download files");
                        ScpFrom scpFrom = new ScpFrom(log);
                        setOperation(scpFrom);
                        scpFrom.doCopy(newsession, remoteFiles, localFiles);
                    } else {
                        log.info("Going to start scp to upload files");
                        ScpTo scpTo = new ScpTo(log);
                        setOperation(scpTo);
                        scpTo.doCopy(newsession, remoteFiles, localFiles);
                    }
                } else {
                    log.info("Skipping scp operation: no files");
                }
                TerminationRecord termR = TerminationRecord.normal(
                        "SSH Session to "+owner.getConnectionDetails()+" finished: ",
                        owner.sfCompleteName());
                new ComponentHelper(owner).sfSelfDetachAndOrTerminate(termR);
            } catch (JSchException e) {
                throw owner.translateStartupException(e);
            } finally {
                setOperation(null);
            }
    }

    /**
     * Runs the {@link #execute()} method, catching any exception it throws and
     * storing it away for safe keeping.
     * After the run, the notify object is notified and the component
     * then gets to terminated if there was an error.
     */
    public void run() {
        super.run();
        if(getThrown()!=null) {
            TerminationRecord record = TerminationRecord.abnormal(
                    "SCP failed to connect to "
                            + owner.getConnectionDetails(),
                    owner.sfCompleteNameSafe(),
                    getThrown());
            owner.sfTerminate(record);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public void beginTransfer(File localFile, String remoteFile)
            throws SmartFrogException, RemoteException {

    }

    /**
     * {@inheritDoc}
     *
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public synchronized void endTransfer(File localFile, String remoteFile)
            throws SmartFrogException, RemoteException {
        counter++;
        owner.sfReplaceAttribute(ScpComponent.ATTR_TRANSFER_COUNT,new Integer(counter));
    }
}
