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

package org.smartfrog.services.jmx.discovery;

import java.io.Serializable;
import javax.management.ObjectName;

/**
 *  Provides a description of a given advertised service.
 *
 *
 *@author         Serrano
 *@version        1.0
 */

public class CommunicatorDescriptor implements Serializable {

    String protocol;

    String host;

    int port = -1;

    String servicename = "";

    ObjectName objectname;

    String type;


    /**
     *  Gets the protocol attribute of the ServiceDescriptor object
     *
     *@return    The protocol value
     */
    public String getProtocol() {
        return protocol;
    }


    /**
     *  Gets the host attribute of the ServiceDescriptor object
     *
     *@return    The host value
     */
    public String getHost() {
        return host;
    }


    /**
     *  Gets the port attribute of the ServiceDescriptor object
     *
     *@return    The port value
     */
    public int getPort() {
        return port;
    }


    /**
     *  Gets the serviceName attribute of the ServiceDescriptor object
     *
     *@return    The serviceName value
     */
    public String getServiceName() {
        return servicename;
    }


    /**
     *  Gets the objectName attribute of the ServiceDescriptor object
     *
     *@return    The objectName value
     */
    public ObjectName getObjectName() {
        return objectname;
    }


    /**
     *  Gets the type attribute of the ServiceDescriptor object
     *
     *@return    The type value
     */
    public String getType() {
        return type;
    }

    public String toString() {
        String prot = "null";
        if (protocol != null) prot = protocol.toLowerCase();
        return prot+"://"+host+":"+port+"/"+servicename;
    }
}
