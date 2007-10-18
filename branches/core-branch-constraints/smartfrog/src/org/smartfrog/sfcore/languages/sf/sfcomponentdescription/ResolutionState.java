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

package org.smartfrog.sfcore.languages.sf.sfcomponentdescription;

import java.util.Vector;

import org.smartfrog.sfcore.reference.Reference;


/**
 * Encapsulates the state that is maintained during type, place, and deployment
 * resolution.  During all recursive resolution phases, state needs to be
 * maintained about whether any resolutions still need to be done after the
 * phase, and if any were actually resolved in this phase.
 *
 */
public class ResolutionState {
    /** Stores the unresolved values. */
    protected Vector unresolved = new Vector();

    /** Stores whether something was resolved. */
    protected boolean haveResolved = false;
    
    /**
     * Constructs the ResolutionState object.
     */
    public ResolutionState() {
    }

    /**
     * Clears the state to have no unresolved elements with nothing resolved
     * yet.
     */
    public void clear() {
        unresolved.removeAllElements();
        haveResolved = false;
    }

    /**
     * Returns whether anything was resolved.
     *
     * @return true if anything was resolved, false if not
     */
    public boolean haveResolved() {
        return haveResolved;
    }

    /**
     * Sets the have resolved flag.
     *
     * @param val new value for glag
     */
    public void haveResolved(boolean val) {
        haveResolved = val;
    }

    /**
     * Returns the unresolved elements.
     *
     * @return unresolved elements
     */
    public Vector unresolved() {
        return unresolved;
    }

    /**
     * Adds an unresovled element to the unresolved list.
     *
     * @param val unresolved object to add to state
     */
    public void addUnresolved(Object val) {
        addUnresolved(val, null, "", null);
    }

    /**
     * Adds an unresolved element with a source reference to the unresolved
     * table.
     *
     * @param val value that was unresolved
     * @param source source that held the value
     * @param attrName the name of the attribute affected
     * @param cause the exception that caused the resolution failure
     */
    public void addUnresolved(Object val, Reference source, String attrName, Exception cause) {
        UnresolvedValue unres = new UnresolvedValue(val, source, attrName, cause);

        if (!unresolved.contains(unres)) {
            unresolved.addElement(unres);
        }
    }

    /**
     * Checks if doing another resolution phase would make sense. There is more
     * to resolve if there are any unresolved items and something was resolved
     * in the last cycle.
     *
     * @return true if more to resolve, false otherwise
     */
    public boolean moreToResolve() {
        return (unresolved.size() > 0) && (haveResolved);
    }
}
