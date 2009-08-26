/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.apache.hadoop.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.smartfrog.services.hadoop.core.BindingTuple;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


/**
 * Created 03-Feb-2009 13:46:19
 */

public class NodeUtils {
    
    private static final Log LOG = LogFactory.getLog(NodeUtils.class);
    
    public static BindingTuple toBindingTuple(String name, String protocol, InetSocketAddress addr) {
        String stringValue = toURL(protocol, addr);
        BindingTuple bindingTuple = new BindingTuple(name, stringValue);
        return bindingTuple;
    }

    public static String toURL(String protocol, InetSocketAddress addr) {
        String address = addressToString(addr);
        int port = addr.getPort();
        return toURL(protocol, address, port);
    }

    public static String toURL(String protocol, String address, int port) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(protocol);
        buffer.append("://");
        buffer.append(address);
        buffer.append(":");
        buffer.append(Integer.toString(port));
        buffer.append("/");
        return buffer.toString();
    }


    /**
     * Resolve an address from an attribute of a configuration
     * @param conf the configuration
     * @param attribute the attribute
     * @return the socket address
     * @throws IllegalArgumentException if the address attribute is missing
     */
    public static InetSocketAddress resolveAddress(Configuration conf, String attribute) {
        String address = conf.get(attribute);
        if (address == null) {
            throw new IllegalArgumentException("No value for " + attribute);
        }
        return NetUtils.createSocketAddr(address);
    }
    
    public static String addressToString(InetSocketAddress addr) {
        InetAddress address = addr.getAddress();
        if (address.isAnyLocalAddress()) {
            //it's local, so patch it to whatever we think we are
            return cachedHostAddress;
        } else {
            return addr.getAddress().getHostAddress();
        }
    }


    /**
     * This may look like code in Hadoop DNS, but that's because I wrote both.
     */
    private static final String cachedHostAddress = resolveLocalHostIPAddress();

    private static String resolveLocalHostIPAddress() {
        String address;
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.info("Unable to determine address of the host"
                    + "-falling back to localhost address", e);
            try {
                address = InetAddress.getByName("localhost").getHostAddress();
            } catch (UnknownHostException noLocalHostAddressException) {
                //at this point, deep trouble
                LOG.error("Unable to determine local loopback address "
                        + "of \"" + "localhost" + "\" " +
                        "-this system's network configuration is unsupported", e);
                address = "127.0.0.1";
            }
        }
        return address;
    }
}
