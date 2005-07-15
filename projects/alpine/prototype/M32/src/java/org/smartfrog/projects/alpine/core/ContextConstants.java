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
 
 */
public interface ContextConstants {
    
    /**
     * name of class that is used to handle things
     * {@value}
     */ 
    public static final String ATTR_HANDLER_CLASS="handlerClass";
    
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
    public static final String ATTR_CONTENT_TYPE = "getContentType";
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
}
