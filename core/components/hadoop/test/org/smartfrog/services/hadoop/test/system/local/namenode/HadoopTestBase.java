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

import org.smartfrog.test.PortCheckingTestBase;
import org.smartfrog.services.hadoop.examples.ExamplePorts;

/**
 * This is a test case for  the hadoop components. It adds the ability to test for ports being closed at the end of the
 * test run, so can catch a failing termination earlier.
 */

public class HadoopTestBase extends PortCheckingTestBase implements ExamplePorts {


    public HadoopTestBase(String name) {
        super(name);
    }

    protected void enablePortCheck() {
        super.enablePortCheck();
        checkNameNode();
    }

    protected void checkNameNode() {
        setCheckPorts(true);
        addPortCheck("NameNode IPC server", NAMENODE_IPC_PORT);
        addPortCheck("NameNode IPC server", NAMENODE_HTTP_PORT);
    }

    /**
     * Add checks for the job tracker
     */
    protected void checkDataNode() {
        setCheckPorts(true);
        addPortCheck("DATANODE_HTTP_PORT", DATANODE_HTTP_PORT);
        addPortCheck("DATANODE_HTTPS_PORT", DATANODE_HTTPS_PORT);
        addPortCheck("DATANODE_IPC_PORT", DATANODE_IPC_PORT);
    }

    /**
     * Add checks for the task tracker
     */
    protected void checkTaskTracker() {
        setCheckPorts(true);
        addPortCheck("Task Tracker HTTP", TASKTRACKER_HTTP_PORT);
    }

    /**
     * Add checks for the job tracker
     */
    protected void checkJobTracker() {
        setCheckPorts(true);
        addPortCheck("Job Tracker IPC", JOBTRACKER_IPC_PORT);
        addPortCheck("Job Tracker HTTP", JOBTRACKER_HTTP_PORT);
    }

    protected void checkFileSystem() {
        checkNameNode();
        checkDataNode();
    }

    protected void checkMapRedCluster() {
        checkFileSystem();
        checkJobTracker();
        checkTaskTracker();
    }

}
