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


package org.smartfrog.services.cddlm.api;

import org.apache.axis.types.URI;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.engine.JobRepository;
import org.smartfrog.services.cddlm.engine.ServerInstance;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationReferenceListType;
import org.smartfrog.services.cddlm.generated.api.types.EmptyElementType;

import java.rmi.RemoteException;

/**
 * Date: 10-Aug-2004 Time: 21:18:05
 */
public class ListApplicationsProcessor extends Processor {

    public ListApplicationsProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    /**
     * list all apps in the repository
     *
     * @param listApplications
     * @return
     * @throws RemoteException
     */
    public ApplicationReferenceListType listApplications(
            EmptyElementType listApplications) throws RemoteException {

        JobRepository jobs = ServerInstance.currentInstance().getJobs();
        URI[] uriList = jobs.listJobs();
        ApplicationReferenceListType results = new ApplicationReferenceListType(
                uriList);
        return results;
    }

}
