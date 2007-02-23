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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

/**
 * A SmartFrogDeploymentException is thrown if the attempt at creating the
 * SmartFrog tree from a description fails.
 *
 */
public class SmartFrogDeploymentException extends SmartFrogRuntimeException implements Serializable {

    /** String name for the deployed component description. */
    public final static String COMPONENT_DESCRIPTION =
                                            "deployedComponentDescription";

    /** String name for the context in which component is deployed. */
    public final static String DEPLOY_CONTEXT="deployedContext";

    /** String name for object name. */
    public final static String OBJECT_NAME="objectName";

    /**
     * Constructs a SmartFrogDeploymentException with specified message.
     *
     * @param message exception message
     */
    public SmartFrogDeploymentException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogDeploymentException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified cause. Also
     * initializes the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogDeploymentException(Throwable cause, Prim sfObject) {
       super(cause);
       init(sfObject);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified message and
     * cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogDeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified message.
     * Also initializes the exception context with component details.
     *
     * @param message message
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogDeploymentException(String message, Prim sfObject) {
        super(message);
        init(sfObject);
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified message.
     * Also initializes the exception context with component details.
     *
     * @param message message
     * @param deployContext The context in which a component is deployed
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogDeploymentException(String message,  Prim sfObject,
            Context deployContext) {
        super(message, sfObject);
        init(sfObject);
        put (DEPLOY_CONTEXT,serializableContext(deployContext));
    }

    /**
     * Constructs a SmartFrogDeploymentException with specified message and
     * cause. Also initializes the exception context with component details.
     *
     * @param message message
     * @param cause The cause for exception
     * @param sfObject The Component that has encountered the exception
     * @param deployContext to be merged with the deployed component
     * description
     */
    public SmartFrogDeploymentException(String message, Throwable cause,
            Prim sfObject, Context deployContext) {
        super(message, cause);
        init(sfObject);
        put (DEPLOY_CONTEXT,serializableContext(deployContext));
    }

    /** Constructs a deployment exception.
     *
     * @param ref reference causing the deployment exceptioh
     * @param source source that raised the exception
     * @param name component failed to deploy
     * @param deployedCompDesc compoent description failed to deploy
     * @param deployedContext to be merged with the deployed component
     * description
     * @param message message for exception
     * @param cause cause for exception
     * @param data additional data for exception */
    public SmartFrogDeploymentException(Reference ref, Reference source,
        Object name, ComponentDescription deployedCompDesc,
        Context deployedContext, String message, Throwable cause,
                Object data) {
      super(message,cause);
      if ((source!=null)) put(SOURCE,source.copy());
      if ((ref!=null))put(REFERENCE,ref.copy());
      if (name!=null)put(OBJECT_NAME,name);
      if (deployedCompDesc!=null) put(DEPLOY_CONTEXT,serializableContext(deployedContext));
      if (data!=null) put(DATA,data.toString());
    }

    /**
     * Initializes the exception context with component details.
     *
     * @param sfObject The Component that has encountered the exception
     */
    public void init (Prim sfObject){
        if (sfObject == null) return;
        super.init(sfObject);
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return SmartFrogException that is a SmartFrogDeploymentException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogDeploymentException) {
            return (SmartFrogDeploymentException)thr;
        } else {
            return new SmartFrogDeploymentException (thr);
        }
    }

    /**
     * To forward SmartFrogDeploymentException exceptions instead of chain them.
     * If thr is an instance of SmartFrogDeploymentException then the exception is returned
     * without any modification, if not a new SmartFrogDeploymentException is created
     * with message as a paramenter
     * @param message String message
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogDeploymentException
     */
    public static SmartFrogException forward (String message, Throwable thr){
        if (thr instanceof SmartFrogDeploymentException) {
            if (message!=null){
                ((SmartFrogException)thr).add("msg: ",message);
            }
            return (SmartFrogDeploymentException)thr;
        } else {
            return new SmartFrogDeploymentException(message, thr);
        }
    }


    /**
     * Returns the message.
     *
     * @return the message value
     */
    public String getMessage(){
        StringBuffer strb = new StringBuffer();
        strb.append ((((this.containsKey(SOURCE)&&
                                (this.get(SOURCE)!=null)&&
                                (((Reference)this.get(SOURCE)).size()!=0)))
                                ? (get(SOURCE)+ " failed to deploy ") : "" ));
        strb.append ((((this.containsKey(OBJECT_NAME)))? ("'"+get(OBJECT_NAME)
                                 +"' component") : "unnamed component" ));
        strb.append ((super.getMessage() == null)  ? "" : ". "+super.getMessage().toString());
       return strb.toString();
    }


    /**
     * Returns a string representation of the deployment exception.
     *
     * @param nm Message separator (ex. "\n");
     *
     * @return reason source and ref of exception
     */
    public String toString(String nm) {
        StringBuffer strb = new StringBuffer();
        strb.append (""+ shortClassName() +": ");

        if (getMessage()!=null){
            if ((getCause()!=null) && (getCause().toString().equals(getMessage()))) {
               strb.append (getCauseMessage(nm));
            } else {
                //Only print message when message != cause
                strb.append (getMessage());
                strb.append ((((getCause() == null) ) ? "" : (nm+"cause: " + getCauseMessage(nm))));
            }
        } else {
            strb.append ((((getCause() == null) ) ? "" : (getCauseMessage(nm))));
        }

        strb.append ((((this.containsKey(REFERENCE)
                       && (this.get(REFERENCE)!=null)
                       &&(((Reference)this.get(REFERENCE)).size()!=0)))
                        ? (nm+REFERENCE+  ": " + get(REFERENCE)) : "" ));
        strb.append ((((this.containsKey(COMPONENT_DESCRIPTION)))
                    ? (nm+COMPONENT_DESCRIPTION+  ": "
                        + "included") : "" ));
        strb.append ((((this.containsKey(DEPLOY_CONTEXT)))
                    ? (nm+DEPLOY_CONTEXT+  ": " + "included") : "" ));
        strb.append ((((this.containsKey(PRIM_CONTEXT)))
                    ? (nm+PRIM_CONTEXT+  ": " + "included") : "" ));
        strb.append ((((this.containsKey(DATA)))
                    ? (nm+DATA+  ": " + get(DATA)) : ""));
        return strb.toString();
    }

}
