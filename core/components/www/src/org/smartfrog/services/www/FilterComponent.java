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
package org.smartfrog.services.www;


/**
 * Interface defining a filter
 */


public interface FilterComponent extends ServletContextComponent {
    /**
     * {@value}
     */
    String ATTR_NAME = ServletComponent.ATTR_NAME;

    /**
     * {@value}
     */
    String ATTR_CLASSNAME = ServletComponent.ATTR_CLASSNAME;
    
    String ATTR_PATTERN = "pattern";

    /**
     * {@value }
     */
    String ATTR_DISPATCH_REQUEST = "dispatchRequest";
    /**
     * {@value }
     */
    String ATTR_DISPATCH_FORWARD = "dispatchForward";
    /**
     * {@value }
     */
    String ATTR_DISPATCH_INCLUDE = "dispatchInclude";
    /**
     * {@value }
     */
    String ATTR_DISPATCH_ERROR = "dispatchError";
    

}