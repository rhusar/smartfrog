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

import javax.management.NotificationBroadcaster;

/**
 *  This is the interface that most ConnectorServers and Adaptors should use.
 *  It extends NotificationBroadcaster interface so that the Communicator
 *  can notify a change of state from inactive to active and vice versa.
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */

public interface CommunicatorMBean {

    /**
     *  Gets the host attribute of the CommunicatorMBean object
     *
     *@return    The host value
     */
    public String getHost();


    /**
     *  Gets the port attribute of the CommunicatorMBean object
     *
     *@return    The port value
     */
    public int getPort();


    /**
     *  Gets the protocol attribute of the CommunicatorMBean object
     *
     *@return    The protocol value
     */
    public String getProtocol();


    /**
     *  Gets the active attribute of the CommunicatorMBean object
     *
     *@return    The active value
     */
    public boolean isActive();


    /**
     *  Sets the port attribute of the CommunicatorMBean object
     *
     *@param  port                                 The new port value
     *@exception  java.lang.IllegalStateException  Description of the Exception
     */
    public void setPort(int port) throws java.lang.IllegalStateException;


    /**
     *  Description of the Method
     */
    public void start();


    /**
     *  Description of the Method
     */
    public void stop();

}
