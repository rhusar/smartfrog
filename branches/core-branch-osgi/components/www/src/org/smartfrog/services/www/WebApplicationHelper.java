/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.net.InetAddress;
import java.rmi.RemoteException;

/**
 * Things to help web applications.
 * Factored out of JettyHelper.
 * created 05-Apr-2006 15:13:52
 */

public class WebApplicationHelper extends ComponentHelper {

    public WebApplicationHelper(Prim owner) {
        super(owner);
    }

    /**
     * Concatenate two paths together, inserting a '/' if needed, and ensuring
     * that there is no '//' at the join.
     *
     * @param path1 first path
     * @param path2 secpnd path
     * @return concatenated paths
     */
    public static String concatPaths(String path1, String path2) {
        StringBuilder buffer = new StringBuilder(path1.length() +
                path2.length() +
                1);
        boolean endsWithSlash = path1.endsWith("/");
        boolean beginsWithSlash = path2.startsWith("/");
        buffer.append(path1);
        if (!endsWithSlash) {
            buffer.append('/');
        }
        if (beginsWithSlash) {
            buffer.append(path2.substring(1));
        } else {
            buffer.append(path2);
        }
        return buffer.toString();
    }

    /**
     * Get the ipaddrs of the local machine
     *
     * @return the IP address that we are deployed on
     * @throws RemoteException if the owner is not talking
     */
    public String getIpAddress() throws RemoteException {
        InetAddress deployedHost = getOwner().sfDeployedHost();
        String hostAddress = deployedHost.getHostAddress();
        return hostAddress;
    }

    /**
     * strip any trailing '*' from a path and give the base bit up to where that
     * began.
     *
     * @param path path to tidy up
     * @return the stripped path
     */
    public static String deregexpPath(String path) {
        String result;
        int star = path.indexOf('*');
        if (star < 0) {
            return path;
        }
        if (star == 0) {
            return "/";
        }
        result = path.substring(0, star - 1);
        return result;
    }
}
