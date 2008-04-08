package org.smartfrog.services.dependencies.threadpool;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.LinkedList;

/**
 * Implementation of the SmartFrog ThreadPool component
 * <p/>
 * Implements the ThreadPool Interface, and provides methods for
 * controlling the execution of a set of jobs by a thread pool
 */
public class ThreadPoolImpl extends PrimImpl implements Remote, Prim, ThreadPool {
   private ThreadGroup threads = null;
   private int busyThreads = 0;
   private int freeThreads = 0;
   private LinkedList jobs = new LinkedList();

   private int maxFreeThreads = 1;
   private int maxThreads = 5;

   private int threadInstance = 1;

   private class PoolThread extends Thread {
      public PoolThread(ThreadGroup g, String name) {
         super(g, name);
      }

      public void run() {
         if (sfLog().isTraceEnabled())
            sfLog().trace("thread " + getName() + " starting");
         Runnable action = null;

         try {
            while (true) {
               // I'm free, so if there is a job to do, do it, otherwise wait
               synchronized (jobs) {
                  if (jobs.size() == 0) {
                     if (sfLog().isTraceEnabled())
                        sfLog().trace("thread " + getName() + " waiting");
                     jobs.wait(); //an interrupt indicates finish thread??
                  }

                  //I'm allocated to work...
                  synchronized (threads) {
                     freeThreads--;
                     busyThreads++;
                     updateAttributes();
                  }

                  if (jobs.size() > 0) {
                     action = (Runnable) jobs.removeFirst();
                  } else {
                     // actually nothing to do...
                     action = null;
                  }
               }

               // run the job, protecting againt any error
               try {
                  if (action != null) {
                     if (sfLog().isTraceEnabled())
                        sfLog().trace("thread " + getName() + " running job " + action);
                     action.run();
                     if (sfLog().isTraceEnabled())
                        sfLog().trace("thread " + getName() + " completed job " + action);
                  }
               } catch (Throwable t) {//protect the thread
                  if (sfLog().isErrorEnabled())
                     sfLog().error("thread " + getName() + " had error in job " + t);
                  t.printStackTrace();
               }

               synchronized (jobs) {
                  synchronized (threads) {
                     freeThreads++;
                     busyThreads--;
                     updateAttributes();
                     if ((freeThreads > maxFreeThreads) && (jobs.size() == 0))
                        throw new Exception("thread not needed");
                  }
               }

               if (sfLog().isTraceEnabled())
                  sfLog().trace("thread " + getName() + " finished ");
            }  //while loop...

         } catch (Exception e) {
         } // should be controlled termination indication

         synchronized (threads) {
            freeThreads--;
            updateAttributes();
         }

         if (sfLog().isTraceEnabled())
            sfLog().trace("thread " + getName() + " terminating");
      }
   }


   private void updateAttributes() {
      try {
         sfReplaceAttribute("threads", new Integer(threads()));
         sfReplaceAttribute("freeThreads", new Integer(threadsFree()));
         sfReplaceAttribute("jobs", new Integer(queueLength()));
         if (sfLog().isTraceEnabled())
            sfLog().trace("threads " + threads() + " free " + threadsFree() + " jobs " + queueLength() + " busy " + busyThreads);
      } catch (Exception r) {
         //shouldn't happen
      }
   }

   public ThreadPoolImpl() throws RemoteException {
   }

   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      maxFreeThreads = sfResolve("maxFreeThreads", maxFreeThreads, false);
      maxThreads = sfResolve("maxThreads", maxThreads, false);
      threads = new ThreadGroup(sfCompleteNameSafe().toString());
   }

   public synchronized void sfStart() throws RemoteException, SmartFrogException {
      super.sfStart();
   }

   public synchronized void sfTerminateWith(TerminationRecord tr) {
      maxThreads = 0;
      maxFreeThreads = 0;
      threads.interrupt();  //should allow busy to finish and frees will termiante
      super.sfTerminateWith(tr);
   }


   /**
    * register a Runnable to be allocated a thread
    *
    * @param run The instances of a Runnable
    * @return An Object corresponding to the Runnable
    */
   public Object addToQueue(Runnable run){
      synchronized (jobs) {
         if (sfLog().isTraceEnabled())
            sfLog().trace("adding job to queue " + run);
         jobs.addLast(run);
         updateAttributes();

         synchronized (threads) {
            if ((freeThreads < jobs.size()) && (threads() < getMaxThreads())) {
               //create a new thread
               if (sfLog().isTraceEnabled())
                  sfLog().trace("adding thread " + threadInstance);
               freeThreads++;
               updateAttributes();
               new PoolThread(threads, "pool thread " + threadInstance++).start();
            } else {
               jobs.notify();
            }
         }
      }
      return run;
   }

   /**
    * Remove a runnable from the registered Runnable jobs
    *
    * @param run the task to remove
    * @return true if successful, fales if it did not exist or was already allocated
    */
   public boolean removeFromQueue(Object task){
      synchronized (jobs) {
         return jobs.remove((Runnable) task);
      }
   }

   /**
    * get the length of the currently unallocated Runnables
    *
    * @return the queue length
    */
   private int queueLength() {
      return jobs.size();
   }

   /**
    * Obtan the number of threads owned by the pool
    *
    * @return the number of threads
    */
   private int threads() {
      return freeThreads + busyThreads;
   }

   /**
    * Obtain the number of free threads
    *
    * @return the number of free threads
    */
   private int threadsFree() {
      return freeThreads;
   }

   /**
    * Get the setting for the maximum number of threads that this pool may allocated
    *
    * @return the number of threads
    */
   private int getMaxThreads() {
      return maxThreads;
   }

}
