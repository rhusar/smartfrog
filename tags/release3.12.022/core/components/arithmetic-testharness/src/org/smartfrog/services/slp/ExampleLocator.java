package org.smartfrog.services.slp;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.workflow.eventbus.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.*;
import java.util.Vector;


public class ExampleLocator extends EventPrimImpl implements Prim{

  Prim locatorAccessPoint = null;
  int retryInterval;
  Vector allResults = new Vector();
  boolean activeWait = false;
  boolean keepLooking = false;
  private class TryAndDie extends Thread {
    public void run() {
      System.out.println("ExampleLocator : I'll try to find an answer and die whatever the result ");
      try{
        ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locatorAccessPoint.sfResolve(SFSLPLocator.refResults);
        while (sle.hasMoreElements()){
          ServiceURL sURL =(ServiceURL) sle.nextElement();
          if (sURL != null) {
            signalResult(sURL);
          } else {
            break;
          }
        }
      } catch (Exception ex) {
          ex.printStackTrace();
      }
      System.out.println("ExampleLocator :  Tried, and died : Exiting thread ");
    }
  }
  private class FindAndDie extends Thread {
    public void run() {
      System.out.println("ExampleLocator : I'll find an answer and die ");
      try{
        ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locatorAccessPoint.sfResolve(SFSLPLocator.refResults);
        while (sle.hasMoreElements()){
        // ServiceLocationEnumeration.next() triggers a new request each time it is called if no result has been found.
          ServiceURL sURL =(ServiceURL) sle.next();
          if (sURL != null) {
            signalResult(sURL);
          } else {
            Thread.sleep(retryInterval);
          }
        }
      } catch (Exception ex) {
          System.out.println(" Exception occured during search ");
          ex.printStackTrace();
          return;
      }
      System.out.println("ExampleLocator :  Found, and died : Exiting thread ");
    }
  }
  private class KeepLooking extends Thread {
    boolean running = true;
    public void run() {
      System.out.println("I'll keep looking ");
      while (running) {
        try{
          ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locatorAccessPoint.sfResolve(SFSLPLocator.refResults);
          while (sle.hasMoreElements()){
            ServiceURL sURL =(ServiceURL) sle.next();
            if (sURL != null) {
              if (!allResults.contains(sURL)){
                signalResult(sURL);
                allResults.addElement(sURL);
                System.out.println("ExampleLocator :  And here is all I have " + allResults);
              }
            } else {
              Thread.sleep(retryInterval);
            }
          }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      }
      System.out.println("ExampleLocator :  I kept Looking, but... : Exiting thread ");
    }
    public void stopThread() {
      running = false;
    }
  }

  /**
   * Default constructor
   */
  public ExampleLocator() throws RemoteException {
  }
  /**
   * Signal the arrival of a new result
   */
  protected void signalResult(ServiceURL sURL) {
    try {
      System.out.println(sfCompleteName() + " Found " + sURL);
    } catch (Exception ex) {ex.printStackTrace();}
    storeResult(sURL);
    this.sendEvent(sURL.getServiceType().toString());
  }
  /**
   * Store a new result
   */
  protected void storeResult(ServiceURL sURL){
    Vector resultsForThisType = null;
    try{
      resultsForThisType = (Vector) sfResolve(sURL.getServiceType().toString());
    } catch (SmartFrogResolutionException rex){
      resultsForThisType = new Vector();
      try {
        this.sfReplaceAttribute(sURL.getServiceType().toString(),
                                resultsForThisType);
      }catch (SmartFrogRuntimeException sfRex) {
          System.out.println("Run time exception :"+ sfRex); //Temporary Fix
      } catch (RemoteException re) {
          System.out.println("Remote Exception:"+ re);
      }
       // could specify a destination component other than 'this'
    } catch (Exception ex){
      System.out.println( " Could not store new item ");
      ex.printStackTrace();
      return;
    }
    resultsForThisType.addElement(convert(sURL));
  }
  /**
   * Convert the result if necessary
   */
  public Object convert(ServiceURL sURL){
    return sURL;
  }
  /**
   * Get a pointer on the locator
   */
    public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    retryInterval = ((Integer) this.sfResolve("retryInterval")).intValue();
    activeWait = Boolean.valueOf((String)sfResolve("activeWait")).booleanValue();
    keepLooking = Boolean.valueOf((String)sfResolve("keepLooking")).booleanValue();
    try {
      locatorAccessPoint = (Prim)this.sfResolve("locator");
    } catch (Exception e ){
      e.printStackTrace();
    }
  }


  /**
   * Retrieve the results.
   */
    public void sfStart() throws SmartFrogException, RemoteException {
    super.sfStart();
    System.out.println( "ExampleLocator : Locator access point started ");
    Thread serviceURLFinder = null;
    if (keepLooking){
      serviceURLFinder = new KeepLooking();
    } else if (activeWait) {
      serviceURLFinder = new FindAndDie();
    } else {
      serviceURLFinder = new TryAndDie();
    }
    serviceURLFinder.start();

  }
}
