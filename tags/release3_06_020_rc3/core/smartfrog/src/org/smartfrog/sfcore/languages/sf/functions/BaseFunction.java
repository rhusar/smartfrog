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

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;

import java.util.Stack;

/**
 * Defines the base function for all the functions.
 */
public abstract class BaseFunction implements PhaseAction  {
    /** The component description. */
    protected ComponentDescription component;

    /** The context of the component. */
    protected Context context;

    /** The name of the component for exceptions */
    protected Reference name;

    /** The path used to get to this component */
    protected Stack path;

    /** The phase in which this is being invoked */
    protected String phaseName;

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
        Object o;
        ComponentDescription origin = null;
        if (path.size() > 0)
            origin = (ComponentDescription) path.peek();
        else
            throw new SmartFrogCompileResolutionException(MessageUtil.formatMessage(MessageKeys.ROOT_COMPONENT_IS_FUNCTION));

        try {
            o = component.sfResolve("sfFunctionResult");
        } catch (SmartFrogResolutionException e) {
            //remove phase attribute to avoid it being used in function - but must add it back later
            Object phase = context.remove(phaseName);
            o = doFunction();

            try {
                component.sfReplaceAttribute("sfFunctionResult", o);
            } catch (SmartFrogRuntimeException e1) {
                throw new SmartFrogCompileResolutionException("error recording function result in function", e1);
            }
            context.put(phaseName, phase);
        }

        ComponentDescription parent = (ComponentDescription) component.sfParent();
        if (path.size() > 0) origin = (ComponentDescription) path.peek();
        if (o instanceof ComponentDescription) ((ComponentDescription) o).setParent(parent);
        Context originContext = origin.sfContext();
        Object key = originContext.keyFor(component);
        originContext.put(key, o);
    }


    /**
     * Prime the function with the necessary data
     */
    public void forComponent(ComponentDescription cd, String phaseName, Stack p) {
        path = p;
        component = cd;
        this.phaseName = phaseName;
        name = cd.sfCompleteName();
        context = cd.sfContext();
    }
}
