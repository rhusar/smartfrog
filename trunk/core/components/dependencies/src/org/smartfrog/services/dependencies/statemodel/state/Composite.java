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
import java.util.*;

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.common.Timer;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.languages.sf.functions.ApplyEffects.DeployingAgent;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import static org.smartfrog.services.dependencies.statemodel.state.Constants.*;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * Composite pattern for orchestration components
 */
public class
        Composite extends CompoundImpl implements Compound, StateChangeNotification,
        RunSynchronisation, DeployingAgent {

    private String name = "";
    private volatile boolean terminating = false;
    private List<String> toTerminate = new ArrayList<String>();
    private List<CompositeQueueListener> listeners = new ArrayList<CompositeQueueListener>();
    private HashMap<String, ComponentDescription> toDeploy = new HashMap<String, ComponentDescription>();
    private static final int WAIT_A_REASONABLE_PERIOD=5000;
    private static long notificationSleep;
    private boolean threaded=false;
    private Vector<Mapping> sfMappings;
    private Prim eventLog;

    private static final String E_BAD_PATH = "Bad path in Mapping, should be a Reference...";
    private static final String E_BAD_ATTR = "Bad attr in Mapping, should be a String...";
    private static final String E_BAD_ECHOATTR = "Bad echo attr in Mapping, should be a String (if present)...";
    private static final String E_BAD_REPATTR = "Bad replaceAttr in Mapping, should be a String (if present)...";
    private static final String E_BAD_ECHO = "Bad echo in Mapping, should be a Reference (if present)...";
    private static final String E_BAD_SCOPE = "Bad scope in Mapping, should be a Reference (if present)...";

    private static final String ATTR = "attr";
    private static final String PATH = "path";
    private static final String REPLACE_ATTR = "replaceAttr";
    private static final String ECHO = "echo";
    private static final String ECHOATTR = "echoAttr";
    private static final String RETRY = "retry";
    private static final String SCOPE = "scope";

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

        ComponentDescription sfMapping = sfResolve(MAPPING, (ComponentDescription) null, false);

        if (sfMapping != null) {

            sfMappings = new Vector<Mapping>();
            Enumeration els = sfMapping.sfContext().elements();
            while (els.hasMoreElements()) {
                Object el = els.nextElement();
                if (el instanceof ComponentDescription) {
                    ComponentDescription mapping = (ComponentDescription) el;
                    Object attrObj = mapping.sfResolve(ATTR);
                    if (attrObj != null) {
                        Mapping newMapping = new Mapping();
                        sfMappings.add(newMapping);
                        try {
                            newMapping.attr = (String) attrObj;
                        } catch (ClassCastException e) {
                            throw new SmartFrogException(E_BAD_ATTR, e);
                        }

                        Object replaceObj = mapping.sfResolve(REPLACE_ATTR);
                        if (replaceObj instanceof SFNull) newMapping.replaceAttr = null;
                        else if (replaceObj instanceof String) newMapping.replaceAttr = replaceObj.toString();
                        else {
                            //force the exception...
                            try {
                                newMapping.replaceAttr = (String) replaceObj;
                            } catch (ClassCastException e) {
                                throw new SmartFrogException(E_BAD_REPATTR, e);
                            }
                        }

                        try {
                            newMapping.path = (Reference) mapping.sfContext().get(PATH);
                        } catch (ClassCastException e) {
                            throw new SmartFrogException(E_BAD_PATH, e);
                        }

                        Object echoObj = mapping.sfContext().get(ECHO);
                        if (echoObj instanceof SFNull) newMapping.echo = null;
                        else if (echoObj instanceof Reference) newMapping.echo = (Reference) echoObj;
                        else {
                            //force the exception...
                            try {
                                newMapping.echo = (Reference) echoObj;
                            } catch (ClassCastException e) {
                                throw new SmartFrogException(E_BAD_ECHO, e);
                            }
                        }

                        if (newMapping.echo!=null){
                            try {
                                newMapping.echoAttr = (String) mapping.sfResolve(ECHOATTR);
                            } catch (ClassCastException e) {
                                throw new SmartFrogException(E_BAD_ECHOATTR, e);
                            }
                        }

                        newMapping.retry = mapping.sfResolve(RETRY, false, true);

                        Object scopeObj = mapping.sfContext().get(SCOPE);
                        if (scopeObj instanceof SFNull) newMapping.scope = null;
                        else if (scopeObj instanceof Reference) newMapping.scope = (Reference) scopeObj;
                        else {
                            //force the exception...
                            try {
                                newMapping.scope = (Reference) scopeObj;
                            } catch (ClassCastException e) {
                                throw new SmartFrogException(E_BAD_SCOPE, e);
                            }
                        }
                    }
                }
            }
        }

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

   /* public String getDesiredStatusAsString() throws RemoteException, SmartFrogResolutionException {
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
    }*/

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
		  sfLog().info("FIX: graceful child deploy failure...");
		  Iterator<String> keys = toDeploy.keySet().iterator();
		  //Vector<String> success = new Vector<String>();
		  while (keys.hasNext()){
			  String key = keys.next();
			  sfLog().debug(name +": Deploying "+key);
              try {
                  sfCreateNewChild(key, toDeploy.get(key), null);
                  //success.add(key);
              } catch (SmartFrogDeploymentException e) {
                  sfLog().error(name + ": Exception in Deploying " + key + " : ");
                  sfLog().error(e);
                  //throw e;
              }
		  }
		  /*for (String skey: success){
			  toDeploy.remove(skey);  //new HashMap<String, ComponentDescription>();
		  }*/
		  toDeploy = new HashMap<String, ComponentDescription>();
	  }

	  if (toTerminate.size()>0){
		  Iterator<String> keys = toTerminate.iterator();
		  while (keys.hasNext()){
              String key = keys.next();
              Prim p = (Prim) sfResolve(key);
              //if (p instanceof Composite) p.sfReplaceAttribute(NORMALTERMINATION, true);
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

    @Override
    public Object sfReplaceAttribute(Object name, Object value) throws SmartFrogRuntimeException, RemoteException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        sfLog().debug("Key:"+name+" , Value:"+value+", "+sfMappings);



        Object retval =  super.sfReplaceAttribute(name, value);

        sfLog().debug("Ready to try mappings...");
        String modelRunning1 = null;
        try {
            modelRunning1 = (String) sfResolve("mapByDefault");
            sfLog().debug("Model Running Attribute:"+modelRunning1);
        } catch (Exception e) {
            //sfLog().debug(e);
        }

        if (modelRunning1!=null){
            sfLog().debug("Key:" + name + " , Cf:" + modelRunning1 );

            if (name.equals(modelRunning1)) {
                doValueMapping(name);
                return retval;
            }

        }
        boolean modelRunning2=false;
            try {
                modelRunning2 = sfResolve(new Reference(ReferencePart.attrib("modelRunning")), false, false);
            } catch (Exception e) {
                sfLog().debug(e);
            }

            if (modelRunning2) {
                doValueMapping(name);
            }

        return retval;
    }


    private void doValueMapping(final Object name)  {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        if (sfMappings != null) {
            boolean retryMapping = false;
            try {
                retryMapping= doValueMappingWkr(name);
            } catch (SmartFrogRuntimeException e) {
                sfLog().debug(e);
                //should not break just because of mapping issue...
            } catch (RemoteException e) {
                sfLog().debug(e);
            }


            if (retryMapping) {
                java.util.Timer timer = new java.util.Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        doValueMapping(name);
                        cancel(); //Terminate the timer...
                    }
                }, 2500);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean doValueMappingWkr(Object name) throws SmartFrogRuntimeException, RemoteException {
        if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
        boolean retryMapping = false;
        for (Mapping mapping : sfMappings){
            Prim target=this;


            try {
                if (mapping.replaceAttr==null){
                    sfLog().debug("No replaceAttr");
                    //If null, we assume all replacements are "game" and that the name is the vm name·..
                    if (mapping.scope!=null) {
                        target = (Prim) sfResolve(mapping.scope);
                        target = (Prim) target.sfResolve(name.toString());
                    } else {
                        target = (Prim) sfParent().sfResolve(new Reference(ReferencePart.attrib(name.toString())));
                    }
                    sfLog().debug("No replaceAttr:"+target);
                } else if (!(name.equals(mapping.replaceAttr))) continue; //round while...
                sfLog().debug("Getting target...");
                target = (Prim) target.sfResolve(mapping.path);  //get target from path...
                sfLog().debug("Got target..."+target);
            } catch (Exception e) {
                sfLog().debug(e);
                target=null;  
            }

            if (target==null){
                if (mapping.retry) {
                    retryMapping=true;
                }
                continue; //round while...
            }

            //do replace...
            Object value = sfResolve(name.toString());
            sfLog().debug("Doing the replace..."+target+":"+mapping.attr+":"+value);
            target.sfReplaceAttribute(mapping.attr, value);

            //extra replace?
            if (mapping.echo!=null){
                target = (Prim) target.sfResolve(mapping.echo);

                Vector echoValue = new Vector();
                echoValue.add(mapping.attr);
                echoValue.add(value);
                
                target.sfReplaceAttribute(mapping.echoAttr, echoValue);
            }
        }
        return retryMapping;
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

    /* *************************************************
        * Mapping class
        */

      class Mapping {
          Reference path;
          String attr;
          String replaceAttr;
          Reference echo;
          String echoAttr;
          boolean retry;
          Reference scope;
      }
}
