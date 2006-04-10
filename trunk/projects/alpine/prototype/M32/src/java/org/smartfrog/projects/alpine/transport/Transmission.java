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

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.transport.http.HttpTransmitter;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;

import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.io.IOException;

/**
 * This represents an async transmission. The result is the thing callers should block on.
 * created 23-Mar-2006 16:21:58
 */

public class Transmission implements Callable {


    public Transmission(MessageContext context) {
        this.context = context;
    }

    private MessageContext context;

    /**
     * upload feedback
     */
    private ProgressFeedback uploadFeedback;

    /**
     * download feedback; the response
     */
    private ProgressFeedback downloadFeedback;

    /**
     * overall feedback and notification when
     * the message completed
     */
    private ProgressFeedback overallFeedback;

    /**
     * the result; something to block on. In the implementation
     * this is really the task of work that is used to do the operation
     */
    private Future<?> result;


    public MessageContext getContext() {
        return context;
    }


    public MessageDocument getRequest() {
        return context.getRequest();
    }

    public MessageDocument getResponse() {
        return context.getResponse();
    }

    public ProgressFeedback getUploadFeedback() {
        return uploadFeedback;
    }

    public void setUploadFeedback(ProgressFeedback uploadFeedback) {
        this.uploadFeedback = uploadFeedback;
    }

    public ProgressFeedback getDownloadFeedback() {
        return downloadFeedback;
    }

    public void setDownloadFeedback(ProgressFeedback downloadFeedback) {
        this.downloadFeedback = downloadFeedback;
    }

    public ProgressFeedback getOverallFeedback() {
        return overallFeedback;
    }

    public void setOverallFeedback(ProgressFeedback overallFeedback) {
        this.overallFeedback = overallFeedback;
    }

    public Future<?> getResult() {
        return result;
    }

    public void setResult(Future<?> result) {
        this.result = result;
    }

    /**
     * triggers the actuall transmission
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    public Object call() throws Exception {
        HttpTransmitter transmitter=new HttpTransmitter(this);
        transmitter.transmit();
        return null;
    }

    /**
     * Wait for a result
     * @param timeout timeout in milliseconds
     * @throws IOException
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public MessageDocument blockForResult(long timeout) throws IOException, ExecutionException, TimeoutException,
            InterruptedException {
        try {
            result.get(timeout, TimeUnit.MILLISECONDS);
            return getResponse();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            //nested ioes are rethrown
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            //runtime exceptions are stripped out and rethrown
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            //anything else is sent nested.
            throw e;
        }
    }
}
