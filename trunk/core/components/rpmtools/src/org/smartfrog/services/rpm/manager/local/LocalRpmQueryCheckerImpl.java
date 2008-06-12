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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.utils.Executable;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.rpm.manager.RpmFile;
import org.smartfrog.services.rpm.manager.RpmManager;
import org.smartfrog.services.rpm.manager.RpmManagedFile;
import org.smartfrog.services.shellscript.Cmd;
import org.smartfrog.services.shellscript.RunProcessImpl;
import org.smartfrog.services.shellscript.RunProcess;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;

/**
 * this checker 
 * Created 10-Jun-2008 17:13:23
 *
 */

public class LocalRpmQueryCheckerImpl extends AbstractLocalRpmManager implements RpmManager,Executable {

    protected String executable;
    protected List<String> arguments;
    List<RpmManagedFile> files;

    protected WorkflowThread worker;
    private String command;

    public LocalRpmQueryCheckerImpl() throws RemoteException {
    }


    /**
     * start up may trigger a shutdown
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        executable = sfResolve(ATTR_EXECUTABLE, "", true);
        command = sfResolve(ATTR_COMMAND, "", true);
        arguments = ListUtils.resolveStringList(this, new Reference(ATTR_ARGUMENTS), true);
    }

    /**
     * build the list of files
     * @throws SmartFrogLivenessException problems
     * @throws RemoteException    In case of network/rmi error
     */
    protected void probeAllFiles() throws SmartFrogLivenessException, RemoteException {
        files = new ArrayList<RpmManagedFile>();
        for (RpmFile rpm : this) {
            List<String> rpmfiles = rpm.getManagedFiles();
            for(String f:rpmfiles) {
                files.add(new RpmManagedFile(f,rpm));
            }
        }
        new WorkflowThread(this, this, true);
    }

    /**
     * Override point: probe the file
     *
     * @param rpm the file to probe
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for network problems
     */
    @Override
    protected void probe(RpmFile rpm) throws SmartFrogLivenessException, RemoteException {
        try {
            rpm.verifyAllManagedFilesExist();
        } catch (FileNotFoundException e) {
            throw new SmartFrogLivenessException(e);
        }
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

    /**
     * Entry point for worker thread; this runs through the list of files and validates them
     * @throws Throwable
     */
    public void execute() throws Throwable {

        for (RpmManagedFile managedFile : files) {
            //executable+args+filename and check for error/valid strings; make those options
            queryOneFile(managedFile);
        }
    }

    /**
     * run a query against one file
     * @param managedFile the file to look at
     * @throws SmartFrogException trouble
     */
    private void queryOneFile(RpmManagedFile managedFile) throws SmartFrogException {
        String filename=managedFile.getFilename();
        String packageName = managedFile.getRpm().getRpmPackageName();
        List<String> commands=new ArrayList<String>(arguments.size()+2);
        commands.add(command);
        commands.addAll(arguments);
        commands.add(filename);
        Cmd rpmcommand=new Cmd();
        rpmcommand.setTerminate(false);
        rpmcommand.setDetach(false);
        rpmcommand.setCmdArray((String[]) commands.toArray());
        RunProcess process = new RunProcessImpl("rpm", rpmcommand, this);
        process.run();
        int exitValue = process.getExitValue();
        if (exitValue != 0) {
            throw new SmartFrogLivenessException("Failed to validate " + managedFile);
        }
    }


}
