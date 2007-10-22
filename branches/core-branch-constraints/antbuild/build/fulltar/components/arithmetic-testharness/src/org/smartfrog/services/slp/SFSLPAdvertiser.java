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


import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;
import org.smartfrog.sfcore.componentdescription.*;

import java.rmi.*;
import java.net.*;
import java.util.*;
/**
 * Component that advertises services.
 * A component class to build a service type with its attributes from a
 * SmartFrog description and advertise them through SLP.
 * This Service Agent can be configured to point to a DA, by linking the
 * '' attribute to the mslpRef of a DA.
 * See SmartFrog's components example for more details
 *
 * @author Guillaume Mecheneau
 */


public class SFSLPAdvertiser extends EventPrimImpl implements Prim{
  /** Reference used to look up the service locale string */
  protected static final Reference refLocale =
    new Reference(ReferencePart.here("sfLocale"));

  /** Reference used to look up the service advertisement's scopes */
  protected static final Reference refScopes =
    new Reference(ReferencePart.here("sfAdvertisementScopes"));


  /** Reference used to look up the advertised serviceURL string */
  protected static final Reference refServiceURL =
    new Reference(ReferencePart.here("sfAdvertisementServiceURL"));
  /** Reference used to look up the attributes string of the advertisement */
  protected static final String refAttributes ="sfAdvertisementAttributes";
  /** Reference used to look up the lifetime of the advertisement */
  protected static final Reference refLifetime =
    new Reference(ReferencePart.here("sfAdvertisementLifetime"));

  /** The SLP ServiceURL that wil be advertised */
  ServiceURL serviceURL;

  /** The locale in which the service will be advertised */
  Locale locale = null;

  /** The message to advertise the described service */
  protected static final String registerMessage ="register";

  /** The message to deadvertise the described service */
  protected static final String deregisterMessage ="deregister";

 // private int dieAfter = 1000; // if oneShot is true, die after n ms
/**
 * Standard constructor.
 */
  public SFSLPAdvertiser() throws RemoteException {}

  /**
   * During this deploy phase, the scopes specified through the 'scopes' attribute (comma separated list)
   * are added to the list of scopes of this host's ServiceLocationManager.
   */
  public void sfDeploy() throws SmartFrogException , RemoteException{
    super.sfDeploy();
    try {
      String scopes = (String) sfResolve(refScopes);
      for (StringTokenizer st = new StringTokenizer(scopes,","); st.hasMoreElements();){
        ServiceLocationManager.addScope(st.nextToken());
      }
    } catch (SmartFrogResolutionException ignored){
    //if no scopes are specified use ServiceLocation Manager's scopes.
    }
    try {
      String language = (String) sfResolve(refLocale);
      locale = new Locale(language,"");
    } catch (SmartFrogResolutionException ignored) {
    }

  }

  /**
   * Start phase : the component will advertise the service described in the
   * description ('sfAdvertisementServiceURL' attribute) or die trying.
   * The 'sfAdvertisementLocale' attribute is used to specified the advertisement locale.
   * The 'oneShot' attribute can be set to true if the user wants the component
   * to be detached and terminated after the advertisement.
   *
   * N.B: the following are mslp (the current SLP implementation used) specifics:
   *  The 'useDA' attribute can point to an mslpRef of a DA to use it directly.
   *  The 'retry' attribute can be used to specify the number of times you want
   *    the ua to try and discover a DA.
   *
   * @exception Exception error while starting
   */
  public void sfStart() throws SmartFrogException , RemoteException{
    super.sfStart();
    // add here the registerOnStart test
    boolean advOnStart = false;
    Object advertiseOnStart = sfResolveHere("advertiseOnStart",false);
    if (advertiseOnStart != null)
      advOnStart = (advertiseOnStart instanceof String)?
          Boolean.valueOf((String)advertiseOnStart).booleanValue():((Boolean)advertiseOnStart).booleanValue();
    if (advOnStart) {
        try {
          advertise(true);
        }catch (Exception e) {
            throw new SmartFrogException(e);
        }
      // if oneShot is 'true' the advertiser will die after its advertisement
      boolean singleShot = false;
      Object oneShot = sfResolveHere("oneShot",false);
      if (oneShot != null)
        singleShot = (oneShot instanceof String)? Boolean.valueOf((String)oneShot).booleanValue():((Boolean)oneShot).booleanValue();
      if (singleShot) {
        Thread killer  = new Thread(new Runnable() {
          public void run() {
            try {
              sfDetachAndTerminate( new TerminationRecord("normal","Advertisement done",sfCompleteName()));
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        });
        killer.start();
      }
    }
  }
  /**
   * Return a ready-to-use advertiser with the locale specified for this component.
   * Proceed to any implementation-specific .
   * @return an instance of Advertiser.
   * @exception Exception error while querying for advertiser
   */
  public Advertiser getAdvertiser() throws Exception {
    // ServiceLocationManager can cope with a null locale
    Advertiser adv = ServiceLocationManager.getAdvertiser(locale);
    // ------------------------------
    // beginning of mslp-specifc code
    // the following two try block will only work with the mslp implementation.
    // The SLP standard java interface does not specify DA/UA interactions.
    try {
      String useDAName = (String) sfResolve("useDA"); // if a DA is specified, indicate the agent to use it.
//      System.out.println( "Using SF Reference to Directory Agent");
     ((SFuaf) adv).getDAList().addElement(useDAName); // add the da in the ua's list
//      System.out.println( "                      Directory Agent is "+ useDAName);
      ((SFuaf) adv).changeDA(useDAName); // ask the ua to use this one
    } catch (SmartFrogResolutionException rex){
    // if no ua specified, ask the UA to discover one
      ((SFuaf) adv).reDiscoverDA();
    } catch (Exception e){
      e.printStackTrace();
    }
    // set the number of retry if available.
    try {
      ((SFuaf) adv).setRetry(((Integer)sfResolve("retry")).intValue());
    } catch (SmartFrogResolutionException e){}
    // end of mslp-specifc code
    // ------------------------
    return adv;
  }
  /**
   * Register / deregister the service described in the component
   * @param register set to true if registration, false if deregistration.
   * @exception Exception error while advertising
   */
  public void advertise(boolean register) throws Exception{
    Advertiser adv = getAdvertiser();
    if (adv!=null) {
      // construct the SLP serviceURL.
      serviceURL = this.buildServiceURL();
      // advertise it
      if (register) {
        adv.register( serviceURL, this.buildAttributes());
      } else {
        adv.deregister( serviceURL ); //, this.buildAttributes());
      }
  } else {
      // terminate if no advertiser could be found
      this.sfTerminate( new TerminationRecord(  "abnormal",
                                                "Advertiser not implemented.",
                                                this.sfCompleteName()) );
    }
  }
  /**
   * Event handling : advertise or de-advertise.
   * @param event : 'register' or 'deregister'
   */
  public void handleEvent(String event){
    try {
      if (event.compareToIgnoreCase(registerMessage)==0){
        advertise(true);
      } else if (event.compareToIgnoreCase(deregisterMessage)==0){
        advertise(false);
      }
    } catch (Exception e) {
      sendEvent("registrationFailure");
    }
  }

  /**
  * Constructs an SLP serviceURL from the component description.
  *
  * The attributes used are 
  * <ol>
  * <li> sfAdvertisedServiceURL</li>
  * <li> sfAdvertisedLifetime</li>
  * <li> sfAdvertisedAttributes</li>
  * </ol>
  * If 'localhost' is found in the address, it is replaced by the actual name of the machine
  * @return the ServiceURL corresponding to the attributes
  * @throws Exception if one of the attributes is missing
  */
  public ServiceURL buildServiceURL() throws Exception {
    String serviceURLString;
    String hostname = "";
    serviceURLString = (String) this.sfResolve(refServiceURL); // mandatory !

    if (serviceURLString.indexOf("localhost")!=-1) {
      hostname = InetAddress.getLocalHost().getHostName();
      String deb = serviceURLString.substring(0,serviceURLString.indexOf("localhost"));
      String fin = serviceURLString.substring(serviceURLString.indexOf("localhost")+"localhost".length());
      serviceURLString = deb + hostname + fin;
     // System.out.println("Using address: "+ hostname);
    }


    // use default lifetime if not provided
    try {
      int lifetime = ((Integer) this.sfResolve(refLifetime)).intValue();
      return new ServiceURL( serviceURLString, lifetime);
    } catch (SmartFrogResolutionException rex ){
      return new ServiceURL( serviceURLString ) ;
    }

  }
  /**
   * Utility function to convert a comma separated list into a Vector
   *
   * @return a Vector of Strings
   */
  private Vector buildValues(String s) {
    Vector result = new Vector();
    for (StringTokenizer st = new StringTokenizer(s,","); st.hasMoreElements();){
      result.addElement(st.nextToken());
    }
    return result;
  }
  /**
   * If the 'serviceAttributes' attribute is present, extract and convert
   * the information into an SLP ServiceLocationAttributes vector
   *
   * @return a Vector of SLPAttributes
   */
  public Vector buildAttributes(){
    Vector atts = new Vector();
    try {
      Context cxt = ((ComponentDescription) this.sfContext().get(refAttributes)).sfContext();
      if (cxt!=null){
        for (Enumeration e = cxt.keys();e.hasMoreElements();){
          String name = e.nextElement().toString();
          Object baseValue = cxt.get(name);
          ServiceLocationAttribute sla = new ServiceLocationAttribute(name,buildValues(baseValue.toString()));
          atts.addElement(sla);
        }
      }
    } catch (Exception e){
      e.printStackTrace();
    }
 //   System.out.println( "attributes are "+atts);
    return atts;
  }
}
