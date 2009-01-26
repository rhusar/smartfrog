package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
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
   private Vector stateData = new Vector();
   
   protected NotificationLock parentLocking = null;
   
   private HashMap<String,ComponentDescription> transitions = new HashMap<String,ComponentDescription>();
   private HashMap<String,ComponentDescription> enabled = null;
   private HashMap<String,Object> toset = null;
   private ComponentDescription statefunction = null;
   protected String name="";
   private boolean notificationNeeded=false;
   private boolean haveLocked=false;  /*This needs tidying*/
   private Vector<ApplyReference> dpes=new Vector<ApplyReference>();
   protected int sfIndex = -1;
   
   public StateComponent() throws RemoteException {
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      
      Context cxt = sfContext();
      stateData = (Vector) cxt.get("sfStateData");
      if (stateData==null) stateData = new Vector();
      asAndConnector = sfResolve("asAndConnector", asAndConnector, true);
      parentLocking = (NotificationLock)sfParent();
      
      //transitions and dpes
      Enumeration keys = cxt.keys();
      while (keys.hasMoreElements()){
    	  String key = (String) keys.nextElement();
    	  Object val = cxt.get(key);
    	  
    	  System.out.println("REGISTRATION REGISTRATION!!!"+key+val);
    	  
    	  if (val instanceof ComponentDescription){
    		  ComponentDescription cd = (ComponentDescription) val;
    		  if (cd.sfContext().get("sfIsStateComponentTransition")!=null) transitions.put(key, cd);
    	  } else if (val instanceof ApplyReference){
    		  ApplyReference ar = (ApplyReference) val;
    		  ComponentDescription cd = ar.getComponentDescription();
    		  String functionClass = (String) cd.sfContext().get("sfFunctionClass");
    		  System.out.println("REGISTRATION REGISTRATION!!!fc"+functionClass);
    		  if (functionClass.endsWith("DynamicPolicyEvaluation")) {
    			  dpes.add(ar);
    		  } else System.out.println("Haven't filed...");
    	  }
      }
      
      //My name...
      Object name_o = cxt.get("name");
      if (name_o==null) cxt.get("sfUniqueComponentID");
      
      if (name_o!=null && name_o instanceof String) name = (String) name_o;
      else name = (String) sfParent.sfAttributeKeyFor(this);
      
      //My index
      Integer indexI = null;
      
      try { indexI = (Integer) sfResolve(new Reference(ReferencePart.attrib("sfIndex"))); } catch (Exception e){/*Just ignore if not there*/}
      if (indexI!=null)  sfIndex= ((Integer)indexI).intValue();    
   }

   private HashMap<String, Object> getLocalOrchestrationState(int orch){
	   HashMap<String, Object> state = new HashMap<String, Object>();
	   Enumeration keys = sfContext().keys();
	   while (keys.hasMoreElements()){
		   Object key = keys.nextElement();
		   boolean isorch = stateData.contains(key);
		   if (orch==g_ORCH && !isorch || orch==g_NONORCH && isorch) continue;
		   
		   Object val = null;
		   try { 
			   val=sfResolve(new Reference(ReferencePart.here(key)));
		   } catch (Exception e){/*pah*/}
		   
		   state.put(key.toString(), val);
	   }
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
	   System.out.println("Hoping to acquire the lock...");
	   acquireLock();
	   System.out.println("Hoping to acquire the lock2...");
	   resetPossibleTransitions();
	   System.out.println("Hoping to acquire the lock3...");
	   iasc.actOn(this);
	   System.out.println("Hoping to acquire the lock4...");
	   clean();
	   System.out.println("Hoping to acquire the lock5...");
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
		   
		   
		   System.out.println("transition"+trans);
		   try { go = trans.sfResolve("sfGuard", false, true); } catch (Exception e){
			   /**Exception needs handling properly**/
			   System.out.println("We have excepted!!!");
			   transitionException = new StateComponentTransitionException(StateComponentTransitionException.g_DEPENDENCYVALUEUNRESOLVABLE);
			   enabled=null;
		   }
		   
		   System.out.println("transitionresult"+go);
		   
		   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Key:"+key+":"+go);
		   
		   if (go) {
			   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency enabled.");
		   } else {
			   if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").resetPossibleTransitions(). Dependency not enabled."); 
			   System.out.println("enabled+"+enabled+"+key+"+key);
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
	   parentLocking.lock();
	   notificationNeeded=false;
   }
   
   protected void cleanLock(){
	   while (transitionLock.isHeldByCurrentThread()) {
		   parentLocking.unlock(notificationNeeded);
		   transitionLock.unlock();
	   }
   }
   
   public HashMap getPossibleTransitions() throws StateComponentTransitionException {	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").getPossibleTransitions()");
	   
	   if (transitionException!=null) throw transitionException;
	   if (enabled==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTRANSITIONS);
	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").getPossibleTransitions()");
	   
	   return enabled;
   }
	   
   public void setTransitionToCommit(String key) throws StateComponentTransitionException {
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").setTransitionToCommit(...)");
	   
	   checkLock();
	   	   
	   sfLog().debug("1");
	   
	   if (enabled==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTRANSITIONS);
	   
	   sfLog().debug("2");
	   
	    ComponentDescription trans = (ComponentDescription) enabled.get(key);
	    
	    sfLog().debug("3");
	    
	    if (trans==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOSUCHAVAILABLETRANSITION);
	    
	    sfLog().debug("4");
	    
	    Object sfo = trans.sfContext().get("sfEffects");
	    if (sfo==null || !(sfo instanceof ComponentDescription)) 
	    	throw new StateComponentTransitionException(StateComponentTransitionException.g_INVALIDSTATEFUNCTION); //probably should throw...
	    
	    sfLog().debug("5");
	    
	    statefunction = (ComponentDescription) sfo;
	    toset = new HashMap<String, Object>();
	    
	    sfLog().debug("6");
	    
	    Enumeration keys = statefunction.sfContext().keys();
	    while (keys.hasMoreElements()){
	    	
	    	String sfkey = (String) keys.nextElement();
	    	Object val = null;
	    	try { val = statefunction.sfResolveHere(sfkey); } 
	    	catch (Exception e) { throw new StateComponentTransitionException(StateComponentTransitionException.g_ALLOWEDVALUEUNRESOLVABLE); }
	    	
	    	
	    	if (val instanceof ComponentDescription) continue;
	    	toset.put(sfkey, val);
	    }
	    
	    sfLog().debug("7");
	    
	    if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").setTransitionToCommit(...)");
   }
   
   public void setAttribute(String key, Object value) throws StateComponentTransitionException {	   
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").setAttribute(...)");

	   if (!(stateData.contains(key))) {
		   try {sfReplaceAttribute(key, value);} catch (Exception e){/*Shouldn't happen*/}
		   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").setAttribute(...)");
		   return;
	   }
	   
	   //Orchestration state attribute...
	   
	   checkLock();
	   
	   if (statefunction==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTRANSITIONSELECTED);
	   
	   if (toset.get(key)!=null) throw new StateComponentTransitionException(StateComponentTransitionException.g_VALUEALREADYSET); 
	   
	   Object allowed_value = null;
	   try { allowed_value = statefunction.sfResolve(key); } catch (Exception e){;}
	   if (allowed_value==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_ALLOWEDVALUEUNRESOLVABLE);
	    
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
	   
	   if (toset==null) throw new StateComponentTransitionException(StateComponentTransitionException.g_NOTRANSITIONSELECTED);
	    
	   for (Iterator keys = toset.keySet().iterator(); keys.hasNext();) {
           Object key = keys.next();
           Object value = toset.get(key);
           if (sfLog().isDebugEnabled())  sfLog().debug("WITHIN: State("+name+").go(). Setting:"+key+" to:"+value+" within component:"+name);
           try {sfReplaceAttribute(key, value);} catch (Exception e){/*Shouldn't happen*/}
           
           notificationNeeded=true;
           
        }
	   
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
     
   protected void clean(){
	   if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").clean(...)");
	   toset=null;
	   statefunction=null;
	   cleanLock();
	   if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").clean(...)");
   }
   
   public void handleStateChange() { 
	   
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: State("+name+").handleStateChange()");
	  
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
		  notifyStateChange();
		  System.out.println("Other side!");System.out.flush();
		  return; //skip now, as this will come round...
	  }
	  
	  if (checkIsEnabled()) {
		  if (sfLog().isTraceEnabled()) sfLog().trace("enabled..." + sfCompleteNameSafe() + " " + true); 
   		} else {
		  if (sfLog().isTraceEnabled()) sfLog().trace("enabled..." + sfCompleteNameSafe() + " " + false);
		  if (sfLog().isDebugEnabled())  sfLog().debug("OUT: State("+name+").handleStateChange()");
		  return;
	  } 
	  
	  resetPossibleTransitions(); 
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
      dependencies.add(d);
   }

   public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.remove(d);
      notifyStateChange();
   }

   public void notifyStateChange() {
	  if (parentLocking!=null) parentLocking.notifyStateChange();
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
   
   protected abstract void setState() throws StateComponentTransitionException;
   protected abstract boolean clearCurrentAction();

   public synchronized Object sfReplaceAttribute(Object name, Object value)
   		throws SmartFrogRuntimeException, RemoteException {
	   
	    System.out.println("*****************************************************************************************Replacing Attribute..."+name+":"+value);
	   
	    Object result = super.sfReplaceAttribute(name,value);
	    
	    if (!Constraint.isUpdateContextLocked()) notifyStateChange();
	    return result;
   }
   
}
