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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;




/**
 * A smartfrog exception that represents a failed DNS config modification.
 *
 * 
 * 
 */
public class DNSModifierException extends DNSException {

    /** A configuration  update for the DNS server. */ 
    ComponentDescription update = null;

     /**
     * Creates a new <code>DNSModifierException</code> instance.
     *
     * @param message An extra info on the exception.
     */
    public DNSModifierException(String message) {
        super(message);
    }


    /**
     * Creates a new <code>DNSModifierException</code> instance.
     *
     * @param message An extra info on the exception.
     * @param cause An original exception that caused this one.
     */
    public DNSModifierException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new <code>DNSModifierException</code> instance.
     *
     * @param message An extra info on the exception.
     * @param cause An original exception that caused this one.
     * @param update A configuration  update for the DNS server.
     */
    public  DNSModifierException(String message, Throwable cause,
                                 ComponentDescription update) {
        super(message, cause);
        this.update = update;
    }

    /**
     * Gets the component description that encapsulates the configuration 
     * changes submitted to the DNS server.
     *
     * @return The component description that encapsulates the configuration 
     * changes submitted to the DNS server.
     *
     */
    public ComponentDescription getUpdate() {

        return update;
    }
    


}
