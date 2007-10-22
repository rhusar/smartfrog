package org.smartfrog.services.slp;

import java.util.*;
/**
 * The advertiser interface allows service agents to register / deregister
 * service advertisements and their attributes.
 *
 * @author Guillaume Mecheneau
 */
public interface Advertiser {
/**
 * Register a new service with the given attributes.
 * @param URL the service URL to advertise
 * @param serviceLocationAttributes the Vector of ServiceLocationAttribute objects specifying the service
 * @throws ServiceLocationException if the operation fails
 */
  public abstract void register(ServiceURL URL,
                               Vector serviceLocationAttributes) throws ServiceLocationException ;


/**
 * Return the language locale with which this object was created
 */
  public abstract Locale getLocale();


/**
 * Deregister a service with the service location protocol for every Locale
 * and scope it was registered under.
 * @param URL the service URL to deregister
 * @throws ServiceLocationException if the operation fails
 */
  public abstract void deregister(ServiceURL URL) throws ServiceLocationException ;

/**
 * Add attributes to a service URL advertisement in the locale of the advertiser object.
 * @param URL the advertised service URL to which the attributes should be added
 * @param serviceLocationAttributes the Vector of ServiceLocationAttribute objects to add
 * @throws ServiceLocationException if the operation fails
 */

  public abstract void addAttributes(ServiceURL URL,
                                    Vector serviceLocationAttributes) throws ServiceLocationException;

/**
 * Delete the attributes from a service URL advertisement in every locale and configured scopes.
 * @param URL the service URL from which the attributes should be removed
 * @param attributeIds the Vector of ServiceLocationAttribute objects to deregister
 * @throws ServiceLocationException if the operation fails
 */
  public abstract void deleteAttributes(ServiceURL URL,
                                       Vector attributeIds) throws ServiceLocationException;

}
