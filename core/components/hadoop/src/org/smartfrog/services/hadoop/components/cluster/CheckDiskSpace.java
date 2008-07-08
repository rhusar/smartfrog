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

/**
 * Created 12-May-2008 16:06:08
 */


public interface CheckDiskSpace extends CheckableCondition {

    /**
     * Vector of directories: {@value}
     */
    String ATTR_DIRECTORIES ="directories";

    /** minimum MB:{@value} */
    String ATTR_MIN_AVAILABLE_MB ="minAvailableMB";

    /** minimum GB. A GB=1024*1MB, despite what disk vendors say: {@value}*/
    String ATTR_MIN_AVAILABLE_GB ="minAvailableGB";

    /** flag to say: skip any dir that isnt there  {@value}*/
    String ATTR_SKIP_ABSENT_DIRECTORIES ="skipAbsentDirectories";

}
