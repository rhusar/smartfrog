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
package org.smartfrog.services.sfunit;

import org.smartfrog.services.xunit.base.RunnerConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;

import java.rmi.RemoteException;

/** created 08-Jan-2007 14:57:40 */

public class SmartFrogTestSuiteImpl extends EventCompoundImpl
        implements SmartFrogTestSuite {
    private volatile boolean finished = false;
    private volatile boolean failed = false;
    private volatile boolean succeeded = false;
    private volatile boolean forcedTimeout = false;
    private volatile TerminationRecord status;

    public SmartFrogTestSuiteImpl() throws RemoteException {
    }

    /**
     * Return true iff the component is finished. Spin on this, with a (delay) between calls
     *
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    /** @return true only if the test has finished and failed */
    public boolean isFailed() {
        return failed;
    }

    /** @return true iff the test succeeded */

    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Get the exit record
     *
     * @return the exit record, will be null for an unfinished child
     */


    public TerminationRecord getStatus() {
        return status;
    }

    /**
     * return the tests prim
     *
     * @return the child component. this will be null after termination.
     */
    public Prim getAction() {
        return null;
    }


    /**
     * declare that the old notation is not supported
     *
     * @return false
     */
    protected boolean isOldNotationSupported() {
        return false;
    }


    /**
     * This is an override point; it is where subclasses get to change their workflow depending on what happens
     * underneath. It is only called outside of component termination, i.e. when {@link #isWorkflowTerminating()} is
     * false, and when the comp parameter is a child, that is <code>sfContainsChild(comp)</code> holds. If the the
     * method returns true, we terminate the component. <p/> Always return false if you start new components from this
     * method! </p>
     *
     * For the test suite, we handle the termination of a child by reporting it to the container.
     *
     * @param status exit record of the component
     * @param comp   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     * @throws SmartFrogRuntimeException for runtime exceptions
     * @throws RemoteException for network problems
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) throws SmartFrogRuntimeException, RemoteException {
        return super.onChildTerminated(status, comp);
    }


    /**
     * bind to the configuration. A null parameter means 'stop binding'
     *
     * @param configuration configuration to bind to
     * @throws SmartFrogException for other problems
     * @throws RemoteException for network problems
     */
    public void bind(RunnerConfiguration configuration) throws RemoteException, SmartFrogException {

    }

    /**
     * run the tests
     *
     * @return true if they worked
     * @throws RemoteException for network problems
     * @throws SmartFrogException for other problems
     */
    public boolean runTests() throws RemoteException, SmartFrogException {
        super.synchCreateChildren();
        return false;
    }
}
