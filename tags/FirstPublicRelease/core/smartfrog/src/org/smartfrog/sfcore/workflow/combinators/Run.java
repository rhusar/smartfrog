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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;


/**
 * Run is a modified compound which differs in that its only sub-component is
 * created and then left parentless to fend for itself. A Run combinator
 * creates no subcomponents until it's sfStart phase at which point the
 * subcomponent is created in the normal way. The Run combinator starts the
 * sub-component and then terminates normally if no error ocurred. If an error
 * occurs at any point, or a sub-component terminates abnormally during
 * start-up, the Run combinator does too.
 *
 * <p>
 * The file Run.sf contains the SmartFrog configuration file for the base Run
 * combinator. This file conatins the details of the attributes which may be
 * passed to Run.
 * </p>
 */
public class Run extends EventCompoundImpl implements Compound {
    static Reference actionRef = new Reference("action");
    static Reference parentRef = new Reference("parent");
    static Reference asNameRef = new Reference("asName");
    ComponentDescription action;
    Reference name;
    Compound parent=null;
    String asName;

    /**
     * Constructs Run.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Run() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Reads component's attributes
     * @throws SmartFrogResolutionException In case of error while resolving 
     * attributes
     * @throws RemoteException In case of RMI or network failure.
     */
    private void readSFAttributes() throws SmartFrogResolutionException,
                                                         RemoteException {
        // Optional attributes
        parent =  sfResolve(parentRef, parent, false);
        asName =  sfResolve(asNameRef, asName, false);
        // Mandatory attribute
        action = sfResolve(actionRef, action, true);
        name = sfCompleteNameSafe();
    }
    /**
     * Deploys and reads the basic configuration of the component.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        this.readSFAttributes();
    }

    /**
     * Starts the component and deploys the subcomponent then terminates.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogLifecycleException In case of any error while  starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        //super.sfStart();
        // let any errors be thrown and caught by SmartFrog for abnormal
        // termination  - including empty actions
        Prim comp = null;

        try {
            super.sfStart();

            if (parent != null) {
                comp = parent.sfDeployComponentDescription(asName, parent,
                        action, null);
            } else {
                comp = sfDeployComponentDescription(null, null, action, null);
            }
            try {
                comp.sfDeploy();
            } catch (Throwable thr){
                throw SmartFrogLifecycleException.sfDeploy("",thr,null);
            }
            try {
                comp.sfStart();
            } catch (Throwable thr){
                throw SmartFrogLifecycleException.sfStart("",thr,null);
            }
        } catch (Throwable e) {
            if (comp != null) {
                Reference compName = null;
                try {
                    compName = comp.sfCompleteName();
                }catch (Exception ex ) {
                    //ignore
                    //log message
                }
                try {
                     String compNameStr ="";
                     if (compName != null) compNameStr=compName.toString();
                     comp.sfTerminate(TerminationRecord.
                              abnormal("failed to deploy and start correctly " +
                                   compNameStr, name));
                 } catch (Exception ex) {}
            }
            throw ((SmartFrogException) SmartFrogException.forward(e));
        }
        Runnable terminator = new Runnable() {
                public void run() {
                    sfTerminate(TerminationRecord.normal(name));
                }
        };

        new Thread(terminator).start();
    }
}
