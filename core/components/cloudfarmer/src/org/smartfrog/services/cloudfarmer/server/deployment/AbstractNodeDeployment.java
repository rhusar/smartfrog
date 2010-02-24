/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.server.deployment;

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.sfcore.logging.LogRemote;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 12-Nov-2009 16:08:51
 */

public abstract class AbstractNodeDeployment implements NodeDeploymentService {

    protected ClusterNode clusterNode;

    /**
     * Store the cluster node data
     * @param clusterNode the node we are deploying to
     */
    protected AbstractNodeDeployment(ClusterNode clusterNode) {
        this.clusterNode = clusterNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterNode getClusterNode() throws IOException {
        return clusterNode;
    }


    /**
     * Log at the the text locally and on the remote log 
     * @param localLog local log, can be null
     * @param remoteLog the remote log, can be null
     * @param text text to log   
     * @return the text
     */
    protected String info(LogSF localLog, LogRemote remoteLog, String text) {
        if (localLog != null) {
            localLog.info(text);
        }
        if (remoteLog != null) {
            try {
                remoteLog.info(text);
            } catch (RemoteException e) {
                if (localLog != null) {
                    localLog.warn(e);
                }
            }
        }
        return text;
    }

    /**
     * Log at the the text locally and on the remote log at error level
     *
     * @param localLog  local log, can be null
     * @param remoteLog the remote log, can be null
     * @param text      text to log
     * @return the text
     * @throws RemoteException if the remote log operation failed
     */
    protected String error(LogSF localLog, LogRemote remoteLog, String text) throws RemoteException {
        if (localLog != null) {
            localLog.error(text);
        }
        if (remoteLog!=null) try {
            remoteLog.error(text);
        } catch (RemoteException e) {
            if (localLog != null) {
                localLog.warn(e);
            }
        }
        return text;
    }
    /**
     * Log at the the text locally and on the remote log at error level
     *
     * @param localLog  local log, can be null
     * @param remoteLog the remote log, can be null
     * @param text      text to log
     * @param t a thrown exception
     * @throws RemoteException if the remote log operation failed
     */
    protected void error(LogSF localLog, LogRemote remoteLog, String text, Throwable t) throws RemoteException {
        if (localLog != null) {
            localLog.error(text, t);
        }
        try {
            remoteLog.error(text, t);
        } catch (RemoteException e) {
            if (localLog != null) {
                localLog.warn(e);
            }
        }
    }
}
