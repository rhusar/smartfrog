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

package org.smartfrog.services.jmx.notification;

import java.rmi.*;
import javax.management.*;

/**
 *  Description of the Interface
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public interface RemoteNotificationBroadcaster extends Remote {

    /**
     *  Adds a feature to the RemoteNotificationListener attribute of the
     *  RemoteNotificationBroadcaster object
     *
     *@param  listener                      The feature to be added to the
     *      RemoteNotificationListener attribute
     *@param  filter                        The feature to be added to the
     *      RemoteNotificationListener attribute
     *@param  handback                      The feature to be added to the
     *      RemoteNotificationListener attribute
     *@exception  MBeanException            Description of the Exception
     *@exception  IllegalArgumentException  Description of the Exception
     *@exception  RemoteException           Description of the Exception
     */
    public void addRemoteNotificationListener
            (RemoteNotificationListener listener, NotificationFilter filter, Object handback)
             throws MBeanException, IllegalArgumentException, RemoteException;


    /**
     *  Description of the Method
     *
     *@param  listener                       Description of the Parameter
     *@exception  ListenerNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public void removeRemoteNotificationListener(RemoteNotificationListener listener)
             throws ListenerNotFoundException, RemoteException;


    /**
     *  Gets the remoteNotificationInfo attribute of the
     *  RemoteNotificationBroadcaster object
     *
     *@return                      The remoteNotificationInfo value
     *@exception  RemoteException  Description of the Exception
     */
    public MBeanNotificationInfo[] getRemoteNotificationInfo() throws RemoteException;

}
