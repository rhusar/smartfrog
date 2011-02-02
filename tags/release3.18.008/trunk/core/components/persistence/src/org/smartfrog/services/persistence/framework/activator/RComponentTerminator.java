/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */

package org.smartfrog.services.persistence.framework.activator;

import java.rmi.RemoteException;
import java.util.Collection;

import org.smartfrog.services.persistence.rcomponent.RComponentImpl;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.services.persistence.storage.TransactionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimHook;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * This class deals with the management of the disposal worker and load worker
 * threads. This component is loaded when the frame work starts and sets a
 * deployWithHook. When RComponents are deployed, the deployWithHook will
 * set its load worker and disposal worker to be the ones managed through this
 * component.  
 */
public class RComponentTerminator extends CompoundImpl implements Compound {
    
    private PrimHook deployWithHook = new DeployWithHook();
    private DisposalWorker disposalWorker = null;
    private LoadWorker loadWorker = null;

    public RComponentTerminator() throws RemoteException {
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        sfDeployWithHooks.addHook(deployWithHook);
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        try {
            sfDeployWithHooks.removeHook(deployWithHook);
        } catch (SmartFrogLifecycleException e) {
            
        }
        super.sfTerminateWith(status);
    }
    
    
    public synchronized void stopDisposal() {
        if( disposalWorker != null ) {
            disposalWorker.terminate();
            disposalWorker = null;
        }
    }
    
    public synchronized void stopLoading() {
        if( loadWorker != null ) {
            loadWorker.terminate();
            loadWorker = null;
        }
    }
    
    public synchronized void startDisposal() {
        stopDisposal();
        disposalWorker = new DisposalWorker();
        disposalWorker.start();
    }
    
    public synchronized void startLoading() {
        loadWorker.start();
    }
    
    public synchronized void newLoader(Transaction xact) throws TransactionException {
        stopLoading();
        loadWorker = new LoadWorker(xact);        
    }
    
    public synchronized void handleOrphans(Collection orphans) {
        if( disposalWorker != null ) {
            disposalWorker.addOrphans(orphans);
        }
    }
    
    
    private class DeployWithHook implements PrimHook {
        
        public void sfHookAction(Prim prim, TerminationRecord terminationRecord) throws SmartFrogException {
            /**
             * We only deal with RComponents derived from RComponentImpl
             */
            if( !(prim instanceof RComponentImpl) ) {
                return;
            }
            
            RComponentImpl rcomp = (RComponentImpl)prim;
            rcomp.setDisposalWorker(disposalWorker);
            rcomp.setLoadWorker(loadWorker);
        }

    }

}
