package org.smartfrog.services.persistence.test.testcases;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.rcomponent.RComponentImpl;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class TransactionDeadlockTestImpl extends RComponentImpl implements
		RComponent, Tester {

	public TransactionDeadlockTestImpl() throws RemoteException {
	}

	public synchronized void sfStart() throws SmartFrogException,
			RemoteException {
		super.sfStart();
	}

	public synchronized void sfDeploy() throws SmartFrogException,
			RemoteException {
		super.sfDeploy();
		try{
			doTest();
		}catch(Exception e1){
			throw new SmartFrogException("Failed to get next transaction" + e1);
		}
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

	public void doTest() throws RemoteException, Exception {
		sfLog().info("Transaction Deadlock test started");
		Set<Transaction> xacts = new HashSet<Transaction>();
		try {
			while(true) {
				if( sfLog().isInfoEnabled() ) {
					sfLog().info("Getting tansaction " + xacts.size() + 1);
				}
				xacts.add(sfGetTransaction());
			}
		} catch (Exception e) {
			
			if( sfLog().isInfoEnabled() ) {
				sfLog().info("Obtained " + xacts.size() + " transactions");
				sfLog().info("Failed to get next transaction : " + e.getMessage());
			}	
				//sfLog().error(e);
			throw e;
		} finally {
			for(Transaction xact : xacts) {
				xact.commit();
			}
			xacts.clear();
		}
	}

}
