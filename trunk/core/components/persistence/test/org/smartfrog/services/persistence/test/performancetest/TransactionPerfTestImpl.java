package org.smartfrog.services.persistence.test.performancetest;

import java.rmi.RemoteException;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.rcomponent.RComponentImpl;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.services.persistence.test.testcases.Tester;


public class TransactionPerfTestImpl extends RComponentImpl implements RComponent, Tester {
    
    class TestTransaction extends Transaction {
        TestTransaction() {
            super();
        }
        public void commit() throws StorageException {
        }
    }

    public TransactionPerfTestImpl() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }

    public void doTest() throws RemoteException, Exception {
        test(1);
        test(10);
        test(100);
        test(1000);
        test(10000);
    }
    
    private void test(int iterations) throws SmartFrogRuntimeException {
        long start = System.currentTimeMillis();
        for(int i = 0; i<iterations; i++) {
            Transaction xact = new TestTransaction();
        }
        long end = System.currentTimeMillis();
        System.out.println(iterations + "iterations control took: " + (end - start));
        
        
        start = System.currentTimeMillis();
        for(int i =0; i<iterations; i++) {
            Transaction xact = sfGetTransaction();
            xact.commit();
        }
        end = System.currentTimeMillis();
        System.out.println(iterations + "iterations test took: " + (end - start));
        
    }

}
