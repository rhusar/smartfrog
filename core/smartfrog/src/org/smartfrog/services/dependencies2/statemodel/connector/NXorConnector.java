package org.smartfrog.services.dependencies2.statemodel.connector;

import java.rmi.RemoteException;
import java.util.Iterator;

import org.smartfrog.services.dependencies2.statemodel.dependency.DependencyValidation;

public class NXorConnector extends Connector {
	   public NXorConnector() throws RemoteException {
		   super();
	   }

	   public boolean isEnabled() {
			  boolean any_enabled = false;
		      for (Iterator d = dependencies.iterator(); d.hasNext();) {
		    	   boolean enabled = ((DependencyValidation) d.next()).isEnabled();
		    	   if (any_enabled && enabled) return false;
		    	   if (enabled) any_enabled=true;
		      }
		      return any_enabled;
	   }
}
