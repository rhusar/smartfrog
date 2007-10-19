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

import java.util.Set;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.ObjectInputStream;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.net.InetAddress;

import javax.management.*;
import org.smartfrog.services.jmx.communication.ObjectID;
import org.smartfrog.services.jmx.notification.RemoteNotificationListener;
import org.smartfrog.services.jmx.notification.NotificationListenerWrapper;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class RmiMBeanServerInvoker extends UnicastRemoteObject implements RemoteMBeanServer {

    private RmiConnectorServer rmiConnectorServer;

    private String mbeanServerId;

    private Hashtable objectBunch;

    private Hashtable remoteListeners;

    private Hashtable remoteClients;

    private static long clientNumber = 0;

    private int port;

    private String serviceName;

    private String url;

    private boolean isActive = false;


    /**
     *  Constructor for the RmiConnectorProxy object
     *
     *@param  rmiconnectorserver   Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public RmiMBeanServerInvoker(RmiConnectorServer rmiconnectorserver) throws RemoteException, Exception {
        rmiConnectorServer = rmiconnectorserver;
        mbeanServerId = (String) rmiConnectorServer.getMBeanServer().getAttribute(new ObjectName("JMImplementation:type=MBeanServerDelegate"), "MBeanServerId");
    }


    /**
     *  Description of the Method
     */
    void bind() {
        try {
            // Build URL
            port = rmiConnectorServer.getPort();
            serviceName = rmiConnectorServer.getServiceName();
            String host;
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                host = "localhost";
            }
            url = buildURL(host, port, serviceName);

            // Bind this object
            try {
                Naming.bind(url, this);
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(port);
                Naming.bind(url, this);
            }
            isActive = true;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    /**
     *  Description of the Method
     */
    void unbind() {
        try {
            Naming.unbind(url);
            rmiConnectorServer = null;
            isActive = false;
        } catch (Exception e) {}
    }


    /**
     *  Gets the active attribute of the RmiConnectorProxy object
     *
     *@return    The active value
     */
    boolean isActive() {
        return isActive;
    }


    /**
     *  Description of the Method
     *
     *@param  host         Description of the Parameter
     *@param  port         Description of the Parameter
     *@param  serviceName  Description of the Parameter
     *@return              Description of the Return Value
     */
    private String buildURL(String host, int port, String serviceName) {
        return "rmi://" + host + ":" + port + "/" + serviceName;
    }


    /**
     *  Description of the Method
     */
    void stopListeners() {
        if (remoteListeners != null) {
            for (Enumeration e = remoteListeners.keys(); e.hasMoreElements(); ) {
                NotificationListenerWrapper listener = (NotificationListenerWrapper) remoteListeners.remove(e.nextElement());
                listener.stopListen();
            }
            if (!remoteListeners.isEmpty()) {
                remoteListeners.clear();
            }
            remoteListeners = null;
        }
        if (remoteClients != null) {
            for (Enumeration e = remoteClients.keys(); e.hasMoreElements(); ) {
                ClientInfo client = (ClientInfo) remoteClients.remove(e.nextElement());
                client.interrupt();
            }
            if (!remoteClients.isEmpty()) {
                remoteClients.clear();
            }
            remoteClients = null;
        }
    }


    /**
     *  This method suppose that both machines are closely synchronized to
     *  compute the timeout
     *
     *@param  clientID             Description of the Parameter
     *@param  period               Description of the Parameter
     *@param  retries              Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     */
    public String heartBeatPing(String clientID, int period, int retries) throws RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        if (remoteClients == null) {
            remoteClients = new Hashtable();
        }
        long currentTime = System.currentTimeMillis();
        ClientInfo client;
        if (clientID == null) {
            clientID = mbeanServerId + "_" + clientNumber;
            client = new ClientInfo(clientID, period, retries, currentTime);
            if (++clientNumber == 0x7fffffffffffffffL) {
                clientNumber = 0;
            }
        } else {
            client = (ClientInfo) remoteClients.get(clientID);
            if (client != null) {
                client.setNewInfo(period, retries, currentTime);
            } else {
                return null;
            }
        }
        remoteClients.put(clientID, client);
        return clientID;
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
    class ClientInfo extends Thread {
        int period = 0;
        int retries = 0;
        long lastCurrentTime;
        long timeout;
        String clientID;


        /**
         *  Constructor for the ClientInfo object
         *
         *@param  clid         Description of the Parameter
         *@param  period       Description of the Parameter
         *@param  retries      Description of the Parameter
         *@param  currentTime  Description of the Parameter
         */
        public ClientInfo(String clid, int period, int retries, long currentTime) {
            clientID = clid;
            setNewInfo(period, retries, currentTime);
        }


        /**
         *  Sets the newInfo attribute of the ClientInfo object
         *
         *@param  p   The new newInfo value
         *@param  r   The new newInfo value
         *@param  ct  The new newInfo value
         */
        public void setNewInfo(int p, int r, long ct) {
            period = p;
            retries = r;
            lastCurrentTime = ct;
            // Calculate timeout estimating Internet averaget delay 100 ms
            if (retries == 0) {
                timeout = 100 + (long) ((double) period + 1.2 * (double) period);
            } else {
                timeout = 100 + (long) ((double) period + 1.2 * (double) (period * retries));
            }
            if (period > 0 && !isAlive()) {
                start();
            } else if (period <= 0) {
                this.interrupt();
            }
        }


        /**
         *  Main processing method for the ClientInfo object
         */
        public void run() {
            while (period > 0) {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ie) {
                    break;
                }
                if (System.currentTimeMillis() > (lastCurrentTime + timeout)) {
                    System.out.println("RmiConnectorServer '" + serviceName + "': Client " + clientID + " stopped pinging.");
                    remoteClients.remove(clientID);
                    break;
                }
            }
        }
    }


    /**
     *  Methods to handle objects through the network ****
     *
     *@param  object  Description of the Parameter
     *@return         Description of the Return Value
     */

    /**
     *  This method creates an ObjectID for an object and stored it in a
     *  Hashtable for future uses.
     *
     *@param  object  Description of the Parameter
     *@return         Description of the Return Value
     */
    private ObjectID createObjectID(Object object) {
        ObjectID objectId = new ObjectID(mbeanServerId, object);
        if (objectBunch == null) {
            objectBunch = new Hashtable();
        }
        objectBunch.put(objectId, object);
        return objectId;
    }


    /**
     *  Description of the Method
     *
     *@param  objects     Description of the Parameter
     *@param  signatures  Description of the Parameter
     */
    private void checkForObjectID(Object[] objects, String[] signatures) {
        for (int i = 0; i < objects.length; i++) {
            Object realObject = checkIfObjectID(objects[i]);
            if (objects[i] != realObject) {
                // Replace the Object Handler by the effective object
                objects[i] = realObject;
                if (i < signatures.length && signatures[i].equals(ObjectID.class.getName())) {
                    signatures[i] = realObject.getClass().getName();
                }
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  object  Description of the Parameter
     *@return         Description of the Return Value
     */
    private Object checkIfObjectID(Object object) {
        if (object instanceof ObjectID) {
            return objectBunch.get(object);
        } else {
            return object;
        }
    }


    /**
     *  RemoteMBeanServer Interface ****
     *
     *@param  name                           The feature to be added to the
     *      NotificationListener attribute
     *@param  remotelistener                 The feature to be added to the
     *      NotificationListener attribute
     *@param  filter                         The feature to be added to the
     *      NotificationListener attribute
     *@param  handback                       The feature to be added to the
     *      NotificationListener attribute
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */

    public void addNotificationListener(ObjectName name, RemoteNotificationListener remotelistener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        NotificationListenerWrapper listenerWrapper = new NotificationListenerWrapper(remotelistener);
        rmiConnectorServer.getMBeanServer().addNotificationListener(name, listenerWrapper, filter, handback);
        if (remoteListeners == null) {
            remoteListeners = new Hashtable();
        }
        remoteListeners.put(remotelistener, listenerWrapper);
    }


    /**
     *  Adds a feature to the NotificationListener attribute of the
     *  RmiConnectorProxy object
     *
     *@param  name                           The feature to be added to the
     *      NotificationListener attribute
     *@param  listener                       The feature to be added to the
     *      NotificationListener attribute
     *@param  filter                         The feature to be added to the
     *      NotificationListener attribute
     *@param  handback                       The feature to be added to the
     *      NotificationListener attribute
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public void addNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        rmiConnectorServer.getMBeanServer().addNotificationListener(name, listener, filter, handback);
    }


    /**
     *  Description of the Method
     *
     *@param  name                           Description of the Parameter
     *@param  remoteListener                 Description of the Parameter
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  ListenerNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public void removeNotificationListener(ObjectName name, RemoteNotificationListener remoteListener) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        Object obj = remoteListeners.remove(remoteListener);
        if (obj == null) {
            return;
        }
        NotificationListenerWrapper listenerWrapper = (NotificationListenerWrapper) obj;
        rmiConnectorServer.getMBeanServer().removeNotificationListener(name, listenerWrapper);
        listenerWrapper.stopListen();
    }


    /**
     *  Description of the Method
     *
     *@param  name                           Description of the Parameter
     *@param  listener                       Description of the Parameter
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  ListenerNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public void removeNotificationListener(ObjectName name, ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        rmiConnectorServer.getMBeanServer().removeNotificationListener(name, listener);
    }


    /**
     *  Description of the Method
     *
     *@param  className                Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  ReflectionException  Description of the Exception
     *@exception  MBeanException       Description of the Exception
     *@exception  RemoteException      Description of the Exception
     */
    public Object instantiate(String className) throws ReflectionException, MBeanException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return createObjectID(rmiConnectorServer.getMBeanServer().instantiate(className));
    }


    /**
     *  Description of the Method
     *
     *@param  className                      Description of the Parameter
     *@param  loaderName                     Description of the Parameter
     *@return                                Description of the Return Value
     *@exception  ReflectionException        Description of the Exception
     *@exception  MBeanException             Description of the Exception
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public Object instantiate(String className, ObjectName loaderName) throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return createObjectID(rmiConnectorServer.getMBeanServer().instantiate(className, loaderName));
    }


    /**
     *  Description of the Method
     *
     *@param  className                Description of the Parameter
     *@param  params                   Description of the Parameter
     *@param  signature                Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  ReflectionException  Description of the Exception
     *@exception  MBeanException       Description of the Exception
     *@exception  RemoteException      Description of the Exception
     */
    public Object instantiate(String className, Object[] params, String[] signature) throws ReflectionException, MBeanException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        checkForObjectID(params, signature);
        return createObjectID(rmiConnectorServer.getMBeanServer().instantiate(className, params, signature));
    }


    /**
     *  Description of the Method
     *
     *@param  className                      Description of the Parameter
     *@param  loaderName                     Description of the Parameter
     *@param  params                         Description of the Parameter
     *@param  signature                      Description of the Parameter
     *@return                                Description of the Return Value
     *@exception  ReflectionException        Description of the Exception
     *@exception  MBeanException             Description of the Exception
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public Object instantiate(String className, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        checkForObjectID(params, signature);
        return createObjectID(rmiConnectorServer.getMBeanServer().instantiate(className, loaderName, params, signature));
    }


    /**
     *  Description of the Method
     *
     *@param  className                           Description of the Parameter
     *@param  name                                Description of the Parameter
     *@return                                     Description of the Return
     *      Value
     *@exception  ReflectionException             Description of the Exception
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  RemoteException                 Description of the Exception
     */
    public ObjectInstance createMBean(String className, ObjectName name) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().createMBean(className, name);
    }


    /**
     *  Description of the Method
     *
     *@param  className                           Description of the Parameter
     *@param  name                                Description of the Parameter
     *@param  loaderName                          Description of the Parameter
     *@return                                     Description of the Return
     *      Value
     *@exception  ReflectionException             Description of the Exception
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  InstanceNotFoundException       Description of the Exception
     *@exception  RemoteException                 Description of the Exception
     */
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().createMBean(className, name, loaderName);
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
     *@exception  ReflectionException             Description of the Exception
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  RemoteException                 Description of the Exception
     */
    public ObjectInstance createMBean(String className, ObjectName name, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        checkForObjectID(params, signature);
        return rmiConnectorServer.getMBeanServer().createMBean(className, name, params, signature);
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
     *@exception  ReflectionException             Description of the Exception
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  InstanceNotFoundException       Description of the Exception
     *@exception  RemoteException                 Description of the Exception
     */
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        checkForObjectID(params, signature);
        return rmiConnectorServer.getMBeanServer().createMBean(className, name, loaderName, params, signature);
    }


    /**
     *  Description of the Method
     *
     *@param  object                              Description of the Parameter
     *@param  name                                Description of the Parameter
     *@return                                     Description of the Return
     *      Value
     *@exception  InstanceAlreadyExistsException  Description of the Exception
     *@exception  MBeanRegistrationException      Description of the Exception
     *@exception  NotCompliantMBeanException      Description of the Exception
     *@exception  RemoteException                 Description of the Exception
     */
    public ObjectInstance registerMBean(Object object, ObjectName name) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().registerMBean(checkIfObjectID(object), name);
    }


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@exception  InstanceNotFoundException   Description of the Exception
     *@exception  MBeanRegistrationException  Description of the Exception
     *@exception  RemoteException             Description of the Exception
     */
    public void unregisterMBean(ObjectName name) throws InstanceNotFoundException, MBeanRegistrationException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        rmiConnectorServer.getMBeanServer().unregisterMBean(name);
    }


    /**
     *  Gets the objectInstance attribute of the RmiConnectorProxy object
     *
     *@param  name                           Description of the Parameter
     *@return                                The objectInstance value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public ObjectInstance getObjectInstance(ObjectName name) throws InstanceNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().getObjectInstance(name);
    }


    /**
     *  Description of the Method
     *
     *@param  name                 Description of the Parameter
     *@param  query                Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     */
    public Set queryMBeans(ObjectName name, QueryExp query) throws RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().queryMBeans(name, query);
    }


    /**
     *  Description of the Method
     *
     *@param  name                 Description of the Parameter
     *@param  query                Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     */
    public Set queryNames(ObjectName name, QueryExp query) throws RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().queryNames(name, query);
    }


    /**
     *  Gets the registered attribute of the RmiConnectorProxy object
     *
     *@param  name                 Description of the Parameter
     *@return                      The registered value
     *@exception  RemoteException  Description of the Exception
     */
    public boolean isRegistered(ObjectName name) throws RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().isRegistered(name);
    }


    /**
     *  Gets the mBeanCount attribute of the RmiConnectorProxy object
     *
     *@return                      The mBeanCount value
     *@exception  RemoteException  Description of the Exception
     */
    public Integer getMBeanCount() throws RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().getMBeanCount();
    }


    /**
     *  Gets the attribute attribute of the RmiConnectorProxy object
     *
     *@param  name                            Description of the Parameter
     *@param  attribute                       Description of the Parameter
     *@return                                 The attribute value
     *@exception  MBeanException              Description of the Exception
     *@exception  AttributeNotFoundException  Description of the Exception
     *@exception  InstanceNotFoundException   Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     *@exception  RemoteException             Description of the Exception
     */
    public Object getAttribute(ObjectName name, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().getAttribute(name, attribute);
    }


    /**
     *  Gets the attributes attribute of the RmiConnectorProxy object
     *
     *@param  name                           Description of the Parameter
     *@param  attributes                     Description of the Parameter
     *@return                                The attributes value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  ReflectionException        Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public AttributeList getAttributes(ObjectName name, String[] attributes) throws InstanceNotFoundException, ReflectionException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().getAttributes(name, attributes);
    }


    /**
     *  Sets the attribute attribute of the RmiConnectorProxy object
     *
     *@param  name                                The new attribute value
     *@param  attribute                           The new attribute value
     *@exception  InstanceNotFoundException       Description of the Exception
     *@exception  AttributeNotFoundException      Description of the Exception
     *@exception  InvalidAttributeValueException  Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  ReflectionException             Description of the Exception
     *@exception  RemoteException                 Description of the Exception
     */
    public void setAttribute(ObjectName name, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        rmiConnectorServer.getMBeanServer().setAttribute(name, attribute);
    }


    /**
     *  Sets the attributes attribute of the RmiConnectorProxy object
     *
     *@param  name                           The new attributes value
     *@param  attributes                     The new attributes value
     *@return                                Description of the Return Value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  ReflectionException        Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public AttributeList setAttributes(ObjectName name, AttributeList attributes) throws InstanceNotFoundException, ReflectionException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().setAttributes(name, attributes);
    }


    /**
     *  Description of the Method
     *
     *@param  name                           Description of the Parameter
     *@param  operationName                  Description of the Parameter
     *@param  params                         Description of the Parameter
     *@param  signature                      Description of the Parameter
     *@return                                Description of the Return Value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  MBeanException             Description of the Exception
     *@exception  ReflectionException        Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        checkForObjectID(params, signature);
        return rmiConnectorServer.getMBeanServer().invoke(name, operationName, params, signature);
    }


    /**
     *  Gets the defaultDomain attribute of the RmiConnectorProxy object
     *
     *@return                      The defaultDomain value
     *@exception  RemoteException  Description of the Exception
     */
    public String getDefaultDomain() throws RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().getDefaultDomain();
    }


    /**
     *  Gets the mBeanInfo attribute of the RmiConnectorProxy object
     *
     *@param  name                           Description of the Parameter
     *@return                                The mBeanInfo value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  IntrospectionException     Description of the Exception
     *@exception  ReflectionException        Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public MBeanInfo getMBeanInfo(ObjectName name) throws InstanceNotFoundException, IntrospectionException, ReflectionException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().getMBeanInfo(name);
    }


    /**
     *  Gets the instanceOf attribute of the RmiConnectorProxy object
     *
     *@param  name                           Description of the Parameter
     *@param  className                      Description of the Parameter
     *@return                                The instanceOf value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public boolean isInstanceOf(ObjectName name, String className) throws InstanceNotFoundException, RemoteException {
        if (!isActive) {
            throw new RemoteException("RmiConnectorServer is not active");
        }
        return rmiConnectorServer.getMBeanServer().isInstanceOf(name, className);
    }

}
