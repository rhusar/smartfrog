/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cddlm.api;

import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationReferenceListType;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.CreateRequest;
import org.smartfrog.services.cddlm.generated.api.types.CreateResponse;
import org.smartfrog.services.cddlm.generated.api.types.EmptyElementType;
import org.smartfrog.services.cddlm.generated.api.types.LookupApplicationRequest;
import org.smartfrog.services.cddlm.generated.api.types.RunRequest;
import org.smartfrog.services.cddlm.generated.api.types.ServerStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types.ServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.SetNotificationRequest;
import org.smartfrog.services.cddlm.generated.api.types.TerminateRequest;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;
import java.rmi.RemoteException;

/**
 * created Aug 4, 2004 9:49:56 AM
 */

public class DeploymentEndpoint extends SmartFrogHostedEndpoint
        implements org.smartfrog.services.cddlm.generated.api.endpoint.DeploymentEndpoint,
        ServiceLifecycle {

    /**
     * log for everything other than operations
     */
    private static final Log log = LogFactory.getLog(DeploymentEndpoint.class);

    private ServletEndpointContext servletContext;

    /**
     * log just for operational data
     */
    private static final Log operations = LogFactory.getLog(
            DeploymentEndpoint.class.getName() + ".OPERATIONS");


    public CreateResponse create(CreateRequest request)
            throws RemoteException {
        try {
            operations.info("entering create");
            CreateProcessor processor = new CreateProcessor(this);
            return processor.create(request);
        } finally {
            operations.info("exiting create");
        }
    }

    public boolean run(RunRequest request) throws RemoteException {
        try {
            operations.info("entering run");
            StartProcessor processor = new StartProcessor(this);
            return processor.run(request);
        } finally {
            operations.info("exiting run");
        }
    }

    public boolean terminate(TerminateRequest request) throws RemoteException {
        try {
            operations.info("entering undeploy");
            TerminateProcessor processor = new TerminateProcessor(this);
            return processor.terminate(request);
        } finally {
            operations.info("exiting undeploy");
        }
    }


    public ServerStatusType serverStatus(ServerStatusRequest serverStatus)
            throws RemoteException {
        try {
            operations.info("entering serverStatus");
            ServerStatusProcessor serverStatusProcessor = new ServerStatusProcessor(
                    this);
            return serverStatusProcessor.serverStatus(serverStatus);
        } finally {
            operations.info("exiting serverStatus");
        }
    }

    public ApplicationStatusType applicationStatus(
            ApplicationStatusRequest applicationStatus)
            throws RemoteException {
        try {
            operations.info("entering applicationStatus");
            ApplicationStatusProcessor processor = new ApplicationStatusProcessor(
                    this);
            return processor.applicationStatus(applicationStatus);
        } finally {
            operations.info("exiting applicationStatus");
        }
    }

    public URI lookupApplication(LookupApplicationRequest lookupApplication)
            throws RemoteException {
        try {
            operations.info("entering lookupApplication");
            LookupApplicationProcessor processor = new LookupApplicationProcessor(
                    this);
            return processor.lookupApplication(lookupApplication);
        } finally {
            operations.info("exiting lookupApplication");
        }
    }

    public ApplicationReferenceListType listApplications(
            EmptyElementType listApplications) throws RemoteException {
        try {
            operations.info("entering listApplications");
            ListApplicationsProcessor processor = new ListApplicationsProcessor(
                    this);
            return processor.listApplications(listApplications);
        } finally {
            operations.info("exiting listApplications");
        }
    }

    /**
     * set a new callback
     *
     * @param setCallback
     * @return
     * @throws RemoteException
     */
    public boolean setNotification(SetNotificationRequest setCallback)
            throws RemoteException {
        CallbackProcessor processor = new CallbackProcessor(this);
        return processor.process(setCallback);
    }

    /**
     * Used for initialization of a service endpoint. After a service endpoint
     * instance (an instance of a service endpoint class) is instantiated, the
     * JAX-RPC runtime system invokes the <code>init</code> method. The service
     * endpoint class uses the <code>init</code> method to initialize its
     * configuration and setup access to any external resources. The context
     * parameter in the <code>init</code> method enables the endpoint instance
     * to access the endpoint context provided by the underlying JAX-RPC runtime
     * system.
     * <p/>
     * The init method implementation should typecast the context parameter to
     * an appropriate Java type. For service endpoints deployed on a servlet
     * container based JAX-RPC runtime system, the <code>context</code>
     * parameter is of the Java type <code>javax.xml.rpc.server.ServletEndpointContext</code>.
     * The <code>ServletEndpointContext</code> provides an endpoint context
     * maintained by the underlying servlet container based JAX-RPC runtime
     * system
     * <p/>
     *
     * @param context Endpoint context for a JAX-RPC service endpoint
     * @throws ServiceException If any error in initialization of the service
     *                          endpoint; or if any illegal context has been
     *                          provided in the init method
     */
    public void init(Object context) throws ServiceException {
        servletContext = (ServletEndpointContext) context;
        log.debug("ServiceLifecycle::init we are initialised");
    }

    /**
     * JAX-RPC runtime system ends the lifecycle of a service endpoint instance
     * by invoking the destroy method. The service endpoint releases its
     * resources in the implementation of the destroy method.
     */
    public void destroy() {
        log.debug("ServiceLifecycle::destroy we are destroyed");

    }
}
