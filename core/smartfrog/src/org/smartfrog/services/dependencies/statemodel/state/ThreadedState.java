package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

/**
 */
public abstract class ThreadedState extends StateComponent implements Prim {

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
   
   public void setState() throws StateComponentTransitionException {
	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: ThreadedStateComponent.setState()");
      
	  System.out.println("1");
	  
	 //Attempt to clear action from thread pool (if not started)
	 if (clearCurrentAction()) parentLocking.threadStopped(); 
      
	 System.out.println("2");
	 
     if (currentAction!=null) return; //Not appropriate to allow further transition at this time...
              
     System.out.println("3");
     
     if (requireThread()) {
    	 System.out.println("3b");
       	   parentLocking.threadStarted();
       	System.out.println("3c");
           threadpool.addToQueue(currentAction=new StateUpdateThread());
           System.out.println("3d");
     } 
     
     System.out.println("4");
     
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
   
   protected abstract boolean requireThread() throws StateComponentTransitionException;
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
       		   parentLocking.threadStopped();
        	   ThreadedState.this.clean();
           } else {
        	   ThreadedState.this.asyncResponse=true;
        	   ThreadedState.super.clean();  //don't want to lose the current action, but release lock...
           }
    	  }  catch (StateComponentTransitionException stce) {/*Hardly acceptable handling*/}
          if (sfLog().isDebugEnabled())  sfLog().debug("OUT: StateUpdateThread.run()");
      }
   }
}

