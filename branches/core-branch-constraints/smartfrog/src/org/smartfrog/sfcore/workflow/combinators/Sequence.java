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
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.utils.ComponentHelper;

/**
 * Sequence is a modified compound which differs in that the sub-components
 * operate sequentially. They do not share the same lifecycle. A Sequence
 * combinator creates no subcomponents until it's sfStart phase at which point
 * the first mentioned sub-components is created in the normal way. The
 * Sequence combinator waits for each of its sub-components to terminate at
 * which point it starts the next. When teh last terminates normally it too
 * terminates normally. If an error occurs at any point, or a sub-component
 * terminates abnormally, the Sequence combinator does too.
 *
 * <p>
 * The file sequence.sf contains the SmartFrog configuration file for the base
 * Sequence combinator. This file conatins the details of the attributes which
 * may be passed to Seqeunce.
 * </p>
 */
public class Sequence extends EventCompoundImpl implements Compound {

    /**
     * Constructs Sequence.
     *
     * @throws RemoteException In case of RMI or network failure.
     */
    public Sequence() throws RemoteException {
        super();
    }

    /**
     * Reads the basic configuration of the component and deploys it.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        name = sfCompleteNameSafe();
    }

    /**
     * Deploys and manages the sequential subcomponents.
     * Overrides CompoundImpl.sfStart.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogLifecycleException In case of any error while  starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if(actionKeys.hasMoreElements()) {
            // let any errors be thrown and caught by SmartFrog for abnormal
            // termination  - including empty actions
            String componentName;
            componentName = (String)actionKeys.nextElement();
            ComponentDescription act =null;
            try {
              act = (ComponentDescription) actions.get(componentName);
              sfCreateNewChild(componentName, act, null);
            } catch (Exception ex) {
              if (ex instanceof java.lang.ClassCastException){
                throw new SmartFrogDeploymentException("Error when deploying " + componentName
                    + " Class" +(actions.get(componentName)).getClass().getName(), ex, this, null);
              } else{
                throw SmartFrogDeploymentException.forward("Error when deploying "
                        + componentName +" in " +sfCompleteNameSafe() ,ex);
              }
            }
        } else {
            //nothing in the sequence, so just terminate ourselves
            new ComponentHelper(this).sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                    "Empty Sequence",
                    null,
                    null);
        }
    }


    /**
     * Handle child termination.
     * Sequence behaviour for a normal child termination is
     * <ol>
     * <li> to start the next component.</li>
     * <li> if it is the last - terminate normally. </li>
     * <li> If starting the next component raised an error, terminate abnormally</li>
     * </ol>
     * Abnormal child terminations are relayed up.
     * @param status exit record of the component
     * @param comp child component that is terminating
     * @return true whenever a child component is not started
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        boolean terminate = true;
        if (status.isNormal()) {

            if (actionKeys.hasMoreElements()) {
                try {
                    sfRemoveChild(comp);
                    String componentName = (String) actionKeys.nextElement();
                    if (sfLog().isDebugEnabled()) {
                        sfLog().debug(
                                "starting next component '" + componentName + "' in sequence " + name.toString());
                    }
                    ComponentDescription act = (ComponentDescription) actions.get(componentName);
                    sfCreateNewChild(componentName, act, null);
                    //do not forward the event
                    terminate = false;
                } catch (Exception e) {
                    //oops, something went wrong
                    if (sfLog().isErrorEnabled()) {
                        sfLog().error(name + " - error in starting next component ", e);
                    }
                    TerminationRecord tr = TerminationRecord
                            .abnormal("error in starting next component: exception " + e, name, e);
                    sfTerminate(tr);
                    //we've triggered an abnormal shutdown, so no forwarding of the earlier event
                    //as that would use the (normal) terminator used.
                    terminate = false;
                }

            } else {
                // Sequence terminates if there are no more sub-components
                //log message
                if (sfLog().isDebugEnabled()) {
                    sfLog().debug("no more components for sequence " + name.toString());
                }
                terminate = true;
            }
        } else {
            //abnormal terminations
            if (sfLog().isErrorEnabled()) {
                StringBuffer text=new StringBuffer();
                text.append(name);
                text.append("- error in child component\n");
                text.append(status.toString());
                text.append("\n");
                sfLog().error(text.toString(),status.getCause());
            }
            terminate = true;
        }
        return terminate;
    }

}
