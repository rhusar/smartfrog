package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

/**
 */
public abstract class ThreadedState extends State implements Prim {

   protected ThreadPool threadpool;
   protected StateUpdateThread currentAction = null;
   protected boolean asyncResponse = false;
   
   public ThreadedState() throws RemoteException {
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      threadpool = (ThreadPool) sfResolve("threadpool", false);
   }

   protected boolean clearCurrentAction(){
	   boolean cleared = threadpool.removeFromQueue(currentAction); 
	   if (cleared) currentAction=null;
	   return cleared;  
   }
   
   public void setState() {
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: ThreadedStateComponent.setState()");
      
	 //Attempt to clear action from thread pool (if not started)
	 if (clearCurrentAction()) parentLocking.threadStopped(); 
      
     if (currentAction!=null) return; //Not appropriate to allow further transition at this time...
              
     if (requireThread()) {
       	   parentLocking.threadStarted();
           threadpool.addToQueue(currentAction=new StateUpdateThread());
     } 
     if (sfLog().isDebugEnabled())  sfLog().debug("OUT: ThreadedStateComponent.setState()");
   }

   protected void clean(){
	   if (asyncResponse){
		   parentLocking.threadStopped();
		   asyncResponse=false;
	   }
	   currentAction=null;   
	   super.clean();
   }
   
   protected abstract boolean requireThread();
   protected abstract boolean threadBody();

   /**
    * ********************************************************
    * thread definition
    * ********************************************************
    */
   public class StateUpdateThread implements Runnable {
      public void run() {
    	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: StateUpdateThread.run()");
    	  acquireLock();
    	   if (threadBody()) {
       		   parentLocking.threadStopped();
        	   ThreadedState.this.clean();
           } else ThreadedState.this.asyncResponse=true;
           if (sfLog().isDebugEnabled())  sfLog().debug("OUT: StateUpdateThread.run()");
      }
   }
}

