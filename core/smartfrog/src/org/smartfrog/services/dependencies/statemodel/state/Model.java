package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;

/**

 */
public class Model extends SynchedComposite implements Compound {
	   
	   //Threads on tap...
	   private ThreadPool threadpool;

	   public Model() throws RemoteException {
	   }

	   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
		  //System.out.println("&&&&& IN MODEL DEPLOY &&&&&");
	      super.sfDeploy();
	      threadpool = (ThreadPool) sfResolve("threadpool", true);
	   }

	   public synchronized void sfStart() throws RemoteException, SmartFrogException {
		   //System.out.println("&&&&& IN MODEL START &&&&&");  
		   super.sfStart();
	      threadpool.setIdleRunnable(new Notifier());
	   }

	   public void runNotifier(){
		   threadpool.runIdle();
	   }
	   

	   /* *************************************************
	   * Update class
	   */
	   protected class Notifier implements Runnable {
	      public void run() {
	    	  if (sfLog().isDebugEnabled())  sfLog().debug("IN: Model.Notifier.run()");    
	    	  //System.out.println("++++++++++++++++++++HANDLE STATE CHANGE!!!");
	          handleStateChange();
	          if (sfLog().isDebugEnabled())  sfLog().debug("OUT: Model.Notifier.run()");    
	      }
	   }

	}