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

/**
 * All the messages keys used in SmartFrog system should be defined here. The
 * classes using the SmartFrog exception framework should implement
 * MessageKeys interface.
 */
public interface MessageKeys {
    /** 
     * Message key: receiving deploy request after the component is terminated.
     */     
    public static String MSG_DEPLOY_COMP_TERMINATED
                                            = "MSG_DEPLOY_COMP_TERMINATED";
    /** 
     * Message key: receiving start request after the component is terminated.
     */     
    public static String MSG_START_COMP_TERMINATED
                                            = "MSG_START_COMP_TERMINATED";
    /** 
     * Message key: file cannot be found. 
     */
    public static String MSG_FILE_NOT_FOUND = "MSG_FILE_NOT_FOUND";
    /** 
     * Message key: Compound has a non-replacable attribute.
     */     
    public static String MSG_NON_REP_ATTRIB = "MSG_NON_REP_ATTRIB";
    /** 
     * Message key: class cannot be found. 
     */
    public static String MSG_CLASS_NOT_FOUND = "MSG_CLASS_NOT_FOUND";
    /** 
     * Message key: class cannot be instantiated. 
     */
    public static String MSG_INSTANTIATION_ERROR = "MSG_INSTANTIATION_ERROR";
    /** 
     * Message key: class has illegal access on method. 
     */
    public static String MSG_ILLEGAL_ACCESS = "MSG_ILLEGAL_ACCESS";
    /** 
     * Message key: class has invocation target error.
     */  
    public static String MSG_INVOCATION_TARGET = "MSG_INVOCATION_TARGET";
    /** 
     * Message key: method not found.
     */ 
    public static String MSG_METHOD_NOT_FOUND = "MSG_METHOD_NOT_FOUND";
    /** 
     * Message key: input stream is null.
     */  
    public static String MSG_INPUTSTREAM_NULL = "MSG_INPUTSTREAM_NULL";
    /** 
     * Message key: failed to find parent location.
     */  
    public static String MSG_PARENT_LOCATION_FAILED
                                    = "MSG_PARENT_LOCATION_FAILED";
    /** 
     * Message key: failed to contact parent.
     */  
    public static String MSG_FAILED_TO_CONTACT_PARENT
                                    = "MSG_FAILED_TO_CONTACT_PARENT";
    /** 
     * Message key: failure in deploywith phase.
     */  
    public static String MSG_DEPLOYWITH_PHASE_FAILED
                                    = "MSG_DEPLOYWITH_PHASE_FAILED";
    /** 
     * Message key: failure in object registration.
     */  
    public static String MSG_OBJECT_REGISTRATION_FAILED
                                    = "MSG_OBJECT_REGISTRATION_FAILED";
    /** 
     * Message key: failure in starting liveness thread.
     */  
    public static String MSG_LIVENESS_START_FAILED
                                    = "MSG_LIVENESS_START_FAILED";
    /** 
     * Message key: failure in hook action.
     */  
    public static String MSG_HOOK_ACTION_FAILED = "MSG_HOOK_ACTION_FAILED";
    /** 
     * Message key: unable to start a component randomly.
     */  
    public static String MSG_RANDM_ERR = "MSG_RANDM_ERR";
    /** 
     * Message key: found invalid object type.
     */  
    public static String MSG_INVALID_OBJECT_TYPE = "MSG_INVALID_OBJECT_TYPE";
    /** 
     * Message key: url is null.
     */  
    public static String MSG_NULL_URL = "MSG_NULL_URL";
    /** 
     * Message key: unable to locate language in url.
     */  
    public static String MSG_LANG_NOT_FOUND = "MSG_LANG_NOT_FOUND";
    /** 
     * Message key: unable to locate file or url.
     */  
    public static String MSG_URL_NOT_FOUND = "MSG_URL_NOT_FOUND";
    /** 
     * Message key: error in deployment of url .
     */  
    public static String MSG_ERR_DEPLOY_FROM_URL = "MSG_ERR_DEPLOY_FROM_URL";
    /** 
     * Message key: stack trace follows.
     */  
    public static String MSG_STACKTRACE_FOLLOWS = "MSG_STACKTRACE_FOLLOWS";
    /** 
     * Message key: continuing with other deployment.
     */  
    public static String MSG_CONT_OTHER_DEPLOY = "MSG_CONT_OTHER_DEPLOY";
    /** 
     * Message key: error during termination.
     */  
    public static String MSG_ERR_TERM = "MSG_ERR_TERM";
    /** 
     * Message key: parser error.
     */  
    public static String MSG_ERR_PARSE = "MSG_ERR_PARSE";
    /** 
     * Message key: parser error while resolving phases.
     */  
    public static String MSG_ERR_RESOLVE_PHASE = "MSG_ERR_RESOLVE_PHASE";
    /** 
     * Message key: smartfrog ready.
     */  
    public static String MSG_SF_READY = "MSG_SF_READY";
    /** 
     * Message key: smartfrog dead.
     */  
    public static String MSG_SF_DEAD = "MSG_SF_DEAD";
    /** 
     * Message key: smartfrog daemon terminated.
     */  
    public static String MSG_SF_TERMINATED = "MSG_SF_TERMINATED";
    /** 
     * Message key: error in starting smartfrog daemon.
     */  
    public static String MSG_ERR_SF_RUNNING = "MSG_ERR_SF_RUNNING";
    /** 
     * Message key: unable to locate host.
     */  
    public static String MSG_UNKNOWN_HOST = "MSG_UNKNOWN_HOST";
    /** 
     * Message key: unable to connect to daemon.
     */  
    public static String MSG_CONNECT_ERR = "MSG_CONNECT_ERR";
    /** 
     * Message key: unable to connect to daemon.
     */  
    public static String MSG_REMOTE_CONNECT_ERR = "MSG_REMOTE_CONNECT_ERR";
    /** 
     * Message key: unable to connect to daemon on remote host.
     */  
    public static String MSG_DEPLOY_SUCCESS = "MSG_DEPLOY_SUCCESS";
    /** 
     * Message key: successful termination of components.
     */  
    public static String MSG_TERMINATE_SUCCESS = "MSG_TERMINATE_SUCCESS";
    /** 
     * Message key: stack trace logging enabled.
     */  
    public static String MSG_WARNING_STACKTRACE_ENABLED
                        = "MSG_WARNING_STACKTRACE_ENABLED";
    /** 
     * Message key: stack trace logging disabled.
     */  
    public static String MSG_WARNING_STACKTRACE_DISABLED
                        = "MSG_WARNING_STACKTRACE_DISABLED";

    // Resolution Exception Messages starts
    /** 
     * Message key: unresolved reference.
     */  
    public static String MSG_UNRESOLVED_REFERENCE = "MSG_UNRESOLVED_REFERENCE";
    /** 
     * Message key: reference not found.
     */  
    public static String MSG_NOT_FOUND_REFERENCE = "MSG_NOT_FOUND_REFERENCE";
    /** 
     * Message key: reference with no value.
     */  
    public static String MSG_NOT_VALUE_REFERENCE = "MSG_NOT_VALUE_REFERENCE";
    /** 
     * Message key: reference is not a smartfrog component.
     */  
    public static String MSG_NOT_COMPONENT_REFERENCE
                                            = "MSG_NOT_COMPONENT_REFERENCE";
    /** 
     * Message key: illegal reference.
     */  
    public static String MSG_ILLEGAL_REFERENCE = "MSG_ILLEGAL_REFERENCE";
    /** 
     * Message key: illegal classtype.
     */  
    public static String MSG_ILLEGAL_CLASS_TYPE = "MSG_ILLEGAL_CLASS_TYPE";
    // Resolution Exception Messages ends
    /** 
     * Message key: unhandled exception.
     */  
    public static String MSG_UNHANDLED_EXCEPTION = "MSG_UNHANDLED_EXCEPTION";
    /** 
     * Message key: url not found for parsing.
     */  
    public static String MSG_URL_TO_PARSE_NOT_FOUND 
                                               = "MSG_URL_TO_PARSE_NOT_FOUND";





    /** 
     * Message key: illegal numeric parameter to function.
     */  
    public static String ILLEGAL_NUMERIC_PARAMETER 
                                               = "ILLEGAL_NUMERIC_PARAMETER";
    /** 
     * Message key: illegal string parameter to function.
     */  
    public static String ILLEGAL_STRING_PARAMETER 
                                               = "ILLEGAL_STRING_PARAMETER";
    /** 
     * Message key: illegal numeric parameter to function.
     */  
    public static String ILLEGAL_VECTOR_PARAMETER 
                                               = "ILLEGAL_VECTOR_PARAMETER";
    /** 
     * Message key: illegal boolean parameter to function.
     */  
    public static String ILLEGAL_BOOLEAN_PARAMETER 
                                               = "ILLEGAL_BOOLEAN_PARAMETER";
    /** 
     * Message key: too many parameters to function.
     */  
    public static String MISSING_PARAMETER 
                                               = "MISSING_PARAMETER";
}
