
package org.smartfrog.services.slp;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.processcompound.PrimHostDeployerImpl;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.componentdescription.*;

import java.util.*;
import java.net.*;

/** Implements a specialized description deployer. This deployer uses
 *  a service description in the target component to find through SLP
 * the sfProcessHost and sfRootLocatorPort attributes. The remote
 * ProcessCompound is then located through standard process and the descriptions
 * are handed off to it.
 *
 * @author Guillaume Mecheneau
 */

public class PrimSLPDeployerImpl extends PrimHostDeployerImpl {
  /** Efficiency holders  */

  protected static final Reference refDeployerDesc =
    new Reference(ReferencePart.here("sfDeployerDescription"));
  protected static final Reference refDeployerLocale =
    new Reference(ReferencePart.here("sfDeployerLocale"));
  protected static final Reference refDeployerAttributes =
    new Reference(ReferencePart.here("sfDeployerAttributes"));
  protected static final Reference refDeployerScope =
    new Reference(ReferencePart.here("sfDeployerScopes"));
  public static String deployerServiceType = "smartfrogDeployer";

  /** Constructor
   *
   * @param descr target to operate on */
  public PrimSLPDeployerImpl(ComponentDescription descr) {
    super(descr);
  }
  public String buildServiceQuery(ComponentDescription attributesRequirements){
    String result = "";
    if (attributesRequirements != null) {
      Context cxt = attributesRequirements.sfContext();
      for(Enumeration e = cxt.keys(); e.hasMoreElements();){
        String att = (String) e.nextElement();
        result = result.equals("")?result+"(&":result;
        result+= "("+att+"="+(String) cxt.get(att)+")";
      }
      result +=")";
    }
    return result;
  }

/**
 * The actual discovery of the service.
 */
  public ServiceLocationEnumeration discoverService(ComponentDescription serviceDescription) throws ServiceLocationException{
    // initialize default values
    String deployerType = deployerServiceType;
    ServiceType serviceType = new ServiceType(deployerType);
    String language = "en";
    Vector scopes = new Vector();
    Locale locale = null;
    String serviceQuery = "";

    if (serviceDescription !=null) {
      // extract the locale & the deployer type
      //System.out.println(" --PrimSLPDeployerImpl-- Looking for deployer: " +serviceDescription);
      Context serviceInfo = serviceDescription.sfContext();
      deployerType = (String) serviceInfo.get("sfDeployerType");


      // get the locale of the service
      if (serviceInfo.containsKey("sfLocale")){
        language = (String) serviceInfo.get("sfLocale");
      } else { // default will be "en"
        System.out.println(" Default locale adopted for location : " + language);
      }
      // get the attributes desired and build the query
      serviceQuery = (serviceInfo.containsKey("serviceQuery"))?
                     (String) serviceInfo.get("serviceQuery"):
                     buildServiceQuery((ComponentDescription) serviceInfo.get("serviceAttributes"));
      // if scopes are not specified, use the ServiceLocationManager's
      try {
        for (StringTokenizer st = new StringTokenizer((String) serviceInfo.get("scopes"),","); st.hasMoreElements();){
          scopes.addElement((String) st.nextToken());
        }
      } catch (Exception ex) {
      }
    }
    if (scopes.isEmpty()) scopes = ServiceLocationManager.findScopes();
//    System.out.println("language " + language);
    locale = new Locale(language,"");
    if (deployerType.indexOf(ServiceType.servicePrefix)==-1)
        deployerType = ServiceType.servicePrefix+deployerType ;
    serviceType = new ServiceType(deployerType);
//System.out.println(" locale " + locale );
//System.out.println(" setyp "+ serviceType);
//
//System.out.println(" Scopes "+ scopes);
    // get the locator
    Locator loco = ServiceLocationManager.getLocator(locale);
    return loco.findServices(serviceType,scopes,serviceQuery);
  }
  /** Returns a process compound corresponding  to the description
   *  in the component. SLP is used to get the actual host and process name.
   *  They are then handed over to standard ProcessCompound location.
   *
   * @return process compound on host with name
   * @exception Exception failed to find process compound */
  protected ProcessCompound getProcessCompound() throws Exception {
    ComponentDescription deployerDescription = null;
    ServiceURL deployerURL = null;
    // check if the local is advertised as deployer.
    ProcessCompound localpc = SFProcess.getProcessCompound();
    //ServiceURL sURL = new ServiceURL(sfAdvertisementServiceURL);

    try {
      deployerDescription = (ComponentDescription) target.sfResolve(refDeployerDesc);
      // take the first of the enumeration returned.
    } catch (SmartFrogResolutionException resex) {
      System.out.println("Adopting standard deployer type : "+ deployerServiceType);
    }
    Enumeration pcEnum = this.discoverService(deployerDescription);
    if (pcEnum.hasMoreElements()){
      deployerURL = (ServiceURL) pcEnum.nextElement();
    }
    if (deployerURL == null){
      throw new Exception("No suitable process compound found : aborting ");
    } else {
      String deployerType = (String) localpc.sfContext().get("advertisedAs");
        // if the process compound has been advertised with this service type, return it.
      if (deployerURL.getServiceType().equals(deployerType)){
        return localpc;
      }
      String host = deployerURL.getHost();
      int port = deployerURL.getPort();
     // System.out.println("Attempting deployment on host: " + deployerURL.toString()+"\n HOST"+ host+ " \n port"+port   );
      InetAddress hostAddress = null;
      try {
        hostAddress = InetAddress.getByName(host);
      } catch (Exception resex) {
        throw new Exception("Deployer host of service provider unreachable.");
      }
     // String sfProcessComponentName = deployerURL.getURLPath();
      ProcessCompound pc = null;
      if (port!=0)
        pc = SFProcess.getRootLocator().getRootProcessCompound(hostAddress, port);
      else
        pc = SFProcess.getRootLocator().getRootProcessCompound(hostAddress);
     /* if (!sfProcessComponentName.equals("")) { // left for later.
                                For the momen only RootPC is advertised/located
        pc = pc.sfResolveProcess(sfProcessComponentName);
      }*/
      return pc;
    }
  }
}
