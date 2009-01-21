package org.smartfrog.services.dependencies.legacy.statemodel.dependency;

import org.smartfrog.services.dependencies.legacy.statemodel.state.StateChangeNotification;
import org.smartfrog.services.dependencies.legacy.statemodel.state.StateDependencies;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Context;

import java.rmi.RemoteException;


/**
 * Class that implements the dependency between two StateDependencies-implementing objects.
 * <p/>
 * On creation will add the dependency, or termination will remove it.
 */
public class Dependency extends PrimImpl implements Prim, DependencyValidation, StateChangeNotification {
   StateDependencies by = null;
   Prim on = null;

   boolean relevant;
   boolean enabled;
   boolean isEnabled; //the result...

   public Dependency() throws RemoteException {
   }

   public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      by = (StateDependencies) sfResolve("by", true);
      on = (Prim) sfResolve("on", true);
   }

   public synchronized void sfStart() throws SmartFrogException, RemoteException {
      super.sfStart();
      by.register(this);
   }

   public synchronized void sfTerminateWith(TerminationRecord tr) {
      try {
         by.deregister(this);
      } catch (Exception e) {
      }
      super.sfTerminateWith(tr);
   }

   public boolean isEnabled() {
      try {
         relevant = sfResolve("relevant", true, false);
         enabled = sfResolve("enabled", true, false);
         isEnabled = !relevant || enabled && (!(on instanceof DependencyValidation) || ((DependencyValidation) on).isEnabled());
      } catch (Exception e) {
         // ?? what to do ??
         isEnabled = false;
      }
      return isEnabled;
   }

   public void handleStateChange() {
      //maybe cache and clear cache, or something....
   }

   public void notifyStateChange() {
      //@TODO deal with this better or split StateChangeNotification interface
      //not needed on dependency
   }
}
