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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.common.*;


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

    // Old attributes
    /** Reference to parent for the new component
     String name for attribute. Value {@value}.*/
    static final Reference parentRef = new Reference("parent");

    /** Reference to name for the new component
     String name for attribute. Value {@value}.*/
    static final Reference asNameRef = new Reference("asName");

    // New attributes
    /** Reference to myName for the new component.
     String name for attribute. Value {@value} or asName.*/
    private static final String ATTR_NAME = "newComponentName";

    /** Reference to parent for the new deployment.
     String name for attribute. Value {@value} or parent.*/
    private static final String ATTR_PARENT = "newComponentParent";

    /** Reference to compund component used to drive the new deployment.
     String name for attribute. Value {@value} or parent.*/
    private static final String ATTR_DEPLOYER = "newComponentDeployer";

    private Compound parent=null;

    private String newComponentName=null;

    private Compound newComponentDeployer = null;

    private ComponentDescription newComponentCD = null;

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
    protected void readSFAttributes() throws SmartFrogResolutionException, RemoteException {
        // Optional attributes
        //old attributes
        parent =  sfResolve(parentRef, parent, false);
        newComponentName =  sfResolve(asNameRef, newComponentName, false);
        //new attributes
        parent =  sfResolve(ATTR_PARENT, parent, false);
        newComponentName =  sfResolve(ATTR_NAME, newComponentName, false);

        newComponentDeployer =  sfResolve(ATTR_DEPLOYER, newComponentDeployer, false);
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
        newComponentCD = getComponentDescription();
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
            if ((parent != null) && (newComponentName==null)) {
                String parentName = "parentName";
                try { parentName = parent.sfCompleteName().toString(); } catch (Throwable ex){}
                String message = sfCompleteNameSafe()+ " needs to provide a name () when providing a parent ('parent' "+ parentName+")";
                if (sfLog().isErrorEnabled()) sfLog().error(message);
                throw new SmartFrogDeploymentException( message , this, sfContext());
            }
            comp = createNewChild();
        } catch (Throwable e) {
            if (comp!=null) {
                Reference compName = null;
                try {
                    compName = comp.sfCompleteName();
                } catch (Exception ex) { }
                try {
                    String compNameStr = "";
                    if (compName!=null)compNameStr = compName.toString();
                    comp.sfTerminate(TerminationRecord.abnormal( "failed to deploy and start correctly "+ compNameStr, name));
                } catch (Exception ex) {}
            }
            //terminateComponent(this.parent,e,null);
            //terminateComponent(this,e,null);
            sfTerminate(TerminationRecord.abnormal( "failed to deploy child ", this.sfCompleteNameSafe(),e));

            throw ((SmartFrogException)SmartFrogException.forward(e));
        }
        TerminatorThread terminator = new TerminatorThread(this,TerminationRecord.normal(name));
        terminator.start();

//        Runnable terminator = new Runnable() {
//            public void run() { sfTerminate(TerminationRecord.normal(name)); }
//        };
//        new Thread(terminator).start();
    }

    protected ComponentDescription getComponentDescription() throws SmartFrogException {
        //Run reads the component description from the action attribute
        return action;
    }

    protected Prim createNewChild() throws SmartFrogDeploymentException, RemoteException {
        Prim comp;
        if (newComponentDeployer!=null) {
            comp = newComponentDeployer.sfCreateNewChild(newComponentName,parent,newComponentCD,null);
        } else {
            //Same semantics as RUN
            if (parent!=null) {
                comp = parent.sfCreateNewChild(newComponentName, newComponentCD, null);
            } else {
                comp = sfCreateNewApp(newComponentName, newComponentCD, null);
            }
        }
        return comp;
    }


}
