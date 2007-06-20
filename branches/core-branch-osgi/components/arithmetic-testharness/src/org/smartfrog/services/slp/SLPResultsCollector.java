/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.services.slp;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.workflow.eventbus.*;
import org.smartfrog.sfcore.reference.*;

import java.rmi.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * A component to collect and store results provided by an SLPLocator.
 * Once an SLPLocator has been found, one of three different threads can be started :
 * - 'TryAndDie' thread that will query the locator for its results,
 *    enumerate other them and store, then terminate whether results were found or not.
 * - 'FindAndDie' thread that will query the locator until it gets non empty
 *    results, stores and signals them, then terminates.
 * - 'KeepLooking' thread that will keep on querying the locator for results
 *    and store and signal them.
 * Results of same service type are stored in the same vector. This vector itself
 * is stored in the component context under the service type name.
 * This class can be extended to convert the ServiceURL if appropriate. See
 * PrimResultsCollector for more details.
 */
public class SLPResultsCollector extends EventPrimImpl implements Prim{

  private SFSLPLocator locatorAccessPoint = null;
  private int retryInterval;
  private Vector allResults = new Vector(); //used to prevent redundancy.
  private Thread serviceURLFinder = null;
  private Reference name = null;
  public final static String stopSearch = "stopSearch";

  protected final String activeWaitString = "activeWait";
  protected final String tryAndDieString = "tryAndDie";
  protected final String keepLookingString = "keepLooking";

  private boolean sendServiceURLEvent = false;
  private boolean sendServiceAttributesEvent = false;
  private boolean sendServiceDeregistrationEvent = false;


  /** Reference used to look up the type of thread to be launched */
  protected static final Reference refThreadType = new Reference(ReferencePart.here("threadType"));
  /** Reference used to look up the retry interval of the thread to be launched */
  protected static final Reference refRetryInterval = new Reference(ReferencePart.here("retryInterval"));

  protected boolean display = false; // used to eventually print the results found.

  private abstract class ResearchThread extends Thread {
    protected Prim destination = null;
    protected SFSLPLocator locator;
    ResearchThread(){}
    /**
     * Thread constructor : Get the locator and fail if none specified.
     */
    ResearchThread(Prim destination){
      this.destination = destination;
      locator = getLocator();
      if (locator == null )
        throw new NullPointerException(" No locator specified ");
    }
    protected int resultCount()  {
      return allResults.size();
    }
    /**
     * Signal the arrival of a new result to any subscriber for this service type.
     */
    protected void signalResult(ServiceURL sURL) {
      SLPResultsCollector.this.storeResult(sURL,destination);
      String regEvent = "";
      if (sendServiceAttributesEvent) {
        try {
          String scopeString = (String) locator.sfResolve(SFSLPLocator.refScopes);
          Vector scopes = new Vector();
          for (StringTokenizer st = new StringTokenizer(scopeString,",");st.hasMoreElements();){
            scopes.addElement(st.nextElement());
          }
//          System.out.println("Requesting " + sURL + " with scopes "+ scopes);
          // send request for attributes of the url
          ServiceLocationEnumeration sla =
            locator.getLocator().findAttributes(sURL,scopes,null);//new Vector());
          Vector allAtts  = new Vector();
          while (sla.hasMoreElements()) {
            allAtts.addElement(sla.nextElement());
          }
//          String allAtt = "";
//          for (;sle.hasMoreElements();) {
//            allAtt+="("+((ServiceLocationAttribute) sle.nextElement()).toString()+")";
//          }
          regEvent = sURL.toString()+allAtts.toString();
        } catch (Exception e) { e.printStackTrace();}
      } else if (sendServiceURLEvent) {
        // send complete service url , but not the attributes
        regEvent  = sURL.toString();
      } else {
      // by default, send service type
        regEvent = sURL.getServiceType().toString();
      }
      if (display) System.out.println( "Sending event: Registration of "+ regEvent);

      SLPResultsCollector.this.sendEvent(regEvent);
    }
    protected void signalNoResult(ServiceURL sURL) {
      SLPResultsCollector.this.removeResult(sURL,destination);
      String unRegEvent = "unregister_";
      // send complete service url if boolean is set, otherwise just servicetype

      unRegEvent += ((sendServiceURLEvent)||(sendServiceAttributesEvent)) ?
            sURL.toString() :
            sURL.getServiceType().toString();
      if (sendServiceDeregistrationEvent) {
        SLPResultsCollector.this.sendEvent(unRegEvent);
        if (display) System.out.println( "Sending event: Service unregistered "+ unRegEvent);
      }
    }

    public abstract void stopThread();
  }

  private class TryAndDie extends ResearchThread {
    TryAndDie(Prim destination){
      super(destination);
    }
    public void run() {
      try{
        if (display) System.out.println("Starting TryAndDie thread for "+name);
        ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locator.sfResolve(SFSLPLocator.refResults);
        while (sle.hasMoreElements()){
          ServiceURL sURL =(ServiceURL) sle.nextElement();
          if (sURL != null) {
            signalResult(sURL);
          }
//          else {
//            break; // exit as soon as all elements have been signaled
//          }
        }
        if (display)  System.out.println("End of TryAndDie thread (started for : "+name+")");
      } catch (Exception ex) {
          ex.printStackTrace();
      }
    }
    public void stopThread() { // do nothing
    }
  }
  private class FindAndDie extends ResearchThread {
    boolean running = true;
    FindAndDie(Prim destination){
      super(destination);
    }
    public void run() {
      try{
        if (display) System.out.println("Starting ActiveWait thread for "+ name);
        while (running) {
          boolean gotResults = false;
          ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locator.sfResolve(SFSLPLocator.refResults);
          while (sle.hasMoreElements()){
//            ServiceURL sURL =(ServiceURL) sle.nextElement();
//            if (sURL != null) {
            signalResult((ServiceURL) sle.nextElement());
//            }
            stopThread();
          }
          Thread.sleep(retryInterval);
        }
        if (display) System.out.println("End of ActiveWait thread (Started for "+ name+")");
      } catch (Exception ex) {
          System.out.println(" Exception occured during search ");
          ex.printStackTrace();
          return;
      }
    }
    public void stopThread() {
      running = false;
    }
  }
  private class KeepLooking extends ResearchThread {
    boolean running = true;
    KeepLooking(Prim destination){
      super(destination);
    }
    public void run() {
      if (display) System.out.println("Starting KeepLooking thread for "+name);
      while (running) {
        try{
          ServiceLocationEnumeration sle = (ServiceLocationEnumeration) locator.sfResolve(SFSLPLocator.refResults);
          Vector oldAllResults = (Vector) allResults.clone();
          allResults = new Vector(); // will need to be rebuild before the end of the try
          while (sle.hasMoreElements()){
            ServiceURL sURL =(ServiceURL) sle.nextElement();
//            System.out.println( "examining " + sURL);
            if (sURL != null) {
              if (!allResults.contains(sURL)){ // allResults.elements() = sle
                allResults.addElement(sURL);
                if (!oldAllResults.contains(sURL)){ // send only if it hasn't been already sent
                  signalResult(sURL);
                }
              }
            }
          }
          // go through the former vector to
          for (Enumeration e = oldAllResults.elements() ; e.hasMoreElements() ; ){
            ServiceURL oldSURL = (ServiceURL) e.nextElement();
            if (!allResults.contains(oldSURL)){
              signalNoResult(oldSURL);
            }
          }
          Thread.sleep(retryInterval);
        } catch (Exception ex) {
          System.out.println(" Exception occured during 'KeepLooking' search ");
          ex.printStackTrace();
          stopThread();
        }
      }
    }
    public void stopThread() {
      running = false;
    }
  }

  /**
   * Default constructor
   */
  public SLPResultsCollector() throws RemoteException {
  }
    /**
     * Store a new result
     */
    protected void removeResult(ServiceURL sURL,Prim destination){
      Vector resultsForThisType = null;
      String keyName = sURL.getServiceType().toString().substring(ServiceType.servicePrefix.length());
      try{
        resultsForThisType = (Vector) destination.sfResolve(keyName);
        if (resultsForThisType.contains(sURL)) {
          resultsForThisType.removeElement(sURL);
          destination.sfReplaceAttribute(keyName,resultsForThisType);
        }
      } catch (Exception ex){
      }
    }

    /**
     * Store a new result
     */
    protected void storeResult(ServiceURL sURL,Prim destination){
      Vector resultsForThisType = null;
      String keyName = sURL.getServiceType().toString().substring(ServiceType.servicePrefix.length());
  //    System.out.println(" keyName "+ keyName);
      try{
        resultsForThisType = (Vector) destination.sfResolve(keyName);
//        if (display)  System.out.println(" Results already found: "+ resultsForThisType+ " size="+ resultsForThisType.size());
      } catch (SmartFrogResolutionException rex){
        resultsForThisType = new Vector();
//        if (display)  System.out.println(" First results found ");
        try {
          destination.sfReplaceAttribute(keyName,resultsForThisType);
        } catch (Exception anyE) {anyE.printStackTrace();}
      } catch (Exception ex){
        System.out.println( " Could not store new item :"+sURL);
        ex.printStackTrace();
        return;
      }
      if (display)
        try {
          System.out.println(" Storing new service of type : "+
            sURL.getServiceType().toString() + " in " + destination.sfCompleteName());
        } catch (Exception e) {}
      resultsForThisType.addElement(convert(sURL));
 //     System.out.println(" Results are now "+ resultsForThisType+ " size="+ resultsForThisType.size());

    }
  /**
   * Prints the serviceURL if 'display' is set to true.
   * Extend to convert the result if necessary.
   * @param sURL the serviceURL to convert.
   */
  public Object convert(ServiceURL sURL){
    if (display)
      try {
          System.out.println(sfCompleteName() + " Found " + sURL);
      } catch (Exception ex) {ex.printStackTrace();}
    return sURL;
  }

  /**
   * Creates the desired thread. Only one for this component's lifetime.
   * @param threadType : the type of thread desired. One of :
   *    'activeWait', 'tryAndDie', 'keepLooking's
   * @param destination : the component the thread will use to store the collected results.
   */
  private Thread getSearchThread(String threadType, Prim destination) {
    Thread finder = null;
    if (threadType.compareToIgnoreCase(keepLookingString)==0){
      finder = new KeepLooking(destination);
    } else if (threadType.compareToIgnoreCase(activeWaitString)==0) {
      finder = new FindAndDie(destination);
    } else if (threadType.compareToIgnoreCase(tryAndDieString)==0){
      finder = new TryAndDie(destination);
    }
    return finder;
  }

  /**
   * Return a pointer to the destination
   */
  public Prim getDestination(){
    Prim destination = null;
    try {
      destination = (Prim) this.sfResolve("destination");
    } catch (Exception e ){
      destination = this;
    }
    return destination;
  }
  /**
   * Return a pointer to the locator.
   */
  public SFSLPLocator getLocator() {
    if (locatorAccessPoint == null) {
      try {
        locatorAccessPoint = (SFSLPLocator) this.sfResolve("locator");
      } catch (SmartFrogResolutionException rex){
      } catch (Exception ex){
        System.out.println(" Failed to get Locator ");
        ex.printStackTrace();
      }
    }
    return locatorAccessPoint;
  }
  /**
   * On reception of an event, do not forward it.
   */
  synchronized public void event(String event) {
        handleEvent(event);
  }
 /**
   * Event handling : stop a query thread.
   * @param event : the event string
   */
  public void handleEvent(String event){
    try {
//      System.out.println( sfCompleteName() + " RECEIVED MESSAGE " + event);
    } catch (Exception ex){}
    if (event.equals(stopSearch)) {
      stopAllThreads();
    }
  }

  private void stopAllThreads() {
    // only one for the moment :
    ((ResearchThread)serviceURLFinder).stopThread();
  }
  /**
   * Get a pointer on the locator, and looks up 'retryInterval' (time in ms),
   */
  public void sfDeploy() throws SmartFrogException, RemoteException{
    super.sfDeploy();
    getLocator(); // initialize the locator if available.
    retryInterval = ((Integer) this.sfResolve(refRetryInterval)).intValue();
    try {
      name = sfCompleteName();
    } catch (Exception e) {e.printStackTrace();}
    try {
      sendServiceURLEvent = ((Boolean)sfResolveHere("sendURLEvent",false)).booleanValue();
      sendServiceAttributesEvent = ((Boolean)sfResolveHere("sendAttributesEvent",false)).booleanValue();
      sendServiceDeregistrationEvent = ((Boolean) sfResolveHere("sendDeregistration",false)).booleanValue();
    } catch (Exception e){
      System.out.println( "Default boolean values must be specified for sendURLEvent, sendAttributesEvent, and sendDeregistration");
      e.printStackTrace();
    }
    Object disp = sfResolveHere("display",false);
    if (disp != null) display = (disp instanceof String)?
        Boolean.valueOf((String)disp).booleanValue():
        ((Boolean)disp).booleanValue();
  }


  /**
   * Retrieve the results by launching the specified thread.
   */
  public void sfStart() throws SmartFrogException , RemoteException{
    super.sfStart();
//    System.out.println( "SLPResults Collector : Locator access point started ");
    serviceURLFinder = getSearchThread((String) this.sfResolve(refThreadType),getDestination());
    if (serviceURLFinder !=null) {
      serviceURLFinder.start();
    }
  }

  public void sfTerminateWith(TerminationRecord tr){
    // stop all threads that won't die by themselves.
    // or let them fail & die
//    try {
//      System.out.println(this.sfCompleteName() + " Stopping all threads");
//    } catch (Exception e) {e.printStackTrace();}
    stopAllThreads();
    super.sfTerminateWith(tr);
  }


}
