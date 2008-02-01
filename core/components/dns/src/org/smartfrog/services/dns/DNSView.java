/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

For more information: www.smartfrog.org

*/

package org.smartfrog.services.dns;

import org.xbill.DNS.Resolver;

import java.net.InetAddress;
import java.util.Vector;





/**
 * An interface for a DNS view.
 *
 * 
 */
public interface DNSView extends DNSComponent {



    /**
     * Gets the name of this view.
     *
     * @return The name of this view.
     */
    public String getName();


    /**
     * Whether this view should be considered "default".
     *
     * @return True if  this view should be considered "default".
     */
    public boolean isDefault();


    /**
     * Gets the main IP address to contact this view.
     *
     * @return The main IP address to contact this view.
     */
    public InetAddress getAddress();

    /**
     * Gets a vector of (strings)  IP addresses representing all the 
     * network interfaces the view listens to.
     *
     * @return  A vector of (strings)  IP addresses representing all the 
     * network interfaces the view listens to.
     *
     */
    public Vector getInterfacesAddress();


   /**
     * Replaces a new zone object as child of this top level component.
     * If it is not present it is just added.  We gave it
     * an attribute equal to its name, and if it is a "default" (reverse) zone,
     * we also bound it with the DEFAULT_ZONE or DEFAULT_REVERSE_ZONE 
     * attributes.
     *
     * @param zone  A zone data object that replaces/adds.
     * @return The old zone with the same name or null.
     */
    public  DNSZone replaceZone(DNSZone zone);

    /**
     * Gets a resolver to communicate with the name server in our view.
     *
     * @return A resolver to communicate with the name server in our view.
     * @exception DNSException if an error occurs while getting a resolver
     * to the  name server.
     */
    public Resolver  getResolver() 
        throws DNSException;


   /**
     * Returns a pointer to the parent "data" or null if it has not 
     *  been inserted into a top level "data" yet.
     *
     * @return A pointer to the parent "data" or null if it has not 
     *  been inserted into a top level "data" yet.
     */
    public DNSData getEnclosingData();

    /**
     * Gets a vector of IP addresses to forward queries that we cannot resolve
     *   locally for this view.
     *
     * @return A vector of IP addresses to forward queries that we cannot 
     * resolve locally.
     */
    public Vector getForwarders();

}
