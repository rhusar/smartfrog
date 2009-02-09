package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.functions.Constraint;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.ApplyReference;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;


/*Allow explicit stateListen*/
/*stateData will be got at model checking stage*/

/**
 */
public abstract class StateComponent extends PrimImpl implements Prim, StateDependencies, StateChangeNotification, DependencyValidation {

   private HashSet dependencies = new HashSet();
   private boolean asAndConnector = false;
     
   private HashMap<String,ComponentDescription> transitions = new HashMap<String,ComponentDescription>();
   private HashMap<String,ComponentDescription> enabled = null;
   private String name="";
   private Vector<ApplyReference> dpes=new Vector<ApplyReference>();
      
   private ThreadPool threadpool;
   private Future<?> currentActionFuture = null;
   private boolean asyncResponse = false;
   private boolean m_running=false;
   private ReentrantLock transitionLock = new ReentrantLock();
   
   public StateComponent() throws RemoteException {
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
            
      //System.out.println("&&&&& IN STATECOMPONENT DEPLOY &&&&&");
	     
      threadpool = (ThreadPool) sfResolve("threadpool", false);
      Context cxt = sfContext();
      asAndConnector = sfResolve("asAndConnector", asAndConnector, true);
      
      //transitions, dpes, state reflection...
      Enumeration keys = cxt.keys();
      while (keys.hasMoreElements()){
    	  String key = (String) keys.nextElement();
    	  Object val = cxt.get(key);
    	  
    	  //System.out.println("REGISTRATION REGISTRATION!!!"+key+val);
    	  
    	  if (val instanceof ComponentDescription){
    		  ComponentDescription cd = (ComponentDescription) val;
    		  if (cd.sfContext().get("sfIsStateComponentTransition")!=null) transitions.put(key, cd);
    	  } else if (val instanceof ApplyReference){
    		  ApplyReference ar = (ApplyReference) val;
    		  ComponentDescription cd = ar.getComponentDescription();
    		  String functionClass = (String) cd.sfContext().get("sfFunctionClass");
    		  //System.out.println("REGISTRATION REGISTRATION!!!fc"+functionClass);
    		  if (functionClass.endsWith("DynamicPolicyEvaluation")) {
    			  dpes.add(ar);
    		  } //else System.out.println("Haven't filed...");
    	  }
      }
      
      //My name...
      Object name_o = cxt.get("name");
      //if (name_o==null) cxt.get("sfUniqueComponentID");
      
      if (name_o!=null && name_o instanceof String) name = (String) name_o;
      else name = (String) sfParent().sfAttributeKeyFor(this);
      
   }

   private boolean checkRunning(){
	   System.out.println("Am I running -IN-"+name+"?"+m_running);
	   if (!m_running){
		   Boolean runValue = null;
		   try { runValue = (Boolean) sfResolve(new Reference(ReferencePart.attrib("running"))); }
		   catch (Exception e){/*System.out.println("Wee exception:"+e);*/}
		   if (runValue!=null) m_running = runValue.booleanValue();
	   }
	   System.out.println("Am I running -OUT-"+name+"?"+m_running);
	   return m_running;
   }
   
   public HashMap<String, Object> getLocalState(){
	   HashMap<String, Object> state = new HashMap<String, Object>();
	   Enumeration keys = sfContext().keys();
	   while (keys.hasMoreElements()){
		   Object key = keys.nextElement();
		   Object val = null;
		   try { 
			   val=sfResolve(new Reference(ReferencePart.here(key)));
		   } catch (Exception e){/*pah*/} 
		   state.put(key.toString(), val);
	   }
	   return state;
   }
   
   public void invokeAsynchronousStateChange(InvokeAsynchronousStateChange iasc) throws StateComponentTransitionException {
	   //System.out.println("Hoping to acquire the lock...");
	   acquireLock();
	   //System.out.println("Hoping to acquire the lock2...");
	   resetPossibleTransitions();
	   //System.out.println("Hoping to acquire the lock3...");
	   iasc.actOn(this);
	   //System.out.println("Hoping to acquire the lock4...");
	   clean();
	   //System.out.println("Hoping to acquire the lock5...");
   }
   
   
   private void resetPossibleTransitions() throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").resetPossibleTransitions()");
	   System.out.println("IN: State("+name+").resetPossibleTransitions()");
	   
	   enabled=null;
	   
	   if (!checkRunning() || !checkIsEnabled()) throw new StateComponentTransitionException(StateComponentTransitionException.g_COMPONENTNOTENABLED);
	   
	   enabled = (HashMap<String,ComponentDescription>) transitions.clone(); 
	  
	  //Remove externally disabled transitions...
	  System.out.println("Going thru dependencies...");
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
		   
		   
		   System.out.println("transition"+trans);
		   try { go = trans.sfResolve(ConstraintConstants.GUARD, false, true); } catch (Exception e){
			   enabled=null;
			   /**Exception needs handling properly**/
			   System.out.println("We have excepted!!!");
			   throw new StateComponentTransitionException(StateComponentTransitionException.g_DEPENDENCYVALUEUNRESOLVABLE);
			   
		   }
		   
		   System.out.println("transitionresult"+go);
		   
		   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Key:"+key+":"+go);
		   
		   if (go) {
			   System.out.println("Component: "+name+", enabled transition: "+key);
			   
			   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency enabled.");
		   } else {
			   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency not enabled."); 
		      enabled.remove(key);
		   }
	   }
	   
	   if (enabled.isEmpty()) enabled=null;

	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").resetPossibleTransitions()");
   }
   
   protected void acquireLock(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").aquireLock(...)");
	   transitionLock.lock();
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").aquireLock(...)");
	   
   }
   
   protected void cleanLock(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").cleanLock(...)");
	   while (transitionLock.isHeldByCurrentThread()) {  //CHECK!
		   transitionLock.unlock();
	   }
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").cleanLock(...)");
   }
       
   public boolean selectSingleAndGo(){	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").selectSingleAndGo(...)");

	   //System.out.println("IN: State("+name+").selectSingleAndGo(...)"+name);

	   boolean result=true;
	   try {
		 
		   resetPossibleTransitions(); 		
		   
		   //System.out.println("IN: State("+name+").selectSingleAndGo(...)"+trans.size());

		   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").selectSingleAndGo(...). Number transitions..."+enabled.size());
		   if (enabled.size()==0 || enabled.size()>1) return false;		   
		   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").selectSingleAndGo(...). Single transition...");
		   
		   Iterator keys = enabled.keySet().iterator(); 
		   if (keys.hasNext()){
				String key = (String) keys.next();
				go(key);
		   }
	   } catch (StateComponentTransitionException e) {result=false;}
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").selectSingleAndGo(...)");
	   
	   return result;
   }
   
   public void go(String transition) throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").go()");
	   
	   ComponentDescription trans = (ComponentDescription) enabled.get(transition);
	   try {
		   trans.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.EFFECTS)));
	   } catch (Exception e){throw new StateComponentTransitionException("Unable to apply effects in transition: "+trans, StateComponentTransitionException.g_UNABLETOAPPLYEFFECTS);}
	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").go()");
   }
   
   boolean runDPEs(){
	   boolean ret=false;
	   for (int i=0;i<dpes.size();i++){
		   ApplyReference ar = dpes.get(i);
		   try {
			   Boolean b = (Boolean) sfResolve(ar);
			   if (b.booleanValue()) ret=true;
		   }catch(Exception e){/*Intentionally do nothing*/}
	   }
	   return ret;
   }
     
   public void clean(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").clean(...)");
	   currentActionFuture=null;
	   cleanLock();
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").clean(...)");
   }
   
   public void handleStateChange() { 
	   
	  //System.out.println("++++++++++++++++++++HANDLE STATE CHANGE!!! COMPONENT:"+name);
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").handleStateChange()");
	  
	  /*
	   *This needs re-implementing. Check whether they applicable. If they are -- new thread. 
	   
	   System.out.println("To run DPEs..."+dpes.size());
	  
	  boolean progress=false;
	  
	  synchronized(CoreSolver.getInstance()){
		  Constraint.lockUpdateContext();
		  progress = runDPEs();	  
		  Constraint.applyUpdateContext();
	  }
	  System.out.println("^^^^^^^^^^^^^^^^^^^Progress"+progress);System.out.flush();
	  
	  if (progress) {
		  System.out.println("Progress made");
		  threadpool.runIdle();
		  System.out.println("Other side!");System.out.flush();
		  return; //skip now, as this will come round...
	  }*/
	  try {
	  resetPossibleTransitions();
	  } catch (Exception e){
		
		  enabled=null;
	  }
	  
	  if (enabled==null) {
		  System.out.println("no enabled transitions...");
		  
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange() -- Nothing to do...");
		  return;  //nothing to do...
	  }
	   
	  try {
		  setState();
	  } catch (StateComponentTransitionException stce) {/*Hardly acceptable handling*/}
      	  
	  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange()");
   }     

   public void register(DependencyValidation d) throws SmartFrogStateLifecycleException {
	  System.out.println("Dependency Registration. Component: "+name+", d: "+d.toString());
      dependencies.add(d);
   }

   public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.remove(d);
   }

   private boolean checkIsEnabled() {  //and connector on the dependencies
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
   
   public synchronized Object sfReplaceAttribute(Object name, Object value)
		throws SmartFrogRuntimeException, RemoteException {
  
	   //System.out.println("*****************************************************************************************Replacing Attribute..."+name+":"+value);
	  
	   Object result = super.sfReplaceAttribute(name,value);
	   
	   if (!Constraint.isUpdateContextLocked() && threadpool!=null) threadpool.runIdle();
	   return result;
   }
   
   private Future<?> clearCurrentAction(){
	   if (currentActionFuture ==null) return null;
	   return (currentActionFuture = threadpool.removeFromQueue(currentActionFuture)); 
   }
   
   public void setState() throws StateComponentTransitionException {
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: StateComponent.setState()");
      
	  if (clearCurrentAction()!=null) return; //Not appropriate to allow further transition at this time...
	 
      currentActionFuture= threadpool.addToQueue(new StateUpdateThread());
     
      if (sfLog().isDebugEnabled())  sfLog().debug("OUT: StateComponent.setState()");
   }
   
   protected abstract boolean threadBody() throws StateComponentTransitionException;

   /**
    * ********************************************************
    * thread definition
    * ********************************************************
    */
   public class StateUpdateThread implements Runnable {
      public void run() {
    	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: StateUpdateThread.run()");
    	  acquireLock();
    	  try{
    	   if (threadBody()) {
    		   StateComponent.this.asyncResponse=false;
    		   StateComponent.this.clean();
           } else {
        	   StateComponent.this.asyncResponse=true;
           }
    	  }  catch (StateComponentTransitionException stce) {/*Hardly acceptable handling*/}
          if (sfLog().isDebugEnabled())  sfLog().debug("OUT: StateUpdateThread.run()");
      }
   }
   
}
