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

import org.smartfrog.services.jmx.communication.CommunicatorMBean;

/**
 *  Description of the Interface
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public interface RmiConnectorServerMBean extends CommunicatorMBean {

    /**
     *  Gets the serviceName attribute of the RmiConnectorServerMBean object
     *
     *@return    The serviceName value
     */
    public String getServiceName();


    /**
     *  Sets the serviceName attribute of the RmiConnectorServerMBean object
     *
     *@param  serviceName                          The new serviceName value
     *@exception  java.lang.IllegalStateException  Description of the Exception
     */
    public void setServiceName(String serviceName) throws java.lang.IllegalStateException;

}
