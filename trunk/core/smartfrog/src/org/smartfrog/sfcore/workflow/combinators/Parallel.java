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
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.workflow.eventbus.EventRegistration;


/**
 * Parallel is a modified compound which differs in that the sub-components
 * operate in parallel but do not share the same lifecycle, and in particular
 * the same termination. A Parallel combinator creates no subcomponents until
 * it's sfStart phase at which point all the subcomponents are created in the
 * normal way and with synchronized or asynchronized lifecycle. The Parallel combinator waits
 * for each of its sub-components to terminate normally at which point it too
 * terminates normally. If an error occurs at any point, or a sub-component
 * terminates abnormally, the Parallel combinator does too.
 *
 * <p>
 * The file parallel.sf contains the SmartFrog configuration file for the base
 * Parallel combinator. This file contains the details of the attributes which
 * may be passed to Parallel.
 * </p>
 */
public class Parallel extends EventCompoundImpl implements Compound {
    static Reference actionsRef = new Reference("actions");
    static Reference asynchCreateChildRef = new Reference ("asynchCreateChild");

    Context actions;
    Enumeration actionKeys;
    Reference name;
    boolean asynchCreateChild=false;
    Vector asynchChildren = null;

    /**
     * Constructs Parallel.
     *
     * @throws java.rmi.RemoteException In case of network or RMI failure.
     */
    public Parallel() throws java.rmi.RemoteException {
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
        actions = ((ComponentDescription) sfResolve(actionsRef)).sfContext();
        asynchCreateChild = sfResolve(asynchCreateChildRef,asynchCreateChild,false);
        name = sfCompleteNameSafe();
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
            if (!asynchCreateChild){
                synchCreateChild();
            } else {
                asynchCreateChild();
            }
        } catch (Exception ex) {
            //Logger.log(this.sfCompleteNameSafe()+" - Failed to start sub-components ",ex);
            if (sflog().isErrorEnabled()){
              sflog().error(this.sfCompleteNameSafe()+" - Failed to start sub-components ",ex);
            }
            sfTerminate(TerminationRecord.abnormal("Failed to start sub-components " + ex, name));
        }
    }




    private void asynchCreateChild() throws SmartFrogDeploymentException,
            RemoteException, SmartFrogRuntimeException, SmartFrogException {
            asynchChildren = new Vector();
            actionKeys = actions.keys();
            try {
                while (actionKeys.hasMoreElements()) {
                    Object key = actionKeys.nextElement();
                    ComponentDescription act = (ComponentDescription) actions.get(key);
                    //asynchChildren.add();
                    Thread thread = new CreateNewChildThread(key,this,act, null);
                    thread.start();
                    asynchChildren.add(thread);
                }
            } catch (java.util.NoSuchElementException nex){
               throw new SmartFrogRuntimeException ("Empty actions",this);
            }
    }



    private void synchCreateChild() throws SmartFrogDeploymentException,
        RemoteException, SmartFrogRuntimeException, SmartFrogException {
        actionKeys = actions.keys();
        try {
            while (actionKeys.hasMoreElements()) {
                Object key = actionKeys.nextElement();
                ComponentDescription act = (ComponentDescription)
                    actions.get(key);
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
    }

    /**
     * Terminates the component. It is invoked by sub-components at
     * termination. If normal termiantion, Parallel behaviour is to terminate
     * that comopnent but leave the others running if it is the last -
     * terminate normally. if an erroneous termination -
     * terminate immediately passing on the error
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        if (sfContainsChild(comp)) {
            try {
                if (!(status.errorType.equals("normal".intern()))) {
                    sfTerminate(status);
                } else {
                    sfRemoveChild(comp);
                }

                if (!sfChildren().hasMoreElements()) {
                    sfTerminate(TerminationRecord.normal(name));
                }
            } catch (Exception e) {
//                Logger.log(this.sfCompleteNameSafe()+" - error handling child termination ",e );
              if (sflog().isErrorEnabled()){
                sflog().error(this.sfCompleteNameSafe()+" - error handling child termination ",e );
              }
              sfTerminate(TerminationRecord.abnormal("error handling child termination " + e, name));
            }
        }
    }

    /**
     * Cancels all remaining createChild threads
     *
     * @param status Termination  Record
     * @param comp Component which caused the termination
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {

        /* unregister from all remote registrations */
        if  (asynchChildren!=null){
            for (Enumeration e = asynchChildren.elements(); e.hasMoreElements(); ) {
                CreateNewChildThread t = (CreateNewChildThread)e.nextElement();
                try {
                    t.cancel(true);
                } catch (Exception ex1) {
                }
            }
        }
        super.sfTerminateWith(status);
    }
}
