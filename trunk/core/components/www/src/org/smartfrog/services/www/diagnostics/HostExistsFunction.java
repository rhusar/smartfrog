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
package org.smartfrog.services.www.diagnostics;

import org.smartfrog.sfcore.languages.sf.functions.BaseUnaryOperator;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 */
public class HostExistsFunction extends BaseUnaryOperator {


    /**
     * @param host the host to look for
     * @return true if the host exists
     * @throws SmartFrogFunctionResolutionException for signature compatibility
     */
    protected Object doOperator(Object host)
            throws SmartFrogFunctionResolutionException {
        String hostname=(String) host;
        return Boolean.valueOf(hostExists(hostname));
    }

    public static boolean hostExists(String hostname) {
        try {
            InetAddress.getByName(hostname);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
