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
package org.smartfrog.services.hostnames;

import org.smartfrog.sfcore.languages.sf.functions.BaseFunction;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;

/**
 * Created 03-Mar-2009 12:15:50
 */

public class LocalhostFunction extends BaseFunction {


    public LocalhostFunction() {
    }

    /**
     * Work out the local hostname. When first deployed, it calculates the value
     *
     * @return the hostname of the local host as a string
     * @throws SmartFrogFunctionResolutionException
     *
     */
    protected Object doFunction() throws SmartFrogFunctionResolutionException {
        return HostnameUtils.getLocalHostname();
    }
}
