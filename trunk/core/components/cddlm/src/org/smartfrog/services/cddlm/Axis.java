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
package org.smartfrog.services.cddlm;

import java.rmi.Remote;

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

    public final static String WSDD_RESOURCE="wsddResource";

    /**
     * maximum number of threads
     */
    public final static String THREADS="threads";

    /**
     * maximum number of sessions
     */
    public final static String SESSIONS="sessions";
}
