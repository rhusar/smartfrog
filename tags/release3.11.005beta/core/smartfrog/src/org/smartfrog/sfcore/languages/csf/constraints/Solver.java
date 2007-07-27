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

package org.smartfrog.sfcore.languages.csf.constraints;

import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.CSFComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

/**
 * Interface that all solver plug-ins must implement in order to be used by the SmartFrog constraint
 * resolution phase.
 */
public interface Solver {
    /**
     * carry out the constraint resolution
     *
     * @param cd the top of the component tree to constraint resolve
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException some kind of failure occurred!
     *
     */
    public void solve(CSFComponentDescription cd) throws SmartFrogResolutionException;

    /**
     * Method to show how to map a varibale into a representation appropriate for the constraint plug-in's language
     *
     * @param v a FreeVAr object representing the variable
     * @return a string representation of the varibale suitable for the language.
     */
   // public String freeVariableName(FreeVar v);

    /**
     * Method to convert an SF value to an object suitable for the solver
     * This should be the inverse of mapValueOut
     *
     * @param v the value to convert
     * @return the converted value
     */
    //public Object mapValueIn(Object v) throws SmartFrogTypeResolutionException;

    /**
     * Method to convert an object returned by the solver into one suited for SF
     * This should be the inverse of mapValueIn
     *
     * @param v the value to convert
     * @return the converted value
     */
     //public Object mapValueOut(Object v) throws SmartFrogTypeResolutionException;

    /**
     *  Obtain the character that will be used to delimit the start and end of a SmartFrog reference within the text of
     *  the query and theory strings. Two consecutive characters are used to escape the character.
     * @return the delimiter character
     */
     //ublic char referenceDelimiter();
}
