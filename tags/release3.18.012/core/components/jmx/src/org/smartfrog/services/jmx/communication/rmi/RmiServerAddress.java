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

import org.smartfrog.services.jmx.communication.ServerAddress;

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
public class RmiServerAddress extends ServerAddress implements Serializable {

    /**
     *  Constructor for the RmiServerAddress object
     */
    public RmiServerAddress() {
        protocol = ServerAddress.RMI;
        host = "localhost";
        port = ServerAddress.RMI_PORT;
        resource = ServerAddress.SERVICE_NAME;
    }


    /**
     *  Constructor for the RmiServerAddress object
     *
     *@param  host         Description of the Parameter
     *@param  port         Description of the Parameter
     *@param  serviceName  Description of the Parameter
     */
    public RmiServerAddress(String host, int port, String serviceName) {
        super(host, port, serviceName);
        super.protocol = ServerAddress.RMI;
    }


    /**
     *  Gets the serviceName attribute of the RmiServerAddress object
     *
     *@return    The serviceName value
     */
    public String getServiceName() {
        return getResource().toString();
    }


    /**
     *  Sets the serviceName attribute of the RmiServerAddress object
     *
     *@param  name  The new serviceName value
     */
    public void setServiceName(String name) {
        setResource(name);
    }

}
