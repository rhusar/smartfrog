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
package org.smartfrog.sfcore.workflow.conditional.conditions;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.rmi.RemoteException;

/**
 * Created 18-Nov-2009 15:19:35
 */

public class StringLength extends PrimImpl implements Condition {

    /**
     * {@value}
     */
    public static final String ATTR_STRING = "string";

    /**
     * {@value}
     */
    public static final String ATTR_LENGTH = "length";

    private boolean isTrue;

    public StringLength() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        String stringValue;
        int length = 0;
        stringValue = sfResolve(ATTR_STRING).toString();
        length = sfResolve(ATTR_LENGTH, length, true);
        isTrue = stringValue.length() == length;
    }

    @Override
    public boolean evaluate() throws RemoteException, SmartFrogException {
        return isTrue;
    }
}
