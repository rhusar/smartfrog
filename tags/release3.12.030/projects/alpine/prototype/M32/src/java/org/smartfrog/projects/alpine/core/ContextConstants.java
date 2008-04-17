/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.projects.alpine.core;

/**
 * Constants. These are here to decouple the toolkit from Smartfrog
 */
public interface ContextConstants {


    /**
     * name of list of handlers
     * {@value}
     */
    public static final String ATTR_HANDLERS = "handlers";


    /**
     * path for the endpoint
     * {@value}
     */
    public static final String ATTR_PATH = "path";

    /**
     * name of the endpoint
     * {@value}
     */
    public static final String ATTR_NAME = "name";


    /**
     * Text for a message {@value}
     */
    public static final String ATTR_GET_CONTENT_TYPE = "getContentType";
    /**
     * HTML content for a get {@value}
     */
    public static final String ATTR_GET_MESSAGE = "getMessage";

    /**
     * integer response code for a get {@value}
     */
    public static final String ATTR_GET_RESPONSECODE = "getResponseCode";

    /**
     * resource of the WSDL. for ?WSDL operations
     * {@value}
     */
    public static final String ATTR_WSDL = "wsdl";

    /**
     * override of factory
     * {@value}
     */
    public static final String ATTR_FACTORY = "factory";

    /**
     * URL used to serve from
     * {@value}
     */
    public static final String ATTR_URL = "url";

    /**
     * Actor/role value of endpoint
     * {@value}
     */
    public static final String ATTR_ROLE = "role";

    /**
     * SOAP Content type. {@value}
     */
    public static final String ATTR_SOAP_CONTENT_TYPE = "soapContentType";

    /**
     * owner endpoint; used in binding message contexts to endpoint contexts
     */
    public static final String ATTR_OWNER_ENDPOINT = "_ownerEndpoint";

    /**
     * Classname of a Xom factory. This is used when parsing messages,
     * and lets you hand off parsing of your custom classes to a custom Xom factory
     */
    public static final String ATTR_XOM_FACTORY = "factory";

    /**
     * Used in the message context to log the sender ip address
     */
    public static final String REQUEST_REMOTE_ADDRESS = "request.remote.address";
}
