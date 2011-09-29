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
package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;


import java.rmi.RemoteException;

/**
 * Deploy a component
 */
public class ActionDeployWait extends ActionDeploy {
   long poll = 1000;

    /**
     * Override point; call the deployment operations
     * @param configuration configuration to deploy
     * @param name name of the component
     * @param parent parent flag
     * @param targetP target process (can be null)
     * @return the deployed prim
     * @throws org.smartfrog.sfcore.common.SmartFrogException for deployment problems
     * @throws java.rmi.RemoteException for network problems
     */
    protected Prim doDeploy(ConfigurationDescriptor configuration,
                            String name,
                            Prim parent,
                            ProcessCompound targetP) throws SmartFrogException, RemoteException {
        Prim prim = super.doDeploy(configuration, name, parent, targetP);
        try {
            waitForTermination(prim);
        } catch (Exception e) {
            throw SmartFrogLifecycleException.sfTerminate("failed to detect termination of component", e, null);
        }
        return prim;
    }

    /**
     * Wait for the given component to terminate, then return.
     * Checks sfIsTerminated every second.
     * @param p the component
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    private void waitForTermination(Prim p) throws Exception {
       while(true) {
           if (p.sfIsTerminated()) {
               break;
           }
           Thread.sleep(poll);
       }
    }
}
