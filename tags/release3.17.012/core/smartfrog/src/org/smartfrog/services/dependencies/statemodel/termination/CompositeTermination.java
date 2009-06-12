package org.smartfrog.services.dependencies.statemodel.termination;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.statemodel.state.StateComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 */
public class CompositeTermination extends StateComponent {
   Prim toTerminate = null;
   TerminationRecord tr = TerminationRecord.normal(sfCompleteName());
   boolean required;
   boolean detach = false;

   public CompositeTermination() throws RemoteException {
	   super();
   }

   public void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      toTerminate = (Prim) sfResolve("toTerminate", true);
      detach = sfResolve("detachFirst", detach, false);
   }

   public boolean threadBody() {
	   if (sfLog().isInfoEnabled()) sfLog().info("terminating composite with " + tr);
	     //System.out.println("terminating model with " + tr);
	     
	     try {
	        if (detach) {
	           toTerminate.sfDetachAndTerminate(tr);
	        } else {
	           toTerminate.sfTerminate(tr);
	        }
	     } catch (RemoteException e) {
	        sfLog().error("failed to terminate model - hope liveness picks it up!", e);
	     }
	   return true;
   }
}

