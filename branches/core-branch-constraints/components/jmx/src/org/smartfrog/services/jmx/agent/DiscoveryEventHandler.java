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

import java.util.Vector;
import java.util.Set;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;
//import org.smartfrog.services.slp.ServiceURL;
import org.smartfrog.services.jmx.discovery.ServiceURL;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.smartfrog.services.jmx.communication.ConnectionFactory;
import org.smartfrog.services.jmx.communication.ServerAddress;
import org.smartfrog.services.jmx.cascading.Cascader;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * An DiscoveryEventHandler is needed in order to handle events from a SLP User Agent, which is
 * listening for new service registrations in the SLP Directory Agent. This DiscoveryEventHandler
 * creates a new CascaderMBean to master every SubAgent found by the SLP UA.
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class DiscoveryEventHandler extends EventPrimImpl {

    SFJMXAgentImpl agent;

    Vector subagents;

    Boolean master = new Boolean(false);


    /**
     *  Constructor for the EventHandler object
     *
     *@exception  RemoteException  Description of the Exception
     */
    public DiscoveryEventHandler() throws RemoteException { }


    /**
     *  Description of the Method
     *
     *@param  event  Description of the Parameter
     */
    public void handleEvent(String event) {
        if (event == null) {
            return;
        }
        synchronized (agent) {
            handleDiscoveryEvent(event);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    private void handleDiscoveryEvent(String e) {
        if (e == null || agent == null || !master.booleanValue()) return;
        String type = null;
        String protocol = null;
        String host = null;
        int port = -1;
        String resource = null;
        String localServerId = null;
        ServerAddress sa = null;
        try {
            sfLog().info("Received event from ResultsCollector: " + e);
            String urlStr = e.substring(0, e.indexOf('['));
            String attributes = e.substring(e.indexOf('[')+1, e.indexOf(']'));
            StringTokenizer st = new StringTokenizer(attributes, ", ");
            String protocolAttrib = null;
            String typeAttrib = null;
            while(st.hasMoreTokens()) {
              String token = null;
              token = st.nextToken();
              if (token.startsWith("type=org.smartfrog.services.jmx.communication.")
                  && token.endsWith("ConnectorServer")) {
                  typeAttrib = token;
              }
              else if (token.startsWith("protocol=")) {
                  protocolAttrib = token;
              }
              //if (protocolAttrib != null && typeAttrib != null) break; // If there are many attributes, it could more efficient
            }
            if (protocolAttrib == null || typeAttrib == null) return;
            protocol = protocolAttrib.substring(protocolAttrib.indexOf('=')+1);
            ServiceURL url = new ServiceURL(urlStr);
            host = url.getHost();
            port = url.getPort();
            resource = url.getURLPath();
            sfLog().info(protocol+"://"+host+"/"+port+"/"+resource);
            // We do nothing if we do not have agent where register the Cascader o this Agent is not a master
            localServerId = agent.getMBeanServerId();
            sa = ConnectionFactory.createServerAddress(protocol, host, port, resource);
            if (sa == null) {
                return;
            }
            Cascader cascader = new Cascader(sa, new ObjectName("*:*"), null);
            String mBeanServerId = cascader.getMBeanServerId();
            if (mBeanServerId == null || (subagents != null && subagents.contains(mBeanServerId))) {
                return;
            }
            // We make sure about assigning a unique objectname
            ObjectName objectName = new ObjectName("Services:name=Cascader,type=sf.jmx.srv.cascader" + ",protocol=" + protocol + ",host=" + host + ",port=" + port + ",resource=" + resource.toString() + ",server=" + localServerId);
            agent.getMBeanServer().registerMBean(cascader, objectName);
            cascader.start();
            if (subagents == null) {
              subagents = new Vector();
            }
            subagents.addElement(mBeanServerId);
            sfLog().info("Cascader created for MBeanServer: "+mBeanServerId);
        } catch (Exception ex) {
            sfLog().warn("Could not start cascader: " + ex.toString(),ex);
        }
    }


    // Lifecycle methods

    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        try {
          agent = (SFJMXAgentImpl) sfResolve("jmxAgent");
        }
        catch (Exception e) {
            sfLog().warn("DiscoveryEventHandler: Could not find JMX Agent");
        }
        try {
            master = (Boolean) agent.sfResolve("master");
        }
        catch (Exception e) {
            sfLog().ignore(e);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  tr  Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        this.agent = null;
        super.sfTerminateWith(tr);
    }
}
