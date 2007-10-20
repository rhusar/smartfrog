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
import org.smartfrog.sfcore.languages.cdl.utils.NamespaceLookup;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import nu.xom.Node;

/**
 */
public class StepRoot extends Step {

    /**
     * Returns a string representation of the object.
     *
     * @return ""
     */
    public String toString() {
        return "/";
    }

    /**
     * Is this step a root node?
     *
     * @return true always.
     */
    public boolean isRootStep() {
        return true;
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
        PropertyList current = node;
        while(!current.isRoot()) {
            Node parent = node.getParent();
            if (parent == null) {
                throw new CdlResolutionException(ERROR_NO_STEP_UP + node + ERROR_ORPHAN_NODE, state);
            }
            PropertyList next=(PropertyList) parent;
            current=next;
        }

        return state.next(current);
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
        //TODO: check this is correct
        reference.addElement(ReferencePart.root());
    }
}
