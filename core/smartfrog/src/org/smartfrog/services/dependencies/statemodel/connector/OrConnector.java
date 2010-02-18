package org.smartfrog.services.dependencies.statemodel.connector;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.services.dependencies.statemodel.state.StateDependencies;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.util.HashSet;
import java.util.Iterator;
import java.rmi.RemoteException;

/**
 */
 public class OrConnector extends Connector {

   public OrConnector() throws RemoteException {
	   super();
   }

   public boolean isEnabled() {
      for (Iterator d = dependencies.iterator(); d.hasNext();) {
            if (((DependencyValidation) d.next()).isEnabled()) return true;
      }
      return false;
   }
}
