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

import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/**
 *  Description of the Interface
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public interface HeartBeatHandler {

    /**
     *  Adds a feature to the HeartBeatNotificationListener attribute of the
     *  HeartBeatHandler object
     *
     *@param  notifListener  The feature to be added to the
     *      HeartBeatNotificationListener attribute
     *@param  notifFilter    The feature to be added to the
     *      HeartBeatNotificationListener attribute
     *@param  handback       The feature to be added to the
     *      HeartBeatNotificationListener attribute
     */
    public void addHeartBeatNotificationListener(NotificationListener notifListener, NotificationFilter notifFilter, Object handback);


    /**
     *  Gets the heartBeatPeriod attribute of the HeartBeatHandler object
     *
     *@return    The heartBeatPeriod value
     */
    public int getHeartBeatPeriod();


    /**
     *  Gets the heartBeatRetries attribute of the HeartBeatHandler object
     *
     *@return    The heartBeatRetries value
     */
    public int getHeartBeatRetries();


    /**
     *  Description of the Method
     *
     *@param  notifListener  Description of the Parameter
     */
    public void removeHeartBeatNotificationListener(NotificationListener notifListener);


    /**
     *  Sets the heartBeatPeriod attribute of the HeartBeatHandler object
     *
     *@param  i  The new heartBeatPeriod value
     */
    public void setHeartBeatPeriod(int i);


    /**
     *  Sets the heartBeatRetries attribute of the HeartBeatHandler object
     *
     *@param  i  The new heartBeatRetries value
     */
    public void setHeartBeatRetries(int i);


    /**
     *  Gets the running attribute of the HeartBeatHandler object
     *
     *@return    The running value
     */
    public boolean isRunning();


    /**
     *  Description of the Method
     */
    public void startHeartBeat();


    /**
     *  Description of the Method
     */
    public void stopHeartBeat();

}
