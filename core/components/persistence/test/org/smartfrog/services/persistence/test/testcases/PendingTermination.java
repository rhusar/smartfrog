package org.smartfrog.services.persistence.test.testcases;

import java.rmi.RemoteException;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


public class PendingTermination extends CompoundImpl implements Compound {
    
    public PendingTermination() throws RemoteException {
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            sfLog().info("\nExecuting Tree reattach test");
            testReattach();
			sfLog().info("\nExecuting Tree detach test");
            testDetach();
			sfLog().info("\nExecuting Tree nested detach test");
            testNestedDetach();
			sfLog().info("\nExecuting Tree create and detach test");
            testCreateAndDetach();
        } catch (SmartFrogException e1) {
			throw e1;
        }
		catch (RemoteException e2) {
			throw e2;
        }
        
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
    }
    
    public void testReattach() throws RemoteException, SmartFrogRuntimeException {
        Transaction xact = Transaction.nullTransaction;
        try {
           
            RComponent A = (RComponent)sfResolve("A");
            RComponent parent = (RComponent)sfResolve("AParent");
            String name = (String)parent.sfAttributeKeyFor(A);           
            xact = A.sfGetTransaction();           
            A.sfDetachPendingTermination(xact);           
            A.sfReattachPending(name, parent, xact);
           
        }
		finally {
            if( !xact.isNull() ) {
                xact.commit();
            }
        }
    }

    public void testDetach() throws RemoteException, SmartFrogRuntimeException {
        Transaction xact = Transaction.nullTransaction;
        try {
           
            RComponent B = (RComponent)sfResolve("B");           
            xact = B.sfGetTransaction();           
            B.sfDetachPendingTermination(xact);
          
        } finally {
            if( !xact.isNull() ) {
                xact.commit();
            }
        }
    }
    
    public void testNestedDetach() throws RemoteException, SmartFrogRuntimeException {
        Transaction xact = Transaction.nullTransaction;
        try {
           
            RComponent C = (RComponent)sfResolve("C");
            RComponent D = (RComponent)sfResolve("D");           
            xact = C.sfGetTransaction();           
            C.sfDetachPendingTermination(xact);
			D.sfDetachPendingTermination(xact);
           
        } finally {
            if( !xact.isNull() ) {
                xact.commit();
            }
        }
    }
    
    public void testCreateAndDetach() throws RemoteException, SmartFrogRuntimeException {
        Transaction xact = Transaction.nullTransaction;
        try {
            RComponent E = (RComponent)sfResolve("E");
            ComponentDescription cd = (ComponentDescription)sfResolve("F_cd");
            xact = E.sfGetTransaction();           
            RComponent F = (RComponent)E.sfCreateNewChild("F", cd, null, xact);          
            F.sfDetachPendingTermination(xact);
           
        } finally {
            if( !xact.isNull() ) {
                xact.commit();
            }
        }
    }

}
