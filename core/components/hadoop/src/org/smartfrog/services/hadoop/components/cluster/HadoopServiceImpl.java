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
package org.smartfrog.services.hadoop.components.cluster;

import org.apache.hadoop.util.Service;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

import java.rmi.RemoteException;
import java.io.IOException;

/**
 * This is a component that deploys a hadoop service.
 * If the
 */

public abstract class HadoopServiceImpl extends HadoopComponentImpl {

    private Service service;

    protected HadoopServiceImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is started and if there is a parent the
     * deployed component is added to the heartbeat. Subclasses can override to provide additional deployment behavior.
     * Attributees that require injection are handled during sfDeploy().
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            if (service != null) {
                service.start();
            }
        } catch (IOException e) {
            throw new SmartFrogDeploymentException(
                    "Failed to start: " + e.getMessage(), e, this, sfContext());
        }
    }

    /**
     * On shutdown, terminates any non-null service.
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        terminateService();
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    @Override
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (service != null) {
            try {
                service.ping();
            } catch (IOException e) {
                throw new SmartFrogLivenessException(e, this);
            }
        } else if (requireNonNullServiceInPing()) {
            throw new SmartFrogLivenessException("No running " + getName(), this);
        }
    }

    /**
     * Return the name of this service; please override for better messages
     *
     * @return a name of the service for error messages
     */
    protected String getName() {
        return "Service";
    }

    /**
     * Override point; tell teh base class whether or not {@link #service} must be non-null in the
     * {@link #sfPing(Object)} operation
     *
     * @return true for non-null, false to allow null values.
     */
    protected boolean requireNonNullServiceInPing() {
        return true;
    }

    /**
     * Terminate a service and set the {@link #service} field to null. Does nothing if the service is already terminated
     */
    protected synchronized void terminateService() {
        Service.terminate(service);
        service = null;
    }

    /**
     * Get the current service value; may be null
     *
     * @return the service
     */
    public synchronized Service getService() {
        return service;
    }

    /**
     * Set the current service value; null is acceptable
     *
     * @param service the new value
     */
    protected synchronized void setService(Service service) {
        this.service = service;
    }


}
