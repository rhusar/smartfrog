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
package org.smartfrog.sfcore.workflow.conditional;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Class to help with condition work
 * Created 01-Jul-2009 16:45:36
 */

public class ConditionalHelper {
    
    public static boolean resolveConditionAttribute(Prim target, String attribute, boolean required, boolean defval)
            throws SmartFrogException, RemoteException {
        return resolveConditionAttribute(target, new Reference(attribute), required, defval);
        
    }

    public static boolean resolveConditionAttribute(Prim target, Reference ref, boolean required, boolean defval)
            throws SmartFrogException, RemoteException {
        //check the result exists
        Object result = target.sfResolve(ref, required);
        // first, look for a boolean value
        if (result != null) {
            if (result != null && result instanceof Boolean) {
                Boolean b = (Boolean) result;
                return b;
            }
            //we want a reference to a condition, so ask for a Prim (this has specific error messages in some failure cases)
            Condition cond = (Condition) target.sfResolve(ref, (Prim) null, true);
            return cond.evaluate();
        } else {
            return defval;
        }
        
    }

}
