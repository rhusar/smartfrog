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

import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.interfaces.SoapFaultSource;

/**
 * This represents a SOAPException from the XML. 
 * The fault string is extracted, the rest is retained as XML.
 */
public class SoapException extends AlpineRuntimeException implements SoapFaultSource {

    private Fault fault;
    
    /**
     * message is extracted from the fault string
     * @param fault
     */ 
    public SoapException(Fault fault) {
        super(fault.getFaultString()!=null? fault.getFaultString():
             fault.toXML());
        this.fault = fault;
        if(fault.getFaultString()==null) {

        }
    }

    /**
     * Create a full fault
     * @param faultcode faultcode
     * @param faultActor actor
     * @param message message (to use as FaultString)
     * @param detail optional detail
     */
    public SoapException(String faultcode, String faultActor,String message, SoapElement detail) {

        super(message);
        fault=new Fault();
        fault.setFaultCode(faultcode);
        fault.setFaultActor(faultActor);
        fault.setFaultString(message);
        fault.setFaultDetail(detail);
    }

    /**
     * a custom message
     * @param message
     * @param fault
     */ 
    public SoapException(String message, Fault fault) {
        super(message);
        this.fault = fault;
    }

    /**
     * create an exception with an underlying cause and a fault
     * @param message
     * @param cause
     * @param fault
     */ 
    public SoapException(String message, Throwable cause, Fault fault) {
        super(message, cause);
        this.fault = fault;
    }

    /**
     * message is extracted from the fault string
     * @param cause
     * @param fault
     */ 
    public SoapException(Throwable cause, Fault fault) {
        super(cause);
        this.fault = fault;
    }

    public SoapException(String text,MessageDocument message) {
        this(text,message.getFault());
        addAddressDetails(message);
    }

    public SoapException(MessageDocument message) {
        this(message.getFault());
        addAddressDetails(message);
    }

    /**
     * Get the fault information
     * @return
     */ 
    public Fault getFault() {
        return fault;
    }

    /**
     * return the fault we were built with
     *
     * @return a fault
     */
    public Fault GenerateSoapFault() {
        return fault;
    }

    /**
     * Get the fault code.
     *
     * @return the string to be used in the fault code
     */
    public String getFaultCode() {
        return fault.getFaultCode();
    }

    public String getFaultActor() {
        return fault.getFaultActor();
    }

    


}
