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

import java.net.InetAddress;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;




/**
 * A "bean like" interface  to a service instance.
 *
 * 
 * 
 */
public interface DNSServiceInstance {


    /**
     * Gets the name of the service.
     *
     * @return The name of the service.
     */
    public String getServiceName();

    /**
     * Gets the host name where the service is running.
     *
     * @return The host name where the service is running.
     */
    public String getHostName();

    /**
     * Gets the IP address where the service is running. This is 
     * useful when a "conventional" DNS look-up will not provide it.
     *
     * @return An  IP address where the service is running. 
     */
    public InetAddress getHostAddress();

    /**
     * Gets the port number where the service is running.
     *
     * @return  the port number where the service is running.
     */
    public int getPort();


    /**
     * Gets the weight associated to this instance.
     * A proportional random factor for equal priority selection (0-65535). 
     * (see DNS-SD RFC) 
     * @return  the weight associated to this instance.
     */
    public int getWeight();


    /**
     * Gets the priority associated to this instance (lower better).
     * (see DNS-SD RFC) 
     *
     * @return The priority associated to this instance (lower better).
     */
    public int getPriority();
    

    /**
     * Gets a set of attribute/ value pairs that describe this instance.
     *
     * @return  a set of attribute/ value pairs that describe this instance.
     */
    public ComponentDescription getDescription();

    /**
     * Gets  the maximum range for weights associated to a common  priority.
     *
     * @return The maximum range for weights associated to a common  priority.
     */
    public int getWeightRange();


    /**
     * Sets the maximum range for weights associated to a common  priority.
     *
     * @param weightRange the maximum range for weights associated to a common
     *  priority.
     * @return The old value for the weight range.
     */
    public int setWeightRange(int weightRange);


    /**
     * Gets a random weight associated with this service (proportional to
     * the actual weight).
     *
     * @return A random weight associated with this service (proportional to
     * the actual weight).
     */
    public float getRandomWeight();


    /**
     * Initializes a random weight proportional to the weight and relative
     * to a range.
     *
     */
    public void resetRandomWeight();


}
