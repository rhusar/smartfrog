/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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


package org.apache.hadoop.dfs;

import org.apache.hadoop.conf.Configuration;
import org.smartfrog.services.hadoop.components.namenode.NamenodeImpl;
import org.smartfrog.services.hadoop.core.HadoopPingable;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 *
 */
public class ExtNameNode extends NameNode implements HadoopPingable {

    //this defaults to false
    private boolean stopped;
    private boolean checkRunning;

    public static ExtNameNode createNameNode(Configuration conf)
            throws IOException {
        try {
            ExtNameNode enn = new ExtNameNode(conf);
            return enn;
        } catch (IOException ioe) {
            //any cleanup here
            throw ioe;
        }

    }

    /**
     * Start NameNode. This starts all the worker threads
     *
     * @param conf configuration
     *
     * @throws IOException io problems
     */
    private ExtNameNode(Configuration conf) throws IOException {
        super(conf);
        //any other config here.
        checkRunning = conf.getBoolean(NamenodeImpl.ATTR_CHECK_RUNNING, true);
    }

    /**
     * Shut down
     */
    public void terminate() {
        stop();
    }


    public synchronized void stop() {
        stopped = true;
        super.stop();
    }

    /**
     * Get the stopped exception
     *
     * @return true if we have stopped
     */
    public synchronized boolean isStopped() {
        return stopped;
    }

    /**
     * Ping the node
     *
     * @throws SmartFrogLivenessException if the node is unhappy
     * @throws RemoteException for network problems
     */
    public synchronized void ping()
            throws SmartFrogLivenessException, RemoteException {
        if (isStopped()) {
            throw new SmartFrogLivenessException("NameNode is stopped");
        }
        if (checkRunning && !isFileSystemLive()) {
            throw new SmartFrogLivenessException("Filesystem is not running");
        }
    }


    private boolean isFileSystemLive() {
        return super.namesystem.fsRunning;
    }
}
