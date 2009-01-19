/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.reference.Reference;

/**
  * A SmartFrogFunctionResolutionException is thrown when an irrecoverable
  * resolution error occurs while compilation.
 */
public class SmartFrogTypeResolutionException extends
                                               SmartFrogPhaseResolutionException {
    /** String name for resolution phase. */
    { RESOLUTION_PHASE = "type resolution"; }

    /**
     * Constructs a SmartFrogResolutionException with specified message.
     *
     * @param message exception message
     */
    public SmartFrogTypeResolutionException(String message) {
        super(message);
    }


    /**
     *  Constructs a resolution exception with additional data.
     *
     *@param  message exception message
     *@param  cause  exception causing this exception
     *@param  source source that raised the exception
     *@param  data    additional data for exception
     */
    public SmartFrogTypeResolutionException (String message, Throwable cause, Reference source, Object data) {
       super(message, cause, source, data);
    }


    /**
     * Constructs a SmartFrogTypeResolutionException with specified cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogTypeResolutionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogFunctionResolutionException with specified message
     * and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogTypeResolutionException(String message,
                                            Throwable cause) {
        super(message, cause);
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return SmartFrogException that is a SmartFrogTypeResolutionException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogTypeResolutionException) {
            return (SmartFrogTypeResolutionException)thr;
        } else {
            return new SmartFrogTypeResolutionException (thr);
        }
    }
}
