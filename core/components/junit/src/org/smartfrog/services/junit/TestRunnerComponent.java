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
package org.smartfrog.services.junit;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.compound.CompoundImpl;

import java.rmi.RemoteException;

/**
 * This is the test runner.
 * created 15-Apr-2004 15:44:41
 */

public class TestRunnerComponent extends CompoundImpl implements TestRunner {

    public TestRunnerComponent() throws RemoteException {
    }

    /**
     * who listens to the tests?
     */
    private TestListener listener;

    /**
     * flag to identify whether the task should fail when it is time
     */
    private boolean keepGoing = true;

    /**
     * fork into a new process?
     */
    private boolean fork = false;

    /**
     * validate our settings, bail out if they are invalid
     *
     * @throws SmartFrogInitException
     */
    private void validate() throws SmartFrogInitException {
        if (fork == true) {
            throw new SmartFrogInitException("forking is not yet implemented");
        }
    }

    /**
     * Deploy the compound. Deployment is defined as iterating over the context
     * and deploying any parsed eager components.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure deploying compound or
     *                                  sub-component
     * @throws java.rmi.RemoteException In case of Remote/nework error
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
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failed to start compound
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        Object o = sfResolve(ATTRIBUTE_LISTENER, listener, true);
        if (!(o instanceof TestListener)) {
            throw new SmartFrogException("The attribute " + ATTRIBUTE_LISTENER
                    + "must refer to an implementation of TestListener");
        }
        listener = (TestListener) o;
        fork = sfResolve(ATTRIBUTE_FORK, fork, false);
        keepGoing = sfResolve(ATTRIBUTE_KEEPGOING, keepGoing, false);
        validate();
    }

    public TestListener getListener() {
        return listener;
    }

    public void setListener(TestListener listener) {
        this.listener = listener;
    }

    public boolean getKeepGoing() {
        return keepGoing;
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public boolean getFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }
}
