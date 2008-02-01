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

import java.io.File;
import java.util.Vector;



/**
 * An interface that defines a DNS Zone.
 *
 * 
 * 
 */
public interface DNSZone extends DNSComponent {

    /**
     * Replaces a new binding object as child of this component.
     * If it is not present it is just added.  We gave it
     * an attribute equal to its name.
     *
     * @param binding  A binding data object that replaces/adds.
     * @return The old binding with the same name or null.
     */
    public DNSBindingIP replaceBinding(DNSBindingIP binding);

 
    /**
     * Gets the name of the view this zone is in.
     *
     * @return The name of the view this zone is in.
     */
    public String getViewName();



    /**
     * Returns a pointer to the parent view or null if it has not 
     *  been inserted into a view yet.
     *
     * @return A pointer to the parent view or null if it has not 
     *  been inserted into a view yet.
     */
    public DNSView getEnclosingView();



    /**
     * Gets the host name that will appear in the NS records as 
     *  master of the zone.
     *
     * @return The host name that will appear in the NS records as 
     *  master of the zone.
     *
     */
    public String getNameServerHostName();

    /**
     * Whether we allow dynamic updates for this zone.
     *
     * @return True if  we allow dynamic updates for this zone.
     */
    public boolean getAllowUpdate();

    /**
     * Whether we have a forward instead of master zone.
     *
     * @return True if we have a forward instead of master zone.
     */
    public boolean isForwarding();

    /**
     * Gets a vector of addresses we forward to (if we don't master).
     *
     * @return A vector of addresses we forward to.
     */
    public Vector getForwarders();

    /**
     * Whether this a reverse mapping zone.
     *
     * @return True if this is a reverse mapping zone.
     */
    public boolean isReverse();

    
    /**
     * Whether this zone should be considered "default".
     *
     * @return True if  this zone should be considered "default".
     */
    public boolean isDefault();


    /**
     * Gets a standard SOA record time limit.
     *
     * @return A standard SOA record time limit.
     */
    public int getRefresh();

    /**
     * Gets a standard SOA record time limit.
     *
     * @return A standard SOA record time limit.
     */
     public int getRetry();

    /**
     * Gets a standard SOA record time limit.
     *
     * @return A standard SOA record time limit.
     */
     public int getExpire();


    /**
     * Gets a standard SOA record time limit.
     *
     * @return A standard SOA record time limit.
     */
    public int getTTL();

    
    /**
     * Dumps to a file the corresponding SOA record.
     *
     * @param dir A directory where to write the record.
     * @param overwrite True if we overwrite existing files.
     * @exception DNSException if an error occurs
     */
    public void writeSOARecord(File dir, boolean overwrite)
        throws DNSException;
}
