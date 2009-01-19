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

package org.smartfrog.examples.arithnet;

import java.rmi.Remote;

/**
 * Defines the Plus component.
 */ 
public class Plus extends NetElemImpl implements Remote {
    /**
     * Left hand side of the sum.
     */
    private int lhs = 0;
    /**
     * Left hand side of the sum.
     */
    private int rhs = 0;
    /**
     * Constructs Plus.
     * @throws java.rmi.RemoteException if unable to construct the object remotely.
     */
    public Plus() throws java.rmi.RemoteException {
    }
    
    /**
     * Method from NetElem interface.
     * @param from placeholder
     * @param value integer value
     */
    protected int evaluate(String from, int value) {
        if (from.equals("lhs")) {
            lhs = value;
        } else {
            rhs = value;
        }

        return lhs + rhs;
    }
}
