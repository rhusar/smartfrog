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
package org.apache.hadoop;

import org.apache.hadoop.util.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * This is to keep the ping code somewhere that still builds regularly
 */

public class PingableService extends Service {

    private static final Log LOG = LogFactory.getLog(Service.class);
    
    /**
     * Ping: checks that a component considers itself live.
     *
     * This may trigger a health check in which the service probes its constituent parts to verify that they are
     * themselves live. The base implementation considers any state other than {@link ServiceState#FAILED} and {@link
     * ServiceState#CLOSED} to be valid, so it is OK to ping a component that is still starting up. However, in such
     * situations, the inner ping health tests are skipped, because they are generally irrelevant.
     *
     * Subclasses should not normally override this method, but instead override {@link #innerPing(ServiceStatus)} with
     * extra health checks that will only be called when a system is actually live.
     *
     * @return the current service state.
     * @throws IOException           for any ping failure
     * @throws ServiceStateException if the component is in a wrong state.
     */
 /*   protected ServiceStatus ping() throws IOException {
        ServiceStatus status = new ServiceStatus(this);
        ServiceState state = status.getState();
        if (state == ServiceState.LIVE) {
            try {
                innerPing(status);
            } catch (Throwable thrown) {
                //TODO: what happens whenthe ping() returns >0 causes of failure but 
                //doesn't throw an exception -this method will not get called. Is 
                //that what we want?
                status = onInnerPingFailure(status, thrown);
            }
        } else {
            //ignore the ping
            LOG.debug("ignoring ping request while in state " + state);
            //but tack on any non-null failure cause, which may be a valid value
            //in FAILED or TERMINATED states.
            status.addThrowable(getFailureCause());
        }
        return status;
    }*/

    /**
     * {@inheritDoc}
     *
     * @param status a status that can be updated with problems
     * @throws IOException for any problem
     */
/*    public void innerPing(ServiceStatus status) throws IOException {
    }*/

    /**
     * This is an override point for services -handle failure of the inner ping operation. The base implementation calls
     * {@link #enterFailedState(Throwable)} and then updates the service status with the (new) state and the throwable
     * that was caught.
     *
     * @param currentStatus the current status structure
     * @param thrown        the exception from the failing ping.
     * @return an updated service status structure.
     * @throws IOException for IO problems
     */
 /*   protected ServiceStatus onInnerPingFailure(ServiceStatus currentStatus,
                                               Throwable thrown)
            throws IOException {
        //something went wrong
        //mark as failed
        //TODO: don't enter failed state on a failing ping? Just report the event
        //to the caller?
        enterFailedState(thrown);
        //update the state
        currentStatus.updateState(this);
        currentStatus.addThrowable(thrown);
        //and return the exception.
        return currentStatus;
    }*/
}
