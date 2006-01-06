package org.smartfrog.services.deployapi.transport.faults;

import nu.xom.Element;
import org.apache.axis2.AxisFault;
import org.ggf.cddlm.utils.FaultTemplate;
import org.smartfrog.services.deployapi.binding.XomHelper;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 */
public class BaseException extends RuntimeException {


    private QName faultCode;

    private String faultReason;

    private String faultActor;

    private Calendar timestamp= new GregorianCalendar();

    /**
     * This is here because WS-BaseFaults persists in having some world view of declared faults, forcing
     * the recipient to expect to handle everything that went wrong.
     */
    private List<Element> data=new ArrayList<Element>();


    /**
     * Constructs a new runtime exception with <code>null</code> as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public BaseException() {
    }

    public BaseException(Throwable arg1) {
        super(arg1);
        init();
    }


    public BaseException(FaultTemplate template) {
        super(template.getErrorMessage());
        initFromTemplate(template);
    }



    public BaseException(FaultTemplate template,Throwable thrown) {
        super(template.getErrorMessage(),thrown);
        initFromTemplate(template);
    }


    /**
     * @param message
     */
    public BaseException(String message) {
        super(message);
        init();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public BaseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        init();
    }

    private void init() {
    }

    /**
     * Override point: get the default faultcode
     *
     * @return the qname of the default fault code for this type of fault
     */
    protected QName getDefaultFaultCode() {
        //TODO
        return null;
    }


    private void initFromTemplate(FaultTemplate template) {
        init();
        QName qualifiedName = template.getQualifiedName();
        setFaultCode(qualifiedName);
        faultReason = template.getWireMessage();
    }

    /**
     * convert a throwable into a BaseException.
     *
     * @param thrown
     * @return
     */
    public static BaseException makeFault(Throwable thrown) {
        if (thrown instanceof BaseException) {
            return (BaseException) thrown;
        }
        if (thrown instanceof AxisFault) {
            return new DeploymentException((AxisFault) thrown);
        }
        return new BaseException(thrown);
    }

    protected void initFaultCode() {
        if (faultCode == null) {
            faultCode = getDefaultFaultCode();
        }
    }

    public QName getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(QName faultCode) {
        this.faultCode = faultCode;
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

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Turn into an Axis Fault or otherwise serialize
     *
     * @return
     */
    public AxisFault makeAxisFault() {
        AxisFault fault = new AxisFault(this);

        fault.setFaultCode(faultCode!=null?faultCode.toString():"no-fault-code");
        return fault;
    }

    /**
     * add some fault detail
     * @param name
     * @param detail
     */
    public void addFaultDetail(QName name, String detail) {
        Element elt= XomHelper.element(name);
        elt.appendChild(detail);
        data.add(elt);
    }
}
