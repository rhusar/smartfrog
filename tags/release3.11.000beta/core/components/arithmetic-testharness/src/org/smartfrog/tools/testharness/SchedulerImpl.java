package org.smartfrog.tools.testharness;
import java.rmi.RemoteException;
import java.util.HashMap;


/** Coordinates the interactions of certain SF objets, by keeping
 * state of progress, and blocking clients that should not proceed
 * yet. Notification of progress is asynchronous in all cases. Tags
 * are used to group "common interest" objects.  A simple rule to allow
 * progress is based on the number of (possibly distinct) clients that
 * are happy with letting others continue.   
 *
 */
public class SchedulerImpl implements Scheduler {

 /** A flag to customize whether identical messages are just ignored. */
  boolean removeDuplicates = true;

  /** The number of clients that should agree before a check can proceed. */
  int numberOfAcks = 1;

  /** A hashmap that stores all the GO_AHEAD requests to avoid duplicates. */ 
  HashMap duplicates = new HashMap();

  /** A hasmap that keeps progress state of tags */
  HashMap tagsStatus = new HashMap();


  /**
   * Class Constructor.
   *
   * @exception RemoteException Cannot contact the scheduler.
   */
  public SchedulerImpl(boolean removeDuplicates, int numberOfAcks)
    throws RemoteException {

    this.removeDuplicates = removeDuplicates;
    this.numberOfAcks = numberOfAcks;

    if (removeDuplicates)
       this.duplicates = new HashMap();
    this.tagsStatus = new HashMap();
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

    TagInfo tagInfo;
    /* We want to avoid having more than one entry, and also hashmap
     * is NOT synchronized.*/
    synchronized(this) {
      tagInfo = (TagInfo) tagsStatus.get(tag);
	if (tagInfo !=null)
      	 System.out.println("TAG " + tag + " in hash");
	else 
      	 System.out.println("TAG "  + tag + " not in hash");
	if (tagInfo == null) {
        // Adding a new entry but with 0 acks.
        tagInfo = new TagInfo(numberOfAcks);
        tagsStatus.put(tag,tagInfo);
      }
    }

    synchronized (tagInfo) {
      if (tagInfo.isDone()) {
        return;
      } else {
        try {
          System.out.println("Scheduler:waitGoAhead blocking "+tag);//DEBUG
          tagInfo.wait();
          System.out.println("Scheduler:waitGoAhead releasing "+tag);//DEBUG
	  if (!tag.equals("waitForDaemons")) {
	     System.out.println("Removing " + tag);
	     tagsStatus.remove(tag); // added
           }
        } catch (Exception e) {
          
        }
      }    
    }
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

    System.out.println("Scheduler:signalGoAhead  "+tag + " from "+senderId 
                       + " force "+force);//DEBUG
    TagInfo tagInfo;
    /* Protect hasmaps and make sure that only one entry per tag.*/
    synchronized(this) {
     /* if (removeDuplicates) {
        Boolean temp = new Boolean(force);
        String key = tag+senderId+ temp;
        if (duplicates.containsKey(key))
          return;
        else
          duplicates.put(key,null);
      }*/

      tagInfo = (TagInfo) tagsStatus.get(tag);
      if (tagInfo == null) {
        // Adding a new entry ...
        tagInfo = new TagInfo(numberOfAcks);
        tagsStatus.put(tag,tagInfo);
      }
    }

    synchronized(tagInfo) {
	System.out.println("INSIDE SYNCHRONIZED BLOCK");
      tagInfo.incAcks();
      if (force) 
        tagInfo.forceDone();    
      if (tagInfo.isDone()) {
        /* We've already grabbed a lock to "this", so no need to grab
         * the lock on tagInfo earlier...*/ 
        tagInfo.notifyAll();
      }
    }
  }

  /** A convenience class that encapsulates tag state information.
   *
   */
  private class TagInfo {
    int numAcks;
    int maxAcks;
    boolean done = false;

    public TagInfo(int maxAcks) {
      this.numAcks = 0;
      this.maxAcks = maxAcks;
    }

    public void incAcks() {
      numAcks++;
      if (numAcks == maxAcks) 
        done = true;
    }

    public void forceDone() {
      done = true;
    }

    public boolean isDone() {
      return done;
    }
  }
}

