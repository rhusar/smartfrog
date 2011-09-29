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
package org.smartfrog.services.hadoop.components.cluster;

import java.rmi.Remote;

/**
 * Created 30-Apr-2008 14:49:26
 */


public interface ClusterStatusChecker extends Remote {


    String ATTR_CHECK_ON_STARTUP = "checkOnStartup";
    String ATTR_CHECK_ON_LIVENESS = "checkOnLiveness";
    //declares that we can handle the filesystem
    String ATTR_SUPPORTEDFILESYSTEM = "supportedFileSystem";

    String ATTR_MIN_ACTIVE_MAP_TASKS = "minActiveMapTasks";
    String ATTR_MAX_ACTIVE_MAP_TASKS = "maxActiveMapTasks";
    String ATTR_MIN_ACTIVE_REDUCE_TASKS = "minActiveReduceTasks";
    String ATTR_MAX_ACTIVE_REDUCE_TASKS = "maxActiveReduceTasks";
    String ATTR_MAX_SUPPORTED_MAP_TASKS = "maxSupportedMapTasks";
    String ATTR_MAX_SUPPORTED_REDUCE_TASKS = "maxSupportedReduceTasks";

    /**
     * Declares that the job tracker is live
     */
    String ATTR_JOBTRACKERLIVE = "jobtrackerLive";

}
