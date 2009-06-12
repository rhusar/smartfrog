/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.projects.alpine.faults;

import nu.xom.Element;
import org.smartfrog.projects.alpine.interfaces.SoapFaultSource;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.wsa.AddressingConstants;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * this is a runtime exception
 */
public class AlpineRuntimeException extends RuntimeException implements SoapFaultSource {

    /**
     * List of details
     */
    private List<Element> details = new ArrayList<Element>();

    /**
     * create from a message and a nested fault
     * @param message message
     * @param cause underlying cause
     */
    public AlpineRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create from a throwable
     * @param cause underlying cause
     */
    public AlpineRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Create from a text message
     * @param message message
     */
    public AlpineRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with <code>null</code> as its detail
     * message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public AlpineRuntimeException() {
    }

    /**
     * Create a soap fault from ourselves.
     * subclass this to add more detail than just the message, stack trace,
     *
     * @return a fault
     * @param soapNamespace namespace to generate
     */
    public Fault GenerateSoapFault(String soapNamespace) {
        Fault fault = new Fault(SoapConstants.ELEMENT_FAULT, soapNamespace);
        fault.setFaultCode(getFaultCode());
        fault.addThrowable(this);
        addExtraDetails(fault);
        for (Element e : details) {
            fault.appendToFaultDetail(e.copy());
        }
        return fault;
    }

    /**
     * Override point: get a fault code. the default is
     * {@link SoapConstants#FAULTCODE_SERVER};
     *
     * @return the string to be used in the fault code
     */
    protected String getFaultCode() {
        return SoapConstants.FAULTCODE_SERVER;
    }

    /**
     * This is an override point, subclasses can add stuff to a fault that already
     * has been preconfigured by the base class
     *
     * @param fault fault details
     */
    public void addExtraDetails(Fault fault) {

    }

    /**
     * add some fault detail
     *
     * @param detail new element to dadd
     */
    public void addDetail(Element detail) {
        details.add(detail);
    }

    /**
     * add a new detail element with the given text value
     *
     * @param name  element name
     * @param value text to add as a child
     */
    public void addDetail(QName name, String value) {
        details.add(new SoapElement(name, value));
    }

    /**
     * Add an address to a fault. Because this is part of the error handling,
     * we deliberately don't throw an NPE if the address is null, because it
     * would only hide the underlying fault.
     *
     * @param epr the endpoint
     * @param wsaNamespace addressing namespace
     */
    public void addAddressDetails(AlpineEPR epr, String wsaNamespace) {
        if (epr != null) {
            String ns=wsaNamespace!=null? wsaNamespace: AddressingConstants.XMLNS_WSA_2005;
            addDetail(epr.toXomInNewNamespace("epr", 
                    FaultConstants.NS_URI_ALPINE, "alpine",
                    ns, "wsa"));
            addDetail(new SoapElement(FaultConstants.QNAME_FAULTDETAIL_HOSTNAME, 
                    epr.getAddress()));
        }
    }

    /**
     * Add an address to a fault. As with {@link #addAddressDetails(AlpineEPR, String)}
     * the call gracefully handles a null message, or missing address details.
     *
     * @param message message to extract the destination from.
     */
    public void addAddressDetails(MessageDocument message) {
        if (message != null && message.getAddressDetails() != null) {
            AddressDetails addr = message.getAddressDetails();
            addAddressDetails(addr.getTo(), addr.getNamespace());
        }
    }

    /**
     * Returns a short description of this throwable.
     * @return a string representation of this throwable.
     */

    public String toString() {
        Fault fault = GenerateSoapFault(SoapConstants.URI_SOAPAPI);
        return XsdUtils.printToString(fault) + '\n';
    }


}
