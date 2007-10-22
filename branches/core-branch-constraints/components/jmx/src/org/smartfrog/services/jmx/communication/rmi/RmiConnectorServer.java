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

import java.net.InetAddress;
import java.io.Serializable;
import java.rmi.RemoteException;
import javax.management.*;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 *  Prim implementation that acts as an RmiConnectorServer
 */
public class RmiConnectorServer extends PrimImpl implements Prim, Serializable, RmiConnectorServerMBean, MBeanRegistration, NotificationBroadcaster {

    /**
     *  Description of the Field
     */
    public static int defaultPort = 3800;

    /**
     *  Description of the Field
     */
    public static String defaultServiceName = "RMIConnectorServer";

    private int port;

    private String serviceName;

    private RmiMBeanServerInvoker rmiConnectorInvoker;

    private MBeanServer mbeanServer;

    private ObjectName objectName;

    private NotificationBroadcasterSupport broadcaster = new NotificationBroadcasterSupport();

    private long sequenceNumber = 0;

    /**
     *  Constructor for the RmiConnectorServer object
     *
     *@exception  RemoteException  Description of the Exception
     */
    public RmiConnectorServer() throws RemoteException {
        this(defaultPort, defaultServiceName);
    }

    /**
     *  Constructor for the RmiConnectorServer object. It allows configuring
     *  port with default service name.
     *
     *@param  port                 Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     */
    public RmiConnectorServer(int port) throws RemoteException {
        this(port, defaultServiceName);
    }


    /**
     *  Constructor for the RmiConnectorServer object
     *
     *@param  serviceName          Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     */
    public RmiConnectorServer(String serviceName) throws RemoteException {
        this(defaultPort, serviceName);
    }


    /**
     *  Constructor for the RmiConnectorServer object
     *
     *@param  port                 Description of the Parameter
     *@param  serviceName          Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     */
    public RmiConnectorServer(int port, String serviceName) throws RemoteException {
        this.port = port;
        this.serviceName = serviceName;
    }


// MBeanRegistration interface

    public void postDeregister() { }


    /**
     *  Description of the Method
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
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public void preDeregister() throws Exception {
        stop();
        synchronized (this) {
            mbeanServer = null;
        }
        objectName = null;
    }


    /**
     *  Description of the Method
     *
     *@param  mbeanserver    Description of the Parameter
     *@param  objectname     Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ObjectName preRegister(MBeanServer mbeanserver, ObjectName objectname) throws Exception {
        objectName = objectname;
        synchronized (this) {
            if (mbeanServer != null && mbeanServer != mbeanserver) {
                throw new IllegalArgumentException("Connector has been already registered in other MBean server");
            }
            mbeanServer = mbeanserver;
        }
        return objectname;
    }


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
            new MBeanNotificationInfo(new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                                      AttributeChangeNotification.ATTRIBUTE_CHANGE,
                                      "Notify a change of state")
        };
    }

    private void sendStateNotification(boolean newState) {
        String message = "";
        if (newState == true) message = "Service started";
        else message = "Service stopped";

        Boolean newValue = new Boolean(newState);
        Boolean oldValue = new Boolean(!newState);

        broadcaster.sendNotification(
                    new AttributeChangeNotification(
                        this.objectName,
                        sequenceNumber++,
                        System.currentTimeMillis(),
                        message,
                        "Active",
                        "java.lang.Boolean",
                        oldValue,
                        newValue));
    }


// RmiConnectorServerMBean interface

    /**
     *  Gets the mBeanServer attribute of the RmiConnectorServer object
     *
     *@return    The mBeanServer value
     */
    MBeanServer getMBeanServer() {
        return mbeanServer;
    }


    /**
     *  RmiConectorServerMBean Interface ****
     *
     *@return    The host value
     */

    public String getHost() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            host = "Unknown";
        }
        return host;
    }


    /**
     *  Gets the port attribute of the RmiConnectorServer object
     *
     *@return    The port value
     */
    public int getPort() {
        return port;
    }


    /**
     *  Gets the protocol attribute of the RmiConnectorServer object
     *
     *@return    The protocol value
     */
    public String getProtocol() {
        return "RMI";
    }


    /**
     *  Gets the active attribute of the RmiConnectorServer object
     *
     *@return    The active value
     */
    public boolean isActive() {
        if (rmiConnectorInvoker == null) {
            return false;
        }
        return rmiConnectorInvoker.isActive();
    }


    /**
     *  Sets the port attribute of the RmiConnectorServer object
     *
     *@param  newPort                              The new port value
     *@exception  java.lang.IllegalStateException  Description of the Exception
     */
    public void setPort(int newPort) throws java.lang.IllegalStateException {
        if (isActive()) {
            throw new IllegalStateException("You should stop this RMIConnectorServer first");
        }
        port = newPort;
    }


    /**
     *  Description of the Method
     */
    public void start() {
        if (isActive() || mbeanServer == null) {
            return;
        }
        try {
            rmiConnectorInvoker = new RmiMBeanServerInvoker(this);
            if (!isActive()) {
                rmiConnectorInvoker.bind();
            }
            if (isActive()) sendStateNotification(true);
        } catch (Exception e) {
            sfLog().warn(e);
        }
    }


    /**
     *  Description of the Method
     */
    public void stop() {
        if (isActive()) {
            rmiConnectorInvoker.unbind();
            rmiConnectorInvoker.stopListeners();
            rmiConnectorInvoker = null;
            sendStateNotification(false);
        }
    }


    /**
     *  Gets the serviceName attribute of the RmiConnectorServer object
     *
     *@return    The serviceName value
     */
    public String getServiceName() {
        return serviceName;
    }


    /**
     *  Sets the serviceName attribute of the RmiConnectorServer object
     *
     *@param  newServiceName                       The new serviceName value
     *@exception  java.lang.IllegalStateException  Description of the Exception
     */
    public void setServiceName(String newServiceName) throws java.lang.IllegalStateException {
        if (isActive()) {
            throw new IllegalStateException("You should stop this RMIConnectorServer first");
        }
        serviceName = newServiceName;
    }

// Prim interface

    /**
     *  Prim Interface ****
     *
     *@exception  Exception  Description of the Exception
     */

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        try {
            port = ((Integer) sfResolveHere("port")).intValue();
        } catch (Exception e) {
            sfLog().ignore(e);
        }
        try {
            serviceName = (String) sfResolveHere("name");
        } catch (Exception e) {
            sfLog().ignore(e);
        }
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        start();
        if (isActive()) {
            if (sfLog().isInfoEnabled()){ sfLog().info(" -> RMIConnectorServer started [rmi://"+ this.getHost()+":"+port +"/"+this.serviceName+"]");}
        } else {
            if (sfLog().isErrorEnabled()){ sfLog().error(" -> Could not start RMIConnectorServer");}
        }
    }


    /**
     *  Description of the Method
     *
     *@param  tr  Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        stop();
        super.sfTerminate(tr);
    }

}
