package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import java.rmi.*;


/*
 * Based on "FutureTask" by Doug Lea with assistance from members of JCP JSR-166

 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain. Use, modify, and
 * redistribute this code in any way without acknowledgement.
 */

public class CreateNewChildThread extends Thread {

/** State value representing that task is running */
 private static final int RUNNING   = 1;
 /** State value representing that task ran */
 private static final int RAN       = 2;
 /** State value representing that task was cancelled */
 private static final int CANCELLED = 4;

 /** The result to return from get() */
 private Object result=null;
 /** The exception to throw from get() */
 private Throwable exception;

 private int state;

 //Create New Child info
 /**name of attribute which the deployed component should adopt*/
 Object name=null;
 /** parent of deployed component  */
 Compound parent=null;
 /**cmp compiled component to deploy and start*/
 ComponentDescription cmp=null;
 /**parms parameters for description*/
 Context parms=null;

 /**
  * The thread running task. When nulled after set/cancel, this
  * indicates that the results are accessible.  Must be
  * volatile, to ensure visibility upon completion.
  */
 private volatile Thread runner;

 /**
  * Creates a <tt>CreateNewChildThread</tt> that will upon running, execute the
  * given <tt>CreateNewChild</tt>.
  *
  * @param  callable the callable task
  * @throws NullPointerException if callable is null
  */
 public CreateNewChildThread(Object name, Prim parent, ComponentDescription cmp, Context parms) throws SmartFrogException{


      if ((parent==null)||!(parent instanceof Compound)){
          //@todo: Replace by SF Exception
          throw new SmartFrogException("Wrong parent");
      }
      String parentName = "parent";
      try {
          parentName = parent.sfCompleteName().toString();
      } catch (RemoteException ex) {
      }

      this.setName("CreateNewChildThread-"+parentName+"."+name);

      this.name=name;

      this.parent=(Compound)parent;

      this.cmp=cmp;

      this.parms=parms;
 }


 public synchronized boolean isCancelled() {
     return state == CANCELLED;
 }

 public synchronized boolean isDone() {
     return ranOrCancelled() && runner == null;
 }

 public boolean cancel(boolean mayInterruptIfRunning) {
     synchronized (this) {
         if (ranOrCancelled()) return false;
         state = CANCELLED;
         if (mayInterruptIfRunning) {
             Thread r = runner;
             if (r != null) r.interrupt();
         }
         runner = null;

         //Terminate child if it was deployed
         if (result!=null) {
             try {
                 ((Prim)result).sfTerminate(TerminationRecord.abnormal(
                     "cancelled", null));
             } catch (RemoteException ex) {
             }
         }
         result=null;

         notifyAll();
     }

     done();
     return true;
 }

 public synchronized Object get()
     throws InterruptedException, SmartFrogException
 {
     waitFor();
     return getResult();
 }

 public synchronized Object get(long timeout)
     throws InterruptedException, SmartFrogException
 {
     waitFor(timeout);
     return getResult();
 }

 /**
  * Protected method invoked when this task transitions to state
  * <tt>isDone</tt> (whether normally or via cancellation). The
  * default implementation does nothing.  Subclasses may override
  * this method to invoke completion callbacks or perform
  * bookkeeping. Note that you can query status inside the
  * implementation of this method to determine whether this task
  * has been cancelled.
  */
 protected void done() { }

 /**
  * Sets the result of this Future to the given value unless
  * this future has already been set or has been cancelled.
  * @param v the value
  */
 protected void set(Object v) {
     setCompleted(v);
 }

 /**
  * Causes this future to report an <tt>ExecutionException</tt>
  * with the given throwable as its cause, unless this Future has
  * already been set or has been cancelled.
  * @param t the cause of failure.
  */
 protected void setException(Throwable t) {
     setFailed(t);
 }

 /**
  * Sets this Future to the result of computation unless
  * it has been cancelled.
  */
 public void run() {
     synchronized (this) {
         if (state != 0) return;
         state = RUNNING;
         runner = Thread.currentThread();
     }
     try {
         setCompleted(parent.sfCreateNewChild(name,parent,cmp,parms));
     }
     catch (Throwable ex) {
         setFailed(ex);
     }
 }


 // PRE: lock owned
 private boolean ranOrCancelled() {
     return (state & (RAN | CANCELLED)) != 0;
 }

 /**
  * Marks the task as completed.
  * @param result the result of a task.
  */
 private void setCompleted(Object result) {
     synchronized (this) {
         if (ranOrCancelled()) return;
         this.state = RAN;
         this.result = result;
         this.runner = null;
         notifyAll();
     }

     // invoking callbacks *after* setting future as completed and
     // outside the synchronization block makes it safe to call
     // interrupt() from within callback code (in which case it will be
     // ignored rather than cause deadlock / illegal state exception)
     done();
 }

 /**
  * Marks the task as failed.
  * @param exception the cause of abrupt completion.
  */
 private void setFailed(Throwable exception) {
     synchronized (this) {
         if (ranOrCancelled()) return;
         this.state = RAN;
         this.exception = exception;
         this.runner = null;
         notifyAll();
     }

     // invoking callbacks *after* setting future as completed and
     // outside the synchronization block makes it safe to call
     // interrupt() from within callback code (in which case it will be
     // ignored rather than cause deadlock / illegal state exception)
     done();
 }

 /**
  * Waits for the task to complete.
  * PRE: lock owned
  */
 private void waitFor() throws InterruptedException {
     while (!isDone()) {
         wait();
     }
 }

 /**
  * Waits for the task to complete for timeout milliseconds or throw
  * TimeoutException if still not completed after that
  * PRE: lock owned
  */
 private void waitFor(long timeout) throws InterruptedException, SmartFrogException {
     if (timeout < 0) throw new SmartFrogException("IllegalArgumentException");
     if (isDone()) return;
     long deadline = timeout;
     while (timeout > 0) {
         this.wait(timeout);
         if (isDone()) return;
         timeout = deadline - System.currentTimeMillis();
     }
     throw new SmartFrogException("Timeout");
 }

 /**
  * Gets the result of the task.
  *
  * PRE: task completed
  * PRE: lock owned
  */
 private Object getResult() throws SmartFrogException {
     if (state == CANCELLED) {
         throw new SmartFrogException("CancellationException");
     }
     if (exception != null) {
         throw new SmartFrogException("ExecutionException",exception);
     }
     return result;
 }
}
