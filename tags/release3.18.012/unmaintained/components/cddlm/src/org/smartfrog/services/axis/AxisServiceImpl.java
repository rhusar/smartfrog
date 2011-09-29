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

import org.apache.axis.client.AdminClient;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import javax.xml.rpc.ServiceException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * This is the axis service, which registers for anything.
 * The service supports different modes of deployment: deploy local, and deploy remote
 * But that detail is hidden because the work is handed back up to the parent.
 * <p/>
 * Date: 14-Jun-2004
 * Time: 14:27:26
 */
/*
    serviceName extends String;
    //port on this machine
    port extends Integer;
    // username for admin
    username extends OptionalString
    //password for admin
    password extends OptionalString;
    //name of web application
    webapp extends String;
    //path under webapp to services
    servicePath extends String;
    //admin service name
    adminService extends String;
    */

public class AxisServiceImpl extends PrimImpl implements AxisService {

    public AxisServiceImpl() throws RemoteException {
    }

    private String transport;
    private String username;
    private String password;
    private String serviceName;
    private String webapp;
    private String servicePath;
    private String adminService;
    private String wsdlPath;
    private String deployResource;
    private String undeployResource;
    private int port;
    private String adminPath;
    private URL adminURL;
    private String protocol;
    private String hostname;
    private AdminClient adminClient;

    private Log log;


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        log = sfGetApplicationLog();
        protocol = sfResolve(AxisService.PROTOCOL, protocol, true);
        hostname = sfResolve(AxisService.HOSTNAME, hostname, true);
        port = sfResolve(AxisService.PORT, port, true);
        webapp = sfResolve(AxisService.WEBAPP, webapp, true);
        serviceName = sfResolve(AxisService.SERVICE_NAME, serviceName, true);
        servicePath = sfResolve(AxisService.SERVICE_PATH, servicePath, true);
        adminService = sfResolve(AxisService.ADMIN_SERVICE, adminService, true);
        transport = sfResolve(AxisService.TRANSPORT, transport, false);
        username = sfResolve(AxisService.USERNAME, username, false);
        //password only matters if username is set
        password = sfResolve(AxisService.PASSWORD, password, username != null);
        String path = "/" + webapp + "/" + servicePath + "/" + adminService;

        try {
            adminURL = new URL(protocol, hostname, port, path);
            log.info("Admin url is " + adminURL);
            adminClient = new AdminClient(false);
            if (username != null) {
                adminClient.setLogin(username, password);
            }
            adminClient.setTargetEndpointAddress(adminURL);
            adminClient.setTransport(transport);
        } catch (MalformedURLException e) {
            throw SmartFrogException.forward(e);
        } catch (ServiceException e) {
            throw SmartFrogException.forward(e);
        }
        //at this point we have the admin client. Now we can do things with it.
        deployResource = sfResolve(AxisService.DEPLOY_RESOURCE, deployResource, false);
        undeployResource = sfResolve(AxisService.UNDEPLOY_RESOURCE, undeployResource, deployResource != null);
        if (deployResource != null) {
            deployResource(deployResource);
        }
    }


    /**
     * undeploy a component. Errors are logged but not thrown
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (undeployResource != null) {
            try {
                deployResource(undeployResource);
            } catch (SmartFrogException e) {
                log.error("while undeploying " + serviceName, e);
            } catch (RemoteException e) {
                log.error("while undeploying " + serviceName, e);
            }
        }

    }


    /**
     * deploy a named resource
     *
     * @param resourcename
     * @throws SmartFrogException which may wrap an axis fault too
     * @throws RemoteException
     */
    public void deployResource(String resourcename) throws SmartFrogException, RemoteException {
        try {
            ComponentHelper helper = new ComponentHelper(this);
            InputStream instream = helper.loadResource(resourcename);
            assert instream != null;
            String results = adminClient.process(instream);
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw SmartFrogException.forward(e);
        }
    }

    /**
     * do any result postprocessing
     *
     * @param results 
     * @throws SmartFrogException
     */
    public void postProcessResults(String results) throws SmartFrogException {
        if (log.isDebugEnabled()) {
            log.debug("Received " + results);
        }
    }

}