/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.os.java;

import org.smartfrog.services.filesystem.FileUsingComponent;

/**
 * created 28-Feb-2006 13:50:34
 */


public interface LoadPropertyFile extends FileUsingComponent {

    /**
     *   //resource, which must be on the classpath of the component
     resource extends OptionalString;
     */

     String ATTR_RESOURCE="resource";
    /**
     required flag; you can set this to false for optional property loads
     required extends Boolean;
     */

    String ATTR_REQUIRED="required";

    /**
     * {@value}
     */
    String ATTR_PROPERTIES="properties";
}
