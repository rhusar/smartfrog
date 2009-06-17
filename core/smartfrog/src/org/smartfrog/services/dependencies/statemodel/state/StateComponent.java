package org.smartfrog.services.dependencies.statemodel.state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.functions.Constraint;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
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
   protected HashMap<String,ComponentDescription> enabled = null;
   protected String name="";
   private Vector<ApplyReference> dpes=new Vector<ApplyReference>();
      
   private ThreadPool threadpool;
   private Object currentAction = null;
   protected boolean asyncResponse = false;
   private boolean m_running=false;
   private ReentrantLock transitionLock = new ReentrantLock();
   
   public StateComponent() throws RemoteException {
   }

   public synchronized void sfTerminateWith(TerminationRecord t) {
		if (sfLog().isDebugEnabled()) sfLog().debug ("StateComponent: (IN) sfTerminateWith(t)");
		
		ComponentDescription tTermination = transitions.get("tOnTermination");
		try{
			if (sfLog().isDebugEnabled()) sfLog().debug ("StateComponent: Applying termination script");	
			tTermination.sfResolve("doScript");}
		catch(Exception e){/*Ok for now*/}
		
		super.sfTerminateWith(t);
       if (sfLog().isDebugEnabled()) sfLog().debug ("StateComponent: "+name+" terminated");
       if (sfLog().isDebugEnabled()) sfLog().debug ("StateComponent: (OUT) sfTerminateWith(t)");
   }
   
   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
            
      //System.out.println("&&&&& IN STATECOMPONENT DEPLOY &&&&&");
	     
      threadpool = (ThreadPool) sfResolve("threadpool", false);
      Context cxt = sfContext();
      asAndConnector = sfResolve("asAndConnector", asAndConnector, false);
      
      //transitions, dpes, state reflection...
      Enumeration keys = cxt.keys();
      while (keys.hasMoreElements()){
    	  String key = (String) keys.nextElement();
    	  Object val = cxt.get(key);
    	  
    	  //System.out.println("REGISTRATION REGISTRATION!!!"+key+val);
    	  
    	  if (val instanceof ComponentDescription){
    		  ComponentDescription cd = (ComponentDescription) val;
    		  if (cd.sfContext().get("sfIsStateComponentTransition")==SFNull.get()) transitions.put(key, cd);
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
	   //System.out.println("Am I running -IN-"+name+"?"+m_running);
	   if (!m_running){
		   Boolean runValue = null;
		   try { runValue = (Boolean) sfResolve(new Reference(ReferencePart.attrib("running"))); }
		   catch (Exception e){/*System.out.println("Wee exception:"+e);*/}
		   if (runValue!=null) m_running = runValue.booleanValue();
	   }
	   //System.out.println("Am I running -OUT-"+name+"?"+m_running);
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
	   
	   if (currentAction!=null) return;  //temporary  NEED TO EXCEPT!!!
	   
	   //System.out.println("Hoping to acquire the lock...");
	   if (!acquireLock()) return; //temporary  NEED TO EXCEPT!!!
	   //System.out.println("Hoping to acquire the lock2...");
	   resetPossibleTransitions();
	   //System.out.println("Hoping to acquire the lock3...");
	   iasc.actOn(this);
	   //System.out.println("Hoping to acquire the lock4...");
	   handleDPEs();
	   clean();
	   //System.out.println("Hoping to acquire the lock5...");
   }
   
   
   private void resetPossibleTransitions() throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").resetPossibleTransitions()");
	   //System.out.println("IN: State("+name+").resetPossibleTransitions()");
	   
	   enabled=null;
	   
	   if (!checkRunning() || !checkIsEnabled()) throw new StateComponentTransitionException(StateComponentTransitionException.g_COMPONENTNOTENABLED);
	   
	   enabled = (HashMap<String,ComponentDescription>) transitions.clone(); 
	  
	  //Remove externally disabled transitions...
	  //System.out.println("Going thru dependencies...");
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
		   if (key.toString().equals("tOnTermination")) {
			   enabled.remove(key);
			   continue;  //ignore
		   }
		   
		   ComponentDescription trans = (ComponentDescription) enabled.get(key);
		   boolean go=false; 
		   
		   
		   //System.out.println("transition"+trans);
		   try { go = trans.sfResolve(ConstraintConstants.GUARD, false, true); } catch (Exception e){
			   enabled=null;
			   /**Exception needs handling properly**/
			   System.out.println("We have excepted!!!");
			   throw new StateComponentTransitionException(StateComponentTransitionException.g_DEPENDENCYVALUEUNRESOLVABLE);
			   
		   }
		   
		   //System.out.println("transitionresult"+go);
		   
		   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Key:"+key+":"+go);
		   
		   if (go) {
			   //System.out.println("Component: "+name+", enabled transition: "+key);
			   
			   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency enabled.");
		   } else {
			   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency not enabled."); 
		      enabled.remove(key);
		   }
	   }
	   
	   if (enabled.isEmpty()) enabled=null;

	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").resetPossibleTransitions()");
   }
   
   protected boolean acquireLock(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").acquireLock(...)");
	   if (sfLog().isDebugEnabled())  sfLog().debug("is locked?"+transitionLock.isLocked()+transitionLock.getHoldCount()+transitionLock.getQueueLength());
	   if ((currentAction!=null && currentAction!=Thread.currentThread()) || //Allow locking only by scheduled action...
			   (currentAction==null && transitionLock.isLocked() && !transitionLock.isHeldByCurrentThread())) return false;  //or by current owner thread
	   transitionLock.lock();
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").acquireLock(...)");
	   return true;
   }
   
   protected void cleanLock(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").cleanLock(...)");
	   while (transitionLock.isHeldByCurrentThread()) {  //CHECK!
		   transitionLock.unlock();
	   }
	   if (sfLog().isDebugEnabled())  sfLog().debug("is locked?"+transitionLock.isHeldByCurrentThread()+transitionLock.isLocked()+transitionLock.getHoldCount()+transitionLock.getQueueLength());
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
   
   public void preparefinalize(boolean prepare, String transition) throws StateComponentTransitionException {
		 //Is there a prepare/finalizetransition script?
	      String actualName = (prepare?"tPrepare"+transition.substring(1):"tFinalize"+transition.substring(1));
	      if (sfLog().isDebugEnabled())  sfLog().debug("Prepare transition..."+actualName);
	      ComponentDescription action = (ComponentDescription) sfContext().get(actualName);
		  if (action!=null) {
			  if (sfLog().isDebugEnabled())  sfLog().debug("Prepare/Finalize transition...");
              			  
			  Object qual = action.sfContext().get("sfIsStateComponentTransition");
			  if (sfLog().isDebugEnabled())  sfLog().debug("Prepare/Finalize transition..."+qual.toString());
			  
			  try{if (qual!=null && qual.toString().equals((prepare?"prepare":"finalize"))) action.sfResolve("doScript");}
			  catch(Exception e){/*Ok for now*/if (sfLog().isDebugEnabled())  sfLog().debug("EXCEPTION: Prepare/Finalize transition..."+e.getMessage());
			  }
		  }
   }
   
   public void doPrepare(String transition) throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("Prepare transition...");
	   preparefinalize(true, transition);
   }
   
   public void doFinalize(String transition) throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("Finalize transition...");
	   preparefinalize(false, transition);
   }
   
   public void go(String transition) throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").go()");
	   
	   try {
		   if (sfLog().isDebugEnabled())  sfLog().debug("Key in simple transition"+transition);
		   ComponentDescription trans = null;
		   try{trans = (ComponentDescription) enabled.get(transition);} catch (Throwable e){sfLog().debug("WHAT THE JC? "+e.getMessage());}
		   trans.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.EFFECTS)));
	   } catch (Exception e){throw new StateComponentTransitionException("Unable to apply effects in transition: "+transition, StateComponentTransitionException.g_UNABLETOAPPLYEFFECTS);}
	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").go()");
   }
   
   
     
   public void clean(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").clean(...)");
	   currentAction=null;
	   cleanLock();
	   asyncResponse=false;
	   scriptTimer=null;
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").clean(...)");
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
   
   public boolean handleDPEs(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").handleDPEs()");
	   boolean progress=false;
		  
		  //synchronized(CoreSolver.getInstance()){
			  Constraint.lockUpdateContext();
			  progress = runDPEs();	  
			  Constraint.applyUpdateContext();
		  //}
		  
		  if (sfLog().isDebugEnabled())  sfLog().debug("^^^^^^^^^^^^^^^^^^^Progress"+progress);
		  
		  return progress;
   }
   
   public String getStatusAsString() throws RemoteException {return "";}
   
   Timer scriptTimer;
   public void handleStateChange() throws RemoteException { 
	   
	  
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").handleStateChange()");
	  	   
	  if (sfLog().isDebugEnabled())  sfLog().debug("To run DPEs..."+dpes.size()+": currentAction: "+currentAction);
	
	  if (currentAction!=null || scriptTimer!=null) return;  //temporary
	  
	  if (!acquireLock()) return; //need to except...
	  
	  if (handleDPEs()){
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange()");
		  clean();
		  return;
	  }
	  
	  try {
	  resetPossibleTransitions();
	  } catch (Exception e){
		  enabled=null;
	  }
	  
	  if (enabled==null) {
		  if (sfLog().isDebugEnabled())  sfLog().debug("no enabled transitions...");
		  
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange() -- Nothing to do...");
		  clean();
		  return;  //nothing to do...
	  }
	   
	  //Check if simple transition...
	  try {
	  if (enabled.size()==1){
		  final String key = enabled.keySet().iterator().next();
		  final ComponentDescription transition = (ComponentDescription) enabled.get(key);
		  if (sfLog().isDebugEnabled())  sfLog().debug("Key in simple transition: "+key);
		  
		  if (!transition.sfResolve("requiresThread", false, true)){
			
			  if (sfLog().isDebugEnabled())  sfLog().debug("Does not require thread");
			  
			  //Is there a preparetransition script?
			  doPrepare(key);
			  
			  int lag = transition.sfResolve("lag", 0, true);
			  if (lag>0) {
				  ActionListener taskPerformer = new ActionListener() {
				      public void actionPerformed(ActionEvent evt) {
				          transitionScript(transition, key);
				      }
				  };
				  (scriptTimer=new Timer(lag, taskPerformer)).start();
				  
			  } else transitionScript(transition, key);
			  return;
		  }
	  }
	  } catch (SmartFrogException e){if (sfLog().isDebugEnabled())  sfLog().debug("EXCEPTION: in applying simple transition"+e.getMessage());}
	  
	  
	  try {
		  setState();
	  } catch (StateComponentTransitionException stce) {/*Hardly acceptable handling*/}
      	  
	  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange()");
   }     

   void transitionScript(ComponentDescription transition, String key)  {
	   //Need to handle properly...
	   try {
	   	  if (sfLog().isDebugEnabled())  sfLog().debug("State("+name+").handleStateChange() -- Script call...");
		  try{transition.sfResolve("doScript");} catch (Exception e){if (sfLog().isDebugEnabled())  sfLog().debug("EXCEPTION: in applying simple script "+e.getMessage());}//Apply script...
		  
		  //Is there a finalizetransition script?
		  doFinalize(key);
		  
		  go(key);
		  clean();
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange() -- Script call...");
	   } catch (SmartFrogException e){if (sfLog().isDebugEnabled())  sfLog().debug("EXCEPTION: in applying simple transition"+e.getMessage());}
   }
   
   public void register(DependencyValidation d) throws SmartFrogStateLifecycleException {
	  //System.out.println("Dependency Registration. Component: "+name+", d: "+d.toString());
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
  
	   Object result = super.sfReplaceAttribute(name,value);
	   
	   return result;
   }
   
   private Object clearCurrentAction(){
	   return currentAction;
	   //if (currentActionFuture ==null) return null;
	   //return (currentActionFuture = threadpool.removeFromQueue(currentActionFuture)); 
   }
   
   public void setState() throws StateComponentTransitionException {
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: StateComponent.setState()");
      
	  //handled above: if (clearCurrentAction()!=null) return; //Not appropriate to allow further transition at this time...
	 
	  if (sfLog().isDebugEnabled())  sfLog().debug("Adding to queue...");
	 
	  
      //currentActionFuture= threadpool.addToQueue(new StateUpdateThread());
      
      threadpool.addToQueue((StateUpdateThread)(currentAction=new StateUpdateThread()));
      
      if (sfLog().isDebugEnabled())  sfLog().debug("IN: StateComponent.setState() Added to Queue");
      
      cleanLock();
      
      System.out.println("Adding to queue..."+currentAction);
     
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
        	   //Should this clean now?
        	   StateComponent.this.cleanLock();
        	   StateComponent.this.asyncResponse=true;
           }
    	  }  catch (StateComponentTransitionException stce) {/*Hardly acceptable handling*/}
          if (sfLog().isDebugEnabled())  sfLog().debug("OUT: StateUpdateThread.run()");
      }
   }
}
