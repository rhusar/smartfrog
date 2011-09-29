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
import java.util.Vector;

import org.smartfrog.services.persistence.framework.connectionpool.ConnectionPool;
import org.smartfrog.services.persistence.framework.interfaceguard.InterfaceGuard;
import org.smartfrog.services.persistence.framework.interfaceguard.InterfaceGuardImpl;
import org.smartfrog.services.persistence.framework.interfaceguard.InterfaceGuardSetter;
import org.smartfrog.services.persistence.framework.interfaceguard.InterfaceManager;
import org.smartfrog.services.persistence.storage.StorageAccessException;
import org.smartfrog.services.persistence.storage.StorageDeploymentAccessException;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.StorageExceptionNotification;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * ActivatorImpl is the class that manages the process of activation and deactivation. 
 * These actions are asynchronous, so when an activate or deactivate
 * method returns the action will not have been completed. Sequences of actions are queued, but 
 * deactivae takes precedence over earlier actions and will remove all prior actions from the 
 * queue. If there are two activate actions in a row the second is a noop, so it will not be queued.
 * Consequently there can never be more than two actions queued (deactivate followed by activate).
 */
public class ActivatorImpl extends PrimImpl implements Prim, InterfaceManager, Runnable, Activator {
    
    private static final String CONNECTION_POOL_ATTR          = "connectionPool";
    private static final String REGISTER_ATTR                 = "register";
    private static final String STORAGE_EXCEPTION_TARGET_ATTR = "storageExceptionTarget";
    private static final String HOSTS_ATTR                    = "recoveryHostList";
    
    enum Action { NONE, ACTIVATE, DEACTIVATE; }
    class ActionQueue {
    	Action firstAction = Action.NONE;
        Action secondAction = Action.NONE;
        void AddActivate() {
        	if( firstAction == Action.DEACTIVATE ) {
        		secondAction = Action.ACTIVATE;
        	} else {
        		firstAction = Action.ACTIVATE;
        	}
        }
        void AddDeactivate() {
        	firstAction = Action.DEACTIVATE;
        	secondAction = Action.NONE;
        }
        boolean contains(Action action) {
        	return (firstAction == action || secondAction == action);
        }
        Action get() {
        	Action result = firstAction;
        	firstAction = secondAction;
        	secondAction = Action.NONE;
        	return result;
        }
        boolean empty() {
        	return firstAction == Action.NONE;
        }
        void clear() {
        	firstAction = Action.NONE;
        	secondAction = Action.NONE;
        }
    }
    
    private ActionQueue queue = new ActionQueue();
    
    private boolean isActive                    = false;
    private Status  activationStatus            = Status.SUCCESS;
    
    private boolean running                     = true;
    private Vector<String> hosts                = null;
    private ConnectionPool connectionPool       = null;
    private Register register                   = null;
    private InterfaceGuardSetter interfaceGuard = new InterfaceGuardImpl();
    private Object activationMonitor            = new Object();
    private Object terminationMonitor           = new Object();
    private StorageExceptionNotification sen    = null;
    
    public ActivatorImpl() throws RemoteException {
        super();
    }
    
    public void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy();
        connectionPool = (ConnectionPool)sfResolve(CONNECTION_POOL_ATTR);
        register = (Register)sfResolve(REGISTER_ATTR);
        hosts = (Vector<String>)sfResolve(HOSTS_ATTR);
        sen = (StorageExceptionNotification)sfResolve(STORAGE_EXCEPTION_TARGET_ATTR);
        new Thread(this, "Activator").start();
    }
    
    public void sfTerminateWith(TerminationRecord tr) {
        terminate();
        super.sfTerminateWith(tr);
    }
    
    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#sanityCheck(java.lang.StringBuffer)
     */
    public synchronized void sanityCheck(StringBuffer out) throws RemoteException, SmartFrogException { 
        
    	if( isActive || queue.contains(Action.ACTIVATE) ) {
            out.append("#################################");
            out.append("# Cannot check sanity when active");
            return;
        }
        
        /**
         * Create the connectionPool, execute sanity check and
         * close the connection pool.
         */
        connectionPool.createConnectionPool();
        try {
            register.checkSanity(out);
        } finally {
            connectionPool.closeConnectionPool();
        }
        
    }
    

    
    public void run() {
        while(true) {
            
            synchronized(this) {
                
                /**
                 * If not running we break the loop
                 */
                if(!running) {
                    break;
                }
                
                /**
                 * open the interfaces if we are active and status is ACTIVATING 
                 * and there are no more actions queued 
                 * (Status.ACTIVATING => isActive, so isActive part is probably superfluous)
                 */
                if( isActive && activationStatus == Status.ACTIVATING && queue.empty() ) {
                	interfaceGuard.open();
                	activationStatus = Status.SUCCESS;
                	if( sfLog().isDebugEnabled() ) {
                		sfLog().debug("Activator: activation status is SUCCESS");
                	}
                }
                
                /**
                 * If there is no action queued up then wait
                 */
                if( queue.empty() ) {

                    try {
                        if( sfLog().isDebugEnabled() ) {
                            sfLog().debug("Activator: no work to do, waiting - persistence framework is " + (isActive ? "active" : "inactive"));
                        }
                        wait();
                    } catch (InterruptedException e) {
                    }

                }
                
                /**
                 * If we stopped running during the wait we break
                 * the loop. 
                 */
                if( !running ) { 
                    break; 
                } 
                
                /**
                 * If nothing to do loop round.
                 */
                if( queue.empty() ) {
                    continue;
                } 
                
                /**
                 * Set the next action to match be the next queued action
                 */
                isActive = (queue.get() == Action.ACTIVATE);
                
                /**
                 * if we are activating open the connection pool now
                 */
                if( isActive ) {
                	connectionPool.createConnectionPool();
                	activationStatus = Status.ACTIVATING;
                	if( sfLog().isDebugEnabled() ) {
                		sfLog().debug("Activator: activation status is ACTIVATING");
                	}
                }
            }

            /**
             * This part is synchronized to make exclusive
             * the deactivation part of terminate().
             */
            synchronized(activationMonitor) {
                /**
                 * Do asynchronous activation/deactivation
                 */
                if (isActive) {

                    asyncActivatePart();

                } else {

                    asyncDeactivatePart();
                    
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#terminate()
     */
    public void terminate() {
        
        /**
         * This method should not be called twice as 
         * it could lead to deadlock - see comment below. 
         * This synchronized block prevents this from happening
         * and is the reason that the method is not synchronized.
         */
        synchronized(terminationMonitor) {
            if( !running ) {
                return;
            } else {
                running = false;
            }
        }

        /**
         * Synchronized on this and activationMonitor. This could cause deadlock
         * if the method is called from inside the asyncActivation() or asyncDeativation()
         * methods as they are also synchronized on activationMonitor inside the run() method).
         */
        synchronized(this) {

        	if( sfLog().isDebugEnabled() ) {
        		sfLog().debug("Activator: Initiating Termination");
        	}

        	queue.empty();
        	isActive = false;
        	interfaceGuard.close();
        	connectionPool.closeConnectionPool();
            notify();
        }

        /**
         * Do the deactivate synchronously so that it is complete 
         * by the time that this method completes.
         */
        synchronized(activationMonitor) {

        	try {
        		register.sfUnloadAll();
        	} catch (RemoteException e) {
        		if( sfLog().isDebugEnabled() ) {
        			sfLog().debug("Activator: Unexpected remote exception in deactivation during termination");
        		}
        	}
        }
    }
     
    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#activate()
     */
    public synchronized boolean activate() {
        
    	/**
    	 * if we are not running return false
    	 */
        if( !running ) {
            return false;
        }
        
        /**
         * under the following conditions do nothing and return false:
         * 1. we are active and there is no actions queued
         * 2. there is an activate queued already
         * (activate will never precede deactivate in queue)
         */
        if( isActive && queue.empty() || queue.contains(Action.ACTIVATE) ) {  
        	return false;
        }
        
        /**
         * queue an activation
         */        
        if( sfLog().isDebugEnabled() ) {
            sfLog().debug("Activator: Initiating Activation");
        }
        queue.AddActivate();
        activationStatus = Status.PENDING;
    	if( sfLog().isDebugEnabled() ) {
    		sfLog().debug("Activator: activation status is PENDING");
    	}
        notify();
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#deactivate()
     */
    public synchronized boolean deactivate() {
        
    	/**
    	 * if we are not running return false
    	 */
        if( !running ) {
            return false;
        }
        
        /**
         * if we are inactive and the queue is empty
         * return false. Note we assume we can always 
         * schedule deactivation and do the initial cut
         * off actions, even if already scheduled. 
         */
        if( !isActive && queue.empty() ) {
            return false;
        }
        
        if( sfLog().isDebugEnabled() ) {
            sfLog().debug("Activator: Initiating Deactivation");
        }
        
        /**
         * Add the deactivation action to the queue. If this cancels a pending or 
         * in progress activation set the activation status to failure, do initial
         * actions to close the connection pool and interfaces.
         */
        queue.AddDeactivate();
        if( activationStatus == Status.PENDING || activationStatus == Status.ACTIVATING ) {
        	activationStatus = Status.FAILURE;
        	if( sfLog().isDebugEnabled() ) {
        		sfLog().debug("Activator: activation status is FAILURE");
        	}
        }
        interfaceGuard.close();
        connectionPool.closeConnectionPool();
        notify();
        return true;
    }
    
    /**
     * This method loads components from the database.
     */
    private void asyncActivatePart() {
        
    	/**
    	 * if we are not running return false
    	 */
        if( !running ) {
            return;
        }
        
        try {
            register.sfLoadAll();
        } catch(StorageAccessException e) {
            if( sfLog().isErrorEnabled() ) {
                sfLog().error("Activator: Storage cut off during activation - load abandoned");
            }
            deactivate();
        } catch(StorageDeploymentAccessException e) {
            if( sfLog().isErrorEnabled() ) {
                sfLog().error("Activator: Storage cut off during activation - load abandoned");
            }
            deactivate();
        } catch(SmartFrogException e) {
        	if( sfLog().isErrorEnabled() ) {
        		sfLog().error("Activator: SmartFrog error loading data", e);
        	}                
            deactivate();
        } catch (Throwable e) {
            if( sfLog().isFatalEnabled() ) {
                sfLog().fatal("Activator: unknown error loading data", e);
            }
            deactivate();
        }        
    }
    
    /**
     * This method unloads components. 
     * It is expected to execute quickly.
     */
    private void asyncDeactivatePart() {
        
        if( !running ) {
            return;
        }
        
        try {
            register.sfUnloadAll();
        } catch (RemoteException e) {
            if( sfLog().isDebugEnabled() ) {
                sfLog().debug("Activator: Unexpected remote exception in asyncDeactivatePart()");
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.interfaceguard.InterfaceManager#getInterfaceGuard()
     */
    public InterfaceGuard getInterfaceGuard() {
        return (InterfaceGuard)interfaceGuard;
    }

    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.interfaceguard.InterfaceManager#getPendingTermination()
     */
    public PendingTermination getPendingTermination() {
        return (PendingTermination)interfaceGuard;
    }

    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.interfaceguard.InterfaceManager#getRecoveryHosts()
     */
    public Vector<String> getRecoveryHosts() {
        return hosts;
    }

    /**
     * Storage exceptions are forwarded on to the storage manager.
     */
    public void storageExceptionNotification(StorageException s) {
        sen.storageExceptionNotification(s);
    }

    /* (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.Activator#getActivationStatus()
     */
	public synchronized Status getActivationStatus() {
		return activationStatus;
	}

}
