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
package org.smartfrog.services.cddlm.api;

import org.apache.axis.AxisFault;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.engine.JobState;
import org.smartfrog.services.cddlm.generated.api.types.NotificationInformationType;
import org.smartfrog.services.cddlm.generated.api.types.SetNotificationRequest;

/**
 * extract callback information from a job, attach it to a job Can be called in
 * a setNotification call, or as part of a deployment operation
 * <p/>
 * created Sep 2, 2004 5:42:51 PM
 */

public class CallbackProcessor extends Processor {

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(CallbackProcessor.class);

    public CallbackProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    /**
     * called by DeployProcessor
     *
     * @param job
     * @param callbackInfo
     * @param required
     * @throws AxisFault
     */
    public void process(JobState job, NotificationInformationType callbackInfo,
            boolean required)
            throws AxisFault {
        CallbackInfo info = new CallbackInfo();
        if (!info.extractCallback(callbackInfo, required)) {
            job.clearCallbackData();
            return;
        } else {

        }
        log.info("sending callbacks to " + info.getAddress());
        job.setCallbackInformation(callbackInfo);
        job.setCallbackURL(info.getUrl());
        job.setCallbackType(info.getType());
        job.setCallbackIdentifier(info.getIdentifier());
        CddlmCallbackRaiser callbackRaiser = new CddlmCallbackRaiser(job);
        job.setCallbackRaiser(callbackRaiser);
    }

    /**
     * handle the request from the endpoint by looking up the app and handing
     * off to our internal process method
     */
    public boolean process(SetNotificationRequest request) throws AxisFault {
        final URI appURI = request.getApplication();
        if (appURI == null) {
            throw raiseBadArgumentFault(ERROR_NO_APPLICATION);
        }
        JobState job;
        /*
        job = lookupJobNonFaulting(appURI);
        if(job==null && request.getNotification()==null) {
            return true;
        }
        */
        job = lookupJob(appURI);

        process(job, request.getNotification(), false);

        return true;
    }
}


