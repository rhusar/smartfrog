/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * This component is designed to wrap two workflow actions/sequences into a pair, with one sequence run
 * at startup, and the second sequence executed during termination. The idea is that the second sequence
 * is the counteraction to the first; such as the deletion of a database or the dropping of a table.

 * when started, this component
 * <ol>
 * <li>Deploys and starts the <i>action</i> component
 * <li>If it is marked as terminating, it then terminates itself. Otherwise it stays deployed (default)
 * <li>Deploys the <i>counterAction</i> component, which will be run at termination.
 * <li>Deploys and starts the <i>liveness</i> component, if it exists.
 * This component gets liveness tests relayed to it.
 * <li>When terminating, starts the <i>counterAction<i> component.
 * </ol>
 */
public class CounterAction extends EventCompoundImpl implements Compound {


    public static final String ATTR_COUNTER_ACTION="counterAction";
    public static final String ATTR_LIVENESS = "liveness";

    private static final Reference COUNTER_ACTION_REF=new Reference(ATTR_COUNTER_ACTION);
    private static final Reference LIVENESS_REF = new Reference(ATTR_LIVENESS);
    
    private Prim actionPrim;
    private Prim livenessPrim;
    private Prim counterActionPrim;

    /**
     * Constructs Try.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public CounterAction() throws java.rmi.RemoteException {
        super();
    }


    /**
     * This is an override point. The original set of event components suppored the 'old' notation, in which actions
     * were listed in the {@link #ATTR_ACTIONS element} New subclasses do not need to remain backwards compatible and
     * should declare this fact by returning false from this method
     *
     * @return false
     */
    protected boolean isOldNotationSupported() {
        return false;
    }

    /**
     * Deploys and reads the basic configuration of the component.
     * Overrides EventCOmpoundImpl.sfStart.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        checkActionDefined();
    }

    /**
     * Deploys and manages the primary subcomponent.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while  starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        actionPrim = deployChildCD(ATTR_ACTION, true);
        ComponentDescription counterAct=null;
        counterAct = sfResolve(ATTR_COUNTER_ACTION, counterAct, false);
        //deploy but dont start the counter action.
        if(counterAct!=null) {
            counterActionPrim = deployComponentDescription(ATTR_COUNTER_ACTION, counterAct);
        }
        livenessPrim = deployChildCD(ATTR_LIVENESS,false);
    }


    /**
     * Handle child termination.
     *
     * Abnormal child terminations are relayed up.
     * @param status exit record of the component
     * @param comp child component that is terminating
     * @return true whenever a child component is not started
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        //get and reset the primary flag in a thread safe manner.

        boolean isAction;
        synchronized(this) {
            isAction = actionPrim==comp;
        }
        if (isAction) {
            try {
                sfRemoveChild(comp);
                return false;
            } catch (Exception e) {
                String message = "error in starting follow-on component for '" + status.errorType + "' try action";
                if (sfLog().isErrorEnabled()) {
                    sfLog().error(message, e);
                }
                //trigger our own abnormal termination
                sfTerminate(TerminationRecord.abnormal(message, name));
                //and tell the container not to
                return false;
            }
        } else {
            //not the action
            return true;
        }
    }


    /**
     * When we terminate, we start the counterAction, that gets to do the cleanup.
     *
     * @param status termination record.
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        if (counterActionPrim != null) {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("Starting the CounterAction");
            }
            try {
                counterActionPrim.sfStart();
            } catch (SmartFrogException e) {
                sfLog().error("When starting the CounterAction", e);
            } catch (RemoteException e) {
                sfLog().error("When starting the CounterAction", e);
            }
        }
        super.sfTerminateWith(status);
    }


    /**
     * Called by {@link #sfPing(Object)} to run through the list of children and ping each in turn.
     * In this subclass, only the liveness prim (if deployed) is checked for health.
     * 
     */
    protected void sfPingChildren() {
       if(livenessPrim!=null) {
           sfPingChildAndTerminateOnFailure(livenessPrim);
       }
    }
}
