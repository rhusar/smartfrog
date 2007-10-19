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
package org.smartfrog.sfcore.languages.cdl.resolving;

import org.smartfrog.sfcore.languages.cdl.process.PropertyListOperation;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;

import java.io.IOException;

/**
 * Operation to put the node into any state we like
 * created 18-Jan-2006 13:23:44
 */

public class ResetStateOperation implements PropertyListOperation {

    private ResolveEnum state;

    public ResetStateOperation(ResolveEnum state) {
        this.state = state;
    }

    /**
     * Apply an operation to a node.
     *
     * @param node
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     */
    public void apply(PropertyList node) throws CdlException, IOException {
        node.setResolveState(state);
    }
}
