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
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.EmptyElementType;
import org.smartfrog.services.cddlm.generated.api.types.ServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types._applicationStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types._deployRequest;
import org.smartfrog.services.cddlm.generated.api.types._deployResponse;
import org.smartfrog.services.cddlm.generated.api.types._lookupApplicationRequest;
import org.smartfrog.services.cddlm.generated.api.types._undeployRequest;

import java.rmi.RemoteException;

/**
 * created Aug 4, 2004 9:49:56 AM
 */

public class DeploymentEndpoint extends SmartFrogHostedEndpoint
        implements org.smartfrog.services.cddlm.generated.api.endpoint.DeploymentEndpoint {

    /**
     * log for everything other than operations
     */
    private static Log log = LogFactory.getLog(DeploymentEndpoint.class);

    /**
     * log just for operational data
     */
    private static Log operations = LogFactory.getLog(DeploymentEndpoint.class.getName() + ".OPERATIONS");


    public _deployResponse deploy(_deployRequest deploy)
            throws RemoteException {
        try {
            operations.info("entering deploy");
            DeployProcessor processor = new DeployProcessor(this);
            return processor.deploy(deploy);
        } finally {
            operations.info("exiting deploy");
        }
    }

    public boolean undeploy(_undeployRequest undeploy) throws RemoteException {
        try {
            operations.info("entering undeploy");
            UndeployProcessor processor = new UndeployProcessor(this);
            return processor.undeploy(undeploy);
        } finally {
            operations.info("exiting undeploy");
        }
    }

    public ServerStatusType serverStatus(EmptyElementType serverStatus) throws RemoteException {
        try {
            operations.info("entering serverStatus");
            ServerStatusProcessor serverStatusProcessor = new ServerStatusProcessor(this);
            return serverStatusProcessor.serverStatus(serverStatus);
        } finally {
            operations.info("exiting serverStatus");
        }
    }

    public ApplicationStatusType applicationStatus(_applicationStatusRequest applicationStatus)
            throws RemoteException {
        try {
            operations.info("entering lookupApplication");
            ApplicationStatusProcessor processor = new ApplicationStatusProcessor(this);
            return processor.applicationStatus(applicationStatus);
        } finally {
            operations.info("exiting lookupApplication");
        }
    }

    public URI lookupApplication(_lookupApplicationRequest lookupApplication)
            throws RemoteException {
        try {
            operations.info("entering lookupApplication");
            LookupApplicationProcessor processor = new LookupApplicationProcessor(this);
            return processor.lookupApplication(lookupApplication);
        } finally {
            operations.info("exiting lookupApplication");
        }
    }

    public ApplicationReferenceListType listApplications(EmptyElementType listApplications) throws RemoteException {
        try {
            operations.info("entering listApplications");
            ListApplicationsProcessor processor = new ListApplicationsProcessor(this);
            return processor.listApplications(listApplications);
        } finally {
            operations.info("exiting listApplications");
        }
    }

}
