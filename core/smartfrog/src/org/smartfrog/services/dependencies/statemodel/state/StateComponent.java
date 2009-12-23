/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.services.dependencies.statemodel.state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.functions.Constraint;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.ApplyReference;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import static org.smartfrog.services.dependencies.statemodel.state.Constants.T_FINALIZE;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.T_PREPARE;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.FINALIZE;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.PREPARE;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.T_ONTERMINATION;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.DO_SCRIPT;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.NAME;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.RUNNING;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.ASANDCONNECTOR;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.DPE;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.FUNCTIONCLASS;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.ISSTATECOMPONENTTRANSITION;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.THREADPOOL;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.REQUIRES_THREAD;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.LAG;

/**
 */

public abstract class StateComponent extends PrimImpl implements Prim, StateDependencies, StateChangeNotification, DependencyValidation {
   private boolean asAndConnector = false;  //document this!
     
   private HashMap<String,ComponentDescription> transitions = new HashMap<String,ComponentDescription>();
   protected HashMap<String, ComponentDescription> enabled = null;
   private Vector<DependencyValidation> dependencies = new Vector<DependencyValidation>();
  
   protected String name="";
   private Vector<ApplyReference> dpes=new Vector<ApplyReference>();
      
   private ThreadPool threadpool;
   private Object currentAction = null;
   protected boolean asyncResponse = false;
   private boolean amRunning=false;
   private ReentrantLock transitionLock = new ReentrantLock();
   
   public StateComponent() throws RemoteException {}

   public synchronized void sfTerminateWith(TerminationRecord t) {
		sfLog().debug ("StateComponent: (IN) sfTerminateWith(t)");
		
		ComponentDescription tTermination = transitions.get(T_ONTERMINATION);

		sfLog().debug ("StateComponent: Applying termination script");
        try {
            tTermination.sfResolve(DO_SCRIPT);
        } catch (SmartFrogResolutionException e) {
            sfLog().error("StateComponent: FAILED TO RUN TERMINATION SCRIPT...");
            sfLog().error(e);
        }
		
		super.sfTerminateWith(t);
        sfLog().debug ("StateComponent: "+name+" terminated");
        sfLog().debug ("StateComponent: (OUT) sfTerminateWith(t)");
   }
   
   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
       super.sfDeploy();

       Context cxt = sfContext();
       
       threadpool = (ThreadPool) sfResolve(THREADPOOL, false);
       asAndConnector = sfResolve(ASANDCONNECTOR, asAndConnector, false);
      
      //transitions and dpes
      Enumeration keys = cxt.keys();
      while (keys.hasMoreElements()){
    	  String key = (String) keys.nextElement();
    	  Object val = cxt.get(key);
    	  
    	  if (val instanceof ComponentDescription){
    		  ComponentDescription cd = (ComponentDescription) val;
    		  if (cd.sfContext().get(ISSTATECOMPONENTTRANSITION)!=null) {
                  transitions.put(key, cd);
              }
    	  } else if (val instanceof ApplyReference){
    		  ApplyReference ar = (ApplyReference) val;
    		  ComponentDescription cd = ar.getComponentDescription();
    		  String functionClass = (String) cd.sfContext().get(FUNCTIONCLASS);
    		  if (functionClass.endsWith(DPE)) {
    			  dpes.add(ar);
    		  } 
    	  }
      }
      
      //My name...
      Object name_o = cxt.get(NAME);
      if (name_o!=null && name_o instanceof String) name = (String) name_o;
      else name = (String) sfParent().sfAttributeKeyFor(this);
      
   }

   private boolean checkRunning(){
	   if (!amRunning){
		   Boolean runValue = null;
		   try { runValue = (Boolean) sfResolve(new Reference(ReferencePart.attrib(RUNNING))); }
		   catch (Exception e){/*System.out.println("Wee exception:"+e);*/}
		   if (runValue!=null) amRunning = runValue;
	   }
	   return amRunning;
   }
   
   public HashMap<String, Object> getLocalState(){
	   HashMap<String, Object> state = new HashMap<String, Object>();
	   Enumeration keys = sfContext().keys();
	   while (keys.hasMoreElements()){
		   Object key = keys.nextElement();
		   Object val = null;

           try {
               val=sfResolve(new Reference(ReferencePart.here(key)));
           } catch (SmartFrogResolutionException ignored) {
                sfLog().ignore(ignored);
           } catch (RemoteException ignored) {
               sfLog().ignore(ignored);
           }
		   state.put(key.toString(), val);
	   }
	   return state;
   }
   
   public void invokeAsynchronousStateChange(InvokeAsynchronousStateChange iasc) throws StateComponentTransitionException, RemoteException {
	   
	   if (currentAction!=null) {
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.CURRENTACTION_ONGOING);  
       }
       
	   acquireLock();
	   resetPossibleTransitions();
	   iasc.actOn(this);
       handleDPEs();
       clean();
  }
   
   @SuppressWarnings("unchecked")
   private void resetPossibleTransitions() throws StateComponentTransitionException, RemoteException {
	    sfLog().debug("IN: State("+name+").resetPossibleTransitions()");
	   
	   enabled=null;
	   if (!checkRunning()){
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.COMPONENT_NOTRUNNING);
       }
       if (!checkIsEnabled()) {
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.COMPONENT_NOTENABLED);	   
       }
	   enabled = (HashMap<String,ComponentDescription>) transitions.clone(); 
	  
	  //Remove externally disabled transitions...
      for (DependencyValidation dv : dependencies ) {
          String transition = dv.getTransition();
          if (transition!=null && !dv.isEnabled()){
        	  enabled.remove(transition);
          }
      } 
	   
	   Iterator keys = ((HashMap<String,ComponentDescription>)enabled.clone()).keySet().iterator();
	   while (keys.hasNext()){
		   String key = keys.next().toString();
		   if (key.equals(T_ONTERMINATION)) {
			   enabled.remove(key);
			   continue;  //ignore
		   }
		   
		   ComponentDescription trans = enabled.get(key);
		   boolean go=false; 
          
           try {
               go = trans.sfResolve(ConstraintConstants.GUARD, false, true);
           } catch (SmartFrogResolutionException e) {

			   enabled=null;
			   throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_RESOLVETRANSITIONGUARD);
			   
		   }
		   sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Key:"+key+":"+go);
		   
		   if (go) {
			    sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency enabled.");
		   } else {
			    sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency not enabled."); 
		      enabled.remove(key);
		   }
	   }
	   
	   if (enabled.isEmpty()) enabled=null;

	   sfLog().debug("OUT: State("+name+").resetPossibleTransitions()");
   }
   
   protected void acquireLock() throws StateComponentTransitionException {
	   sfLog().debug("IN: State("+name+").acquireLock(...)");
	   sfLog().debug("is locked?"+transitionLock.isLocked()+transitionLock.getHoldCount()+transitionLock.getQueueLength());
	   if ((currentAction!=null && currentAction!=Thread.currentThread()) || //Allow locking only by scheduled action...
			   (currentAction==null && transitionLock.isLocked() && !transitionLock.isHeldByCurrentThread())) {
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_ACQUIRELOCK);
       }
	   transitionLock.lock();
	   sfLog().debug("OUT: State("+name+").acquireLock(...)");
   }
   
   protected void cleanLock(){
	   sfLog().debug("IN: State("+name+").cleanLock(...)");
	   while (transitionLock.isHeldByCurrentThread()) {  //CHECK!
		   transitionLock.unlock();
	   }
	   sfLog().debug("is locked?"+transitionLock.isHeldByCurrentThread()+transitionLock.isLocked()+transitionLock.getHoldCount()+transitionLock.getQueueLength());
	   sfLog().debug("OUT: State("+name+").cleanLock(...)");
   }
       
   public void selectSingleAndGo() throws RemoteException, StateComponentTransitionException {
	   sfLog().debug("IN: State("+name+").selectSingleAndGo(...)");

       resetPossibleTransitions();
       sfLog().debug("WITHIN: State("+name+").selectSingleAndGo(...). Number transitions..."+enabled.size());
       if (enabled.size()==0 || enabled.size()>1) {
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_FINDSINGLEDEPENDENCYENABLED);
       }
       sfLog().debug("WITHIN: State("+name+").selectSingleAndGo(...). Single transition...");

       Iterator keys = enabled.keySet().iterator();
       String key = (String) keys.next();
       go(key);
		      
	   sfLog().debug("OUT: State("+name+").selectSingleAndGo(...)");
   }
   
   public void preparefinalize(boolean prepare, String transition) throws StateComponentTransitionException {
		 //Is there a prepare/finalizetransition script?
	     String actualName = (prepare?T_PREPARE+transition.substring(1):T_FINALIZE+transition.substring(1));
	     sfLog().debug("Prepare transition..."+actualName);
	     ComponentDescription action = (ComponentDescription) sfContext().get(actualName);
		 if (action!=null) {
			  sfLog().debug("Prepare/Finalize transition...");
              			  
			  Object qual = action.sfContext().get(ISSTATECOMPONENTTRANSITION);
			  sfLog().debug("Prepare/Finalize transition..."+qual.toString());
			  		  
              if (qual!=null && qual.toString().equals((prepare?PREPARE:FINALIZE))) {
                  try {
                      action.sfResolve(DO_SCRIPT);
                  } catch (SmartFrogResolutionException e) {
                      sfLog().error(e);
                      throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_EXECUTETRANSITIONSCRIPT, e);
                  }
              }
		 }
   }
   
   public void doPrepare(String transition) throws StateComponentTransitionException {
	    sfLog().debug("Prepare transition...");
	   preparefinalize(true, transition);
   }
   
   public void doFinalize(String transition) throws StateComponentTransitionException {
	    sfLog().debug("Finalize transition...");
	   preparefinalize(false, transition);
   }
   
   public void go(String transition) throws StateComponentTransitionException {
	   sfLog().debug("IN: State("+name+").go()");
       sfLog().debug("Key in simple transition"+transition);
       ComponentDescription trans = null;
       //try{
       trans = enabled.get(transition);
       if (trans==null){
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_GETNAMEDENABLEDTRANSITION);
       }

       try {
           trans.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.EFFECTS)));
       } catch (SmartFrogResolutionException e) {
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_RESOLVETRANSITIONEFFECTS, e);
       }
       sfLog().debug("OUT: State("+name+").go()");
   }
     
   public void clean(){
	   sfLog().debug("IN: State("+name+").clean(...)");
	   currentAction=null;
	   cleanLock();
	   asyncResponse=false;
	   scriptTimer=null;
	   sfLog().debug("OUT: State("+name+").clean(...)");
   }
   
   boolean runDPEs() throws SmartFrogResolutionException, RemoteException {
       sfLog().debug("IN: State(" + name + ").runDPEs()");
	   boolean ret=false;
	   for (ApplyReference ar: dpes){
		  if ((Boolean)sfResolve(ar)) ret=true;
	   }
       sfLog().debug("OUT: State(" + name + ").runDPEs()");
	   return ret;
   }
   
   public boolean handleDPEs() throws RemoteException, StateComponentTransitionException {
	   sfLog().debug("IN: State("+name+").handleDPEs()");
	   boolean progress=false;

       Constraint.lockUpdateContext();
       try {
           progress = runDPEs();
       } catch (SmartFrogResolutionException e) {
           sfLog().warn(e);
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_HANDLEDPES, e);
       }
       Constraint.applyUpdateContext();
			  
	   sfLog().debug("IN: State("+name+").handleDPEs() Progress"+progress);
		  
	   return progress;
   }
   
   public String getStatusAsString() throws RemoteException {
       return "";
   }
   
   Timer scriptTimer;
   public void handleStateChange() throws RemoteException, SmartFrogException { 
	   
	   sfLog().debug("IN: State("+name+").handleStateChange()");
	  	   
	   sfLog().debug("To run DPEs..."+dpes.size()+": currentAction: "+currentAction);
	
	  if (currentAction!=null || scriptTimer!=null) return;  //do nothing...

       try {
           acquireLock();
       } catch (StateComponentTransitionException ignored) {
          sfLog().ignore(ignored);
          return;  //skip for now...
       }

       if (handleDPEs()){
		  sfLog().debug("OUT: State("+name+").handleStateChange()");
		  clean();
		  return;  //dpes have proceeded...
	  }

	  resetPossibleTransitions();
	  
	  if (enabled==null) {
		  sfLog().debug("no enabled transitions...");
		  
		  sfLog().debug("OUT: State("+name+").handleStateChange() -- Nothing to do...");
		  clean();
		  return;  //nothing to do...
	  }
	   
	  //Check if simple transition...
	  if (enabled.size()==1){
		  final String key = enabled.keySet().iterator().next();
		  final ComponentDescription transition = enabled.get(key);
		  sfLog().debug("Key in simple transition: "+key);
		  
		  if (!transition.sfResolve(REQUIRES_THREAD, false, true)){
			
			   sfLog().debug("Does not require thread");
			  
			  //Is there a preparetransition script?
			  doPrepare(key);
			  
			  int lag = transition.sfResolve(LAG, 0, true);
			  if (lag>0) {
				  ActionListener taskPerformer = new ActionListener() {
				      public void actionPerformed(ActionEvent evt) {
                          try {
                              transitionScript(transition, key);
                          } catch (StateComponentTransitionException ignored) {
                            sfLog().warn(ignored); //ok to ignore, as all it means is transition is not followed...
                          }
                      }
				  };
				  (scriptTimer=new Timer(lag, taskPerformer)).start();
				  
			  } else transitionScript(transition, key);
			  return;
		  }
	  }

	  setState();
	  sfLog().debug("OUT: State("+name+").handleStateChange()");
   }     

   void transitionScript(ComponentDescription transition, String key) throws StateComponentTransitionException {
	   sfLog().debug("State("+name+").handleStateChange() -- Script call...");

       try {
           transition.sfResolve(DO_SCRIPT);
       } catch (SmartFrogResolutionException e) {
           sfLog().error(e);
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_EXECUTETRANSITIONSCRIPT, e);
       }

      //Is there a finalizetransition script?
      doFinalize(key);

      go(key);
      clean();
      sfLog().debug("OUT: State("+name+").handleStateChange() -- Script call...");
   }
   
   public void register(DependencyValidation d) {
      dependencies.add(d);
   }

   public void deregister(DependencyValidation d) {
      dependencies.remove(d);
   }

   private boolean checkIsEnabled() throws RemoteException {  //and connector on the dependencies
      for (DependencyValidation dv : dependencies) {
          if (dv.getTransition()!=null) continue;
    	  if (!dv.isEnabled()) return false;
      }
      return true;
   }

   public boolean isEnabled() throws RemoteException {
      return (!asAndConnector) || checkIsEnabled();
   }

   public String getTransition(){
	   return null;
   }
   
   public synchronized Object sfReplaceAttribute(Object name, Object value)
		throws SmartFrogRuntimeException, RemoteException {

       return super.sfReplaceAttribute(name,value);
   }
   
   public void setState() throws StateComponentTransitionException {
	  sfLog().debug("IN: StateComponent.setState()");

      sfLog().debug("Adding to queue...");
      threadpool.addToQueue((StateUpdateThread)(currentAction=new StateUpdateThread()));
      sfLog().debug("Added to Queue");
      
      cleanLock();
     
      sfLog().debug("OUT: StateComponent.setState()");
   }
   
   protected abstract boolean threadBody() throws StateComponentTransitionException, RemoteException;

   /**
    * ********************************************************
    * thread definition
    * ********************************************************
    */
   public class StateUpdateThread implements Runnable {
      public void run() {
    	  sfLog().debug("IN: StateUpdateThread.run()");
    	  try{
    	   String key=null;
    	   if (enabled.size()==1){
    		   key = enabled.keySet().iterator().next();
    		   doPrepare(key);
    	   }
    		  
    	   if (threadBody()) {
    		   StateComponent.this.clean();
    		   if (key!=null) doFinalize(key);
           } else {

        	   StateComponent.this.cleanLock();
        	   StateComponent.this.asyncResponse=true;
           }
    	  }  catch (StateComponentTransitionException ignored) {
              sfLog().warn(ignored);  //appropriate action will have already been taken
          } catch (RemoteException ignored) {
              sfLog().warn(ignored);  //appropriate action will have already been taken
          }
           sfLog().debug("OUT: StateUpdateThread.run()");
      }
   }
}
