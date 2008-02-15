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
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogExtractedException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.ContextImpl;
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
     * list of exceptions
     */
    private Vector<Vector<String>> exceptions;
    /**
     * The message of a forced shutdown. This is important, as we look for it when the component is terminated, and can
     * use its presence to infer that the helper thread did the work.
     */
    public static final String FORCED_TERMINATION = "Timed shutdown of test components";
    /** {@value} */
    public static final String TEST_FAILED_WRONG_STATUS = "Expected action to terminate with the status ";
    /** {@value} */
    public static final String EXIT_EXPECTED_STARTUP_EXCEPTION = "Exiting with expected exception thrown during startup";
    /** {@value} */
    public static final String UNEXPECTED_STARTUP_EXCEPTION = "The exception(s) raised at startup time do not match those expected\n";
    /** {@value} */
    public static final String ERROR_NO_EXCEPTIONS_FOUND = "No exceptions were in the termination record, expected: ";
    /** {@value} */
    public static final String ERROR_LESS_EXCEPTIONS_THAN_EXPECTED = "Less exceptions than expected";
    public static final String EXPECTED_EXIT_TEXT = "Expected exit text: ";
    public static final String EXPECTED_SUCCESSFUL_DEPLOYMENT = "expected successful deployment, but got: ";
    public static final String UNEXPECTED_TERMINATION = "(this termination was not expected)";
    public static final String TERMINATION_MESSAGE_MISMATCH = "Termination message mismatch";

    /**
     * Constructor
     * @throws RemoteException as the parent does
     */
    public TestCompoundImpl() throws RemoteException {
    }


    /**
     * Deploys and reads the basic configuration of the component. Overrides EventCompoundImpl.sfDeploy.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while deploying the component
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
        exceptions = ListUtils.resolveStringTupleList(this, new Reference(ATTR_EXCEPTIONS), true);
        //and we are deployed!
        sendEvent(new DeployedEvent(this));
    }

    /**
     * Override point: is the condition required. IF not, there is no attempt to deploy it at startup
     *
     * @return false, always
     */
    @Override
    protected boolean isConditionRequired() {
        return false;
    }

    /**
     * Startup is complex, as a failure is not always unexpected.
     * @throws RemoteException network problems
     * @throws SmartFrogException unable start up
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //report we have started up
        sendEvent(new StartedEvent(this));
        Throwable thrown = null;
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
            actionPrim = sfDeployComponentDescription(ACTION_RUNNING, this,
                    (ComponentDescription) action.copy(), new ContextImpl());
            // it is now a child, so need to guard against double calling of lifecycle...
            actionPrim.sfDeploy();
        } catch (Throwable e) {
            thrown = e;
            //thrown= new SmartFrogDeploymentException(" exception on sfDeploy ",e);
        } finally {
            sendEvent(new TestStartedEvent(this));
        }

        if (thrown == null) {
            //a null exception meant the action was deployed successfully

            //if we get here. then it is time to actually start the action.
            try {
                actionPrim.sfStart();
            } catch (Throwable e) {
                thrown = e;
            }
        }

        //did we catch something during deployment?
        if (thrown != null) {
            //get the message and check it against expections
            TerminationRecord record;
            String message = thrown.getMessage();
            if (message == null) {
                message = "";
            }
            
            if (isNormalTerminationExpected) {
                //if so, it didnt happen. rethrow the exception
                sfLog().info("Exception raised during startup, which was not expected");
                record = TerminationRecord.abnormal(UNEXPECTED_STARTUP_EXCEPTION, getName(), thrown);
                setStatus(record);
                actionTerminationRecord = record;
                updateFlags(false);
                endTestRun(record);
                //then throw an exception
                throw SmartFrogException.forward(UNEXPECTED_STARTUP_EXCEPTION
                        + EXPECTED_SUCCESSFUL_DEPLOYMENT
                        + message + "'\n",
                        thrown);
            }

            //here, an exception was expected. But was the failure we encountered the one expected?

            //look for the exit text or the exceptions
            String exceptionCheck = checkExceptionsWereExpected(thrown);
            boolean wrongExitText = !message.contains(exitText);
            if (wrongExitText || exceptionCheck != null) {

                String recordText="";
                if(exceptionCheck != null) {
                    recordText = UNEXPECTED_STARTUP_EXCEPTION + exceptionCheck+"\n";
                }
                if(wrongExitText) {
                    recordText+= EXPECTED_EXIT_TEXT +exitText+"\n"
                        + "But got: " + message + "\n";
                } else {
                    recordText += "Exit message: " + message + "\n";
                }

                record = TerminationRecord.abnormal(recordText, getName(), thrown);
                setStatus(record);
                //log it
                sfLog().error(record);
                actionTerminationRecord = record;
                updateFlags(false);
                endTestRun(record);
                //then throw an exception
                throw SmartFrogException.forward(UNEXPECTED_STARTUP_EXCEPTION
                        + "expected: '" + exitText + "'\n"
                        + "found   : '" + message + "'\n"
                        + exceptionCheck!=null?exceptionCheck:"",
                        thrown);
            } else {
                //valid exit. Save the results, then
                record = TerminationRecord.normal(EXIT_EXPECTED_STARTUP_EXCEPTION,
                        getName(), thrown);
                setStatus(record);
                actionTerminationRecord = record;
                updateFlags(true);
                endTestRun(record);
                //and optionally end the component
                new ComponentHelper(this).sfSelfDetachAndOrTerminate(record);
            }

        } else {
            //the action is deployed
            //start the terminator
            actionTerminator = new DelayedTerminator(actionPrim, undeployAfter, sfLog(),
                    FORCED_TERMINATION + " after " + testTimeout + " milliseconds",
                    !expectTerminate);
            actionTerminator.start();

            //now deploy the tests.
            startTests();
        }

    }

    /**
     * deploy a component passing all errors up.
     *
     * @param childname   the name of the component
     * @param description the component description to use (a copy is made before deployment)
     * @return the new child
     * @throws RemoteException network problems
     * @throws SmartFrogException unable start up
     */
    protected Prim deployComponentDescriptionThrowing(String childname, ComponentDescription description)
            throws SmartFrogException, RemoteException {
        Prim child = sfDeployComponentDescription(childname, this,
                (ComponentDescription) description.copy(), new ContextImpl());
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
    private void startTests() throws RemoteException, SmartFrogDeploymentException {
        if (tests != null) {
            testsPrim = sfCreateNewChild(TESTS_RUNNING, tests, null);
            //the test terminator reports a termination as a failure
            testsTerminator = new DelayedTerminator(testsPrim, testTimeout, sfLog(),
                    FORCED_TERMINATION+" after "+testTimeout+" milliseconds",
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
     * method returns true, the event is forwarded up the object hierarchy, which will eventually trigger a component
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
                String exitTextMessage=null;
                if (expectTerminate) {
                    //act on whether or not a fault was expected.
                    if (childStatus.errorType.contains(exitType)) {
                        //we have a match
                        sfLog().debug("Exit type is as expected");
                        expected = true;
                        if (exitText != null && exitText.length() > 0) {
                            String childDescription = childStatus.description;
                            if (childDescription == null) {
                                childDescription = "";
                            }

                            if (!childDescription.contains(exitText)) {
                                exitTextMessage= TERMINATION_MESSAGE_MISMATCH +" -expected \""
                                        + exitText
                                        + "\" but got \""
                                        + childDescription
                                        + "\"";
                                sfLog().debug(exitTextMessage);
                                expected = false;
                            }
                        }
                    } else {
                        //wrong exit type
                        sfLog().info("Action Exit type mismatch");
                        expected = false;
                    }
                    //also check the error text, regardless of the previous checks
                    String exceptionCheck = checkExceptionsWereExpected(childStatus);
                    if (exceptionCheck != null) {
                        sfLog().debug(exceptionCheck);
                        expected = false;
                    }

                    if (!expected) {
                        testSucceeded = false;
                        //the action prim terminated in a way that was not expected
                        //
                        String errorText = TEST_FAILED_WRONG_STATUS + exitType +
                                '\n';

                        if(exitText.length()>0) {
                            errorText += "and exit text '" + exitText + "'\n";
                        }
                        errorText += "But got " + childStatus+"\n";
                        if(exceptionCheck!=null) {
                            errorText+=exceptionCheck;
                            errorText += "\n";
                        }
                        if(exitTextMessage!=null) {
                            errorText+=exitTextMessage;
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
                        sfLog().info("Action terminated as expected");
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
                                childStatus.description
                                + UNEXPECTED_TERMINATION,
                                childStatus.id,
                                childStatus.getCause());
                    } else {
                        exitRecord = childStatus;
                    }
                    sfLog().error(childStatus);
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
     * Go through the exception list in {@link #exceptions} and compare them
     * with the results in the TerminationRecord.
     * @param record the termination record
     * @return any error string, or null for no errors
     */
    protected String checkExceptionsWereExpected(TerminationRecord record) {
        return checkExceptionsWereExpected(record.getCause());
    }

    /**
     * Go through the exception list in {@link #exceptions} and compare them
     * with what happened.
     * @param exception what is to be scanned
     * @return any error string, or null for no errors
     */
    protected String checkExceptionsWereExpected(Throwable exception) {
        Throwable thrown = exception;
        int expectedExceptionCount=exceptions==null?0:exceptions.size();
        if(thrown==null) {
            if(expectedExceptionCount==0) {
                return null;
            } else {
                return ERROR_NO_EXCEPTIONS_FOUND +expectedExceptionCount;
            }
        } else {
            //now run through the exception list
            for(Vector<String> tuple:exceptions) {
                if(thrown==null) {
                    return ERROR_LESS_EXCEPTIONS_THAN_EXPECTED;
                }
                String classname=tuple.get(0);
                String text=tuple.get(1);
                String canonicalName = thrown.getClass().getCanonicalName();
                if(thrown instanceof SmartFrogExtractedException) {
                    SmartFrogExtractedException sfe=(SmartFrogExtractedException) thrown;
                    canonicalName = sfe.getExceptionCanonicalName();
                }
                if(classname.length()>0 && !canonicalName.contains(classname)) {
                    return "Did not find classname '"+classname+"' in "+ canonicalName +" "+thrown.getMessage();
                }
                if (text.length() > 0 && !thrown.getMessage().contains(text)) {
                    return "Did not find text '" + text + "' in " + canonicalName + " " + thrown.getMessage();
                }
                //copy the next exception
                thrown=thrown.getCause();
            }
            //here we have gone through the list. It's OK to have more exceptions than expected
            return null;
        }
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

    /**
     * @return true iff the test succeeded
     * */

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
     * Use results + internal state to decide if the test passed or not
     *
     * @param record termination record
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    protected void endTestRun(TerminationRecord record) throws SmartFrogRuntimeException, RemoteException {
        //work out the forced timeout info from the terminators
        //its only a forced timeout if the action was expected to terminate itself anyway
        boolean actionForcedTimeout = expectTerminate && actionTerminator != null && actionTerminator.isForcedShutdown();
        //any timeout of the tests is an error
        boolean testForcedTimeout = testsTerminator != null && testsTerminator.isForcedShutdown();
        //a forced timeout of action or tests => forced timeout of components
        forcedTimeout= actionForcedTimeout || testForcedTimeout;
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
