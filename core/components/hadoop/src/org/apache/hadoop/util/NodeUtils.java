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

import org.smartfrog.services.hadoop.core.BindingTuple;

import java.net.InetSocketAddress;

/**
 * Created 03-Feb-2009 13:46:19
 */

public class NodeUtils {
    public static BindingTuple toBindingTuple(String name, String protocol, InetSocketAddress addr) {
        String stringValue = toURL(protocol, addr);
        BindingTuple bindingTuple = new BindingTuple(name, stringValue);
        return bindingTuple;
    }

    public static String toURL(String protocol, InetSocketAddress addr) {
        String address = addr.getAddress().getHostAddress();
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
        String stringValue = buffer.toString();
        return stringValue;
    }
}
