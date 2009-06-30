package org.smartfrog.services.persistence.test.testcases;

import java.rmi.RemoteException;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.rcomponent.RComponentImpl;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;


public class FailingRComponentTestImpl extends RComponentImpl implements RComponent {

    private static enum FailPoint {
        DEPLOY_WITH(false, "deployWith"),
        DEPLOY(false, "deploy"),
        START(false, "start"),
        RECOVERY_DEPLOY_WITH(true, "recoveryDeployWith"),
        RECOVERY_DEPLOY(true, "recoveryDeploy"),
        RECOVERY_START(true, "recoveryStart");
        private final boolean recovery;
        private final String lifeCycle;
        FailPoint(boolean recovery, String lifeCycle) {
            this.recovery = recovery;
            this.lifeCycle = lifeCycle;
        }
        public void here(boolean rec, Object lc, LogSF log) {
            if( rec == recovery &&  lifeCycle.equals(lc) ) {
                if( log.isInfoEnabled() ) {
                    log.info("TEST: throwing RuntimeException in lifecycle step " + lifeCycle);
                }
                throw new RuntimeException("TEST: throwing RuntimeException in lifecycle step " + lifeCycle);
            }
        }
    }
    
    private static final String FAIL_POINT_ATTR = "failPoint";    
    private Object failPoint = null;
    
    public FailingRComponentTestImpl() throws RemoteException {
        super();
    }
    
    public void sfDeployWith(Prim parent, Context context) throws SmartFrogDeploymentException, RemoteException  {
        super.sfDeployWith(parent, context);
        
        if( context.sfContainsAttribute(FAIL_POINT_ATTR) ) {
            try {
                failPoint = context.sfResolveAttribute(FAIL_POINT_ATTR);
            } catch (SmartFrogContextException e) {
            }
        } 
        
        FailPoint.DEPLOY_WITH.here(sfIsRecovered, failPoint, sfLog());
        FailPoint.RECOVERY_DEPLOY_WITH.here(sfIsRecovered, failPoint, sfLog());
        
    }
    
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        
        FailPoint.DEPLOY.here(sfIsRecovered, failPoint, sfLog());
        FailPoint.RECOVERY_DEPLOY.here(sfIsRecovered, failPoint, sfLog());
        
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        
        FailPoint.START.here(sfIsRecovered, failPoint, sfLog());
        FailPoint.RECOVERY_START.here(sfIsRecovered, failPoint, sfLog());
        
    }

}
