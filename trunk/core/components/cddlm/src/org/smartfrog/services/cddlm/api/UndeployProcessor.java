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

import org.apache.axis.AxisFault;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.api.types._undeployRequest;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * process undeploy operation created Aug 4, 2004 4:04:20 PM
 */

public class UndeployProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(UndeployProcessor.class);

    public UndeployProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }
    public boolean undeploy(_undeployRequest undeploy) throws RemoteException {
        final URI appURI = undeploy.getApplication();
        JobState job = lookupJob(appURI);
        Prim p = job.resolvePrimFromJob();
        throwNotImplemented();
        return true;
    }

    public boolean undeploy2(_undeployRequest undeploy) throws RemoteException {
        final URI appURI = undeploy.getApplication();
        String application = extractApplicationFromURI(appURI);
        if (isEmpty(application)) {
            throw raiseNoSuchApplicationFault(appURI.toString());
        }
        try {
            ConfigurationDescriptor config = new ConfigurationDescriptor();
            config.setHost(null);
            config.setName(application);
            config.setActionType(ConfigurationDescriptor.Action.DETaTERM);
            log.info("Undeploying " + application);
            //deploy, throwing an exception if we cannot
            final ProcessCompound processCompound = SFProcess.getProcessCompound();
            assert processCompound != null;
            config.execute(processCompound);
            Object targetC = config.execute(null);
            //TODO: act on the target
        } catch (SmartFrogException exception) {
            throw AxisFault.makeFault(exception);
        } catch (Exception exception) {
            throw AxisFault.makeFault(exception);
        }
        return true;
    }


}
