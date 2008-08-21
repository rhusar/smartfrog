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

import java.util.ListResourceBundle;


/**
 * Resource bundle for all the exception messages. Guideline: All messages keys
 * should be added in MessageKeys interface and detailed message should be
 * defined here.
 *
 */
public class ErrorMessageBundle extends ListResourceBundle
    implements MessageKeys {

    private static final String LINE_END = System.getProperty("line.separator");

    /**
     * Array containing message keys and message.
     */
    static final Object[][] contents = {
        { MSG_DEPLOY_COMP_TERMINATED,
            "Component: {0} received deploy request after it is terminated" },
        { MSG_INJECTION_FAILED,
            "Component: {0} failed to find set method or field for attribute {1} during injection" },
        { MSG_INJECTION_SETFIELD_FAILED,
            "Component: {0} failed to assign value {2} to attribute {1} during injection" },
        { MSG_INJECTION_SETMETHOD_FAILED,
             "Component: {0} error in set method for attribute {1} with value {2} during injection" },
        { MSG_INJECTION_VALUE_FAILED,
            "Component: {0} resolve value for attribute {1} during injection" },
        { MSG_START_COMP_TERMINATED,
            "Component: {0} received start request after it is terminated" },
        { MSG_FILE_NOT_FOUND, "Cannot find file {0}" },
        { MSG_NON_REP_ATTRIB, "Compound has a non-replaceable attribute {0}" },
        { MSG_NULL_DEF_METHOD, "{0} is null during {1}"},
        { MSG_CLASS_NOT_FOUND, "Class {0} could not be found" },
        { MSG_INSTANTIATION_ERROR, "Class {0} cannot be instantiated" },
        { MSG_ILLEGAL_ACCESS, "Class {0} had illegal access on method {1}" },
        { MSG_METHOD_NOT_FOUND, "Class {0} has no method as {1}" },
        { MSG_INVOCATION_TARGET, "Class {0} has invocation target error" },
        { MSG_INPUTSTREAM_NULL, "input stream is null" },
        { MSG_RANDM_ERR, "Unable to start a component randomly" },
        { MSG_NULL_URL, "Url is null" },
        { MSG_LOADING_URL, "Trying to load {0}" },
        { MSG_LANG_NOT_FOUND, "Unable to locate language in URL: {0}" },
        { MSG_URL_NOT_FOUND, "Unable to locate File or URL: {0} for component: {1} "+ LINE_END + "    Reason: The path to URL may be incorrect or file may be missing"},
        { MSG_RESOURCE_NOT_FOUND_IN_CLASS_ENV, "Unable to locate resource {0} in environment {1}, env content: {2} "},   
        { MSG_ERR_DEPLOY_FROM_URL,
          "Error during deployment of URL: {0}, for component: {1}"},
        { MSG_STACKTRACE_FOLLOWS, "Stack trace follows:"},
        { MSG_CONT_OTHER_DEPLOY, "Continuing with other deployments" },
        { MSG_ERR_TERM, "Error during termination of: {0}" },
        { MSG_ERR_PARSE, "Parser error"},
        { MSG_ERR_RESOLVE_PHASE, "Parser error while resolving phases"},
        { MSG_SF_READY, "SmartFrog ready... {0}"},
        { MSG_SF_DEAD, "SmartFrog [{0}] dead "},
        { MSG_SF_TERMINATED, "SmartFrog daemon terminated"},
        { MSG_ERR_SF_RUNNING, "SmartFrog daemon could not start because another instance is running"},
        { MSG_WARNING_LIVENESS_ENABLED ,
                             "Warning: Liveness trace logging enabled"},
        { MSG_WARNING_STACKTRACE_ENABLED ,
                             "Warning: stack trace logging enabled"},
        { MSG_WARNING_STACKTRACE_DISABLED ,
                             "Warning: stack trace logging disabled"},
        { MSG_UNKNOWN_HOST, "Unable to locate IP address of the host: {0}"},
        { MSG_CONNECT_ERR,
        "Unable to connect to sfDaemon on: {0}.\nReason: sfDaemon may not be running on {0}"},
        { MSG_REMOTE_CONNECT_ERR,
        "Unable to connect to sfDaemon on: {0}.\nReason: sfDaemon may not be authenticated properly"},
        { MSG_PARENT_LOCATION_FAILED,
         "Failed to locate parent"},
        // Resolution Exception messages start
        { MSG_UNRESOLVED_REFERENCE , "Unresolved Reference"},
        { MSG_UNRESOLVED_REFERENCE_IN , "Failed to resolve {0} in {1}"},
        { MSG_NOT_FOUND_REFERENCE , "Reference not found"},
        { MSG_NOT_FOUND_ATTRIBUTE , "Attribute {0} not found"},
        { MSG_REPEATED_ATTRIBUTE , "Attribute {0} already present"},
        { MSG_NOT_VALUE_REFERENCE , "Reference with no value"},
        { MSG_NOT_COMPONENT_REFERENCE , "Reference is referencing through an object which is not a SmartFrog Component"},
        { MSG_ILLEGAL_REFERENCE , "Illegal Reference"},
        { MSG_ILLEGAL_CLASS_TYPE , "Illegal ClassType"},
        { MSG_ILLEGAL_CLASS_TYPE_EXPECTING_GOT , "Illegal ClassType. Expecting type \"[{0}]\" and got \"{1} [{2}]\""},
        { MSG_ILLEGAL_CLASS_TYPE_EXPECTING_PRIM_GOT_CD, "Expecting a Prim but got a reference to an undeployed component \"{1} [{2}]\""},
        { MSG_TBD_REFERENCE , "Failure in evaluating assertion - TBD found still undefined" },
        { MSG_ASSERTION_FAILURE , "Failure in evaluating assertion - assertion not evaluate to true" },
        // Resolution Exception messages end
        { MSG_DEPLOY_SUCCESS , "Successfully deployed: {0}"},
        { MSG_UPDATE_SUCCESS , "Successfully updated: {0}"},            
        { MSG_TERMINATE_SUCCESS , "Successfully terminated: {0}"},
        { MSG_PING_SUCCESS , "\"{0}\" was successfully contacted in \"{1}\". {2}"},
        { MSG_DUMP_SUCCESS , "\"{0}\" was successfully contacted and configuratin read in \"{1}\". {2}"},
        { MSG_DETACH_SUCCESS , "Successfully detached component: {0}"},
        { MSG_DETACH_TERMINATE_SUCCESS , "Successfully detatched and terminated: {0}"},
        { MSG_UNHANDLED_EXCEPTION , "Unhandled exception: "},
        { MSG_URL_TO_PARSE_NOT_FOUND , "Unable to locate URL \"{0}\""+ LINE_END +"    Reason: URL may be incorrect or resource is missing"},

        { ILLEGAL_NUMERIC_PARAMETER , "Illegal numeric parameter"},
        { ILLEGAL_STRING_PARAMETER  , "Illegal string parameter"},
        { ILLEGAL_VECTOR_PARAMETER  , "Illegal vector parameter"},
        { ILLEGAL_BOOLEAN_PARAMETER  , "Illegal boolean parameter"},
        { MISSING_PARAMETER  , "Missing Parameter {0}"},
        { COPY_SERIALIZE_FAILED , "Attempt to copy data failed, exception during serialize/deserialize {0}"},
        { COPY_FAILED , "Attempt to copy data failed, data not serializable {0}"},
        { MSG_OBJECT_REGISTRATION_FAILED, "Failed to register/export the object"},
        { MSG_FAILED_INET_ADDRESS_LOOKUP, "Failed to get Inet address of the localhost"},
        { LIVENESS_SEND_FAILURE_IN , "Liveness Send Failure in {0} when calling {1}"},
        { LIVENESS_SEND_FAILURE , "Liveness Send Failure when calling {1}"},
        { COMPONENT_TERMINATED, "Component Terminated"},
        { WARN_NO_SECURITY, "SmartFrog security is NOT active"},
        { WARN_SECURE_RESOURCES_OFF, "SmartFrog security is active BUT resources can be loaded without restrictions. This should be a secure node"},    
        { ERROR_NO_SECURITY_BUT_REQUIRED, "Smartfrog Security was not active, but was marked as required"},
        { ROOT_MUST_BE_COMPONENT, "The sfConfig attribute of a SmartFrog description must be a Component Description, found a {0}"},
        { CANNOT_LINK_TO_PREDICATE, "Attribute {1} is a link to a predicate. Predicates should be extended and not linked to: in component {0}"},
        { CANNOT_OVERRIDE_FINAL, "Attribute {1} is tagged as sfFinal, it may not be overridden: in component {0}"},
        { MSG_CANNOT_ADD_VALUE_CHILD, "\"{0}\" cannot be added because its value \"{1}\" is already a child of \"{2}\""},
		{ EXISTING_APP_ENV_IS_DIFFERENT, "The process: {0} already has an application environment of name: {1}, but it is different than the designed environment for the application."
                + LINE_END + "Check whether the application environment has been changed, but not undeployed on this process."}
    };

    /**
     * Gets the message array.
     * @return The message array.
     */
    public Object[][] getContents() {
        return contents;
    }
}
