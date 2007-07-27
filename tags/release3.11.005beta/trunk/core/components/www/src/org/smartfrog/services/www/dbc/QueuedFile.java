package org.smartfrog.services.www.dbc;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;
import java.io.IOException;

/**
 */
public class QueuedFile {

    private File source;
    private File dest;
    private volatile boolean processed;
    private volatile SmartFrogException fault;


    public QueuedFile(File source, File dest) {
        this.source = source;
        this.dest = dest;
    }

    public File getSource() {
        return source;
    }


    public File getDest() {
        return dest;
    }

    public boolean isProcessed() {
        return processed;
    }

    /**
     * set the processed bit.
     * @param processed
     */
    public synchronized void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * Get any fault that was caught during the copy
     * @return a fault or null for 'no fault'
     */
    public SmartFrogException getFault() {
        return fault;
    }

    /**
     * log any exception; implicitly sets the processed flag
     * @param fault the fault that occurred
     */
    public synchronized void setFault(SmartFrogException fault) {
        setProcessed(true);
        this.fault = fault;
    }


    /**
     * Do a blocking copy; throws a fault if somethig failed, and saves the
     * result in the QueuedFile value. The processed field of the queued file is
     * always copied.
     *
     * @param owner owner for logging and error context
     * @throws SmartFrogDeploymentException if the IO failed
     */
    protected void execute(PrimImpl owner)
            throws SmartFrogDeploymentException {
        //get the destination directory
        //determine the destination file (with the target extension)
        owner.sfLog().info("Deploying " + toString());
        try {
            //do the blocking copy
            FileSystem.fCopy(source, dest);
        } catch (IOException e) {
            SmartFrogDeploymentException fault =
                    new SmartFrogDeploymentException("Failed: " + this,
                            e,
                            owner,
                            owner.sfContext());
            setFault(fault);
            throw fault;
        } finally {
            setProcessed(true);
        }
    }

    /**
     * Check the health of this operation.
     *
     * The method returns false if the operation has not completed,
     * true if it has and all is well. An exception is thrown
     * if the copy failed.
     * If the destination file is now missing, a {@link SmartFrogLivenessException}
     * is thrown.
     * @return true if the copy completed and the destination file is present
     * @throws SmartFrogException for copy or liveness failures.
     */
    public synchronized boolean ping() throws SmartFrogException {
        if (!isProcessed()) {
            return false;
        }
        if (getFault() != null) {
            throw getFault();
        } else {
            if (!getDest().exists()) {
                throw new SmartFrogLivenessException(
                              "Deployed File "
                                + getDest()
                                + " has disappeared from "
                                + this);
            }
        }
        return true;
    }

    /**
     * Delete the destination file, or queue it for later destruction
     */
    public void deleteDestFile() {
        if (dest != null && dest.exists() && !dest.delete()) {
            dest.deleteOnExit();
        }
    }


    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return source.getAbsolutePath()
                + " copied to "
                + dest.getAbsolutePath();
    }
}
