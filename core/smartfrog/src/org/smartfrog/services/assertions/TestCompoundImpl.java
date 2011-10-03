/* (C) Copyright 2006-2008 Hewlett-Packard Development Company, LP

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

import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.services.assertions.events.TestStartedEvent;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogExtractedException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.ShouldDetachOrTerminate;
import org.smartfrog.sfcore.workflow.combinators.DelayedTerminator;
import org.smartfrog.sfcore.workflow.conditional.ConditionCompound;
import org.smartfrog.sfcore.workflow.events.DeployedEvent;
import org.smartfrog.sfcore.workflow.events.StartedEvent;
import org.smartfrog.sfcore.workflow.events.TerminatedEvent;

import java.rmi.RemoteException;
import java.util.Vector;

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
    private volatile Prim testsPrim;

    protected static final String ACTION_RUNNING = ATTR_ACTION;
    protected static final String TESTS_RUNNING = ATTR_TESTS;
    private long undeployAfter;
    private long testTimeout;
    private boolean expectTerminate;
    private boolean failInTestSequence;
    private volatile DelayedTerminator actionTerminator;
    private volatile DelayedTerminator testsTerminator;
    private volatile Prim actionPrim;
    private String exitType;
    private String exitText;
    private String description;
    /** flag set once the test child has terminated */
    protected volatile boolean testChildTerminated;
    private volatile boolean finished = false;
    private volatile boolean failed = false;
    private volatile boolean succeeded = false;
    private volatile boolean forcedTimeout = false;
    private volatile boolean skipped = false;
    private volatile boolean testsRun = false;
    private volatile TerminationRecord status;
    private volatile TerminationRecord actionTerminationRecord;
    private volatile TerminationRecord testsTerminationRecord;

    private boolean shouldTerminate;
    /**
     * list of exceptions
     */
    private Vector<Vector<String>> exceptions;
    /**
     * The message of a forced shutdown. This is important, as we look for it when the component is terminated, and can
     * use its presence to infer that the helper thread did the work.
     */
    public static final String FORCED_TERMINATION = "Timed shutdown of ";
    /**
     * {@value}
     */
    public static final String TEST_FAILED_WRONG_STATUS = "Expected action to terminate with the status ";
    /**
     * {@value}
     */
    public static final String EXIT_EXPECTED_STARTUP_EXCEPTION
            = "Exiting with expected exception thrown during startup";
    /**
     * {@value}
     */
    public static final String UNEXPECTED_STARTUP_EXCEPTION
            = "The exception(s) raised at startup time do not match those expected\n";
    /**
     * {@value}
     */
    public static final String ERROR_NO_EXCEPTIONS_FOUND = "No exceptions were in the termination record; expected: ";
    /**
     * {@value}
     */
    public static final String ERROR_LESS_EXCEPTIONS_THAN_EXPECTED = "Less exceptions than expected";
    public static final String EXPECTED_EXIT_TEXT = "Expected exit text: ";
    public static final String EXPECTED_SUCCESSFUL_DEPLOYMENT = "Expected successful deployment, but got: ";
    public static final String UNEXPECTED_TERMINATION = " (this termination was not expected)";
    public static final String TERMINATION_MESSAGE_MISMATCH = "Termination message mismatch";
    public static final String FAILED_TO_START_CONDITION = "Failed to start condition";
    private static final String ACTION_TERMINATED_AS_EXPECTED = "Action terminated as expected";
    private static final String ABNORMAL_TEST_TERMINATION = "Test component terminated abnormally";
    private static final String UNEXPECTED_CHILD_TERMINATION =
            "A child that was neither an action nor a test terminated";
    private String self;

    /**
     * Constructor
     *
     * @throws RemoteException as the parent does
     */
    public TestCompoundImpl() throws RemoteException {
    }


    /**
     * Deploys and reads the basic configuration of the component. Overrides EventCompoundImpl.sfDeploy.
     *
     * @throws RemoteException    In case of network/rmi error
     * @throws SmartFrogException In case of any error while deploying the component
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        self = sfCompleteNameSafe().toString();
        //look for the action
        checkActionDefined();
        waitForCD = sfResolve(ATTR_WAITFOR, waitForCD, false);
        tests = sfResolve(ATTR_TESTS, tests, false);
        testTimeout = sfResolve(ATTR_TEST_TIMEOUT, 0L, true);
        undeployAfter = sfResolve(ATTR_UNDEPLOY_AFTER, 0L, true);
        expectTerminate = sfResolve(ATTR_EXPECT_TERMINATE, false, true);
        failInTestSequence = sfResolve(ATTR_FAIL_IN_TEST_SEQUENCE, false, true);
        exitType = sfResolve(ATTR_EXIT_TYPE, exitType, true);
        exitText = sfResolve(ATTR_EXIT_TEXT, exitText, true);
        shouldTerminate = sfResolve(ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE, true, true);
        description = sfResolve(ATTR_DESCRIPTION, description, false);
        exceptions = ListUtils.resolveStringTupleList(this, new Reference(ATTR_EXCEPTIONS), true);
        //and we are deployed!
        sendEvent(new DeployedEvent(this));
    }


    /**
     * Override point: where the condition is deployed at startup. The default action is to call {@link
     * #deployCondition()}
     *
     * @throws SmartFrogException in case of problems creating the child
     * @throws RemoteException    In case of network/rmi error
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected void deployConditionAtStartup() throws SmartFrogException, RemoteException {
        //do nothing
    }

    /**
     * Override point: is the condition required. IF not, there is no attempt to deploy it at startup
     *
     * @return false, always
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected boolean isConditionRequired() {
        return false;
    }

    /**
     * Startup is complex, as a failure is not always unexpected.
     *
     * @throws RemoteException    network problems
     * @throws SmartFrogException unable start up
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {


        //the superclass does not deploy the condition.
        super.sfStart();
        //report we have started up
        sendEvent(new StartedEvent(this));
        //now deploy the condition. Failures are caught, noted and then passed up
        try {
            deployCondition();
        } catch (SmartFrogResolutionException e) {
            noteStartupFailure(FAILED_TO_START_CONDITION, e);
            throw e;
        } catch (RemoteException e) {
            noteStartupFailure(FAILED_TO_START_CONDITION, e);
            throw e;
        } catch (SmartFrogDeploymentException e) {
            noteStartupFailure(FAILED_TO_START_CONDITION, e);
            throw e;
        }

        boolean shouldRunTests = sfResolve(ATTR_RUN_TESTS_ON_STARTUP, false, false);
        if (shouldRunTests) {
            sfLog().debug(self +" starting test run in sfStart");
            runTests();
        } else {
            sfLog().debug(self + " Deferring test run until explicitly invoked");
        }
    }

    @Override
    public boolean runTests() throws RemoteException, SmartFrogException {
        //stop a re-entrant operation
        boolean isDebug = sfLog().isDebugEnabled();
        if (isDebug) sfLog().debug(self + " starting TestCompound test run");
        synchronized (this) {
            if (testsRun) {
                if (isDebug) sfLog().debug(self + " skipping re-entrant test run");
                return false;
            }
            testsRun = true;
        }

        //mark the tests as not completed yet. Any termination of the action from a timeout will be viewed
        //as a problem
        testChildTerminated = false;

        //evaluate the condition.
        //then decide whether to run or not.

        if (getCondition() != null && !evaluate()) {
            sendEvent(new TestStartedEvent(this));
            skipped = true;
            //stop the tests running again
            testsRun = true;
            updateFlags(false);
            String message = "Skipping test run " + getName();
            sfLog().debug(message);
            //send a test terminated event
            endTestRun(TerminationRecord.normal(message, getName()));
            //end: do not deploy anything else
            return false;
        }

        Throwable thrown = null;

        //deploy the action under a terminator, then the assertions, finally teardown afterwards.
        final boolean isNormalTerminationExpected = TerminationRecord.NORMAL.equals(exitType);
        if (isDebug) sfLog().debug(self + " expecting normal termination");
        try {
            if (isDebug) sfLog().debug(self + " deploying \"action\" child");
            actionPrim = sfDeployComponentDescription(ACTION_RUNNING,
                    this,
                    (ComponentDescription) action.copy(),
                    new ContextImpl());
            // it is now a child, so need to guard against double calling of lifecycle...
            if (isDebug) sfLog().debug(self + " deploying \"action\": " + actionPrim);
            actionPrim.sfDeploy();
        } catch (Throwable e) {
            sfLog().debug(self + " : exception during deployment of \"action\": " + e, e);
            thrown = e;
        }
        //even if the action fails, the test started event is sent
        sendEvent(new TestStartedEvent(this));

        if (thrown == null) {
            //a null exception meant the action was deployed successfully

            //sanity check
            assert actionPrim != null : "actionPrim is null, yet we did not catch an exception";

            if (isDebug) sfLog().debug(self + " Starting \"action\" component " + actionPrim);

            //if we get here. then it is time to actually start the action.
            try {
                actionPrim.sfStart();
            } catch (Throwable e) {
                if (isDebug) sfLog().debug(self + " Exception during startup of \"action\": " + e, e);
                thrown = e;
            }
        }

        //did we catch something during deployment or startup?
        if (thrown != null) {
            //get the message and check it against expections
            String message = thrown.getMessage();
            if (message == null) {
                message = thrown.toString();
            }

            //did we expect this component to terminate normally?
            if (isNormalTerminationExpected) {
                //if so, it didn't happen. log and rethrow the exception
                sfLog().info("Exception raised during \"action\" startup, which was not expected: " + message, thrown);
                noteStartupFailure(EXPECTED_SUCCESSFUL_DEPLOYMENT, thrown);
                //then throw an exception
                throw SmartFrogException.forward(EXPECTED_SUCCESSFUL_DEPLOYMENT
                        + message + "'\n",
                        thrown);
            }

            //here, an exception was expected. But was the failure we encountered the one expected?

            //look for the exit text or the exceptions
            String exceptionCheck = checkExceptionsWereExpected(thrown);
            boolean wrongExitText = !message.contains(exitText);
            if (wrongExitText || exceptionCheck != null) {
                if (isDebug) sfLog().debug(
                        self + " No startup exception from deploying \"action\" expected, or the exit test is wrong");
                String recordText = "";
                if (exceptionCheck != null) {
                    recordText = UNEXPECTED_STARTUP_EXCEPTION + exceptionCheck + '\n';
                }
                if (wrongExitText) {
                    recordText += EXPECTED_EXIT_TEXT + exitText + '\n'
                            + "But got: " + message + '\n';
                } else {
                    recordText += "Exit message: \"" + message + "\"\n";
                }
                noteStartupFailure(recordText, thrown);
                //then throw an exception
                throw SmartFrogException.forward(UNEXPECTED_STARTUP_EXCEPTION
                        + "expected: '" + exitText + "'\n"
                        + "found   : '" + message + "'\n"
                        + (exceptionCheck != null ? exceptionCheck : ""),
                        thrown);
            } else {
                if (isDebug) sfLog().debug(self + " exception received -and it was expected: " + thrown, thrown);
                //valid exit.
                // Propagate the results
                TerminationRecord record;
                record = TerminationRecord.normal(EXIT_EXPECTED_STARTUP_EXCEPTION,
                        getName(), thrown);
                noteEndOfTestRun(record, true);
                //and optionally end the component
                new ComponentHelper(this).sfSelfDetachAndOrTerminate(record);
            }

        } else {

            //the action is deployed
            //start the terminator
            actionTerminator = new DelayedTerminator(actionPrim, undeployAfter, sfLog(),
                    FORCED_TERMINATION + "action after " + undeployAfter + " milliseconds",
                    !expectTerminate);
            if (isDebug) sfLog().debug(self + " \"action\" is deployed, starting: " + actionTerminator.toString());

            actionTerminator.start();

            //now deploy the tests.
            if (isDebug) sfLog().debug(self + " Invoking startTests()");
            startTests();
        }

        return true;
    }

    /**
     * Create an abnormal TerminationRecord from the exception and the text, record the end of the test run in status
     * attributes and workflow notifications
     *
     * @param text   message for the TR
     * @param thrown the exception tht was thrown
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    private void noteStartupFailure(String text, Throwable thrown) throws SmartFrogRuntimeException, RemoteException {
        TerminationRecord record;
        record = TerminationRecord.abnormal(text, getName(), thrown);
        if (sfLog().isDebugEnabled()) sfLog().debug(self + " startup failure: " + record);
        noteEndOfTestRun(record, false);
    }

    /**
     * Record the end of the test run in status attributes and workflow notifications. If the test failed, the TR is
     * also logged at error level.
     *
     * @param record  the TR of this test
     * @param success flag set to true to indicate success
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    private void noteEndOfTestRun(TerminationRecord record, boolean success)
            throws SmartFrogRuntimeException, RemoteException {
        if (!success) {
            sfLog().warn(record.toString());
            if (record.getCause() != null) {
                sfLog().warn(record.getCause());
            }
        }
        setStatus(record);
        actionTerminationRecord = record;
        updateFlags(success);
        endTestRun(record);
    }

    /**
     * deploy a component passing all errors up.
     *
     * @param childname the name of the component
     * @param desc      the component description to use (a copy is made before deployment)
     * @return the new child
     * @throws RemoteException    network problems
     * @throws SmartFrogException unable start up
     */
    protected Prim deployComponentDescriptionThrowing(String childname, ComponentDescription desc)
            throws SmartFrogException, RemoteException {
        Prim child = sfDeployComponentDescription(childname,
                this,
                (ComponentDescription) desc.copy(),
                new ContextImpl());
        // it is now a child, so need to guard against double calling of lifecycle...
        child.sfDeploy();
        return child;
    }

    /**
     * Start the test runner and a terminator
     *
     * @throws SmartFrogDeploymentException smartfrog problems
     * @throws RemoteException              RMI problems
     */
    private synchronized void startTests() throws RemoteException, SmartFrogException {
        boolean isDebug = sfLog().isDebugEnabled();
        if (testsPrim != null) {
            sfLog().debug(self + " \"tests\" child is already deployed");
            return;
        }
        if (tests != null) {
            if (isDebug) sfLog().debug(self + " starting the \"tests\" child");

            testsPrim = sfCreateNewChild(TESTS_RUNNING,
                    this,
                    (ComponentDescription) tests.copy(),
                    null);
            // it is now a child, so need to guard against double calling of lifecycle...
            testsPrim.sfDeploy();


            //the test terminator reports a termination as a failure
            testsTerminator = new DelayedTerminator(testsPrim, testTimeout, sfLog(),
                    FORCED_TERMINATION + "tests after " + testTimeout + " milliseconds",
                    false);
            if (isDebug) sfLog().debug(self + " child \"tests\" deployed, starting " + testsTerminator);
            testsTerminator.start();
        } else {
            //say that the tests have finished
            testChildTerminated = true;
            //no test child started, explain why
            sfLog().info(self + " no \"tests\" child to start");

            //what happens now? Well, the action had better finish on its own, otherwise timeouts will kick in.
        }
    }

    /**
     * Called by {@link #sfPing(Object)} to run through the list of children and ping each in turn. If any child fails,
     * {@link #sfLivenessFailure(Object, Object, Throwable)} is called and the iteration continues. <p/> Override this
     * method to implement different child ping behaviour.
     */
    @Override
    protected void sfPingChildren() {
        if (actionPrim != null && actionTerminationRecord == null) {
            sfPingActionAndHandleFailure();
        }
        if (testsPrim != null && testsTerminationRecord == null) {
            sfPingChildAndTerminateOnFailure(testsPrim);
        }
    }

    /**
     * Ping the action. If we get a liveness failure (i.e. the child has terminated, we check to see if it was actually
     * expected, in which case this is not a problem. We note and ignore it.
     *
     * The reason for this special handling is related to race conditions in termination...a ping could be received and
     * processed while we are getting ready to deal with terminated children. This handler ignores such situations
     */
    protected void sfPingActionAndHandleFailure() {
        try {
            sfPingChild(actionPrim);
        } catch (SmartFrogLivenessException ex) {
            handleActionLivenessFailure(ex);
        } catch (RemoteException ex) {
            handleActionLivenessFailure(ex);
        }
    }

    /**
     * Handle a liveness failure by maybe terminating the application, maybe just noting it is being ignored
     *
     * @param ex the exception that triggered this failure
     * @return true if termination was triggered
     */
    private synchronized boolean handleActionLivenessFailure(Exception ex) {
        sfLog().info("Liveness failure pinging the action", ex);
        if (!expectTerminate) {
            sfLivenessFailure(this, actionPrim, ex);
            return true;
        } else {
            sfLog().info("Ignoring as we were expecting termination");
            return false;
        }
    }

    /**
     * When terminating we shutdown the action and the tests, and Send out notifications of termination
     *
     * @param record exit status
     */
    @Override
    public void sfTerminateWith(TerminationRecord record) {
        sendEvent(new TerminatedEvent(this, record));
        super.sfTerminateWith(record);
        try {
            shutdown(actionTerminator);
        } finally {
            actionTerminator = null;
        }
        try {
            shutdown(testsTerminator);
        } finally {
            testsTerminator = null;
        }
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
     * Add some more text to a termination record description
     * @param tr record to update
     * @param text text to append
     */
    private void appendToDescription(TerminationRecord tr, String text) {
        tr.description = tr.description + "; " + text;
    }

    /**
     * {@inheritDoc}
     *
     * Work out which child has terminated and act on it. 
     * @param childStatus exit record of the component
     * @param terminatingChild       child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Override
    protected boolean onChildTerminated(TerminationRecord childStatus, Prim terminatingChild) {
        boolean propagateTermination = shouldTerminate;
        TerminationRecord exitRecord = null;
        boolean testSucceeded;
        Prim actionChild;
        DelayedTerminator actionTerminatorChild;
        Prim testsChild;

        boolean isDebug = sfLog().isDebugEnabled();
        if (isDebug) {
            sfLog().debug("Handling termination of child "
                    + terminatingChild + " -- " + childStatus);
        }

        synchronized (this) {
            actionChild = actionPrim;
            actionTerminatorChild = actionTerminator;
            testsChild = testsPrim;
        }

        if (actionChild == terminatingChild) {
            if (isDebug) sfLog().debug(self + " child \"action\" is terminating");
            actionTerminationRecord = childStatus;
            //child termination
            if (actionTerminatorChild != null
                    && actionTerminatorChild.isForcedShutdown()
                    && !expectTerminate
                    && testChildTerminated) {
                //this is a forced shutdown, all is well
                if (testChildTerminated) {
                    sfLog().info("Forced shutdown of \"action\" component (expected)");
                    testSucceeded = true;
                } else {
                    final String message = "Forced shutdown of \"action\" component before tests completed";
                    sfLog().info(message);
                    testSucceeded = false;
                    //copy the termination record, but mark it as abnormal
                    exitRecord = TerminationRecord.abnormal(message, childStatus.id, childStatus.cause);
                    exitRecord.errorType = TerminationRecord.ABNORMAL;
                }
            } else {
                if (isDebug) sfLog().debug(self + " \"action\" termination is not forced, or happened before the tests had finished");
                //not a forced shutdown, so why did it die?
                boolean expected;
                String exitTextMessage = null;
                if (expectTerminate) {
                    //act on whether or not a fault was expected.
                    if (childStatus.errorType.equals(exitType)) {
                        //we have a match
                        sfLog().debug(self + " \"action\" exit type is as expected : " + exitType);
                        expected = true;
                        if (exitText != null && exitText.length() > 0) {
                            String childDescription = childStatus.description;
                            if (childDescription == null) {
                                childDescription = "";
                            }

                            if (!childDescription.contains(exitText)) {
                                exitTextMessage = TERMINATION_MESSAGE_MISMATCH + " -expected \""
                                        + exitText
                                        + "\" but got \""
                                        + childDescription
                                        + '\"';
                                sfLog().info(exitTextMessage);
                                expected = false;
                            } else {
                                if (isDebug) {
                                    sfLog().debug(self + " \"action\" exit text contains the required text "
                                            + "\"" + exitText + "\"");
                                }
                            }
                        } else {
                            //exit text is not set, all is well
                            if (isDebug) sfLog().debug(self + " No exit text expected, so skipping that check");
                        }
                    } else {
                        //wrong exit type
                        sfLog().info("Exit type mismatch -expected " + exitType + " but got " + childStatus.errorType);
                        expected = false;
                    }
                    //also check the error text, regardless of the previous checks
                    String exceptionCheck = checkExceptionsWereExpected(childStatus);
                    if (exceptionCheck != null) {
                        sfLog().debug(exceptionCheck, childStatus.getCause());
                        expected = false;
                    }

                    if (!expected) {
                        testSucceeded = false;
                        //the action prim terminated in a way that was not expected
                        //
                        String errorText = TEST_FAILED_WRONG_STATUS + exitType +
                                '\n';

                        if (exitText.length() > 0) {
                            errorText += "and exit text '" + exitText + "'\n";
                        }
                        errorText += "But got " + childStatus + '\n';
                        if (exceptionCheck != null) {
                            errorText += exceptionCheck;
                            errorText += "\n";
                        }
                        if (exitTextMessage != null) {
                            errorText += exitTextMessage;
                        }
                        exitRecord = TerminationRecord.abnormal(errorText,
                                childStatus.id,
                                childStatus.getCause());
                        sfLog().error(errorText);
                        sfLog().error(exitRecord);
                    } else {
                        //expected action termination.
                        //now look at the record, and if it is abnormal, convert it
                        //to a normal status, preserving the message
                        testSucceeded = true;
                        sfLog().info(ACTION_TERMINATED_AS_EXPECTED);
                        if (!childStatus.isNormal()) {
                            exitRecord = TerminationRecord.normal(
                                    childStatus.description,
                                    childStatus.id,
                                    childStatus.getCause());
                        } else {
                            //error is good,
                            //The decision to terminate is based on the shouldTerminate flag
                            sfLog().debug(propagateTermination ?
                                    "Terminating normally"
                                    : "Not terminating as sfShouldTerminate is false");
                        }

                    }
                } else {
                    //child terminated and it was not expected. This is an error.
                    sfLog().error("Unexpected termination of \"action\"");
                    testSucceeded = false;
                    if (childStatus.isNormal()) {
                        //flip this to abnormal
                        exitRecord = TerminationRecord.abnormal(
                                (childStatus.description != null
                                        ? childStatus.description
                                        : ("\"action\" terminated normally which was not expected: " + childStatus))
                                        + UNEXPECTED_TERMINATION,
                                childStatus.id,
                                childStatus.getCause());
                    } else {
                        exitRecord = childStatus;
                    }
                    sfLog().error(childStatus);
                }

            }
        } else {
            if (terminatingChild == testsChild) {
                testChildTerminated = true;
                sfLog().debug(self + " \"tests\" component has terminated");
                //tests are terminating.
                testsTerminationRecord = childStatus;
                //it is an error if these terminated abnormally, for any reason at all.
                //that is: test failure triggers an undeployment.
                if (!childStatus.isNormal()) {
                    sfLog().info(ABNORMAL_TEST_TERMINATION);
                    //mark this as an error.
                    exitRecord = childStatus;
                    appendToDescription(exitRecord, ABNORMAL_TEST_TERMINATION);
                    testSucceeded = false;
                } else {
                    sfLog().debug(self + " \"tests\" termination was successful");
                    //normal termination is good
                    testSucceeded = true;
                }
            } else {
                //something odd just terminated, like the condition.
                //whatever, it is the end of the test run.
                exitRecord = childStatus;
                appendToDescription(exitRecord, UNEXPECTED_CHILD_TERMINATION
                        + " ("
                        + " testPrim = " + testsChild
                        + " actionPrim=" + actionChild
                        + " terminatingChild=" + terminatingChild
                        + ")");
                sfLog().warn(UNEXPECTED_CHILD_TERMINATION + ": " + exitRecord);
                testSucceeded = false;
            }
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
            sfLog().debug(self + " when ending the test run" + e, e);
        } catch (RemoteException e) {
            sfLog().debug(self + " when ending the test run" + e, e);
        }

        //if the error record is non null, terminate ourselves with the new record
        if (exitRecord != null) {
            if(isDebug) sfLog().debug(self + " terminating with Termination Record " + exitRecord);
            sfTerminate(exitRecord);
            //don't forward, as we are terminating with an error
            propagateTermination = false;
        }
        //trigger termination.
        return propagateTermination;
    }

    /**
     * Go through the exception list in {@link #exceptions} and compare them with the results in the TerminationRecord.
     *
     * @param record the termination record
     * @return any error string, or null for no errors
     */
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    protected String checkExceptionsWereExpected(TerminationRecord record) {
        return checkExceptionsWereExpected(record.getCause());
    }

    /**
     * Go through the exception list in {@link #exceptions} and compare them with what happened.
     *
     * @param exception what is to be scanned
     * @return any error string, or null for no errors
     */
    protected String checkExceptionsWereExpected(Throwable exception) {
        boolean isDebug = sfLog().isDebugEnabled();
        Throwable thrown = exception;
        int expectedExceptionCount = exceptions == null ? 0 : exceptions.size();
        if (thrown == null) {
            if (expectedExceptionCount == 0) {
                sfLog().debug(self + " No exceptions expected; no exceptions raised");
                return null;
            } else {
                sfLog().debug(self + " No exceptions raised -but expected "+ expectedExceptionCount);
                return ERROR_NO_EXCEPTIONS_FOUND + expectedExceptionCount;
            }
        } else {
            if (expectedExceptionCount == 0) {
                //we have an exception, but the list of exceptions is null
                sfLog().debug(self + " No exception types or strings provided; assuming a match");
                return null;
            }
            //now run through the exception list
            for (Vector<String> tuple : exceptions) {
                String classname = tuple.get(0);
                String text = tuple.get(1);
                if (thrown == null) {
                    String expectedFault = " at (" + classname + " \"" + text + "\")";
                    if(isDebug) {
                        sfLog().debug(self + " Ran out of exceptions "
                                + expectedFault, thrown);
                    }
                    return ERROR_LESS_EXCEPTIONS_THAN_EXPECTED + expectedFault;
                }

                String canonicalName = thrown.getClass().getCanonicalName();
                if (thrown instanceof SmartFrogExtractedException) {
                    SmartFrogExtractedException sfe = (SmartFrogExtractedException) thrown;
                    canonicalName = sfe.getExceptionCanonicalName();
                }
                String thrownMessage = thrown.getMessage();
                if(isDebug) {
                    sfLog().debug(self + " Next expected exception is (" + canonicalName + " \"" + thrownMessage + "\")");
                }
                if (!classname.isEmpty() && !canonicalName.contains(classname)) {
                    return "Did not find classname '" + classname + "' in " + canonicalName + ' ' + thrownMessage;
                }
                if (!text.isEmpty() && !thrownMessage.contains(text)) {
                    return "Did not find text '" + text + "' in " + canonicalName + ' ' + thrownMessage;
                }
                //copy the next exception
                thrown = thrown.getCause();
            }
            //here we have gone through the list. It's OK to have more exceptions than expected
            if (isDebug) {
                if(thrown!=null) {
                    sfLog().debug(self + " End of expected exception list: all have matched: remaining exception is "+ thrown, thrown);
                } else {
                    sfLog().debug(self + " End of expected exception list: all have matched; no leftovers");
                }
            }
            return null;
        }
    }

    /**
     * Return true iff the component is finished. Spin on this, with a (delay) between calls
     *
     * @return true if the run is finished
     */
    @Override
    public boolean isFinished() {
        return finished;
    }

    /**
     * @return true only if the test has finished and failed
     */
    @Override
    public boolean isFailed() {
        return failed;
    }

    /**
     * @return true iff the test succeeded
     */

    @Override
    public boolean isSucceeded() {
        return succeeded;
    }


    /**
     * {@inheritDoc}
     *
     * @return the skipped state
     */
    @Override
    public boolean isSkipped() {
        return skipped;
    }

    /**
     * Get the exit record
     *
     * @return the exit record, will be null for an unfinished child
     */


    @Override
    public TerminationRecord getStatus() {
        return status;
    }

    /**
     * return the tests prim
     *
     * @return the child component. this will be null after termination.
     */
    @Override
    public Prim getAction() {
        return actionPrim;
    }


    /**
     * Get the termination record for this child; may be null
     *
     * @return a termination record or null
     */
    @Override
    public TerminationRecord getActionTerminationRecord() {
        return actionTerminationRecord;
    }

    /**
     * Get the termination record for this child; may be null
     *
     * @return a termination record or null
     */
    @Override
    public TerminationRecord getTestsTerminationRecord() {
        return testsTerminationRecord;
    }

    /**
     * Set the termination record for the component.
     *
     * @param status status record
     */
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
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
     * Use results + internal state to decide if the test passed or not
     *
     * @param record termination record
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    protected synchronized void endTestRun(TerminationRecord record) throws SmartFrogRuntimeException, RemoteException {
        //work out the forced timeout info from the terminators
        //its only a forced timeout if the action was expected to terminate itself anyway
        boolean actionForcedTimeout = expectTerminate &&
                actionTerminator != null && actionTerminator.isForcedShutdown();
        //any timeout of the tests is an error
        boolean testForcedTimeout = testsTerminator != null && testsTerminator.isForcedShutdown();
        if (sfLog().isDebugEnabled()) {
            sfLog().debug(self + " Ending test run. "
                    + "ActionForcedTimeout = " + actionForcedTimeout
                    + " testsForcedTimeout =" + testForcedTimeout);
        }
        //a forced timeout of action or tests => forced timeout of components
        forcedTimeout = actionForcedTimeout || testForcedTimeout;
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
    private void setTestBlockAttributes(
            TerminationRecord record,
            boolean timeout)
            throws SmartFrogRuntimeException, RemoteException {
        boolean success = record.isNormal();
        if (sfLog().isDebugEnabled()) {
            sfLog().debug(self + " setTestBlockAttributes of completed test with status " + record + " timeout=" + timeout);
        }
        sfReplaceAttribute(ATTR_STATUS, record);
        sfReplaceAttribute(ATTR_FINISHED, Boolean.TRUE);
        sfReplaceAttribute(ATTR_SUCCEEDED, Boolean.valueOf(success));
        sfReplaceAttribute(ATTR_FAILED, Boolean.valueOf(!success));
        sfReplaceAttribute(ATTR_FORCEDTIMEOUT, Boolean.valueOf(timeout));
    }
}
