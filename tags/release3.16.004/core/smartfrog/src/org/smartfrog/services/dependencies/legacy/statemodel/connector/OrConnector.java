package org.smartfrog.services.dependencies.legacy.statemodel.connector;

import org.smartfrog.services.dependencies.legacy.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.legacy.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.services.dependencies.legacy.statemodel.state.StateDependencies;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.util.HashSet;
import java.util.Iterator;
import java.rmi.RemoteException;

/**
 */
 public class OrConnector extends PrimImpl implements Prim, DependencyValidation, StateDependencies {
   protected HashSet dependencies = new HashSet();

   public OrConnector() throws SmartFrogException, RemoteException {
   }


   public boolean isEnabled() {
      for (Iterator d = dependencies.iterator(); d.hasNext();) {
         try {
            if (((DependencyValidation) d.next()).isEnabled()) {
               return true;
            }
         } catch (RemoteException e) {
            //??
         }
      }
      return false;
   }

   public void register(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.add(d);
   }

   public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.remove(d);
   }
}
