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
package org.smartfrog.services.deployapi.components.hosting;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cddlm.cdl.base.CdlCompoundImpl;
import org.smartfrog.services.cddlm.cdl.base.LifecycleListener;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

/**
 * this component reports lifecycle events to a lifecycle listener
 * created Sep 8, 2004 2:33:27 PM
 */

public class DeployapiCompoundImpl extends CdlCompoundImpl
        implements DeployapiCompound, LifecycleListener {

    private URI jobURI;
    private static final Log log = LogFactory.getLog(DeployapiCompoundImpl.class);
    private LifecycleListener listener;

    public DeployapiCompoundImpl() throws RemoteException {
    }


    /**
     * bind to something listening for lifecycle events. No events are raised at this point.
     * @param uri job ID
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void subscribe(String uri, LifecycleListener target) throws SmartFrogException,
            RemoteException {
        try {
            jobURI = new URI(uri);
        } catch (URISyntaxException e) {
            throw SmartFrogException.forward(e);
        }
        this.listener = target;
        sfReplaceAttribute(ATTR_JOBURI, uri);
    }





    /**
     * enter a state, send notification if this is different from a state we
     * were in before This method is synchronous, you cannot enter a state till
     * the last one was processed.
     * <p/>
     * If you try and enter the current state, then nothing happens
     *
     * @param newState new state to enter
     * @param info     string to record in the stateInfo field.
     * @throws java.rmi.RemoteException for network problems
     */
    public void enterStateNotifying(LifecycleStateEnum newState, String info) throws RemoteException {
        if (listener != null) {
            listener.enterStateNotifying(newState, info);
        }
    }

    /**
     * terminate, send a message out
     *
     * @param record termination record
     * @throws java.rmi.RemoteException for network problems
     */
    public void enterTerminatedStateNotifying(TerminationRecord record) throws RemoteException {
        if(listener!=null) {
            listener.enterTerminatedStateNotifying(record);
        }
    }

    public LifecycleListener getListener() {
        return listener;
    }
}
