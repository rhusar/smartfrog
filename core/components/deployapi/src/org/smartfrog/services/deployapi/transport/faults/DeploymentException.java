package org.smartfrog.services.deployapi.transport.faults;

import org.ggf.cddlm.utils.FaultTemplate;


/**
 */
public class DeploymentException extends BaseException {



    public DeploymentException() {
    }

    public DeploymentException(Throwable arg1) {
        super(arg1);
    }

    /**
     * @param message
     */
    public DeploymentException(String message) {
        super(message);
    }


    /**
     * @param message
     * @param arg1
     */
    public DeploymentException(String message, Throwable arg1) {
        super(message, arg1);
    }

    public DeploymentException(FaultTemplate template) {
        super(template);
    }




}
