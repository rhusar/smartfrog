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
import org.smartfrog.projects.alpine.faults.*;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.transport.http.HttpTransmitter;
import org.smartfrog.projects.alpine.wsa.MessageIDSource;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
     * triggers the actual transmission
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    public Object call() throws Exception {
        HttpTransmitter transmitter = new HttpTransmitter(this);
        transmitter.transmit();
        return null;
    }

    /**
     * Wait for a result
     *
     * @param timeout timeout in milliseconds
     * @throws AlpineRuntimeException which contains anything else translated or nested
     * @throws org.smartfrog.projects.alpine.faults.TimeoutException
     *                                if there is a timeout
     * @return the response
     */
    public MessageDocument blockForResult(long timeout) {
        try {
            result.get(timeout, TimeUnit.MILLISECONDS);
            return getResponse();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            //nested ioes are rethrown
            AlpineRuntimeException fault;
            if (cause instanceof AlpineRuntimeException) {
                //nested alpine runtime exceptions get their
                //address appended and are rethrown
                fault = (AlpineRuntimeException) cause;
                fault.addAddressDetails(getRequest());
                throw fault;
            }
            if (cause instanceof RuntimeException) {
                //runtime exceptions are stripped out and rethrown
                throw (RuntimeException) cause;
            }
            if (cause instanceof IOException) {
                fault = new NetworkIOException((IOException) cause);
            } else {
                //anything else is sent nested.
                fault = new ClientException(e);
            }
            fault.addAddressDetails(getRequest());
            throw fault;
        } catch (java.util.concurrent.TimeoutException timeoutException) {
            //convert to alpine timeout exception
            throw TimeoutException.fromConcurrentTimeout(timeoutException);
        } catch (InterruptedException e) {
            throw new ClientException("Interrupted while waiting for response from "+
                    getRequest().getAddressDetails().getTo().toString());
        }
    }


    /**
     * Add the request and response messages to the fault as XML fragments. Good for diagnostics.
     *
     * @param fault the fault that is being raised
     */
    public void addMessagesToFault(AlpineRuntimeException fault) {
        if (getRequest() != null) {
            //add the request
            fault.addAddressDetails(getRequest());
            fault.addDetail(new SoapElement(FaultConstants.QNAME_FAULTDETAIL_REQUEST,
                    getRequest().getRootElement().copy()));
        }
        if (getResponse() != null) {
            //add the response
            fault.addDetail(new SoapElement(FaultConstants.QNAME_FAULTDETAIL_RESPONSE,
                    getResponse().getRootElement().copy()));
        }
    }





}
