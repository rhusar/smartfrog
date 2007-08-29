/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP
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


package org.smartfrog.services.persistence.model;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * NullModel is a dummy persistence model that behaves as if there is no persistent 
 * copy of the component. It is used to make an RComponent behave as if it were a
 * regular SmartFrog compound.
 */
public class NullModel extends PersistenceModel {

    /**
     * The constructor does not examine its parameter
     * @param configdata configuration data for the persistence model
     */
    public NullModel( ComponentDescription configdata ) {
        return;
    }

    
    /**
     * Opportunity to change or examine the context when initially deployed.
     * No changes are made to the context .
     */
	public void initialContext(Context context) throws SmartFrogDeploymentException {
		// Do nothing
		return;
	}

	
	/**
	 * There is no storage for the null model, so all commit points return false
	 */
	public boolean isCommitPoint(Prim component, String point) throws SmartFrogException, RemoteException {
		// Do nothing
		return false;
	}

	
	/**
	 * Everything is volatile in the null model
	 * returns true
	 */
	public boolean isVolatile(Object attr) {
		// everything is volatile
		return true;
	}

	
	/**
	 * The null model does not leave a tomb stone
	 */
	public boolean leaveTombStone(Prim Component, TerminationRecord tr) {
		// never leave a tomb stone
		return false;
	}

	
	/**
	 * Opportunity to change or examine the context when deployed on recovery from storage.
	 * No changes are made to the context - this model would not be recovered from storage.
	 */
	public void recoverContext(Context context) throws SmartFrogDeploymentException {
		// do nothing
		return;
	}

	
	/**
	 * The null model does not redeploy on recovery.
	 */
	public boolean redeploy(Prim component) {
		// Never gets redeployed
		return false;
	}

	
	/**
	 * The null model does not restart on recovery.
	 */
	public boolean restart(Prim component) {
		// never gets restarted
		return false;
	}

}
