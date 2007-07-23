/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.discovery;

import java.util.Vector;
import java.util.Enumeration;
import javax.management.Notification;

/**
 *  This notification is sent whenever a remote Communicator is advertised and
 *  discovered or whenever a discoveed Communicator has been deleted from the
 *  AdvertisingService. It contains all the information related to the
 *  discovered remote Communicator as well as the MBeanServer that hosts it. <p>
 *
 *  Title: SmartFrog</p> <p>
 *
 *  Description: </p> <p>
 *
 *  Copyright: Copyright (c) 2002</p> <p>
 *
 *  Company: Hewlett Packard</p>
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *@author         Serrano
 *@version        1.0
 */

public class DiscoveryNotification extends Notification {

    /**
     *  Notification type denoting that a new service has been advertised
     */
    public final static String DISCOVERY_REGISTER = "jmx.discovery.register";

    /**
     *  Notification type denoting that a new service has been deleted
     */
    public final static String DISCOVERY_UNREGISTER = "jmx.discovery.unregister";

    /**
     *  JMX Agent descriptor to which the service belongs
     */
    AgentDescriptor agentDescriptor;


    /**
     *  Creates a DiscoveryNotification object.
     *
     *@param  type            The notification type.
     *@param  source          The notification source.
     *@param  sequenceNumber  The notification sequence number within the source
     *      object.
     *@param  message         The detailed message.
     *@param  timeStamp       Description of the Parameter
     */
    public DiscoveryNotification(String type, Object source, long sequenceNumber, long timeStamp, String message) {
        super(type, source, sequenceNumber, timeStamp, message);
    }


    /**
     *  Gets the agentDescriptor attribute of the DiscoveryNotification object
     *
     *@return    The agentDescriptor value
     */
    public AgentDescriptor getAgentDescriptor() {
        return agentDescriptor;
    }

}
