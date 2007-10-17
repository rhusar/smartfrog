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

package org.smartfrog.sfcore.processcompound;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;


/**
 * Implements a specialized description deployer. This deployer uses
 * sfProcessName attributes in addition to those used by PrimHostDeployerImpl
 * to locate the appropriate remote ProcessCompound off of the root process
 * compound on that host to forward descriptions to.
 *
 */
public class PrimProcessDeployerImpl extends PrimHostDeployerImpl {
    /** Efficiency holder for sfProcessName reference. */
    protected static final Reference refProcessName = new Reference(
                SmartFrogCoreKeys.SF_PROCESS_NAME);

    /**
     * Constructs PrimHostDeployerImpl object with component description.
     *
     * @param descr description of the component that is to be deployed
     */
    public PrimProcessDeployerImpl(ComponentDescription descr) {
        super(descr);
    }

    /**
     * Returns the process compound with a particular process name.
     * @return process compound on host with name
     *
     * @throws Exception if failed to find process compound
     */
    protected ProcessCompound getProcessCompound() throws Exception {
        // get root process compound if sfProcessHost is specified,
        // returns this process otherwise;
        ProcessCompound hostCompound = super.getProcessCompound();

        // try to look up process name; if it exists, look up in the
        // root process compound (parent of the current?)
        String processName = null;

        try {
            processName = (String) target.sfResolve(refProcessName);
        } catch (SmartFrogResolutionException resex) {
            // there is no process name, so use the specified hosts root/current process
            return hostCompound;
        }

        return hostCompound.sfResolveProcess(processName, target);
    }
}
