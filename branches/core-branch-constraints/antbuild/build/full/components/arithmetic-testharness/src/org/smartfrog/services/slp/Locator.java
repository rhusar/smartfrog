package org.smartfrog.services.slp;

import java.util.*;
/**
 * The Locator interface allows user agent objects to query for service URLs,
 * service types or service types through different filters.
 */
public interface Locator {
/**
 * Return an enumeration of the attributes for the service URL.
 * @param URL the service URL.
 * @param scopes the scopes of the service.
 * @param attributeIds a Vector of ServiceLocationAttribute objects identifying the attributes to match.
 * @return a ServiceLocationEnumeration of ServiceLocationAttribute objects matching the query
 * @throws ServiceLocationException if the operation fails.
 */
 public abstract ServiceLocationEnumeration findAttributes(ServiceURL URL,
                                       Vector scopes,
                                       Vector attributeIds) throws ServiceLocationException;
/**
 * Return the attributes for all services matching this service type.
 * @param serviceType the service type.
 * @param scopes the scopes of the service.
 * @param attributeIds a Vector of ServiceLocationAttribute objects identifying the attributes to match.
 * @return a ServiceLocationEnumeration of ServiceLocationAttribute objects matching the query
 * @throws ServiceLocationException if the operation fails.
 */
  public abstract ServiceLocationEnumeration findAttributes( ServiceType serviceType,
                                       Vector scopes,
                                       Vector attributeIds) throws ServiceLocationException;
/**
 * Returns the locale of the Locator object.
 */
  public abstract Locale getLocale();

/**
 * Return an enumeration of ServiceURL objects of the specified type matching the query.
 * @param serviceType the service type.
 * @param scopes the scopes of the service.
 * @param searchFilter a string query identifying the service.
 * @return a ServiceLocationEnumeration of ServiceURL objects matching the query.
 * @throws ServiceLocationException if the operation fails.
 */
 public abstract ServiceLocationEnumeration findServices(ServiceType serviceType,
                                     Vector scopes,
                                     String searchFilter) throws ServiceLocationException;


/**
 * Returns an enumeration of known service types for this scope and naming authority.
 *
 * @param namingAuthority the String representing the naming authority for the query.
 * Unless a proprietary or experimental service is being discovered, the
 * namingAuthority parameter should be null.
 * @param scopes the scopes of the service type.
 * @return a ServiceLocationEnumeration of ServiceType objects matching the query.
 * @throws ServiceLocationException if the operation fails.
 */
  public abstract ServiceLocationEnumeration findServiceTypes(String namingAuthority,
                                         Vector scopes) throws ServiceLocationException;

}
