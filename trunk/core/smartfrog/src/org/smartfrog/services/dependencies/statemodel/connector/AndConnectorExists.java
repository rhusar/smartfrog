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
 public class AndConnectorExists extends Connector {

   public AndConnectorExists() throws RemoteException {
	   super();
   }

   public boolean isEnabled() {
	  boolean exists=false;
	  System.out.println("InACE1");
      for (Iterator d = dependencies.iterator(); d.hasNext();){
    	  System.out.println("InACE2");
            if (!((DependencyValidation) d.next()).isEnabled()) return false;
            System.out.println("InACE3");
            exists=true;
      }
      System.out.println("InACE4");
      return exists;
   }
}
