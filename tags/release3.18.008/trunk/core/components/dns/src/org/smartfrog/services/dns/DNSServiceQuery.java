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





/**
 * A query interface to a service class registered in DNS, for which we can 
 * find a  set of instances currently registered.
 *
 * 
 * 
 */
public interface DNSServiceQuery {


    /**
     * Performs a look-up of the registered service class using 
     * a particular resolver and filtering according to a given 
     * criteria. Matching instances are returned in decreasing
     * priority. Multiple requests could return different results
     * since the available instances or priorities could change 
     * dynamically. It uses  default DNS settings and filters.
     *
     * @return An array of service instances matching the criteria
     * and in decreasing priority or an array of lenght zero if 
     * no services found.
     * @exception DNSException if an error occurs while looking up.
     */
    public DNSServiceInstance[] lookup()
        throws DNSException;


    /**
     * Performs a look-up of the registered service class using 
     * a particular resolver and filtering according to a given 
     * criteria. Matching instances are returned in decreasing
     * priority. Multiple requests could return different results
     * since the available instances or priorities could change 
     * dynamically.
     *
     * @param resol A network connection to resolve the query.
     * @param filter An extra filtering function to be applied to the 
     * results or null if no extra filtering is required.
     * @return An array of service instances matching the criteria
     * and in decreasing priority or an array of lenght zero if 
     * no services found.
     * @exception DNSException if an error occurs while looking up.
     */
    public DNSServiceInstance[] lookup(Resolver resol,
                                       DNSServiceFilter filter)
        throws DNSException; 
    
}
