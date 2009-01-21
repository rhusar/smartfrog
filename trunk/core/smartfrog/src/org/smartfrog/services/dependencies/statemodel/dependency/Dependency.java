package org.smartfrog.services.dependencies.statemodel.dependency;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.statemodel.state.StateChangeNotification;
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
public class Dependency extends PrimImpl implements Prim, DependencyValidation, StateChangeNotification {
   String transition = null;
   StateDependencies by = null;
   Prim on = null;

   boolean relevant;
   boolean enabled;
   boolean isEnabled; //the result...

   public Dependency() throws RemoteException {
   }

   public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      
      Prim p = sfParent();
      Prim pp = p.sfParent();
      System.out.println("Who am I?"+p.sfAttributeKeyFor(this));
      System.out.println("Who am I 2?"+pp.sfAttributeKeyFor(p));
      
      Object by1=null;
      
      Object transition_obj = sfResolve("transition", false);
      if (transition_obj!=null && transition_obj instanceof String){
    	  transition = (String) transition_obj;
      } 
      try {by1 = sfResolve("by"); } catch(Exception e) {/*Handle fall out next*/}
      try {on = (Prim) sfResolve("on"); } catch(Exception e) {/*Handle fall out next*/}
    	 
      Object by_pref= null;
      Object by_path= null;
      
      try {by_pref = sfResolve("sfByPrefix"); } catch(Exception e) {/*Handle fall out next*/}
      try {by_path = sfContext().get("sfByPath"); } catch(Exception e) {/*Handle fall out next*/}
      
      System.out.println("sfByPrefix"+by_pref);
      System.out.println("sfByPath"+by_path);
      
      if (on==null) throw new SmartFrogException("Unable to resolve on in"+this);
            
      if (by1!=null){
    	  if (by1 instanceof StateDependencies) { 
    		  System.out.println("**************My my my"+by1);
    		  by=(StateDependencies) by1;
    	  }
    	  else {
    		  if (by_pref!=null){
	    		  Prim by1p=null;
	    		  if (by1 instanceof Prim) {
	    			  by1p = (Prim) by1;
	    			  by1=by1p.sfResolveHere(by_pref.toString());
	    			  System.out.println("**************We have resolved"+by_pref.toString()+by1);
	    			  if (by1!=null){
	    				  if (by_path==null && by1 instanceof StateDependencies) by=(StateDependencies) by1;
	    				  else if (by_path!=null && by_path instanceof Reference){
	    					  System.out.println("**************We have resolved path"+by_path.toString());
	    					  if (by1 instanceof Prim) {
	    		    			  by1p = (Prim) by1;
	    		    			  try {by1 = by1p.sfResolve((Reference)by_path); } catch(Exception e) {/*Handle fall out next*/}
	    		    			  System.out.println("**************We have resolved:"+by1);
	    		    			  if (by1!=null && by1 instanceof StateDependencies) by=(StateDependencies) by1;
	    					  }
	    				  }
	    			  }
	    		  }
    		  }
    	  }
      } 
      if (by!=null)  sfReplaceAttribute("by", by);
      else throw new SmartFrogException("Unable to resolve by in"+this);
      
   }

   public synchronized void sfStart() throws SmartFrogException, RemoteException {
      super.sfStart();
      by.register(this);
   }

   public synchronized void sfTerminateWith(TerminationRecord tr) {
      try {
         by.deregister(this);
      } catch (Exception e) {
      }
      super.sfTerminateWith(tr);
   }

   public boolean isEnabled() {
	  System.out.println("Enablement check:"+this.sfContext()); 
	  try{Prim p = sfParent();
      Prim pp = p.sfParent();
      System.out.println("Who am I?"+p.sfAttributeKeyFor(this));
      System.out.println("Who am I 2?"+pp.sfAttributeKeyFor(p));
	  } catch (Exception e){}
      try {
         relevant = sfResolve("relevant", true, false);
         enabled = sfResolve("enabled", true, false);
         isEnabled = !relevant || (enabled && (!(on instanceof DependencyValidation) || ((DependencyValidation) on).isEnabled()));
      } catch (Exception e) {
         // ?? what to do ??
         isEnabled = false;
      }
      System.out.println("Enabled:"+isEnabled);
      return isEnabled;
   }

   public void handleStateChange() {
      //maybe cache and clear cache, or something....
   }

   public void notifyStateChange() {
      //TODO deal with this better or split StateChangeNotification interface
      //not needed on dependency
   }  

   public String getTransition(){
	   return transition;
   }
}
