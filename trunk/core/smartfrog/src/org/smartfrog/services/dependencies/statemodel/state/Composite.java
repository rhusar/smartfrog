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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.languages.sf.functions.ApplyEffects.DeployingAgent;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import static org.smartfrog.services.dependencies.statemodel.state.Constants.NAME;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.ORCHMODEL;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.NORMALTERMINATION;

/**
 * Composite pattern for orchestration components
 */
public class Composite extends CompoundImpl implements Compound, StateChangeNotification, RunSynchronisation, DeployingAgent {

    private String name = "";
    private volatile boolean terminating = false;
    private List<String> toTerminate = new ArrayList<String>();
    private List<CompositeQueueListener> listeners = new ArrayList<CompositeQueueListener>();
    private HashMap<String, ComponentDescription> toDeploy = new HashMap<String, ComponentDescription>();
    private static final int WAIT_A_REASONABLE_PERIOD=5000;

    private static class CompositeQueueListener {
        private volatile boolean cleared = false;
        void clear() {
            cleared = true;
        }
        public boolean isCleared() {
            return cleared;
        }
    }
   
   public Composite() throws RemoteException {  
   }
   
   public void waitOnQueuesCleared() throws IOException {
	   sfLog().debug("IN: Composite: waitOnQueuesCleared()");
	   CompositeQueueListener ccl = new CompositeQueueListener();
	   synchronized (listeners){
		   listeners.add(ccl);
	   }
	   while (true) {
		    synchronized (ccl){
		    	if (ccl.isCleared()) break; //from while...
		    }
		    sfLog().debug("Sleeping...");
			try {
                Thread.sleep(WAIT_A_REASONABLE_PERIOD);
            } catch(InterruptedException e){
                sfLog().debug(e);
                InterruptedIOException ie = new InterruptedIOException(e.getMessage());
                ie.setStackTrace(e.getStackTrace());
                throw ie;
            }
	   }
	   sfLog().debug("OUT: Composite: waitOnQueuesCleared()");
   }
   
   public void addToDeploy(String name, ComponentDescription cd) throws SmartFrogException {
	   sfLog().debug("IN: Composite: addToDeploy(...)");
	   synchronized (toDeploy){
		   if (toDeploy.containsKey(name)){
			   throw new SmartFrogException("Name: "+name+" exists already");
		   }
		   
		   toDeploy.put(name, cd);
		   
		   /*CODE LEFT HERE FOR CONVENIENCE.  Need to accommodate, but not now.
		   OrchComponentModel model = null;
			try {
				model = (OrchComponentModel) effects.sfResolve(new Reference(ReferencePart.attrib("orchModel")));
			} catch (Exception e){ /*Intentionally leave* }
			if (model!=null){
				Prim added = null;
				try{
					added = (Prim) source_nd.sfResolve(key.toString());
				} catch (Exception e){/*System.out.println("EXCEPTION2:"+e);*}
				if (added!=null && added instanceof SynchedComposite) model.addToRun(added); 
			}*/
		   sfLog().debug("OUT: Composite: addToDeploy(...)");
	   }
   }
   
   public void addToTerminate(String name){
	   sfLog().debug("IN: Composite: addToTerminate(...)");
	   synchronized (toTerminate){
		   toTerminate.add(name);
	   }
	   sfLog().debug("OUT: Composite: addToTerminate(...)");
	   
   }
   
   public void sfDeploy() throws RemoteException, SmartFrogException {      
	   sfLog().debug("IN: Composite: sfDeploy(...)");
	   
	   //My name...
       name = sfResolve(NAME, (String)null, false);

      if (name==null){
    	  Prim p = sfParent();
    	  if (p!=null) name = sfParent().sfAttributeKeyFor(this).toString();
    	  else name="sfConfig";
      }

      super.sfDeploy();
      sfLog().debug("OUT: Composite: sfDeploy(...)");
	   
   }

   public void sfStart() throws RemoteException, SmartFrogException {
	   sfLog().debug("IN: Composite: sfStart(...)");
	   //try {
       try {
           sfResolve(ORCHMODEL);
           new Thread(new Notifier()).start();  //Only for orch models do we set this off!
       } catch (SmartFrogResolutionException ignored) {
           sfLog().ignore(ignored);  //intentionally ok
       }
	   super.sfStart();   
	   sfLog().debug("OUT: Composite: sfStart(...)");
		
   }
   
   public void sfTerminateWith(TerminationRecord tr) {
	   sfLog().debug("IN: Composite: sfTerminateWith(...)");
		
	   terminating=true;      
	   super.sfTerminateWith(tr);
	   sfLog().debug("OUT: Composite: sfTerminateWith(...)");
		
   }
   
   public void sfRun() throws SmartFrogException, RemoteException {
	   sfLog().debug("IN: Composite: sfRun(...)");
		
	   for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
	         Object c = e.nextElement();
	         if (c instanceof RunSynchronisation) {
	        	 ((RunSynchronisation)c).sfRun();
	         }
	      }
	   sfLog().debug("OUT: Composite: sfRun(...)");
		
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
   
   public void handleStateChange() throws RemoteException, SmartFrogException {
	  sfLog().debug("IN: Composite: handleStateChange()");
	  synchronized (toDeploy){ synchronized (toTerminate) { synchronized (listeners){	
	   
	  sfLog().debug("IN: Composite.hsc()"+name);   
	  for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
         Object c = e.nextElement();
         if (c instanceof StateChangeNotification) {
        	 //if (sfLog().isDebugEnabled())  sfLog().debug("GOING IN with:"+c); 
             try {
                 ((StateChangeNotification)c).handleStateChange();
             } catch (RemoteException ignored) {
                sfLog().warn(ignored);  //trying to be somewhat robust to the presence of these, just ignore and try again next time...
             }
         }
      }
	  
	  sfLog().debug("Composite.hsc() "+name +"Deploying/terminating if any ");
	  
	  if (toDeploy.size()>0){
		  Iterator<String> keys = toDeploy.keySet().iterator();
		  while (keys.hasNext()){
			  String key = keys.next();
			  sfLog().debug("Composite.hsc() "+name +"Deploying "+key);
              try {
                  sfCreateNewChild(key, toDeploy.get(key), null);
              } catch (SmartFrogDeploymentException e) {
                  sfLog().error("Composite.hsc() " + name + " Exception in Deploying " + key + " : ");
                  sfLog().error(e);
                  throw e;
              }
		  }
		  toDeploy = new HashMap<String, ComponentDescription>();
	  }

	  if (toTerminate.size()>0){
		  Iterator<String> keys = toTerminate.iterator();
		  while (keys.hasNext()){
              String key = keys.next();
              Prim p = (Prim) sfResolve(key);
              if (p instanceof Composite) p.sfReplaceAttribute(NORMALTERMINATION, true);
              sfLog().debug("Composite.hsc() "+name +"Terminating "+key);
              p.sfDetachAndTerminate(TerminationRecord.normal(null));
		  }
		  toTerminate = new Vector<String>();
	  }
	  
	  for (CompositeQueueListener l : listeners) {
          l.clear();
	  }
	  listeners.clear(); 
	   
	  }}}
	  sfLog().debug("OUT: Composite: handleStateChange()");
	  
   }

   /* *************************************************
	   * Update class
	   */
	   class Notifier implements Runnable {
	      public void run() {
	    	  sfLog().debug("IN: Composite.Notifier.run()");
	          while (!Composite.this.terminating){
	        	  try{
                      handleStateChange();
                  } catch (SmartFrogException e) {
                      sfLog().error(e);
                      throw new RuntimeException(e);
                  } catch (RemoteException ignored) {
                      sfLog().warn(ignored);  //trying to be somewhat robust to the presence of these, just ignore and try again next time...
                  }
	          }
	          sfLog().debug("OUT: Composite.Notifier.run()");
	      }
	   }
	   
}
