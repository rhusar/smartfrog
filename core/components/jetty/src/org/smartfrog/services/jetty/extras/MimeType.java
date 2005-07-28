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
package org.smartfrog.services.jetty.extras;

import java.rmi.Remote;

/**
 * created 28-Jul-2005 16:27:35
 */


public interface MimeType extends Remote {


    /**
     * Extension to define a mime type for. {@value}
     */
    public static final String ATTR_EXTENSION = "extension";

    /**
     * mime type for. {@value}
     */
    public static final String ATTR_TYPE = "type";

    /**
     * context to declare for {@value}
     */
    public static final String ATTR_CONTEXT = "context";
}
