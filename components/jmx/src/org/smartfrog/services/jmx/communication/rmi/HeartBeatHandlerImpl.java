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

package org.smartfrog.services.jmx.communication.rmi;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Date;
import java.io.Serializable;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import org.smartfrog.services.jmx.communication.HeartBeatHandler;
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
public class HeartBeatHandlerImpl implements HeartBeatHandler, Runnable, Serializable {

    /**
     *  The RmiConnectorServer to be pinged
     */
    RmiConnectorClient rmiClient;

    /**
     *  The thread that pings the server
     */
    Thread pingThread;

    /**
     *  The period between two consecutive pings
     */
    int period = 0;

    /**
     *  The number of retries before considering a connection lost
     */
    int retries = 0;

    /**
     *  The client identifier provided by the server
     */
    String clientID = null;

    /**
     *  Flag indicating if this HeartBeatHandler is pinging the server
     */
    boolean isRunning = false;

    /**
     *  Listener hastable containing the hand-back objects.
     */
    private Hashtable handbackTable = new java.util.Hashtable();

    /**
     *  Listener hastable containing the filter objects.
     */
    private Hashtable filterTable = new java.util.Hashtable();

    /**
     *  Sequece number to identify the sent notifications
     */
    private long sequenceNumber = 0;


    /**
     *  HeartBeatHandler Interface ****
     *
     *@param  client  Description of the Parameter
     */

    public HeartBeatHandlerImpl(RmiConnectorClient client) {
        rmiClient = client;
    }


    /**
     *  Adds a feature to the HeartBeatNotificationListener attribute of the
     *  HeartBeatHandlerImpl object
     *
     *@param  notifListener  The feature to be added to the
     *      HeartBeatNotificationListener attribute
     *@param  notifFilter    The feature to be added to the
     *      HeartBeatNotificationListener attribute
     *@param  handback       The feature to be added to the
     *      HeartBeatNotificationListener attribute
     */
    public void addHeartBeatNotificationListener(NotificationListener notifListener, NotificationFilter notifFilter, Object handback) {
        // Check listener
        if (notifListener == null) {
            throw new java.lang.IllegalArgumentException("Listener cannot be null");
        }

        // looking for listener in handbackTable
        Vector handbackList = (Vector) handbackTable.get(notifListener);
        Vector filterList = (Vector) filterTable.get(notifListener);
        if (handbackList == null) {
            handbackList = new Vector();
            filterList = new Vector();
            handbackTable.put(notifListener, handbackList);
            filterTable.put(notifListener, filterList);
        }
        // Add the handback and the filter
        handbackList.addElement(handback);
        filterList.addElement(notifFilter);
    }


    /**
     *  Gets the heartBeatPeriod attribute of the HeartBeatHandlerImpl object
     *
     *@return    The heartBeatPeriod value
     */
    public int getHeartBeatPeriod() {
        return period;
    }


    /**
     *  Gets the heartBeatRetries attribute of the HeartBeatHandlerImpl object
     *
     *@return    The heartBeatRetries value
     */
    public int getHeartBeatRetries() {
        return retries;
    }


    /**
     *  Description of the Method
     *
     *@param  notifListener  Description of the Parameter
     */
    public void removeHeartBeatNotificationListener(NotificationListener notifListener) {
        handbackTable.remove(notifListener);
        filterTable.remove(notifListener);
    }


    /**
     *  Set a new heart beat rate starting or stoping the heart beat if
     *  neccesary.
     *
     *@param  p  The new heartBeatPeriod value
     */
    public void setHeartBeatPeriod(int p) {
        int oldPeriod = period;
        period = p;
        if (!rmiClient.isActive()) {
            return;
        }
        if (period > 0 && oldPeriod <= 0) {
            startHeartBeat();
        } else if (period < 1 && oldPeriod > 0) {
            stopHeartBeat();
        }
    }


    /**
     *  Sets the heartBeatRetries attribute of the HeartBeatHandlerImpl object
     *
     *@param  r  The new heartBeatRetries value
     */
    public void setHeartBeatRetries(int r) {
        retries = r;
    }


    /**
     *  Gets the running attribute of the HeartBeatHandlerImpl object
     *
     *@return    The running value
     */
    public boolean isRunning() {
        return isRunning;
    }


    /**
     *  Description of the Method
     */
    public void startHeartBeat() {
        if (period < 1 || (pingThread != null && pingThread.isAlive())) {
            return;
        }
        pingThread = new Thread(this);
        pingThread.start();
    }


    /**
     *  Description of the Method
     */
    public void stopHeartBeat() {
        if (pingThread == null) {
            return;
        }
        pingThread.interrupt();
        pingThread = null;
        clientID = null;
    }


    /**
     *  Description of the Method
     *
     *@param  notification  Description of the Parameter
     */
    public void sendNotification(HeartBeatNotification notification) {
        for (java.util.Enumeration k = handbackTable.keys(); k.hasMoreElements(); ) {
            NotificationListener listener = (NotificationListener) k.nextElement();

            // Get the associated handback list and the associated filter list
            Vector handbackList = (Vector) handbackTable.get(listener);
            Vector filterList = (Vector) filterTable.get(listener);
            // loop on handback
            Enumeration f = filterList.elements();
            for (java.util.Enumeration h = handbackList.elements(); h.hasMoreElements(); ) {
                Object handback = h.nextElement();
                NotificationFilter filter = (NotificationFilter) f.nextElement();

                if ((filter == null) ||
                        ((filter != null) && (filter.isNotificationEnabled(notification)))) {
                    try {
                        listener.handleNotification(notification, handback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  notifType  Description of the Parameter
     */
    public void sendNotification(String notifType) {
        if (++sequenceNumber == 0x7fffffffffffffffL) {
            sequenceNumber = 0;
        }
        String message = "";
        if (HeartBeatNotification.CONNECTED.equals(notifType)) {
            message = "RMI Client has connected succesfully";
        } else if (HeartBeatNotification.DISCONNECTED.equals(notifType)) {
            message = "RMI Client has disconnected";
        } else if (HeartBeatNotification.LOST.equals(notifType)) {
            message = "Connection lost";
        } else if (HeartBeatNotification.RETRYING.equals(notifType)) {
            message = "RMI Client is retrying to restablish the connection";
        } else if (HeartBeatNotification.RESTABLISHED.equals(notifType)) {
            message = "RMI Client has restablished the connection succesfully";
        }
        HeartBeatNotification notif = new HeartBeatNotification(notifType, rmiClient, sequenceNumber, new Date().getTime(), message, rmiClient.getServerAddress());
        sendNotification(notif);
    }


    /**
     *  Runnable Interface ****
     */
    public void run() {
        int retried = 0;
        while (period > 0) {
            long sendingTime = 0;
            long tripTime = 0;
            long receivingTime = 0;
            try {
                sendingTime = System.currentTimeMillis();
                String tmpID = rmiClient.getRemoteMBeanServer().heartBeatPing(clientID, period, retries);
                if (clientID == null && tmpID != null) {
                    clientID = tmpID;
                } else if (tmpID == null || !tmpID.equals(clientID)) {
                    sendNotification(HeartBeatNotification.LOST);
                    break;
                }
                if (retried > 0) {
                    retried = 0;
                    sendNotification(HeartBeatNotification.RESTABLISHED);
                }
            } catch (Exception e) {
                if (retries != 0) {
                    retried++;
                    if (retried == 1) {
                        sendNotification(HeartBeatNotification.RETRYING);
                    } else if (retried > retries) {
                        sendNotification(HeartBeatNotification.LOST);
                        break;
                    }
                } else {
                    sendNotification(HeartBeatNotification.LOST);
                    break;
                }
            }
            receivingTime = System.currentTimeMillis();
            tripTime = receivingTime - sendingTime;
            if (tripTime >= (long) period) {
                continue;
            } else {
                try {
                    Thread.sleep((long) period - tripTime);
                    continue;
                } catch (Exception e) {
                    break;
                }
            }
        }
        // end while
    }

}
