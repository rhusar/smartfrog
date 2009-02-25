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
package org.smartfrog.services.hadoop.components.dfs;

import org.smartfrog.services.filesystem.FileExists;

/**
 * Created 17-Feb-2009 13:25:52
 */


public interface DfsPathExists extends DfsPathOperation, FileExists {


    String ATTR_MIN_REPLICATION_FACTOR = "minReplicationFactor";
    String ATTR_MAX_REPLICATION_FACTOR = "maxReplicationFactor";
    String ATTR_VERBOSE = "verbose";
}
