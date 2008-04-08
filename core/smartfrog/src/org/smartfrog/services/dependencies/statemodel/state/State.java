package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;
import org.smartfrog.services.dependencies.statemodel.utils.CDUtil;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;



/**
 */
public abstract class State extends PrimImpl implements Prim, StateDependencies, StateSetting,
      StateSaving, StateChangeNotification, DependencyValidation {

   protected HashSet dependencies = new HashSet();

   protected boolean asAndConnector = false;
   protected Vector stateData = new Vector();
   protected Vector stateNotify = new Vector();
   protected Vector stateListen = new Vector();

   protected boolean firstTime = true;
   protected HashMap currentState = new HashMap();
   protected HashMap currentListen = new HashMap();

   NotificationLock parentLocking;

   public State() throws RemoteException {
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      
      Context cxt = sfContext();
      Enumeration keys = cxt.keys(); 
      
      while (keys.hasMoreElements()){
    	  Object key = keys.nextElement();
    	  if (cxt.sfContainsTag(key, "stateData")){
    		  stateData.add(key);
    	  }
    	  if (cxt.sfContainsTag(key, "stateListen")){
    		  stateListen.add(key);
    	  }
    	  if (cxt.sfContainsTag(key, "stateNotify")){
    		  stateNotify.add(key);
    	  }
      }
      
      asAndConnector = sfResolve("asAndConnector", asAndConnector, false);
      parentLocking = (NotificationLock)sfParent();
   }


   public void saveState(HashMap save) {
      boolean notificationNeeded = false;
      try {
         if (sfLog().isTraceEnabled()) sfLog().trace("model locking");
         try {
            parentLocking.lock();
         } catch (RemoteException e) {
            //??
         }
         if (sfLog().isTraceEnabled()) sfLog().trace("saving state " + save + " " + stateNotify);
         for (Iterator keys = save.keySet().iterator(); keys.hasNext();) {
            Object key = keys.next();
            Object value = save.get(key);
            if (stateNotify.contains(key)) {
               notificationNeeded = true;
               if (sfLog().isTraceEnabled()) sfLog().trace("notification needed for " + key);
            } else {
               if (sfLog().isTraceEnabled()) sfLog().trace("notification not needed for " + key);
            }
            try {
               sfReplaceAttribute(key, value);
            } catch (Exception e) {
               if (sfLog().isErrorEnabled()) sfLog().error("error adding attributes to state - ignored", e);
            }
         }
      } finally {
         try {
            parentLocking.unlock(notificationNeeded);
         } catch (RemoteException e) {
            //??
         }
         if (sfLog().isTraceEnabled()) sfLog().trace("model unlocked");
      }
   }

   public void register(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.add(d);
   }

   public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException {
      dependencies.remove(d);
      notifyStateChange();
   }

   public void notifyStateChange() {
      try {
         parentLocking.notifyStateChange();
      } catch (RemoteException e) {
         //
      }
   }

   public void handleStateChange() {
      if (sfLog().isDebugEnabled())
         sfLog().debug("handler " + sfCompleteNameSafe() + dependencies);

      boolean enabled = checkIsEnabled();
      if (sfLog().isTraceEnabled()) sfLog().trace("enabled..." + sfCompleteNameSafe() + " " + enabled);
      if (!enabled) return;

      if (stateChanged() || firstTime) {
         if (sfLog().isTraceEnabled()) sfLog().trace("changed " + sfCompleteNameSafe());
         firstTime = false;
         setState((HashMap) currentState.clone());
      } else {
         if (sfLog().isTraceEnabled()) sfLog().trace("not changed " + sfCompleteNameSafe());
      }
   }

   // ready in case we need it for the dependencies to link through components!
   protected boolean checkIsEnabled() {  //and connector on the dependencies
      for (Iterator d = dependencies.iterator(); d.hasNext();) {
         try {
            if (!((DependencyValidation) d.next()).isEnabled()) {
               return false;
            }
         } catch (RemoteException e) {
            //??
         }
      }
      return true;
   }

   public boolean isEnabled() {
      return (!asAndConnector) || checkIsEnabled();
   }

   protected boolean stateChanged() {
      boolean changes = false;
      HashMap newState = new HashMap();

      for (Enumeration e = stateListen.elements(); e.hasMoreElements();) {
         Object key = e.nextElement();
         Object newValue = null;
         try {
            newValue = sfResolve(new Reference(ReferencePart.here(key)));
         } catch (Exception e1) {
            if (sfLog().isErrorEnabled())
               sfLog().error(sfCompleteNameSafe() + "error checking attributes for changes", e1);
         }
         if (!currentListen.containsKey(key) || !newValue.equals(currentListen.get(key))) {//@todo: need to check
            currentListen.put(key, newValue);
            changes = true;
         }
      }

      for (Enumeration e = stateData.elements(); e.hasMoreElements();) {
         Object key = e.nextElement();
         Object newValue = null;
         try {
            newValue = sfResolve(new Reference(ReferencePart.here(key)));
            if (newValue instanceof ComponentDescription) newValue = CDUtil.copy((ComponentDescription)newValue);
            //@todo: the rest of the types being copied...?
         } catch (Exception e1) {
            if (sfLog().isErrorEnabled())
               sfLog().error(sfCompleteNameSafe() + "error checking attributes for changes", e1);
         }
         if (newValue != null) {
            newState.put(key, newValue);
         }
      }

      currentState = newState;
      return changes;
   }

   public abstract void setState(HashMap data);
}
