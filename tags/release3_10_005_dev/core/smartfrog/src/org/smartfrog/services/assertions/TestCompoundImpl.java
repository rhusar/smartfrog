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
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * created 22-Sep-2006 16:43:35
 */

public class TestCompoundImpl extends EventCompoundImpl implements TestCompound {
    protected Prim teardown;

    protected Prim assertions;

    protected static final String ACTION_RUNNING = "_actionRunning";
    protected long undeployAfter;
    protected boolean expectTerminate;
    protected DelayedTerminator actionTerminator;
    protected Prim actionPrim;
    protected boolean terminating=false;
    protected String exitType;

    protected String exitText;
    /**
     * The message of a forced shutdown. This is important, as we look for
     * it when the component is terminated, and can use its presence to infer
     * that the helper thread did the work.
     */
    public static final String FORCED_TERMINATION = "timed shutdown of test components";
    public static final String TEST_FAILED_WRONG_STATUS = "Expected action to terminate with the status ";

    public TestCompoundImpl() throws RemoteException {
    }

    /**
     * Deploys and reads the basic configuration of the component.
     * Overrides EventCompoundImpl.sfDeploy.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                         In case of any error while
     *                         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        checkActionDefined();
        name = sfCompleteNameSafe();
        assertions = sfResolve(ATTR_ASSERTIONS, assertions, false);
        if (assertions != null) {
            throw new SmartFrogException("Not yet supported " + ATTR_ASSERTIONS);
        }
        teardown = sfResolve(ATTR_TEARDOWN, teardown, false);
        if (teardown != null) {
            throw new SmartFrogException("Not yet supported " + ATTR_TEARDOWN);
        }
        undeployAfter = sfResolve(ATTR_UNDEPLOY_AFTER, 0,true);
        expectTerminate = sfResolve(ATTR_EXPECT_TERMINATE,false,true);
        exitType = sfResolve(ATTR_EXIT_TYPE,exitType,true);
        exitText = sfResolve(ATTR_EXIT_TEXT, exitText, true);
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        LogSF logSF = sfLog();
        Exception exception=null;
        //deploy the action under a terminator, then the assertions, finally teardown afterwards.
        try {
            actionPrim = deployAction();
            actionTerminator = new DelayedTerminator(actionPrim, undeployAfter, logSF,
                    FORCED_TERMINATION,
                    !expectTerminate);
            actionTerminator.start();
        } catch (RemoteException e) {
            if (TerminationRecord.NORMAL.equals(exitType)) {
                throw e;
            }
            exception=e;
        } catch (SmartFrogDeploymentException e) {
            //split on normal/abnormal.
            if(TerminationRecord.NORMAL.equals(exitType)) {
                throw e;
            }
            exception = e;
        }
        //did we catch something during deployment?
        if (exception!=null) {
            //get the message and check it against expections
            String message= exception.getMessage();
            if(message==null) {
                message="";
            }
            if (message.indexOf(exitText) < 0) {
                throw new SmartFrogException("Wrong exitText in the fault raised at creation time\n"
                        +"expected: '"+exitText+"'\n"
                        +"found   : '"+message+"'\n",
                        exception);
            }
            //valid exit. trigger undeploy
            new ComponentHelper(this).sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                    null,null,exception);
        }
    }

    /**
     * Ping handler. This would be the place to provide feedback w.r.t the result of the deployed action.
     * @param source
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        terminating=true;
        super.sfTerminateWith(status);
        if (actionTerminator!=null) {
            //shut down the action terminator if we need to. We assume the superclass
            //terminator is already terminating the children, so there is no need
            //to tell the action terminator to do it.
            try {
                actionTerminator.shutdown(false);
            } catch (RemoteException e) {
                sfLog().error("When shutting down the action terminator",e);
            }
        }
    }

    /**
     * Called when a child terminates, either normally or abnormally.
     * @param status
     * @param comp
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {

        if (terminating) {
            super.sfTerminatedWith(status,comp);
            return;
        }
        if (actionPrim == comp) {
            if (actionTerminator.isForcedShutdown() && expectTerminate == false) {
                //this is a forced shutdown, all is well
                sfLog().info("Graceful shutdown of test components");
            } else {
                //not a forced shutdown, so why did it die?
                boolean expected = false;
                //act on whether or not a fault was expected.
                if (status.errorType.indexOf(exitType) >= 0) {
                    //we have a match
                    sfLog().debug("Exit type is as expected");
                    expected = true;
                    if (exitText != null) {
                        String description = status.description;
                        if (description == null) {
                            description = "";
                        }

                        if (description.indexOf(exitText) >= 0) {
                            expected &= true;
                        } else {
                            sfLog().debug("Exit text mismatch");
                            expected=false;
                        }
                    }

                } else {
                    sfLog().debug("Exit type mismatch");
                }
                if (!expected) {
                    String errorText = TEST_FAILED_WRONG_STATUS + exitType + "\n"
                            + "and error text " + exitText + "\n"
                            + "but got " + status;
                    sfLog().error(errorText);
                    status = TerminationRecord.abnormal(errorText, status.id);
                }
            }
        }
        //TODO: start teardown, etc.

        //trigger workflow termination.
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(status);
    }

    protected Prim deployAction() throws RemoteException, SmartFrogDeploymentException {
        Prim child = sfCreateNewChild(ACTION_RUNNING, action, null);
        return child;
    }

}
