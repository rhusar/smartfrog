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

import java.rmi.*;
import java.util.*;

/**
 * Utility class to adjust SLP Java API and columbia's mslp API.
 *
 * @author Guillaume Mecheneau
 */
public class SFAdvertiserImpl extends SFuaf implements Advertiser {

  /** The locale in which the advertisements will be made. English by default */
  Locale locale = Locale.ENGLISH;
  /** The scopes in which the advertisements will be done.*/
  Vector scopes;
  /**
   * Standard RMI constructor
   */
  public SFAdvertiserImpl() throws Exception {
    scopes = ServiceLocationManager.findScopes();
  }
  /**
   * Builds an advertiser with the specified locale
   */
  public SFAdvertiserImpl(Locale locale) throws Exception {
    scopes = ServiceLocationManager.findScopes();
    if (locale != null)
      this.locale = locale;
  }

/**
 * Return the current language locale
 */
  public Locale getLocale() {
    return locale;
  }

/**
 * Register the service advertisement in all configured scopes with the given
 * attributes and in the language locale the service agent was created.
 * @param serviceURL the complete URL of the service
 * @param serviceLocationAttributes a vector of ServiceLocationAttribute objects describing the service
 * @throws ServiceLocationException if the operation fails
 */

  public void register(ServiceURL serviceURL,Vector serviceLocationAttributes) throws ServiceLocationException{
    super.register(serviceURL,scopes,getLocale(),serviceLocationAttributes);
  }

/**
 * Add attributes to a service advertisement in the locale of this service advertiser
 * @param serviceURL the URL of the service
 * @param serviceLocationAttributes a vector of ServiceLocationAttribute objects to add to the advertisement
 * @throws ServiceLocationException if the operation fails
 */
  public void addAttributes(ServiceURL serviceURL, Vector serviceLocationAttributes) throws ServiceLocationException {
    super.addAttributes(serviceURL, scopes, getLocale(), serviceLocationAttributes);
  }

/**
 * Deregister the service advertisement in all configured scopes and for all
 * locales in which it was advertised.
 * @param serviceURL the URL of the service to deregister
 * @throws ServiceLocationException if the operation fails
 */
  public void deregister(ServiceURL serviceURL) throws ServiceLocationException {
    super.deregister(serviceURL,scopes);
  }

/**
 * Remove attributes from all service advertisement where they appear.
 * @param serviceURL the URL of the service advertised
 * @param serviceLocationAttributes the vector of ServiceLocationAttribute objects indicating the ids to remove from the advertisement
 * @throws ServiceLocationException if the operation fails
 */

  public void deleteAttributes(ServiceURL serviceURL,
                                       Vector serviceLocationAttributes) throws ServiceLocationException{
    super.deleteAttributes(serviceURL,scopes,serviceLocationAttributes);
  }
}
