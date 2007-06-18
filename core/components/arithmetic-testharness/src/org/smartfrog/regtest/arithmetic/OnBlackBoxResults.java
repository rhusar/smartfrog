package org.smartfrog.regtest.arithmetic;

import org.smartfrog.sfcore.workflow.eventbus.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.processcompound.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.services.slp.*;

import java.rmi.*;
import java.util.Vector;
import java.util.Enumeration;

import java.net.*;

public class OnBlackBoxResults extends OnResults implements Prim{
  static int outputNumber = 0;
  int time;
  Thread timer;
  ComponentDescription locateProviderDesc;

  static public String generateUniqueName() {
    String result = "output";
    try {
      result += InetAddress.getLocalHost().toString();
      result += SFProcess.getProcessCompound().sfCompleteName().toString();
    } catch (Exception e){e.printStackTrace();}
    result += (new Integer(outputNumber++)).toString();
    return result;
  }
  public OnBlackBoxResults() throws RemoteException { }
  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    time = ((Integer) sfResolve("timeout")).intValue();
    locateProviderDesc = (ComponentDescription) sfResolve("onTimeout");
  }
  public void sfStart() throws SmartFrogException, RemoteException {
      super.sfStart();
        // let any errors be thrown and caught by SmartFrog for abnormal termination  - including empty actions
      timer = new Thread(new Runnable() {
                public boolean running = true;
                public void run() {
                    if ( time > 0 ){
                      try {
                        Thread.sleep(time);
                        if (running)
                          triggerResourceDeployment();
                      } catch (Exception e) {}
                    }
                }
                public void stop(){
                  running = false;
                }
            });
      timer.start();
  }
  public void triggerResourceDeployment() throws Exception{
  // get the locator for the description provider

//    System.out.println( " Resource depl");
    Prim loc = ((Compound)sfParent()).sfDeployComponentDescription("descriptionLocator",(Compound)sfParent(), locateProviderDesc, null);
    loc.sfDeploy();
    loc.sfStart();
    try {
//    System.out.println( " Started "+loc.sfCompleteName());
    } catch (Exception e){e.printStackTrace();}
  // get the operation for which we want a description
    ComponentDescription resDesc = (ComponentDescription)loc.sfResolve("serviceNeeded");
    String type = (String) resDesc.sfContext().get("sfServiceType");
    String operation = "";
    if (type.indexOf("generator") == -1) {
//      System.out.println( " Generator description needed ");
    } else {
      operation = (String) ((ComponentDescription) resDesc.sfContext().get("sfServiceAttributes")).sfContext().get("op");
//      System.out.println( " Description needed is " +operation);
    }
    // get the description and deploy it.

    DescriptionProvider dp = (DescriptionProvider) loc.sfResolve("descriptionProvider");
//    System.out.println(" I have a description Provider "+ ((Prim)dp).sfCompleteName());
    ComponentDescription soughtAfter = dp.giveDesc(operation);
//    System.out.println(" Received " + soughtAfter);
    Prim opOrGen = ((Compound)sfParent()).sfDeployComponentDescription(null,null, soughtAfter, null);
    opOrGen.sfDeploy();
    opOrGen.sfStart();
//try{     System.out.println( " Generator deployed "+opOrGen.sfCompleteName());
//    } catch (Exception e){e.printStackTrace();}
  }

  /**
   * This default implementation triggers on the service Provider, if it is a Compound, the deployment
   * of a new output pointing to the provided input
   */
  public void triggerActionOn(Object serviceProvider) throws Exception{
    ContextImpl ctxt = null;
    Compound blackBox = (Compound) serviceProvider;
    Prim link = null;
    try {
      ctxt = new ContextImpl();
      ctxt.put("to",sfResolve("to"));
    } catch (Exception e){e.printStackTrace();}
    ComponentDescription outputDescription = (ComponentDescription) sfResolve("outputDescription");
    Prim p = blackBox.sfDeployComponentDescription(OnBlackBoxResults.generateUniqueName(), blackBox,outputDescription,ctxt);
    p.sfDeploy();
    p.sfStart();
    try {
      timer.stop();
      timer.interrupt();
//      System.out.println(" Found blackBox: "+ blackBox.sfCompleteName()+ "\n and deployed new Output: "+p.sfCompleteName());
    } catch (Exception e){}
  }
}
