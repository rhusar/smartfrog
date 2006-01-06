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
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;

import javax.xml.namespace.QName;

/**
 * A refroot step discards all that went before and moves to a new location.
 * created 06-Jan-2006 16:08:35
 */

public class StepRefRoot extends Step {

    private QName refroot;

    public StepRefRoot(QName refroot) {
        this.refroot = refroot;
    }

    public QName getRefroot() {
        return refroot;
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        return "~"+refroot+"/";
    }

    /**
     * This is the operation that steps need to do, to execute a step.
     *
     * @return the result.
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException
     *          if something failed.
     */
    public StepExecutionResult execute(StepExecutionResult state) throws CdlResolutionException {
        CdlDocument owner = state.getNode().getOwner();
        PropertyList target = owner.lookup(refroot);
        return state.next(target);
    }

}
