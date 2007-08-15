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

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogExtractedException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.combinators.DelayedTerminator;
import org.smartfrog.sfcore.workflow.conditional.ConditionCompound;
import org.smartfrog.sfcore.workflow.events.DeployedEvent;
import org.smartfrog.sfcore.workflow.events.StartedEvent;
import org.smartfrog.sfcore.workflow.events.TerminatedEvent;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ShouldDetachOrTerminate;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.services.assertions.events.TestStartedEvent;

import java.rmi.RemoteException;

/**
 * Runner of test children.
 *
 * This compound sends lifecycle events to any listener, and it sends a TestCompletedEvent for every child that
 * completes
 *
 * created 22-Sep-2006 16:43:35
 */

public class TestCompoundImpl extends ConditionCompound
        implements TestCompound {
    private ComponentDescription waitForCD;
    private ComponentDescription tests;
    private Prim testsPrim;

    protected static final String ACTION_RUNNING = ATTR_ACTION;
    protected static final String TESTS_RUNNING = ATTR_TESTS;
    private long undeployAfter;
    private long testTimeout;
    private boolean expectTerminate;
    private DelayedTerminator actionTerminator;
    private DelayedTerminator testsTerminator;
    private Prim actionPrim;
    private String exitType;
    private String exitText;
    private String description;
    private volatile boolean finished = false;
    private volatile boolean failed = false;
    private volatile boolean succeeded = false;
    private volatile boolean forcedTimeout = false;
    private volatile boolean skipped = false;
    private volatile TerminationRecord status;
    private volatile TerminationRecord actionTerminationRecord;
    private volatile TerminationRecord testsTerminationRecord;

    private boolean shouldTerminate;
    /**
     * The message of a forced shutdown. This is important, as we look for it when the component is terminated, and can
     * use its presence to infer that the helper thread did the work.
     */
    public static final String FORCED_TERMINATION = "timed shutdown of test components";
    public static final String TEST_FAILED_WRONG_STATUS = "Expected action to terminate with the status ";
    public static final String EXIT_EXPECTED_STARTUP_EXCEPTION = "Exiting with expected exception thrown during startup";
    public static final String UNEXPECTED_STARTUP_EXCEPTION = "Unexpected message in an exception raised at startup time\n";

    public TestCompoundImpl() throws RemoteException {
    }


    /**
     * Deploys and reads the basic configuration of the component. Overrides EventCompoundImpl.sfDeploy.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                         In case of any error while deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        //look for the action
        checkActionDefined();
        name = sfCompleteNameSafe();
        waitForCD = sfResolve(ATTR_WAITFOR, waitForCD, false);
        tests = sfResolve(ATTR_TESTS, tests, false);
        testTimeout = sfResolve(ATTR_TEST_TIMEOUT, 0L, true);
        undeployAfter = sfResolve(ATTR_UNDEPLOY_AFTER, 0L, true);
        expectTerminate = sfResolve(ATTR_EXPECT_TERMINATE, false, true);
        exitType = sfResolve(ATTR_EXIT_TYPE, exitType, true);
        exitText = sfResolve(ATTR_EXIT_TEXT, exitText, true);
        shouldTerminate = sfResolve(ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE, true, true);
        description = sfResolve(ATTR_DESCRIPTION, description, false);
        sendEvent(new DeployedEvent(this));
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //report we have started up
        sendEvent(new StartedEvent(this));
        Exception exception = null;
        //deploy and evaluate the condition.
        //then decide whether to run or not.

        if (getCondition() != null && !evaluate()) {
            sendEvent(new TestStartedEvent(this));
            skipped = true;
            updateFlags(false);
            String message = "Skipping test run " + name;
            sfLog().info(message);
            //send a test started event
            //followed by a the closing results
            endTestRun(TerminationRecord.normal(message, getName()));
            //initiate cleanup
            finish();
            //end: do not deploy anything else
            return;
        }

        //deploy the action under a terminator, then the assertions, finally teardown afterwards.
        final boolean isNormalTerminationExpected = TerminationRecord.NORMAL.equals(exitType);
        try {
            actionPrim = deployComponentDescription(ACTION_RUNNING, action);
        } catch (SmartFrogDeploymentException e) {
            //did we expect a normal termination?
            if (isNormalTerminationExpected) {
                //if so, it didnt happen. rethrow the exception
                throw e;
            }
            //we expected failure, so cache it for-post mortem analsysis
            exception = e;
        } finally {
            sendEvent(new TestStartedEvent(this));
        }

        if (exception == null) {
            //a null exception meant the action was deployed successfully

            //if we get here. then it is time to actually start the action.
            //exceptions are caught and compared to expectations.
            try {
                actionPrim.sfStart();
            } catch (SmartFrogException e) {
                //split on normal/abnormal.
                if (isNormalTerminationExpected) {
                    throw e;
                }
                exception = e;

            } catch (RemoteException e) {
                if (isNormalTerminationExpected) {
                    throw e;
                }
                exception = e;
            }
        }

        //did we catch something during deployment?
        if (exception != null) {
            //get the message and check it against expections
            TerminationRecord record;
            String message = exception.getMessage();
            if (message == null) {
                message = "";
            }
            if (message.indexOf(exitText) < 0) {
                //an exit code of an unknown type?
                record = TerminationRecord.abnormal(UNEXPECTED_STARTUP_EXCEPTION, getName(), exception);
                setStatus(record);
                actionTerminationRecord = record;
                updateFlags(false);
                endTestRun(record);
                //then throw an exception
                throw new SmartFrogException(UNEXPECTED_STARTUP_EXCEPTION
                        + "expected: '" + exitText + "'\n"
                        + "found   : '" + message + "'\n",
                        exception);
            }
            //valid exit. Save the results, then
            record = TerminationRecord.normal(EXIT_EXPECTED_STARTUP_EXCEPTION,
                    getName(), exception);
            setStatus(record);
            actionTerminationRecord = record;
            updateFlags(true);
            endTestRun(record);
            //and optionally end the component
            new ComponentHelper(this).sfSelfDetachAndOrTerminate(record);

        } else {
            //the action is deployed
            //start the terminator
            actionTerminator = new DelayedTerminator(actionPrim, undeployAfter, sfLog(),
                    FORCED_TERMINATION,
                    !expectTerminate);
            actionTerminator.start();

            //now deploy the tests.
            startTests();
        }

    }

    /**
     * Start the test runner and a terminator
     *
     * @throws SmartFrogDeploymentException smartfrog problems
     * @throws RemoteException              RMI problems
     */
    private void startTests() throws RemoteException, SmartFrogDeploymentException {
        if (tests != null) {
            testsPrim = sfCreateNewChild(TESTS_RUNNING, tests, null);
            //the test terminator reports a termination as a failure
            testsTerminator = new DelayedTerminator(testsPrim, testTimeout, sfLog(),
                    FORCED_TERMINATION,
                    false);
            testsTerminator.start();
        }
    }

    /**
     * Called by {@link #sfPing(Object)} to run through the list of children and ping each in turn. If any child fails,
     * {@link #sfLivenessFailure(Object,Object,Throwable)} is called and the iteration continues. <p/> Override this
     * method to implement different child ping behaviour.
     */
    protected void sfPingChildren() {

        if (actionPrim != null) {
            sfPingChildAndTerminateOnFailure(actionPrim);
        }
        if (testsPrim != null) {
            sfPingChildAndTerminateOnFailure(testsPrim);
        }
    }

    /**
     * When terminating we shutdown the action and the tests, and Send out notifications of termination
     *
     * @param record exit status
     */
    public synchronized void sfTerminateWith(TerminationRecord record) {
        sendEvent(new TerminatedEvent(this, record));
        super.sfTerminateWith(record);
        shutdown(actionTerminator);
        shutdown(testsTerminator);
    }

    /**
     * Shut down the specified terminator if we need to.
     *
     * We assume the superclass terminator is already terminating the children, so there is no need to tell the action
     * terminator to do it. Therefore, this should only be called from {@link #sfTerminateWith(TerminationRecord)}
     *
     * @param terminator terminator to shut down
     */
    private void shutdown(DelayedTerminator terminator) {
        if (terminator != null) {
            terminator.shutdown(false);
        }
    }


    /**
     * This is an override point; it is where subclasses get to change their workflow depending on what happens
     * underneath. It is only called outside of component termination, i.e. when {@link #isWorkflowTerminating()} is
     * false, and when the comp parameter is a child, that is <code>sfContainsChild(comp)</code> holds. If the the
     * method returns true, the event is forwarded up the object heirarchy, which will eventually trigger a component
     * termination. <p/> Always return false if you start new components from this method! </p>
     *
     * @param childStatus exit record of the component
     * @param child       child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    protected boolean onChildTerminated(TerminationRecord childStatus, Prim child) {
        boolean propagateTermination = shouldTerminate;
        TerminationRecord exitRecord = null;
        boolean testSucceeded;
        if (actionPrim == child) {
            actionTerminationRecord = childStatus;
            //child termination
            if (actionTerminator != null && actionTerminator.isForcedShutdown() && !expectTerminate) {
                //this is a forced shutdown, all is well
                sfLog().info("Forced shutdown of test components (expected)");
                testSucceeded=true;
            } else {
                //not a forced shutdown, so why did it die?
                boolean expected = false;
                if (expectTerminate) {

                    //act on whether or not a fault was expected.
                    if (childStatus.errorType.indexOf(exitType) >= 0) {
                        //we have a match
                        sfLog().debug("Exit type is as expected");
                        expected = true;
                        if (exitText != null && exitText.length() > 0) {
                            String description = childStatus.description;
                            if (description == null) {
                                description = "";
                            }

                            if (description.indexOf(exitText) < 0) {
                                sfLog().info("Action text mismatch: expected \""
                                        + exitText
                                        + "\" but got \""
                                        + description
                                        + "\"");
                                expected = false;
                            }
                        }

                    } else {
                        //wrong exit type
                        sfLog().info("Action Exit type mismatch");
                        expected = false;
                    }
                    if (!expected) {
                        testSucceeded = false;
                        //the action prim terminated in a way that was not expected
                        //
                        String errorText = TEST_FAILED_WRONG_STATUS + exitType +
                                '\n'
                                + "and error text '" + exitText + "'\n"
                                + "but got " + childStatus;
                        sfLog().error(errorText);
                        exitRecord = TerminationRecord.abnormal(errorText,
                                childStatus.id,
                                childStatus.getCause());
                    } else {
                        //expected action termination.
                        //now look at the record, and if it is abnormal, convert it
                        //to a normal status, preserving the message
                        testSucceeded = true;
                        sfLog().info("Action terminated abnormally, as expected");
                        if (!childStatus.isNormal()) {
                            exitRecord = TerminationRecord.normal(
                                    childStatus.description,
                                    childStatus.id,
                                    childStatus.getCause());
                        }

                    }
                } else {
                    //child terminated and it was not expected. This is an error.
                    sfLog().error("Unexpected termination of action");
                    exitRecord = childStatus;
                    testSucceeded = false;
                    if (childStatus.isNormal()) {
                        //flip this to abnormal
                        exitRecord = TerminationRecord.abnormal(
                                childStatus.description,
                                childStatus.id,
                                childStatus.getCause());
                    } else {
                        exitRecord = childStatus;
                    }
                }

            }
        } else if (child == testsPrim) {
            //tests are terminating.
            testsTerminationRecord = childStatus;
            //it is an error if these terminated abnormally, for any reason at all.
            //that is: test failure triggers an undeployment.
            if (!childStatus.isNormal()) {
                sfLog().info("Tests have failed");
                //mark this as an error.
                exitRecord = childStatus;
                testSucceeded = false;
            } else {
                //normal termination is good
                testSucceeded = true;
            }
        } else {
            //something odd just terminated, like the condition.
            //whatever, it is an end of the test run.
            testSucceeded=false;
        }

        synchronized (this) {
            //update internal data structures
            if (exitRecord != null) {
                setStatus(exitRecord);
            } else {
                //whereas a child status implies success
                setStatus(childStatus);
            }
            updateFlags(testSucceeded);
        }
        try {
            endTestRun(getStatus());
        } catch (SmartFrogRuntimeException e) {
            sfLog().ignore(e);
        } catch (RemoteException e) {
            sfLog().ignore(e);
        }

        //if the error record is non null, terminate ourselves with the new record
        if (exitRecord != null) {
            sfTerminate(exitRecord);
            //dont forward, as we are terminating with an error
            propagateTermination = false;
        }
        //trigger termination.
        return propagateTermination;
    }


    /**
     * Return true iff the component is finished. Spin on this, with a (delay) between calls
     *
     * @return true if the run is finished
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
     * {@inheritDoc}
     *
     * @return the skipped state
     */
    public boolean isSkipped() {
        return skipped;
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
        return actionPrim;
    }


    /**
     * Get the termination record for this child; may be null
     *
     * @return a termination record or null
     */
    public TerminationRecord getActionTerminationRecord() {
        return actionTerminationRecord;
    }

    /**
     * Get the termination record for this child; may be null
     *
     * @return a termination record or null
     */
    public TerminationRecord getTestsTerminationRecord() {
        return testsTerminationRecord;
    }

    /**
     * Set the termination record for the component.
     *
     * @param status status record
     */
    protected void setStatus(TerminationRecord status) {
        this.status = status;
        status.setCause(SmartFrogExtractedException.convert(status.getCause()));
    }

    /**
     * update finished and succeeded/failed flags when we finish
     *
     * @param success flag to indicated whether we considered the run a success
     */
    protected synchronized void updateFlags(boolean success) {
        succeeded = success;
        failed = !success;
        finished = true;
    }

    /**
     * Use results + insternal state to decide if the test passed or not
     *
     * @param record termination record
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    protected void endTestRun(TerminationRecord record) throws SmartFrogRuntimeException, RemoteException {
        //send out a completion event
        sendEvent(new TestCompletedEvent(this, isSucceeded(), forcedTimeout, isSkipped(), record, description));
        setTestBlockAttributes(record, forcedTimeout);
    }

    /**
     * Set the various attributes of the component based on whether the test record was success or not
     *
     * @param record  termination record
     * @param timeout did we time out?
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    public void setTestBlockAttributes(
            TerminationRecord record,
            boolean timeout)
            throws SmartFrogRuntimeException, RemoteException {
        boolean success = record.isNormal();
        sfLog().debug("Terminated Test with status " + record + " timeout=" + timeout);
        sfReplaceAttribute(ATTR_STATUS, record);
        sfReplaceAttribute(ATTR_FINISHED, Boolean.TRUE);
        sfReplaceAttribute(ATTR_SUCCEEDED, Boolean.valueOf(success));
        sfReplaceAttribute(ATTR_FAILED, Boolean.valueOf(!success));
        sfReplaceAttribute(ATTR_FORCEDTIMEOUT, Boolean.valueOf(timeout));
    }
}
