package org.smartfrog.services.slp;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.*;
import java.util.Vector;

/**
 * This event prim registers with a given slp locator and sends 
 * event to subscribers each time a service of the given type has been found.
 *
 * @author Guillaume Mecheneau
 */

public class LocatorAnnouncer extends EventPrimImpl implements Prim{
  Prim locatorAccessPoint = null;
  int retryInterval;
  Vector allResults = new Vector();
  boolean activeWait = false;
  boolean keepLooking = false;
  private class TryAndDie extends Thread {
    public void run() {
      System.out.println("Locator : I'll try to find an answer and die whatever the result ");
      try{
        ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locatorAccessPoint.sfResolve(SFSLPLocator.refResults);
        while (sle.hasMoreElements()){
          ServiceURL sURL =(ServiceURL) sle.nextElement();
          if (sURL != null) {
            System.out.println(sfCompleteName() + " Found " + sURL);
          } else {
            break;
          }
        }
      } catch (Exception ex) {
          ex.printStackTrace();
      }
      System.out.println("Locator :  Tried, and died : Exiting thread ");
    }
  }
  private class FindAndDie extends Thread {
    public void run() {
      System.out.println("Locator : I'll find an answer and die ");
      try{
        ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locatorAccessPoint.sfResolve(SFSLPLocator.refResults);
        while ((sle!=null)&&(sle.hasMoreElements())){
        // ServiceLocationEnumeration.next() triggers a new request each time it is called if no result has been found.
          ServiceURL sURL =(ServiceURL) sle.next();
          if (sURL != null) {
            System.out.println(sfCompleteName() + " Found " + sURL);
          } else {
            Thread.sleep(retryInterval);
          }
        }
      } catch (Exception ex) {
          System.out.println(" Exception occured during search ");
          ex.printStackTrace();
          return;
      }
      System.out.println("Locator :  Found, and died : Exiting thread ");
    }
  }
  private class KeepLooking extends Thread {
    boolean running = true;
    public void run() {
      System.out.println("I'll keep looking ");
      while (running) {
        try{
          ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locatorAccessPoint.sfResolve(SFSLPLocator.refResults); // can be extremely costly !
          while (sle.hasMoreElements()){
            ServiceURL sURL =(ServiceURL) sle.next();
            if (sURL != null) {
              if (!allResults.contains(sURL)){
                System.out.println(sfCompleteName() + " Found " + sURL);
                allResults.addElement(sURL);
                System.out.println("Locator :  And here is all I have " + allResults);
              }
            } else {
              Thread.sleep(retryInterval);
            }
          }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      }
      System.out.println("Locator :  I kept Looking, but... : Exiting thread ");
    }
    public void stopThread() {
      running = false;
    }
  }

  /**
   * Default constructor
   */
  public LocatorAnnouncer() throws RemoteException {
  }
  /**
   * Get a pointer on the locator
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      retryInterval = ((Integer) this.sfResolve("retryInterval")).intValue();
      activeWait = Boolean.valueOf((String) sfResolve("activeWait")).booleanValue();
      keepLooking = Boolean.valueOf((String) sfResolve("keepLooking")).booleanValue();
      try {
          locatorAccessPoint = (Prim) this.sfResolve("locator");
      } catch (Exception e) {
          e.printStackTrace();
      }
  }


    /**
   * Retrieve the results attribute in
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        System.out.println("ExampleLocator : Locator access point started ");
        Thread serviceURLFinder = null;
        if (keepLooking) {
            serviceURLFinder = new KeepLooking();
        } else if (activeWait) {
            serviceURLFinder = new FindAndDie();
        } else {
            serviceURLFinder = new TryAndDie();
        }
        serviceURLFinder.start();

    }
}
