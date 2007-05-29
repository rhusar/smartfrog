/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/

package org.smartfrog.sfcore.workflow.combinators;

import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * This component runs an action when a termination request is received. The action is deployed on startup, but it is
 * only moved into the started state when the Try component is to be terminated.
 * <p/>
 * You can use this component to deploy some action for cleanup, such as the execution of a remote shutdown command.
 * <p/>
 * Be warned that there is no way to guarantee that the finally action will always be started. Emergency system
 * shutdowns can bypass this operation
 */
public class Finally extends EventCompoundImpl implements Compound {

    private Prim finallyPrim;
    private static final String FINALLY_CHILD_NAME = "action";

    /**
     * Constructs Try.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Finally() throws java.rmi.RemoteException {
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
     * Deploys and reads the basic configuration of the component. Overrides EventCOmpoundImpl.sfStart.
     *
     * @throws java.rmi.RemoteException In case of network/rmi error
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  In case of any error while deploying the component
     */
    public synchronized void sfDeploy() throws
            SmartFrogException, RemoteException {
        super.sfDeploy();
        checkActionDefined();
    }

    /**
     * Deploys and manages the primary subcomponent.
     *
     * @throws RemoteException    In case of network/rmi error
     * @throws SmartFrogException In case of any error while  starting the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        //Copies component description before deploying it!
        finallyPrim= deployComponentDescription(FINALLY_CHILD_NAME, action);
    }

    /**
     * When we terminate, we deploy the finally child.
     * @param status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        if(finallyPrim!=null) {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("Starting the Finally Action");
            }
            try {
                finallyPrim.sfStart();
            } catch (SmartFrogException e) {
                sfLog().error("When starting the finally action",e);
            } catch (RemoteException e) {
                sfLog().error("When starting the finally action", e);
            }
        }
        super.sfTerminateWith(status);
    }
}
