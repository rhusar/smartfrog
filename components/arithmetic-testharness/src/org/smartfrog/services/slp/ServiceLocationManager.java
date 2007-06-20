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

import java.util.*;
import org.smartfrog.sfcore.security.SFClassLoader;
/**
 * The Service Location manager manages access to the service location framework.
 * Clients obtain the Locator (UA) and Advertiser (SA) , and a Vector of
 * known scope names from the service location manager.
 * In this implementation the ServiceLocationManager (SLM) will not be created on its
 * own but merely statically called by UAs and SAs. Therefore scopes configuration
 * will be made through UAs and SAs objects configuration and not by the SLM constructor.
 * N.B: For the moment and for simplicity reasons, only one locale is allowed per JVM.
 *
 * @author Guillaume Mecheneau
 */
public class ServiceLocationManager {

  /** The SLP default scope */
  public static String defaultScope = "DEFAULT";

  /** The vector of configured scopes for this host */
  public static Vector scopes;

  /** The current locator instance number */
  protected static int locInstCount = 0;

  /** The current advertiser instance number */
  protected static int advInstCount = 0;

  /** The current locator instance */
  protected static Locator locator;
  /** The current advertiser instance */
  protected static Advertiser advertiser;

  /** The default locator class */
  protected static final String defaultLocatorClass     = "org.smartfrog.services.slp.SFLocatorImpl";
  /** The default advertiser class */
  protected static final String defaultAdvertiserClass  = "org.smartfrog.services.slp.SFAdvertiserImpl";

  /** The locator class */
  protected static String locatorClassName = defaultLocatorClass;
  /** The advertiser class */
  protected static String advertiserClassName = defaultAdvertiserClass;
  protected static boolean displayMSLPTrace = true;
  private static Locale lastLocatorLocale;
  private static Locale lastAdvertiserLocale;


  /**
   * Returns a vector of strings with all configured scope names.
   * There is no active scope discovery.
   * There is always at least one String in the Vector, the default scope, "DEFAULT".
   * @return a Vector of configured scopes.
   * @throws ServiceLocationException if the operation fails.
   */
  public static Vector findScopes() throws ServiceLocationException {
    if (scopes == null)
      addScope(defaultScope);
    return scopes;
  }

  /**
   * SmartFrog-launched UAs and SAs may use this function to add their
   * configured scope (in the SF files) to the serviceLocationManager for one host.
   * Every UA or SA on this host (JVM) will then use these scopes.
   * @param scope the scope to add to the list.
   */
  public static void addScope(String scope){
    if (scopes == null)
      scopes = new Vector();
    if (!scopes.contains(scope)){
      scopes.addElement(scope);
    }
  }
  /**
   * Default constructor
   */
  public ServiceLocationManager() {
  }
  /**
   * Returns an instance of the Locator or Advertiser class specified.
   * @param agentClassName the class of the Agent to instantiate.
   * @param locale the locale object for which this agent will be configured.
   * @return an instance of User or Service Agent.
   * @throws Exception if the operation fails.
   */
  private static Object instantiateSLPAgent(String agentClassName, Locale locale) throws Exception{
    Class[] parameters = new Class[1];
    Object[] args = new Object[1];
    parameters[0] = SFClassLoader.forName("java.util.Locale");
    args[0] = locale;
    // should probably be spawned in a new Thread...
    return SFClassLoader.forName(agentClassName).getConstructor(parameters).newInstance(args);
  }

  /**
   * Return an instance of the Locator class specified in the locatorClassName
   * with the specified locale.
   * @param locale the locale object for which this user agent will be configured.
   * @return an instance of User Agent.
   * @throws ServiceLocationException if the operation fails.
   */
  public static Locator getLocator(Locale locale) throws ServiceLocationException {
    if (locale == null)
        throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
    // instantiate only if necessary . Worth it ?
    if((locator == null)||(!locale.equals(lastLocatorLocale)) ){
      try {
        locator = (Locator) instantiateSLPAgent(locatorClassName,locale);
        lastLocatorLocale = locale;
      } catch (Exception e) {
        throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
      }
    }
    return locator;
  }

 /**
  * Return an instance of the Advertiser class specified in the locatorClassName
  * with the specified locale.
  * @param locale the locale object for which this service agent will be configured.
  * @return an instance of Service Agent.
  * @throws ServiceLocationException if the operation fails.
  */
  public static Advertiser getAdvertiser(Locale locale) throws ServiceLocationException {
    if (locale == null)
      throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
    if((advertiser == null)||(!locale.equals(lastAdvertiserLocale)) ){
     try {
        advertiser = (Advertiser) instantiateSLPAgent(advertiserClassName,locale);
        lastAdvertiserLocale = locale;
      } catch (Exception e) {
        throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
      }
    }
    return advertiser;
  }
}
