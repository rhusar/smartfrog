/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.framework;

import java.rmi.RemoteException;

import org.smartfrog.services.persistence.framework.activator.Activator;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;

/**
 * This class is used to collect the persistence framework components as a
 * single group. It implements the Activator interface. 
 */
public class PersistenceFramework extends CompoundImpl implements Compound, Activator {
    
    private static final String ACTIVATOR_ATTR = "activator";
    private Activator activator;

    public PersistenceFramework() throws RemoteException {
    }
    
    public void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy();
        activator = (Activator)sfResolve(ACTIVATOR_ATTR);
    }

    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#activate()
     */
    public boolean activate() {
        return activator.activate();
    }

    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#deactivate()
     */
    public boolean deactivate() {
        return activator.deactivate();
    }

    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#sanityCheck(java.lang.StringBuffer)
     */
    public void sanityCheck(StringBuffer out) throws RemoteException, SmartFrogException {
        activator.sanityCheck(out);
    }

    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#terminate()
     */
    public void terminate() {
        activator.terminate();
    }

    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#getActivationStatus()
     */
	public Status getActivationStatus() {
		return activator.getActivationStatus();
	}


}
