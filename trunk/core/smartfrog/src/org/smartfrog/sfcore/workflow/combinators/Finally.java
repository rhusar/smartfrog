package org.smartfrog.sfcore.workflow.combinators;

import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.logging.Log;

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
    protected ComponentHelper helper;

    /**
     * Constructs Try.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Finally() throws java.rmi.RemoteException {
        super();
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
        sfCreateNewChild(FINALLY_CHILD_NAME, action, null);

        //Copies component description before deploying it!
        finallyPrim = sfDeployComponentDescription(FINALLY_CHILD_NAME, this, (ComponentDescription) action.copy(), new ContextImpl());
        // it is now a child, so need to guard against double calling of lifecycle...
        try {
            finallyPrim.sfDeploy();
        } catch (Throwable thrown) {
            //forget about the finally component as we did not deploy properly.
            helper = new ComponentHelper(finallyPrim);
            finallyPrim = null;
            helper.sfSelfDetachAndOrTerminate(TerminationRecord.ABNORMAL,
                    "failed to create", null, thrown);
            throw (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thrown);
        }
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        if(finallyPrim!=null) {
            try {
                finallyPrim.sfStart();
            } catch (SmartFrogException e) {
                sfLog().info("When starting the finally action",e);
            } catch (RemoteException e) {
                sfLog().info("When starting the finally action", e);
            }
        }
        super.sfTerminateWith(status);
    }
}
