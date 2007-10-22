/*
Service Location Protocol - SmartFrog components.
 Copyright (C) 1998-2003 Hewlett-Packard Development Company, LP
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 Copyright (C) 2007 Hewlett-Packard Development Company, LP
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
*/

package org.smartfrog.services.comm.slp;

import org.smartfrog.sfcore.common.SmartFrogException;


/** @author Guillaume Mecheneau */
public class ServiceLocationException extends SmartFrogException {

    /**
     * The location service did not have a registration in the language locale of the request, although it did have one
     * in another language locale. Not properly used throughout the implementation for the moment
     *
     * {@value}
     */
    public static final short LANGUAGE_NOT_SUPPORTED = 1;

    /** An error occured while parsing a URL, attribute list, or other part of a service location message. {@value} */
    public static final short PARSE_ERROR = 2;

    /**
     * Upon registration, this error is returned if the URL is invalid or if some other problem occurs with the
     * registration. Upon deregistration it is also returned if the URL is not registered. {@value}
     */
    public static final short INVALID_REGISTRATION = 3;

    /** An attempt was made to register in a scope not supported. {@value} */
    public static final short SCOPE_NOT_SUPPORTED = 4;

    /** The given SLP SPI is not supported {@value} */
    public static final short AUTHENTICATION_UNKNOWN = 5;

    /** Authentication was missing from a message that required it. {@value} */
    public static final short AUTHENTICATION_ABSENT = 6;

    /** Authentication failed on a message. {@value} */
    public static final short AUTHENTICATION_FAILED = 7;

    /** An attempt was made to update a nonexisting registration. {@value} */
    public static final short INVALID_UPDATE = 13;

    /** The service URL lifetime was rejected by the directory agent. {@value} */
    public static final short INVALID_LIFETIME = 8;

    /** Operation isn't implemented. {@value} */
    public static final short NOT_IMPLEMENTED = 9;

    /** Initialization of the network failed. {@value} */
    public static final short NETWORK_INIT_FAILED = 10;

    /** A TCP connection timed out. {@value} */
    public static final short NETWORK_TIMED_OUT = 5;

    /** An error occured during networking. {@value} */
    public static final short NETWORK_ERROR = 12;

    /** An error occured in the client-side code. {@value} */
    public static final short INTERNAL_SYSTEM_ERROR = 11;

    /** Registration failed to match the service type template or schema. {@value} */
    public static final short TYPE_ERROR = 14;

    /** Packet size overflow on transmission. {@value} */
    public static final short BUFFER_OVERFLOW = 15;

    /** Could not contact DA. Set to a high number to avoid conflicts with the standard values. {@value} */
    public static final short DA_NOT_AVAILABLE = 500;
    /** Could not find what was sought. Set to a high number to avoid conflicts with the standard values. {@value} */
    public static final short LOOKUP_FAILED = 404;

    public static final short UNKNOWN_HOST = 501;
    private short errorCode;

    /** Service Location Exception */
    public ServiceLocationException() {
    }

    public ServiceLocationException(String s) {
        super(s);
    }

    public ServiceLocationException(short errorCode) {
        this.errorCode = errorCode;
    }

    public ServiceLocationException(short errorCode, String s) {
        super(s);
        this.errorCode = errorCode;
    }


    public ServiceLocationException(short errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Return the error code.
     *
     * @return The integer error code.
     */
    public short getErrorCode() {
        return errorCode;
    }

    /** Return the localized message, in the default locale. */
    public String getMessage() {
        return super.getMessage();
    }

    public String toString() {
        return "ServiceLocationException error code: " + errorCode +
                "\n\t" + super.toString();
    }
}
