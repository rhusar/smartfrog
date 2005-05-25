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
 * Parallel is a modified compound which differs in that the sub-components
 * operate in parallel but do not share the same lifecycle, and in particular
 * the same termination. A Parallel combinator creates no subcomponents until
 * it's sfStart phase at which point all the subcomponents are created in the
 * normal way and with synchronized lifecycle. The Parallel combinator waits
 * for each of its sub-components to terminate normally at which point it too
 * terminates normally. If an error occurs at any point, or a sub-component
 * terminates abnormally, the Parallel combinator does too.
 *
 * <p>
 * The file parallel.sf contains the SmartFrog configuration file for the base
 * Parallel combinator. This file conatins the details of the attributes which
 * may be passed to Parallel.
 * </p>
 */
public class FireBreak extends EventCompoundImpl implements Compound {

    /**
     * Constructs Container.
     *
     * @throws java.rmi.RemoteException In case of network or RMI failure.
     */
    public FireBreak() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Reads the basic configuration of the component and deploys.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    /**
     * Deploys and manages the parallel subcomponents.
     *
     * @throws RemoteException The required remote exception.
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        // let any errors be thrown and caught by SmartFrog for abnormal termination  - including empty actions
        try {
            actionKeys = actions.keys();
            try {
                while (actionKeys.hasMoreElements()) {
                    Object key = actionKeys.nextElement();
                    ComponentDescription act = (ComponentDescription) actions.get(key);
                    Prim comp = sfDeployComponentDescription(key, this, act, null);
                }
            } catch (java.util.NoSuchElementException nex){
               throw new SmartFrogRuntimeException ("Empty actions",this);
            }

            //Actions are now children of parallel, they are deployed and
            //started
            for (Enumeration e = sfChildren(); e.hasMoreElements();) {
                Object elem = e.nextElement();

                if (elem instanceof Prim) {
                    ((Prim) elem).sfDeploy();
                }
            }

            for (Enumeration e = sfChildren(); e.hasMoreElements();) {
                Object elem = e.nextElement();

                if (elem instanceof Prim) {
                    ((Prim) elem).sfStart();
                }
            }
        } catch (Exception ex) {
            //Logger.log(this.sfCompleteNameSafe()+" - Failed to start sub-components ",ex);
            if (sfLog().isErrorEnabled()){
              sfLog().error(this.sfCompleteNameSafe()+" - Failed to start sub-components ",ex);
            }
            sfTerminate(TerminationRecord.abnormal( "Failed to start sub-components " + ex, name));
        }
    }

    /**
     * It is invoked by sub-components at
     * termination. It simply removes the child and continues to run
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        if (sfContainsChild(comp)) {
            try {
		sfDetachAndTerminate(status);
            } catch (Exception e) {
                //Logger.log(this.sfCompleteNameSafe()+" - error handling child termination ",e );
                if (sfLog().isErrorEnabled()){
                  sfLog().error(this.sfCompleteNameSafe()+" - error handling child termination ",e);
                }
            }
        }
    }


    /**
     * Handle ping failures. Default behavior is to terminate with a liveness
     * send failure record storing the name of the target of the ping (which
     * generally is one of the children or the parent of this component).
     *
     * @param source source of update
     * @param target target that update was trying to reach
     * @param failure error that occurred
     */
    protected void sfLivenessFailure(Object source, Object target,
				     Throwable failure) {

	if (target.equals(sfParent)) {
	    super.sfLivenessFailure(source, target, failure);
	} else {
	    try {
		sfDetachAndTerminate(TerminationRecord.abnormal("liveness error", sfCompleteNameSafe(),  failure));
	    } catch (Exception e) {
	    }
	}
    }
}
