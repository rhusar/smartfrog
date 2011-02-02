package org.smartfrog.services.persistence.test.testcases;

import java.rmi.RemoteException;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.rcomponent.RComponentImpl;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.TerminationRecord;


public class SimpleRComponent extends RComponentImpl implements RComponent {

    public SimpleRComponent() throws RemoteException {
        // TODO Auto-generated constructor stub
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if( sfIsRecovered ) {
           // System.out.println("Simple RComponent NOT adding an attribute - in recovery");
        } else {
           // System.out.println("Simple RComponent adding an attribute");
            sfAddAttribute("attribute1", "value1");
        }
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
       /* try {
            //System.out.println("Simple RComponent terminating " + sfCompleteName() );
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
        super.sfTerminateWith(status);
    }
    
    public void sfDetachPendingTermination(Transaction xact) throws SmartFrogRuntimeException, RemoteException {
        //System.out.println("Simple RComponent detaching " + sfCompleteName() );
        super.sfDetachPendingTermination(xact);
    }

    public void sfReattachPending(Transaction xact) throws SmartFrogRuntimeException, RemoteException {
       // System.out.println("Simple RComponent reattaching " + sfCompleteName());
        super.sfDetachPendingTermination(xact);
    }



}
