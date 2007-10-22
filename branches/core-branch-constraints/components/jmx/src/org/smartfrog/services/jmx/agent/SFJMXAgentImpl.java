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

package org.smartfrog.services.jmx.agent;

import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.rmi.*;
import java.util.*;
import java.io.*;
import java.text.DateFormat;
import java.net.InetAddress;
import javax.management.*;
//import javax.management.modelmbean.*;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.processcompound.*;
import org.smartfrog.services.jmx.communication.ConnectionFactory;
import org.smartfrog.services.jmx.communication.ConnectorClient;
import org.smartfrog.services.jmx.common.Utilities;
//import com.sun.management.jmx.*;
import mx4j.*;
import java.net.UnknownHostException;

/**
 *  A compound that represents the remote agent
 *@version        1.0
 */
public class SFJMXAgentImpl extends CompoundImpl implements Compound, SFJMXAgent, Remote {

    private MBeanServer server;

    private Hashtable remoteListeners;

    private DiscoveryEventHandler eventHandler;

    private Vector agentNotificationListeners;

    Hashtable subAgents;
    String localhost;
    String localServerId;

    Reference nameRef;
    String name = "sfJMXAgent";
    private static final String DESCRIPTION_MBEANS = "descriptionMBeans";
    private static final String COMPONENT_MBEANS = "componentMBeans";


    /**
     *  Constructor for the SFJMXAgentImpl object
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public SFJMXAgentImpl() throws RemoteException, Exception {
        // Parse system properties to check if LEVEL_TRACE and/or LEVEL_DEBUG are set
        // and enable the TRACE level accordingly
      //  try {
            //Trace.parseTraceProperties();
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/

        // CREATE the MBeanServer
       //server = MBeanServerFactory.createMBeanServer("SmartFrog");
       
//	server = Registry.getServer();
    }


    /**
     *  Gets the serverID attribute of the SFJMXAgentImpl object
     *
     *@return    The serverID value
     */
    public String getMBeanServerId() throws RemoteException {
        return this.localServerId;
    }

    public ConnectorClient getConnectorClient() throws RemoteException, Exception {
        return ConnectionFactory.findConnectorClient(server);
    }

    /**
     *  Description of the Method
     *
     *@param  listener  Description of the Parameter
     */
    public void addAgentNotificationListener(NotificationListener listener) {
        if (agentNotificationListeners == null) {
            agentNotificationListeners = new Vector();
        }
        agentNotificationListeners.addElement(listener);
    }


    /**
     *  Description of the Method
     *
     *@param  listener  Description of the Parameter
     */
    public void removeAgentNotificationListener(NotificationListener listener) {
        if (agentNotificationListeners != null) {
            agentNotificationListeners.remove(listener);
        }
    }


    /**
     *  This method is used by the JMX Agent to send a
     *  AgentTerminatingNotification to those mbeans that needs to take specific
     *  action before the Agent is terminated.
     *
     *@param  e  Event to be handled
     */
    private void handleTerminateEvent(String e) {
        try {
            for (Enumeration l = agentNotificationListeners.elements(); l.hasMoreElements(); ) {
                NotificationListener listener = null;
                try {
                    listener = (NotificationListener) l.nextElement();
                    listener.handleNotification(new AgentTerminatingNotification(this, e), null);
                    removeAgentNotificationListener(listener);
                } catch (Exception ex) {
                    if (sfLog().isErrorEnabled()){ sfLog().error("EventHandler: Could not send AgentTerminatingNotification",ex);}
                }
            }
            agentNotificationListeners.clear();
        } catch (Exception exc) {
            return;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  msg  Description of the Parameter
     */
    private void echo(String msg) {
        if (sfLog().isInfoEnabled()){ sfLog().info(msg);}
    }


    /**
     *  Returns the MBeanServer held by this Agent
     *
     *@return    The mBeanServer value
     */
    public MBeanServer getMBeanServer() {
        return server;
    }


    /**
     *  Description of the Method
     *
     *@param  mbeanClass     Description of the Parameter
     *@param  constructor    Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    private Object intantiateMBean(String mbeanClass, ObjectName objectName, ComponentDescription constructor) throws Exception {

        echo("IN INTANTIATEMBEAN");
        // Search for constructor parameters
        Context constructorContext = null;
        if (constructor != null) {
            constructorContext = constructor.sfContext();
        } else {
            constructorContext = new ContextImpl(0, 0.75F);
        }
        Object[] parameters = new Object[constructorContext.size()];
        String[] signature = new String[constructorContext.size()];
        int i = 0;
        for (Enumeration p = constructorContext.elements(); p.hasMoreElements(); ) {
            ComponentDescription paramComp = (ComponentDescription) p.nextElement();
            Context paramContext = paramComp.sfContext();
            String paramClass = (String) paramContext.get("class");
            if ( paramClass.startsWith("[L") && paramClass.endsWith(";") ) {
                // It is an array and we expect a Vector as a value
                String singleClass = paramClass.substring(2, paramClass.lastIndexOf(';'));
                Vector vector = (Vector) paramContext.get("value");
                Object[] valueArray = (Object[]) Array.newInstance(Class.forName(singleClass), vector.size());
                for (int j = 0; j < vector.size(); j++) {
                    Object item = vector.get(j);
                    if (item instanceof Vector) {
                        valueArray[j] = Utilities.objectFromVector(singleClass, item);
                    } else {
                        valueArray[j] = Utilities.objectFromString(singleClass, item);
                    }
                }
                parameters[i] = valueArray;
            }
            else if ( paramClass.endsWith("[]") ) {
                // It is an array and we expect a Vector as a value
                String singleClass = paramClass.substring(0, paramClass.indexOf('['));
                paramClass = "[L" + singleClass + ";";
                Vector vector = (Vector) paramContext.get("value");
                Object[] valueArray = (Object[]) Array.newInstance(Class.forName(singleClass), vector.size());
                for (int j = 0; j < vector.size(); j++) {
                    Object item = vector.get(j);
                    if (item instanceof Vector) {
                        valueArray[j] = Utilities.objectFromVector(singleClass, item);
                    } else {
                        valueArray[j] = Utilities.objectFromString(singleClass, item);
                    }
                }
                parameters[i] = valueArray;
            } else {
                Object value = paramContext.get("value");
                if (value instanceof Vector) {
                    parameters[i] = Utilities.objectFromVector(paramClass, value);
                } else {
                    parameters[i] = Utilities.objectFromString(paramClass, value);
                }
                // If not a String, this method returns the same object
            }
            signature[i++] = paramClass;
        }
       // return server.instantiate(mbeanClass, parameters, signature);
        return server.createMBean(mbeanClass, objectName, null, parameters, signature);
    }


    /**
     *  Adds a feature to the DescriptionMBeans attribute of the SFJMXAgentImpl
     *  object
     */
    protected void addDescriptionMBeans() {
        Context descriptionMBeans = null;
        try {
            descriptionMBeans = ((ComponentDescription) sfResolveHere(DESCRIPTION_MBEANS)).sfContext();
        } catch (Exception e) {
            sfLog().error("Failed to resolve "+DESCRIPTION_MBEANS,e);
            return;
        }

        for (Enumeration c = descriptionMBeans.elements(); c.hasMoreElements(); ) {
            // Properties of the adaptor or connector
            String domain = null;
            Integer port = null;
            String className = null;
            boolean registerWithAgent = false;

            ComponentDescription mbeanComp = null;
            try {
                mbeanComp = (ComponentDescription) c.nextElement();
            } catch (ClassCastException cce) {
                continue;
            }
            // Context of the Adaptor or Connector
            Context mbeanContext = mbeanComp.sfContext();

            // Search for the domain
            try {
                domain = (String) mbeanContext.get("domain");
                if (domain == null || domain.length() == 0) {
                    domain = "default";
                }
                domain = (domain.equals("default") ? server.getDefaultDomain() : domain);
            } catch (ClassCastException cce) {
                domain = server.getDefaultDomain();
            }

            // Search for the port
            try {
                port = (Integer) mbeanContext.get("port");
            } catch (ClassCastException cce) {}

            // Search for the class
            try {
                className = (String) mbeanContext.get("class");
            } catch (ClassCastException cce) {
                continue;
            }

            // Search for the registerWithAgent boolean just in case the MBean needs to receive
            // Agent Notifications for synchronization
            try {
                Boolean registerWithAgentBoolean = ((Boolean) mbeanContext.get("registerWithAgent"));
                if (registerWithAgentBoolean != null) {
                    registerWithAgent = registerWithAgentBoolean.booleanValue();
                }
            } catch (Exception cce) {
                continue;
            }

            // Search for the properties
            Context properties = null;
            try {
                properties = ((ComponentDescription) mbeanContext.get("properties")).sfContext();
            } catch (Exception e) {}
            if (properties == null) {
                properties = new ContextImpl();
            }

            // Make sure that properties 'name', 'type', 'host' exist
            if (!properties.containsKey("name")) {
                properties.put("name", Utilities.getDefaultNamePropertyFor(mbeanComp));
            }
            if (!properties.containsKey("type")) {
                properties.put("type", "sf.jmx.mbean.generic");
            }
          /*  if (!properties.containsKey("server")) {
                properties.put("server", localServerId);
            }*/

            echo("Serve Id===" + localServerId);
            echo("Serve domain===" + domain);
            echo("Name===" + properties.get("name"));
            // Build ObjectName and instantiate
            Object mbeanInstance = null;
            ObjectName mbeanObjectName = null;
            try {
                mbeanObjectName = new ObjectName(domain, (Hashtable) properties);
               // mbeanObjectName = new ObjectName(domain);
                mbeanInstance = intantiateMBean(className, mbeanObjectName, (ComponentDescription) mbeanContext.get("constructor"));
            } catch (MalformedObjectNameException mone) {
                if (sfLog().isErrorEnabled()){ sfLog().error("Could not create an ObjectName for MBean: " + (String)properties.get("name"),mone);}
                continue;
            } catch (Exception e) {
                if (sfLog().isErrorEnabled()){ sfLog().error("Could not create MBean for MBean: " + (String)properties.get("name"),e);}
                continue;
            }

            // If it is a NotificationListener, we register it for Agent Notifications
            if (registerWithAgent && mbeanInstance instanceof NotificationListener) {
                addAgentNotificationListener((NotificationListener) mbeanInstance);
            }

            // Configure timer
            if (className.equals("javax.management.Timer")) {
                configureTimer(mbeanInstance, mbeanContext);
            }

            // Configure port by invoking the method setPort() if necessary, such as a Connector or Adaptor
            // It is done before registering just in case an adaptor be started during the preRegistering
            if (port != null) {
                Method setPortMethod = null;
                try {
                    setPortMethod = mbeanInstance.getClass().getMethod("setPort", new Class[]{int.class});
                } catch (Exception e) {
                    try {
                        setPortMethod = mbeanInstance.getClass().getMethod("setPort", new Class[]{Integer.class});
                    } catch (Exception ex) {
                        if (sfLog().isErrorEnabled()){ sfLog().error("MBean " + mbeanInstance.getClass().getName() + " does not have a setPort() method",ex);}
                    }
                }
                try {
                    setPortMethod.invoke(mbeanInstance, new Object[]{port});
                } catch (Exception exc) {
                    if (sfLog().isErrorEnabled()){ sfLog().error("Error invoking method setPort(): " + exc,exc);}
                }
            }
            // end if

            // Register the MBean
            try {
                //server.registerMBean(mbeanInstance, mbeanObjectName);
                if (sfLog().isDebugEnabled()){ sfLog().debug(mbeanObjectName + " registered");}
            } catch (Exception e) {
                if (sfLog().isErrorEnabled()){ sfLog().error("Could not register MBean: " + mbeanObjectName,e);}
                continue;
            }

            // Start the MBean if necessary
            String startMethod = (String) mbeanContext.get("startMethod");
            if (startMethod != null) {
                try {
                    Method start = mbeanInstance.getClass().getMethod(startMethod, new Class[]{});
                    start.invoke(mbeanInstance, new Object[]{});
                } catch (Exception e) {
                    if (sfLog().isErrorEnabled()){ sfLog().error("Could not start MBean: " + mbeanObjectName,e);}
                }
            }
        }
    }


    /**
     *  Adds a feature to the ComponentMBeans attribute of the SFJMXAgentImpl
     *  object
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    protected void addComponentMBeans() throws RemoteException, Exception {
        Compound componentMBeans = null;
        try {
            componentMBeans = ((Compound) sfResolveHere(COMPONENT_MBEANS));
        } catch (Exception e) {
            sfLog().error(COMPONENT_MBEANS,e);
            return;
        }
        for (Enumeration b = componentMBeans.sfContext().keys(); b.hasMoreElements(); ) {
            // Get the Prim component
            String beanKey = null;
            Prim mbeanInstance = null;
            try {
                beanKey = (String) b.nextElement();
                mbeanInstance = (Prim) componentMBeans.sfResolve(beanKey);
            } catch (Exception e) {
                sfLog().error("resolving "+beanKey, e);
                continue;
            }

            //Test if the property matches a compliant MBean class
        /*    try {
                MBeanIntrospector.testCompliance(mbeanInstance.getClass());
            } catch (NotCompliantMBeanException ncbe) {
                echo("Not compliant MBean. It will not be registered within the MBeanServer: " + beanKey);
                echo(ncbe.toString());
                continue;
            }
*/
            // Search for the domain
            String domain;
            try {
                domain = (String) ((Prim) mbeanInstance).sfResolve("domain");
                if (domain == null || domain.length() == 0) {
                    domain = "default";
                }
                domain = (domain.equals("default") ? server.getDefaultDomain() : domain);
            } catch (Exception e) {
                domain = server.getDefaultDomain();
            }

            // Search for the registerWithAgent boolean just in case the MBean needs to receive
            // Agent Notifications for synchronization
            boolean registerWithAgent = false;
            try {
                Boolean registerWithAgentBoolean = (Boolean) ((Prim) mbeanInstance).sfResolve("registerWithAgent");
                if (registerWithAgentBoolean != null) {
                    registerWithAgent = registerWithAgentBoolean.booleanValue();
                }
            } catch (Exception cce) {
                continue;
            }

            // Search for the properties
            Context properties = null;
            try {
                properties = ((ComponentDescription) mbeanInstance.sfResolve("properties")).sfContext();
            } catch (Exception e) {}
            if (properties == null) {
                properties = new ContextImpl();
            }

            // Make sure that properties 'name', 'type', 'host' exist
            if (!properties.containsKey("name")) {
                properties.put("name", Utilities.getDefaultNamePropertyFor(mbeanInstance));
            }
            if (!properties.containsKey("type")) {
                properties.put("type", "sf.jmx.mbean");
            }
          /*  if (!properties.containsKey("server")) {
                properties.put("server", localServerId);
            }*/

            // Build ObjectName and register
            ObjectName mbeanObjectName = null;
            try {
                mbeanObjectName = new ObjectName(domain, (Hashtable) properties);
                server.registerMBean(mbeanInstance, mbeanObjectName);
                if (sfLog().isDebugEnabled()){ sfLog().debug(mbeanObjectName + " registered");}
            } catch (MalformedObjectNameException mone) {
                if (sfLog().isErrorEnabled()){ sfLog().error("Could not create an ObjectName for MBean: " + (String)properties.get("name"));}
                continue;
            } catch (Exception e) {
                if (sfLog().isErrorEnabled()){ sfLog().error("Could not register MBean for description: " + (String)properties.get("name"));}
                e.printStackTrace();
                continue;
            }

            // If it is a NotificationListener, we register it for Agent Notifications
            if (registerWithAgent && mbeanInstance instanceof NotificationListener) {
                addAgentNotificationListener((NotificationListener) mbeanInstance);
            }
        }
    } //end createMBeans()


    /**
     *  Description of the Method
     *
     *@param  timer         Description of the Parameter
     *@param  timerContext  Description of the Parameter
     */
    protected void configureTimer(Object timer, Context timerContext) {
        Boolean sendPastNotifications = (Boolean) timerContext.get("sendPastNotifications");
        Context notifications = ((ComponentDescription) timerContext.get("notifications")).sfContext();
        // setSendPastNotifications, by default it is false
        if (sendPastNotifications.booleanValue()) {
            try {
                Method setSendPastNotifications = timer.getClass().getMethod("setSendPastNotifications", new Class[]{boolean.class});
                setSendPastNotifications.invoke(timer, new Object[]{new Boolean(true)});
            } catch (Exception e) {}
        }
        // Add notifications to the Timer
        Enumeration n = notifications.elements();
        if (!n.hasMoreElements()) {
            return;
        }

        // Check Timer object has an addNotification method
        Method addNotification = null;
        try {
            addNotification = timer.getClass().getMethod("addNotification",
                    new Class[]{
                    String.class,
                    String.class,
                    Object.class,
                    Date.class,
                    long.class,
                    long.class
                    });
        } catch (Exception e) {
            return;
        }

        for (; n.hasMoreElements(); ) {
            Context notification = null;
            String type = null;
            String message = null;
            Object userData = null;
            Date date = null;
            Long period = null;
            Long nbOcurrences = null;
            try {
                notification = ((ComponentDescription) n.nextElement()).sfContext();
                type = (String) notification.get(type);
                message = (String) notification.get(message);
                userData = notification.get(userData);
                date = (new SimpleDateFormat()).parse((String) notification.get("date"));
                period = (Long) notification.get(period);
                nbOcurrences = (Long) notification.get(nbOcurrences);

                addNotification.invoke(timer, new Object[]{type, message, userData, date, period, nbOcurrences});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } // end for
    }


    /**
     *  LIFECYCLE METHODS ****
     *
     *@exception  Exception  Description of the Exception
     */

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
       server = MBeanServerFactory.createMBeanServer("SmartFrog");
        // Search for a name (Default name is "sfJMXAgent")
        nameRef = sfCompleteNameSafe();
        try {
          name = nameRef.toString();
          if (name.length() == 0) {
            name = "sfJMXAgent";
          }

          // Local properties
          localhost = InetAddress.getLocalHost().getHostName();
          localServerId = (String) server.getAttribute(new ObjectName(
              "JMImplementation:type=MBeanServerDelegate"), "MBeanServerId");

          // Register now adaptors and connectors to provide remote access
          addDescriptionMBeans();
          addComponentMBeans();
          echo(name + " deployed");
//        } catch (RemoteException ex) {
//        } catch (MalformedObjectNameException ex) {
//        } catch (ReflectionException ex) {
//        } catch (InstanceNotFoundException ex) {
//        } catch (AttributeNotFoundException ex) {
//        } catch (MBeanException ex) {
//        } catch (UnknownHostException ex) {
        } catch (Exception ex) {
          throw SmartFrogException.forward(ex);
        }
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        echo(name + " started");
    }


    /**
     *  Description of the Method
     *
     *@param  tr  Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        handleTerminateEvent("sfTerminateWith: " + tr.toString());
        Set beanSet = server.queryNames(null, null);
        Iterator i = beanSet.iterator();
        if (sfLog().isInfoEnabled()){ sfLog().info(name + ": Unregistering MBeans...");}
        while (i.hasNext()) {
            ObjectName beanName = (ObjectName) i.next();
            try {
                server.unregisterMBean(beanName);
            } catch (Exception e) {}
        }
        MBeanServerFactory.releaseMBeanServer(server);
        super.sfTerminateWith(tr);
        if (sfLog().isDebugEnabled()){ sfLog().debug(name + ": Terminated for reason " + tr.toString());}
    }

}
