package org.smartfrog.tools.testharness;

import org.smartfrog.sfcore.compound.CompoundImpl;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.*;


/** A compound that implements the scheduler interface, so that it can
 * coordinates the interactions of certain SF objets, by keeping  
 * state of progress, and blocking clients that should not proceed
 * yet.
 *
 */
public class SchedulerCompoundImpl extends CompoundImpl implements Scheduler {

  /** An object that implements the Scheduler interface for this compound.*/ 
  Scheduler scheduler = null;

 /** A flag to customize whether identical messages are just ignored. */
  boolean removeDuplicates = true;

  /** The number of clients that should agree before a check can proceed. */
  int numberOfAcks = 1;

  /**
   * Class Constructor.
   *
   * @exception RemoteException 
   */
  public SchedulerCompoundImpl() throws RemoteException {
  }

  
   /** Deploy the compound. Deployment is defined as iterating over the
   * context and deploying any parsed eager components. 
   * 
   * @exception Exception failure deploying compound or sub-component */
  public void sfDeploy() throws SmartFrogException, RemoteException {

    try {
      removeDuplicates = 
        (Boolean.valueOf((String) sfResolve(REMOVE_DUPLICATES))).booleanValue();
    } catch (SmartFrogResolutionException e) {
      // Leave default values
    }

    try {
      numberOfAcks = ((Number) sfResolve(NUMBER_OF_ACKS)).intValue();
    } catch (SmartFrogResolutionException e) {
      // Leave default values
    }

    scheduler = new SchedulerImpl(removeDuplicates,numberOfAcks);

    super.sfDeploy();
  }


  /** Returns inmediately if the state corresponding to tag is
   * GO_AHEAD. Otherwise, it blocks until there is a transition to
   * that state. 
   *
   * @param tag A hint that allows the scheduler decide whether the
   * call should continue.
   * @exception RemoteException An error contacting the scheduler.
   */
  public void waitGoAhead(String tag) 
    throws RemoteException {

    if (scheduler != null)
      scheduler.waitGoAhead(tag);
  }  

  /** Notifies the scheduler that, to the extent the caller is
   * concerned, someone that checks on "tag" should be allowed to
   * continue. 
   *
   * @param tag A hint that allows the scheduler decide whether the
   * call should continue.
   * @param senderId An identifier that the scheduler finds unique. 
   * @param force Ignore what other clients say and do allow checks on
   * "tag" to proceed.
   * @exception RemoteException An error contacting the scheduler.
   */
  public void signalGoAhead(String tag, String senderId, 
                                         boolean force) 
                        throws RemoteException {
    
    if (scheduler != null)
      scheduler.signalGoAhead(tag,senderId,force);
  }  

}
