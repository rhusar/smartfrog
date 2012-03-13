package org.smartfrog.services.persistence.test.hsqldb;

import org.smartfrog.services.persistence.framework.activator.Activator;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.StorageExceptionNotification;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.processcompound.SFProcess;


public class TestInterfaceImpl extends PrimImpl implements Prim , TestInterface {

	private Activator persistenceFramework = null; 
	private static final String PERSISTENCE_FRAMEWORK_ATTR = "persistence";

	public TestInterfaceImpl() throws RemoteException {
	}

	public synchronized void sfStart() throws SmartFrogException,
			RemoteException {
		super.sfStart();
		
		
		try{
			persistenceFramework = (Activator)sfResolve(PERSISTENCE_FRAMEWORK_ATTR);	
		}catch(Exception e){
			System.out.println(e);
		}
		
		//simulate();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		
		super.sfDeploy();	
		
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		
		super.sfTerminateWith(status);
	}

	public void activate() throws RemoteException, SmartFrogException{
		 persistenceFramework.activate();
	}

	public void deactivate() throws RemoteException, SmartFrogException{
		 persistenceFramework.deactivate();
	}
	
	public void waitForActivation() throws RemoteException, SmartFrogException {
		
		Activator.Status status = persistenceFramework.getActivationStatus();
		while( status != Activator.Status.SUCCESS && status != Activator.Status.FAILURE ) {
		     try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// ignore interruptions
			}
		     status = persistenceFramework.getActivationStatus();
		}
		if(status == Activator.Status.FAILURE) {
			throw new SmartFrogException("Persistence framework failed to activate");
		}
	}

	public void simulateFailover() throws RemoteException, SmartFrogException {
		deactivate();
		activate();
		waitForActivation();
	}

}

