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

package org.smartfrog.services.jmx.mbeanbrowser;

import java.io.Serializable;
import java.rmi.*;
import javax.swing.*;
import javax.management.*;
import javax.management.monitor.*;
import org.smartfrog.services.jmx.communication.ServerAddress;
import org.smartfrog.services.jmx.communication.HeartBeatNotification;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class ListenerImpl implements Serializable, NotificationListener {

    MainFrame m_frame = null;
    NotificationViewer m_viewer = null;


    /**
     *  Constructor for the ListenerImpl object
     */
    public ListenerImpl() { }


    /**
     *  Sets the frame attribute of the ListenerImpl object
     *
     *@param  frame  The new frame value
     */
    public void setFrame(MainFrame frame) {
        m_frame = frame;
    }


    /**
     *  Adds a feature to the NotificationViewer attribute of the ListenerImpl
     *  object
     *
     *@param  viewer  The feature to be added to the NotificationViewer
     *      attribute
     */
    public void addNotificationViewer(NotificationViewer viewer) {
        m_viewer = viewer;
    }


    /**
     *  Interface NotificationListener *
     *
     *@param  notification  Description of the Parameter
     *@param  handback      Description of the Parameter
     */
    /**
     *  Invoked when a JMX notification occurs. The implementation of this
     *  method should return as soon as possible, to avoid blocking its
     *  notification broadcaster.
     *
     *@param  notification  The notification.
     *@param  handback      An opaque object which helps the listener to
     *      associate information regarding the MBean emitter. This object is
     *      passed to the MBean during the addListener call and resent, without
     *      modification, to the listener. The MBean object should not use or
     *      modify the object.
     */
    public synchronized void handleNotification(Notification notification, Object handback) {
        if (m_viewer != null) {
            m_viewer.addNotification(notification);
        }
        new NotificationHandler(notification).start();
    }


    /**
     *  Description of the Class
     *
     *          sfJMX
     *   JMX-based Management Framework for SmartFrog Applications
     *       Hewlett Packard
 *
     *@version        1.0
     */
    class NotificationHandler extends Thread {
        Notification notification;


        /**
         *  Constructor for the NotificationHandler object
         *
         *@param  notif  Description of the Parameter
         */
        public NotificationHandler(Notification notif) {
            notification = notif;
        }


        /**
         *  Main processing method for the NotificationHandler object
         */
        public void run() {
            if (notification instanceof HeartBeatNotification) {
                ServerAddress notif_address = ((HeartBeatNotification) notification).getServerAddress();
                if (notification.getType().equals(HeartBeatNotification.LOST)) {
                    if (m_frame != null) {
                        m_frame.isConnectionLost = true;
                        m_frame.disconnect();
                    }
                    JOptionPane.showMessageDialog(m_frame, notification.getType(), "HeartBeat Notification", JOptionPane.WARNING_MESSAGE);
                }
            } else if (notification instanceof MBeanServerNotification) {
                ObjectName objectname = ((MBeanServerNotification) notification).getMBeanName();
                String type = notification.getType().toLowerCase();
                if (type.equals("jmx.mbean.registered")) {
                    m_frame.registerForMBean(objectname);
                    //JOptionPane.showMessageDialog(m_frame, type+": "+objectname, "MBeanServer Notification", JOptionPane.WARNING_MESSAGE);
                    m_frame.queryPanel.requery();
                } else if (type.equals("jmx.mbean.unregistered")) {
                    //JOptionPane.showMessageDialog(m_frame, type+": "+objectname, "MBeanServer Notification", JOptionPane.WARNING_MESSAGE);
                    m_frame.queryPanel.requery();
                    m_frame.setMBean(null);
                }
            } else if (notification instanceof MonitorNotification) {
                MonitorNotification monNotif = (MonitorNotification) notification;
                JOptionPane.showMessageDialog(m_frame,
                        monNotif.getType() + "\nObserved Object: " + monNotif.getObservedObject() + "\nObserved Attribute: " + monNotif.getObservedAttribute(),
                        "Monitor Notification", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}
