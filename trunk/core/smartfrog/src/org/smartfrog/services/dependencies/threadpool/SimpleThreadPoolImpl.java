package org.smartfrog.services.dependencies.threadpool;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * Implementation of the SmartFrog ThreadPool component
 * <p/>
 * Implements the ThreadPool Interface, and provides methods for
 * controlling the execution of a set of jobs by a thread pool
 * 
 */
public class SimpleThreadPoolImpl extends PrimImpl implements ThreadPool, Remote, Prim, Serializable {
   private int numThreads=5;
   private int busyThreads=0;
   private transient ExecutorService es;
   
   public SimpleThreadPoolImpl() throws RemoteException {
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      numThreads = sfResolve("numThreads", numThreads, false);
      es = Executors.newFixedThreadPool(numThreads);
   }

   public synchronized void sfTerminateWith(TerminationRecord tr) {
      es.shutdown();
      super.sfTerminateWith(tr);
   }

   private class Runnable_ implements Runnable {
	   Runnable toRun;
	   Runnable_(Runnable run){
		   toRun=run;
	   }
	   public void run(){
		   toRun.run();
		   synchronized(SimpleThreadPoolImpl.this){
		       busyThreads--;
		   }
	   }
   }
   
   /**
    * register a Runnable to be allocated a thread
    *
    */
   public Future<?> addToQueue(Runnable run){
	  if (sfLog().isDebugEnabled()) sfLog().debug("IN: threadPool: addToQueue()");
	  synchronized (this){
		  busyThreads++;  //we should increase before it's run...
	  }
	  if (sfLog().isDebugEnabled()) sfLog().debug("OUT: threadPool: addToQueue()");
      return es.submit(new Runnable_(run));
   }  
 
}
