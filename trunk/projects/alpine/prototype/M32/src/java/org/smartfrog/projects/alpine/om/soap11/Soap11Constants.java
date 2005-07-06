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

package org.smartfrog.projects.alpine.om.soap11;

/**
 * constants of soap11
 */
public interface Soap11Constants {
    
    /**
     * {@value}
     */
    public static final String NAMESPACE_SOAP11="http://schemas.xmlsoap.org/soap/envelope/";
    
    /**
     * {@value}
     */
    public static final String ELEMENT_ENVELOPE="Envelope";
    
    /**
     * {@value}
     */
    public static final String ELEMENT_HEADER="Header";

    /**
     * {@value}
     */
    public static final String ELEMENT_BODY = "Body";
    

    /**
     * {@value}
     */
    public static final String ELEMENT_FAULT = "Fault";

    /**
     * {@value}
     */
    public static final String FAULT_CODE = "faultcode";

    /**
     * {@value}
     */
    public static final String FAULT_STRING = "faultstring";

    /**
     * {@value}
     */
    public static final String FAULT_ACTOR = "faultactor";
    /**
     * {@value}
     */
    public static final String FAULT_DETAIL = "detail";
    
    /**
     * The processing party found an invalid 
     * namespace for the SOAP Envelope element (see section 4.1.2)
     * {@value}
     */
    public static final String FAULTCODE_VERSION_MISMATCH = "VersionMismatch";
    
    /**
     * MustUnderstand
     * An immediate child element of the SOAP Header element that was either
     * not understood or not obeyed by the processing party contained a SOAP
     * mustUnderstand attribute with a value of "1" (see section 4.2.3)
     * {@value}
     */
    public static final String FAULTCODE_MUST_UNDERSTAND = "MustUnderstand";

    /**
     * The Client class of errors indicate that the message was incorrectly 
     * formed or did not contain the appropriate information in order to succeed.
     * {@value}
     */
    public static final String FAULTCODE_CLIENT = "Client";
    /**
     * The Server class of errors indicate that the message could not be processed 
     * for reasons not directly attributable to the contents of the message itself 
     * but rather to the processing of the message.
     * {@value}
     */
    public static final String FAULTCODE_SERVER = "Server";
    /**
     * {@value}
     */
    public static final String ACTOR_NEXT = "http://schemas.xmlsoap.org/soap/actor/next";
    
    /**
     * {@value}
     */
    public static final String ATTR_MUSTUNDERSTAND = "mustUnderstand";

    /**
     * {@value}
     */
    public static final String ATTR_ENCODING = "encodingStyle";
    
    /**
     * {@value}
     */
    public static final String ATTR_ACTOR = "actor";
    /**
     * URI of section5 encoding. 
     * {@value}
     */
    public static final String ENCODING_SECTION_5 = "http://schemas.xmlsoap.org/soap/encoding/";
    
}
