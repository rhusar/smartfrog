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



/**
 * An interface that defines a DNS IP/hostName binding.
 *
 * 
 * 
 */
public interface DNSBindingIP extends DNSComponent {


    /**
     * Gets the name of the zone.
     *
     * @return The name of the zone.
     */
    public String getZoneName();


    /**
     * Returns a pointer to the parent zone or null if it has not 
     *  been inserted into a zone yet.
     *
     * @return A pointer to the parent zone or null if it has not 
     *  been inserted into a zone yet.
     */
    public DNSZone getEnclosingZone();

 
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
     * Gets the dns record associated with this binding.
     *
     * @return The dns record associated with this binding.
     */
    public DNSRecord  getRecord();

    /**
     *  Registers a DNS binding update in the name server. 
     *
     * @exception DNSModifierException if an error occurs 
     * while registering the update.
     */
    public void register() throws DNSModifierException;


    /**
     * Unregisters a DNS binding update in the name server.
     *
     * @exception DNSModifierException if an error occurs 
     * while unregistering the update.
     */
    public void unregister() throws DNSModifierException;



    /**
     * Tests whether the binding can be looked-up in the server.
     *
     * @return True if the binding can be looked-up in the server.
     */
    public boolean testBinding();
}
