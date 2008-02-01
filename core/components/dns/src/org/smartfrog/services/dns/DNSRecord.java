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

import org.xbill.DNS.Name;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Update;





/**
 * An interface for a  helper class that represents a DNS record that 
 * needs to be updated.
 *
 * 
 */
public interface DNSRecord {
    

    /**
     * Sets the time-to-live value in seconds of this record.
     *
     * @param ttl  The time-to-live value in seconds of this record.
     * @return The old time-to-live value in seconds of this record.
     */
    public int setTTL(int ttl);

    /**
     * Gets the time-to-live value in seconds of this record.
     *
     * @return The time-to-live value in seconds of this record.
     */
    public int getTTL();
      


    /**
     * Whether to replace all previous bindings during registration.        
     *
     * @return True  if we  replace all previous bindings during registration.
     */
    public boolean getReplaceAll();


    /**
     * Sets whether to replace all previous bindings during registration.  
     *
     * @param replaceAll whether to replace all previous bindings during 
     * registration. 
     * @return An old value for that flag.
     */
    public boolean setReplaceAll(boolean replaceAll);


    /**
     * Gets the class of this DNS record, i.e., IN.
     *
     * @return  the class of this DNS record, i.e., IN.
     */
    public int getClassRecord();

    /**
     * Gets the type of this DNS record, i.e., A, PTR, SRV...
     *
     * @return  the type of this DNS record, i.e., A, PTR, SRV...
     */
    public int getType();

    /**
     * Gets the name of this DNS record.
     *
     * @return  the  name of this DNS record.
     */
    public Name getName();

    /**
     * Gets a unique name in the zone context for this DNS record.
     *
     * @return   a unique name in the zone context for this DNS record.
     */
    public String getUniqueName();

    /**
     * Gets an update record that will register this record in a 
     * given zone.
     *
     * @param zone A zone where this record is registered.
     * @return  An update record that will register this record in a 
     * given zone.
     * @exception DNSModifierException if an error occurs while obtaining the
     * update.
     */
    public Update getRegisterUpdate(Name zone)  
        throws DNSModifierException;

    /**
     * Gets an update record that will unregister this record in a 
     * given zone.
     *
     * @param zone A zone where this record is registered.
     * @return  An update record that will unregister this record in a 
     * given zone.
     * @exception DNSModifierException if an error occurs while obtaining the
     * update.
     */
     public Update getUnregisterUpdate(Name zone)  
        throws DNSModifierException;

    /**
     * Validates whether we can look up the record in the DNS server.
     *
     * @param zone  A zone where this record is registered.
     * @param resol A <code>Resolver</code> to contact the DNS server.
     * @return True if we can validate, false otherwise.
     */
    public boolean validLookup(Name zone, Resolver resol);
        

}
