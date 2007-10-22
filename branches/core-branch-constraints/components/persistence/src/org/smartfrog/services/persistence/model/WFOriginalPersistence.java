/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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



package org.smartfrog.services.persistence.model;

import java.rmi.RemoteException;

import org.smartfrog.services.persistence.recoverablecomponent.RComponent;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * WFOriginalPersistence is the original WoodFrog persistence model.
 * This model assumes that children handle their own recovery and that references
 * to them rebind themselves. Children attributes are non-volatile and will be
 * serialized to the persistent storage.
 */
public class WFOriginalPersistence extends PersistenceModel {

    private boolean stable = false;

    private static String HOST = "sfHost";
    private static String LOG = "sfLog";

    {
        volatileAttrs.add(HOST);
        volatileAttrs.add(LOG);
    }


    /**
     * The constructor
     */
    public WFOriginalPersistence(ComponentDescription configdata) {
    }


    /**
     * {@inheritDoc}
     *
     * WFOriginalPersistence does not change the initial context.
     *
     */
    public void initialContext(Context context) throws
            SmartFrogDeploymentException {
        try {
            context.sfAddAttribute(RComponent.WFSTATUSENTRY,
                                   RComponent.WFSTATUS_DEAD);
        } catch (SmartFrogContextException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed to add status entry", ex);
        }
    }


    /**
     * {@inheritDoc}
     *
     * WFOriginalPersistence does not change the recovering context
     *
     */
    public void recoverContext(Context context) throws
            SmartFrogDeploymentException {
        // nothing to do here
    }


    /**
     * {@inheritDoc}
     *
     * WFOriginalPersistence does not redeploy - it assumes children recover
     * themselves
     *
     * @return boolean - false
     */
    public boolean redeploy(Prim component) {
        return false;
    }


    /**
     * {@inheritDoc}
     *
     * WFOriginalPersistence does not restart - it assumes children recover
     * themselves
     *
     * @return boolean - false
     */
    public boolean restart(Prim component) {
        return false;
    }


    /**
     * {@inheritDoc}
     *
     * WFOriginalPersistence leaves a tomb stone behind after termination if
     * it got past its initial startup, otherwise it doesn't.
     *
     */
    public boolean leaveTombStone(Prim Component, TerminationRecord tr) {
        try {
            if (Component.sfIsStarted()) {
                Component.sfReplaceAttribute(RComponent.WFSTATUSENTRY,
                                             RComponent.WFSTATUS_DEAD);
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Impossible to write on stable storage",
                                       ex);
        }
    }


    /**
     * {@inheritDoc}
     *
     * WFOriginalPersistence Commit points are after start and after recovery
     *
     */
    public boolean isCommitPoint(Prim component, String point) throws
            SmartFrogException, RemoteException {

        if (CommitPoints.POST_START.equals(point)) {
            // System.out.println(" ===== " + point + " is a commit point");
            component.sfReplaceAttribute(RComponent.WFSTATUSENTRY,
                                         RComponent.WFSTATUS_STARTED);
            stable = true;
            return true;
        } else if (CommitPoints.POST_RECOVER.equals(point)) {
            // System.out.println(" ===== " + point + " is a commit point");
            return true;
        } else {
            // System.out.println(" ===== " + point + " is not a commit point");
            return false;
        }
    }


}
