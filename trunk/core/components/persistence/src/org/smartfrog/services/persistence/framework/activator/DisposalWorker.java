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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.rcomponent.RComponentImpl;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageAccessException;
import org.smartfrog.services.persistence.storage.StorageDeploymentAccessException;
import org.smartfrog.services.persistence.storage.StorageDeploymentMissingException;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;

/**
 * Disposal is the act of removing terminated components from the persistent store: basically
 * persistence garbage collection. For reasons to do with locking and performance, recoverable 
 * components are not removed from the persistent store at the same time as they are terminated 
 * in the smartfrog runtime. Instead they are queued for asynchronous disposal. If a component 
 * is not disposed before a failure it will be recovered as an orphaned component. The disposal 
 * worker is a thread that keeps running in the background for terminating orphan components 
 * and performing disposal. 
 */
public class DisposalWorker extends Thread {

    private volatile boolean running = true;
    private BlockingQueue<RComponentImpl> queue = new LinkedBlockingQueue<RComponentImpl>();
    private BlockingQueue<ComponentDescription> orphanQueue = new LinkedBlockingQueue<ComponentDescription>();
    private LogSF log = LogFactory.getLog(DisposalWorker.class);
    private static boolean isTiming = Boolean.parseBoolean(System.getProperty(RComponent.DIAG_TIMING_PROP));

    public DisposalWorker() {
        super("Disposal Worker");
        super.setDaemon(true);
    }
    
    public void terminate() {
        running = false;
    }

    public void add(RComponentImpl rcomp) {
        if(running) {
            queue.add(rcomp);
        }
    }
    
    public void addOrphan(ComponentDescription storage) {
        if( running ) {
            orphanQueue.add(storage);
        }
    }
    
    public void addOrphans(Collection orphans) {
        if( running ) {
            orphanQueue.addAll(orphans);
        }
    }

    public void run() {
        while( running ) {
            terminateOrphans( getOrphans() );
            dispose( getDisposals() );
            try { sleep(500); } 
            catch (InterruptedException e) { }
        }
    }
    
    private BlockingQueue<ComponentDescription> getOrphans() {
        BlockingQueue<ComponentDescription> orphans = new LinkedBlockingQueue<ComponentDescription>();
        int drain = ( (queue.size() / 2) > 100 ? (queue.size() / 2) : 100);
        orphanQueue.drainTo(orphans, drain);
        return orphans;
    }
    
    private void terminateOrphans(BlockingQueue<ComponentDescription> orphans) {
        
        for( ComponentDescription config : orphans ) {
            
            if( !Storage.isStorageDescription(config) ) {
                if( log.isErrorEnabled() ) {
                    log.error("Attempt to terminate orphan that is not a storage description");
                }
                continue;
            }
            
            String name = "";
            try {
                config.sfReplaceAttribute(RComponent.RECOVERY_MARKER_ATTR, RComponent.TERMINATE_RECOVERY_MARKER_VALUE);
                name = (String) config.sfResolveHere(Storage.COMPONENT_NAME_ATTR);

                if (log.isDebugEnabled()) {
                    log.debug("Recoverying orphan " + name);
                }

                long startTime = 0;
                if( isTiming ) {
                    startTime = System.currentTimeMillis();
                }
                
                RComponent comp = (RComponent) SFProcess.getProcessCompound().sfDeployComponentDescription(name, null, config, null);
                comp.sfTerminate(TerminationRecord.abnormal("Orphan terminated after crash recovery", null));
                
                long endTime = 0;
                if( isTiming && log.isInfoEnabled() ) {
                    endTime = System.currentTimeMillis();
                    log.info("Startup recovery and termination for orphan " + name + " completed in " + (endTime - startTime) + " millis.");                    
                }

            } catch( StorageDeploymentAccessException e ) {
                /**
                 * If we can't access the storage, give up
                 */
                return;
                
            } catch( StorageDeploymentMissingException e ) {
                /**
                 * If its missing it cleaned up behind our backs, go on without it
                 */
                continue;
                
            } catch (SmartFrogException e) {
                
                if( log.isErrorEnabled() ) {
                    log.error("Failed to load orphan component " + name + ", continuing without", e);
                }
                
            } catch (RemoteException e) {
                
                if( log.isErrorEnabled() ) {
                    log.error("Remote exception loading or terminating orphan " + name, e);
                }

            }

        }
    }

    private BlockingQueue<RComponentImpl> getDisposals() {
        BlockingQueue<RComponentImpl> disposals = new LinkedBlockingQueue<RComponentImpl>();
        int drain = ( (queue.size() / 2) > 100 ? (queue.size() / 2) : 100);
        queue.drainTo(disposals, drain);
        return disposals;
    }

    private void dispose(BlockingQueue<RComponentImpl> disposals) {
        if( disposals == null ) {
            return;
        }
        RComponentImpl next = null;
        Transaction xact = null;

        /**
         * Get the first that will give us a transaction
         */
         while( xact == null ) {

             if( (next = disposals.poll()) == null ) {
                 /**
                  * If there are no more components give up
                  */
                 return;
             }

             try {
                 xact = next.sfGetTransaction();
             } catch(StorageAccessException e) {
                 /**
                  * If we can't access the storage give up
                  */
                 return;
             } catch (SmartFrogRuntimeException e) {
                 /**
                  * if there is another problem - drop this one and move on
                  */
                 if( log.isDebugEnabled() ) {
                     log.debug("Failed to delete storage - giving up on component" , e);
                 }
                 continue;
             }
         }

         /**
          * Dispose of all in the queue
          */
         next.disposeStorage(xact);
         while( (next = disposals.poll()) != null ) {
             next.disposeStorage(xact);
         }

         /**
          * Commit quietly
          */
         try {
             xact.commit();
         } catch (StorageException e) {
             if( log.isDebugEnabled() ) {
                 log.debug("Failed to commit disposal transaction", e);
             }
         }
    }

}


