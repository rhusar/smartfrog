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


package org.apache.hadoop.hdfs.server.namenode;

import org.smartfrog.services.hadoop.components.cluster.FileSystemNode;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.IOException;

/**
 *
 */
public class ExtNameNode extends NameNode {

    //this defaults to false
    private boolean stopped;
    private boolean checkRunning;
    private Prim owner;
    private ManagedConfiguration conf;
    public static final String NAME_NODE_IS_STOPPED = "NameNode is stopped";
    public static final String NO_FILESYSTEM = "Filesystem is not running";
    private boolean expectNodeTermination;
    private boolean terminationInitiated;

    /**
     * Create a new name node and deploy it.
     * @param owner owner
     * @param conf configuration
     * @return the new name node
     * @throws IOException if the node would not deploy
     */
    public static ExtNameNode createAndDeploy(Prim owner, ManagedConfiguration conf)
            throws IOException {
        ExtNameNode enn = new ExtNameNode(owner, conf);
        deploy(enn);
        return enn;
    }

    /**
     * create a new name node, but do not start it
     * @param owner owner
     * @param conf configuration
     * @return the new name node
     * @throws IOException if the node would not deploy
     */
    public static ExtNameNode create(Prim owner,
                                     ManagedConfiguration conf)
            throws IOException {
        ExtNameNode enn = new ExtNameNode(owner, conf);
        return enn;
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
        //any other config here.
        checkRunning = conf.getBoolean(FileSystemNode.ATTR_CHECK_RUNNING, true);
        expectNodeTermination = conf.getBoolean(FileSystemNode.ATTR_EXPECT_NODE_TERMINATION, true);
    }


  /**
   * This method is designed for overriding, with subclasses implementing
   * termination logic inside it.
   *
   * It is only called when the component is entering the terminated state; and
   * will be called once only.
   *
   * @throws IOException exceptions which will be logged
   */
  @Override
    public synchronized void innerTerminate() throws IOException {
        super.innerTerminate();
        stopped = true;
    }

    /**
     * Get the stopped exception
     *
     * @return true if we have stopped
     */
    public synchronized boolean isStopped() {
        return getServiceState()== ServiceState.TERMINATED;
    }

    /**
     * Ping the node
     *
     * @throws IOException if the node is unhappy
     */
    public synchronized void ping()
            throws IOException {
        if (checkRunning) {
            super.ping();
        }
        if (isStopped()) {
            if (expectNodeTermination) {
                //if we expect the node to terminate, now is a good time to say 'we'd like to terminate ourselves'
                if (!terminationInitiated) {
                    //that is, if it hasn't already been initiated
                    terminationInitiated = true;
                    new ComponentHelper(owner).targetForWorkflowTermination(
                            TerminationRecord.normal("Name node has halted",
                                    owner.sfCompleteName()));
                }
            } else {
                //the node is stopped and we were not expecting it. throw a liveness failure
                throw new IOException(NAME_NODE_IS_STOPPED);
            }
        }
    }

}
