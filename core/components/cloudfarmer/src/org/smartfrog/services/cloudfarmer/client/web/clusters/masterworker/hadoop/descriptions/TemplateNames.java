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



package org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.hadoop.descriptions;

import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.MasterWorkerRoles;

/**
 * Define the names of various resource templates. These are all things to deploy And the properties to set
 */


public interface TemplateNames extends MasterWorkerRoles {

   

    /**
     * The number of slots for a tasktracker binding.tasktracker.slots IPROPERTY binding.tasktracker.slots;
     */
    String BINDING_TASKTRACKER_SLOTS = "binding.tasktracker.slots";

    /**
     * Package containing the temlates
     * {@value}
     */
    String TEMPLATE_PACKAGE = "/org/smartfrog/services/cloudfarmer/client/web/cluster/masterworker/hadoop/descriptions/";

    /**
     * This defines a master node. It will create a node given some predefined JVM properties {@value}
     */
    String HADOOP_MASTER_SF = TEMPLATE_PACKAGE + "master.sf";
    /**
     * This defines a master node. It will create a node given some predefined JVM properties {@value}
     */
    String HADOOP_MASTER_WORKER_SF = TEMPLATE_PACKAGE + "master_worker.sf";

    /**
     * {@value}
     */
    String HADOOP_WORKER_SF = TEMPLATE_PACKAGE + "worker.sf";

    /**
     * {@value}
     */
    String MAPREDUCE_JOB_SF = TEMPLATE_PACKAGE + "mrjob.sf";

    /**
     * {@value}
     */
    String TOOL_JOB = TEMPLATE_PACKAGE + "tool.sf";

    /**
     * The default name for a farmer on a server, if no path is given in the URL. Value: {@value}
     */
    String FARMER_PATH = "farmer";

    /**
     * Default # of task slots in a cluster, {@value}
     */
    int TASK_SLOTS = 4;
}
