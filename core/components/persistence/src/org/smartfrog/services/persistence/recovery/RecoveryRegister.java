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


package org.smartfrog.services.persistence.recovery;

import java.rmi.RemoteException;
import java.util.Iterator;

import org.smartfrog.services.persistence.recoverablecomponent.RComponent;
import org.smartfrog.services.persistence.recoverablecomponent.RComponentImpl;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public class RecoveryRegister extends RComponentImpl implements RComponent {

	
	public RecoveryRegister() throws RemoteException {
		super();
	}

	/**
	 * recovery goes through all attributes. If they are (other component's) storage 
	 * descriptions then it deploys them with the process compound as the parent 
	 * and the attribute name as their name - then they go through the sfDeploy
	 * and sfStart lifecycle.
	 */
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		Iterator iter = sfAttributes();
		while( iter.hasNext() ) {
			String name = (String)iter.next();
			Object value = sfContext.get(name);
			if( Storage.isStorageDescription(value) && !Storage.CONFIG_DATA.equals(name) ) {
				try {
					RComponent comp = (RComponent)sfDeployComponentDescription(name, null, (ComponentDescription)value, null);
					comp.sfDeploy();
					comp.sfStart();
				} catch (SmartFrogDeploymentException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
