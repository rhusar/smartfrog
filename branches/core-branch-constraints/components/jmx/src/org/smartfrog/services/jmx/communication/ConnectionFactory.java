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

package org.smartfrog.services.jmx.communication;

import java.util.Set;
import java.util.Iterator;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import org.smartfrog.services.jmx.agent.AgentNotFoundException;


/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class ConnectionFactory {

    /**
     *  Constructor for the ConnectionFactory object
     */
    public ConnectionFactory() { }


    /**
     *  This method provides implementation transparency when creating a new
     *  ConnectorClient from the given ServerAddress. Basically it takes the
     *  protocol and instantiate the matching ConnectorClient such as
     *  RmiConnectorClient. So far there exist only the RmiConnectorClient, but
     *  in a future there might be a HttpConnectorClient or a
     *  HttpsConnectorClient. This factory considers the corresponding
     *  implementation class located in the following package:
     *  "org.smartfrog.services.jmx.communication."+protocol+"."+protocol+"ConnectorClient"
     *  being "protocol" a String representing the protocol to be used for the
     *  remote connection. If it fails to create it, it will return an
     *  RmiConnectorClient by default.
     *
     *@param  protocol  the protocol to connect to remote JMX Agent
     *@return           the apropriate ConnectorClient implementation
     */

    public static ConnectorClient createConnectorClient(String protocol) throws java.net.MalformedURLException {
        //Build ConnectorAddress
        if (protocol == null || protocol.length()<1) {
            throw new IllegalArgumentException("protocol cannot be null or empty string");
        }
        protocol = protocol.toLowerCase();
        String firstLetterToUpperCase = protocol.substring(0, 1).toUpperCase();
        String Protocol = firstLetterToUpperCase + protocol.substring(1);
        ConnectorClient connectorClient;
        try {
            connectorClient = (ConnectorClient) Class.forName("org.smartfrog.services.jmx.communication." + protocol + "." + Protocol + "ConnectorClient").newInstance();
        } catch (Exception e) {
            throw new java.lang.IllegalArgumentException("Protocol " + protocol + " not supported");
        }
        return connectorClient;
    }


    /**
     *  This method provides implementation transparency when creating a new
     *  ConnectorClient from the given ServerAddress. Basically it takes the
     *  protocol and instantiate the matching ConnectorClient such as
     *  RmiConnectorClient. So far there exist only the RmiConnectorClient, but
     *  in a future there might be a HttpConnectorClient or a
     *  HttpsConnectorClient. This factory considers the corresponding
     *  implementation class located in the following package:
     *  "org.smartfrog.services.jmx.communication."+protocol+"."+protocol+"ConnectorClient"
     *  being "protocol" a String representing the protocol to be used for the
     *  remote connection. If it fails to create it, it will return an
     *  RmiConnectorClient by default.
     *
     *@param  protocol  the protocol to connect to remote JMX Agent
     *@param  host      the remote host
     *@param  port      the listening port
     *@param  resource  name of the resource to reach, such as a name service,
     *      file, etc.
     *@return           the apropriate ServerAddress implementation
     */
    public static ServerAddress createServerAddress(String protocol, String host, int port, Object resource) throws java.net.MalformedURLException {
        ServerAddress serverAddress = null;
        if (protocol == null || protocol.length()<1) {
            throw new IllegalArgumentException("protocol cannot be null or empty string");
        }
        protocol = protocol.toLowerCase();
        String firstLetterToUpperCase = protocol.substring(0, 1).toUpperCase();
        String Protocol = firstLetterToUpperCase + protocol.substring(1);
        try {
            serverAddress = (ServerAddress) Class.forName("org.smartfrog.services.jmx.communication." + protocol + "." + Protocol + "ServerAddress").newInstance();
        } catch (Exception e) {
            throw new java.net.MalformedURLException(("invalid protocol: " + protocol));
        }
        serverAddress.setHost(host);
        serverAddress.setPort(port);
        serverAddress.setResource(resource);
        return serverAddress;
    }

    /**
     *  Description of the Method
     *
     *@param  url                           Description of the Parameter
     *@return                               Description of the Return Value
     *@exception  IllegalArgumentException  Description of the Exception
     *@exception  MalformedURLException     Description of the Exception
     */
    public static ServerAddress parseURL(String url) throws IllegalArgumentException, MalformedURLException {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        ServerAddress parsed;
        try {
            int startFile = -1;

            String protocol = url.substring(0, url.indexOf(':')).toLowerCase();
            parsed = ConnectionFactory.createServerAddress(protocol, null, -1, null);

            // remove the approved protocol
            url = url.substring(url.indexOf(':') + 1);


            // Anchors (i.e. '#') are meaningless in rmi URLs - disallow them
            if (url.indexOf('#') >= 0) {
                throw new MalformedURLException("Invalid character, '#', in URL: " + url);
            }

            // No protocol must remain
//    int checkProtocol = url.indexOf(':');
//    if (checkProtocol >= 0 && (checkProtocol < url.indexOf('/')))
//        throw new java.net.MalformedURLException("invalid protocol: " + url.substring(0, checkProtocol));

            if (url.startsWith("//")) {
                final int startHost = 2;
                int nextSlash = url.indexOf("/", startHost);
                if (nextSlash >= 0) {
                    startFile = nextSlash + 1;
                } else {
                    // no trailing slash implies no name
                    nextSlash = url.length();
                    startFile = nextSlash;
                }

                int colon = url.indexOf(":", startHost);
                if ((colon > 1) && (colon < nextSlash)) {
                    // explicit port supplied
                    try {
                        parsed.setPort(Integer.parseInt(url.substring(colon + 1, nextSlash)));
                    } catch (NumberFormatException e) {
                        throw new MalformedURLException("invalid port number: " + url);
                    }
                }

                // if have colon then endhost, else end with slash
                int endHost;
                if (colon >= startHost) {
                    endHost = colon;
                } else {
                    endHost = nextSlash;
                }
                parsed.setHost(url.substring(startHost, endHost));
            } else if (url.startsWith("/")) {
                startFile = 1;
            } else {
                startFile = 0;
            }
            // set the bind name
            String resource = url.substring(startFile);
            if (!resource.equals("") && !resource.equals("/")) {
                parsed.setResource(resource);
            }
        } catch (Exception ex){
            throw new MalformedURLException("invalid url: " + url);
        }
        return parsed;
    }

    /**
     *  This method returns a ConnectorClient connected to a deployed remote
     *  instance of a JMX Agent whose address is located in "sfAgentName". The
     *  "sfAgentName" attribute must contain a valid URL of a ConnectorServer.
     *
     *@param  urlStr                      Description of the Parameter
     *@return                             the Connector Client
     *@exception  AgentNotFoundException  Description of the Exception
     *@exception  RemoteException         Description of the Exception
     */
    public static MBeanServer findMBeanServer(String urlStr) throws AgentNotFoundException {
        try {
            ServerAddress sa = parseURL(urlStr);
            ConnectorClient client = createConnectorClient(sa.getProtocol());
            if (client == null) {
                throw new Exception("Could not create a ConnectorClient for url: " + urlStr);
            }
            client.connect(sa);
            return (MBeanServer)client;
        } catch (java.net.UnknownHostException uex){
            throw new AgentNotFoundException(urlStr, null , "unknown host", uex.toString());
        } catch (java.net.ConnectException cex) {
            throw new AgentNotFoundException(urlStr, null , "error connecting to ["+urlStr+"]", cex.toString());
        } catch (java.rmi.NotBoundException nex){
            throw new AgentNotFoundException(urlStr, null , "not found resource ["+urlStr+"]", nex.toString());
        } catch (Exception e) {
            throw new AgentNotFoundException(urlStr, null ,e.toString());
        }
    }

    public static ConnectorClient findConnectorClient(MBeanServer server) throws Exception {
        ConnectorClient cc = null;
        String initialSubstring = "org.smartfrog.services.jmx.communication.";
        String finalSubstring = "ConnectorServer";
        QueryExp exp1 = Query.initialSubString(Query.classattr(), Query.value(initialSubstring));
        QueryExp exp2 = Query.finalSubString(Query.classattr(), Query.value(finalSubstring));
        Set connectors = server.queryNames(new ObjectName("*:*"), Query.and(exp1, exp2));
        for (Iterator i = connectors.iterator(); i.hasNext(); ) {
            try {
                ObjectName connectorName = (ObjectName) i.next();
                String protocol = (String) server.getAttribute(connectorName, "Protocol");
                String host = (String) server.getAttribute(connectorName, "Host");
                int port = ((Integer) server.getAttribute(connectorName, "Port")).intValue();
                String service = null;
                //if (protocol.equalsIgnoreCase("RMI")) {
                try {
                    service = (String) server.getAttribute(connectorName, "ServiceName");
                } catch (Exception e) { }
                //}
                cc = createConnectorClient(protocol);
                ServerAddress sa = createServerAddress(protocol, host, port, service);
                cc.connect(sa);
                break;
            } catch (Exception e) {
                cc = null;
            }
        }
        return cc;
    }

}
