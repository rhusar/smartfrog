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

import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cddlm.engine.JobState;
import org.smartfrog.services.cddlm.engine.NotificationAction;
import org.smartfrog.services.cddlm.engine.ServerInstance;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleEventRequest;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import java.net.URL;

/**
 * class handles cddlm callbacks created Sep 14, 2004 4:04:05 PM
 */

public class CddlmCallbackRaiser extends CallbackRaiser {


    private URI application;

    private String identifier;

    private URL callbackURL;


    /**
     * log
     */
    private static final Log log = LogFactory.getLog(CddlmCallbackRaiser.class);


    public CddlmCallbackRaiser(URI application,
            URL callbackURL,
            String identifier) {
        this.application = application;
        this.callbackURL = callbackURL;
        this.identifier = identifier;
    }

    public CddlmCallbackRaiser(JobState job) {
        this(job.getUri(), job.getCallbackURL(), job.getCallbackIdentifier());
    }

    /**
     * raise an event
     *
     * @param object object (may be null
     * @param sfe
     */
    public void raiseLifecycleEvent(JobState job, Prim object,
            SmartFrogException sfe) {
        LifecycleEventRequest event = job.createLifecycleEventMessage();
        log.info("queuing " +
                event.getStatus().getState() +
                " event to " +
                callbackURL);
        NotificationAction action = new NotificationAction(callbackURL,
                event);
        ServerInstance server = ServerInstance.currentInstance();
        server.queue(action);
    }


}
