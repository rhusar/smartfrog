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

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

/**
 * Defines the Set function.
 */
public class Set extends BaseFunction implements MessageKeys {
    public final static String MIN = "setMin";
    public final static String MAX = "setMax";
    public final static String INCREMENT = "setInc";
    public final static String SIZE = "setSize";
    public final static String ELEMENT = "setElem";
    public final static String PREFIX = "setPrefix";

	private int setMin  = 0;
    private int setSize = -1;
    private int setInc  = 1;
    private int setMax  = -1;
    private SFComponentDescription setElem = null;
    private String setPrefix = "elem";

    /**
     * Creates a set of elements based on indices for a name and a template for each element
     *
     * @return an sfComponentDescription representing the Set
     * @throws org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected ComponentDescription doFunction() throws SmartFrogFunctionResolutionException {
        try {
            if (context.containsKey(MIN)) {setMin = ((Integer) context.get(MIN)).intValue(); comp.sfRemoveAttribute(MIN);}
            if (context.containsKey(MAX)) {setMax = ((Integer) context.get(MAX)).intValue(); comp.sfRemoveAttribute(MAX);}
            if (context.containsKey(INCREMENT)) {setInc = ((Integer) context.get(INCREMENT)).intValue(); comp.sfRemoveAttribute(INCREMENT);}
            if (context.containsKey(SIZE)) {setSize = ((Integer) context.get(SIZE)).intValue(); comp.sfRemoveAttribute(SIZE);}
            if (context.containsKey(ELEMENT)) {setElem = ((SFComponentDescription) context.get(ELEMENT)); comp.sfRemoveAttribute(ELEMENT);}
            if (context.containsKey(PREFIX)) {setPrefix = ((String) context.get(PREFIX)); comp.sfRemoveAttribute(PREFIX);}
            comp.sfRemoveAttribute("sfFunctionClass");
        } catch (Exception e) {
                throw new SmartFrogFunctionResolutionException("Error reading a parameter of a Set ", e,  name, null);
        }

        if (setMax == -1 && setSize == -1) throw new SmartFrogFunctionResolutionException("One of " + MAX + " or " + SIZE + "must be assigned in a Set", null,  name, null);
        if (setMax != -1 && setSize != -1)throw new SmartFrogFunctionResolutionException("Only one of " + MAX + " or " + SIZE + "may be assigned in a Set", null,  name, null);
        if (setElem == null)throw new SmartFrogFunctionResolutionException("Set requires an element", null,  name, null);

        if (setSize == -1) setSize = 1 + (setMax - setMin) / setInc;
        if (setSize < 0) throw new SmartFrogFunctionResolutionException("Set cannot have a negative size", null,  name, null);

        for (int i = 0; i < setSize; i++) {
            try {
                SFComponentDescription elem = (SFComponentDescription) setElem.copy();
                int index = setMin + i*setInc;
                //elem.sfAddAttribute("index", index);
                comp.sfAddAttribute(setPrefix + index, elem);
            } catch (SmartFrogRuntimeException e) {
                throw new SmartFrogFunctionResolutionException("Error adding element to set", null,  name, e);
            }
        }

        return comp;
    }
}
