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
import org.smartfrog.sfcore.languages.cdl.utils.NamespaceLookup;
import org.smartfrog.sfcore.reference.Reference;

/**
 * This represents a step in a path
 */
public abstract class Step {
    public static final String ERROR_WRONG_PARENT = " parent is not of the right type";
    public static final String ERROR_NO_STEP_UP = "Cannot apply '..' action to ";
    public static final String ERROR_ORPHAN_NODE = " there is no parent";

    /**
     * Is this step a root node?
     * @return true iff we are a root node. The default value is false.
     */
    public boolean isRootStep() {
        return false;
    }

    /**
     * This is the operation that steps need to do, to execute a step.
     *
     * @return the result.
     * @throws CdlResolutionException if something failed.
     */
    public abstract StepExecutionResult execute(StepExecutionResult state) throws CdlResolutionException;


    /**
     * append zero or more reference parts to the current reference chain.
     * @param namespaces base to use for determining xmlns mapping
     * @param reference reference to build up
     */
    public abstract void appendReferenceParts(NamespaceLookup namespaces,Reference reference)
            throws CdlResolutionException;

}
