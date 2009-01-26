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
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;


/**
 * RandomSequence is a modified sequence which differs in that the
 * sub-components operate sequentially but are started at random.
 * RandomSequence maintains a vector of the started sub-components. A
 * sub-component can not be deployed and started twice. Sub-components do not
 * share the same lifecycle. A RandomSequence combinator creates no
 * sub-components until its sfStart phase at which point the first mentioned
 * random sub-components is created in the normal way. The RandomSequence
 * combinator waits for each of its sub-components to terminate at which point
 * it starts a randomly chosen one in the action. When the last terminates
 * normally it too terminates normally. If an error occurs at any point, or a
 * sub-component terminates abnormally, the RandomSequence combinator does
 * too.
 *
 * <p>
 * The file randomsequence.sf contains the SmartFrog configuration file for the
 * base Random,Sequence combinator. This file contains the details of the
 * attributes which may be passed to Sequence.
 * </p>
 *
 */
public class RandomSequence extends EventCompoundImpl implements Compound {
    private int seed;
    private Random random;
    private Vector actionKeysVector;
    public static final String ATTR_SEED = "seed";

    /**
     * Constructs RandomSequence.
     *
     * @throws RemoteException In case of RMI or network error.
     */
    public RandomSequence() throws RemoteException {
        super();
    }

    /**
     * Initializes the current keys vector. This vector is used to count
     * the remaining keys and select a random one to get and deploy a component
     * among those described in 'actions'
     */
    private void initActionKeys() {
        actionKeysVector = new Vector();

        for (Enumeration en = actions.keys(); en.hasMoreElements();) {
            actionKeysVector.addElement(en.nextElement());
        }
    }

    /**
     * Starts a random component and chooses one key in the actionKeys Vector,
     * then deploys the description and steps through the corresponding
     * component's lifecycle.
     *
     * @throws Exception if any error occurs while starting the next component
     */
    private void startNextRandom() throws Exception {
        // get a random component description in the action
        Object randomKey = actionKeysVector.elementAt((int) (random.nextInt(actionKeysVector.size())));
        ComponentDescription act = (ComponentDescription) actions.get(randomKey);

        // remove current key
        actionKeysVector.removeElement(randomKey);

        // deploy and start the component
        sfCreateNewChild(randomKey.toString(),
			 (ComponentDescription) act.copy(), null);
    }

    /**
     * Reads the basic configuration of the component and deploys.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        seed = sfResolve(ATTR_SEED,0,true);
        random = new Random(seed);
        initActionKeys();
    }

    /**
     * Starts and manages the random subcomponents.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogLifecycleException In case of any error while  starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        try {
            super.sfStart();
            // let any errors be thrown and caught by SmartFrog for
            // abnormal termination  - including empty actions
            startNextRandom();
        } catch (Exception ex) {
            throw new SmartFrogLifecycleException(MessageUtil.formatMessage(
                    MSG_RANDM_ERR), ex, this);
        }
    }


    /**
     * If normal termination, RandomSequence behaviour is to
     * start a random component in the actions if it is the last - terminate
     * normally. if an erroneous termination - terminate immediately passing
     * on the error
     *
     * @param status exit record of the component
     * @param comp   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        boolean forward = true;
        if (status.isNormal()) {
            try {
                sfRemoveChild(comp);
                if (actionKeysVector.size() != 0) {
                    startNextRandom();
                    forward = false;
                }
            } catch (Exception e) {
                sfTerminate(TerminationRecord.abnormal(
                        "error in starting next random component", getName(), e));
                forward=false;
            }
        }
        return forward;
    }
}
