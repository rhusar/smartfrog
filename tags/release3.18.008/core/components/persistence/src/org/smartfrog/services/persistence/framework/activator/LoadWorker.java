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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.smartfrog.services.persistence.rcomponent.RComponentImpl;
import org.smartfrog.services.persistence.storage.StorageDeploymentAccessException;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.services.persistence.storage.TransactionException;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;


/**
 * Component loading is initiated by the activator as part of the activation
 * action. The normal behaviour is to load all components as one synchronous operation,
 * but components can be marked for lazy loading. If that is the case the children of
 * those components are queued to be loaded later asynchronously. The load worker is
 * is a thread that performs this loading in the background. When loading is complete
 * the thread terminates, so activation will also start a load worker to handle
 * asynchronous lazy loading.
 */
public class LoadWorker extends Thread {
    
    class TransactionLock {
        private Transaction transaction;
        private boolean transactionFree;
        public TransactionLock(Transaction xact) throws TransactionException {
            transaction = xact;
            transaction.setReusable(true);
            transactionFree = true;            
        }
        public synchronized Transaction get() throws TransactionException {
            while(running) {
                if( transactionFree ) {
                    transactionFree = false;
                    return transaction;
                }
                try { wait(); } 
                catch (InterruptedException e) { }
            }
            throw new TransactionException("Load worker is not running");            
        }
        public synchronized void release(Transaction xact) {
            if( transaction != xact ) {
                return;
            }
            if( running ) {
                transactionFree = true;
            } else {
                close();
            }
            notifyAll();
        }
        public synchronized void terminate() {
            if( transactionFree ) {
                close();
            }
        }
        private void close() {
            try {
                transactionFree = false;
                transaction.setReusable(false);
                transaction.commit();
            } catch (Exception e) {
                // ignore - it is valid for there to be failures
                // here as we are possibly cleaning up on failure
            }
        }
        
    }
    
    private volatile boolean running = true;
    private BlockingQueue<RComponentImpl> queue = new LinkedBlockingQueue<RComponentImpl>();
    private Object queueMonitor = new Object();
    private LogSF log = LogFactory.getLog(LoadWorker.class);
    private TransactionLock transactionLock;

    public LoadWorker(Transaction xact) throws TransactionException {
        super("Load Worker");
        super.setDaemon(true);
        transactionLock = new TransactionLock(xact);
    }
    
    public void terminate() {
        running = false;
        synchronized(queueMonitor) {
            queueMonitor.notify();
        }
    }
    
    public Transaction getTransaction() throws TransactionException {
        return transactionLock.get();        
    }
    
    public void releaseTransaction(Transaction xact) {
        transactionLock.release(xact);
    }

    public void add(RComponentImpl rcomp) {
        if(running) {
            synchronized(queueMonitor) {
                queue.add(rcomp);
                queueMonitor.notify();
            }
        }
    }
    
    public void remove(RComponentImpl rcomp) {
        if(running) {
            queue.remove(rcomp);
        }
    }
    

    public void run() {
        RComponentImpl rcomp = null;
        while( running ) {
            
            synchronized(queueMonitor) {
                if( queue.isEmpty() ) {
                    running = false;
                    break;
                } else {
                    rcomp = queue.poll();
                }
            }
            
            if( running && rcomp != null ) {
                try { rcomp.loadLazy(); } 
                catch (StorageDeploymentAccessException e1) {
                    running = false;
                    break;
                }
                try { sleep(100); } 
                catch (InterruptedException e) { }
            }
            
        }
        
        transactionLock.terminate();
        
        if( log.isDebugEnabled() ) {
            log.debug("========== load worker terminating ==========");
        }
    }
    
    

}


