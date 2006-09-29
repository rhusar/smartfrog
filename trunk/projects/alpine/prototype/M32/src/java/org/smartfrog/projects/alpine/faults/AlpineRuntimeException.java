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

import nu.xom.Document;
import nu.xom.Element;
import org.smartfrog.projects.alpine.interfaces.SoapFaultSource;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.Soap11Constants;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.wsa.AddressingConstants;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * this is a runtime exception
 */
public class AlpineRuntimeException extends RuntimeException implements SoapFaultSource {

    public AlpineRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlpineRuntimeException(Throwable cause) {
        super(cause);
    }

    public AlpineRuntimeException(String message) {
        super(message);
    }

    private List<Element> details = new ArrayList<Element>();

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
     */
    public Fault GenerateSoapFault() {
        Fault fault = new Fault();
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
     * {@link Soap11Constants#FAULTCODE_SERVER};
     *
     * @return the string to be used in the fault code
     */
    protected String getFaultCode() {
        return Soap11Constants.FAULTCODE_SERVER;
    }

    /**
     * This is an override point, subclasses can add stuff to a fault that already
     * has been preconfigured by the base class
     *
     * @param fault
     */
    public void addExtraDetails(Fault fault) {

    }

    /**
     * add some fault detail
     *
     * @param detail
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
     */
    public void addAddressDetails(AlpineEPR epr) {
        if (epr != null) {
            addDetail(epr.toXomInNewNamespace("epr", 
                    FaultConstants.NS_URI_ALPINE, "alpine",
                    AddressingConstants.XMLNS_WSA_2005, "wsa"));
            addDetail(new SoapElement(FaultConstants.QNAME_FAULTDETAIL_HOSTNAME, epr.getAddress()));
        }
    }

    /**
     * Add an address to a fault. As with {@link #addAddressDetails(org.smartfrog.projects.alpine.wsa.AlpineEPR)}
     * the call gracefully handles a null message, or missing address details.
     *
     * @param message message to extract the destination from.
     */
    public void addAddressDetails(MessageDocument message) {
        if (message != null && message.getAddressDetails() != null) {
            addAddressDetails(message.getAddressDetails().getTo());
        }
    }

    /**
     * Returns a short description of this throwable.
     * @return a string representation of this throwable.
     */

    public String toString() {
        Fault fault = GenerateSoapFault();
        return XsdUtils.printToString(fault) + "\n";
    }


}
