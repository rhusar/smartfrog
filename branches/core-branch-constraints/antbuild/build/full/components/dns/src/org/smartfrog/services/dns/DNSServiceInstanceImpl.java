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
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TXTRecord;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Random;


/**
 * An implementation of a "bean" that contains info of a service instance.
 *
 * 
 * 
 */
public class DNSServiceInstanceImpl implements DNSServiceInstance {

    /** The name of service. */
    String serviceName = null;
    
    /** the host name where the service is running.*/
    String hostName = null;

    /** the IP address where the service is running.*/
    InetAddress hostAddress = null;;

    /**  the port number where the service is running.*/
    int port = -1;

    /**  the weight associated to this instance.*/
    int weight = 0;

    /** the priority associated to this instance (lower better). */
    int priority = 0;

    /**  a set of attribute/ value pairs that describe this instance.*/
    ComponentDescription description = null;

    /** A range of weights for this priority. */
    int weightRange = 65535;

    /** A random weight associated to this instance. */
    float randomWeight = NOT_INIT_WEIGHT;
    

    /** A comparator of instances (singleton). */
    private static Comparator _comparator = null;

    /** A very primitive random number generator. */
    private static Random rg = new Random();

    /** not init random weight. */
    private static final float NOT_INIT_WEIGHT = (float) -1.0;
    /**
     * Creates a new <code>DNSServiceInstanceImpl</code> instance.
     *
     * @param srv a <code>SRVRecord</code> value
     * @param txt a <code>TXTRecord</code> value
     * @param hostAddress an <code>InetAddress</code> value (or null)
     */
    public DNSServiceInstanceImpl(SRVRecord srv, TXTRecord txt,
                                  InetAddress hostAddress) 
        throws DNSException {
        
        try {
            serviceName = srv.getName().toString();
            hostName = srv.getTarget().toString();
            // use default name server if the address is null
            this.hostAddress = ((hostAddress != null) ? hostAddress
                                : InetAddress.getByName(hostName));
            port = srv.getPort();
            weight = srv.getWeight();
            priority = srv.getPriority();
            description = DNSServiceImpl.txtToCD(txt.getStrings());
        } catch (UnknownHostException e) {
            throw new DNSException("can't resolve" + hostName, e);
        } 
    }


   /**
     * Gets the name of the service.
     *
     * @return The name of the service.
     */
    public String getServiceName() {

        return serviceName;
    }

    /**
     * Gets the host name where the service is running.
     *
     * @return The host name where the service is running.
     */
    public String getHostName() {

        return hostName;
    }

    /**
     * Gets the IP address where the service is running. This is 
     * useful when a "conventional" DNS look-up will not provide it.
     *
     * @return An  IP address where the service is running. 
     */
    public InetAddress getHostAddress() {

        return hostAddress;
    }

    /**
     * Gets the port number where the service is running.
     *
     * @return  the port number where the service is running.
     */
    public int getPort() {

        return port;
    }


    /**
     * Gets  the maximum range for weights associated to a common  priority.
     *
     * @return The maximum range for weights associated to a common  priority.
     */
    public int getWeightRange() {

        return weightRange;
    }

    /**
     * Sets the maximum range for weights associated to a common  priority.
     *
     * @param weightRange the maximum range for weights associated to a common
     *  priority.
     * @return The old value for the weight range.
     */
    public int setWeightRange(int weightRange) {

        int old = this.weightRange;
        this.weightRange = weightRange;
        return old;
    }

    /**
     * Gets a random weight associated with this service (proportional to
     * the actual weight).
     *
     * @return A random weight associated with this service (proportional to
     * the actual weight).
     */
    public synchronized float getRandomWeight() {

        if (randomWeight ==  NOT_INIT_WEIGHT) {
            resetRandomWeight();
        }
        return randomWeight;
    }

    /**
     * Initializes a random weight proportional to the weight and relative
     * to a range.
     *
     */
    public synchronized void resetRandomWeight() {

        randomWeight = rg.nextFloat()
            * (((float) weight)/ ((float) weightRange));
    }

    /**
     * Gets the weight associated to this instance.
     * A proportional random factor for equal priority selection (0-65535). 
     * (see DNS-SD RFC) 
     * @return  the weight associated to this instance.
     */
    public int getWeight() {
        
        return weight;
    }


    /**
     * Gets the priority associated to this instance (lower better).
     * (see DNS-SD RFC) 
     *
     * @return The priority associated to this instance (lower better).
     */
    public int getPriority() {

        return priority;
    }
    

    /**
     * Gets a set of attribute/ value pairs that describe this instance.
     *
     * @return  a set of attribute/ value pairs that describe this instance.
     */
    public ComponentDescription getDescription() {

        return description;
    }




    /**
     * Gets a singleton comparator for instances.
     *
     * @return A singleton comparator for instances.
     */
    public static Comparator getComparatorInstance() {

        if (_comparator == null) {
            _comparator = newComparator();
        }
        return _comparator;
    }


    /**
     * Formats the service instance as a string.
     *
     * @return a <code>String</code> value
     */
    public String toString() {

        return (serviceName + "@" + hostName + ":"
                + Integer.toString(port) + "/?w=" + Integer.toString(weight) 
                + "&p=" +  Integer.toString(priority)+ "&d=" + description);

    }
    
    /**
     * Creates a comparator of instances using standard DNS-SD RFC
     * conventions, so that more relevant services are first in the list.
     * Note that lower priory number means more important, 
     * and if equal priority
     * we randomize according to a random uniform distribution 
     * (proportional to the weight) to choose the most important
     * (higher weight).
     *
     * @return  A comparator of instances using standard DNS-SD RFC
     * conventions, so that more relevant services are first in the list.
     */
    static Comparator newComparator() {

        return new Comparator() {
                public int compare(Object o1, Object o2) {
                    if ((o1 instanceof DNSServiceInstance)
                        && (o2 instanceof DNSServiceInstance)) {
                        
                        if (o1.equals(o2)) {
                            return 0;
                        }
                        DNSServiceInstance s1 = (DNSServiceInstance) o1;
                        DNSServiceInstance s2 = (DNSServiceInstance) o2;
                        if (s1.getPriority() < s2.getPriority()) {
                            // lower priority means more important
                            return -1;
                        } else if (s1.getPriority() > s2.getPriority()) {
                            return 1;
                            // same priority, compare weigths
                        } else if (s1.getRandomWeight() 
                                   < s2.getRandomWeight()) {
                            // bigger weight means more important
                            return 1;
                        } else if (s1.getRandomWeight() 
                                   > s2.getRandomWeight()) {
                            return -1;
                        } else {
                            // all weigths are the same
                            // don't want to return 0 since they are not equal!
                            // compare hascodes for consistency...
                            // this won't work if hashCode is not unique...
                            return (s1.hashCode() > s2.hashCode() ? -1 : 1);
                        }
                    } else {
                        throw new IllegalArgumentException("Can't compare"
                                                           +" instances");
                    }
                }                        
            };
    }



}
