/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.rpm.manager.local;

import org.smartfrog.sfcore.utils.Executable;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.rpm.manager.RpmManagedFile;

import java.rmi.RemoteException;
import java.util.List;

/**
 *
 */
public abstract class AbstractLocalRpmExecutor extends AbstractLocalRpmManager implements Executable {

    protected String executable;
    protected List<String> arguments;
    List<RpmManagedFile> files;
    protected WorkflowThread worker;
    protected String command;

    protected AbstractLocalRpmExecutor() throws RemoteException {
    }

    protected void readSettings() throws SmartFrogResolutionException, RemoteException {
        executable = sfResolve(ATTR_EXECUTABLE, "", true);
        command = sfResolve(ATTR_COMMAND, "", true);
        arguments = ListUtils.resolveStringList(this, new Reference(ATTR_ARGUMENTS), true);
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        SmartFrogThread.ping(worker);
    }

    /**
     * Deregisters from all current registrations.
     *
     * @param status Record having termination details of the component
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread.requestThreadTermination(worker);
    }

    public abstract void execute() throws Throwable;
}
