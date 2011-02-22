/*
Service Location Protocol - SmartFrog components.
 Copyright (C) 1998-2003 Hewlett-Packard Development Company, LP
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
*/

package org.smartfrog.services.comm.slp;

import org.smartfrog.sfcore.logging.LogSF;

import java.util.Locale;
import java.util.Vector;

/** The Locator interface allows user agent objects to query for service URLs, service types or service attributes. */
public interface Locator {
    /**
     * Return an enumeration of the attributes for the service URL.
     *
     * @param URL          the service URL.
     * @param scopes       the scopes of the service.
     * @param attributeIds a Vector of ServiceLocationAttribute objects identifying the attributes to match.
     * @return a ServiceLocationEnumeration of ServiceLocationAttribute objects matching the query
     * @throws ServiceLocationException if the operation fails.
     */
    public abstract ServiceLocationEnumeration findAttributes(ServiceURL URL,
                                                              Vector scopes,
                                                              Vector attributeIds) throws ServiceLocationException;

    /**
     * Return the attributes for all services matching this service type.
     *
     * @param serviceType  the service type.
     * @param scopes       the scopes of the service.
     * @param attributeIds a Vector of ServiceLocationAttribute objects identifying the attributes to match.
     * @return a ServiceLocationEnumeration of ServiceLocationAttribute objects matching the query
     * @throws ServiceLocationException if the operation fails.
     */
    public abstract ServiceLocationEnumeration findAttributes(ServiceType serviceType,
                                                              Vector scopes,
                                                              Vector attributeIds) throws ServiceLocationException;

    /** Returns the locale of the Locator object. */
    public abstract Locale getLocale();

    /**
     * Return an enumeration of ServiceURL objects of the specified type matching the query.
     *
     * @param serviceType  the service type.
     * @param scopes       the scopes of the service.
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
     * @param namingAuthority the String representing the naming authority for the query. Unless a proprietary or
     *                        experimental service is being discovered, the namingAuthority parameter should be null.
     * @param scopes          the scopes of the service type.
     * @return a ServiceLocationEnumeration of ServiceType objects matching the query.
     * @throws ServiceLocationException if the operation fails.
     */
    public abstract ServiceLocationEnumeration findServiceTypes(String namingAuthority,
                                                                Vector scopes) throws ServiceLocationException;

    /**
     * Returns the list of scopes supported by the locator. In the case where the locator has been configured with no
     * scope list, this method will return the scopes discovered by the UA through DA/SA adverts.
     */
    public abstract Vector getScopes();

    public abstract void setSFLog(LogSF log);
}
