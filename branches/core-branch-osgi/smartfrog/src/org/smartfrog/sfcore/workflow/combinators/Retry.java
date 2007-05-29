/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.workflow.combinators;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;


/**
 * Retry is a modified compound which differs in that its single sub-component
 * is created and should it terminate abnormally, is recreated. This is
 * repeated a number of times or until the sub-component suceeds. A Retry
 * combinator creates no subcomponents until it's sfStart phase at which point
 * all the subcomponent is created in the normal way. The Retry combinator
 * waits for its sub-component to terminate normally at which point it too
 * terminates normally. If an error occurs at any point, or a sub-component is
 * retried unless a limit is reached in which case it too terminates
 * abnormall.
 *
 * <p>
 * The file retry.sf contains the SmartFrog configuration file for the base
 * Retry combinator. This file conatins the details of the attributes which
 * may be passed to Retry.
 * </p>
 */
public class Retry extends EventCompoundImpl implements Compound {

    public static final String ATTR_RETRY = "retry";
    public static final String ATTR_COUNT = "count";
    private static Reference retryRef = new Reference(ATTR_RETRY);
    private int retry;
    private int currentRetries = 0;
    public static final String ERROR_NEGATIVE_COUNT = "A negative "+ATTR_RETRY+" value is not supported";

    /**
     * Constructs Retry.
     *
     * @throws java.rmi.RemoteException In case of RMI or network error.
     */
    public Retry() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Reads the basic configuration of the component and deploys it.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        checkActionDefined();
        retry = ((Integer) sfResolve(retryRef)).intValue();
        if(retry<0) {
            throw new SmartFrogDeploymentException (ERROR_NEGATIVE_COUNT);
        }
    }

    /**
     * Starts the component by starting the first copy of the retry component
     *
     * @throws SmartFrogException in case of problems creating the child
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        redeployAction();
    }


    /**
     * If normal termination, Retry behaviour is to terminate
     * normally. If an abnormal termination - retry unless some limit is
     * reached, in which case terminate abnormally.

     * @param status exit record of the component
     * @param comp   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        boolean forward = true;
        try {
            //remove the child
            sfRemoveChild(comp);

            if (!status.isNormal()) {
                //abnormal exit. Check the retry count
                if (currentRetries < retry) {
                    //yes, we can retry.
                    //increment the counter
                    //log it
                    if (sfLog().isInfoEnabled()) {
                        sfLog().info("Retry child "+ makeName(currentRetries)+" terminated ",
                                null,status);
                    }

                    currentRetries++;

                    if (sfLog().isDebugEnabled()) {
                        sfLog().debug("Retry: " + getName() + " " + currentRetries + " /" + retry);
                    }
                    //redeploy
                    String newchild=redeployAction();
                    if (sfLog().isDebugEnabled()) {
                        sfLog().debug("Retry: " + getName() + " started " + newchild);
                    }

                    //and do not forward up the termination
                    forward = false;
                } else {
                    if (sfLog().isDebugEnabled()) {
                        sfLog().debug(getName() + "terminated incorrectly: too many retries - fail ");
                    }
                    //sfTerminate(TerminationRecord.abnormal(ERROR_TOO_MANY_RETRIES, name));
                    //return true so that the current status code is forwarded
                    forward = true;
                }
            } else {
                if (sfLog().isDebugEnabled()) {
                    sfLog().debug(getName() + "terminated correctly - no need to retry  ");
                }


            }
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Error in restarting next component " + getName(), e);
            }
            sfTerminate(TerminationRecord.abnormal("Error in restarting next component (" + e.toString() + ")", getName()));
            forward = false;
        }
        return forward;
    }

    /**
     * clone and redeploy the action
     * @return the name of the new child
     * @throws RemoteException network problems
     * @throws SmartFrogDeploymentException deployment problems
     */
    private String redeployAction() throws RemoteException, SmartFrogRuntimeException {
        String newname= makeName(currentRetries);
        int count=currentRetries+1;
        sfReplaceAttribute(ATTR_COUNT,new Integer(count));
        sfCreateNewChild(newname,
                (ComponentDescription) action.copy(), null);
        return newname;
    }

    /**
     * make the name of a new child
     * @param retries
     * @return the name of the next child to deploy
     */
    private String makeName(int retries) {
        return "running_" + retries;
    }
}
