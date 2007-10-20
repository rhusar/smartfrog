/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.references;

import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.ToplevelList;
import org.smartfrog.sfcore.languages.cdl.utils.NamespaceLookup;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import nu.xom.Node;

/**
 */
public class StepUp extends Step {
    public static final String ERROR_PATH_TOO_FAR_UP = " the path goes too far up";

    /**
     * Returns a string representation of the object.
     *
     * @return .
     */
    public String toString() {
        return "..";
    }

    /**
     * This is the operation that steps need to do, to execute a step.
     *
     * @return the result.
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException
     *          if something failed.
     */
    public StepExecutionResult execute(StepExecutionResult state) throws CdlResolutionException {
        PropertyList node = state.getNode();
        Node parent = node.getParent();
        if (parent == null) {
            throw new CdlResolutionException(ERROR_NO_STEP_UP + node + ERROR_ORPHAN_NODE, state);
        }
        if (!(parent instanceof PropertyList)) {
            throw new CdlResolutionException(ERROR_NO_STEP_UP + node + ERROR_WRONG_PARENT, state);
        }
        if (parent instanceof ToplevelList) {
            throw new CdlResolutionException(ERROR_NO_STEP_UP + node + ERROR_PATH_TOO_FAR_UP, state);
        }

        return state.next((PropertyList) parent);
    }

    /**
     * append zero or more reference parts to the current reference chain.
     *
     * @param namespaces base to use for determining xmlns mapping
     * @param reference  reference to build up
     */
    public void appendReferenceParts(NamespaceLookup namespaces,
                                     Reference reference)
            throws CdlResolutionException {
        reference.addElement(ReferencePart.parent());
    }
}
