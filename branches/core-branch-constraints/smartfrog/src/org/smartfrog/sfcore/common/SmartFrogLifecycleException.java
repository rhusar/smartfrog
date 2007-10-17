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

import java.io.Serializable;

import org.smartfrog.sfcore.prim.Prim;

/**
 * A SmartFrogLifecycleException is thrown if the attempt at executing one of
 * the SmartFrog lifecycle phases fails.
 *
 */
public class SmartFrogLifecycleException extends SmartFrogRuntimeException implements Serializable {
    /**
     * Constructs a SmartFrogLifecycleException with message.
     *
     * @param message exception message
     */
    public SmartFrogLifecycleException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogLifecycleException with cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogLifecycleException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogLifecycleException with cause. Also initializes
     * the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogLifecycleException(Throwable cause, Prim sfObject) {
        super(cause, sfObject);
    }

    /**
     * Constructs a SmartFrogLifecycleException with message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogLifecycleException with message. Also initializes
     * the exception context with component details.
     *
     * @param message exception message
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogLifecycleException(String message, Prim sfObject) {
        super(message, sfObject);
    }

    /**
     * Constructs a SmartFrogLifecycleException with message and cause.
     * Also initializes the exception context with component details.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogLifecycleException(String message, Throwable cause,
            Prim sfObject) {
        super(message, cause, sfObject);
    }

    /**
     * Forwards the SmartFrog exceptions at the time of deployment.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject the component that has encountered the exception
     *
     * @return SmartFrogException that is a SmartFrogLifecycleException
     */
    static public SmartFrogLifecycleException sfDeploy(String message, Throwable cause,
            Prim sfObject) {
       try {
            return (SmartFrogLifecycleException)SmartFrogLifecycleException.forward("[sfDeploy] "+message, cause, sfObject);
        } catch (Throwable thr) {
            return (SmartFrogLifecycleException)SmartFrogLifecycleException.forward("[sfDeploy] "+message, cause);
        }
    }

    /**
     * Forwards the SmartFrog exceptions at the time of sfStart.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject the component that has encountered the exception
     *
     * @return SmartFrogException that is a SmartFrogLifecycleException
     */
    static public SmartFrogLifecycleException sfStart(String message, Throwable cause,
            Prim sfObject) {
        try {
            return (SmartFrogLifecycleException)SmartFrogLifecycleException.forward("[sfStart] "+message, cause, sfObject);
        } catch (Throwable thr) {
            return (SmartFrogLifecycleException)SmartFrogLifecycleException.forward("[sfStart] "+message, cause);
        }
    }


    /**
     * Forwards the SmartFrog exceptions at the time of termination.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject the component that has encountered the exception
     *
     * @return SmartFrogException that is a SmartFrogLifecycleException
     */
    static public SmartFrogLifecycleException sfTerminate (String message, Throwable cause,
            Prim sfObject) {
        try {
            return (SmartFrogLifecycleException)SmartFrogLifecycleException.forward("[sfTerminate] "+message, cause,
                        sfObject);
        } catch (Throwable thr) {
           return (SmartFrogLifecycleException)SmartFrogLifecycleException.forward( "[sfTerminate] " + message, cause);
        }

    }

    /**
     * Forwards the SmartFrog exceptions at the time of termination.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject the component that has encountered the exception
     *
     * @return SmartFrogException that is a SmartFrogLifecycleException
     */
    static public SmartFrogLifecycleException sfTerminateWith (String message, Throwable cause,
            Prim sfObject) {
        return (SmartFrogLifecycleException) SmartFrogLifecycleException.forward( "sfTerminateWith: " + message, cause,
            sfObject);
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return SmartFrogException that is a SmartFrogLifecycleException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogLifecycleException) {
            return (SmartFrogLifecycleException)thr;
        } else {
            return new SmartFrogLifecycleException (thr);
        }
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     * If thr is an instance of SmartFrogLifecycleException then the exception is returned
     * without any modification, if not a new SmartFrogException is created
     * with message as a paramenter
     * @param message String message
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogException
     */
    public static SmartFrogException forward (String message, Throwable thr){
        if (thr instanceof SmartFrogLifecycleException) {
            if (message!=null){
                ((SmartFrogLifecycleException)thr).add("msg: ",message);
            }
            return (SmartFrogLifecycleException)thr;
        } else {
            return new SmartFrogLifecycleException(message, thr);
        }
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param message message
     * @param thr throwable object to be forwarded
     * @param sfObject the component that has encountered the exception
     *
     * @return SmartFrogException that is a SmartFrogLifecycleException
     */
    public static SmartFrogException forward (String message, Throwable thr, Prim sfObject){
        try {
            if (thr instanceof SmartFrogLifecycleException) {
                // add message to data
                if (sfObject!=null) {
                    try {
                        ((SmartFrogException)thr).init(sfObject);
                        if ((message!=null)&&(!message.equals(""))) {
                            String name = null;
                            try {
                                name = "msg:"+ sfObject.sfCompleteName().toString();
                            } catch (Exception ex) {
                            }
                            ((SmartFrogLifecycleException)thr).add(name, message);
                        }
                    } catch (Throwable ex2) {
                    }
                }
                return (SmartFrogLifecycleException)thr;
            } else {
                return new SmartFrogLifecycleException(message, thr, sfObject);
            }
        } catch (Throwable ex1) {
            ex1.printStackTrace();
        }

        return  (SmartFrogLifecycleException)SmartFrogLifecycleException.forward(thr);
    }
}
