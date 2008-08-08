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
    String MSG_DEPLOY_COMP_TERMINATED
                                            = "MSG_DEPLOY_COMP_TERMINATED";
    /**
     * Message key: receiving deploy request after the component is terminated.
     */
    String MSG_INJECTION_FAILED
                                            = "MSG_INJECTION_FAILED";
    /**
     * Message key: receiving deploy request after the component is terminated.
     */
    String MSG_INJECTION_SETFIELD_FAILED
                                            = "MSG_INJECTION_SETFILED_FAILED";
    /**
     * Message key: receiving deploy request after the component is terminated.
     */
    String MSG_INJECTION_SETMETHOD_FAILED
                                            = "MSG_INJECTION_SETMETHOD_FAILED";
    /**
     * Message key: receiving deploy request after the component is terminated.
     */
    String MSG_INJECTION_VALUE_FAILED
                                            = "MSG_INJECTION_VALUE_FAILED";
    /**
     * Message key: receiving start request after the component is terminated.
     */
    String MSG_START_COMP_TERMINATED
                                            = "MSG_START_COMP_TERMINATED";
    /**
     * Message key: file cannot be found.
     */
    String MSG_FILE_NOT_FOUND = "MSG_FILE_NOT_FOUND";
    /**
     * Message key: Compound has a non-replacable attribute.
     */
    String MSG_NON_REP_ATTRIB = "MSG_NON_REP_ATTRIB";
    /**
     * Message key: Attribute 'name' already present.
     */
    String MSG_REPEATED_ATTRIBUTE = "MSG_REPEATED_ATTRIBUTE";

    /**
     * Message key: An object is null in a method call.
     */
    String MSG_NULL_DEF_METHOD = "MSG_NULL_DEF_METHOD";

        /**
     * Message key: class cannot be found.
     */
    String MSG_CLASS_NOT_FOUND = "MSG_CLASS_NOT_FOUND";
    /**
     * Message key: class cannot be instantiated.
     */
    String MSG_INSTANTIATION_ERROR = "MSG_INSTANTIATION_ERROR";
    /**
     * Message key: class has illegal access on method.
     */
    String MSG_ILLEGAL_ACCESS = "MSG_ILLEGAL_ACCESS";
    /**
     * Message key: class has invocation target error.
     */
    String MSG_INVOCATION_TARGET = "MSG_INVOCATION_TARGET";
    /**
     * Message key: method not found.
     */
    String MSG_METHOD_NOT_FOUND = "MSG_METHOD_NOT_FOUND";
    /**
     * Message key: input stream is null.
     */
    String MSG_INPUTSTREAM_NULL = "MSG_INPUTSTREAM_NULL";
    /**
     * Message key: failed to find parent location.
     */
    String MSG_PARENT_LOCATION_FAILED = "MSG_PARENT_LOCATION_FAILED";
    /**
     * Message key: failed to contact parent.
     */
    String MSG_FAILED_TO_CONTACT_PARENT = "MSG_FAILED_TO_CONTACT_PARENT";
    /**
     * Message key: failure in deploywith phase.
     */
    String MSG_DEPLOYWITH_PHASE_FAILED = "MSG_DEPLOYWITH_PHASE_FAILED";
    /**
     * Message key: failure in object registration.
     */
    String MSG_OBJECT_REGISTRATION_FAILED  = "MSG_OBJECT_REGISTRATION_FAILED";
    /**
     * Message key: failure in starting liveness thread.
     */
    String MSG_LIVENESS_START_FAILED = "MSG_LIVENESS_START_FAILED";
    /**
     * Message key: failure in hook action.
     */
    String MSG_HOOK_ACTION_FAILED = "MSG_HOOK_ACTION_FAILED";
    /**
     * Message key: unable to start a component randomly.
     */
    String MSG_RANDM_ERR = "MSG_RANDM_ERR";
    /**
     * Message key: found invalid object type.
     */
    String MSG_INVALID_OBJECT_TYPE = "MSG_INVALID_OBJECT_TYPE";
    /**
     * Message key: url is null.
     */
    String MSG_NULL_URL = "MSG_NULL_URL";
    /**
     * Message key: Loading url.
     */
    String MSG_LOADING_URL = "MSG_LOADING_URL";

    /**
     * Message key: unable to locate language in url.
     */
    String MSG_LANG_NOT_FOUND = "MSG_LANG_NOT_FOUND";
    /**
     * Message key: unable to locate file or url.
     */
    String MSG_URL_NOT_FOUND = "MSG_URL_NOT_FOUND";
    /**
     * Message key: error in deployment of url .
     */
    String MSG_ERR_DEPLOY_FROM_URL = "MSG_ERR_DEPLOY_FROM_URL";
    /**
     * Message key: stack trace follows.
     */
    String MSG_STACKTRACE_FOLLOWS = "MSG_STACKTRACE_FOLLOWS";
    /**
     * Message key: continuing with other deployment.
     */
    String MSG_CONT_OTHER_DEPLOY = "MSG_CONT_OTHER_DEPLOY";
    /**
     * Message key: error during termination.
     */
    String MSG_ERR_TERM = "MSG_ERR_TERM";
    /**
     * Message key: parser error.
     */
    String MSG_ERR_PARSE = "MSG_ERR_PARSE";
    /**
     * Message key: parser error while resolving phases.
     */
    String MSG_ERR_RESOLVE_PHASE = "MSG_ERR_RESOLVE_PHASE";
    /**
     * Message key: smartfrog ready.
     */
    String MSG_SF_READY = "MSG_SF_READY";

    /**
     * Message key: smartfrog dead.
     */
    String MSG_SF_DEAD = "MSG_SF_DEAD";
    /**
     * Message key: smartfrog daemon terminated.
     */
    String MSG_SF_TERMINATED = "MSG_SF_TERMINATED";
    /**
     * Message key: error in starting smartfrog daemon.
     */
    String MSG_ERR_SF_RUNNING = "MSG_ERR_SF_RUNNING";
    /**
     * Message key: unable to locate host.
     */
    String MSG_UNKNOWN_HOST = "MSG_UNKNOWN_HOST";
    /**
     * Message key: unable to connect to daemon.
     */
    String MSG_CONNECT_ERR = "MSG_CONNECT_ERR";
    /**
     * Message key: unable to connect to daemon.
     */
    String MSG_REMOTE_CONNECT_ERR = "MSG_REMOTE_CONNECT_ERR";
    /**
     * Message key: deployment successful.
     */
    String MSG_DEPLOY_SUCCESS = "MSG_DEPLOY_SUCCESS";
    /**
     * Message key: update successful.
     */
    String MSG_UPDATE_SUCCESS = "MSG_UPDATE_SUCCESS";
    /**
     * Message key: successful termination of components.
     */
    String MSG_TERMINATE_SUCCESS = "MSG_TERMINATE_SUCCESS";

    /**
     * Message key: successful ping of components.
     */
    String MSG_PING_SUCCESS = "MSG_PING_SUCCESS";

    /**
     * Message key: successful dump of components.
     */
    String MSG_DUMP_SUCCESS = "MSG_DUMP_SUCCESS";

    /**
    * Message key: successful detachment of components.
    */
   String MSG_DETACH_SUCCESS = "MSG_DETACH_SUCCESS";
   /**
    * Message key: successful detachment and termination of components.
    */
   String MSG_DETACH_TERMINATE_SUCCESS = "MSG_DETACH_TERMINATE_SUCCESS";


   /**
    * Message key: liveness trace logging enabled
    */
   String MSG_WARNING_LIVENESS_ENABLED = "MSG_WARNING_LIVENESS_ENABLED";

   /**
     * Message key: stack trace logging enabled.
     */
    String MSG_WARNING_STACKTRACE_ENABLED  = "MSG_WARNING_STACKTRACE_ENABLED";
    /**
     * Message key: stack trace logging disabled.
     */
    String MSG_WARNING_STACKTRACE_DISABLED  = "MSG_WARNING_STACKTRACE_DISABLED";

    // Resolution Exception Messages starts
    /**
     * Message key: unresolved reference.
     */
    String MSG_UNRESOLVED_REFERENCE = "MSG_UNRESOLVED_REFERENCE";
    String MSG_UNRESOLVED_REFERENCE_IN = "MSG_UNRESOLVED_REFERENCE_IN";
    /**
     * Message key: reference not found.
     */
    String MSG_NOT_FOUND_REFERENCE = "MSG_NOT_FOUND_REFERENCE";
    /**
     * Message key: attribute not found.
     */
    String MSG_NOT_FOUND_ATTRIBUTE = "MSG_NOT_FOUND_ATTRIBUTE";
    /**
     * Message key: reference with no value.
     */
    String MSG_NOT_VALUE_REFERENCE = "MSG_NOT_VALUE_REFERENCE";
    /**
     * Message key: reference is not a smartfrog component.
     */
    String MSG_NOT_COMPONENT_REFERENCE = "MSG_NOT_COMPONENT_REFERENCE";
    /**
     * Message key: illegal reference.
     */
    String MSG_ILLEGAL_REFERENCE = "MSG_ILLEGAL_REFERENCE";
    /**
     * Message key: illegal classtype.
     * @see #MSG_ILLEGAL_CLASS_TYPE_EXPECTING_GOT
     */
    String MSG_ILLEGAL_CLASS_TYPE = "MSG_ILLEGAL_CLASS_TYPE";

    /**
     * Message key: illegal classtype with added information.
     * @see #MSG_ILLEGAL_CLASS_TYPE
     */
    String MSG_ILLEGAL_CLASS_TYPE_EXPECTING_GOT ="MSG_ILLEGAL_CLASS_TYPE_EXPECTING_GOT";

    /**
     * Message key: illegal classtype with added information.
     *
     * @see #MSG_ILLEGAL_CLASS_TYPE
     */
    String MSG_ILLEGAL_CLASS_TYPE_EXPECTING_PRIM_GOT_CD = "MSG_ILLEGAL_CLASS_TYPE_EXPECTING_PRIM_GOT_CD";

    /**
     * Message key: illegal classtype with added information.
     * @see #MSG_TBD_REFERENCE
     */
    String MSG_TBD_REFERENCE ="MSG_TBD_REFERENCE";

    /**
     * Message key: illegal classtype with added information.
     * @see #MSG_ASSERTION_FAILURE
     */
    String MSG_ASSERTION_FAILURE ="MSG_ASSERTION_FAILURE";

    // Resolution Exception Messages ends
    /**
     * Message key: unhandled exception.
     */
    String MSG_UNHANDLED_EXCEPTION = "MSG_UNHANDLED_EXCEPTION";
    /**
     * Message key: url not found for parsing.
     */
    String MSG_URL_TO_PARSE_NOT_FOUND  = "MSG_URL_TO_PARSE_NOT_FOUND";

    /**
     * Message key: illegal numeric parameter to function.
     */
    String ILLEGAL_NUMERIC_PARAMETER = "ILLEGAL_NUMERIC_PARAMETER";
    /**
     * Message key: illegal string parameter to function.
     */
    String ILLEGAL_STRING_PARAMETER  = "ILLEGAL_STRING_PARAMETER";
    /**
     * Message key: illegal numeric parameter to function.
     */
    String ILLEGAL_VECTOR_PARAMETER  = "ILLEGAL_VECTOR_PARAMETER";
    /**
     * Message key: illegal boolean parameter to function.
     */
    String ILLEGAL_BOOLEAN_PARAMETER  = "ILLEGAL_BOOLEAN_PARAMETER";
    /**
     * Message key: too many parameters to function.
     */
    String MISSING_PARAMETER = "MISSING_PARAMETER";

    /**
     * Message key: copy of data in ComponentDescritpionImpl, etc,  failed
     */
    String COPY_FAILED = "COPY_FAILED";

    /**
     * Message key: copy of data in ComponentDescritpionImpl, etc,  failed
     */
    String COPY_SERIALIZE_FAILED = "COPY_SERIALIZE_FAILED";

    /**
     * Message key: Failed to get InetAddress of the localhost
     */
    String MSG_FAILED_INET_ADDRESS_LOOKUP = "MSG_FAILED_INET_ADDRESS_LOOKUP";

    /**
     * Message key: Component terminated. Used by SmartFrogLivenessException
     */
    String COMPONENT_TERMINATED = "COMPONENT_TERMINATED";

    /**
     * Message key: Liveness send failure message when called is known.
     */
    String LIVENESS_SEND_FAILURE_IN = "LIVENESS_SEND_FAILURE_IN";

    /**
     * Message key: Liveness send failure message when called is unknown.
     */
    String LIVENESS_SEND_FAILURE = "LIVENESS_SEND_FAILURE";

    /**
     * Message key: Warning: security is not enabled.
     */
    String WARN_NO_SECURITY = "WARN_NO_SECURITY";

    /**
     * Message key: Warning: security is not enabled.
     */
    String WARN_SECURE_RESOURCES_OFF = "WARN_SECURE_RESOURCES_OFF";

    /**
     * Message key: Warning: security is not enabled and it is required
     */
    String ERROR_NO_SECURITY_BUT_REQUIRED = "ERROR_NO_SECURITY_BUT_REQUIRED";

    /**
     * * Message Key: A description has been created which has the root component as a function.
     * This is not possible for technical reasons
     */
     String ROOT_MUST_BE_COMPONENT = "ROOT__MUST_BE_COMPONENT_FUNCTION";
    /**
     * * Message Key: A description may not link to a component description that is being used as a predicate.
     *  Predicates must be extended to ensure a copy is taken.
     */
     String CANNOT_LINK_TO_PREDICATE = "CANNOT_LINK_TO_PREDICATE";

   /**
    * Message key: Cannot override an attribute marked sfFinal
    */
   String CANNOT_OVERRIDE_FINAL = "CANNOT_OVERRIDE_FINAL";

    /**
     * Message Key: Cannot add attribute with the same value of an existing child
     */
   String MSG_CANNOT_ADD_VALUE_CHILD = "MSG_CANNOT_ADD_VALUE_CHILD";

	/**
     * Message Key: Cannot use app env because it is different
     */
    String EXISTING_APP_ENV_IS_DIFFERENT = "EXISTING_APP_ENV_IS_DIFFERENT";
}
