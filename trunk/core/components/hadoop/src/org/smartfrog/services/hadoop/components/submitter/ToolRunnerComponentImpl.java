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

package org.smartfrog.services.hadoop.components.submitter;

import org.apache.hadoop.util.Tool;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.Executable;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;
import java.util.Vector;

public class ToolRunnerComponentImpl extends PrimImpl implements ToolRunnerComponent, Executable {

    private WorkflowThread worker;
    private Tool tool;
    private static final int WORKER_TERMINATION_TIMEOUT = 1000;
    private String[] arguments;
    private String argumentsAsString;
    private ManagedConfiguration toolConf;

    public ToolRunnerComponentImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        String toolClassname = sfResolve(ATTR_TOOLCLASS, "", true).trim();
        if (toolClassname.isEmpty()) {
            throw new SmartFrogException("No tool declared in the " + ATTR_TOOLCLASS + " attribute");
        }
        ComponentHelper ch = new ComponentHelper(this);

        Vector<String> vector = ListUtils.resolveStringList(this, new Reference(ATTR_ARGUMENTS), true);
        arguments = new String[vector.size()];
        vector.copyInto(arguments);
        argumentsAsString = ListUtils.stringify(vector, "(", ", ", ")");
        if(sfLog().isInfoEnabled()) {
            sfLog().info("Running " + toolClassname + " with " + argumentsAsString);
        }

        Class toolClass = ch.loadClass(toolClassname);
        if (!Tool.class.isAssignableFrom(toolClass)) {
            throw new SmartFrogLifecycleException("Class " + toolClassname
                    + " does not implement the org.apache.hadoop.util.Tool interface");
        }
        try {
            Object instance = toolClass.newInstance();
            tool = (Tool) instance;
        } catch (InstantiationException e) {
            throw new SmartFrogLifecycleException("Class " + toolClassname
                    + " cannot be instantiated: " + e, e);
        } catch (IllegalAccessException e) {
            throw new SmartFrogLifecycleException("Class " + toolClassname
                    + " cannot be instantiated: " + e, e);
        }
        //sort out the configuration
        toolConf = ManagedConfiguration.createConfiguration(this, true, false, true);
        //start the tool in a new thread
        worker = new WorkflowThread(this, this, true);
        worker.start();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    @Override
    protected void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        WorkflowThread.requestAndWaitForThreadTerminationWithInterrupt(worker, WORKER_TERMINATION_TIMEOUT);
    }

    /**
     * Execute the tool
     *
     * @throws Throwable if something goes wrong
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    public void execute() throws Throwable {
        tool.setConf(toolConf);
        int returnCode = tool.run(arguments);
        sfReplaceAttribute(ATTR_RETURNCODE, returnCode);
        sfLog().info("Tool completed: " + returnCode);
        boolean failOnNonZeroReturnCode = sfResolve(ATTR_FAIL_ON_NON_ZERO_RETURN_CODE, true, true);
        if (returnCode != 0 && failOnNonZeroReturnCode) {
            throw new SmartFrogException("Return value of executing the tool " + tool
                    + " with arguments " + argumentsAsString
                    + " was " + returnCode);
        }
    }
}
