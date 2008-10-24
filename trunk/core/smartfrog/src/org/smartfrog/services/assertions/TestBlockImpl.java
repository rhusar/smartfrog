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
package org.smartfrog.services.assertions;

import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.workflow.combinators.DelayedTerminator;
import org.smartfrog.sfcore.workflow.events.DeployedEvent;
import org.smartfrog.sfcore.workflow.events.StartedEvent;
import org.smartfrog.sfcore.workflow.events.TerminatedEvent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogExtractedException;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.services.assertions.events.TestStartedEvent;

import java.rmi.RemoteException;

/**
 * created 13-Oct-2006 16:46:44
 *
 * The testblock sends out lifecycle events to anyone interested; this can be used
 * by monitors to walk the testblock through a controlled state sequence, and to await test
 * results.
 * Test results are notified as a {@link TestCompletedEvent}
 */

public class TestBlockImpl extends EventCompoundImpl implements TestBlock {

    private volatile boolean finished=false;
    private volatile boolean failed = false;
    private volatile boolean succeeded = false;
    private volatile boolean forcedTimeout = false;
    private volatile TerminationRecord status;
    private DelayedTerminator actionTerminator;
    private volatile Prim actionPrim;
    private String description;
    public static final String ERROR_STARTUP_FAILURE = "Failed to start up action";

    public TestBlockImpl() throws RemoteException {
    }

    /**
     * Return true iff the component is finished.
     * Spin on this, with a (delay) between calls
     *
     * @return true if we are finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @return true only if the test has finished and failed
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * @return true iff the test succeeded
     */

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
     * return the current action
     * @return the child component. this will be null after termination.
     */
    public Prim getAction() {
        return actionPrim;
    }

    /**
     * {@inheritDoc}
     * @return false always
     */
    public boolean isSkipped() {
        return false;
    }

    /**
     * Registers components referenced in the SendTo sub-component registers itself with components referenced in the
     * RegisterWith sub-component.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while deploying the
     * component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        description = sfResolve(ATTR_DESCRIPTION, description, false);
        checkActionDefined();
        sendEvent(new DeployedEvent(this));
    }


    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * A TestStartedEvent will always be sent.
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        try {
            super.sfStart();
            startChildAction();
        } finally {
            sendEvent(new TestStartedEvent(this));
        }
    }

    /**
     * Called in sfStart to start the child action.
     * Can be overridden to disable that action, in which case some derived form of the
     * logic must be repeated to start the action and (always) send a TestStartedEvent.
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException In case of Remote/nework error
     */
    protected void startChildAction() throws RemoteException, SmartFrogException {
        long timeout = sfResolve(ATTR_TIMEOUT,0L,true);
        boolean expectTimeout=sfResolve(ATTR_EXPECTTIMEOUT,false,true);
        sendEvent(new StartedEvent(this));
        try {
            actionPrim =sfCreateNewChild(ACTION,action, null);
            if(timeout>0) {
                actionTerminator=new DelayedTerminator(actionPrim, timeout, sfLog(), ATTR_TIMEOUT,expectTimeout);
                actionTerminator.start();
            }
        } catch (RemoteException e) {
            startupException(e);
        } catch (SmartFrogDeploymentException e) {
            startupException(e);
        }
    }


    /**
     * Send out notifications of termination
     * @param record exit status
     */
    public void sfTerminateWith(TerminationRecord record) {
        sendEvent(new TerminatedEvent(this, record));
        super.sfTerminateWith(record);
    }

    /**
     * turn a startup exception into a cleaner exit
     * @param e exception
     * @throws SmartFrogRuntimeException smartfrog problems
     * @throws RemoteException RMI problems
     */
    private void startupException(Exception e)
            throws SmartFrogRuntimeException, RemoteException {
        //this is called if we failed during startup
        TerminationRecord fault = TerminationRecord.abnormal(ERROR_STARTUP_FAILURE, getName(), e);
        end(fault);
    }

    /**
     * log the end of the event. This may trigger workflow termination.
     * does nothing if finished==true.
     * @param record termination record
     * @throws SmartFrogRuntimeException smartfrog problems
     * @throws RemoteException RMI problems
     */
    private synchronized void end(TerminationRecord record)
            throws SmartFrogRuntimeException, RemoteException {
        if(finished) {
            //non-reentrant
            return;
        }
        finished=true;
        //guarantee that the cause is shareable
        record.setCause(SmartFrogExtractedException.convert(record.getCause()));
        status = record;
        succeeded=record.isNormal();
        failed=!succeeded;
        if(actionTerminator!=null) {
            //test to see if the terminator caused the shutdown
            forcedTimeout = actionTerminator.isForcedShutdown();
            //and terminate it quietly too.
            actionTerminator.shutdown(false);
            actionTerminator = null;
        }
        setTestBlockAttributes(record, forcedTimeout);
        //send out a completion event
        sendEvent(new TestCompletedEvent(this,succeeded,forcedTimeout, false, record, description));
        //this can trigger a shutdown if we want it
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(record);
    }


    /**
     * This is an override point; it is where subclasses get to change their workflow
     * depending on what happens underneath.
     * It is only called outside of component termination, i.e. when {@link #isWorkflowTerminating()} is
     * false, and when the comp parameter is a child, that is <code>sfContainsChild(comp)</code> holds.
     * If the the method returns true, the event is forwarded up the object heirarchy, which
     * will eventually trigger a component termination.
     * <p/>
     * Always return false if you start new components from this method!
     * </p>
     *
     * @param record exit record of the component
     * @param comp   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     * @throws SmartFrogRuntimeException for runtime exceptions
     * @throws RemoteException for network problems
     */
    protected boolean onChildTerminated(TerminationRecord record, Prim comp)
            throws SmartFrogRuntimeException, RemoteException {
        if(comp == actionPrim) {
            //this is the action terminating,
            //forget about our now-terminated child (it cannot be serialized any more)
            actionPrim =null;
            //log the closure and continue
            end(record);
            return false;
        } else {
            //something unknown
            return true;
        }
    }

    /**
     * Set the various attributes of the component
     * based on whether the test record was success or not
     * @param record termination record
     * @param timeout did we time out
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException network errors
     */
    public void setTestBlockAttributes(
            TerminationRecord record,
            boolean timeout)
            throws SmartFrogRuntimeException, RemoteException {
        boolean success=record.isNormal();
        sfLog().debug("Terminated Test with status "+record+" timeout="+timeout);
        sfReplaceAttribute(ATTR_STATUS,record);
        sfReplaceAttribute(ATTR_FINISHED, Boolean.TRUE);
        sfReplaceAttribute(ATTR_SUCCEEDED, Boolean.valueOf(success));
        sfReplaceAttribute(ATTR_FAILED, Boolean.valueOf(!success));
        sfReplaceAttribute(ATTR_FORCEDTIMEOUT, Boolean.valueOf(timeout));
    }
}
