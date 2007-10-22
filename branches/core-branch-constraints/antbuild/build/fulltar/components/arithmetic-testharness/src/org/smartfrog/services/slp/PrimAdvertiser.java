package org.smartfrog.services.slp;


import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.processcompound.*;
import org.smartfrog.sfcore.componentdescription.*;

import sun.misc.BASE64Encoder;
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
public class PrimAdvertiser extends SFSLPAdvertiser implements Prim{
  /** The components to advertise */
  private Context advertised;
   /** Reference used to lookup the components to advertise */
  public static final Reference refToAdvertise =
    new Reference(ReferencePart.here("toAdvertise"));
   /** Reference used to lookup the service url prefix */
  public static final Reference refSURLPrefix =
    new Reference(ReferencePart.here("sfAdvertisementServiceURL"));
  /** The service url; prefix String that all the advertised components will use */
  private String serviceURLPrefix;
  /** Standard constructor */
  public PrimAdvertiser() throws RemoteException {
  }
/**
 * Look up the sfAdvertised attributes and store the corresponding
 * ComponentDescription's context.
 */
  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    advertised = ((ComponentDescription) this.sfResolve(refToAdvertise)).sfContext();
    serviceURLPrefix = (String) sfResolve(refSURLPrefix);
  }
  /**
   * Encode a remote reference.
   * @param ref the reference to encode.
   * @return the 64-encoded string representing the reference.
   */
  protected String encodedReference(Object ref) throws Exception{
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(os);
    oos.writeObject(ref);
    oos.close();
    BASE64Encoder encoder = new BASE64Encoder();
    String objRef64enc =encoder.encode(os.toByteArray());
    os.flush();
    os.close();
    return objRef64enc;
  }
/**
 * Advertise the linked components .
 * @param register : set to true to advertise, to false to deregister the components
 */
  public void advertise(boolean register) throws Exception{
    Advertiser adv = getAdvertiser();
    if (adv!=null) {
      // enumerate other the component to advertise.
      for (Enumeration e = advertised.keys() ; e.hasMoreElements() ; ){
        // find the component in the advertiser's context
        Prim p = (Prim) sfResolve((Reference)advertised.get(e.nextElement()));
        // encode and ship the RemoteReference of this component
        String encodedRef  = "";
        if (p instanceof PrimImpl) { //local object --> we need to get the reference
          encodedRef = encodedReference(((PrimImpl)p).sfExportRef());
        } else if (p instanceof RemoteStub) { // remote object --> just encode the stub
          encodedRef = encodedReference(((RemoteStub)p));
        }
        // replace the current url by the extended one
        sfContext().put("sfAdvertisementServiceURL",serviceURLPrefix+encodedRef);
        // resume normal advertisement :
        // construct the SLP serviceURL.
        serviceURL = this.buildServiceURL();
        // advertise it
        try {
          if (register) {
            adv.register( serviceURL, this.buildAttributes());
          } else {
            adv.deregister( serviceURL );
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    } else {
      // terminate if no advertiser could be found
      this.sfTerminate( new TerminationRecord(  "abnormal",
                                                "Advertiser not implemented.",
                                                this.sfCompleteName())        );
    }
  }

}
