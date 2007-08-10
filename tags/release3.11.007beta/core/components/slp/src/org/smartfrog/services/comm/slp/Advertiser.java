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

/**
 * The advertiser interface allows service agents to register / deregister service advertisements and their attributes.
 *
 * @author Guillaume Mecheneau
 */
public interface Advertiser {
    /**
     * Register a new service with the given attributes.
     *
     * @param URL                       the service URL to advertise
     * @param serviceLocationAttributes the Vector of ServiceLocationAttribute objects specifying the service
     * @throws ServiceLocationException if the operation fails
     */
    public abstract void register(ServiceURL URL,
                                  Vector serviceLocationAttributes) throws ServiceLocationException;


    /** Return the language locale with which this object was created */
    public abstract Locale getLocale();


    /**
     * Deregister a service with the service location protocol for every Locale and scope it was registered under.
     *
     * @param URL the service URL to deregister
     * @throws ServiceLocationException if the operation fails
     */
    public abstract void deregister(ServiceURL URL) throws ServiceLocationException;

    /**
     * Add attributes to a service URL advertisement in the locale of the advertiser object.
     *
     * @param URL                       the advertised service URL to which the attributes should be added
     * @param serviceLocationAttributes the Vector of ServiceLocationAttribute objects to add
     * @throws ServiceLocationException if the operation fails
     */

    public abstract void addAttributes(ServiceURL URL,
                                       Vector serviceLocationAttributes) throws ServiceLocationException;

    /**
     * Delete the attributes from a service URL advertisement in every locale and configured scopes.
     *
     * @param URL          the service URL from which the attributes should be removed
     * @param attributeIds The Vector of ServiceLocationAttribute objects to deregister
     * @throws ServiceLocationException if the operation fails
     */
    public abstract void deleteAttributes(ServiceURL URL,
                                          Vector attributeIds) throws ServiceLocationException;

    /** Returns the list of scopes supported by the advertiser. */
    public abstract Vector getScopes();

    public abstract void setSFLog(LogSF log);
}
