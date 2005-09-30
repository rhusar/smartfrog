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
package org.smartfrog.services.deployapi.transport.endpoints;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;

import java.net.URI;
import java.rmi.RemoteException;
import java.io.IOException;

/**
 * created Aug 4, 2004 3:59:42 PM
 */

public class Processor extends FaultRaiser {
    private static final Log log = LogFactory.getLog(Processor.class);

    public Processor(XmlBeansEndpoint owner) {
        this.owner = owner;
    }

    /**
     * our owner
     */
    private XmlBeansEndpoint owner;

    private MessageContext messageContext;

    public XmlBeansEndpoint getOwner() {
        return owner;
    }


    public MessageContext getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    private static URI makeRuntimeException(String url,
                                            Exception e) {
        log.error("url", e);
        throw new RuntimeException(url, e);
    }

    /**
     * turn an application into a valid URI
     *
     * @param application
     * @return a URI that can be used as a reference
     * @throws RuntimeException if the URL was malformed
     */
/*    public static URI makeURIFromApplication(String application) {
        try {
            assert application != null;
            return new URI("http://localhost/" + application);
        } catch (URISyntaxException e) {
            return makeRuntimeException(application, e);
        }
    }*/


    /**
     * look up a job in the repository
     *
     * @param jobURI
     * @return the jobstate reference
     * @throws BaseException if there is no such job
     */
    public Job lookupJob(URI jobURI) throws BaseException {
        Job job = lookupJobNonFaulting(jobURI);
        if (job == null) {
            throw raiseNoSuchApplicationFault(
                    ERROR_APP_URI_NOT_FOUND + jobURI.toString());
        }
        return job;
    }

    /**
     * map from URI to job
     *
     * @param jobURI seach uri
     * @return job or null for no match
     */

    public Job lookupJobNonFaulting(URI jobURI) {
        JobRepository jobs = ServerInstance.currentInstance().getJobs();
        Job job = jobs.lookup(jobURI);
        return job;
    }

    /**
     * parse a message fragment and turn it into a Xom document
     *
     * @param element
     * @param message
     * @return
     */
/*    protected Document parseMessageFragment(MessageElement element,
                                            final String message)
            throws BaseFault {
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
    }*/

    /**
     * go from URI to language enum
     *
     * @param uri of language
     * @return
     */
/*
    public static int determineLanguage(String uri) {
        int l = Constants.LANGUAGE_UNKNOWN;
        for (int i = 0; i < Constants.LANGUAGE_NAMESPACES.length; i++) {
            if (Constants.LANGUAGE_NAMESPACES[i].equals(uri)) {
                l = i;
                break;
            }
        }
        return l;
    }
*/
    protected void maybeValidate(XmlObject bean) {
        Utils.maybeValidate(bean);
    }

    public OMElement process(OMElement request) throws IOException {
        throwNotImplemented();
        return null;
    }
}
