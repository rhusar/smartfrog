package org.smartfrog.services.persistence.test.testcases;

import java.rmi.RemoteException;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.storage.ConcurrentTransactionException;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


public class TransactionTestImpl extends CompoundImpl implements Compound, Tester {
    
    RComponent target;
    ComponentDescription store;

    public TransactionTestImpl() throws RemoteException {
        // TODO Auto-generated constructor stub
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
		try{
			doTest();
		}catch(ConcurrentTransactionException e){
			throw e;

		}catch(Exception e1){
			throw new SmartFrogException("Unexpected Exception" + e1);
		}
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
    }
    
    public void doTest() throws RemoteException, ConcurrentTransactionException, Exception {

        target = (RComponent)sfResolve("testTarget");
        store = (ComponentDescription)target.sfResolve(RComponent.STORAGE_DATA_ATTR);

        Transaction t1 = target.sfGetTransaction();
        target.sfReplaceAttribute("attr1", "value1", t1);
        sfLog().info("Added attr1 in transaction t1");
        //printStorage();
        t1.commit();
        sfLog().info("Commited transaction t1");
        //printStorage();

        Transaction t2 = target.sfGetTransaction();
        target.sfAddTag("attr1", "tag1", t2);
         sfLog().info("Added tag to attr1 in transaction t2");
        //printStorage();
        t2.commit();
         sfLog().info("Commited transaction t2");
        //printStorage();

        Transaction t3 = target.sfGetTransaction();
        Transaction t4 = target.sfGetTransaction();
         sfLog().info("Adding attr3 in transaction t3");
        try {
            target.sfAddAttribute("attr3", "value3", t3);
        } catch (ConcurrentTransactionException e) {
				sfLog().error("ConcurrentTransactionException adding attr4"+e);
				throw new ConcurrentTransactionException("ConcurrentTransactionException adding attr3"+e);
        }
        sfLog().info("Adding attr4 in transaction t4");
        try {
            target.sfAddAttribute("attr4", "value4", t4);
        } catch (ConcurrentTransactionException e) {
				sfLog().error("ConcurrentTransactionException adding attr4"+e);
				throw new ConcurrentTransactionException("ConcurrentTransactionException adding attr4"+e);
        }
         sfLog().info("Committing transactions t3 and t4");
        t3.commit();
        t4.commit();
        //printStorage();

        
    }

    private void printStorage() throws StorageException {
        Storage storage = Storage.openStorage(store);
        Context context = storage.getContext(Transaction.nullTransaction);
        storage.close();
        sfLog().info("Target storage: " + context );
    }



}
