package org.smartfrog.services.dependencies.legacy.statemodel.state;

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

	   final Object updateLock = new Object();
	   final Object notifyLock = new Object();

	   //at most one of the following conditions may be true at any time:
	   //  stateLockCount > 0,
	   //  notificationInProgress,
	   //  updateInProgress
	   int stateLockCount = 0;
	   boolean notificationInProgress = false;
	   boolean updateInProgress = false;

	   // indicator that an notification is required at a specific moment
	   boolean notificationRequired = false;
	   boolean updateRequired = false;

	   boolean running = true;

	   int threadCount = 0;

	   ThreadPool threadpool;


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


	   Object notifier = null;
	   public void notifyStateChange() {
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
	   }


	   // the locks between state saving and notification to ensure that updates and notifies don't happen at the same time!
	   // the state saves set the locks (counted), whereas the notify thread simply checks the count is zero and holds the
	   // notifyLock until finished.
	   public void lock() {
	      synchronized (notifyLock) {
	         if (threadpool.removeFromQueue(notifier)) {
	            //System.out.println("notifier killed");
	         }
	         if (sfLog().isDebugEnabled()) sfLog().debug("locking " + stateLockCount);
	         stateLockCount++;
	      }
	   }

	   public void unlock(boolean notify) {
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


	   // the count of the number ot threeds - be they notification or state threads - running
	   // used in determining quiescence

	   // do started before submitting thread, do stopped at the end of the thread
	   public void threadStarted() {
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
	   }

	   public void threadStopped() {
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
	   }


	   /* *************************************************
	   * Update class
	   */
	   protected class Notifier implements Runnable {
	      public void run() {
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
	                        handleStateChange();
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
	      }
	   }

	   public boolean isQuiescent() {
	      // there are no state threads still running or commited to running
	      // there is no sweep happening or scheduled to happen
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
	      //updateLock.notifyAll();
	      notifyStateChange();
	   }
	}