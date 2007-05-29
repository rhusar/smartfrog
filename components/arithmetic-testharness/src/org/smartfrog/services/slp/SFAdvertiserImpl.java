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
 * @throw ServiceLocationException if the operation fails
 */

  public void register(ServiceURL serviceURL,Vector serviceLocationAttributes) throws ServiceLocationException{
    super.register(serviceURL,scopes,getLocale(),serviceLocationAttributes);
  }

/**
 * Add attributes to a service advertisement in the locale of this service advertiser
 * @param serviceURL the URL of the service
 * @param serviceLocationAttributes a vector of ServiceLocationAttribute objects to add to the advertisement
 * @throw ServiceLocationException if the operation fails
 */
  public void addAttributes(ServiceURL serviceURL, Vector serviceLocationAttributes) throws ServiceLocationException {
    super.addAttributes(serviceURL, scopes, getLocale(), serviceLocationAttributes);
  }

/**
 * Deregister the service advertisement in all configured scopes and for all
 * locales in which it was advertised.
 * @param serviceURL the URL of the service to deregister
 * @throw ServiceLocationException if the operation fails
 */
  public void deregister(ServiceURL serviceURL) throws ServiceLocationException {
    super.deregister(serviceURL,scopes);
  }

/**
 * Remove attributes from all service advertisement where they appear.
 * @param serviceURL the URL of the service advertised
 * @param serviceLocationAttributes the vector of ServiceLocationAttribute objects indicating the ids to remove from the advertisement
 * @throw ServiceLocationException if the operation fails
 */

  public void deleteAttributes(ServiceURL serviceURL,
                                       Vector serviceLocationAttributes) throws ServiceLocationException{
    super.deleteAttributes(serviceURL,scopes,serviceLocationAttributes);
  }
}
