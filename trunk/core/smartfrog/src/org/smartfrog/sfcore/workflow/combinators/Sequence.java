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

package org.smartfrog.sfcore.workflow.combinators;

import java.rmi.RemoteException;
import java.util.Enumeration;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;


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
    static Reference actionsRef = new Reference("actions");
    Context actions;
    Enumeration actionKeys;
    Reference name;

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
        actions = ((ComponentDescription) sfResolve(actionsRef)).
                                    getContext();
        actionKeys = actions.keys();
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
            // let any errors be thrown and caught by SmartFrog for abnormal
            // termination  - including empty actions
            String componentName;
            try {
                componentName = (String)actionKeys.nextElement();
            } catch (java.util.NoSuchElementException nex){
               throw new SmartFrogRuntimeException ("Empty actions",this);
            }
            ComponentDescription act = (ComponentDescription) actions.
                                get(componentName);
            Prim comp = sfDeployComponentDescription(componentName, this, act,
                                                                    null);
            comp.sfDeploy();
            comp.sfStart();
    }

    /**
     * Terminates the component. It is invoked by sub-components on
     * termination. If normal termiantion, Sequence
     * behaviour is to start the next component if it is the last - terminate
     * normally. if an erroneous termination - terminate immediately passing
     * on the error
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        if (sfContainsChild(comp)) {
            try {
                if (status.errorType.equals("normal".intern())) {
                    if (actionKeys.hasMoreElements()) {
                        sfRemoveChild(comp);
                        // log msg
                        //System.out.println("starting next component in sequence " + name.toString());
                        String componentName = (String)actionKeys.nextElement();
                        ComponentDescription act = (ComponentDescription) actions.get(componentName);
                        Prim c = sfDeployComponentDescription(componentName, this,
                                act, null);
                        c.sfDeploy();
                        c.sfStart();
                    } else {
                        // Sequence terminates if there are no more sub-components
                        //log message
                        //System.out.println("no more components for sequence " + name.toString());
                        sfTerminate(TerminationRecord.normal(name));
                    }
                } else {
                    //System.out.println("error in previous sequenced component, aborting " + name.toString() + " parent:" + sfParent().toString());
                    super.sfTerminatedWith(status, comp);
                }
            } catch (Exception e) {
                sfTerminate(TerminationRecord.abnormal(
                        "error in starting next component: exception " + e, name));
            }
        }
    }
}
