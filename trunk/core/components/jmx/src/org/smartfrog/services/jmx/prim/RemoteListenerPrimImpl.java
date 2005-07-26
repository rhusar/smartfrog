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

package org.smartfrog.services.jmx.prim;

import java.util.Date;
import java.rmi.*;
import javax.management.*;
import org.smartfrog.services.jmx.notification.RemoteNotificationListener;

/**
 *  Description of the Class
 *
 *@title          sfJMX
 *@description    JMX-based Management Framework for SmartFrog Applications
 *@company        Hewlett Packard
 *
 *@version        1.0
 */
public class RemoteListenerPrimImpl extends RemotePrimImpl implements RemoteNotificationListener {

    /**
     *  Constructor for the ListenerPrimImpl object
     *
     *@exception  RemoteException  Description of the Exception
     */
    public RemoteListenerPrimImpl() throws RemoteException {
        super();
    }


    /**
     *  By default it prints notification data on screen. Subclasses should
     *  provide a more useful functionality.
     *
     *@param  notification         Description of the Parameter
     *@param  handback             Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     */
    public void handleRemoteNotification(Notification notification, Object handback) throws RemoteException {
        long sequenceNumber = notification.getSequenceNumber();
        String type = notification.getType();
        Object source = notification.getSource();
        String message = notification.getMessage();
        Object userData = notification.getUserData();
        String timestamp = (new Date(notification.getTimeStamp())).toString();
        System.out.println(sfCompleteName() + ": notification received");
        System.out.println("\tSeq number:\t\t" + sequenceNumber);
        System.out.println("\tType:\t\t" + type);
        System.out.println("\tsource:\t\t" + source);
        System.out.println("\tmessage:\t\t" + message);
        System.out.println("\tuserData:\t\t" + userData);
        System.out.println("\ttimestamp:\t\t" + type);
        if (notification instanceof AttributeChangeNotification) {
            AttributeChangeNotification acn = (AttributeChangeNotification) notification;
            System.out.println("\tAttribute:\t\t" + acn.getAttributeName());
            System.out.println("\tOld value:\t\t" + acn.getOldValue());
            System.out.println("\tOld value:\t\t" + acn.getNewValue());
        }
    }
}
