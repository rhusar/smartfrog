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

    /**
     * Array containing message keys and message.
     */
    static final Object[][] contents = {
        { MSG_DEPLOY_COMP_TERMINATED,
            "Component :{0} received deploy request after it is terminated" },
        { MSG_START_COMP_TERMINATED,
            "Component : {0} received start request after it is terminated" },
        { MSG_FILE_NOT_FOUND, "Cannot find file {0}" },
        { MSG_NON_REP_ATTRIB, "Compound has a non-replacable attribute {0}" },
        { MSG_CLASS_NOT_FOUND, "Class {0} could not be found" },
        { MSG_INSTANTIATION_ERROR, "Class {0} cannot be instantiated" },
        { MSG_ILLEGAL_ACCESS, "Class {0} had illegal access on method {1}" },
        { MSG_METHOD_NOT_FOUND, "Class {0} has no method as {1}" },
        { MSG_INVOCATION_TARGET, "Class {0} has invocation target error" },
        { MSG_INPUTSTREAM_NULL, "input stream is null" },
        { MSG_RANDM_ERR, "Unable to start a component randomly" },
        { MSG_NULL_URL, "Url is null" },
        { MSG_LANG_NOT_FOUND, "Unable to locate language in URL : {0}" },
        { MSG_URL_NOT_FOUND, "Unable to locate File or URL: {0} for component : {1}"+
            " \nReason: The path to URL may be incorrect or file may be missing"},
        { MSG_ERR_DEPLOY_FROM_URL,
          "Error during deployment of URL:{0}, for component: {1}"},
        { MSG_STACKTRACE_FOLLOWS, "Stack trace follows:"},
        { MSG_CONT_OTHER_DEPLOY, "Continuing with other deployments" },
        { MSG_ERR_TERM, "Error during termination of: {0}" },
        { MSG_ERR_PARSE, "Parser error"},
        { MSG_ERR_RESOLVE_PHASE, "Parser error while resolving phases"},
        { MSG_SF_READY, "SmartFrog ready... {0}"},
        { MSG_SF_DEAD, "SmartFrog [{0}] dead "},
        { MSG_SF_TERMINATED, "SmartFrog daemon terminated"},
        { MSG_ERR_SF_RUNNING, "SmartFrog daemon could not start because another instance is running"},
        { MSG_WARNING_STACKTRACE_ENABLED ,
                             "Warning: stack trace logging enabled"},
        { MSG_WARNING_STACKTRACE_DISABLED ,
                             "Warning: stack trace logging disabled"},
        { MSG_UNKNOWN_HOST, "Unable to locate IP address of the host: {0}"},
        { MSG_CONNECT_ERR,
        "Unable to connect to sfDaemon on: {0}.\nReason:sfDaemon may not be running on {0}"},
        { MSG_REMOTE_CONNECT_ERR,
        "Unable to connect to sfDaemon on: {0}.\nReason:sfDaemon may not be authenticated properly"},
        // Resolution Exception messages start
        { MSG_UNRESOLVED_REFERENCE , "Unresolved Reference"},
        { MSG_NOT_FOUND_REFERENCE , "Reference not found"},
        { MSG_NOT_VALUE_REFERENCE , "Reference with no value"},
        { MSG_NOT_COMPONENT_REFERENCE , "Reference is not a SmartFrog Component"},
        { MSG_ILLEGAL_REFERENCE , "Illegal Reference"},
        { MSG_ILLEGAL_CLASS_TYPE , "Illegal ClassType"},
        // Resolution Exception messages end
        { MSG_DEPLOY_SUCCESS , "Successfully deployed components: {0}"},
        { MSG_TERMINATE_SUCCESS , "Successfully terminated components: {0}"},
        { MSG_UNHANDLED_EXCEPTION , "Unhandled exception: "},
        { MSG_URL_TO_PARSE_NOT_FOUND , "Unable to locate URL \"{0}\"\nReason: URL may be incorrect or file is misssing"},

        { ILLEGAL_NUMERIC_PARAMETER , "Illegal numeric parameter"},
        { ILLEGAL_STRING_PARAMETER  , "Illegal string parameter"},
        { ILLEGAL_VECTOR_PARAMETER  , "Illegal vector parameter"},
        { ILLEGAL_BOOLEAN_PARAMETER  , "Illegal boolean parameter"},
        { MISSING_PARAMETER  , "Missing Parameter {0}"},
    };
    
    /**
     * Gets the message array.
     * @return The message array.
     */
    public Object[][] getContents() {
        return contents;
    }
}
