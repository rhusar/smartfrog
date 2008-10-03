package org.smartfrog.tools.testharness;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.*;
import java.util.*;



/** Similar to a compound but the termination is synchronized with an
 * scheduler. If this scheduler decides that we are not ready to "die"
 * we will block until it thinks otherwise.
 *
 */
public class SynchCompoundImpl extends CompoundImpl 
  implements SynchCompound {  


  /** A flag that avoids calling multiple times the scheduler. */
  private boolean alreadyChecked = false;

  /** A scheduler that will tell us if we can terminate. */
  private Scheduler sched = null;

  /** A tag that helps the scheduler decide whether we can terminate
   * or not. */
  private String terminationTag;

  static int count =0;  
  /**
   * Class Constructor.
   *
   * @exception RemoteException
   */
  public  SynchCompoundImpl() throws RemoteException {

  }

  /** Deploy the compound. Deployment is defined as iterating over the
   * context and deploying any parsed eager components. 
   * 
   * @exception Exception failure deploying compound or sub-component */
  public void sfDeploy() throws SmartFrogException, RemoteException{
   count++;
    try {      
      sched = (Scheduler) sfResolve(SCHEDULER);
    } catch (Exception e) {
      // Behave like a normal compound.
    }

    try {
      terminationTag = (String) sfResolve(TERMINATE_TAG); 
     /* if(!terminationTag.equals("waitForDaemons")) {
     // int sec = new Date(System.currentTimeMillis()).getSeconds();
     // String time = Integer.toString(sec);
     // String pattern = "custom_";
      terminationTag = terminationTag.concat("_");
      terminationTag = terminationTag.concat(Integer.toString(count));
      this.sfReplaceAttribute(TERMINATE_TAG, terminationTag);
      System.out.println("TErminate Tag" + terminationTag);
     }*/
    } catch (Exception e) {
      // Behave like a normal compound.     
    }

    super.sfDeploy();
  }
 
  /** Performs the synchronized compound termination behaviour. 
   *
   *
   * @param status  Termination status record.
   */
  public void sfTerminateWith(TerminationRecord status) {

    waitTerminationAllowed(status);
    super.sfTerminateWith(status);
  }

  /** Request this component to terminate. Even though this function
   * eventually calls sfTerminateWith, it is too late to just do the
   * check there...
   *
   *
   * @param status Termination status record.
   */
  public void sfTerminate(TerminationRecord status) {

    waitTerminationAllowed(status);
    super.sfTerminate(status);
  }

  /** Checks with the scheduler whether it can proceed with
   * termination, otherwise it blocks. If termination is abnormal or
   * we cannot find the scheduler, we just simply go ahead. We only
   * block once in this check.
   *
   *
   * @param status Termination status record.
   */
  synchronized void waitTerminationAllowed(TerminationRecord status) {
    
    // We have already waited...
    if (alreadyChecked)
      return;

    // No scheduler found, no need to wait...
    if ((sched == null)||( terminationTag == null))
      return;
   
    // Don't block on abnormal terminations.
    if ((status != null) && (status.errorType.equals("normal")))
      try {
        sched.waitGoAhead(terminationTag);
      } catch (RemoteException e) {
        // just behave as a normal compound.
        System.out.println("waitTerminationAllowed:Can't"+
                           " connect to scheduler");
      }
    // Never again...
    alreadyChecked = true;
  }    
}
