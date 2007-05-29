package org.smartfrog.tools.testharness;
import java.rmi.RemoteException;
import java.rmi.Remote;


/** Coordinates the interactions of certain SF objets, by keeping
 * state of progress, and blocking clients that should not proceed
 * yet. Notification of progress is asynchronous in all cases. Tags
 * are used to group "common interest" objects.  A simple rule to allow
 * progress is based on the number of (possibly distinct) clients that
 * are happy with letting others continue.   
 *
 */
public interface Scheduler extends Remote {

  /** A tag to customize whether identical messages are just ignored. */
  static public final String REMOVE_DUPLICATES="removeDuplicates";

  /** A tag to customize the number of messages needed to change state
   * to GO_AHEAD. */ 
  static public final String NUMBER_OF_ACKS="numberOfAcks";
  
  
  /** Returns inmediately if the state corresponding to tag is
   * GO_AHEAD. Otherwise, it blocks until there is a transition to
   * that state. 
   *
   * @param tag A hint that allows the scheduler decide whether the
   * call should continue.
   * @exception RemoteException An error contacting the scheduler.
   */
  public void waitGoAhead(String tag) throws RemoteException;


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
                            boolean force) throws RemoteException;
}
