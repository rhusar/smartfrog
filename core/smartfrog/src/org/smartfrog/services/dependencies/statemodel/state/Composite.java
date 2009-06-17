package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;
import java.util.Enumeration;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 *
 */
public class Composite extends CompoundImpl implements Compound, StateChangeNotification, RunSynchronisation {

   private String name="";
   private boolean terminating=false;
   
   public Composite() throws RemoteException {
	   super();
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {      
      //My name...
      Object name_o = sfContext().get("name");
      if (name_o!=null && name_o instanceof String) name = (String) name_o;
      else {
    	  Prim p = sfParent();
    	  if (p!=null) name = (String) sfParent().sfAttributeKeyFor(this);
    	  else name="sfConfig";
      }

      super.sfDeploy();
   }

   public synchronized void sfStart() throws RemoteException, SmartFrogException {
	   try { 
			sfResolve("sfIsOrchModel"); 
		    new Thread(new Notifier()).start();
		} catch(Exception e){/*Intentionally ok!*/}
	   super.sfStart();   
   }
   
   public synchronized void sfTerminateWith(TerminationRecord tr) {
	   terminating=true;      
	   super.sfTerminateWith(tr);
   }
   
   public synchronized void sfRun() throws SmartFrogException{
	   //System.out.println("IN: sfRun"+this);
	   
	   for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
	         Object c = e.nextElement();
	         if (c instanceof RunSynchronisation) {
	        	 ((RunSynchronisation)c).sfRun();
	         }
	      }
	   //System.out.println("OUT: sfRun"+this);
   }
   
   public String getName(){
	   return name;
   }
   
   public String getStatusAsString() throws RemoteException {
	   String status="";
	   for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
	         Object c = e.nextElement();
	         if (c instanceof StateChangeNotification) {
	        	 status+=((StateChangeNotification)c).getStatusAsString();
	         }
	   }
	   return status;
   }
   
   //child down to State, where it is handled
   public void handleStateChange() throws RemoteException {
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: Composite.hsc()"+name);   
	  for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
         Object c = e.nextElement();
         if (c instanceof StateChangeNotification) {
        	 //if (sfLog().isDebugEnabled())  sfLog().debug("GOING IN with:"+c); 
        	 ((StateChangeNotification)c).handleStateChange();
         }
      }
	  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Composite.hsc()"+name); 
   }

   /* *************************************************
	   * Update class
	   */
	   class Notifier implements Runnable {
	      public void run() {
	    	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: Composite.Notifier.run()");    
	    	  //System.out.println("++++++++++++++++++++HANDLE STATE CHANGE!!!");
	          while (!Composite.this.terminating){
	        	  try{handleStateChange();} catch (RemoteException re){throw new RuntimeException(re);}
	          }
	          if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Composite.Notifier.run()");    
	      }
	   }
}
