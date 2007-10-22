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
package org.smartfrog.services.axis;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the remotable interface.
 * @author steve loughran
 *         created 02-Mar-2004 17:28:23
 */


public interface Axis extends Remote {

    /**
     * port string
     */
    public final static String PORT="port";

    /**
     * name of wsdd file
     */

    public final static String WSDD_RESOURCE="wsddDescriptor";

    /**
     * maximum number of threads
     */
    public final static String THREADS="threads";

    /**
     * maximum number of sessions
     */
    public final static String SESSIONS="sessions";

    /**
     * name of the liveness page
     */
    public final static String LIVENESS_PAGE="livenessPage";

    /** path to axis under app server; generated at run time */
    public static final String SERVICE_PATH="servicePath";


    /**
     * register a resource
     * the resource must be on the classpath of this component,
     * @param resourcename name of resource on the classpath
     * @throws SmartFrogException
     */

    void registerResource(String resourcename)
            throws SmartFrogException, RemoteException;

}
