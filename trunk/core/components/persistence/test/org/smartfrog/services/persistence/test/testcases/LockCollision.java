package org.smartfrog.services.persistence.test.testcases;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.storage.ConcurrentTransactionException;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.services.persistence.storage.TransactionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


public class LockCollision extends CompoundImpl implements Compound {
    
    private Method replaceAttribute = RComponent.class.getMethod("sfReplaceAttribute", new Class[] {Object.class, Object.class, Transaction.class});
    private Method lockTree = RComponent.class.getMethod("sfLockTree", new Class[] {Transaction.class});
    private Method lock = RComponent.class.getMethod("sfLock", new Class[] {Transaction.class});
    

    public LockCollision() throws RemoteException, NoSuchMethodException {
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {

		super.sfStart();
		try{
			singleComponentTest();
			treeTest();
			treeTestWithOwnLock();
			nullTransactionTerminationTest();
		}catch(Exception e){
			throw new SmartFrogException(e) ;
		}
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
    }
    
    public void nullTransactionTerminationTest() throws Exception{
        RComponent G;
        try {
            sfLog().info("\nExecuting Null transaction termination test");
            G = (RComponent)sfResolve("G");
            G.sfDetachPendingTermination(Transaction.nullTransaction);
        } catch (ConcurrentTransactionException e) {
			sfLog().error("Unexpected lock failure"+e.toString());
			throw e;
        } catch (SmartFrogResolutionException e) {
			sfLog().error("Unexpected SmartFrogException"+e.toString());
			throw e;
        } catch (RemoteException e) {
			sfLog().error("Unexpected RemoteException"+e.toString());
			throw e;
        } catch (SmartFrogRuntimeException e) {
			sfLog().error("Unexpected SmartFrogRuntimeException"+e.toString());
			throw e;
         }
    }
    
    public void singleComponentTest() throws Exception {
        Transaction t1 = Transaction.nullTransaction;
        Transaction t2 = Transaction.nullTransaction;
        RComponent A;
        try {
            sfLog().info("\nExecuting Single component test ");
            A = (RComponent)sfResolve("A");            
            t1 = A.sfGetTransaction();
            t2 = A.sfGetTransaction();
        } catch(Exception e) {
			sfLog().error("Failed to obtain componet A for single component lock test"+e.toString());
			 throw e;
			//System.out.println("Failed to obtain componet A for single component lock test");
            //return;
        }        
        testMethod(A, replaceAttribute, new Object[] {"singleComponent1", "added by t1 in singleComponentTest", t1}, false);        
        testMethod(A, replaceAttribute, new Object[] {"singleComponent2", "added by t2 in singleComponentTest", t2}, true);      
        testMethod(A, replaceAttribute, new Object[] {"singleComponent3", "added by t1 in singleComponentTest", t1}, false);     
        commitTransactions(new Transaction[] {t1, t2});
    }


    public void treeTest() throws Exception{
        Transaction t1 = Transaction.nullTransaction;
        Transaction t2 = Transaction.nullTransaction;
        RComponent B;
        RComponent C;
        try {
            sfLog().info("\nExecuting Tree test");
            B = (RComponent)sfResolve("B");  
            C = (RComponent)sfResolve("C");
            t1 = B.sfGetTransaction();
            t2 = B.sfGetTransaction();
        } catch(Exception e) {
			sfLog().error("Failed to obtain componet A for single component lock test"+e.toString());
            throw e;
			//System.out.println("Failed to obtain componet A for single component lock test");
            //return;
        }
        
        
        testMethod(C, lock, new Object[] {t1}, false);        
        testMethod(B, lockTree, new Object[] {t2}, true);      
        commitTransactions(new Transaction[] {t1});
		testMethod(B, lockTree, new Object[] {t2}, false);       
        commitTransactions(new Transaction[] {t2});       
    }
    
    
    public void treeTestWithOwnLock() throws Exception{
        Transaction t1 = Transaction.nullTransaction;
        Transaction t2 = Transaction.nullTransaction;
        RComponent D;
        RComponent E;
        RComponent F;
        try {
            sfLog().info("\nExecuting Tree test with own lock ");
            D = (RComponent)sfResolve("D");  
            E = (RComponent)sfResolve("E");
            F = (RComponent)sfResolve("F");
            t1 = D.sfGetTransaction();
            t2 = D.sfGetTransaction();
        } catch(Exception e) {
			sfLog().error("Failed to obtain componet A for single component lock test"+e.toString());
			throw e;
            //System.out.println("Failed to obtain componet A for single component lock test");
            //return;
        }
        
        testMethod(F, lock, new Object[] {t1}, false);
        testMethod(E, lock, new Object[] {t2}, false);
		testMethod(D, lockTree, new Object[] {t2}, true);
		testMethod(E, lock, new Object[] {t1}, true);        
        commitTransactions(new Transaction[] {t1});       
        testMethod(D, lockTree, new Object[] {t2}, false);
        commitTransactions(new Transaction[] {t2});
      }
    
    
    public void commitTransactions(Transaction[] transactions) throws StorageException{
        for(Transaction t : transactions) {
            try {
                if( !t.isNull() ) {
                    t.commit();
                }
            } catch (StorageException e) {
				sfLog().error("Failed to obtain componet A for single component lock test"+e.toString());
				throw e;
            }           
        }
    }
    
    public void testMethod(Object o, Method m, Object[] args, boolean expectFailure) throws Exception{
        try {
            m.invoke(o, args);
            if( expectFailure ) {
				sfLog().error("Unexpected success in method");
				throw new Exception("Unexpected success in method");
               // System.out.println("*** Unexpected success in method " + m.getName() + " ***");
            } else {
				sfLog().info(m.getName() + " success - no lock collision");
               // System.out.println(m.getName() + " success - no lock collision");
            }
        } catch (InvocationTargetException ite) {
            Throwable e = ite.getTargetException();
            if( e instanceof ConcurrentTransactionException ) {
               // System.out.println(e.getMessage());
                if( !expectFailure ) {
					sfLog().error("Unexpected lock collision in method " + m.getName() + " " + e.getMessage() );
					throw  new Exception(e);
                    //System.out.println("*** Unexpected lock collision in method " + m.getName() + " " + e.getMessage() + " ***" );
                } else {
					sfLog().info(m.getName() + " failed with lock collision as expected");
                   // System.out.println(m.getName() + " failed with lock collision as expected");
                }
            } else if(e instanceof TransactionException ) {
					sfLog().error("transaction exception in method " + m.getName() + " " + e.getMessage() );
					throw new Exception(e);
                //System.out.println("transaction exception in method " + m.getName() + " " + e.getMessage());
            } else {
					sfLog().error("Non-transaction exception in method " + m.getName() + " " + e.getMessage() );
					throw  new Exception(e);
               // System.out.println("Non-transaction exception in method " + m.getName() + " " + e.getMessage());
            }  
        } catch (IllegalArgumentException e) {
            sfLog().error(e.getMessage() );
			throw e;
        } catch (IllegalAccessException e) {
            sfLog().error(e.getMessage() );
			throw e;
        }
    }   
}
