package org.smartfrog.services.slp;


import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.processcompound.*;

import sun.misc.*;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * This component converts the SLP results it obtains into SmartFrog
 * components references.
 *
 * @author Guillaume Mecheneau
 */
public class PrimResultsCollector extends SLPResultsCollector implements Prim{
//  private Vector allSemas = new Vector();
  private Prim serviceComponent    = null;
  private Object sema = new Object();
  private void waitForComponent(){
    synchronized(sema) {
      try{
        if (serviceComponent==null) {
          sema.wait();
        }
      }catch(Exception ex){}
    }
  }

  private void notifyComponent(){
    synchronized(sema) {
      try{
        sema.notifyAll();
      }catch(Exception ex){}
    }
  }


  private boolean storeLastOnly = false;
  /** Standard constructor */
  public PrimResultsCollector() throws RemoteException {}
  /**
   * Look up for the context storage attribute.
   */
  public void sfDeploy() throws SmartFrogException , RemoteException{
    super.sfDeploy();
    Object storage = sfResolveHere("storeLastOnly",false);
    if (storage != null) {
       storeLastOnly = (storage instanceof String) ? Boolean.valueOf((String)storage).booleanValue(): ((Boolean)storage).booleanValue();
    }
  }
/**
 * If the looked-up reference is sfLocationResult, triggers a service type request
 * based on the component's service description.
 */
  public Object convert(ServiceURL sURL){
    Prim p = null;
    InetAddress hostaddress = null;
    try {
      hostaddress = InetAddress.getByName(sURL.getHost());
    }
    catch (Exception ex) {
      return sURL;
    }
    // collect SmartFrog information : host, processName, component reference
    try {
      String objectReference = sURL.getURLPath();
      // remove the / at the start of the url path !
      if (objectReference.startsWith("/")) objectReference = objectReference.substring(1);
      BASE64Decoder decoder = new BASE64Decoder();
      byte[] byteArray = decoder.decodeBuffer(objectReference);
      ByteArrayInputStream isr = new ByteArrayInputStream(byteArray);
      ObjectInputStream ois = new ObjectInputStream(isr);
      RemoteStub b = (RemoteStub) ois.readObject();
      ois.close();
      p =(Prim) b;
    } catch (Exception e) {
      System.out.println(" Couldn't get a reference for prim " + e);
      e.printStackTrace(); // to be commented later
      return sURL;
    }
    if (display)
      try {
          System.out.println("PrimResultsCollector " + sfCompleteName() + " Found " + p.sfCompleteName());
      } catch (Exception ex) {ex.printStackTrace();}
    return p;
  }
  /**
   * Store a new result
   */
  protected void storeResult(ServiceURL sURL,Prim destination){
    super.storeResult(sURL,destination); // store all results conventionnaly
    // store the last one under "serviceComponent";
    try{
      if (display) System.out.println("Updating serviceComponent attribute");
      serviceComponent = (Prim) convert(sURL);
      destination.sfReplaceAttribute("serviceComponent",serviceComponent);
      notifyComponent();
    }catch(Exception ex){}

  }
  public Object sfResolve(Reference r, int index) throws
                               SmartFrogResolutionException, RemoteException {
    if ("serviceComponent".equals(r.elementAt(index).toString())) {
      if (display) System.out.println("Component requested. Hold on...");
      if (serviceComponent == null) {
        waitForComponent();
      }
      if (display) System.out.println("Component returned " + serviceComponent.sfCompleteName());
      return serviceComponent;
    } else {
      return super.sfResolve(r, index);
    }
  }

}
