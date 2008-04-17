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
package org.smartfrog.projects.alpine.transport;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * Queue of things to transmit
 * created 23-Mar-2006 16:01:02
 */

public class TransmitQueue {

    /**
     * Executor of operations
     */
    private Executor executor;

    /**
     * internal queue of things to send
     */
    private ConcurrentLinkedQueue<Transmission> queue=new ConcurrentLinkedQueue<Transmission>();

    /**
     * default progressor for upload
     */
    private ProgressFeedback uploadProgress = BaseProgress.EMPTY_PROGRESS;

    /**
     * default progressor for download
     */
    private ProgressFeedback downloadProgress = BaseProgress.EMPTY_PROGRESS;

    /**
     * default progressor for overall operations
     */

    private ProgressFeedback overallProgress = BaseProgress.EMPTY_PROGRESS;


    public TransmitQueue(Executor executor) {
        this.executor = executor;
    }

    public Executor getExecutor() {
        return executor;
    }

    public ConcurrentLinkedQueue<Transmission> getQueue() {
        return queue;
    }

    public ProgressFeedback getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(ProgressFeedback uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public ProgressFeedback getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(ProgressFeedback downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public ProgressFeedback getOverallProgress() {
        return overallProgress;
    }

    public void setOverallProgress(ProgressFeedback overallProgress) {
        this.overallProgress = overallProgress;
    }

    /**
     * submit something for upload
     * @param tx
     */
    public void transmit(Transmission tx) {
        FutureTask<?> task;
        synchronized(tx) {
            task = new FutureTask<Object>(tx);
            tx.setResult(task);
        }
        executor.execute(task);
    }

}
