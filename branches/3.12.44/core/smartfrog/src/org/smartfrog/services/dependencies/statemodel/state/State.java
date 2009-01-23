package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;


/*Allow explicit stateListen*/
/*stateData will be got at model checking stage*/

/**
 */
public abstract class State extends PrimImpl implements Prim, StateDependencies, StateChangeNotification, DependencyValidation {

   private HashSet dependencies = new HashSet();

   private boolean asAndConnector = false;
   private Vector stateData = new Vector();

   protected NotificationLock parentLocking = null;
   
   private HashMap<String,ComponentDescription> transitions = new HashMap<String,ComponentDescription>();
   private HashMap<String,ComponentDescription> enabled = null;
   private HashMap<String,Object> toset = null;
   private ComponentDescription statefunction = null;
   private ComponentDescription statefunction_copy = null;
   private String name="";
   private boolean notificationNeeded=false;

   public State() throws RemoteException {
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      
      Context cxt = sfContext();
      stateData = (Vector) cxt.get("sfStateData");
      asAndConnector = sfResolve("asAndConnector", asAndConnector, true);
      parentLocking = (NotificationLock)sfParent();
      
      //transitions
      Enumeration keys = cxt.keys();
      while (keys.hasMoreElements()){
    	  String key = (String) keys.nextElement();
    	  Object val = cxt.get(key);
    	  
    	  if (val instanceof ComponentDescription){
    		  ComponentDescription trans = (ComponentDescription) val;
    		  if (trans.sfContext().get("sfIsStateComponentTransition")!=null){
    			  transitions.put(key, trans);
    		  }
    	  }
      }
      
      //My name...
      name = (String) sfParent().sfAttributeKeyFor(this);

   }

   private HashMap<String, Object> getLocalOrchestrationState(int orch){
	   HashMap<String, Object> state = new HashMap<String, Object>();
	   System.out.println("GLOS");
	   Enumeration keys = sfContext().keys();
	   while (keys.hasMoreElements()){
		   String key = (String) keys.nextElement();
		   boolean isorch = stateData.contains(key);
		   if (orch==g_ORCH && !isorch || orch==g_NONORCH && isorch) continue;
		   
		   Object val = null;
		   try { 
			   val=sfResolve(new Reference(ReferencePart.here(key)));
		   } catch (Exception e){/*pah*/}
		   
		   state.put(key, val);
	   }
	   System.out.println("GLOS:"+state);
	   System.out.flush();
	   return state;
   }

   private static final int g_ALL=0;
   private static final int g_ORCH=1;
   private static final int g_NONORCH=2;
   
   public HashMap<String, Object> getLocalState() {
	   return getLocalOrchestrationState(g_ALL);
   }
   
   public HashMap<String, Object> getLocalOrchestrationState() {
	   return getLocalOrchestrationState(g_ORCH);
   }
   
   public HashMap<String, Object> getLocalNonOrchestrationState() {
	   return getLocalOrchestrationState(g_NONORCH);
   }
   
   public void invokeAsynchronousStateChange(InvokeAsynchronousStateChange iasc){
	   acquireLock();
	   resetPossibleTransitions();
	   iasc.actOn(this);
	   clean();
   }
   
   private ReentrantLock transitionLock = new ReentrantLock();

   private StateComponentTransitionException transitionException = null;
   
   private void resetPossibleTransitions() {
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").resetPossibleTransitions()");
	   //System.out.println("IN: State("+name+").resetPossibleTransitions()");
	   
	   transitionException = null;
	   
	   enabled = (HashMap<String,ComponentDescription>) transitions.clone(); 
	   
	  //Remove externally disabled transitions...
      for (Iterator d = dependencies.iterator(); d.hasNext();) {
          DependencyValidation dv = (DependencyValidation) d.next();
          String transition = dv.getTransition();
          if (transition!=null && !dv.isEnabled()){
        	  enabled.remove(transition);
          }
      } 
	   
	   Iterator keys = ((HashMap<String,ComponentDescription>)enabled.clone()).keySet().iterator();
	   while (keys.hasNext()){
		   Object key = keys.next();
		   ComponentDescription trans = (ComponentDescription) enabled.get(key);
		   boolean go=false; 
		   
		   try { go = trans.sfResolve("dependency", false, true); } catch (Exception e){
			   transitionException = new StateComponentTransitionException(StateComponentTransitionException.g_DEPENDENCYVALUEUNRESOLVABLE);
			   enabled=null;
		   }
		   
		   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Key:"+key+":"+go);
		   
		   if (go) {
			   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency enabled.");
		   } else {
			   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency not enabled."); 
		      enabled.remove(key);
		   }
	   }
	   
	   if (enabled.isEmpty()) enabled=null;

	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").resetPossibleTransitions()");
   }
   
   private void checkLock() throws StateComponentTransitionException {
	   if (!(transitionLock.isHeldByCurrentThread())) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTPERMITTED);
   }
   
   protected void acquireLock(){
	   transitionLock.lock();
	   notificationNeeded=false;
   }
   
   protected void cleanLock(){
	   while (transitionLock.isHeldByCurrentThread()) transitionLock.unlock();
   }
   
   public HashMap getPossibleTransitions() throws StateComponentTransitionException {	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").getPossibleTransitions()");
	   
	   if (transitionException!=null) throw transitionException;
	   if (enabled==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTRANSITIONS);
	   
	   parentLocking.lock();  //Disable notification... 
	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").getPossibleTransitions()");
	   
	   return enabled;
   }
	   
   public void setTransitionToCommit(String key) throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").setTransitionToCommit(...)");
	   
	   checkLock();
	   	   
	   if (enabled==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTRANSITIONS);
	   
	    ComponentDescription trans = (ComponentDescription) enabled.get(key);
	    if (trans==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOSUCHAVAILABLETRANSITION);
	    
	    Object sfo = trans.sfContext().get("statefunction");
	    if (sfo==null || !(sfo instanceof ComponentDescription)) 
	    	throw new StateComponentTransitionException(StateComponentTransitionException.g_INVALIDSTATEFUNCTION); //probably should throw...
	    
	    statefunction = (ComponentDescription) sfo;
	    statefunction_copy = (ComponentDescription) statefunction.clone();
	    toset = new HashMap<String, Object>();
	    
	    Enumeration keys = statefunction.sfContext().keys();
	    while (keys.hasMoreElements()){
	    	String sfkey = (String) keys.nextElement();
	    	Object val = null;
	    	try { val = statefunction.sfResolveHere(sfkey); } 
	    	catch (Exception e) { throw new StateComponentTransitionException(StateComponentTransitionException.g_ALLOWEDVALUEUNRESOLVABLE); }
	    	if (val instanceof ComponentDescription) continue;
	    	statefunction_copy.sfContext().remove(sfkey);
	    	toset.put(sfkey, val);
	    }
	    if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").setTransitionToCommit(...)");
   }
   
   public void setAttribute(String key, Object value) throws StateComponentTransitionException {	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").setAttribute(...)");

	   if (!(stateData.contains(key))) {
		   sfContext().put(key, value);
		   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").setAttribute(...)");
		   return;
	   }
	   
	   //Orchestration state attribute...
	   
	   checkLock();
	   
	   if (statefunction==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTRANSITIONSELECTED);
	   
	   Object allowed_value = null;
	   try { allowed_value = statefunction.sfResolve(key); } catch (Exception e){;}
	   if (allowed_value==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_ALLOWEDVALUEUNRESOLVABLE);
	   
	   //Now get whether we still have to do it...
	   try { allowed_value = statefunction_copy.sfResolve(key); } catch (Exception e){;}
	   if (allowed_value==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_VALUEALREADYSET);
	   
	   //Must be a choice to be still in the list of attributes to be set...
	   ComponentDescription choice = (ComponentDescription) allowed_value;
		   
	   //Check value is in alloweds...
	   Enumeration keys = choice.sfContext().keys();
	   boolean ok=false;
	   while (keys.hasMoreElements()){
		   Object poss_value = null;
		   try { choice.sfResolveHere(keys.nextElement()); } 
		   catch (Exception e){ throw new StateComponentTransitionException(StateComponentTransitionException.g_ALLOWEDVALUEUNRESOLVABLE);}
		   if (value.equals(poss_value)) {
			   ok=true;
			   break;
		   }
	   }
	   if (!ok) throw new StateComponentTransitionException(StateComponentTransitionException.g_INVALIDSUPPLIEDVALUE);    
	   
	   toset.put(key, value);
	   statefunction_copy.sfContext().remove(key);
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").setAttribute(...)");
	   
   }
      
   public boolean selectSingleAndGo(){	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").selectSingleAndGo(...)");

	   //System.out.println("IN: State("+name+").selectSingleAndGo(...)"+name);

	   boolean result=true;
	   try {
		   checkLock();
		   
		   HashMap trans = getPossibleTransitions();		
		   
		   //System.out.println("IN: State("+name+").selectSingleAndGo(...)"+trans.size());

		   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").selectSingleAndGo(...). Number transitions..."+trans.size());
		   if (trans.size()==0 || trans.size()>1) return false;		   
		   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").selectSingleAndGo(...). Single transition...");
		   
		   Iterator keys = trans.keySet().iterator(); 
		   if (keys.hasNext()){
				String key = (String) keys.next();
				setTransitionToCommit(key);
				go();
		   }
	   } catch (StateComponentTransitionException e) {result=false;}
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").selectSingleAndGo(...)");
	   
	   return result;
   }
   
   public void go() throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").go()");
	   
	   checkLock();
	   
	   if (statefunction_copy==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTRANSITIONSELECTED);
	   if (!statefunction_copy.sfContext().isEmpty()) throw new StateComponentTransitionException(StateComponentTransitionException.g_VALUESLEFTTOSET); 
	   
	   for (Iterator keys = toset.keySet().iterator(); keys.hasNext();) {
           Object key = keys.next();
           Object value = toset.get(key);
           if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").go(). Setting:"+key+" to:"+value+" within component:"+name);
           System.out.println("WITHIN: State("+name+").go(). Setting:"+key+" to:"+value+" within component:"+name);
           System.out.flush();
           sfContext().put(key, value);
           
           notificationNeeded=true;
           
        }
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").go()");
   }
     
   protected void clean(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").clean(...)");
	   toset=null;
	   statefunction=null;
	   statefunction_copy=null;
	   parentLocking.unlock(notificationNeeded);
	   cleanLock();
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").clean(...)");
   }
   
   public void handleStateChange() {
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").handleStateChange()");
	  
	  //System.out.println("IN: State("+name+").handleStateChange()"+name);

	  if (checkIsEnabled()) {
		  if (sfLog().isTraceEnabled()) sfLog().trace("enabled..." + sfCompleteNameSafe() + " " + true); 
   		} else {
		  if (sfLog().isTraceEnabled()) sfLog().trace("enabled..." + sfCompleteNameSafe() + " " + false);
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange()");
		  return;
	  } 
	  
	  resetPossibleTransitions(); 
	  if (enabled==null) {
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange()");
		  return;  //nothing to do...
	  }
	    
	  setState();
      	  
	  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange()");
   }     

   public void register(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.add(d);
   }

   public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.remove(d);
      notifyStateChange();
   }

   public void notifyStateChange() {
      parentLocking.notifyStateChange();
   }

   // ready in case we need it for the dependencies to link through components!
   protected boolean checkIsEnabled() {  //and connector on the dependencies
	   //System.out.println("IN: State("+name+").checkIsEnabled()"+name);

      for (Iterator d = dependencies.iterator(); d.hasNext();) {
          DependencyValidation dv = (DependencyValidation) d.next();
          if (dv.getTransition()!=null) continue;
    	  if (!dv.isEnabled()) return false;
      }
      //System.out.println("OUT:true");
      return true;
   }

   public boolean isEnabled() {
      return (!asAndConnector) || checkIsEnabled();
   }

   public String getTransition(){
	   return null;
   }
   
   protected abstract void setState();
   protected abstract boolean clearCurrentAction();

}
