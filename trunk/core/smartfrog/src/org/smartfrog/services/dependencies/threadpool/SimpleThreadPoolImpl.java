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
	  sfLog().debug("IN: threadPool: addToQueue()");
	  synchronized (this){
		  busyThreads++;  //we should increase before it's run...
	  }
	  sfLog().debug("threadPool: addToQueue() SUBMITTING");
      Future<?> future = es.submit(new Runnable_(run));
      sfLog().debug("OUT: threadPool: addToQueue()");
      return future;
   }  
 
}
