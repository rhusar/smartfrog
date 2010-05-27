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

import static org.smartfrog.services.dependencies.statemodel.state.Constants.*;

/**
 * Composite pattern for orchestration components
 */
public class Composite extends CompoundImpl implements Compound, StateChangeNotification,
        RunSynchronisation, DeployingAgent {

    private String name = "";
    private volatile boolean terminating = false;
    private List<String> toTerminate = new ArrayList<String>();
    private List<CompositeQueueListener> listeners = new ArrayList<CompositeQueueListener>();
    private HashMap<String, ComponentDescription> toDeploy = new HashMap<String, ComponentDescription>();
    private static final int WAIT_A_REASONABLE_PERIOD=5000;
    private static long notificationSleep;
    private boolean threaded=false;
    private Prim eventLog;

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

    public void sfDeploy() throws RemoteException, SmartFrogException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        //My name...
        name = sfResolve(NAME, (String) null, false);

        //Sleep for notification
        notificationSleep = (long) sfResolve(NOTIFDELAY, 1000, false);

        if (name == null) {
            Prim p = sfParent();
            if (p != null) name = sfParent().sfAttributeKeyFor(this).toString();
            else name = "sfConfig";
        }

        super.sfDeploy();
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }

    public void sfStart() throws RemoteException, SmartFrogException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        super.sfStart();
        try {
            sfResolve(THREADEDCOMP);
            threaded = true;
            new Thread(new Notifier()).start();  //Only for orch models do we set this off!

            eventLog = sfResolve(EVENTLOG, (Prim) null, false);

        } catch (SmartFrogResolutionException ignored) {
            sfLog().ignore(ignored);  //intentionally ok
        }
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }

    public void sfTerminateWith(TerminationRecord tr) {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        terminating = true;
        super.sfTerminateWith(tr);
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }

    public void sfRun() throws SmartFrogException, RemoteException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements();) {
            Object c = e.nextElement();
            if (c instanceof RunSynchronisation) {
                ((RunSynchronisation) c).sfRun();
            }
        }
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }
   
   public void waitOnQueuesCleared() throws IOException {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

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
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   public void addToDeploy(String name, ComponentDescription cd) throws SmartFrogException {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

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
	   }
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   public void addToTerminate(String name){
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
       synchronized (toTerminate){
		   toTerminate.add(name);
	   }
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
	   
   }
   
   public String getName(){
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   return name;
   }

    public String getDesiredStatusAsString() throws RemoteException, SmartFrogResolutionException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        String status = "";
        for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements();) {
            Object c = e.nextElement();
            if (c instanceof StateChangeNotification) {
                status += ((StateChangeNotification) c).getDesiredStatusAsString();
            }
        }
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return status;
    }

    public String getServiceStateDetails() throws RemoteException, SmartFrogResolutionException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        String status = "";
        for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements();) {
            Object c = e.nextElement();
            if (c instanceof StateChangeNotification) {
                status += ((StateChangeNotification) c).getServiceStateDetails();
            }
        }
        if (sfLog().isDebugEnabled())
            sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return status;
    }

    public String getServiceStateObserved(String key) throws RemoteException, SmartFrogResolutionException{
        return "";
    }

    public String getServiceStateDesired(String key) throws RemoteException, SmartFrogResolutionException{
        return "";
    }

    public String getServiceStateContainer() throws RemoteException, SmartFrogResolutionException{
        return "";
    }


    public String getTransitionLogAsString() throws SmartFrogResolutionException, RemoteException {
        return "";
    }

    public String getModelInfoAsString(String refresh) throws SmartFrogResolutionException, RemoteException {
        return "";
    }


   public boolean isThreadedComposite() throws RemoteException, SmartFrogException {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
      return threaded;   
   }

   public void handleStateChange() throws RemoteException, SmartFrogException {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

      if (this.sfIsTerminating || this.sfIsTerminated || !(this.sfIsStarted)) return;

	  synchronized (toDeploy){ synchronized (toTerminate) { synchronized (listeners){	
	   
	  for (Enumeration<Liveness> e = sfChildren(); e.hasMoreElements(); ) {
         Object c = e.nextElement();
         if (c instanceof StateChangeNotification) {
        	 //if (sfLog().isDebugEnabled())  sfLog().debug("GOING IN with:"+c); 
             try {
                 StateChangeNotification scn = (StateChangeNotification)c;
                 if (!scn.isThreadedComposite()) scn.handleStateChange();
             } catch (RemoteException ignored) {
                sfLog().warn(ignored);  //trying to be somewhat robust to the presence of these, just ignore and try again next time...
             }
         }
      }
	  
	  sfLog().debug(name +": Deploying/terminating if any ");
	  
	  if (toDeploy.size()>0){
		  Iterator<String> keys = toDeploy.keySet().iterator();
		  while (keys.hasNext()){
			  String key = keys.next();
			  sfLog().debug(name +": Deploying "+key);
              try {
                  sfCreateNewChild(key, toDeploy.get(key), null);
              } catch (SmartFrogDeploymentException e) {
                  sfLog().error(name + ": Exception in Deploying " + key + " : ");
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
              sfLog().debug(name +": Terminating "+key);
              p.sfDetachAndTerminate(TerminationRecord.normal(null));
		  }
		  toTerminate = new Vector<String>();
	  }
	  
	  for (CompositeQueueListener l : listeners) {
          l.clear();
	  }
	  listeners.clear(); 
	   
	  }}}
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
	  
   }


 


   /* *************************************************
	   * Update class
	   */
	   class Notifier implements Runnable {
	      public void run() {
              if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	          while (!Composite.this.terminating){
	        	  try{
                      handleStateChange();
                      Thread.sleep(notificationSleep);
                  } catch (InterruptedException ignored) {
                  } catch (SmartFrogException e) {
                      sfLog().error(e);
                      throw new RuntimeException(e);
                  } catch (RemoteException ignored) {
                      sfLog().warn(ignored);  //trying to be somewhat robust to the presence of these, just ignore and try again next time...
                  }
	          }
	      }
	   }
	   
}
