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

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.*;
import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.ReentrantLock;

/**
 */

public abstract class StateComponent extends PrimImpl implements Prim, StateDependencies,
        StateChangeNotification, DependencyValidation, StateComponentManagement {
   private boolean asAndConnector = false;  //document this!
     
   private HashMap<String,ComponentDescription> transitions = new HashMap<String,ComponentDescription>();
   protected HashMap<String, ComponentDescription> enabled = null;
   private Vector<DependencyValidation> dependencies = new Vector<DependencyValidation>();
  
   protected String name="";
   private Vector<ApplyReference> dpes=new Vector<ApplyReference>();
      
   private ThreadPool threadpool;
   private Object currentAction = null;
   protected boolean asyncResponse = false;  //LOG ISSUE AND REMOVE...
   private boolean amRunning=false;
   private ReentrantLock transitionLock = new ReentrantLock();
   private String fullName;
   private String fullNamePath;

   private Prim eventLog;  //this needs proper synchronisation...

   protected boolean sfStarted=false;
   
   public StateComponent() throws RemoteException {}



    @Override
   public synchronized void sfTerminateWith(TerminationRecord t) {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
		
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
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }

    @Override
   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        super.sfDeploy();

       Context cxt = sfContext();
       
       threadpool = (ThreadPool) sfResolve(THREADPOOL, false);
       asAndConnector = sfResolve(ASANDCONNECTOR, asAndConnector, false);
       eventLog = sfResolve(EVENTLOG, (Prim)null, false);

      
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
      name = attributeName(this);
      sfReplaceAttribute("name", name);

      fullName = sfCompleteName().toString();
      int idx = fullName.indexOf("rootProcess:");
      if (idx!=-1) fullName = fullName.substring("rootProcess:".length()+idx);

      fullNamePath = SERVICERESOURCE.substring(1).replaceAll(PATHDELIM, SFDELIM)+fullName;
      sfReplaceAttribute(FULLNAMEPATH, fullNamePath = SERVICERESOURCE.substring(1).replaceAll(PATHDELIM, SFDELIM) + fullName);
      if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
      
   }

   protected String attributeName(Prim component) throws RemoteException {
       return component.sfCompleteName().toString();
    }

   private boolean checkRunning(){
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   if (!amRunning){
		   Boolean runValue = null;
		   try { runValue = (Boolean) sfResolve(new Reference(ReferencePart.attrib(RUNNING))); }
		   catch (Exception e){/*System.out.println("Wee exception:"+e);*/}
		   if (runValue!=null) amRunning = runValue;
	   }
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
	   return amRunning;
   }
   
   @SuppressWarnings("unchecked")
   private void resetPossibleTransitions() throws StateComponentTransitionException, RemoteException, SmartFrogRuntimeException {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   
	   enabled=null;
	   if (!checkRunning()){
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.COMPONENT_NOTRUNNING);
       }
       if (!checkIsEnabled()) {
           enabled =null;
           return;
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
                //actually ok...
               go=true;
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

       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   private void acquireLock() throws StateComponentTransitionException {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   sfLog().debug("IN: State("+name+").acquireLock(...)");
	   sfLog().debug("is locked?"+transitionLock.isLocked()+transitionLock.getHoldCount()+transitionLock.getQueueLength());
	   if ((currentAction!=null && currentAction!=Thread.currentThread()) || //Allow locking only by scheduled action...
			   (currentAction==null && transitionLock.isLocked() && !transitionLock.isHeldByCurrentThread())) {
           throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_ACQUIRELOCK);
       }
	   transitionLock.lock();
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   private void cleanLock(){
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   while (transitionLock.isHeldByCurrentThread()) {  //CHECK!
		   transitionLock.unlock();
	   }
	   sfLog().debug("is locked?"+transitionLock.isHeldByCurrentThread()+transitionLock.isLocked()+transitionLock.getHoldCount()+transitionLock.getQueueLength());
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   private void preparefinalize(boolean prepare, String transition) throws StateComponentTransitionException {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
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
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   private void doPrepare(String transition) throws StateComponentTransitionException {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   preparefinalize(true, transition);
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   private void doFinalize(String transition) throws StateComponentTransitionException {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   preparefinalize(false, transition);
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
     
   private void clean(){
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   currentAction=null;
	   cleanLock();
	   //asyncResponse=false;  //LOG ISSUE AND REMOVE...
	   scriptTimer=null;
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   //See handleDPEs()  -- this must go!!!
   private boolean runDPEs() throws SmartFrogResolutionException, RemoteException {
       sfLog().debug("IN: State(" + name + ").runDPEs()");
	   boolean ret=false;
	   for (ApplyReference ar: dpes){
		  if ((Boolean)sfResolve(ar)) ret=true;
	   }
       sfLog().debug("OUT: State(" + name + ").runDPEs()");
	   return ret;
   }

   private void transitionScript(ComponentDescription transition, String key) throws StateComponentTransitionException {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);

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
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }

    private boolean checkIsEnabled() throws RemoteException, SmartFrogRuntimeException {  //and connector on the dependencies
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        for (DependencyValidation dv : dependencies) {
            if (dv.getTransition() != null) continue;
            if (!dv.isEnabled()) return false;
        }
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return true;
    }

    private void setState() throws StateComponentTransitionException {
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        sfLog().debug("Adding to queue...");
        try {
            threadpool.addToQueue((StateUpdateThread) (currentAction = new StateUpdateThread()));
            sfLog().debug("Added to Queue");
        } catch (RejectedExecutionException e) { /*Gracefully ignore*/ }

        cleanLock();

        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }

    protected abstract boolean threadBody() throws StateComponentTransitionException, RemoteException;

    //////////////////////////////////////////////////////////////////////
    //StateComponentManagement

    /*THIS IS REDUNDANT -- WILL BE REMOVED
    public HashMap<String, Object> getLocalState() {
        HashMap<String, Object> state = new HashMap<String, Object>();
        Enumeration keys = sfContext().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object val = null;

            try {
                val = sfResolve(new Reference(ReferencePart.here(key)));
            } catch (SmartFrogResolutionException ignored) {
                sfLog().ignore(ignored);
            } catch (RemoteException ignored) {
                sfLog().ignore(ignored);
            }
            state.put(key.toString(), val);
        }
        return state;
    }*/


    //Inappropriate name - this is not to do with asyncResponse. Instead, it is external management change related...*/
    public void invokeAsynchronousStateChange(InvokeAsynchronousStateChange iasc) throws StateComponentTransitionException, RemoteException {

        if (currentAction != null) {
            throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.CURRENTACTION_ONGOING);
        }

        acquireLock();
        try {
            resetPossibleTransitions();
        } catch (SmartFrogRuntimeException e) {
            throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_GETTRANSITIONS);
        }
        iasc.actOn(this);
        //handleDPEs();
        clean();
    }

    /*THESE WILL BE ACCOMMODATED AS EXPLICIT TRANSITION -- TO LOG ISSUE AND REMOVE*/
    public boolean handleDPEs() throws RemoteException, StateComponentTransitionException {
        sfLog().debug("IN: State(" + name + ").handleDPEs()");
        boolean progress = false;

        Constraint.lockUpdateContext();
        try {
            progress = runDPEs();
        } catch (SmartFrogResolutionException e) {
            sfLog().warn(e);
            throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_HANDLEDPES, e);
        }
        Constraint.applyUpdateContext();

        sfLog().debug("IN: State(" + name + ").handleDPEs() Progress" + progress);

        return progress;
    }

    public void selectSingleAndGo() throws RemoteException, StateComponentTransitionException, SmartFrogRuntimeException {
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        resetPossibleTransitions();
        sfLog().debug("WITHIN: State(" + name + ").selectSingleAndGo(...). Number transitions..." + enabled.size());
        if (enabled.size() == 0 || enabled.size() > 1) {
            throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_FINDSINGLEDEPENDENCYENABLED);
        }
        sfLog().debug("WITHIN: State(" + name + ").selectSingleAndGo(...). Single transition...");

        Iterator keys = enabled.keySet().iterator();
        String key = (String) keys.next();
        go(key);

        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }

    public void go(String transition) throws StateComponentTransitionException {
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        sfLog().debug("Key in simple transition" + transition);
        ComponentDescription trans = null;
        //try{
        trans = enabled.get(transition);
        if (trans == null) {
            throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_GETNAMEDENABLEDTRANSITION);
        }

        try {
            trans.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.EFFECTS)));
        } catch (SmartFrogResolutionException e) {
            throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_RESOLVETRANSITIONEFFECTS, e);
        }

        try {
            if (eventLog != null) {
                int count = eventLog.sfResolve(COUNT, 0, true);
                eventLog.sfAddAttribute("transition" + count, fullName + ":" + transition);
                eventLog.sfReplaceAttribute(COUNT, ++count);
            }
        } catch (Exception e) {
            throw new StateComponentTransitionException(StateComponentTransitionException.StateComponentExceptionCode.FAILEDTO_WRITEEVENTLOGBUTISPRESENT);
        }

        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }

    //////////////////////////////////////////////////////////////////////
    //StateChangeNotification

    //The following aren't really "StateComponent" methods and should be rehoused at some stage
    public String getModelInfoAsString(String refresh) throws RemoteException, SmartFrogResolutionException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        /*  EXAMPLE:
       sfModelMetaData extends DATA {
      description "This is the foobar service";
      links extends DATA {
         -- extends DATA {
           description "link to my friend";
           link "/friend";
         }
      }
   }
        */

        StringBuilder result = new StringBuilder();

        ComponentDescription metaData = sfResolve(MODELMETADATA, (ComponentDescription) null, false);

        if (metaData != null) {
            result.append(MAINHEADER).append(metaData.sfResolve(DESCRIPTION).toString()).
                    append(MAINHEADERCLOSE);
            
            ComponentDescription links = (ComponentDescription) metaData.sfContext().get(LINKS);
            if (links != null) {
                Enumeration keys = links.sfContext().keys();
                while (keys.hasMoreElements()) {
                    ComponentDescription link = (ComponentDescription) links.sfContext().get(keys.nextElement());
                    result.append(SCRIPTHEADER).
                            append("<A HREF=\"").
                            append(link.sfContext().get(LINK)).
                            append("\">").
                            append(link.sfContext().get(DESCRIPTION)).
                            append("</A>").
                            append(SCRIPTHEADERCLOSE);
                    //append(LINEBREAK);
                }
            }
        }
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");

        return result.toString();
    }

    public String getTransitionLogAsString() throws RemoteException, SmartFrogResolutionException {
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        StringBuilder result = new StringBuilder();

        if (eventLog != null) {
            int count = eventLog.sfResolve(COUNT, 0, true);
            for (int i = 0; i < count; i++) {
                String transition = "transition" + i;
                result.append(SCRIPTHEADER).
                        append(transition).
                        append(": ").
                        append((String) eventLog.sfContext().get(transition)).
                        append(SCRIPTHEADERCLOSE);
            }
        }
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");

        return result.toString();
    }

    void observed(ComponentDescription entry, StringBuilder result, int indent) throws SmartFrogResolutionException, RemoteException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        ComponentDescription observed = (ComponentDescription) entry.sfContext().get(OBSERVED);
        if (observed != null) {
            boolean cont = true;
            /*if (observed.sfContext().get(GUARD)!=null){
                Boolean guardEval = (Boolean) observed.sfResolve(GUARD);
                if ((guardEval != null) && !(guardEval)) cont=false;
            }*/
            if (cont) {

                boolean annotate = observed.sfResolve(ANNOTATE, false, false);

                String description = null;
                try {
                    description = observed.sfResolve(DESCRIPTION).toString();
                } catch (SmartFrogResolutionException e) {
                    sfLog().debug(e);
                }

                if (description!=null){
                    result.append(SCRIPTHEADER1).append(indent).append(SCRIPTHEADER2).//append(SCRIPTHEADER).
                            append(ITAL).
                            append(description).append(ITALCLOSE);//.append(SCRIPTHEADERCLOSE);
                }

                sfLog().debug("ATTRIBUTE! ");
                String attribute = null;
                try {
                    attribute = observed.sfResolve(ATTRIBUTE).toString();
                } catch (SmartFrogResolutionException e) {
                    sfLog().debug(e);
                    return; //take no action...
                }

                sfLog().debug("ATTRIBUTE! " + attribute);
                String value = null;
                try {
                    value = sfResolve(attribute).toString();
                } catch (SmartFrogResolutionException e) {
                    sfLog().debug(e);
                    return; //take no action... 
                }
                
                sfLog().debug("VALUE! " + value);


                sfLog().debug("ALIASES! ");
                ComponentDescription aliases = null;
                try {
                    aliases = (ComponentDescription) observed.sfResolve(ALIASES);
                } catch (SmartFrogResolutionException ignore) {

                }
                if (aliases != null) {
                    String alias = (String) aliases.sfContext().get(value);
                    if (alias != null) value = alias;
                }
                sfLog().debug("VALUE "+value);
                result.append(SOMEPADDING).//append(SCRIPTHEADER1).append(indent).append(SCRIPTHEADER2).
                        append(value).append(annotate?POBSERVED:"").append(SCRIPTHEADERCLOSE);
            }
        }
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]+":LEAVING");
    }

    void desired(ComponentDescription entry, StringBuilder result, int indent) throws SmartFrogResolutionException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        ComponentDescription desired = (ComponentDescription) entry.sfContext().get(DESIRED);
        boolean cont = true;
        if (desired != null) {

            Vector values = null;
            try {
                values = (Vector) desired.sfResolve(VALUES);
            } catch (SmartFrogResolutionException ignore) {

            }

            sfLog().debug("DESIREDGUARD! ");

            boolean annotate = desired.sfResolve(ANNOTATE, false, false);

            String description = null;
            try {
                description = desired.sfResolve(DESCRIPTION).toString();
            } catch (SmartFrogResolutionException e) {
                sfLog().debug(e);
            }

            if (description != null) {
                result.append(SCRIPTHEADER1).append(indent).append(SCRIPTHEADER2).//append(SCRIPTHEADER).
                        append(ITAL).
                        append(description).append(ITALCLOSE);//.append(SCRIPTHEADERCLOSE);
            }

            String attribute = null;
            try {
                attribute = desired.sfResolve(ATTRIBUTE).toString();
            } catch (SmartFrogResolutionException e) {
                 sfLog().ignore(e);
                return; //ok
            }

            String cvalue = sfContext().get(attribute).toString();
            if (values != null) {

                sfLog().debug("DESIREDATTR! ");

                ComponentDescription aliases = null;
                try {
                    aliases = (ComponentDescription) desired.sfResolve(STATEALIASES);
                } catch (SmartFrogResolutionException ignore) {

                }
                sfLog().debug("DESIREDSTATEALIAS! ");

                if (aliases != null) {
                    String alias = (String) aliases.sfContext().get(cvalue);
                    if (alias != null) cvalue = alias;
                }
                result.append(SOMEPADDING).//append(SCRIPTHEADER1).append(indent).append(SCRIPTHEADER2).
                        append(cvalue).append(annotate ? PDESIRED : "").append(SCRIPTHEADERCLOSE);
            }


            sfLog().debug("DESIREDSTATEPRINT! ");

            /*try {
                Boolean guardEval = (Boolean) desired.sfResolve(GUARD);
                if ((guardEval != null) && !(guardEval)) cont = false; //round while
            } catch (Exception ignore) {

            }*/

            if (cont) {

                result.append(SCRIPTHEADER1).append(indent+1).append(SCRIPTHEADER2).append(SETVALUE);


                ComponentDescription aliases = null;
                try {
                    aliases = (ComponentDescription) desired.sfResolve(ACTIONALIASES);
                } catch (SmartFrogResolutionException ignore) {

                }

                sfLog().debug("DESIREDACTIONALIAS! ");

                String dStateName = ":::" + fullName + ":::" + attribute;
                if (values != null) {

                    result.append("<SELECT name=\"").append(dStateName).append("\">");
                    for (Object value : values) {
                        result.append("<OPTION value=\"");
                        String realValue = value.toString();
                        String shownValue = realValue;
                        if (aliases != null) {
                            String alias = (String) aliases.sfContext().get(realValue);
                            if (alias != null) shownValue = alias;
                        }
                        result.append(realValue).append("\">").
                                append(shownValue).append("</OPTION>");
                    }
                    result.append("</SELECT>");

                } else {
                    result.append("<INPUT type=\"text\" value=\"").append(cvalue).
                            append("\"name=\"").append(dStateName).append("\"/>");
                }
                result.append(SCRIPTHEADERCLOSE);
                sfLog().debug("DESIREDSTATESET! ");
            } 
        }
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]+":LEAVING");
    }


    void links(ComponentDescription entry, StringBuilder result, int indent){
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        ComponentDescription links = null;
        try {
            links = (ComponentDescription) entry.sfResolve(LINKS);
        } catch (SmartFrogResolutionException ignore) {

        } catch (ClassCastException ignore) {

        }
        if (links != null) {

            sfLog().debug("LINKS...yes ");
            String dns = null;
            try {
                dns = (String) sfResolve(DNS);
            } catch (Exception ignore) {

            }

            sfLog().debug("LINKS...dns " + dns);

            if (dns != null) {
                Enumeration keys = links.sfContext().keys();
                while (keys.hasMoreElements()) {
                    ComponentDescription link = (ComponentDescription) links.sfContext().get(keys.nextElement());
                    sfLog().debug("LINKS...link " + link);
                    try {
                        Boolean guardEval = (Boolean) link.sfResolve(GUARD);
                        if ((guardEval != null) && !(guardEval)) continue; //round while
                    } catch (Exception ignore) {
                        sfLog().debug("LINKS...did not resolve guard "+ignore);    
                    }
                    result.append(SCRIPTHEADER1).append(indent+1).append(SCRIPTHEADER2).
                            append("<A HREF=\"http://").append(dns).
                            append(link.sfContext().get(LINK)).
                            append("\">").
                            append(link.sfContext().get(DESCRIPTION)).
                            append("</A>").
                            append(SCRIPTHEADERCLOSE);
                }
            }
            
        }
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]+":LEAVING");
    }

    public String getServiceStateDetails() throws RemoteException, SmartFrogResolutionException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        boolean ignoreme = sfResolve(IGNOREMETADATA, false, false);
        if (ignoreme) return "";

        ComponentDescription metaData = sfResolve(INFORMMETADATA, (ComponentDescription) null, false);

        StringBuilder status = new StringBuilder();

        if (metaData!=null){
            boolean show = sfResolve(SHOW, false, false);
            String cdescription = sfResolve(DESCRIPTION, (String) null, false);
            int indent = sfResolve(MYINDENT, MYINDENTDEFAULT, false)-1;

            String myParent=MAINDIV;
            try {
                Object myParentObj = sfResolve(PARENT);
                if (myParentObj instanceof String){
                    Reference myParentRef = Reference.fromString(myParentObj.toString());
                    myParentObj = sfResolve(myParentRef);
                }
                Prim myParentPrim = (Prim) myParentObj;
                myParent=myParentPrim.sfResolve(FULLNAMEPATH).toString();
                myParent=myParent + SFDELIM + CONTAINER;
            } catch (Exception e) {
                sfLog().debug(e); //ok
            }

            //Ok this should be moved...(hence direct specified literal)
            String vmName = sfResolve("vmName", (String)null, false);
            String additional = (vmName!=null? SOMEPADDING+"("+vmName+")"+SOMEPADDING:"");

            status.append("<COMP>");
            status.append("<STATUS>").append(show?SHOW:NOSHOW).append("</STATUS>");
            status.append("<DESCRIPTION>").append(cdescription).append(additional).append("</DESCRIPTION>");
            String resource=fullNamePath+SFDELIM+CONTAINER;
            status.append("<PATH>").append(resource).append("</PATH>");
            status.append("<PARENT>").append(myParent).append("</PARENT>");
            status.append("<INDENT>").append(indent).append("</INDENT>");
            status.append("</COMP>");

            StringBuilder extra = new StringBuilder();

            Enumeration keys = metaData.sfContext().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                ComponentDescription entry = null;
                try {
                    entry = (ComponentDescription) metaData.sfContext().get(key);
                } catch (Exception e) {
                    continue; //round while
                }

                sfLog().debug("ENTRY!!! " + entry + ":" + key);
                sfLog().debug("***GUARD***" + entry.sfContext().get(GUARD));

                try {
                    Boolean guardEval = (Boolean) entry.sfResolve(GUARD);
                    if ((guardEval != null) && !(guardEval)) continue; //round while
                } catch (Exception ignore) {
                    sfLog().debug(ignore);
                }

                sfLog().debug("***GUARD*** PASSES");

                ComponentDescription observed = (ComponentDescription) entry.sfContext().get(OBSERVED);
                ComponentDescription links = null;
                try {
                    links = (ComponentDescription) entry.sfResolve(LINKS);
                } catch (SmartFrogResolutionException ignore) {

                } catch (ClassCastException ignore) {

                }

                sfLog().debug("***LINKS***"+links);
                sfLog().debug("***OBSERVED***" + observed);

                if (links!=null || observed!=null){
                    extra.append("<COMP>");
                    extra.append("<STATUS>").append(observed!=null?"updating":"static").append("</STATUS>");

                    resource = fullNamePath + SFDELIM + key + SFDELIM + OBSERVED;
                    extra.append("<PATH>").append(resource).append("</PATH>");
                    extra.append("<PARENT>").append(fullNamePath).append(SFDELIM).append(CONTAINER).append("</PARENT>");
                    extra.append("</COMP>");
                }

                ComponentDescription desired = (ComponentDescription) entry.sfContext().get(DESIRED);
                sfLog().debug("***DESIRED***" + desired);

                if (desired != null) {
                    extra.append("<COMP>");
                    extra.append("<STATUS>").append("static").append("</STATUS>");
                    resource = fullNamePath + SFDELIM + key + SFDELIM + DESIRED;
                    extra.append("<PATH>").append(resource).append("</PATH>");
                    extra.append("<PARENT>").append(fullNamePath).append(SFDELIM).append(CONTAINER).append("</PARENT>");
                    extra.append("</COMP>");
                }
            }

            if (extra.toString().length()==0) return "";
            else status.append(extra);
        }
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]+":LEAVING");
        
        return status.toString();
    }

    public String getServiceStateObserved(String key) throws RemoteException, SmartFrogResolutionException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);


        StringBuilder result = new StringBuilder();

        ComponentDescription metaData = sfResolve(INFORMMETADATA, (ComponentDescription) null, false);
        ComponentDescription entry = (ComponentDescription) metaData.sfContext().get(key);
        
        sfLog().debug("OBSERVED! " + key);

        int indent = sfResolve(MYINDENT, MYINDENTDEFAULT, false);

        observed(entry, result, indent);

        //ANY LINKS?
        links(entry, result, indent);

        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return result.toString();
    }

    public String getServiceStateDesired(String key) throws RemoteException, SmartFrogResolutionException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);


        StringBuilder result = new StringBuilder();

        ComponentDescription metaData = sfResolve(INFORMMETADATA, (ComponentDescription) null, false);
        ComponentDescription entry = (ComponentDescription) metaData.sfContext().get(key);

        int indent = sfResolve(MYINDENT, MYINDENTDEFAULT, false);
        
        desired(entry, result, indent);
        sfLog().debug("DESIRED! " + key);

        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return result.toString();
    }

    public String getServiceStateContainer() throws RemoteException, SmartFrogResolutionException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        StringBuilder result = new StringBuilder();

        //This should be removed but it requires changing the index.html implementation to not ask for a "container!"...

        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return result.toString();
    }


    public boolean isThreadedComposite() throws RemoteException, SmartFrogException {
        return false;
    }

    Timer scriptTimer;

    public void handleStateChange() throws RemoteException, SmartFrogException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        if (this.sfIsTerminating || this.sfIsTerminated || !(this.sfIsStarted)) return;


        sfLog().debug("IN: State(" + name + ").handleStateChange()");

        sfLog().debug("To run DPEs..." + dpes.size() + ": currentAction: " + currentAction);

        if (currentAction != null || scriptTimer != null) return;  //do nothing...

        try {
            acquireLock();
        } catch (StateComponentTransitionException ignored) {
            sfLog().ignore(ignored);
            return;  //skip for now...
        }


        //This need cutting - but orchdws depends on it!!!
        if (handleDPEs()) {
            sfLog().debug("OUT: State(" + name + ").handleStateChange()");
            clean();
            return;  //dpes have proceeded...
        }
        //

        resetPossibleTransitions();

        if (enabled == null) {
            sfLog().debug("no enabled transitions...");

            sfLog().debug("OUT: State(" + name + ").handleStateChange() -- Nothing to do...");
            clean();
            return;  //nothing to do...
        }

        //Check if simple transition...
        if (enabled.size() == 1) {
            final String key = enabled.keySet().iterator().next();
            final ComponentDescription transition = enabled.get(key);
            sfLog().debug("Key in simple transition: " + key);

            if (!transition.sfResolve(REQUIRES_THREAD, false, true)) {

                sfLog().debug("Does not require thread");

                //Is there a preparetransition script?
                doPrepare(key);

                int lag = transition.sfResolve(LAG, 0, true);
                if (lag > 0) {
                    ActionListener taskPerformer = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            try {
                                transitionScript(transition, key);
                            } catch (StateComponentTransitionException ignored) {
                                sfLog().warn(ignored); //ok to ignore, as all it means is transition is not followed...
                            }
                        }
                    };
                    (scriptTimer = new Timer(lag, taskPerformer)).start();

                } else transitionScript(transition, key);
                return;
            }
        }

        setState();
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]+":LEAVING");
    }

    //////////////////////////////////////////////////////////////////////
   //StateDependencies

   public void register(DependencyValidation d) {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
      dependencies.add(d);
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }

   public void deregister(DependencyValidation d) {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
      dependencies.remove(d);
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }

   /////////////////////////////////////////////////////////
   //DependencyValidation

   public boolean isEnabled() throws RemoteException, SmartFrogRuntimeException {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
      return (!asAndConnector) || checkIsEnabled();
   }

   public String getTransition(){
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
	   return null;
   }

   /**
    * ********************************************************
    * thread definition
    * ********************************************************
    */
   public class StateUpdateThread implements Runnable {
      public void run() {
          if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
    	  try{
    	   String key=null;
    	   if (enabled.size()==1){
    		   key = enabled.keySet().iterator().next();
    		   doPrepare(key);
    	   }
    		  
    	   if (threadBody()) {
    		   StateComponent.this.clean();
    		   if (key!=null) doFinalize(key);
           } /* NO such thing as asynchronous response any longer... LOG ISSUE AND REMOVE...
            else {

        	   StateComponent.this.cleanLock();
        	   StateComponent.this.asyncResponse=true;
           }*/
    	  }  catch (StateComponentTransitionException ignored) {
              sfLog().warn(ignored);  //appropriate action will have already been taken
          } catch (RemoteException ignored) {
              sfLog().warn(ignored);  //appropriate action will have already been taken
          }
          if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
      }
   }
}
