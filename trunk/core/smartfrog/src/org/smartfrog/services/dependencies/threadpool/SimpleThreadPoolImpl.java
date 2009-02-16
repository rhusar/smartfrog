package org.smartfrog.services.dependencies.threadpool;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.smartfrog.services.orchcomponent.model.OrchComponentModel;
import org.smartfrog.services.orchcomponent.model.OrchConstants;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * Implementation of the SmartFrog ThreadPool component
 * <p/>
 * Implements the ThreadPool Interface, and provides methods for
 * controlling the execution of a set of jobs by a thread pool
 */
public class SimpleThreadPoolImpl extends PrimImpl implements Remote, Prim, ThreadPool {
   private int numThreads=5;
   private int busyThreads=0;
   private ExecutorService es;
   private boolean suspended=false;
   private Runnable idleRunnable;
   private boolean runAgain=false;
  
   public SimpleThreadPoolImpl() throws RemoteException {
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      numThreads = sfResolve("numThreads", numThreads, false);
      es = Executors.newFixedThreadPool(numThreads);
   }

   public synchronized void sfStart() throws RemoteException, SmartFrogException {
      super.sfStart();
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
		   synchronized (SimpleThreadPoolImpl.this) {
			   while (suspended) {
				   try {SimpleThreadPoolImpl.this.wait();} catch (InterruptedException ie){/**/}
			   }
		   }
		   toRun.run();
		   synchronized(SimpleThreadPoolImpl.this){
		       busyThreads--;
		       runIdle();
		   }
	   }
   }
   
   private class IdleRunnable implements Runnable {
	   Runnable toRun;
	   IdleRunnable(Runnable run){
		   toRun=run;
	   }
	   public void run(){
		   runAgain=true;
		   while(runAgain){
		       runAgain=false;
			   toRun.run();
		   }
		   synchronized(SimpleThreadPoolImpl.this){ 
			   suspended=false;
			   SimpleThreadPoolImpl.this.notifyAll(); 
		   }
	   }
   }

   /**
    * Remove a runnable from the registered Runnable jobs
    */
   public Future<?> removeFromQueue(Future<?> task){
      boolean cancelled= task.cancel(false);
      if (cancelled) synchronized(this) { busyThreads--; }
      return (cancelled?null:task);
   }
   
   public void runIdleAgain(){
	  runAgain=true;
   }
   
   public void runIdle(){
	   if (busyThreads==0 && !suspended) {
		   suspended=true;
		   es.submit(idleRunnable);
	   }
   }
   
   public void setIdleRunnable(Runnable idleRunnable){
	   this.idleRunnable=new IdleRunnable(idleRunnable);
	   runIdle();
   }
   
   /**
    * register a Runnable to be allocated a thread
    *
    */
   public Future<?> addToQueue(Runnable run){
	  synchronized (this){
		  busyThreads++;  //we should increase before it's run...
	  }
      return es.submit(new Runnable_(run));
   }  
 
}
