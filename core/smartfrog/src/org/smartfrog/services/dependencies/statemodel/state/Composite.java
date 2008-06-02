package org.smartfrog.services.dependencies.statemodel.state;

import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogUpdateException;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 *
 */
public class Composite extends CompoundImpl implements Compound, StateChangeNotification, NotificationLock {
   NotificationLock parentLocking;

   public Composite() throws RemoteException {
	   super();
   }

   public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      checkParentNotificationLock();
   }

   protected void checkParentNotificationLock () throws SmartFrogException {
      try {
         parentLocking = (NotificationLock) sfParent();
      } catch (Exception e) {
         throw new SmartFrogException("error in composite " + sfCompleteNameSafe() + ": parent is not a Composite or Model");
      }
   }

   //child down to State, where it is handled
   public void handleStateChange() {
      for (Enumeration e = sfChildren(); e.hasMoreElements(); ) {
         Object c = e.nextElement();
         if (c instanceof StateChangeNotification) 
            ((StateChangeNotification)c).handleStateChange();
      }
   }

   public void lock() {
      ((NotificationLock)parentLocking).lock();
   }

   public void unlock(boolean notify) {
      ((NotificationLock)parentLocking).unlock(notify);
   }

   public void notifyStateChange() {
      ((NotificationLock)parentLocking).notifyStateChange();
   }

   public void threadStarted() {
      ((NotificationLock)parentLocking).threadStarted();
   }

   public void threadStopped() {
      ((NotificationLock)parentLocking).threadStopped();
   }

   public synchronized boolean sfUpdateWith(Context newCxt) throws RemoteException, SmartFrogException {
      boolean result = super.sfUpdateWith(newCxt);

      if (childrenToTerminate.size() > 0) {
         throw new SmartFrogUpdateException("Composite may not (currently) be updated with terminations " +
                            sfCompleteNameSafe());
      }
      return result;
   }
}
