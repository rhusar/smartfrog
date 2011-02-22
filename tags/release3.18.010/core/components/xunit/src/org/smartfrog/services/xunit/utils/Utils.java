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
package org.smartfrog.services.xunit.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * created 15-Apr-2004 16:26:56
 */

public class Utils {

    private Utils() {
    }

    /**
     * get the current hostname. This is not fully qualified.
     *
     * @return the hostname, or an error string
     */
    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
            return "(unknown host)";
        }
    }

    /**
     * get the current IP addr.
     *
     * @return the address, or an error string
     */
    public static String getIpAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored ) {
            return "(unknown address)";
        }   
    }

    /**
     * Apply system properties. This adds them to the current JVM, and does not unapply it afterwards
     * @param properties system properties
     */
    public static void applySysProperties(Properties properties) {
        Enumeration propertyEnum = properties.keys();
        while (propertyEnum.hasMoreElements()) {
            String key = (String) propertyEnum.nextElement();
            String value = properties.getProperty(key);
            System.setProperty(key, value);
        }
    }
}
