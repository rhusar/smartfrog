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
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.mortbay.util.MultiException;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This is a component that deploys a hadoop service. If the
 */

public abstract class HadoopServiceImpl extends HadoopComponentImpl {

    private Service service;

    protected HadoopServiceImpl() throws RemoteException {
    }

    /**
     * On shutdown, terminates any non-null service.
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        terminateHadoopService();
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException on any liveness problem
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    @Override
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        pingHadoopService();
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
     * Override point; tell the base class whether or not {@link #service} must be non-null in the {@link
     * #sfPing(Object)} operation
     *
     * @return true for non-null, false to allow null values.
     */
    protected boolean requireNonNullServiceInPing() {
        return true;
    }

    /**
     * Ping the hadoop service.
     *
     * @throws SmartFrogLivenessException on a ping failure, or a null service and non-null services are forbidden
     */
    protected void pingHadoopService() throws SmartFrogLivenessException {
        Service hadoopService = service;
        if (hadoopService != null) {
            try {
                hadoopService.ping();
            } catch (IOException e) {
                throw new SmartFrogLivenessException(e, this);
            }
        } else if (requireNonNullServiceInPing()) {
            throw new SmartFrogLivenessException("No running " + getName(), this);
        }
    }

    /**
     * Terminate a service and set the {@link #service} field to null. Does nothing if the service is already
     * terminated
     */
    protected synchronized void terminateHadoopService() {
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

    /**
     * Deploy a service. It must be in the CREATED state.
     * The field {@link #service} is set to the service argument, which must be null.
     * There is special handling for wrapped Jetty exceptions
     * @param hadoopService service to bind to and deploy.
     * @param conf configuration -used for diagnostics
     * @throws SmartFrogException on any problem
     * @throws SFHadoopException for Jetty exceptions and other causes of trouble
     * @throws SmartFrogDeploymentException on some wrapped IOExceptions
     */
    protected void deployService(Service hadoopService, ManagedConfiguration conf) throws SmartFrogException {
        if (service != null) {
            throw new SmartFrogDeploymentException("Cannot bind to a new service ("+hadoopService+")"
                    + " when an existing service is in use: "
                    + service);
        }
        setService(hadoopService);
        try {
            Service.deploy(hadoopService);
        } catch (IOException e) {
            throw SFHadoopException.forward("Failed to deploy " + hadoopService,
                    e,
                    this,
                    conf);
        } catch(IllegalArgumentException e) {
            //convert illegal argument exceptions
            throw SFHadoopException.forward("Failed to deploy " + hadoopService,
                    e,
                    this,
                    conf);
        }
    }
}
