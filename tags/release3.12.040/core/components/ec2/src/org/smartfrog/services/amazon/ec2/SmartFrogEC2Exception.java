/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.amazon.ec2;

import com.xerox.amazonws.ec2.EC2Exception;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.Prim;

/**
 * An exception that wraps an {@link EC2Exception}
 */

public class SmartFrogEC2Exception extends SmartFrogDeploymentException {

    /**
     * Constructs a SmartFrogDeploymentException with specified message.
     *
     * @param message exception message
     */
    public SmartFrogEC2Exception(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogEC2Exception(EC2Exception cause) {
        super(cause);
        bind(cause);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified cause. Also initializes the exception context with
     * component details.
     *
     * @param cause    exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogEC2Exception(EC2Exception cause, Prim sfObject) {
        super(cause, sfObject);
        bind(cause);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified message and cause.
     *
     * @param message exception message
     * @param cause   exception causing this exception
     */
    public SmartFrogEC2Exception(String message, EC2Exception cause) {
        super(message, cause);
        bind(cause);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified message and cause. Also initializes  the exception
     * context with component details.
     *
     * @param message  exception message
     * @param cause    exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogEC2Exception(String message, EC2Exception cause, Prim sfObject) {
        super(message, cause, sfObject);
        bind(cause);
    }

    /**
     * Add post-processing of the exception
     *
     * @param cause the root cause
     */
    private void bind(EC2Exception cause) {
        //do nothing yet
    }

}
