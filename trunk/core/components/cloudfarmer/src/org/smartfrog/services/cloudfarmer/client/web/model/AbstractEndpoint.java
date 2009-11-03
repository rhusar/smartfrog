/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cloudfarmer.client.web.model;


import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class for all RESTy endpoints
 */
public abstract class AbstractEndpoint implements Serializable, Constants {

    private String baseURL;
    /**
     * Timeout in milliseconds
     */
    private int timeout;

    protected static final Log log = LogFactory.getLog(AbstractEndpoint.class);

    /**
     * Construct an instance bound to the url
     * @param baseURL URL to target
     */
    protected AbstractEndpoint(String baseURL) {
        setBaseURL(baseURL);
    }

    protected AbstractEndpoint() {
    }


    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the baseURL
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Set the base URL, for subclasses only
     * @param baseURL new baseURL
     */
    protected void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }


    /**
     * String operator returns the endpoint
     * @return
     */
    @Override
    public String toString() {
        return baseURL != null ? baseURL : "unbound endpoint";
    }

    /**
     * Create a new URL that represents the target
     *
     * @return the target URL
     * @throws MalformedURLException if not
     */
    protected URL getTargetURL() throws MalformedURLException {
        URL url = new URL(baseURL);
        return url;
    }
}
