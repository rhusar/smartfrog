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

import javax.xml.namespace.QName;

/**
 * constants of soap11
 */
public interface SoapConstants {

    /**
     * {@value}
     */
    public static final String URI_SOAP11 = "http://schemas.xmlsoap.org/soap/envelope/";

    /**
     * {@value}
     */
    public static final String URI_SOAP12 = "http://www.w3.org/2003/05/soap-envelope";

    /**
     * The value for our soap apis
     */
    public static final String URI_SOAPAPI= URI_SOAP11;
    /**
     * {@value}
     */
    public static final String PREFIX_SOAP = "soap";


    /**
     * {@value}
     */
    public static final String ELEMENT_ENVELOPE = "Envelope";

    /**
     * Qname of the element
     */
    public static final QName QNAME_ENVELOPE = new QName(URI_SOAPAPI, ELEMENT_ENVELOPE);

    /**
     * {@value}
     */
    public static final String ELEMENT_HEADER = "Header";
    /**
     * Qname of the element
     */
    public static final QName QNAME_HEADER = new QName(URI_SOAPAPI, ELEMENT_HEADER);


    /**
     * {@value}
     */
    public static final String ELEMENT_BODY = "Body";
    /**
     * Qname of the element
     */
    public static final QName QNAME_BODY = new QName(URI_SOAPAPI, ELEMENT_BODY);


    /**
     * {@value}
     */
    public static final String ELEMENT_FAULT = "Fault";
    /**
     * Qname of the element
     */
    public static final QName QNAME_FAULT = new QName(URI_SOAPAPI, ELEMENT_FAULT);

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
    public static final String ATTR_MUST_UNDERSTAND = "mustUnderstand";

    /**
     * {@value}
     */
    public static final String ATTR_ENCODING_STYLE = "encodingStyle";

    /**
     * {@value}
     */
    public static final String ATTR_ACTOR = "actor";
    /**
     * URI of section5 encoding.
     * {@value}
     */
    public static final String ENCODING_SECTION_5 = "http://schemas.xmlsoap.org/soap/encoding/";

    /*
    * Everything from here down is from Axis
    */
/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

    //
    // Schema XSD Namespaces
    //
    public static final String URI_1999_SCHEMA_XSD =
            "http://www.w3.org/1999/XMLSchema";
    public static final String URI_2000_SCHEMA_XSD =
            "http://www.w3.org/2000/10/XMLSchema";
    public static final String URI_XSD_2001 =
            "http://www.w3.org/2001/XMLSchema";


    public static final String ELEM_NOTUNDERSTOOD = "NotUnderstood";
    public static final String ELEM_UPGRADE = "Upgrade";
    public static final String ELEM_SUPPORTEDENVELOPE = "SupportedEnvelope";

    public static final String ELEM_FAULT_CODE_SOAP12 = "Code";
    public static final String ELEM_FAULT_VALUE_SOAP12 = "Value";
    public static final String ELEM_FAULT_SUBCODE_SOAP12 = "Subcode";
    public static final String ELEM_FAULT_REASON_SOAP12 = "Reason";
    public static final String ELEM_FAULT_NODE_SOAP12 = "Node";
    public static final String ELEM_FAULT_ROLE_SOAP12 = "Role";
    public static final String ELEM_FAULT_DETAIL_SOAP12 = "Detail";
    public static final String ELEM_TEXT_SOAP12 = "Text";


    String URI_XML_1998 = "http://www.w3.org/XML/1998/namespace";
    String URI_WSDL = "http://schemas.xmlsoap.org/wsdl/";


}
