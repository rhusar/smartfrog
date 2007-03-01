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
package org.smartfrog.sfcore.workflow.conditional.conditions;

import java.rmi.RemoteException;

/**
 * Implement and logic
 * created 30-Nov-2006 14:43:08
 */

public class OrCompoundCondition extends AbstractCompoundCondition {


    public OrCompoundCondition() throws RemoteException {
    }


    /**
     * Override point; reset the accumulator to its starting value
     */
    protected void resetAccumulator() {
        accumulator=false;
    }

    /**
     * Apply the next part of the operation;
     *
     * @param next the next value to apply to the accumulator
     * @return true if the test is to continue; false if short circuiting indicates we could exit now
     */
    protected boolean apply(boolean next) {
        accumulator |= next;
        //keep going if the accumulator is false; we stop as soon as it is true
        //because the or condition is satisfied
        return !accumulator;
    }
}
