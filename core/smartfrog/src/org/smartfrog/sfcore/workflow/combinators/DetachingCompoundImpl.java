/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.workflow.combinators;

import java.rmi.RemoteException;
import java.util.Enumeration;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;


/**
 * Implements the detaching compound component behaviour. A detaching compound
 * deploys component descriptions, and may detach them from its liveness after
 * succesful deployment, or detach itself from its parent. After this, the
 * component can be set to terminate itself.
 *
 */
public class DetachingCompoundImpl extends CompoundImpl implements DetachingCompound {

    /**
     * Name of the component.
     */
    private Reference name = null;
    /**
     * Set to true if you want the compound to detach its children on start
     */
    private boolean detachDownwards;
    /**
     * Set to true if you want the compound to detach itself on start
     */
    private boolean detachUpwards;
    /**
     * Set to true if you want the compound to terminate at the end of the run
     */
    private boolean autoDestruct;
    /**
     * Constructs DetachingCompoundImpl.
     * @throws RemoteException if there is any network or RMI error
     */
    public DetachingCompoundImpl() throws RemoteException {
    }

    /**
     * Collects the values of the behaviour booleans on the deploy phase and
     * deploys the component.
     *
     * @throws SmartFrogException In case of error while deployment
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        try {
            super.sfDeploy();
            detachDownwards = ((Boolean) sfResolve("detachDownwards")).booleanValue();
            detachUpwards = ((Boolean) sfResolve("detachUpwards")).booleanValue();
            autoDestruct = ((Boolean) sfResolve("autoDestruct")).booleanValue();
        } catch (Exception t) {
            throw SmartFrogLifecycleException.sfDeploy("",t , this);
        }
    }

    /**
     * Starts the detaching compound. This sends sfStart to all managed
     * components in the compound context, then detaches them from itself if
     * detachDownwards has been set to true in the compound description. The
     * compound detaches itself from its parent if detachUpwards has been set
     * to true.Failures while detaching cause autodestruct to be set to true,
     * and a flag indicates abnormal termination. Components that did not
     * detach will terminate, since there liveness times out. On completion
     * (successful or not) the compound terminates itself if autoDestruct has
     * been set to true.
     *
     * @throws SmartFrogException if failed to start detaching compound
     * @throws RemoteException In case of Remote/network error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        try {
            super.sfStart();
            Thread detacher = new Thread(new Detacher());

            if (detachDownwards || detachUpwards || autoDestruct) {
                detacher.start();
            }
        } catch (Throwable t) { // catch throwable as user code is involved
             throw SmartFrogLifecycleException.sfDeploy("",t , this);
        }
    }

    /**
     * Non-static inner class to handle detachment
     */
    private class Detacher implements Runnable {
        public void run() {
            // detach this compound
            if (detachUpwards) {
                try {
                    sfDetach();
                } catch (SmartFrogException dex) {
                    sfTerminate(TerminationRecord.abnormal(
                            "DetachingCompound failed to detach ",
                            null,
                            dex));

                    return;
                } catch (RemoteException rex) {
                    sfTerminate(TerminationRecord.abnormal(
                            "DetachingCompound failed to detach due to remote exception",
                            null,
                            rex));

                    return;
                }
            }

            if (detachDownwards) {
                // detach all children
            	for (Prim child:sfChildList()) {
                    try {
                        child.sfDetach();
                    } catch (SmartFrogException remex) {
                        sfTerminate(TerminationRecord.abnormal(
                                "DetachingCompound failed to detach children",
                                null,
                                remex));

                        return;
                    } catch (RemoteException rex) {
                        sfTerminate(TerminationRecord.abnormal(
                                "DetachingCompound failed to detach children due to remote exception",
                                null,
                                rex));

                        return;
                    }
                }

                if (autoDestruct) {
                    try {
                        name = sfCompleteName();
                    } catch (Exception ignore) {
                    }

                    sfTerminate(TerminationRecord.normal(name));
                }
            }
        }
    }
}
