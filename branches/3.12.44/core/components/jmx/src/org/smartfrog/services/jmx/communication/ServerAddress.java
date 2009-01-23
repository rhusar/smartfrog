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

import java.io.Serializable;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class ServerAddress implements Serializable {

    /**
     *  Specifies the RMI protocol in a URL
     */
    public final static String RMI = "rmi";

    /**
     *  Specifies the HTTP protocol in a URL
     */
    public final static String HTTP = "http";

    /**
     *  Specifies the HTTPS protocol in a URL
     */
    public final static String HTTPS = "https";

    /**
     *  Specifies the CORBA protocol in a URL
     */
    public final static String CORBA = "iiop";

    /**
     *  Default port for RMI protocol. By default it is the one used by
     *  SmartFrog daemon
     */
    public final static int RMI_PORT = 3800;

    /**
     *  Default port for HTTP protocol
     */
    public final static int HTTP_PORT = 8880;

    /**
     *  Default port for HTTPS protocol
     */
    public final static int HTTPS_PORT = 8882;

    /**
     *  Default service name for a RMI server
     */
    public final static String SERVICE_NAME = "RMIConnectorServer";

    /**
     *  The protocol to be used to the connect to server
     */
    protected String protocol;

    /**
     *  The host where the ConnectorServer is located
     */
    protected String host;

    /**
     *  The port where the ConnectorServer is listening
     */
    protected int port;

    /**
     *  It can be any object to identify a specific remote service o resource.
     *  For instance, it can the service name of an RMI server.
     */
    protected Object resource;


    /**
     *  Constructor for the ServerAddress object
     */
    public ServerAddress() { }


    /**
     *  Constructor for the ServerAddress object
     *
     *@param  host      Description of the Parameter
     *@param  port      Description of the Parameter
     *@param  resource  Description of the Parameter
     */
    public ServerAddress(String host, int port, String resource) {
        setHost(host);
        setPort(port);
        setResource(resource);
    }


    /**
     *  Gets the protocol attribute of the ServerAddress object
     *
     *@return    The protocol value
     */
    public String getProtocol() {
        return protocol;
    }


    /**
     *  Gets the host attribute of the ServerAddress object
     *
     *@return    The host value
     */
    public String getHost() {
        return host;
    }


    /**
     *  Gets the port attribute of the ServerAddress object
     *
     *@return    The port value
     */
    public int getPort() {
        return port;
    }


    /**
     *  Gets the resource attribute of the ServerAddress object
     *
     *@return    The resource value
     */
    public Object getResource() {
        return resource;
    }


    /**
     *  Sets the host attribute of the ServerAddress object
     *
     *@param  host  The new host value
     */
    public void setHost(String host) {
        this.host = host;
    }


    /**
     *  Sets the port attribute of the ServerAddress object
     *
     *@param  port  The new port value
     */
    public void setPort(int port) {
        this.port = port;
    }


    /**
     *  Sets the resource attribute of the ServerAddress object
     *
     *@param  resource  The new resource value
     */
    public void setResource(Object resource) {
        this.resource = resource;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        return getProtocol().toString() + "://" + getHost().toString() + ":" + getPort() + "/" + getResource().toString();
    }

}
