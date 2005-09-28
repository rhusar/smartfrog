package org.smartfrog.services.persistence.storage;

import java.net.*;
import java.rmi.*;

import org.smartfrog.services.persistence.recoverablecomponent.*;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.prim.TerminationRecord;


public class StorageAgentImpl extends CompoundImpl implements StorageAgent, Compound {

	public StorageAgentImpl() throws RemoteException {
		super();
		return;
	}
	
	static public String getUrl() {
		try{
			return InetAddress.getLocalHost().getHostAddress();
		}catch (java.net.UnknownHostException exc){
			throw new RuntimeException("Could not find the local IP address.",exc);
		}
	}

	public synchronized void sfDeploy() throws RemoteException, SmartFrogException{
		super.sfDeploy();
	}

	public synchronized void sfStart() throws RemoteException, SmartFrogException{
		System.out.println("Storage Agent started here");
		super.sfStart();
	}
	
	
	public synchronized Object getComponentStub(StorageRef storef) throws RemoteException, StorageException {
		System.out.println("StorageAgent being queried for "+storef);
		StoragePollee stopoll = storef.getStoragePollee();
		Object returnobject = stopoll.pollEntry(RComponent.DBStubEntry);
		stopoll.close();
		System.out.println("Returning current stub.");
		return returnobject;
	}
	
	public synchronized boolean isDead(StorageRef storef) throws RemoteException, StorageException{
		System.out.println("StorageAgent being queried for "+storef);
		StoragePollee stopoll = storef.getStoragePollee();
		Object returnobject = stopoll.pollEntry(RComponent.WFSTATUSENTRY);
		stopoll.close();
		System.out.println("Returning component's status.");
		return returnobject.equals(RComponent.WFSTATUS_DEAD);
	}
	
	public synchronized void sfTerminateQuietlyWith(TerminationRecord status) {
		super.sfTerminateQuietlyWith(status);
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}
	
}
