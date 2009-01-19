package org.smartfrog.services.dependencies.statemodel.state;

import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogUpdateException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.compound.Compound;

import java.rmi.RemoteException;

/**

 */
public class Model extends Composite implements Compound {

	   private final Object updateLock = new Object();
	   private final Object notifyLock = new Object();

	   //at most one of the following conditions may be true at any time:
	   //  stateLockCount > 0,
	   //  notificationInProgress,
	   //  updateInProgress
	   private int stateLockCount = 0;
	   private boolean notificationInProgress = false;
	   private boolean updateInProgress = false;

	   // indicator that a notification is required at a specific moment
	   private boolean notificationRequired = false;
	   private boolean updateRequired = false;

	   // Propagate notifications?
	   private boolean running = true;
	   
	   //Maintains a count of threads outstanding on which it bases quiesence
	   private int threadCount = 0;

	   //Threads on tap...
	   private ThreadPool threadpool;

	   private Thread notificationThread = null;
	   private Object notifier = null;  
	   
	   public Model() throws RemoteException {
	   }

	   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
	      super.sfDeploy();
	      threadpool = (ThreadPool) sfResolve("threadpool", true);
	      running = sfResolve("run", running, false);
	   }

	   public synchronized Object sfReplaceAttribute(Object name, Object value)
	         throws SmartFrogRuntimeException, RemoteException {
	      Object result = super.sfReplaceAttribute(name, value);
	      if (name.equals("run")) {
	         if (value instanceof Boolean) {
	            running = ((Boolean) value).booleanValue();
	         }
	         if (running) notifyStateChange();
	      }
	      return result;
	   }

	   protected void checkParentNotificationLock() throws SmartFrogException {
	      // anything is ok as a parent, so overide the check defined in Composite
	   }

	   public synchronized void sfStart() throws RemoteException, SmartFrogException {
	      super.sfStart();
	      notifyStateChange();
	   }


	   public void notifyStateChange() {
		   if (sfLog().isDebugEnabled())  sfLog().debug("IN: Model.notifyStateChange()");
	      synchronized (notifyLock) {
	         if (stateLockCount == 0 && running) {
	            threadStarted();
	            notificationRequired = false;
	            //System.out.println("notifier launched");
	            notifier = threadpool.addToQueue(new Notifier());
	         } else { // delay the start - someone still needs to run
	            notificationRequired = true;
	         }
	      }
	      if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Model.notifyStateChange()");
	   }


	   // the locks between state saving and notification to ensure that updates and notifies don't happen at the same time!
	   // the state saves set the locks (counted), whereas the notify thread simply checks the count is zero and holds the
	   // notifyLock until finished.
	   public void lock() {
		  if (sfLog().isDebugEnabled())  sfLog().debug("IN: Model.lock()");
		  if (Thread.currentThread()!=notificationThread){
		      synchronized (notifyLock) {
		         if (threadpool.removeFromQueue(notifier)) {
		            //System.out.println("notifier killed");
		         }
		         if (sfLog().isDebugEnabled()) sfLog().debug("locking " + stateLockCount);
		         stateLockCount++;
		      }
		  }
	      if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Model.lock()");
	   }

	   public void unlock(boolean notify) {
		  if (sfLog().isDebugEnabled())  sfLog().debug("IN: Model.unlock(...)"+notify);  
		  if (Thread.currentThread()!=notificationThread){
		      synchronized (notifyLock) {
		         if (sfLog().isDebugEnabled()) sfLog().debug("unlocking " + stateLockCount + " " + notify);
		         if (--stateLockCount == 0) {
		            if (sfLog().isTraceEnabled()) sfLog().trace("unlocking no more locks");
		            if (notificationRequired || notify) {
		               if (sfLog().isTraceEnabled()) sfLog().trace("unlocking starting notify");
		               notifyStateChange();
		            }
		         } else {
		            notificationRequired = notificationRequired || notify;
		         }
		      }
		  }
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Model.unlock(...)");    
	   }


	   // the count of the number ot threeds - be they notification or state threads - running
	   // used in determining quiescence

	   // do started before submitting thread, do stopped at the end of the thread
	   public void threadStarted() {
		   if (sfLog().isDebugEnabled())  sfLog().debug("IN: Model.threadStarted()");    
		   synchronized (updateLock) {
	         if (sfLog().isDebugEnabled()) sfLog().debug("thread started " + threadCount + " " + Thread.currentThread());
	         if (updateInProgress) {
	            try {
	               if (sfLog().isDebugEnabled()) sfLog().debug("waiting on the update");
	               updateLock.wait();
	               if (sfLog().isDebugEnabled()) sfLog().debug("waiting on the update complete");

	            } catch (InterruptedException e) {
	               return;
	            }
	         }
	         threadCount++;
	      }
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Model.threadStarted()");     
	   }

	   public void threadStopped() {
		   if (sfLog().isDebugEnabled())  sfLog().debug("IN: Model.threadStopped()");    
		   synchronized (updateLock) {
	         threadCount--;
	         if (isQuiescent()) {
	            if (updateRequired) {
	               updateInProgress = true;
	               if (sfLog().isDebugEnabled()) sfLog().debug("update required, and now quiescent, so notifying");
	               updateLock.notifyAll();
	            }
	         }
	         if (sfLog().isDebugEnabled())
	            sfLog().debug("thread stopped - quiescent: " + isQuiescent() + " " + threadCount + " " + Thread.currentThread());
	      }
		   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Model.threadStopped()");    
	   }


	   /* *************************************************
	   * Update class
	   */
	   protected class Notifier implements Runnable {
	      public void run() {
	    	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: Model.Notifier.run()");    
	         //System.out.println("notifier running");
	         try {
	            if (running) {
	               if (sfLog().isDebugEnabled()) sfLog().debug("notify started");

	               synchronized (notifyLock) {
	                  notificationInProgress = true;
	                  if (stateLockCount > 0) {
	                     notificationRequired = true;
	                  } else {
	                    try {
	                    	Model.this.notificationThread = Thread.currentThread();
	                        handleStateChange();
	                        Model.this.notificationThread = null;
	                        if (sfLog().isTraceEnabled()) sfLog().trace("notify notified");
	                     } finally {
	                        notificationInProgress = false;
	                        notificationRequired = false;
	                     }
	                  }
	               }

	               if (sfLog().isDebugEnabled()) sfLog().debug("notify completed");
	            }
	         } finally {
	            threadStopped();
	            //System.out.println("notifier finished");
	         }
	         if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Model.Notifier.run()");    
	      }
	   }

	   public boolean isQuiescent() {
	      // there are no state threads still running or commited to running
	      // there is no sweep happening or scheduled to happen
		   if (sfLog().isDebugEnabled())  sfLog().debug("IN/OUT: Model.isQuiescent()");    
		   synchronized (updateLock) {
	         return (threadCount == 0);
	      }
	   }


	   public synchronized void sfPrepareUpdate() throws RemoteException, SmartFrogException {
	      if (sfLog().isDebugEnabled()) sfLog().debug("update preparing");
	      synchronized (updateLock) {
	         if (sfLog().isDebugEnabled()) sfLog().debug("update lock obtained");
	         if (updateInProgress) {
	            throw new SmartFrogUpdateException("model already being updated - cannot do so until previous is complete " +
	                  sfCompleteNameSafe());
	         }
	         if (!isQuiescent()) try {
	            if (sfLog().isDebugEnabled()) sfLog().debug("update triggered, but not quiescent, so waiting");
	            updateRequired = true;
	            updateLock.wait();
	            if (sfLog().isDebugEnabled()) sfLog().debug("update triggered, but now quiescent");
	         } catch (InterruptedException e) {
	            //??
	         }
	      }
	      if (sfLog().isDebugEnabled()) sfLog().debug("super update prepare");
	      updateInProgress = true;
	      super.sfPrepareUpdate();
	      if (sfLog().isDebugEnabled()) sfLog().debug("update prepared");
	   }

	   public synchronized boolean sfUpdateWith(Context newCxt) throws RemoteException, SmartFrogException {
	      try {
	         return super.sfUpdateWith(newCxt);
	      } catch (SmartFrogException e) {
	         updateRequired = false;
	         updateInProgress = false;
	         throw e;
	      }
	   }


	   public synchronized void sfUpdateStart() throws RemoteException, SmartFrogException {
	      super.sfUpdateStart();

	      updateRequired = false;
	      updateInProgress = false;

	      if (sfLog().isDebugEnabled())
	         sfLog().debug("update fininshed, triggering state change and allowing actions through");
	      notifyStateChange();
	   }
	}