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

import org.apache.axis.AxisFault;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;

import javax.xml.namespace.QName;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.Reader;
import java.io.StringReader;

import nu.xom.Document;
import nu.xom.Builder;

/**
 * created Aug 4, 2004 3:59:42 PM
 */

public class Processor {
    private static Log log = LogFactory.getLog(EndpointHelper.class);

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
    public static URI makeURI(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URI.MalformedURIException e) {
            return makeRuntimeException(url, e);
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
            return new URI(Constants.SMARTFROG_SCHEMA, application);
        } catch (URI.MalformedURIException e) {
            return makeRuntimeException(application, e);
        }
    }

    /**
     * turn an application into a valid URI
     *
     * @param application
     * @return a URI that can be used as a reference
     * @throws RuntimeException if the URL was malformed
     */
    public static URI makeURIFromApplication(NCName application) {
        return makeURIFromApplication(application.toString());
    }

    /**
     * turn an application in
     *
     * @param uri
     * @return
     * @throws org.apache.axis.AxisFault if the URI was invalid.
     */
    public static String extractApplicationFromURI(URI uri) throws AxisFault {
        if ( !Constants.SMARTFROG_SCHEMA.equals(uri.getScheme()) ) {
            throw new AxisFault(Constants.ERROR_INVALID_SCHEMA + uri);
        }
        String application = uri.getSchemeSpecificPart();
        return application;
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
public static AxisFault raiseFault(QName code,String message) {
    return raiseFault(code,message,null);
}

    /**
     * construct a fault for throwing
     * @param code qname for the error
     * @param message text message
     * @param thrown optional nested fault
     * @return a fault ready to throw
     */
    public static AxisFault raiseFault(QName code, String message,Throwable thrown) {
        AxisFault fault=new AxisFault();
        fault.setFaultCode(code);
        fault.setFaultReason(message);
        if(thrown!=null) {
            fault.initCause(thrown);
        }
        return fault;
    }

    protected AxisFault raiseUnsupportedLanguageFault(String message) {
        return raiseFault(Constants.FAULT_UNSUPPORTED_LANGUAGE, message);
    }

    protected AxisFault raiseBadArgumentFault(String message) {
        return raiseFault(Constants.FAULT_BAD_ARGUMENT, message);
    }

    protected URL makeURL(URI source) throws MalformedURLException {
        return new URL(source.toString());
    }

    public  AxisFault raiseNestedFault(Exception e, String message) {
        AxisFault fault=AxisFault.makeFault(e);
        fault.setFaultReason(message);
        fault.setFaultCode(Constants.FAULT_NESTED_EXCEPTION);
        return fault;
    }

    /**
     * parse a message fragment and turn it into a Xom document
     * @param element
     * @param message
     * @return
     * @throws org.apache.axis.AxisFault
     */
    protected Document parseMessageFragment(MessageElement element,
                                      final String message) throws AxisFault {
        Document doc;
        try {
            String subdoc=element.getAsString();
            Builder builder = new Builder(false);
            Reader reader = new StringReader(subdoc);
            doc = builder.build(reader);
            return doc;

        } catch (Exception e) {
            throw raiseNestedFault(e,message);

        }
    }
}
