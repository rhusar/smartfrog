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
package org.smartfrog.services.cddlm.api;

import nu.xom.Builder;
import nu.xom.Document;
import org.apache.axis.AxisFault;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.faults.FaultCodes;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;

import javax.xml.namespace.QName;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

/**
 * created Aug 4, 2004 3:59:42 PM
 */

public class Processor {
    private static final Log log = LogFactory.getLog(EndpointHelper.class);

    public Processor(SmartFrogHostedEndpoint owner) {
        this.owner = owner;
    }

    private SmartFrogHostedEndpoint owner;

    public SmartFrogHostedEndpoint getOwner() {
        return owner;
    }

    public void setOwner(SmartFrogHostedEndpoint owner) {
        this.owner = owner;
    }

    /**
     * make a URI.
     *
     * @param url
     * @return
     * @throws RuntimeException if the URL was malformed
     */
    public static URI makeURI(String url) throws AxisFault {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URI.MalformedURIException e) {
            throw raiseNoSuchApplicationFault(url);
        }
        return uri;
    }

    private static URI makeRuntimeException(String url,
            URI.MalformedURIException e) {
        log.error("url", e);
        throw new RuntimeException(url, e);
    }

    /**
     * indicate that something is not implemented by throwing a fault
     *
     * @throws org.apache.axis.AxisFault with an error message
     */
    public static void throwNotImplemented() throws AxisFault {
        throw new AxisFault("This feature is not yet implemented");
    }

    /**
     * turn an application into a valid URI
     *
     * @param application
     * @return a URI that can be used as a reference
     * @throws RuntimeException if the URL was malformed
     */
    public static URI makeURIFromApplication(String application) {
        try {
            assert application != null;
            return new URI("http", "localhost/" + application);
        } catch (URI.MalformedURIException e) {
            return makeRuntimeException(application, e);
        }
    }


    /**
     * look up a job in the repository
     *
     * @param jobURI
     * @return the jobstate reference
     * @throws AxisFault if there is no such job
     */
    public JobState lookupJob(URI jobURI) throws AxisFault {
        JobState jobState = lookupJobNonFaulting(jobURI);
        if (jobState == null) {
            throw raiseNoSuchApplicationFault(jobURI.toString());
        }
        return jobState;
    }

    /**
     * map from URI to job
     *
     * @param jobURI seach uri
     * @return job or null for no match
     */
    public JobState lookupJobNonFaulting(URI jobURI) {
        JobRepository jobs = ServerInstance.currentInstance().getJobs();
        JobState jobState = jobs.lookup(jobURI);
        return jobState;
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


    /**
     * construct a fault for throwing
     *
     * @param code    qname for the error
     * @param message text message
     * @return a fault ready to throw
     */
    public static AxisFault raiseFault(QName code, String message) {
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
    public static AxisFault raiseFault(QName code, String message,
            Throwable thrown) {
        AxisFault fault = new AxisFault();
        fault.setFaultCode(code);
        fault.setFaultReason(message);
        if (thrown != null) {
            fault.initCause(thrown);
        }
        return fault;
    }

    protected static AxisFault raiseUnsupportedLanguageFault(String message) {
        return raiseFault(FaultCodes.FAULT_UNSUPPORTED_LANGUAGE, message);
    }

    protected static AxisFault raiseUnsupportedCallbackFault(String message) {
        return raiseFault(FaultCodes.FAULT_UNSUPPORTED_CALLBACK, message);
    }

    protected static AxisFault raiseBadArgumentFault(String message) {
        return raiseFault(FaultCodes.FAULT_BAD_ARGUMENT, message);
    }

    protected static AxisFault raiseNoSuchApplicationFault(String message) {
        return raiseFault(FaultCodes.FAULT_NO_SUCH_APPLICATION, message);
    }

    protected URL makeURL(URI source) throws MalformedURLException {
        return new URL(source.toString());
    }

    public AxisFault raiseNestedFault(Exception e, String message) {
        AxisFault fault = AxisFault.makeFault(e);
        fault.setFaultReason(message);
        fault.setFaultCode(FaultCodes.FAULT_NESTED_EXCEPTION);
        return fault;
    }

    /**
     * parse a message fragment and turn it into a Xom document
     *
     * @param element
     * @param message
     * @return
     * @throws org.apache.axis.AxisFault
     */
    protected Document parseMessageFragment(MessageElement element,
            final String message)
            throws AxisFault {
        Document doc;
        try {
            String subdoc = element.getAsString();
            Builder builder = new Builder(false);
            Reader reader = new StringReader(subdoc);
            doc = builder.build(reader);
            return doc;

        } catch (Exception e) {
            throw raiseNestedFault(e, message);

        }
    }

    /**
     * turn a smartfrog exception into an axis fault
     *
     * @param exception
     * @return
     */
    public AxisFault translateSmartFrogException(SmartFrogException exception) {
        AxisFault fault = AxisFault.makeFault(exception);
        QName faultCode = FaultCodes.FAULT_NESTED_EXCEPTION;
        //compilation and subclasses
        if (exception instanceof SmartFrogCompilationException) {
            faultCode = FaultCodes.FAULT_DESCRIPTOR_PARSE_ERROR;
            SmartFrogCompilationException ex = (SmartFrogCompilationException) exception;
        }
        if (exception instanceof SmartFrogParseException) {
            faultCode = FaultCodes.FAULT_DESCRIPTOR_PARSE_ERROR;
        }
        if (exception instanceof SmartFrogCompileResolutionException) {
            faultCode = FaultCodes.FAULT_COMPILE_RESOLUTION_FAILURE;
        }
        //init failure
        if (exception instanceof SmartFrogInitException) {
            faultCode = FaultCodes.FAULT_INITIALIZATION_FAILURE;
        }

        //runtime faults
        if (exception instanceof SmartFrogRuntimeException) {
            faultCode = FaultCodes.FAULT_RUNTIME_EXCEPTION;
        }
        if (exception instanceof SmartFrogResolutionException) {
            faultCode = FaultCodes.FAULT_RESOLUTION_FAILURE;
        }
        if (exception instanceof SmartFrogLivenessException) {
            faultCode = FaultCodes.FAULT_LIVENESS_EXCEPTION;
        }
        if (exception instanceof SmartFrogDeploymentException) {
            faultCode = FaultCodes.FAULT_DEPLOYMENT_FAILURE;
        }
        //TODO: add the other runtime faults

        //copy all context keys
        Context context = exception.getContext();
        Enumeration keys = context.keys();
        while (keys.hasMoreElements()) {
            Object key = (Object) keys.nextElement();
            Object value = context.get(key);
            String stringVal = value.toString();
            //TODO: escape local parts that are not valid.
            final String localPart = key.toString();
            fault.addFaultDetail(
                    new QName(FaultCodes.SMARTFROG_NAMESPACE, localPart)
                    , value.toString());
        }

        fault.setFaultCode(faultCode);
        return fault;
    }
}
