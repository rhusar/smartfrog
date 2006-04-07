package org.smartfrog.services.deployapi.transport.faults;

import org.apache.axis2.AxisFault;
import org.ggf.cddlm.utils.FaultTemplate;

import javax.xml.namespace.QName;

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

    /**
     * nest an axisFault inside. Maybe we have special treatment here
     *
     * @param axisFault
     * @TODO: special treatment here :)
     */
    public DeploymentException(AxisFault axisFault) {
        super(axisFault);
    }


    /**
     * overriders beware: this is called in the ctor
     *
     */
/*
    public void configureInnerFault() {
        super.configureInnerFault();
        if (baseFaultType instanceof DeploymentFaultType) {
            DeploymentFaultType fault = (DeploymentFaultType) baseFaultType;
            if (!fault.isSetStack()) {
                StackTraceElement[] stackTrace = getStackTrace();
                copyStackTrace(fault, stackTrace);
            }
            //TODO: extract
        }
    }
*/
/*

    public void copyStackTrace(DeploymentFaultType fault, StackTraceElement[] stackTrace) {
        StringListType stackList = fault.addNewStack();
        for (StackTraceElement element : stackTrace) {
            stackList.addItem(element.toString());
        }
    }
*/



    public void addTextElement(QName qname, String text) {
        //TODO
    }


}
