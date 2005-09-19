package org.smartfrog.services.deployapi.transport.faults;

import org.apache.axis2.AxisFault;
import org.ggf.xbeans.cddlm.wsrf.wsbf.BaseFaultType;
import org.ggf.cddlm.utils.QualifiedName;

import javax.xml.namespace.QName;
import java.util.GregorianCalendar;

/**
 */
public class BaseException extends RuntimeException {

    private BaseFaultType baseFault;

    private QName faultCode;

    private String faultReason;

    private String faultActor;

    /**
     * Constructs a new runtime exception with <code>null</code> as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public BaseException() {
    }

    public BaseException(Throwable arg1) {
        super(arg1);
        createAndConfigureInnerFault();
    }

    public static BaseException makeFault(Throwable thrown) {
        return new BaseException(thrown);
    }

    /**
     * @param arg0
     */
    public BaseException(String arg0) {
        super(arg0);
        createAndConfigureInnerFault();
    }

    /**
     * Constructs a new runtime exception with <code>null</code> as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public BaseException(BaseFaultType xmlfault) {
        this.baseFault = xmlfault;
        createAndConfigureInnerFault();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public BaseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        createInnerFault();
    }

    protected void createAndConfigureInnerFault() {
        BaseFaultType baseFaultType = createInnerFault();
        configureInnerFault(baseFaultType);
        setBaseFault(baseFaultType);
    }


    /**
     * overriders beware: this is called in the ctor
     */
    protected BaseFaultType createInnerFault() {
        BaseFaultType baseFaultType = BaseFaultType.Factory.newInstance();
        configureInnerFault(baseFaultType);
        return baseFaultType;
    }

    /**
     * overriders beware: this is called in the ctor
     * @param baseFaultType
     */
    public void configureInnerFault(BaseFaultType baseFaultType) {
        if(baseFaultType.getTimestamp()!=null) {
            baseFaultType.setTimestamp(new GregorianCalendar());
        }
    }


    public BaseFaultType getBaseFault() {
        return baseFault;
    }

    public void setBaseFault(BaseFaultType baseFault) {
        this.baseFault = baseFault;
    }

    public QName getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(QName faultCode) {
        this.faultCode = faultCode;
    }

    public void setFaultCode(QualifiedName faultCode) {
        setFaultCode(
                faultCode==null?null:
                    new QName(faultCode.getNamespaceURI(),faultCode.getNamespaceURI()));
    }

    public String getFaultReason() {
        return faultReason;
    }

    public void setFaultReason(String faultReason) {
        this.faultReason = faultReason;
    }

    public String getFaultActor() {
        return faultActor;
    }

    public void setFaultActor(String faultActor) {
        this.faultActor = faultActor;
    }


    /**
     * Turn into an Axis Fault or otherwise serialize
     * @return
     */
    public AxisFault makeAxisFault() {
        return new AxisFault(this);
    }

    public void addFaultDetail(QName name, String detail) {
        //TODO
    }
}
