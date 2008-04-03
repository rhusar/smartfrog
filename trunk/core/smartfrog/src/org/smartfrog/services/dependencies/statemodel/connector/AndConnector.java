package org.smartfrog.services.dependencies.statemodel.connector;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.services.dependencies.statemodel.state.StateDependencies;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;

/**
 */
 public class AndConnector extends PrimImpl implements Prim, DependencyValidation, StateDependencies {
   protected HashSet dependencies = new HashSet();

   public AndConnector() throws SmartFrogException, RemoteException {
   }


   public boolean isEnabled() {
      for (Iterator d = dependencies.iterator(); d.hasNext();) {
         try {
            if (!((DependencyValidation) d.next()).isEnabled()) {
               return false;
            }
         } catch (RemoteException e) {
            //??
         }
      }
      return true;
   }

   public void register(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.add(d);
   }

   public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.remove(d);
   }
}
