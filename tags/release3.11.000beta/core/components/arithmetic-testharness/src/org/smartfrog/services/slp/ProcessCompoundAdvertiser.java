package org.smartfrog.services.slp;


import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.processcompound.*;

import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * A SmartFrog component to advertise components using the SLP utility.
 *
 * @author Guillaume Mecheneau
 */
public class ProcessCompoundAdvertiser extends SFSLPAdvertiser implements Prim{

  /** Reference used to lookup the deployer type */
  protected static final Reference refDeployerType =
    new Reference(ReferencePart.here("sfDeployerType"));

  private String deployerType;
  private String serviceURL;
  /** Standard constructor */
  public ProcessCompoundAdvertiser() throws RemoteException {
  }

  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    serviceURL = (String)sfResolve(refServiceURL,"",false);
    // if this serviceURL is empty, build a new one with the deployer type
    if (serviceURL.length()==0) {
      // get the deployer type
      try {
        deployerType = (String) sfResolve(refDeployerType);
      } catch (Exception e){
        deployerType = PrimSLPDeployerImpl.deployerServiceType;
      }
      // build the service URL to advertise, and replace it into the context.
      serviceURL = ServiceType.servicePrefix+deployerType+"://localhost/";
      sfContext().put("sfAdvertisementServiceURL",serviceURL);
    }
  }


/**
 * The service URL advertised by this component should only Advertise the root Process Compound.  Then flag the process compound to mark
 * it as advertised . For further SLP-based deployment, this allows the PC
 * to check if itself is a deployer of the type required before querying
 * for a list of ProcessCompound advertised as deployers with the right type.
 * (In this case, the 'sfServiceType' in the process compound has to match
 * the 'sfDeployerType' in the 'sfDeployerDescription' required for SLP-based
 * deployment)
 */
  public void sfStart() throws SmartFrogException , RemoteException{
    // get the process compound
    ProcessCompound pc = SFProcess.getProcessCompound();
    //ServiceURL sURL = new ServiceURL(serviceURL);
    pc.sfContext().put("advertisedAs",deployerType);
    super.sfStart();
  }
}
