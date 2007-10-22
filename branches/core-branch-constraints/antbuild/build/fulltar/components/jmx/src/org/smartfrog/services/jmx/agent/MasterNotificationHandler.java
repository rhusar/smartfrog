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

import java.util.*;
import java.rmi.RemoteException;
import javax.management.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.services.jmx.cascading.*;
import org.smartfrog.services.jmx.communication.*;
import org.smartfrog.services.jmx.discovery.*;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Master component for handling notifications.
 *  This class registers itself with a DiscoveryService and creates a Cascader
 *  for every new discovered JMX Agent
 *
 * <p>Title: SmartFrog</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Hewlett Packard</p>
 * @author Serrano
 * @version 1.0
 */

public class MasterNotificationHandler extends PrimImpl implements Prim, MasterNotificationHandlerMBean, NotificationListener, MBeanRegistration {

    MBeanServer mbeanServer;

    ObjectName myName;

    String localMBeanServerId;

    boolean isActive = false;

    String pattern = "*:*";

    Vector subagents = new Vector();

    Vector discMBeans = new Vector();

    public MasterNotificationHandler() throws RemoteException { }


    /**
     *  Description of the Method
     */
    public void postDeregister() {
    }


    /**
     *  {@inheritDoc}
     *
     */
    public void postRegister(Boolean registrationDone) {
        if (!registrationDone.booleanValue()) {
            synchronized (this) {
                mbeanServer = null;
            }
        }
    }


    /**
     *  {@inheritDoc}
     *
     */
    public void preDeregister()  {
        if (isActive) {
            stop();
        }
    }

    /**
     *  {@inheritDoc}
     *@exception  Exception  trouble
     */
    public ObjectName preRegister(MBeanServer server, ObjectName objectName) throws Exception {
        // My ObjectName
        myName = objectName;
        // The MBeanServer
        mbeanServer = server;
        // MBeanServerId
        ObjectName delegateName = new ObjectName("JMImplementation:type=MBeanServerDelegate");
        localMBeanServerId = (String) mbeanServer.getAttribute(delegateName, "MBeanServerId");

        return objectName;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        // Search for Discovery services
        try {
            String initialSubstring = "org.smartfrog.services.jmx.discovery.";
            String finalConnSubstring = "DiscoveryService";
            QueryExp exp1 = Query.initialSubString(Query.classattr(), Query.value(initialSubstring));
            QueryExp exp2 = Query.finalSubString(Query.classattr(), Query.value(finalConnSubstring));
            QueryExp exp  = Query.and(exp1, exp2);
            Set discovery = mbeanServer.queryNames( new ObjectName("*:*"), exp);

            for (Iterator i = discovery.iterator(); i.hasNext(); ) {
                try {
                    ObjectName discName = (ObjectName) i.next();
                    boolean active = false;
                    mbeanServer.addNotificationListener(discName, this, null, null);
                    discMBeans.addElement(discName);
                } catch (Exception e) {
                    sfLog().info(e);
                }
            }
            isActive = true;
        }
        catch (Exception e) {
            sfLog().info(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        // Unregister from all listeners
        for (Enumeration enuM = discMBeans.elements(); enuM.hasMoreElements(); ) {
            try {
                ObjectName discName = (ObjectName) enuM.nextElement();
                boolean active = false;
                mbeanServer.removeNotificationListener(discName, this);
            } catch (Exception e) { e.printStackTrace(); }
        }
        isActive = false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * {@inheritDoc}
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public void setPattern(String newPattern) {
        pattern = newPattern;
    }

    /**
     * {@inheritDoc}
     */
    public void handleNotification(Notification notification, Object handback) {
        if (!isActive) return;
        if (!(notification instanceof DiscoveryNotification)) return;

        handleDiscoveryNotification((DiscoveryNotification) notification, handback);
    }

    protected void handleDiscoveryNotification(DiscoveryNotification discNotif, Object handback) {
        if (discNotif == null || DiscoveryNotification.DISCOVERY_UNREGISTER.equals(discNotif.getType())) return;
        AgentDescriptor agDesc = discNotif.getAgentDescriptor();
        if (agDesc == null) return;
        Vector comms = agDesc.getCommunicators();
        if (comms == null) return;
        String mBeanServerId = agDesc.getMBeanServerId();

        // We do not master our JMX Agent
        if (mBeanServerId == null ||
            localMBeanServerId.equals(mBeanServerId) ||
            subagents.contains(mBeanServerId)) return;

        // Service url
        String type = null;
        String protocol = null;
        String host = null;
        int port = -1;
        String servicename = null;
        ServerAddress sa = null;

        try {
            CommunicatorDescriptor commDesc = (CommunicatorDescriptor) comms.get(0);
            // Check type of Connector
            type = commDesc.getType();
                // System.out.println("agDesc: "+agDesc+"; type: "+type);
            if (type == null ||
                !(type.startsWith("org.smartfrog.services.jmx.communication.") &&
                  type.endsWith("ConnectorServer"))) return;

            protocol = commDesc.getProtocol();
            host = commDesc.getHost();
            port = commDesc.getPort();
            servicename = commDesc.getServiceName();
            sfLog().info("DiscoveryNotification: "+protocol+"://"+host+":"+port+"/"+servicename);

            // Build ServerAddress for Cascader
            sa = ConnectionFactory.createServerAddress(protocol, host, port, servicename);
            if (sa == null) return;

            // We make sure about assigning a unique objectname
            ObjectName objectName = new ObjectName("Services:name=Cascader,type=sf.jmx.srv.cascader" + ",protocol=" + protocol + ",host=" + host + ",port=" + port + ",servicename=" + servicename + ",server=" + localMBeanServerId);
            if (mbeanServer.isRegistered(objectName)) {
                mbeanServer.invoke(objectName, "start", new Object[]{}, new String[]{});
            }
            else {
                // Build Cascader
                Cascader cascader = new Cascader(sa, new ObjectName(pattern), null);

                mbeanServer.registerMBean(cascader, objectName);
                cascader.start();
                subagents.addElement(mBeanServerId);
                sfLog().info("Cascader created for MBeanServer: "+mBeanServerId);
            }
        } catch (Exception ex) {
            sfLog().info("Could not start cascader: ",ex);
        }
    }

// Prim interface

    /**
     * {@inheritDoc}
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        try {
          pattern = (String) sfResolve("objectNamePattern");
        }
        catch (Exception e) {
            sfLog().ignore(pattern);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        start();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        stop();
        super.sfTerminateWith(tr);
    }

}
