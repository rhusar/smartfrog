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
package org.smartfrog.services.hadoop.operations.conf;

import org.smartfrog.services.hadoop.operations.core.ClusterBound;
import org.smartfrog.services.hadoop.operations.exceptions.SFHadoopRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * This component can read in from the resources [] and files [] conf files to read in. All resources are read before
 * all files. The values are read in during sfDeploy() time unless readEarly is false; this
 * ensures the values are there for other components
 */

public class HadoopConfigurationImpl extends PrimImpl implements HadoopConfiguration, ClusterBound {

    private ManagedConfiguration managedConf;
    private boolean readEarly;

    public HadoopConfigurationImpl() throws RemoteException {
    }

    /**
     * In the deploy phase, ths component loads in a configuration
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        readEarly = sfResolve(ATTR_READ_EARLY, true, true);
        if (readEarly) {
            sfLog().debug("Reading configuration in sfDeploy()");
            setManagedConf(ConfigurationLoader.loadConfiguration(this));
        }
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (!readEarly) {
            sfLog().debug("Reading configuration in sfStart()");
            setManagedConf(ConfigurationLoader.loadConfiguration(this));
        }
    }

    public ManagedConfiguration getManagedConf() {
        return managedConf;
    }

    public void setManagedConf(final ManagedConfiguration managedConf) {
        this.managedConf = managedConf;
    }

    /**
     * Clone our configuration
     *
     * @return a copy of the configuration, one that is bound to the same parent component
     * @throws SFHadoopRuntimeException if the cloning fails. This will only happen if a parent class of the (final) class
     *                                  {@link ManagedConfiguration} intercepts and rejects the cloning operation.
     */
    ManagedConfiguration cloneConfiguration() {
        try {
            return (ManagedConfiguration) managedConf.clone();
        } catch (CloneNotSupportedException e) {
            throw new SFHadoopRuntimeException("Unable to clone an instance of ManagedConfiguration", e);
        }
    }


}
