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
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;

/**
 * Defines the base function for all the functions.
 */
public abstract class BaseFunction implements PhaseAction {
    /** The component description. */
    protected ComponentDescription component;

    /** The context of the component. */
    protected Context context;

    /**
     * The method to implement the functionality of any function.
     *
     * @return an Object
     * */
    protected abstract Object doFunction();

    public void doit() {
        Object o = doFunction();
        ComponentDescription parent = (ComponentDescription) component.getParent();
        Context context = parent.getContext();
        Object key = context.keyFor(component);
        context.put(key, o);
    }

    public void forComponent(ComponentDescription cd) {
        component = cd;
        context = cd.getContext();
    }
}
