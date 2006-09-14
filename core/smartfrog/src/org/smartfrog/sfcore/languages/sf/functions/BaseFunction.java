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

import org.smartfrog.sfcore.reference.Function;
import org.smartfrog.sfcore.reference.ReferenceResolver;
import org.smartfrog.sfcore.reference.RemoteReferenceResolver;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Defines the base function for all the functions.
 */
public abstract class BaseFunction implements Function  {
    /**
     * The method to implement the functionality of any function.
     *
     * @return the result of the function: an Object
     * @throws SmartFrogFunctionResolutionException
     * */
    protected abstract Object doFunction() throws SmartFrogFunctionResolutionException;

    protected Context context = null;
    protected ReferenceResolver rr = null;
    protected RemoteReferenceResolver rrr = null;

    protected Reference name = null;

    /**
     * base implementation of a function method.
     * Calls the (abstract) method doFunction.
     * Note that it makes sure that the result has no parent if it is a component description - this will
     * cause it to be patched into whereever it is returned.
     * 
     * @throws SmartFrogFunctionResolutionException if the doFunction method does.
     */
    public Object doit(Context context, Reference name, ReferenceResolver rr) throws SmartFrogFunctionResolutionException {
        this.context = context;
        this.rr = rr;

        Object result = doFunction();
        if (result instanceof ComponentDescription) {
            ((ComponentDescription)result).setParent(null);
            ((ComponentDescription)result).setPrimParent(null);
        }

        return result;
    }

    /**
     * base implementation of a function method.
     * Calls the (abstract) method doFunction.
     * Note that it makes sure that the result has no parent if it is a component description - this will
     * cause it to be patched into whereever it is returned.
     *
     * @throws SmartFrogFunctionResolutionException if the doFunction method does.
     */
    public Object doit(Context context, Reference name, RemoteReferenceResolver rr) throws SmartFrogFunctionResolutionException {
        this.context = context;
        this.rrr = rr;

        Object result = doFunction();
        if (result instanceof ComponentDescription) {
            ((ComponentDescription)result).setParent(null);
            ((ComponentDescription)result).setPrimParent(null);
        }
        return result;
    }
}
