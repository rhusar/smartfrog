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

package org.smartfrog.sfcore.languages.sf.predicates;

import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.security.SFClassLoader;



/**
 * Defines the basic schema implementation.
 */
public class Assertions extends BasePredicate implements PhaseAction {

    Reference ref;

    /**
     * Applies predicates.
     * @throws SmartFrogCompileResolutionException if fail to apply predicates.
     */
    protected void doPredicate() throws SmartFrogCompileResolutionException {
        String description = "";

        ref = component.sfCompleteName();
        ComponentDescription parent = (ComponentDescription) component.sfParent();


        for (Enumeration keys = context.keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = context.get(key);
            try {
                if (!key.toString().startsWith("phase")) {
            if (value instanceof Boolean) {
            if (!((Boolean)value).booleanValue()) { // invalid assertion (ignore if valid!)
                throw new SmartFrogCompileResolutionException (
                "Assertion failure: " + key + " (" + value + ")",
                null, ref, "predicate", null
                );
            }
            } else { // wrong type for assertion
            throw new SmartFrogCompileResolutionException (
                "Assertion type is not a boolean: " + key + " (" + description + ")",
                null, ref, "predicate", null
                );
            }
        }
        } catch (Throwable e) {
                if (!(e instanceof SmartFrogCompileResolutionException))
                    throw new SmartFrogCompileResolutionException (
                        "unknown error in checking assertinos (" + description + ")" , e, ref, "predicate", null
                       );
                else
                    throw (SmartFrogCompileResolutionException)e;
            }
        }
    }

}
