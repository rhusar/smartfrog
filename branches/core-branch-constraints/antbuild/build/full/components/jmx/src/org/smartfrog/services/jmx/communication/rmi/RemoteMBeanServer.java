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

import java.rmi.*;
import java.util.Set;
import java.io.ObjectInputStream;
import javax.management.*;
import org.smartfrog.services.jmx.notification.RemoteNotificationListener;

/**
 *  Description of the Interface
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public interface RemoteMBeanServer extends Remote {

    /**
     *  Description of the Method
     *
     *@param  clientID             Description of the Parameter
     *@param  period               Description of the Parameter
     *@param  retries              Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     */
    public String heartBeatPing(String clientID, int period, int retries) throws RemoteException;


    /**
     *  Adds a feature to the NotificationListener attribute of the
     *  RemoteMBeanServer object
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
    public void addNotificationListener(ObjectName name, RemoteNotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, RemoteException;


    /**
     *  Adds a feature to the NotificationListener attribute of the
     *  RemoteMBeanServer object
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
    public void addNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, RemoteException;


    /**
     *  Description of the Method
     *
     *@param  name                           Description of the Parameter
     *@param  listener                       Description of the Parameter
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  ListenerNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public void removeNotificationListener(ObjectName name, RemoteNotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;


    /**
     *  Description of the Method
     *
     *@param  name                           Description of the Parameter
     *@param  listener                       Description of the Parameter
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  ListenerNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public void removeNotificationListener(ObjectName name, ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;


    /**
     *  Description of the Method
     *
     *@param  className                Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  ReflectionException  Description of the Exception
     *@exception  MBeanException       Description of the Exception
     *@exception  RemoteException      Description of the Exception
     */
    public Object instantiate(String className) throws ReflectionException, MBeanException, RemoteException;


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
    public Object instantiate(String className, ObjectName loaderName) throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException;


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
    public Object instantiate(String className, Object params[], String signature[]) throws ReflectionException, MBeanException, RemoteException;


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
    public Object instantiate(String className, ObjectName loaderName, Object params[], String signature[]) throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException;


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
    public ObjectInstance createMBean(String className, ObjectName name) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, RemoteException;


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
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException;


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
    public ObjectInstance createMBean(String className, ObjectName name, Object params[], String signature[]) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, RemoteException;


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
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object params[], String signature[]) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException;


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
    public ObjectInstance registerMBean(Object object, ObjectName name) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, RemoteException;


    /**
     *  Description of the Method
     *
     *@param  name                            Description of the Parameter
     *@exception  InstanceNotFoundException   Description of the Exception
     *@exception  MBeanRegistrationException  Description of the Exception
     *@exception  RemoteException             Description of the Exception
     */
    public void unregisterMBean(ObjectName name) throws InstanceNotFoundException, MBeanRegistrationException, RemoteException;


    /**
     *  Gets the objectInstance attribute of the RemoteMBeanServer object
     *
     *@param  name                           Description of the Parameter
     *@return                                The objectInstance value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public ObjectInstance getObjectInstance(ObjectName name) throws InstanceNotFoundException, RemoteException;


    /**
     *  Description of the Method
     *
     *@param  name                 Description of the Parameter
     *@param  query                Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     */
    public Set queryMBeans(ObjectName name, QueryExp query) throws RemoteException;


    /**
     *  Description of the Method
     *
     *@param  name                 Description of the Parameter
     *@param  query                Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     */
    public Set queryNames(ObjectName name, QueryExp query) throws RemoteException;


    /**
     *  Gets the registered attribute of the RemoteMBeanServer object
     *
     *@param  name                 Description of the Parameter
     *@return                      The registered value
     *@exception  RemoteException  Description of the Exception
     */
    public boolean isRegistered(ObjectName name) throws RemoteException;


    /**
     *  Gets the mBeanCount attribute of the RemoteMBeanServer object
     *
     *@return                      The mBeanCount value
     *@exception  RemoteException  Description of the Exception
     */
    public Integer getMBeanCount() throws RemoteException;


    /**
     *  Gets the attribute attribute of the RemoteMBeanServer object
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
    public Object getAttribute(ObjectName name, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, RemoteException;


    /**
     *  Gets the attributes attribute of the RemoteMBeanServer object
     *
     *@param  name                           Description of the Parameter
     *@param  attributes                     Description of the Parameter
     *@return                                The attributes value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  ReflectionException        Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public AttributeList getAttributes(ObjectName name, String[] attributes) throws InstanceNotFoundException, ReflectionException, RemoteException;


    /**
     *  Sets the attribute attribute of the RemoteMBeanServer object
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
    public void setAttribute(ObjectName name, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, RemoteException;


    /**
     *  Sets the attributes attribute of the RemoteMBeanServer object
     *
     *@param  name                           The new attributes value
     *@param  attributes                     The new attributes value
     *@return                                Description of the Return Value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  ReflectionException        Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public AttributeList setAttributes(ObjectName name, AttributeList attributes) throws InstanceNotFoundException, ReflectionException, RemoteException;


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
    public Object invoke(ObjectName name, String operationName, Object params[], String signature[]) throws InstanceNotFoundException, MBeanException, ReflectionException, RemoteException;


    /**
     *  Gets the defaultDomain attribute of the RemoteMBeanServer object
     *
     *@return                      The defaultDomain value
     *@exception  RemoteException  Description of the Exception
     */
    public String getDefaultDomain() throws RemoteException;


    /**
     *  Gets the mBeanInfo attribute of the RemoteMBeanServer object
     *
     *@param  name                           Description of the Parameter
     *@return                                The mBeanInfo value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  IntrospectionException     Description of the Exception
     *@exception  ReflectionException        Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public MBeanInfo getMBeanInfo(ObjectName name) throws InstanceNotFoundException, IntrospectionException, ReflectionException, RemoteException;


    /**
     *  Gets the instanceOf attribute of the RemoteMBeanServer object
     *
     *@param  name                           Description of the Parameter
     *@param  className                      Description of the Parameter
     *@return                                The instanceOf value
     *@exception  InstanceNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public boolean isInstanceOf(ObjectName name, String className) throws InstanceNotFoundException, RemoteException;

}
