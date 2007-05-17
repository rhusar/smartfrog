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


package org.smartfrog.projects.alpine.faults;

import javax.xml.namespace.QName;

/**
 * Fault constants. many of these values are from Axis' Constants file
 */
public class FaultConstants {

    /**
     * This is the xmlns for axis1.x fault elements.
     * {@value}
     */
    public static final String NS_URI_AXIS = "http://xml.apache.org/axis/";

    /**
     * XMLNS for Alpine fault details
     * {@value}
     */
    public static final String NS_URI_ALPINE = "http://smartfrog.org/alpine/";


    /**
     * QName of invalid element in an element fault.
     * "invalidNode"
     */
    public static final QName QNAME_FAULTDETAIL_INVALID_XML = new QName(NS_URI_ALPINE, "invalidNode");


    /**
     * QName of stack trace element in an axis fault detail.
     * "stackTrace"
     */
    public static final QName QNAME_FAULTDETAIL_STACKTRACE = new QName(NS_URI_AXIS, "stackTrace");

    /**
     * QName of exception Name element in an axis fault detail. Do not use - this is for pre-1.0 server->client
     * exceptions.
     * "exceptionName"
     */
    public static final QName QNAME_FAULTDETAIL_EXCEPTIONNAME = new QName(NS_URI_AXIS, "exceptionName");

    /**
     * Flag set if this was a runtime exception, rather than something thrown by the class at the end of the chain.
     * Axis' logging treats runtime exceptions more seriously.
     * "isRuntimeException"
     */
    public static final QName QNAME_FAULTDETAIL_RUNTIMEEXCEPTION = new QName(NS_URI_AXIS, "isRuntimeException");

    /**
     * QName of stack trace element in an axis fault detail.
     * "HttpErrorCode"
     */
    public static final QName QNAME_FAULTDETAIL_HTTPERRORCODE = new QName(NS_URI_AXIS, "HttpErrorCode");

    /**
     * QName of a nested fault in an axis fault detail.
     * "nestedFault"
     */
    public static final QName QNAME_FAULTDETAIL_NESTEDFAULT = new QName(NS_URI_AXIS, "nestedFault");

    /**
     * QName of a hostname in an axis fault detail.
     * "hostname"
     */
    public static final QName QNAME_FAULTDETAIL_HOSTNAME = new QName(NS_URI_AXIS, "hostname");

    //QNames of well known faults
    /**
     * The no-service fault value.
     * "Server.NoService"
     */
    public static final QName QNAME_NO_SERVICE_FAULT_CODE
            = new QName(NS_URI_AXIS, "Server.NoService");

    /**
     * The processing party found an invalid namespace for the SOAP Envelope element
     * {@value}
     */
    public static final String FAULTCODE_VERSION_MISMATCH = "VersionMismatch";

    /**
     * header not understood, yet it was marked as MustUnderstand.
     * {@value}
     */
    public static final String FAULTCODE_MUST_UNDERSTAND = "MustUnderstand";

    /**
     * Element name of an EPR in a fault
     * {@value}
     */
    public static final String ALPINE_EPR = "epr";
    /**
     * EPR of the fault
     * "epr"
     */
    public static final QName QNAME_FAULTDETAIL_EPR = new QName(NS_URI_ALPINE, ALPINE_EPR);

    /**
     * Element name of a message in a fault
     * {@value}
     */
    private static final String ALPINE_ELEMENT_RESPONSE = "response";
    /**
     * Element name of a request message in a fault
     * {@value}
     */
    private static final String ALPINE_ELEMENT_REQUEST = "request";

    /**
     * QNAME for a request message in a fault
     * "request"
     */
    public static final QName QNAME_FAULTDETAIL_REQUEST = new QName(NS_URI_ALPINE, ALPINE_ELEMENT_REQUEST);

    /**
     * QNAME for a response message in a fault
     * "response"
     */
    public static final QName QNAME_FAULTDETAIL_RESPONSE = new QName(NS_URI_ALPINE, ALPINE_ELEMENT_RESPONSE);
}
