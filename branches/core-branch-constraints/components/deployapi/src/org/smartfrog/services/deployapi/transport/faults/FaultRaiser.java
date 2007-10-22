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
package org.smartfrog.services.deployapi.transport.faults;

import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

/**
 * created Oct 26, 2004 3:25:29 PM
 */

public class FaultRaiser {
    public static final String ERROR_WRONG_MESSAGE_ELEMENT_COUNT = "wrong number of message elements";
    public static final String ERROR_NO_APPLICATION = "No application URI";
    public static final String ERROR_APP_URI_NOT_FOUND = "Not found: ";
    public static final String ERROR_NO_LANGUAGE_DECLARED = "No language was declared";

    /**
     * indicate that something is not implemented by throwing a fault
     *
     * @throws BaseException with an error message
     */
    public static void throwNotImplemented() throws BaseException {
        throw new BaseException("This feature is not yet implemented");
    }

    /**
     * construct a fault for throwing
     *
     * @param code    qname for the error
     * @param message text message
     * @return a fault ready to throw
     */
    public static BaseException raiseFault(QName code, String message) {
        return raiseFault(code, message, null);
    }


    /**
     * construct a fault for throwing
     *
     * @param code    qname for the error
     * @param message text message
     * @param thrown  optional nested fault
     * @return a fault ready to throw
     */
    public static BaseException raiseFault(QName code, String message,
                                           Throwable thrown) {
        BaseException fault = new DeploymentException(message);
        fault.setFaultCode(code);
        fault.setFaultReason(message);
        if (thrown != null) {
            fault.initCause(thrown);
        }
        return fault;
    }

    public static BaseException raiseUnsupportedLanguageFault(String message) {
        return raiseFault(Constants.FAULT_UNSUPPORTED_LANGUAGE,
                message);
    }

    public static BaseException raiseUnsupportedCallbackFault(String message) {
        return raiseFault(Constants.FAULT_UNSUPPORTED_CALLBACK,
                message);
    }

    public static BaseException raiseBadArgumentFault(String message) {
        BaseException baseException = raiseFault(Constants.FAULT_BAD_ARGUMENT, message);
        
        return baseException;
    }

    public static BaseException raiseNoSuchApplicationFault(String message) {
        return raiseFault(Constants.FAULT_NO_SUCH_APPLICATION,
                message);
    }

    public static BaseException raiseNestedFault(Exception e, String message) {
        BaseException fault = BaseException.makeFault(e);
        fault.setFaultReason(message);
        fault.setFaultCode(Constants.FAULT_NESTED_EXCEPTION);
        return fault;
    }
    
    
    public static BaseException raiseNotImplementedFault(String feature) {
        String message = "not implemented:" + feature;
        return raiseInternalError(message);
    }
    
    public static BaseException raiseInternalError(String message) {
        DeploymentException fault=new DeploymentException(message);
        fault.setFaultCode(Constants.QNAME_SMARTFROG_INTERNAL_FAULT);
        fault.setFaultReason(message);
        return fault;
    }

    public static BaseException raiseInternalError(String message,Throwable t) {
        DeploymentException fault = new DeploymentException(message,t);
        fault.setFaultCode(Constants.QNAME_SMARTFROG_INTERNAL_FAULT);
        fault.setFaultReason(message);
        return fault;
    }


    /**
     * make a URI.
     *
     * @param url
     * @return
     * @throws RuntimeException if the URL was malformed
     */
    public static URI makeURI(String url) throws BaseException {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw raiseNoSuchApplicationFault(url);
        }
        return uri;
    }

    /**
     * test for a parameter being null or zero length
     *
     * @param param string to test
     * @return true iff it is empty
     */
    public static boolean isEmpty(String param) {
        return param == null || param.length() == 0;
    }

    protected URL makeURL(URI source) throws MalformedURLException {
        return new URL(source.toString());
    }

    protected URL makeURL(String source) throws MalformedURLException {
        return new URL(source);
    }

    /**
     * turn any fault into an axis fault. Smartfrog exceptions and CDL parse
     * exceptions are picked up specially
     *
     * @param e
     * @return
     */
    public static BaseException translateException(Exception e) {
        if (e instanceof BaseException) {
            return (BaseException) e;
        }
        if (e instanceof SmartFrogException) {
            return translateSmartFrogException((SmartFrogException) e);
        }
        /*
        if (e instanceof ParsingException) {
            return translateCdlFault((ParsingException) e);
        }
        */
        return BaseException.makeFault(e);
    }

    /**
     * extract line and column info, if provided
     *
     * @param exception
     * @return
     */
    /*
    public BaseException translateCdlFault(ParsingException exception) {
        BaseException fault = BaseException.makeFault(exception);
        QualifiedName faultCode;
        faultCode = Constants.FAULT_DESCRIPTOR_PARSE_ERROR;
        fault.setFaultCode(faultCode);
        if (exception.getLineNumber() > -1) {
            fault.addFaultDetail(new QName(
                    Constants.XML_CDL_NAMESPACE,
                    "line"),
                    Integer.toString(exception.getLineNumber()));
        }
        if (exception.getColumnNumber() > -1) {
            fault.addFaultDetail(new QName(
                    Constants.XML_CDL_NAMESPACE,
                    "column"),
                    Integer.toString(exception.getColumnNumber()));
        }
        return fault;
    }
*/

    /**
     * turn a smartfrog exception into an axis fault
     *
     * @param exception
     * @return
     */
    public static BaseException translateSmartFrogException(SmartFrogException exception) {
        BaseException fault = BaseException.makeFault(exception);
        QName faultCode = Constants.FAULT_NESTED_EXCEPTION;
        //compilation and subclasses
        if (exception instanceof SmartFrogCompilationException) {
            faultCode = Constants.FAULT_DESCRIPTOR_PARSE_ERROR;
            SmartFrogCompilationException ex = (SmartFrogCompilationException) exception;
        }
        if (exception instanceof SmartFrogParseException) {
            faultCode = Constants.FAULT_DESCRIPTOR_PARSE_ERROR;
        }
        //init failure
        if (exception instanceof SmartFrogInitException) {
            faultCode = Constants.FAULT_INITIALIZATION_FAILURE;
        }

        //runtime faults
        if (exception instanceof SmartFrogRuntimeException) {
            faultCode = Constants.FAULT_RUNTIME_EXCEPTION;
        }
        if (exception instanceof SmartFrogResolutionException) {
            faultCode = Constants.FAULT_RESOLUTION_FAILURE;
        }
        if (exception instanceof SmartFrogLivenessException) {
            faultCode = Constants.FAULT_LIVENESS_EXCEPTION;
        }
        if (exception instanceof SmartFrogDeploymentException) {
            faultCode = Constants.FAULT_DEPLOYMENT_FAILURE;
        }
        //TODO: add the other runtime faults

        //copy all context keys
        Context context = exception.getContext();
        if(context!=null) {
            Enumeration keys = context.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = context.get(key);
                String stringVal = value.toString();
                //TODO: escape local parts that are not valid.
                final String localPart = key.toString();
                fault.addFaultDetail(new QName(
                        Constants.SMARTFROG_NAMESPACE,
                        localPart)
                        , value.toString());
            }
        }
        fault.setFaultCode(faultCode);
        return fault;
    }

    /**
     * check that a test is valid, throw an exception with the error 
     * text if not
     * @param fact
     * @param errorText
     * @throws BaseException if not
     */
    public static void checkArg(boolean fact,String errorText) {
        if(!fact) {
            raiseBadArgumentFault(errorText);
        }
    }
}
