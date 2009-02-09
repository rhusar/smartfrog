package org.smartfrog.services.dependencies.statemodel.state;

import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogUpdateException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.reference.ApplyReference;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 *
 */
public class Composite extends CompoundImpl implements Compound, StateChangeNotification, RunSynchronisation {

   private String name="";
	
   public Composite() throws RemoteException {
	   super();
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
	      super.sfDeploy();
	            
	      //My name...
	      Object name_o = sfContext().get("name");
	      if (name_o!=null && name_o instanceof String) name = (String) name_o;
	      else name = (String) sfParent().sfAttributeKeyFor(this);
	      
	   }
   
   public synchronized void sfRun() throws SmartFrogException{
	   System.out.println("IN: sfRun"+this);
	   
	   for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
	         Object c = e.nextElement();
	         if (c instanceof RunSynchronisation) {
	        	 ((RunSynchronisation)c).sfRun();
	         }
	      }
	   System.out.println("OUT: sfRun"+this);
   }
   
   public String getName(){
	   return name;
   }
   
   //child down to State, where it is handled
   public void handleStateChange() {
	   //System.out.println("++++++++++++++++++++HANDLE STATE CHANGE!!! COMPOSITE");
	   //System.out.println("handling state change...COMPOSITE");
      for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
         Object c = e.nextElement();
         if (c instanceof StateChangeNotification) {
        	 ((StateChangeNotification)c).handleStateChange();
         }
      }
   }

}
