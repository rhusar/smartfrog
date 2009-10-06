/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.languages.cdl.process.PropertyListOperation;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.resolving.ResolveEnum;

import java.io.IOException;

import nu.xom.Node;

/**
 * Operation to infer early reference state. That is, it turns lazy links into
 * complete ones, for they are complete as far as early ref resolution is concerned.
 * this operation must be run "after", as it infers state from its children
 * created 18-Jan-2006 13:28:37
 */

public class InferEarlyReferenceState implements PropertyListOperation {

    /**
     * Apply an operation to a node.
     *
     * @param target
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     */
    public void apply(PropertyList target) throws CdlException, IOException {
        ResolveEnum state;
        state=target.inferLocalResolutionState();
        if(state==ResolveEnum.ResolvedLazyLinksRemaining) {
            //turn lazy links into complete ones, because there is no more resolution to do here
            state=ResolveEnum.ResolvedComplete;
        }
        if(state==ResolveEnum.ResolvedUnknown) {
            //get info from our immediate children.
            for(Node node:target) {
                if(node instanceof PropertyList) {
                    PropertyList child = (PropertyList) node;
                    ResolveEnum childState = child.getResolveState();
                    state=state.merge(childState);
                }
            }
        }
        target.setResolveState(state);
    }
}
