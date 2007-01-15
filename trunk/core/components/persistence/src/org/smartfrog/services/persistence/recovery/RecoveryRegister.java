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
