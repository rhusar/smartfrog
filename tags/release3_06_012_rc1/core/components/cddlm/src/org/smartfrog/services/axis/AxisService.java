/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.axis;

import java.rmi.Remote;

/**
 * Date: 14-Jun-2004
 * Time: 14:26:58
 */
/*
    port extends Integer;
    username extends OptionalString
    password extends OptionalString;
    webapp extends String;
    servicePath extends String;
    adminService extends String;
    */
public interface AxisService extends Remote {

    /**
     * the name of a resource for the descriptor
     */
    String DEPLOY_RESOURCE = "deployResource";

    /**
     * undeployment resource
     */
    String UNDEPLOY_RESOURCE = "undeployResource";

    /**
     * runtime attr: service path
     */
    String SERVICE_PATH = "servicePath";

    /**
     * username for admin
     */

    String USERNAME = "username";

    /**
     * pass for admin
     */
    String PASSWORD = "password";

    String WEBAPP = "webapp";


    String ADMIN_SERVICE = "adminService";

    //serviceName extends String;
    String SERVICE_NAME = "serviceName";

    String PORT = "port";

    /**
     * runtime attr: path to WSDL. Usually service path plus servicename a query string
     */
    String WSDL_PATH = "wsdlPath";


    String TRANSPORT = "transport";

    String PROTOCOL = "protocol";

    String HOSTNAME = "hostname";
}
