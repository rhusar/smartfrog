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

import java.util.*;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.*;
import javax.management.*;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Discovery service component.
 * @author Serrano
 */

public abstract class DiscoveryService extends PrimImpl implements Prim, DiscoveryServiceMBean, NotificationBroadcaster, MBeanRegistration {

    protected MBeanServer mbeanServer;

    protected ObjectName objectName;

    protected String mbeanServerId;

    protected NotificationBroadcasterSupport broadcaster = new NotificationBroadcasterSupport();

    protected long sequenceNumber = 0;

    boolean isActive = false;

// Constructors

    /**
     * Default constructor for RMI
     * @throws RemoteException
     */
    public DiscoveryService() throws RemoteException {
    }

// MBeanRegistration interface

    /**
     *  Keep a reference to the MBeanServer and assigned ObjectName
     *
     *@param  server                   Description of the Parameter
     *@param  name                     Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  java.lang.Exception  Description of the Exception
     */

    public ObjectName preRegister(MBeanServer server, ObjectName name) throws java.lang.Exception {
        this.mbeanServer = server;
        objectName = name;
        mbeanServerId = (String) server.getAttribute(new ObjectName("JMImplementation:type=MBeanServerDelegate"), "MBeanServerId");
        return name;
    }

    /**
     *  If registration has not been succesfull, set reference to MBeanServer to
     *  null.
     *
     *@param  registrationDone  Description of the Parameter
     */
    public void postRegister(Boolean registrationDone) {
        if (!registrationDone.booleanValue()) {
            synchronized (this) {
                mbeanServer = null;
            }
        }
    }

    /**
     *  Stops this server
     *
     *@exception  java.lang.Exception  Description of the Exception
     */
    public void preDeregister() throws java.lang.Exception {
        stop();
    }


    /**
     *  Does nothing
     */
    public void postDeregister() { }


// NotificationBroadcaster interface

    /**
     * Adds a listener to a registered MBean.
     *
     * @param listener The listener object which will handle the notifications emitted by the registered MBean.
     * @param filter The filter object. If filter is null, no filtering will be performed before handling notifications.
     * @param handback An opaque object to be sent back to the listener when a notification is emitted. This object
     * cannot be used by the Notification broadcaster object. It should be resent unchanged with the notification
     * to the listener.
     *
     * @exception IllegalArgumentException Listener parameter is null.
     */
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
        throws java.lang.IllegalArgumentException {
        broadcaster.addNotificationListener(listener, filter, handback);
    }

    /**
     * Removes a listener from a registered MBean.
     *
     * @param listener The listener object which will handle the notifications emitted by the registered MBean.
     * This method will remove all the information related to this listener.
     *
     * @exception ListenerNotFoundException The listener is not registered in the MBean.
     */
    public void removeNotificationListener(NotificationListener listener)
        throws ListenerNotFoundException {
        broadcaster.removeNotificationListener(listener);
    }

    /**
     * Returns a NotificationInfo object contaning the name of the Java class of the notification
     * and the notification types sent.
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[]{
            new MBeanNotificationInfo(new String[]{ DiscoveryNotification.DISCOVERY_REGISTER },
                                      DiscoveryNotification.DISCOVERY_REGISTER,
                                      "Notify a new service registered in the Advertising service"),
            new MBeanNotificationInfo(new String[]{ DiscoveryNotification.DISCOVERY_UNREGISTER },
                                      DiscoveryNotification.DISCOVERY_UNREGISTER,
                                      "Notify a service unregistered in the Advertising service")
        };
    }

    private void sendDiscoveryNotification(boolean register) {
        String type;
        String message;
        if (register == true) {
            type = DiscoveryNotification.DISCOVERY_REGISTER;
            message = "New service discovered";
        }
        else {
            type = DiscoveryNotification.DISCOVERY_UNREGISTER;
            message = "Service unregistered";
        }

        DiscoveryNotification discoveryNotif =
                              new DiscoveryNotification(type,
                                                        objectName,
                                                        sequenceNumber++,
                                                        System.currentTimeMillis(),
                                                        message);
        // TO DO: insert here discovery information
        broadcaster.sendNotification( discoveryNotif );
    }

// To implement by the developer of a Discovery service

    /**
     * Abstract method that should be implemented by specific implementations of
     * this Advertising service to initialize the underlying specific
     * discovery mechanism. This method is called when the start() method is
     * invoked.
     */
    protected abstract void initialize();

    /**
     * Abstract method that should be implemented by specific implementations of
     * this Advertising service to release resources of the underlying specific
     * discovery mechanism. This method is called when the stop() method is
     * invoked.
     */
    protected abstract void terminate();

// DiscoveryServiceMBean interface

    /**
     * Starts the DiscoveryService by calling initialize() method.
     */
    public void start() {
        if (isActive || mbeanServer == null) {
            return;
        }
        try {
            initialize();
            isActive = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the DiscoveryService by calling terminate() method
     */
    public void stop() {
        if (!isActive) return;
        try {
            terminate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isActive = false;
    }

    /**
     *  Return the state of the AdvertisingService
     *
     *@return    The state
     */
    public boolean isActive() {
        return isActive;
    }

// Prim interface

    /**
     *  Simply call upper class method of the same name
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }


    /**
     *  Starts the AdvertisingService
     *
     * @throws SmartFrogException trouble
     * @throws RemoteException network trouble
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        start();
    }


    /**
     *  Stops the AdvertisingService
     *
     *@param  tr  Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        stop();
        super.sfTerminate(tr);
    }

}
