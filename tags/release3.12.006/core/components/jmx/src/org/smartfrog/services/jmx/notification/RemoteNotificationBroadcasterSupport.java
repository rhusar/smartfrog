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
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class RemoteNotificationBroadcasterSupport implements Runnable, java.io.Serializable {

    /**
     *  Listener hastable containing the hand-back objects.
     */
    private java.util.Hashtable handbackTable = new java.util.Hashtable();

    /**
     *  Listener hastable containing the filter objects.
     */
    private java.util.Hashtable filterTable = new java.util.Hashtable();

    private Notification lastNotification = null;


    /**
     *  Adds a remote listener to a registered MBean.
     *
     *@param  listener             The listener object which will handle the
     *      notifications emitted by the registered MBean.
     *@param  filter               The filter object. If filter is null, no
     *      filtering will be performed before handling notifications.
     *@param  handback             An opaque object to be sent back to the
     *      listener when a notification is emitted. This object cannot be used
     *      by the Notification broadcaster object. It should be resent
     *      unchanged with the notification to the listener.
     *@exception  RemoteException  Description of the Exception
     */
    public synchronized void addRemoteNotificationListener(RemoteNotificationListener listener, NotificationFilter filter, Object handback)
             throws RemoteException {
        // Check listener
        if (listener == null) {
            throw new java.lang.IllegalArgumentException("Listener can't be null");
        }

        // looking for listener in handbackTable
        java.util.Vector handbackList = (java.util.Vector) handbackTable.get(listener);
        java.util.Vector filterList = (java.util.Vector) filterTable.get(listener);
        if (handbackList == null) {
            handbackList = new java.util.Vector();
            filterList = new java.util.Vector();
            handbackTable.put(listener, handbackList);
            filterTable.put(listener, filterList);
        }
        // Add the handback and the filter
        handbackList.addElement(handback);
        filterList.addElement(filter);
    }


    /**
     *  Removes a remote listener from a registered MBean.
     *
     *@param  listener                       The listener object which will
     *      handle the notifications emitted by the registered MBean. This
     *      method will remove all the information related to this listener.
     *@exception  ListenerNotFoundException  The listener is not registered in
     *      the MBean.
     *@exception  RemoteException            Description of the Exception
     */
    public synchronized void removeRemoteNotificationListener(RemoteNotificationListener listener) throws ListenerNotFoundException, RemoteException {
        // looking for listener in handbackTable
        java.util.Vector handbackList = (java.util.Vector) handbackTable.get(listener);
        java.util.Vector filterList = (java.util.Vector) filterTable.get(listener);
        if (handbackList == null) {
            throw new ListenerNotFoundException("listener");
        }

        // If handback is null, remove the listener entry
        handbackTable.remove(listener);
        filterTable.remove(listener);
    }


    /**
     *  Returns a NotificationInfo object contaning the name of the Java class
     *  of the notification and the notification types sent.
     *
     *@return                      The remoteNotificationInfo value
     *@exception  RemoteException  Description of the Exception
     */
    public MBeanNotificationInfo[] getRemoteNotificationInfo() throws RemoteException {
        return new MBeanNotificationInfo[0];
    }


    /**
     *  Enables an MBean to send a notification.
     *
     *@param  notification         The notification to send.
     *@exception  RemoteException  Description of the Exception
     */
    public synchronized void sendRemoteNotification(Notification notification) throws RemoteException {
        lastNotification = notification;
        new Thread(this).start();
    }


    /**
     *  Main processing method for the RemoteNotificationBroadcasterSupport
     *  object
     */
    public void run() {
        // loop on listener
        Notification notification = lastNotification;
        for (java.util.Enumeration k = handbackTable.keys(); k.hasMoreElements(); ) {
            RemoteNotificationListener listener = (RemoteNotificationListener) k.nextElement();

            // Get the associated handback list and the associated filter list
            java.util.Vector handbackList = (java.util.Vector) handbackTable.get(listener);
            java.util.Vector filterList = (java.util.Vector) filterTable.get(listener);
            // loop on handback
            java.util.Enumeration f = filterList.elements();
            for (java.util.Enumeration h = handbackList.elements(); h.hasMoreElements(); ) {
                Object handback = h.nextElement();
                NotificationFilter filter = (NotificationFilter) f.nextElement();

                if ((filter == null) ||
                        ((filter != null) && (filter.isNotificationEnabled(notification)))) {
                    try {
//             System.out.println("Notification: "+notification.getMessage()+" "+notification.getSequenceNumber()+" "+notification.getSource()+" "+notification.getTimeStamp() +" "+notification.getType() instanceof java.io.Serializable+" "+notification.getUserData());
                        listener.handleRemoteNotification(notification, handback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
