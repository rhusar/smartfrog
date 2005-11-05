package org.smartfrog.services.slp;

import java.rmi.*;
import java.util.*;

/**
 * Utility class to adjust SLP Java API and columbia's mslp API.
 *
 * @author Guillaume Mecheneau
 */
public class SFLocatorImpl extends SFuaf implements Locator {
  /** The locale of this locator. English by default */
  Locale locale = Locale.ENGLISH;
  /** The scopes in which the service location will be done.*/
  Vector scopes;

  /**
   * Create a locator with default locale
   */
  public SFLocatorImpl() throws Exception {
    scopes = ServiceLocationManager.findScopes();
  }
  /**
   * Create a locator with the specified locale.
   */
  public SFLocatorImpl(Locale locale) throws Exception {
    scopes = ServiceLocationManager.findScopes();
    if (locale != null)
      this.locale = locale;
  }

  /**
   * Return the locale in which this locator was created
   */
  public Locale getLocale() {
    return locale;
  }

 /**
 * Return an enumeration of ServiceURL objects for service matching the query in
 * the locale of this Locator.
 * @param serviceType the ServiceType to be found
 * @param scopeNames the scopes in which the services should be found
 * @param query the query on service attributes
 * @return an Enumeration of ServiceURL objects matching the query
 * @throw ServiceLocationException if the operation fails
 */
 public ServiceLocationEnumeration findServices(ServiceType serviceType,Vector scopeNamess,String query) throws ServiceLocationException {
    return super.findServices(serviceType,scopes,locale,query);
  }
 /**
 * Returns an enumeration of known service types for this scope and naming authority.
 * Unless a proprietary or experimental service is being discovered,
 * the namingAuthority parameter should be null.
 * @param namingAuthority the naming authority of the service types to be found
 * @param scopeNames the scopes in which the service types should be found
 * @return an Enumeration of ServiceType objects matching the query
 * @throw ServiceLocationException if the operation fails
 */
  public ServiceLocationEnumeration findServiceTypes(String namingAuthority,Vector scopeNames) throws ServiceLocationException {
    if (namingAuthority == null) {
      namingAuthority = ServiceType.IANA;
    } else if (namingAuthority.equals(ServiceType.IANA)) {
    }
    return super.findServiceTypes(namingAuthority,scopes);
  }
/**
 * Returns an enumeration of the attributes and their values for the given
 * ServiceURL in the locale of this Locator.
 * @param serviceURL the ServiceURL whose attributes are required
 * @param scopeNames the scopes of the service
 * @param attributeIds  a vector of the desired service attributes (empty if you want them all)
 * @return a ServiceLocationEnumeration of ServiceLocationAttribute objects matching the attributeIds
 * @throw ServiceLocationException if the operation fails
 */
  public ServiceLocationEnumeration findAttributes(ServiceURL serviceURL, Vector scopeNames, Vector attributeIds) throws ServiceLocationException {
    return super.findAttributes(serviceURL,scopes,locale,attributeIds);
  }
 /**
 * Return an enumeration of all attributes for all serviceURLs having this
 * service type in the specified locale.
 * The attributes id returned match the id patterns in the parameter Vector
 * @param serviceType the type of the service
 * @param scopeNames the scopes of the service type
 * @param attributeIds  a vector of strings identifying the desired service attributes
 * @return a ServiceLocationEnumeration of ServiceLocationAttribute objects matching the attributeIds
 * @throw ServiceLocationException if the operation fails
 */
  public ServiceLocationEnumeration findAttributes(ServiceType serviceType,Vector scopeBNames, Vector attributeIds) throws ServiceLocationException {
    return super.findAttributes(serviceType,scopes,locale, attributeIds);
  }


}
