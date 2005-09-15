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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import java.net.URI;

/**
 * created Aug 4, 2004 3:59:42 PM
 */

public class Processor  {
    private static final Log log = LogFactory.getLog(Processor.class);

    public Processor(XmlBeansEndpoint owner) {
        this.owner = owner;
    }

    /**
     * our owner
     */
    private XmlBeansEndpoint owner;


    public XmlBeansEndpoint getOwner() {
        return owner;
    }

    public void setOwner(XmlBeansEndpoint owner) {
        this.owner = owner;
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
/*    public JobState lookupJob(URI jobURI) throws BaseFault {
        JobState jobState = lookupJobNonFaulting(jobURI);
        if (jobState == null) {
            throw raiseNoSuchApplicationFault(
                    ERROR_APP_URI_NOT_FOUND + jobURI.toString());
        }
        return jobState;
    }*/

    /**
     * map from URI to job
     *
     * @param jobURI seach uri
     * @return job or null for no match
     */
/*
    public JobState lookupJobNonFaulting(URI jobURI) {
        JobRepository jobs = ServerInstance.currentInstance().getJobs();
        JobState jobState = jobs.lookup(jobURI);
        return jobState;
    }
*/


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


}
