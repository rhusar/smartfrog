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
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;

import java.rmi.*;
import java.net.*;
import java.util.*;

/**
 * A SmartFrog component to build a service type request and send it.
 * The service description and user agent characteristics (locale and scopes mainly)
 * are acquired during deployment.
 * Then any lookup on the 'sfLocationResults' attribute triggers a service type requests
 * whose results are returned as value of this attribute in the form of a ServiceLocationEnumeration.
 *
 * @author Guillaume Mecheneau
 */
public class SFSLPLocator extends PrimImpl implements Prim{
  /** Reference under which the ServiceLocationEnumeration results will be stored */
  public static final Reference refResults =
    new Reference(ReferencePart.here("sfLocationResults"));

  /** Reference used to look up the scopes configured for this locator */
  protected static final Reference refScopes =
    new Reference(ReferencePart.here("sfLocationScopes"));
  /** Reference used to look up the service description */
  protected static final Reference refServiceDescription =
    new Reference(ReferencePart.here("sfServiceDescription"));
  /** Reference used to look up the service locale string */
  protected static final Reference refLocale =
    new Reference(ReferencePart.here("sfLocale"));

  /** The service type to be found */
  private ServiceType serviceType;
  /** Locale of this host */
  private Locale locale = null;
  /** the scopes for this service agent */
  private Vector scopes;
  /** The service description data */
  private ComponentDescription serviceDescription;

  /** The results of requests */
  ServiceLocationEnumeration results = null;

  /** Standard constructor */
  public SFSLPLocator() throws RemoteException {}

/**
 * Look up the locale ('sfLocale' attribute) and serviceDescription ('sfServiceDescription'
 * attribute) for the service request.
 * Ignore if not found. (an exception will be thrown if query is made without one of those)
 * If scopes are not specified the scopes of the ServiceLocationManager of this host will be used
 */
  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    try {
      String language = (String) sfResolve(refLocale);
      locale = new Locale(language,"");
    } catch (Exception ex) {
    }
    try {
      serviceDescription = (ComponentDescription) sfResolve(refServiceDescription);
    } catch (SmartFrogResolutionException rex){
    }
    try {
      String scopeString = (String) sfResolve(refScopes);
      scopes = new Vector();
      for (StringTokenizer st = new StringTokenizer(scopeString,",");st.hasMoreElements();){
        scopes.addElement(st.nextElement());
      }
    } catch (SmartFrogResolutionException rex){
    }

  }
//
//  public void startDiscoveryThead() {
//    Thread discovery = new Thread(new Runnable() {
//        public void run() {
//          try {
// //           System.out.println(" Initializing results value ");
//            results = discoverService(
//                 (ComponentDescription)sfResolve(refServiceDescription));
//          } catch (Exception e) {
//            e.printStackTrace();
//          }
// //       System.out.println("Cache initialization completed ");
//        }
//      });
//    discovery.start();
//  }
/**
 * If the looked-up reference is sfLocationResult, triggers a service type request
 * based on the component's service description.
 */
  public Object sfResolve(Reference reference, int index) throws
                        SmartFrogResolutionException, RemoteException {
    ServiceLocationEnumeration res = null;
    try {
        if (refResults.toString().equals(reference.elementAt(index).
                                                             toString())){
          if ( results!=null) {
            res  = results;
          } else {
            res = discoverService((ComponentDescription)
                                    sfResolve(refServiceDescription));
            this.sfReplaceAttribute(refResults,res);
          }
          results = null; // only the first request may already have results ready.
          //return res;
    } else {
        return super.sfResolve(reference,index);
    }
    }catch (ServiceLocationException sle) {
        throw new SmartFrogResolutionException(sle);
    }catch (SmartFrogRuntimeException sfrex){
           throw new SmartFrogResolutionException(sfrex);
    }
    return res;
  }



/**
 * Builds an SLP service discovery query from the set of attributes provided
 * in the context of the component (the comparator is '='. To send a more complex
 * SLP query it has to be specified under 'sfServiceQuery' in full).
 * @param attributesRequirements a description of the attributes to include in the query.
 */
  public String buildServiceQuery(ComponentDescription attributesRequirements){
    String result = "";
    if (attributesRequirements != null) {
      Context cxt = attributesRequirements.sfContext();
      if (cxt.size()!=0) {
        for(Enumeration e = cxt.keys(); e.hasMoreElements();){
          String att = (String) e.nextElement();
          result = result.equals("")?result+"(&":result;
          result+= "("+att+"="+(String) cxt.get(att)+")";
        }
        result +=")";
      }
    }
    return result;
  }

  /**
   * Returns a Locator object for the locale specified for this component.
   * Performs any implementation-specific operations.
   * @return an object instance of Locator
   * @throws ServiceLocationException if operation fails.
   */
  public Locator getLocator() throws ServiceLocationException{
    Locator loco = ServiceLocationManager.getLocator(this.locale);
    // the following two try block will only work with the mslp implementation.
    // The SLP standard java interface does not specify DA/UA interactions.
    try {
      String useDAName = (String) sfResolve("useDA"); // if a DA is specified, indicate the agent to use it.
  //    System.out.println( "Using SF Reference to Directory Agent");
      ((SFuaf) loco).getDAList().addElement(useDAName); // add the da in the ua's list
   //   System.out.println( "                      Directory Agent is "+ useDAName);
      ((SFuaf) loco).changeDA(useDAName); // ask the ua to use this one
    } catch (SmartFrogResolutionException e){
      // if no da is specified, ask the UA to discover one
   //   System.out.println( "Discovering directory Agent");

      ((SFuaf) loco).reDiscoverDA();
    } catch (Exception e){
      e.printStackTrace();
      throw new ServiceLocationException(" Locator unavailable ");
    }
    // set the number of retry if available.
    try {
      ((SFuaf) loco).setRetry(((Integer)sfResolve("retry")).intValue());
    } catch (Exception e){}
    return loco;
  }
/**
 * The actual discovery of the service.
 * The serviceDescription object must contain at least:
 *  - sfServiceType : the type of service to be located
 * and can optionnaly specify one of the following two attributes:
 *  - sfServiceQuery : a String query on the services attributes, or :
 *  - sfServiceAttributes : a ComponentDescription describing a set of attribute/value pairs to be matched.
 */
  public synchronized ServiceLocationEnumeration discoverService(ComponentDescription serviceDescription) throws ServiceLocationException, RemoteException{
    Context serviceInfo = serviceDescription.sfContext();
    serviceType = new ServiceType((String) serviceInfo.get("sfServiceType"));
    if (!serviceType.isServiceURL()) {
      throw new ServiceLocationException(" URL is not a valid service URL ");
    }
    String serviceQuery = (serviceInfo.containsKey("sfServiceQuery"))?
                        (String) serviceInfo.get("sfServiceQuery"):
                        buildServiceQuery((ComponentDescription) serviceInfo.get("sfServiceAttributes"));
    if (scopes == null) {
      scopes = ServiceLocationManager.findScopes();
    }

    // retrieve the results.
  //  System.out.println(" Attempting to locate " + serviceType + " with scopes " + scopes + "matching "+ serviceQuery);
    ServiceLocationEnumeration results = getLocator().findServices(serviceType,scopes,serviceQuery);
    // and place them in the sfLocationResults attribute.
    try {
        this.sfReplaceAttribute(refResults,results);
    }catch (SmartFrogRuntimeException sfrex) {
        throw new ServiceLocationException (sfrex.getMessage());
    }
    return results;
  }

}
