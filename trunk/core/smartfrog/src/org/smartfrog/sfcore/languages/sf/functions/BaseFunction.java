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

package org.smartfrog.sfcore.languages.sf.functions;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;

/**
 * Defines the base function for all the functions.
 */
public abstract class BaseFunction implements PhaseAction, MessageKeys {
    /** The component description. */
    protected ComponentDescription component;

    /** The context of the component. */
    protected Context context;

    /** The name of the component for exceptions */
    protected Reference name;

    /**
     * The method to implement the functionality of any function.
     *
     * @return the result of the function: an Object
     * @throws SmartFrogCompileResolutionException
     * */
    protected abstract Object doFunction() throws SmartFrogCompileResolutionException;

    /**
     * Implementation of the phase action doit() method.
     * Calls the (abstract) method doFunction and replaces this definition with the result.

     * @throws SmartFrogCompileResolutionException if the doFunction method does.
     */
    public void doit() throws SmartFrogCompileResolutionException {
        Object o = doFunction();
        ComponentDescription parent = (ComponentDescription) component.sfParent();
        Context parentContext = parent.sfContext();
        Object key = parentContext.keyFor(component);
        parentContext.put(key, o);
    }

    /**
     * Prime the function with the necessary data
     */
    public void forComponent(ComponentDescription cd) {
        component = cd;
    name = cd.sfCompleteName();
        context = cd.sfContext();
    }
}
