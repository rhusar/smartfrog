package org.smartfrog.services.dependencies.legacy.statemodel.modeltermination;

import org.smartfrog.services.dependencies.legacy.statemodel.state.ThreadedState;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.HashMap;

/**
 */
public class ModelTermination extends ThreadedState {
   Prim toTerminate = null;
   TerminationRecord tr = TerminationRecord.normal(sfCompleteName());
   boolean required;
   boolean detach = false;

   public ModelTermination() throws RemoteException {
   }

   public void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      toTerminate = (Prim) sfResolve("toTerminate", true);
      detach = sfResolve("detachfirst", detach, false);
   }

   public boolean requireThread(HashMap data) {
      return ((Boolean) data.get("required")).booleanValue();
   }

   public HashMap threadBody(HashMap data) {
      required = ((Boolean) data.get("required")).booleanValue();
      if (required) {
         if (sfLog().isInfoEnabled()) sfLog().info("terminating model with " + tr);
         try {
            if (detach) {
               toTerminate.sfDetachAndTerminate(tr);
            } else {
               toTerminate.sfTerminate(tr);
            }
         } catch (RemoteException e) {
            sfLog().error("failed to terminate model - hope liveness picks it up!", e);
         }
      } else {
         if (sfLog().isInfoEnabled()) sfLog().info("not terminating model");
      }
      return new HashMap();
   }
}

