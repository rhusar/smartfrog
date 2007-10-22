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

package org.smartfrog.services.jmx.cascading;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.Vector;
import java.util.Set;
import java.util.Iterator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.io.Serializable;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.QueryEval;
import javax.management.MBeanRegistration;
import javax.management.NotificationListener;
import javax.management.Notification;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.jmx.communication.ServerAddress;
import org.smartfrog.services.jmx.communication.ConnectorClient;
import org.smartfrog.services.jmx.communication.ConnectionFactory;
import org.smartfrog.services.jmx.communication.HeartBeatNotification;
import org.smartfrog.services.jmx.agent.AgentTerminatingNotification;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class Cascader extends PrimImpl implements Prim, Serializable, CascaderMBean, MBeanRegistration, NotificationListener {

    Set mbeans;

    ConnectorClient connectorClient;
    String protocol;
    String host;
    int port;
    Object resource;
    ObjectName pattern;
    QueryExp query;
    String mBeanServerId;

    MBeanServer mbeanServer;
    ObjectName objectName;

    boolean isActive = false;
    boolean terminating = false;

    private String sfCompleteName = null; // For notifying errors.


    /**
     *  Constructor for the Cascader object
     *
     *@exception  RemoteException        Description of the Exception
     *@exception  MalformedURLException  Description of the Exception
     */
    public Cascader() throws RemoteException, MalformedURLException {
        protocol = "rmi";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            host = "localhost";
        }
        port = 3800;
        resource = "RMIConnectorServer";
        connectorClient = ConnectionFactory.createConnectorClient(protocol);
        try {
            pattern = new ObjectName("*:*");
        } catch (MalformedObjectNameException mone) {}
    }


    /**
     *  Constructor for the Cascader object
     *
     *@param  address                    Description of the Parameter
     *@param  pattern                    Description of the Parameter
     *@param  query                      Description of the Parameter
     *@exception  RemoteException        Description of the Exception
     *@exception  MalformedURLException  Description of the Exception
     */
    public Cascader(ServerAddress address, ObjectName pattern, QueryExp query) throws RemoteException, MalformedURLException {
        protocol = address.getProtocol();
        host = address.getHost();
        port = address.getPort();
        resource = address.getResource();
        this.pattern = pattern;
        connectorClient = ConnectionFactory.createConnectorClient(protocol);
    }


    /**
     *  CascaderMBean Interface ****
     *
     *@return    The protocol value
     */

    public String getProtocol() {
        return protocol;
    }


    /**
     *  Sets the protocol attribute of the Cascader object
     *
     *@param  prot           The new protocol value
     *@exception  Exception  Description of the Exception
     */
    public void setProtocol(String prot) throws Exception {
        if (isActive()) {
            throw new Exception("You should stop this cascader first");
        }
        protocol = prot;
    }


    /**
     *  Gets the host attribute of the Cascader object
     *
     *@return    The host value
     */
    public String getHost() {
        return host;
    }


    /**
     *  Sets the host attribute of the Cascader object
     *
     *@param  h              The new host value
     *@exception  Exception  Description of the Exception
     */
    public void setHost(String h) throws Exception {
        if (isActive()) {
            throw new Exception("You should stop this cascader first");
        }
        host = h;
    }


    /**
     *  Gets the port attribute of the Cascader object
     *
     *@return    The port value
     */
    public int getPort() {
        return port;
    }


    /**
     *  Sets the port attribute of the Cascader object
     *
     *@param  p              The new port value
     *@exception  Exception  Description of the Exception
     */
    public void setPort(int p) throws Exception {
        if (isActive()) {
            throw new Exception("You should stop this cascader first");
        }
        port = p;
    }


    /**
     *  Gets the resource attribute of the Cascader object
     *
     *@return    The resource value
     */
    public Object getResource() {
        return resource;
    }


    /**
     *  Sets the resource attribute of the Cascader object
     *
     *@param  r              The new resource value
     *@exception  Exception  Description of the Exception
     */
    public void setResource(Object r) throws Exception {
        if (isActive()) {
            throw new Exception("You should stop this cascader first");
        }
        resource = r;
    }


    /**
     *  Gets the pattern attribute of the Cascader object
     *
     *@return    The pattern value
     */
    public ObjectName getPattern() {
        return pattern;
    }


    /**
     *  Sets the pattern attribute of the Cascader object
     *
     *@param  pattern  The new pattern value
     */
    public void setPattern(ObjectName pattern) {
        this.pattern = pattern;
    }

    public QueryExp getQuery() {
        return query;
    }

    public void setQuery(QueryExp query) {
        this.query = query;
    }

    /**
     *  Gets the remoteMBeansCount attribute of the Cascader object
     *
     *@return    The remoteMBeansCount value
     */
    public int getRemoteMBeansCount() {
        return mbeans.size();
    }


    /**
     *  Gets the remoteMBeans attribute of the Cascader object
     *
     *@return    The remoteMBeans value
     */
    public Set getRemoteMBeans() {
        return mbeans;
    }


    /**
     *  Gets the mBeanServerId attribute of the Cascader object
     *
     *@return    The mBeanServerId value
     */
    public String getMBeanServerId() {
        if (mBeanServerId == null) {
            // The cascader has not been initialized yet
            try {
                if (connectorClient == null) {
                    connectorClient = ConnectionFactory.createConnectorClient(protocol);
                }
                ServerAddress address = ConnectionFactory.createServerAddress(protocol, host, port, resource);
                connectorClient.connect(address);
                ObjectName delegateName = new ObjectName("JMImplementation:type=MBeanServerDelegate");
                mBeanServerId = (String) connectorClient.invoke(delegateName, "getMBeanServerId", new Object[]{}, new String[]{});
                connectorClient.disconnect();
            } catch (java.net.UnknownHostException uex){
               if (sfCompleteName==null) try {sfCompleteName= this.sfCompleteName().toString();} catch (Exception ex) { }
               String message = "unknown host "+host;
               sfLog().error(sfCompleteName+ " " +message,uex);
            } catch (java.net.ConnectException cex) {
                if (sfCompleteName==null) try {sfCompleteName= this.sfCompleteName().toString();} catch (Exception ex) { }
                String url = host.toString();
                try {url= ConnectionFactory.createServerAddress(protocol, host, port, resource).toString();} catch (Exception ex) { }
                String message = "Error connecting to ["+url+"]";
                sfLog().error(sfCompleteName+ " " +message,cex);
            } catch (java.rmi.NotBoundException nex){
                if (sfCompleteName==null) try {sfCompleteName= this.sfCompleteName().toString();} catch (Exception ex) { }
                String message = "not found "+resource+". "+nex.toString();
                sfLog().error(sfCompleteName+ " " +message,nex);
            } catch (Exception e) {
                if (sfCompleteName==null) try {sfCompleteName= this.sfCompleteName().toString();} catch (Exception ex) { }
                sfLog().error(sfCompleteName+ " " + e.toString(),e);
            }

        }
        return mBeanServerId;
    }


    /**
     *  Gets the active attribute of the Cascader object
     *
     *@return    The active value
     */
    public boolean isActive() {
        return isActive;
    }


    /**
     *  Description of the Method
     */
    public void start() {
        if (isActive) {
            return;
        }
        String sfCompleteName = "Cascader";
        try {
            if (connectorClient == null) {
                connectorClient = ConnectionFactory.createConnectorClient(protocol);
            }
            ServerAddress address = ConnectionFactory.createServerAddress(protocol, host, port, resource);
            connectorClient.setHeartBeatPeriod(3000);
            connectorClient.connect(address);
            ObjectName delegateName = new ObjectName("JMImplementation:type=MBeanServerDelegate");
            mBeanServerId = (String) connectorClient.getAttribute(delegateName, "MBeanServerId");
            // Register for local and remote notifications
            connectorClient.addHeartBeatNotificationListener(this, null, null);
            connectorClient.addNotificationListener(delegateName,
                    this,
                    null,
                    "RemoteNotification");
            mbeanServer.addNotificationListener(delegateName,
                    this,
                    null,
                    "LocalNotification");

            mbeans = connectorClient.queryNames(pattern, query);
            synchronized (mbeans) {
                try {
                    mbeans.remove(delegateName);
                } catch (Exception e) {}
                Vector toBeRemoved = new Vector();
                for (Iterator i = mbeans.iterator(); i.hasNext(); ) {
                    ObjectName remoteName = null;
                    try {
                        remoteName = (ObjectName) i.next();
                        if (sfLog().isTraceEnabled()){ sfLog().trace("debug: "+remoteName);}
                        mbeanServer.createMBean("org.smartfrog.services.jmx.mbean.MBeanProxy",
                                remoteName,
                                new Object[]{remoteName, (MBeanServer) connectorClient},
                                new String[]{"javax.management.ObjectName",
                                "javax.management.MBeanServer"}
                                );

                    } catch (Exception e) {
                        //if (sfCompleteName==null) try {sfCompleteName= this.sfCompleteName().toString();} catch (Exception ex) { }
                        if (sfLog().isErrorEnabled()){ sfLog().error(" Could not create MBeanProxy for remote MBean: " + remoteName,e);}
                        toBeRemoved.addElement(remoteName);
                        e.printStackTrace();
                    }
                }
                mbeans.removeAll(toBeRemoved);
            }
            // end for
            isActive = true;
            terminating = false;
        } catch (java.net.UnknownHostException uex){
            if (sfLog().isErrorEnabled()){ sfLog().error("unknown host "+host);}
        } catch (java.net.ConnectException cex) {
            String url = host.toString();
            try {url= ConnectionFactory.createServerAddress(protocol, host, port, resource).toString();} catch (Exception ex) { }
            if (sfLog().isErrorEnabled()){ sfLog().error ("Error connecting to ["+url+"]",cex);}
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()){ sfLog().error("Could not start: " + e.toString(),e);}
        }
    }


    /**
     *  Description of the Method
     */
    public void stop() {
        if (!isActive) {
            return;
        }
        terminating = true;
        String sfCompleteName = "Cascader";
        // Unregister all the remote beans managed by this cascader
        if (mbeanServer != null) {
            for (Iterator i = mbeans.iterator(); i.hasNext(); ) {
                ObjectName name = null;
                try {
                    name = (ObjectName) i.next();
                    mbeanServer.unregisterMBean(name);
                } catch (Exception e) {
                    if (sfLog().isErrorEnabled()){ sfLog().error("Could not unregister MBean: " + name + ", "+e.getMessage(),e);}
                    //e.printStackTrace();
                }
            }
        }
        mbeans.clear();
        if (!mbeans.isEmpty()) {
            mbeans.clear();
        }
        try {
            mbeanServer.removeNotificationListener(new ObjectName("JMImplementation:type=MBeanServerDelegate"), this);
        } catch (Exception e) {}
        try {
            connectorClient.removeNotificationListener(new ObjectName("JMImplementation:type=MBeanServerDelegate"), this);
        } catch (Exception e) {}
        connectorClient.removeHeartBeatNotificationListener(this);
        if (connectorClient != null) {
            connectorClient.disconnect();
        }
        isActive = false;
    }


    /**
     *  MBeanRegistration Interface ****
     */

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


    /**
     *  NotificationListener Interface ****
     *
     *@param  notification  Description of the Parameter
     *@param  handback      Description of the Parameter
     */

    public void handleNotification(Notification notification, Object handback) {
        if (terminating || !isActive) {
            return;
        }
        if (notification instanceof AgentTerminatingNotification) {
            stop();
        }
        if (notification instanceof MBeanServerNotification) {
            MBeanServerNotification mbsNotif = (MBeanServerNotification) notification;
            if ("RemoteNotification".equals(handback)) {
                handleRemoteNotification(mbsNotif);
            } else if ("LocalNotification".equals(handback)) {
                handleLocalNotification(mbsNotif);
            }
        } else if (notification instanceof HeartBeatNotification) {
            handleHeartBeatNotification(notification);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  mbsNotif  Description of the Parameter
     */
    private void handleRemoteNotification(MBeanServerNotification mbsNotif) {
        ObjectName remoteName = mbsNotif.getMBeanName();
        if (mbsNotif.getType().equals(MBeanServerNotification.REGISTRATION_NOTIFICATION)) {
            try {
                mbeanServer.createMBean("org.smartfrog.services.jmx.mbean.MBeanProxy",
                        remoteName,
                        new Object[]{remoteName, (MBeanServer) connectorClient},
                        new String[]{"javax.management.ObjectName",
                        "javax.management.MBeanServer"}
                        );
                mbeans.add(remoteName);
            } catch (Exception e) {
                if (sfLog().isErrorEnabled()){ sfLog().error("Could not create MBeanProxy for remote MBean: " + remoteName,e);}
            }
        } else if (mbsNotif.getType().equals(MBeanServerNotification.UNREGISTRATION_NOTIFICATION)) {
            if (mbeans.contains(remoteName)) {
                try {
                    mbeans.remove(remoteName);
                    mbeanServer.unregisterMBean(remoteName);
                } catch (Exception e) {
                    if (sfLog().isErrorEnabled()){ sfLog().error("Could not unregister MBeanProxy for remote MBean: " + remoteName,e);}
                }
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  mbsNotif  Description of the Parameter
     */
    private void handleLocalNotification(MBeanServerNotification mbsNotif) {
        if (mbsNotif.getType().equals(MBeanServerNotification.UNREGISTRATION_NOTIFICATION)) {
            ObjectName remoteName = mbsNotif.getMBeanName();
            if (mbeans.contains(remoteName)) {
                try {
                    mbeans.remove(remoteName);
                    connectorClient.unregisterMBean(remoteName);
                } catch (Exception e) {
                    if (sfCompleteName==null) try {sfCompleteName= this.sfCompleteName().toString();} catch (Exception ex) { }
                    if (sfLog().isErrorEnabled()){ sfLog().error(sfCompleteName +"Could not unregister remote MBean: " + remoteName + ", " +e.toString(),e);}
                }
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  notif  Description of the Parameter
     */
    private void handleHeartBeatNotification(Notification notif) {
        if (isActive && notif.getType().equals(HeartBeatNotification.LOST)) {
            stop();
        }
    }


    /**
     *  Prim Interface ****
     *
     *@exception  Exception  Description of the Exception
     */

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        try {
            protocol = ((String) sfResolveHere("protocol"));
        } catch (Exception e) {}
        try {
            host = ((String) sfResolveHere("host"));
        } catch (Exception e) {}
        try {
            port = ((Integer) sfResolveHere("port")).intValue();
        } catch (Exception e) {}
        try {
            resource = sfResolveHere("resource");
        } catch (Exception e) {}
        try {
            String objectNamePattern = (String) sfResolveHere("objectNamePattern");
            pattern = new ObjectName(objectNamePattern);
        } catch (Exception e) {}
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        start();
        if (sfCompleteName==null) try {sfCompleteName= this.sfCompleteName().toString();} catch (Exception ex) { }
        if (isActive()) {
            if (sfLog().isInfoEnabled()){ sfLog().info("Cascader " + objectName + " started");}
        } else {
            if (sfLog().isErrorEnabled()){ sfLog().error("Could not start cascader: " + objectName);}
        }
    }


    /**
     *  Description of the Method
     *
     *@param  tr  Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        terminating = true;
        // Prevent from handling new notifications
        stop();
        super.sfTerminate(tr);
    }

}
