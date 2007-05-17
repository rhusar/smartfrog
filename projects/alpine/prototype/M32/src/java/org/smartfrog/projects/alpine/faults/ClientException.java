/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

import org.smartfrog.projects.alpine.om.soap11.SoapConstants;

/**
 * created 11-Apr-2006 10:33:20
 */

public class ClientException extends AlpineRuntimeException {

    /**
     * Construct an exception.
     * @param message message
     * @param cause underlying cause
     */
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct an exception.
     * @param cause underlying cause
     */
    public ClientException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct an exception. 
     * @param message message
     */
    public ClientException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with <code>null</code> as its detail
     * message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public ClientException() {
    }

    /**
     * Override point: get a fault code. the default is
     * {@link org.smartfrog.projects.alpine.om.soap11.SoapConstants#FAULTCODE_SERVER};
     *
     * @return the string to be used in the fault code
     */
    protected String getFaultCode() {
        return SoapConstants.FAULTCODE_CLIENT;
    }

}
