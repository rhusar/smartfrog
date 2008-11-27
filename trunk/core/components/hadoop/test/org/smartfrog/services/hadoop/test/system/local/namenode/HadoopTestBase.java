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
package org.smartfrog.services.hadoop.test.system.local.namenode;

import org.smartfrog.services.hadoop.common.HadoopUtils;
import org.smartfrog.sfcore.utils.TimeoutInterval;
import org.smartfrog.test.DeployingTestBase;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a test case for  the hadoop components. It adds the ability to test for ports being closed at the end of the
 * test run, so can catch a failing termination earlier.
 */

public class HadoopTestBase extends DeployingTestBase {

    protected boolean checkPorts = false;
    protected boolean failOnCheckFailure = false;

    protected int connectTimeout = 5000;

    protected long shutdownTimeout = 20000;
    private int pollInterval = 1000;
    private List<PortPair> ports = new ArrayList<PortPair>();
    protected static final int NAMENODE_HTTP_PORT = 8020;
    protected static final int NAMENODE_IPC_PORT = 8021;
    protected static final int JOBTRACKER_HTTP_PORT = 50030;
    protected static final int JOBTRACKER_IPC_PORT = 8012;
    protected static final int TASKTRACKER_HTTP_PORT = 50060;

    public HadoopTestBase(String name) {
        super(name);
    }


    /**
     * Sets up the fixture,by extracting the hostname and classes dir
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        enablePortCheck();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (checkPorts) {
            blockUntilPortsAreClosed();
        }
    }

    protected void clearPortCheck() {
        ports = new ArrayList<PortPair>();
    }

    /**
     * Add a new port to the list to check
     *
     * @param name the port name
     * @param port the port number
     */
    protected void addPortCheck(String name, int port) {
        ports.add(new PortPair(name, port));
    }

    protected void blockUntilPortsAreClosed() {
        TimeoutInterval ti = new TimeoutInterval(shutdownTimeout);
        StringBuilder portsAtFault = new StringBuilder();
        boolean portIsOpen=true;
        while (!ti.hasTimedOut() && portIsOpen) {
            portIsOpen = false;
            portsAtFault = new StringBuilder();
            for (PortPair pair : ports) {
                if (pair.isOpen()) {
                    portIsOpen = true;
                    portsAtFault.append(pair);
                    portsAtFault.append('\n');
                }
            }
            if (!ti.sleep(pollInterval)) {
                break;
            }
        }
        if(portIsOpen) {
            String message = "Ports still open after " + ti.getDelay() + " milliseconds:\n" + portsAtFault;
            getLog().warn(message);
            if (failOnCheckFailure) {
                fail(message);
            }
        }
    }

    protected boolean isPortOpen(int port) {
        return HadoopUtils.isLocalPortOpen(port, connectTimeout);
    }

    protected void enablePortCheck() {
        checkPorts = true;
        checkNameNode();
    }

    private void checkNameNode() {
        addPortCheck("NameNode IPC server", NAMENODE_IPC_PORT);
        addPortCheck("NameNode IPC server", NAMENODE_HTTP_PORT);
    }

    protected void enableFailOnPortCheck() {
        failOnCheckFailure = true;
    }

    /**
     * Add checks for the task tracker
     */
    protected void checkTaskTracker() {
        addPortCheck("Task Tracker HTTP", TASKTRACKER_HTTP_PORT);
    }

    /**
     * Add checks for the job tracker
     */
    protected void checkJobTracker() {
        addPortCheck("Job Tracker IPC", JOBTRACKER_IPC_PORT);
        addPortCheck("Job Tracker HTTP", JOBTRACKER_HTTP_PORT);
    }

    protected class PortPair {

        private PortPair(String name, int port) {
            this.port = port;
            this.name = name;
        }

        public final int port;
        public final String name;

        @Override
        public String toString() {
            return name + " on port " + port;
        }

        /**
         * check for being open
         *
         * @return true if a connection can be made
         */
        public boolean isOpen() {
            return HadoopUtils.isLocalPortOpen(port, connectTimeout);
        }

        public void assertClosed() {
            if (isOpen()) {
                fail(this + " is running when it should not be");
            }
        }

        public void assertOpen() {
            if (!isOpen()) {
                fail(this + " is not running when it should be");
            }
        }


    }
}
