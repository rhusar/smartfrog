package org.smartfrog.services.sfinterface;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.rmi.RemoteException;


public class SubmitterThread extends SmartFrogThread {
    private String compName = null;
    private ComponentDescription compDes = null;
    private Prim submittedComp;

    SubmitterThread(Prim p) {
        this.submittedComp = p;
    }


    /**
     * If this thread was constructed using a separate {@link Runnable} run
     * object, then that <code>Runnable</code> object's <code>run</code> method
     * is called; otherwise, this method does nothing and returns. <p>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @throws Throwable if anything went wrong
     */
    public void execute() throws Throwable {
        SmartFrogAdapterImpl.sfLog().info("Inside ST");
        try {
            //SmartFrogAdapterImpl.sfDaemon.sfCreateNewApp(compName, compDes, null);
            submittedComp.sfDeploy();
            submittedComp.sfStart();

        } catch (Throwable e) {
            SmartFrogAdapterImpl.sfLog().error("Deployment Exception", e);
            throw e;
        }
    }
}
