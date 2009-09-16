/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.farmer;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * Intermediate class for cluster farmer implementations -contains any helper methods that they should be sharing
 */
public abstract class AbstractClusterFarmer extends PrimImpl {

    /**
     * {@value}
     */
    public static final String WRONG_MACHINE_COUNT
            = "The maximum number of machines requested was less than the minimum";
    /**
     * {@value}
     */
    public static final String NEGATIVE_VALUES_NOT_SUPPORTED = "Negative values not supported";

    protected AbstractClusterFarmer() throws RemoteException {
    }

    /**
     * check the min and max arguments
     *
     * @param min minimum number of nodes desired
     * @param max maximumum number  desired
     * @throws SmartFrogException
     */
    protected void validateClusterRange(int min, int max) throws SmartFrogException {
        if (max < min) {
            throw new SmartFrogException(WRONG_MACHINE_COUNT);
        }
        if (min < 0) {
            throw new SmartFrogException(NEGATIVE_VALUES_NOT_SUPPORTED);
        }
    }
}
