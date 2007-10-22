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

import java.io.ObjectInputStream;
import java.util.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.management.*;
import javax.management.loading.ClassLoaderRepository;
import org.smartfrog.services.jmx.notification.RemoteNotificationListenerWrapper;
import org.smartfrog.services.jmx.communication.ConnectorClient;
import org.smartfrog.services.jmx.communication.HeartBeatNotification;
import org.smartfrog.services.jmx.communication.ServerAddress;
import org.smartfrog.services.jmx.communication.RuntimeConnectionException;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class RmiConnectorClient implements ConnectorClient {

    private RemoteMBeanServer rmiConnectorServer;

    private RmiServerAddress rmiServerAddress;

    private HeartBeatHandlerImpl heartBeatHandler;

    private Hashtable localListeners;

    private boolean isActive = false;


    /**
     *  Constructor for the RmiConnectorClient object
     */
    public RmiConnectorClient() {
        heartBeatHandler = new HeartBeatHandlerImpl(this);
    }

    /**
     * Returns a String representation of this ConnectorClient
     *
     * @return  String
     */
    public String toString() {
        if (rmiServerAddress != null) {
            return "RmiConnectorClient connected to "+rmiServerAddress;
        }
        else return super.toString();
    }

    /**
     *  Gets the remoteMBeanServer attribute of the RmiConnectorClient object
     *
     *@return    The remoteMBeanServer value
     */
    RemoteMBeanServer getRemoteMBeanServer() {
        return rmiConnectorServer;
    }


    /**
     *  ConnectorClient Interface ****
     *
     *@param  serverAddress  Description of the Parameter
     *@exception  Exception  Description of the Exception
     */

    public void connect(ServerAddress serverAddress) throws Exception {
        if (isActive) {
            return;
        }
        if (serverAddress == null) {
            throw new IllegalArgumentException("ServerAddress cannot be null");
        }
        if (!(serverAddress instanceof RmiServerAddress)) {
            throw new IllegalArgumentException("Unexpected ServerAddress");
        }
        rmiServerAddress = (RmiServerAddress) serverAddress;
        rmiConnectorServer = (RemoteMBeanServer) Naming.lookup(rmiServerAddress.toString());
        heartBeatHandler.sendNotification(HeartBeatNotification.CONNECTED);
        heartBeatHandler.startHeartBeat();
        isActive = true;
    }


    /**
     *  Description of the Method
     */
    public void disconnect() {
        if (!isActive) {
            return;
        }
        stopListeners();
        heartBeatHandler.stopHeartBeat();
        heartBeatHandler.sendNotification(HeartBeatNotification.DISCONNECTED);
        rmiConnectorServer = null;
        rmiServerAddress = null;
        isActive = false;
    }


    /**
     *  Description of the Method
     */
    private void stopListeners() {
        if (localListeners == null) {
            return;
        }
        for (Enumeration l = localListeners.keys(); l.hasMoreElements(); ) {
            try {
                Vector key = (Vector) l.nextElement();
                removeNotificationListener((ObjectName) key.get(0), (NotificationListener) key.get(1));
            } catch (Exception e) {}
        }
        localListeners.clear();
    }


    /**
     *  Gets the serverAddress attribute of the RmiConnectorClient object
     *
     *@return    The serverAddress value
     */
    public ServerAddress getServerAddress() {
        return rmiServerAddress;
    }


    /**
     *  Gets the active attribute of the RmiConnectorClient object
     *
     *@return    The active value
     */
    public boolean isActive() {
        return isActive;
    }


    /**
     *  HeartBeatHandler Interface ****
     *
     *@param  notifListener  The feature to be added to the
     *      HeartBeatNotificationListener attribute
     *@param  notifFilter    The feature to be added to the
     *      HeartBeatNotificationListener attribute
     *@param  handback       The feature to be added to the
     *      HeartBeatNotificationListener attribute
     */

    public void addHeartBeatNotificationListener(NotificationListener notifListener, NotificationFilter notifFilter, Object handback) {
        heartBeatHandler.addHeartBeatNotificationListener(notifListener, notifFilter, handback);
    }


    /**
     *  Gets the heartBeatPeriod attribute of the RmiConnectorClient object
     *
     *@return    The heartBeatPeriod value
     */
    public int getHeartBeatPeriod() {
        return heartBeatHandler.getHeartBeatPeriod();
    }


    /**
     *  Gets the heartBeatRetries attribute of the RmiConnectorClient object
     *
     *@return    The heartBeatRetries value
     */
    public int getHeartBeatRetries() {
        return heartBeatHandler.getHeartBeatRetries();
    }


    /**
     *  Description of the Method
     *
     *@param  notifListener  Description of the Parameter
     */
    public void removeHeartBeatNotificationListener(NotificationListener notifListener) {
        heartBeatHandler.removeHeartBeatNotificationListener(notifListener);
    }


    /**
     *  Sets the heartBeatPeriod attribute of the RmiConnectorClient object
     *
     *@param  p  The new heartBeatPeriod value
     */
    public void setHeartBeatPeriod(int p) {
        heartBeatHandler.setHeartBeatPeriod(p);
    }


    /**
     *  Sets the heartBeatRetries attribute of the RmiConnectorClient object
     *
     *@param  r  The new heartBeatRetries value
     */
    public void setHeartBeatRetries(int r) {
        heartBeatHandler.setHeartBeatRetries(r);
    }


    /**
     *  Gets the running attribute of the RmiConnectorClient object
     *
     *@return    The running value
     */
    public boolean isRunning() {
        return heartBeatHandler.isRunning();
    }


    /**
     *  Description of the Method
     */
    public void startHeartBeat() {
        heartBeatHandler.startHeartBeat();
    }


    /**
     *  Description of the Method
     */
    public void stopHeartBeat() {
        heartBeatHandler.stopHeartBeat();
    }


    /***  MBeanServer Interface ***/

    /**
     *@param  name                            The feature to be added to the
     *      NotificationListener attribute
     *@param  listener                        The feature to be added to the
     *      NotificationListener attribute
     *@param  filter                          The feature to be added to the
     *      NotificationListener attribute
     *@param  handback                        The feature to be added to the
     *      NotificationListener attribute
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */

    public void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws RuntimeConnectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            RemoteNotificationListenerWrapper remoteListener = new RemoteNotificationListenerWrapper(listener);
            Vector key = new Vector(2);
            key.addElement(name);
            key.addElement(listener);
            if (localListeners == null) {
                localListeners = new Hashtable();
            }
            localListeners.put(key, remoteListener);
            UnicastRemoteObject.exportObject(remoteListener);
            rmiConnectorServer.addNotificationListener(name, remoteListener, filter, handback);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Adds a feature to the NotificationListener attribute of the
     *  RmiConnectorClient object
     *
     *@param  name                            The feature to be added to the
     *      NotificationListener attribute
     *@param  listener                        The feature to be added to the
     *      NotificationListener attribute
     *@param  filter                          The feature to be added to the
     *      NotificationListener attribute
     *@param  handback                        The feature to be added to the
     *      NotificationListener attribute
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public void addNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws RuntimeConnectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            rmiConnectorServer.addNotificationListener(name, listener, filter, handback);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@param  listener                        Description of the Parameter
     *@exception  ListenerNotFoundException   Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public void removeNotificationListener(ObjectName name, NotificationListener listener) throws RuntimeConnectionException, ListenerNotFoundException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        Vector key = new Vector(2);
        key.addElement(name);
        key.addElement(listener);
        Object obj = localListeners.remove(key);
        if (obj == null) {
            return;
        }
        RemoteNotificationListenerWrapper remoteNotificationListener = (RemoteNotificationListenerWrapper) obj;
        try {
            rmiConnectorServer.removeNotificationListener(name, remoteNotificationListener);
            UnicastRemoteObject.unexportObject(remoteNotificationListener, true);
            remoteNotificationListener.stopListen();
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@param  listener                        Description of the Parameter
     *@exception  ListenerNotFoundException   Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public void removeNotificationListener(ObjectName name, ObjectName listener) throws RuntimeConnectionException, ListenerNotFoundException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            rmiConnectorServer.removeNotificationListener(name, listener);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                       Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     */
    public Object instantiate(String className) throws RuntimeConnectionException, MBeanException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.instantiate(className);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                       Description of the Parameter
     *@param  loaderName                      Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  MBeanException              Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     */
    public Object instantiate(String className, ObjectName loaderName) throws RuntimeConnectionException, MBeanException, InstanceNotFoundException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.instantiate(className, loaderName);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                       Description of the Parameter
     *@param  params                          Description of the Parameter
     *@param  signature                       Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     */
    public Object instantiate(String className, Object[] params, String[] signature) throws RuntimeConnectionException, MBeanException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.instantiate(className, params, signature);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                       Description of the Parameter
     *@param  loaderName                      Description of the Parameter
     *@param  params                          Description of the Parameter
     *@param  signature                       Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  MBeanException              Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     */
    public Object instantiate(String className, ObjectName loaderName, Object[] params, String[] signature) throws RuntimeConnectionException, MBeanException, InstanceNotFoundException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.instantiate(className, loaderName, params, signature);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                           Description of the Parameter
     *@param  name                                Description of the Parameter
     *@return                                     Description of the Return
     *      Value
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  RuntimeConnectionException      Description of the Exception
     *@exception  ReflectionException             Description of the Exception
     */
    public ObjectInstance createMBean(String className, ObjectName name) throws RuntimeConnectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.createMBean(className, name);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                           Description of the Parameter
     *@param  name                                Description of the Parameter
     *@param  loaderName                          Description of the Parameter
     *@return                                     Description of the Return
     *      Value
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  InstanceNotFoundException       Description of the Exception
     *@exception  RuntimeConnectionException      Description of the Exception
     *@exception  ReflectionException             Description of the Exception
     */
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName) throws RuntimeConnectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.createMBean(className, name, loaderName);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                           Description of the Parameter
     *@param  name                                Description of the Parameter
     *@param  params                              Description of the Parameter
     *@param  signature                           Description of the Parameter
     *@return                                     Description of the Return
     *      Value
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  RuntimeConnectionException      Description of the Exception
     *@exception  ReflectionException             Description of the Exception
     */
    public ObjectInstance createMBean(String className, ObjectName name, Object[] params, String[] signature) throws RuntimeConnectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.createMBean(className, name, params, signature);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                           Description of the Parameter
     *@param  name                                Description of the Parameter
     *@param  loaderName                          Description of the Parameter
     *@param  params                              Description of the Parameter
     *@param  signature                           Description of the Parameter
     *@return                                     Description of the Return
     *      Value
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  InstanceNotFoundException       Description of the Exception
     *@exception  RuntimeConnectionException      Description of the Exception
     *@exception  ReflectionException             Description of the Exception
     */
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object[] params, String[] signature) throws RuntimeConnectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.createMBean(className, name, loaderName, params, signature);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  object                              Description of the Parameter
     *@param  name                                Description of the Parameter
     *@return                                     Description of the Return
     *      Value
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  RuntimeConnectionException      Description of the Exception
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     */
    public ObjectInstance registerMBean(Object object, ObjectName name) throws RuntimeConnectionException, MBeanRegistrationException, NotCompliantMBeanException, InstanceAlreadyExistsException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.registerMBean(object, name);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@exception  MBeanRegistrationException  Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public void unregisterMBean(ObjectName name) throws RuntimeConnectionException, MBeanRegistrationException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            rmiConnectorServer.unregisterMBean(name);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Gets the objectInstance attribute of the RmiConnectorClient object
     *
     *@param  name                            Description of the Parameter
     *@return                                 The objectInstance value
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public ObjectInstance getObjectInstance(ObjectName name) throws RuntimeConnectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.getObjectInstance(name);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@param  query                           Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  RuntimeConnectionException  Description of the Exception
     */
    public Set queryMBeans(ObjectName name, QueryExp query) throws RuntimeConnectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.queryMBeans(name, query);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@param  query                           Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  RuntimeConnectionException  Description of the Exception
     */
    public Set queryNames(ObjectName name, QueryExp query) throws RuntimeConnectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.queryNames(name, query);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Gets the registered attribute of the RmiConnectorClient object
     *
     *@param  name                            Description of the Parameter
     *@return                                 The registered value
     *@exception  RuntimeConnectionException  Description of the Exception
     */
    public boolean isRegistered(ObjectName name) throws RuntimeConnectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.isRegistered(name);
        } catch (RemoteException re) {
            return false;
        }
    }


    /**
     *  Gets the mBeanCount attribute of the RmiConnectorClient object
     *
     *@return                                 The mBeanCount value
     *@exception  RuntimeConnectionException  Description of the Exception
     */
    public Integer getMBeanCount() throws RuntimeConnectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.getMBeanCount();
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Gets the attribute attribute of the RmiConnectorClient object
     *
     *@param  name                            Description of the Parameter
     *@param  attribute                       Description of the Parameter
     *@return                                 The attribute value
     *@exception  AttributeNotFoundException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  MBeanException              Description of the Exception
     */
    public Object getAttribute(ObjectName name, String attribute) throws RuntimeConnectionException, MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.getAttribute(name, attribute);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Gets the attributes attribute of the RmiConnectorClient object
     *
     *@param  name                            Description of the Parameter
     *@param  attributes                      Description of the Parameter
     *@return                                 The attributes value
     *@exception  ReflectionException         Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public AttributeList getAttributes(ObjectName name, String[] attributes) throws RuntimeConnectionException, ReflectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.getAttributes(name, attributes);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Sets the attribute attribute of the RmiConnectorClient object
     *
     *@param  name                                The new attribute value
     *@param  attribute                           The new attribute value
     *@exception  AttributeNotFoundException      Description of the Exception
     *@exception  InvalidAttributeValueException  Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  ReflectionException             Description of the Exception
     *@exception  RuntimeConnectionException      Description of the Exception
     *@exception  InstanceNotFoundException       Description of the Exception
     */
    public void setAttribute(ObjectName name, Attribute attribute) throws RuntimeConnectionException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            rmiConnectorServer.setAttribute(name, attribute);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Sets the attributes attribute of the RmiConnectorClient object
     *
     *@param  name                            The new attributes value
     *@param  attributes                      The new attributes value
     *@return                                 Description of the Return Value
     *@exception  ReflectionException         Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public AttributeList setAttributes(ObjectName name, AttributeList attributes) throws RuntimeConnectionException, ReflectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.setAttributes(name, attributes);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@param  operationName                   Description of the Parameter
     *@param  params                          Description of the Parameter
     *@param  signature                       Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  MBeanException              Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws RuntimeConnectionException, MBeanException, ReflectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.invoke(name, operationName, params, signature);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Gets the defaultDomain attribute of the RmiConnectorClient object
     *
     *@return                                 The defaultDomain value
     *@exception  RuntimeConnectionException  Description of the Exception
     */
    public String getDefaultDomain() throws RuntimeConnectionException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.getDefaultDomain();
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Gets the mBeanInfo attribute of the RmiConnectorClient object
     *
     *@param  name                            Description of the Parameter
     *@return                                 The mBeanInfo value
     *@exception  IntrospectionException      Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public MBeanInfo getMBeanInfo(ObjectName name) throws RuntimeConnectionException, IntrospectionException, ReflectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.getMBeanInfo(name);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Gets the instanceOf attribute of the RmiConnectorClient object
     *
     *@param  name                            Description of the Parameter
     *@param  className                       Description of the Parameter
     *@return                                 The instanceOf value
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     */
    public boolean isInstanceOf(ObjectName name, String className) throws RuntimeConnectionException, InstanceNotFoundException {
        if (!isActive) {
            throw new RuntimeConnectionException("ClientConnector not connected");
        }
        try {
            return rmiConnectorServer.isInstanceOf(name, className);
        } catch (RemoteException re) {
            throw new RuntimeConnectionException(re, re.getMessage());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@param  data                            Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  OperationsException         Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     */
    public ObjectInputStream deserialize(ObjectName name, byte[] data) throws RuntimeConnectionException, OperationsException {
        throw new java.lang.UnsupportedOperationException("Method not supported remotely");
//        if (!isActive) {
//            throw new RuntimeConnectionException("ClientConnector not connected");
//        }
//        try {
//            return rmiConnectorServer.deserialize(name, data);
//        } catch (RemoteException re) {
//            throw new RuntimeConnectionException(re, re.getMessage());
//        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                       Description of the Parameter
     *@param  data                            Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  ReflectionException         Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     *@exception  OperationsException         Description of the Exception
     */
    public ObjectInputStream deserialize(String className, byte[] data) throws RuntimeConnectionException, OperationsException, ReflectionException {
        throw new java.lang.UnsupportedOperationException("Method not supported remotely");
//        if (!isActive) {
//            throw new RuntimeConnectionException("ClientConnector not connected");
//        }
//        try {
//            return rmiConnectorServer.deserialize(className, data);
//        } catch (RemoteException re) {
//            throw new RuntimeConnectionException(re, re.getMessage());
//        }
    }


    /**
     *  Description of the Method
     *
     *@param  className                       Description of the Parameter
     *@param  loaderName                      Description of the Parameter
     *@param  data                            Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  OperationsException         Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     *@exception  RuntimeConnectionException  Description of the Exception
     */
    public ObjectInputStream deserialize(String className, ObjectName loaderName, byte[] data) throws RuntimeConnectionException, OperationsException, ReflectionException {
        throw new java.lang.UnsupportedOperationException("Method not supported remotely");
//        if (!isActive) {
//            throw new RuntimeConnectionException("ClientConnector not connected");
//        }
//        try {
//            return rmiConnectorServer.deserialize(className, loaderName, data);
//        } catch (RemoteException re) {
//            throw new RuntimeConnectionException(re, re.getMessage());
//        }
    }

    public String [] getDomains() {
    	return new String[0];
    }
    
    public ClassLoaderRepository getClassLoaderRepository() {
    	return null;
    }

 	public void removeNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws
InstanceNotFoundException, ListenerNotFoundException { 
       
    }

 public ClassLoader getClassLoaderFor(ObjectName mbeanName) throws
InstanceNotFoundException {
        return null; 

    }

    public ClassLoader getClassLoader(ObjectName loaderName) throws
InstanceNotFoundException {
        return null; 
    }
    
 public void removeNotificationListener(ObjectName name,
NotificationListener listener, NotificationFilter filter, Object
handback) throws InstanceNotFoundException, ListenerNotFoundException {
    }
    
}
