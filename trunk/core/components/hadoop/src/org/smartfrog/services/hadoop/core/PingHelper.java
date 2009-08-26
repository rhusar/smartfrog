/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.core;

import org.apache.hadoop.util.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.io.IOException;

/**
 * Created 26-Aug-2009 16:27:59
 */

public class PingHelper implements Pingable {
    
    private InnerPing instance;
    private Service service;
    private static final Log LOG = LogFactory.getLog(PingHelper.class);
    
    public PingHelper(InnerPing instance) {
        this.instance = instance;
        service = (Service) instance;
    }


  
    /**
     * Ping: checks that a component considers itself live.
     *
     * This may trigger a health check in which the service probes its constituent parts to verify that they are
     * themselves live. The base implementation considers any state other than {@link Service.ServiceState#FAILED} and {@link
     * Service.ServiceState#CLOSED} to be valid, so it is OK to ping a component that is still starting up. However, in such
     * situations, the inner ping health tests are skipped, because they are generally irrelevant.
     *
     *
     * @return the current service state.
     * @throws IOException           for any ping failure
     * @throws Service.ServiceStateException if the component is in a wrong state.
     */
    @Override
    public ServicePingStatus ping() throws IOException {
        ServicePingStatus status = new ServicePingStatus(service);
        Service.ServiceState state = status.getState();
        if (state == Service.ServiceState.LIVE) {
            try {
                instance.innerPing(status);
            } catch (Throwable thrown) {
                //TODO: what happens whenthe ping() returns >0 causes of failure but 
                //doesn't throw an exception -this method will not get called. Is 
                //that what we want?
                status = onPingFailure(status, thrown);
            }
        } else {
            //ignore the ping
            LOG.debug("ignoring ping request while in state " + state);
            //but tack on any non-null failure cause, which may be a valid value
            //in FAILED or TERMINATED states.
            status.addThrowable(service.getFailureCause());
        }
        return status;
    }

    /**
     * This is an override point for services -handle failure of the ping operation. 
     * Calls {@link Service#enterFailedState(Throwable)} and then updates the service status with the (new) state and the throwable
     * that was caught.
     *
     * @param currentStatus the current status structure
     * @param thrown        the exception from the failing ping.
     * @return an updated service status structure.
     * @throws IOException for IO problems
     */
    private ServicePingStatus onPingFailure(ServicePingStatus currentStatus,
                                               Throwable thrown) throws IOException {
        //something went wrong
        //mark as failed
        //TODO: don't enter failed state on a failing ping? Just report the event
        //to the caller?
        service.enterFailedState(thrown);
        //update the state
        currentStatus.updateState(service);
        currentStatus.addThrowable(thrown);
        //and return the exception.
        return currentStatus;
    }
}
