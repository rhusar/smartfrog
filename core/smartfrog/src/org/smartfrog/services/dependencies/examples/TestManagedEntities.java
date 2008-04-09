package org.smartfrog.services.dependencies.examples;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class TestManagedEntities extends CompoundImpl implements Compound, WaitToFinish {
		
	public TestManagedEntities() throws RemoteException {super();}  
		
	public synchronized void waitForTerminate(){
		try { this.wait(); } catch (Exception e){}
	}
	
	public synchronized void sfTerminateWith(TerminationRecord tr) {
		super.sfTerminateWith(tr);
		this.notifyAll();
	}
	
}
