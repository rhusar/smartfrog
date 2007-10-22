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

package org.smartfrog.services.jmx.mbean;

import javax.management.MBeanServer;
import javax.management.DynamicMBean;
import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.Attribute;
import javax.management.NotificationBroadcaster;
import javax.management.ObjectName;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.MBeanNotificationInfo;
import javax.management.ListenerNotFoundException;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import org.smartfrog.services.jmx.communication.ConnectorClient;
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
public class MBeanProxy implements DynamicMBean, NotificationBroadcaster {

    ObjectName remoteName;

    MBeanServer mbeanServer;


    // Represents the remote MBeanServer locally

    /**
     *  Constructor for the MBeanProxy object
     *
     *@param  name    Description of the Parameter
     *@param  server  Description of the Parameter
     */
    public MBeanProxy(ObjectName name, MBeanServer server) {
        remoteName = name;
        mbeanServer = server;
    }


    /**
     *  Gets the attribute attribute of the MBeanProxy object
     *
     *@param  attribute                       Description of the Parameter
     *@return                                 The attribute value
     *@exception  AttributeNotFoundException  Description of the Exception
     *@exception  MBeanException              Description of the Exception
     *@exception  ReflectionException         Description of the Exception
     */
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        try {
            return mbeanServer.getAttribute(remoteName, attribute);
        } catch (RuntimeConnectionException ce) {
            throw new MBeanException(ce);
        } catch (InstanceNotFoundException infe) {
            throw new MBeanException(infe);
        }
    }


    /**
     *  Sets the attribute attribute of the MBeanProxy object
     *
     *@param  attribute                           The new attribute value
     *@exception  AttributeNotFoundException      Description of the Exception
     *@exception  InvalidAttributeValueException  Description of the Exception
     *@exception  MBeanException                  Description of the Exception
     *@exception  ReflectionException             Description of the Exception
     */
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        try {
            mbeanServer.setAttribute(remoteName, attribute);
        } catch (RuntimeConnectionException ce) {
            throw new MBeanException(ce);
        } catch (InstanceNotFoundException infe) {
            throw new MBeanException(infe);
        }
    }


    /**
     *  Gets the attributes attribute of the MBeanProxy object
     *
     *@param  attributes  Description of the Parameter
     *@return             The attributes value
     */
    public AttributeList getAttributes(String[] attributes) {
        try {
            return mbeanServer.getAttributes(remoteName, attributes);
        } catch (RuntimeConnectionException ce) {
            return null;
        } catch (InstanceNotFoundException infe) {
            return null;
        } catch (ReflectionException re) {
            return null;
        }
    }


    /**
     *  Sets the attributes attribute of the MBeanProxy object
     *
     *@param  attributes  The new attributes value
     *@return             Description of the Return Value
     */
    public AttributeList setAttributes(AttributeList attributes) {
        try {
            return mbeanServer.setAttributes(remoteName, attributes);
        } catch (RuntimeConnectionException ce) {
            return null;
        } catch (InstanceNotFoundException infe) {
            return null;
        } catch (ReflectionException re) {
            return null;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  actionName               Description of the Parameter
     *@param  params                   Description of the Parameter
     *@param  signature                Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  MBeanException       Description of the Exception
     *@exception  ReflectionException  Description of the Exception
     */
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        try {
            return mbeanServer.invoke(remoteName, actionName, params, signature);
        } catch (RuntimeConnectionException ce) {
            throw new MBeanException(ce);
        } catch (InstanceNotFoundException infe) {
            throw new MBeanException(infe);
        }
    }


    /**
     *  Gets the mBeanInfo attribute of the MBeanProxy object
     *
     *@return    The mBeanInfo value
     */
    public MBeanInfo getMBeanInfo() {
        try {
            return mbeanServer.getMBeanInfo(remoteName);
        } catch (RuntimeConnectionException ce) {
            return null;
        } catch (InstanceNotFoundException infe) {
            return null;
        } catch (IntrospectionException ie) {
            return null;
        } catch (ReflectionException re) {
            return null;
        }
    }


    /**
     *  NotificationBroadcaster Interface ****
     *
     *@param  listener                                The feature to be added to
     *      the NotificationListener attribute
     *@param  filter                                  The feature to be added to
     *      the NotificationListener attribute
     *@param  handback                                The feature to be added to
     *      the NotificationListener attribute
     *@exception  java.lang.IllegalArgumentException  Description of the
     *      Exception
     */

    /**
     *  Adds a listener to a registered MBean.
     *
     *@param  listener                                The listener object which
     *      will handle the notifications emitted by the registered MBean.
     *@param  filter                                  The filter object. If
     *      filter is null, no filtering will be performed before handling
     *      notifications.
     *@param  handback                                An opaque object to be
     *      sent back to the listener when a notification is emitted. This
     *      object cannot be used by the Notification broadcaster object. It
     *      should be resent unchanged with the notification to the listener.
     *@exception  java.lang.IllegalArgumentException  Description of the
     *      Exception
     */
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws java.lang.IllegalArgumentException {
        try {
            if (mbeanServer.isInstanceOf(remoteName, "javax.management.NotificationBroadcaster")) {
                mbeanServer.addNotificationListener(remoteName, listener, filter, handback);
            }
        } catch (RuntimeConnectionException ce) {} catch (InstanceNotFoundException infe) {}
    }


    /**
     *  Removes a listener from a registered MBean.
     *
     *@param  listener                       The listener object which will
     *      handle the notifications emitted by the registered MBean. This
     *      method will remove all the information related to this listener.
     *@exception  ListenerNotFoundException  The listener is not registered in
     *      the MBean.
     */
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        try {
            if (mbeanServer.isInstanceOf(remoteName, "javax.management.NotificationBroadcaster")) {
                mbeanServer.removeNotificationListener(remoteName, listener);
            }
        } catch (RuntimeConnectionException ce) {} catch (InstanceNotFoundException infe) {}
    }


    /**
     *  Returns a NotificationInfo object contaning the name of the Java class
     *  of the notification and the notification types sent.
     *
     *@return    The notificationInfo value
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        try {
            if (mbeanServer.isInstanceOf(remoteName, "javax.management.NotificationBroadcaster")) {
                return (MBeanNotificationInfo[]) mbeanServer.invoke(remoteName, "getNotificationInfo", new Object[]{}, new String[]{});
            }
        } catch (RuntimeConnectionException ce) {} catch (MBeanException mbe) {} catch (ReflectionException re) {} catch (InstanceNotFoundException infe) {}
        return null;
    }
}
