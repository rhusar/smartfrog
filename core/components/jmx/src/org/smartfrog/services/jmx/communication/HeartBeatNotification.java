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

package org.smartfrog.services.jmx.communication;

import javax.management.Notification;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class HeartBeatNotification extends Notification {

    /**
     *  Description of the Field
     */
    public final static String CONNECTED = "jmx.heartbeat.connected";

    /**
     *  Description of the Field
     */
    public final static String RETRYING = "jmx.hearbeat.retrying";

    /**
     *  Description of the Field
     */
    public final static String RESTABLISHED = "jmx.heartbeat.restablished";

    /**
     *  Description of the Field
     */
    public final static String LOST = "jmx.heartbeat.lost";

    /**
     *  Description of the Field
     */
    public final static String DISCONNECTED = "jmx.heartbeat.disconnected";

    private ServerAddress serverAddress;


    /**
     *  Constructor for the HeartBeatNotification object
     *
     *@param  type            Description of the Parameter
     *@param  source          Description of the Parameter
     *@param  sequenceNumber  Description of the Parameter
     *@param  timeStamp       Description of the Parameter
     *@param  message         Description of the Parameter
     *@param  address         Description of the Parameter
     */
    public HeartBeatNotification(String type, Object source, long sequenceNumber, long timeStamp, String message, ServerAddress address) {
        super(type, source, sequenceNumber, timeStamp, message);
        serverAddress = address;
    }


    /**
     *  Gets the serverAddress attribute of the HeartBeatNotification object
     *
     *@return    The serverAddress value
     */
    public ServerAddress getServerAddress() {
        return serverAddress;
    }

}
