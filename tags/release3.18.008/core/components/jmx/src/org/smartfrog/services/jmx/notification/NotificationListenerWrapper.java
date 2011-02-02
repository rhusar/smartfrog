package org.smartfrog.services.jmx.notification;

import java.io.Serializable;
import java.util.Vector;
import java.rmi.*;
import java.rmi.server.*;
import javax.management.NotificationListener;
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
public class NotificationListenerWrapper implements NotificationListener, Serializable {

    protected RemoteNotificationListener remoteListener;

    /**
     *  Constructor for the NotificationListenerWrapper object
     *
     *@param  rmiListener  Description of the Parameter
     */
    public NotificationListenerWrapper(RemoteNotificationListener rmiListener) {
        remoteListener = rmiListener;
    }

        // We make the listener ready for remote invocation
    /**
     * Creates a wrapper of a NotificationListener so that it can be registered
     * in an MBean of a remote JMX Agent and the local wrapped
     * NotificationListener receive their notification.
     *
     * @param listener listener
     * @return a new (exported) listener
     */
    public static NotificationListener createWrapper(NotificationListener listener) throws RemoteException {
        RemoteNotificationListenerWrapper remoteListenerWrapper = new RemoteNotificationListenerWrapper(listener);
        UnicastRemoteObject.exportObject(remoteListenerWrapper);
        return new NotificationListenerWrapper(remoteListenerWrapper);
    }

    /**
     *  Handles the given notification by sending this to the remote client
     *  listener
     *
     *@param  notification  Description of the Parameter
     *@param  handback      Description of the Parameter
     */
    public void handleNotification(Notification notification, Object handback) {
        if (remoteListener == null) {
            return;
        }
        NotificationHandler nh = new NotificationHandler(remoteListener, notification, handback);
        nh.start();
    }


    /**
     *  Description of the Method
     */
    public void stopListen() {
        remoteListener = null;
    }

    public int hashCode() {
        return remoteListener.hashCode();
    }

    public boolean equals(Object obj) {
	if (obj instanceof NotificationListenerWrapper) {
	    if (remoteListener == null) {
		return obj == this;
	    } else {
		return remoteListener.equals(((NotificationListenerWrapper)obj).remoteListener);
	    }
	} else {
	    return false;
	}
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
    static class NotificationHandler extends Thread {
        RemoteNotificationListener remoteListener;
        Notification notification;
        Object handback;


        /**
         *  Constructor for the NotificationHandler object
         *
         *@param  rnl    Description of the Parameter
         *@param  notif  Description of the Parameter
         *@param  hb     Description of the Parameter
         */
        public NotificationHandler(RemoteNotificationListener rnl, Notification notif, Object hb) {
            this.remoteListener = rnl;
            this.notification = notif;
            this.handback = hb;
        }


        /**
         *  Main processing method for the NotificationHandler object
         */
        public void run() {
            if (remoteListener != null) {
                try {
                    remoteListener.handleRemoteNotification(notification, handback);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
            this.remoteListener = null;
        }

    }

}
