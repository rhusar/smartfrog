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
import org.smartfrog.services.hadoop.components.cluster.FileSystemNode;
import org.smartfrog.services.hadoop.core.HadoopPingable;
import org.smartfrog.services.hadoop.core.proposed.HadoopComponentLifecycle;
import org.smartfrog.services.hadoop.core.proposed.HadoopIOException;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 *
 */
public class ExtNameNode extends NameNode implements HadoopComponentLifecycle {

    //this defaults to false
    private boolean stopped;
    private boolean checkRunning;
    private Prim owner;
    private ManagedConfiguration conf;
    public static final String NAME_NODE_IS_STOPPED = "NameNode is stopped";
    public static final String NO_FILESYSTEM = "Filesystem is not running";
    private boolean expectNodeTermination;
    private boolean terminationInitiated;
    private State state = State.CREATED;

    public static ExtNameNode createNameNode(Prim owner, ManagedConfiguration conf)
            throws IOException {
        try {
            ExtNameNode enn = new ExtNameNode(owner,conf);
            return enn;
        } catch (IOException ioe) {
            //any cleanup here
            throw ioe;
        }

    }

    /**
     * Start NameNode. This starts all the worker threads
     * @param owner owner
     * @param conf configuration
     *
     * @throws IOException io problems
     */
    private ExtNameNode(Prim owner, ManagedConfiguration conf) throws IOException {
        super(conf);
        this.conf = conf;
        this.owner = owner;
        state = State.STARTED;
        //any other config here.
        checkRunning = conf.getBoolean(FileSystemNode.ATTR_CHECK_RUNNING, true);
        expectNodeTermination = conf.getBoolean(FileSystemNode.ATTR_EXPECT_NODE_TERMINATION, true);
    }


    /**
     * Initialize; read in and validate values.
     *
     * @throws IOException for any initialisation failure
     */
    public void init() throws IOException {

    }

    /**
     * Start any work (in separate threads)
     *
     * @throws IOException for any initialisation failure
     */
    public void start() throws IOException {

    }

    /**
     * Get the current state
     *
     * @return the lifecycle state
     */
    public State getLifecycleState() {
        return state;
    }

    /**
     * Shut down
     */
    public void terminate() {
        stop();
    }


    public synchronized void stop() {
        stopped = true;
        state=State.TERMINATED;
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
     * @throws HadoopIOException if the node is unhappy
     */
    public synchronized void ping()
            throws IOException {
        if (isStopped()) {
            if (expectNodeTermination) {
                if(!terminationInitiated) {
                    terminationInitiated=true;
                    new ComponentHelper(owner).targetForWorkflowTermination(
                        TerminationRecord.normal("Name node has halted",owner.sfCompleteName()));
                }
            } else {
                //the node is stopped and we were not expecting it. throw a liveness failure
                throw new HadoopIOException(NAME_NODE_IS_STOPPED);
            }
        }
        if (!isFileSystemLive()) {
            if (checkRunning) {
                throw new HadoopIOException(NO_FILESYSTEM);
            } else {
                //look for sfShouldTerminate options
            }
        }
    }


    private boolean isFileSystemLive() {
        return super.namesystem.fsRunning;
    }
}
