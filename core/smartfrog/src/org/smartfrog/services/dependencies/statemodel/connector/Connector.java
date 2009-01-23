package org.smartfrog.services.dependencies.statemodel.connector;

import java.rmi.RemoteException;
import java.util.HashSet;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.services.dependencies.statemodel.state.StateDependencies;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

public class Connector extends PrimImpl implements Prim, DependencyValidation, StateDependencies {
       protected HashSet dependencies = new HashSet();
   
       public Connector() throws RemoteException {
    	   super();
       }
       
	   public void register(DependencyValidation d) throws SmartFrogStateLifecycleException {
		      dependencies.add(d);
	   }
	   public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException {
	      dependencies.remove(d);
	   }
	   
	   public String getTransition(){
		   return null;
	   }
	   
	   public boolean isEnabled(){ 
		   return false;
	   }
}
