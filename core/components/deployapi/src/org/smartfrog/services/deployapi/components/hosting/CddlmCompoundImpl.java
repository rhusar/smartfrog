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
package org.smartfrog.services.deployapi.components.hosting;


import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Constants.LifecycleStateEnum;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimHook;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

/**
 * created Sep 8, 2004 2:33:27 PM
 */

public class CddlmCompoundImpl extends CompoundImpl
        implements CddlmCompound {

    private URI jobURI;

    private Job job;

    public static final String ERROR_UNKNOWN_JOB_URI = "No job of that address defined:";

    public CddlmCompoundImpl() throws RemoteException {
    }

    /**
     * Deploy the compound. Deployment is defined as iterating over the context
     * and deploying any parsed eager components.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException failure deploying compound or sub-component
     * @throws java.rmi.RemoteException    In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException failed to start compound
     * @throws java.rmi.RemoteException    In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        //attributes
        String uri= sfResolve(ATTR_JOBURI, (String) null, true);
        //make the URI
        try {
            jobURI = new URI(uri);
        } catch (URISyntaxException e) {
            throw SmartFrogException.forward(e);
        }
        //find and bind to the job
        ServerInstance server = ServerInstance.currentInstance();
        job = server.getJobs().lookup(jobURI);
        if (job == null) {
            throw new SmartFrogDeploymentException(
                    ERROR_UNKNOWN_JOB_URI + uri);
        }
        //now add our hooks
        LifecycleEventHook deploy=new LifecycleEventHook(job, LifecycleStateEnum.instantiated);
        LifecycleEventHook start = new LifecycleEventHook(job, LifecycleStateEnum.running);
        LifecycleEventHook terminate = new LifecycleEventHook(job, LifecycleStateEnum.terminated);
        sfDeployHooks.addHook(deploy);
        sfStartHooks.addHook(start);
        sfTerminateWithHooks.addHook(terminate);

    }

    /**
     * lifecycle events
     */
    public static class LifecycleEventHook implements PrimHook {

        Job job;
        Constants.LifecycleStateEnum state;

        public LifecycleEventHook(Job job, Constants.LifecycleStateEnum state) {
            this.job = job;
            this.state = state;
        }

        /**
         * forward this event to the system
         * @param prim
         * @param terminationRecord
         * @throws org.smartfrog.sfcore.common.SmartFrogException
         */
        public void sfHookAction(Prim prim,
                                 TerminationRecord terminationRecord)
                throws SmartFrogException {
            String info=null;
            //if we died
            if(terminationRecord!=null) {
                //we tell the system that
                job.enterTerminatedStateNotifying(terminationRecord);
            } else {
                //otherwise, tell it of the fact we entered our known state
                job.enterStateNotifying(state,info);
            }
        }
    }

}
