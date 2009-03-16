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
package org.smartfrog.tools.ant;

/**
 *  This is something steve wrote which lived in the ant sandbox, I've pulled it out, fixed it and added to the SF
 * codebase, retaining the apache license.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Task to get the hostname of a box; as IPaddr or hostname.
 *
 * The hostname values are cached for performance, as on a misbehaving box, time to
 * execute can be 30s or more
 *
 * @ant.task category="SmartFrog" name="sf-localhost"
 */

public class LocalHost extends Task {


    private static final String LOCALHOST = "localhost";

    /**
     * Property to set
     */
    private String property;


    /**
     * Description of the Field
     */
    private boolean address = false;

    /**
     * The cached hostname set on construction
     */

    private static final String cachedHostname = getLocalHostname();

    /**
     * The cached address hostname set on construction
     */
    private static final String cachedHostAddress = getLocalHostIPAddress();
    public static final String ERROR_NO_PROPERTY = "Property attribute must be defined";

    /**
     * Determine the local hostname; retrieving it from cache if it is known If we cannot determine our host name,
     * return "localhost"
     *
     * @return the local hostname or "localhost"
     */
    private static String getLocalHostname() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            return LOCALHOST;
        }
    }

    /**
     * Get the IPAddress of the local host as a string. This may be a loop back value.
     *
     * @return the IPAddress of the localhost
     */
    private static String getLocalHostIPAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            try {
                return InetAddress.getByName(LOCALHOST).getHostAddress();
            } catch (UnknownHostException e1) {
                return "127.0.0.1";
            }
        }
    }

    /**
     * Flag to indicate the address should be retrieved
     *
     * @param useAddress The new Address value
     */
    public void setUseAddress(boolean useAddress) {
        this.address = useAddress;
    }

    /**
     * Set the property to update with the hostname
     * @param property the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Does the work.
     *
     * @throws BuildException Thrown in unrecoverable error.
     */
    public void execute()
            throws BuildException {
        if (property == null) {
            throw new BuildException(ERROR_NO_PROPERTY);
        }
        String result;
        if (address) {
            result = cachedHostAddress;
        } else {
            result = cachedHostname;
        }
        getProject().setNewProperty(property, result);
    }


}
