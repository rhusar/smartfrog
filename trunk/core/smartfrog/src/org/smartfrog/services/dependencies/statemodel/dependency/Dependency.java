package org.smartfrog.services.dependencies.statemodel.dependency;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.statemodel.state.RunSynchronisation;
import org.smartfrog.services.dependencies.statemodel.state.StateDependencies;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;


/**
 * Class that implements the dependency between two StateDependencies-implementing objects.
 * <p/>
 * On creation will add the dependency, or termination will remove it.
 */
public class Dependency extends PrimImpl implements Prim, DependencyValidation, RunSynchronisation {
   String transition = null;
   StateDependencies by = null;
   DependencyValidation on = null;
   String name="";

   public Dependency() throws RemoteException {
   }

   public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      
      //System.out.println("&&&&& IN DEP DEPLOY &&&&&");
	     
      Object transition_obj = sfResolve("transition", false);
      if (transition_obj!=null && transition_obj instanceof String){
    	  transition = (String) transition_obj;
      } 
      
      name = this.sfParent().sfAttributeKeyFor(this).toString();
      
   }

   public synchronized void sfStart() throws SmartFrogException, RemoteException {
	   //System.out.println("&&&&& IN DEP START &&&&&"+by);
	  
	   super.sfStart();   
	   
	   //SHOULD BE A CHECK IN HERE ON RUNNING...
	   Boolean running = null;
	   try { running = (Boolean) sfResolve(new Reference(ReferencePart.attrib("running"))); }
	   catch (Exception e){}
	   
	   if (running!=null && running.booleanValue()) sfRun();  //pre-empt...
   }
   
   public synchronized void sfRun() throws SmartFrogException{
	  //System.out.println("IN: sfRun: "+name+" registering...");
	   try {by = (StateDependencies) sfResolve("by"); } catch(Exception e) {/*Elaborate*/}
	   //System.out.println("1");
	   try {on = (DependencyValidation) sfResolve("on"); } catch(Exception e) {/*Elaborate*/}
	   //System.out.println("2:"+by);
	   by.register(this);
	   //System.out.println("OUT: sfRun: "+name+" registering...");
   }

   public synchronized void sfTerminateWith(TerminationRecord tr) {
      try {
         by.deregister(this);
      } catch (Exception e) {
      }
      super.sfTerminateWith(tr);
   }

   public String toString(){
	   return name;
   }
   
   public boolean isEnabled() {
	   boolean relevant;
	   boolean enabled;
	   boolean isEnabled=true;
	   //System.out.println("Dependency Enablement check:"+ name); 
      
	  try {
         relevant = sfResolve("relevant", true, false);
         
         if (relevant){
        	 //System.out.println("I am relevant!");
        	 boolean onEnabled;
        	 enabled = sfResolve("enabled", false, false);
             
        	 //System.out.println("I am enabled?"+enabled);
        	 
        	 if (on!=null) onEnabled = ((!(on instanceof DependencyValidation)) || ((DependencyValidation) on).isEnabled());
        	 else onEnabled=true;
        	 
        	//System.out.println("I am onEnabled?"+onEnabled);
         
        	 isEnabled = (enabled && onEnabled);
         }
         
      } catch (Exception e) {/*Elaborate*/ isEnabled=false;}

      //System.out.println("Enabled:"+isEnabled);
      return isEnabled;
   }

   public String getTransition(){
	   return transition;
   }
}
