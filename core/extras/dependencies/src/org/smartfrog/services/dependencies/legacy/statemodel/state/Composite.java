package org.smartfrog.services.dependencies.legacy.statemodel.state;

import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogUpdateException;
import org.smartfrog.sfcore.prim.Liveness;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 *
 */
public class Composite extends CompoundImpl implements Compound, StateChangeNotification, NotificationLock {
   NotificationLock parentLocking;

   public Composite() throws RemoteException {
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
      for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
         Object c = e.nextElement();
         if (c instanceof StateChangeNotification) try {
            ((StateChangeNotification)c).handleStateChange();
         } catch (RemoteException e1) {
            //??
         }
      }
   }

   public void lock() throws RemoteException {
      ((NotificationLock)parentLocking).lock();
   }

   public void unlock(boolean notify) throws RemoteException {
      ((NotificationLock)parentLocking).unlock(notify);
   }

   public void notifyStateChange() throws RemoteException {
      ((NotificationLock)parentLocking).notifyStateChange();
   }

   public void threadStarted() throws RemoteException {
      ((NotificationLock)parentLocking).threadStarted();
   }

   public void threadStopped() throws RemoteException {
      ((NotificationLock)parentLocking).threadStopped();
   }

   public synchronized boolean sfUpdateWith(Context newCxt) throws RemoteException, SmartFrogException {
      boolean result = super.sfUpdateWith(newCxt);

      if (childrenToTerminate.size() > 0) {
         throw new SmartFrogUpdateException("Composite may not (currently) be updated with terminations " +
                            sfCompleteNameSafe());
      }
      /*
      if (childrenToCreate.size() > 0) {
         throw new SmartFrogUpdateException("Composite may not (currently) be updated with creations " +
                            sfCompleteNameSafe());
      }
      */

      return result;
   }
}
