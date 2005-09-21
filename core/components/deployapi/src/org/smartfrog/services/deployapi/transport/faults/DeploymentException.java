package org.smartfrog.services.deployapi.transport.faults;

import org.ggf.xbeans.cddlm.wsrf.wsbf.BaseFaultType;
import org.ggf.xbeans.cddlm.cmp.DeploymentFaultType;
import org.ggf.xbeans.cddlm.cmp.StringListType;
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
     * Constructs a new runtime exception with <code>null</code> as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public DeploymentException(BaseFaultType xmlfault) {
        super(xmlfault);
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

    protected BaseFaultType createInnerFault() {
        return super.createInnerFault();
    }

    /**
     * overriders beware: this is called in the ctor
     *
     * @param baseFaultType
     */
    public void configureInnerFault(BaseFaultType baseFaultType) {
        super.configureInnerFault(baseFaultType);
        if (baseFaultType instanceof DeploymentFaultType) {
            DeploymentFaultType fault = (DeploymentFaultType) baseFaultType;
            if(!fault.isSetStack()) {
                StackTraceElement[] stackTrace = getStackTrace();
                copyStackTrace(fault, stackTrace);
            }
            //TODO: extract
        }
    }

    public void copyStackTrace(DeploymentFaultType fault, StackTraceElement[] stackTrace) {
        StringListType stackList = fault.addNewStack();
        for(StackTraceElement element:stackTrace) {
            stackList.addItem(element.toString());
        }
    }

    public DeploymentFaultType getBaseFault() {
        return (DeploymentFaultType) super.getBaseFault();
    }

    public void setBaseFault(DeploymentFaultType baseFault) {
        super.setBaseFault(baseFault);
    }


    public void addTextElement(QName qname, String text) {
        //TODO
    }


}
