/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.services.xmpp;
/**
 * Constants used on both Avalanche Server and clients. This class
 * exists on both places.
 * @author sanjaydahiya
 *
 */
public class MonitoringConstants {

	public static final String ANUBIS_SHARED_NAME = "avalancheDeployEvent";
	public static final String DEPLOY_JMS_QUEUE = "avalancheDeployQueue";
	public static final String MODULE_STATE_MANAGER = "moduleStateManager" ;

	// message types for events
	public static final int MODULE_STATE_CHANGED = 1;
	public static final int MODULE_OPERATION_FAILED = 2;
	public static final int MODULE_VANISH  = 3;
	public static final int MODULE_INFO = 4;

    // message types for vm events
    public static final int VM_MESSAGE = 10;

    public static final int HOST_VANISH = 100;
	public static final int HOST_SHUTTING_DOWN = 101;
	public static final int HOST_STARTED = 102;
}