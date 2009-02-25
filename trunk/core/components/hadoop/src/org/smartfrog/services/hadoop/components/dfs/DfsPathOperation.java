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

package org.smartfrog.services.hadoop.components.dfs;

/**
 * a directory operation needs a directory as well as a cluster
 */
public interface DfsPathOperation extends DfsOperation {

    /**
     * {@value}
     */
    String ATTR_PATH = "path";
    /**
     * {@value}
     */
    String ATTR_IDEMPOTENT = "idempotent";

    String ATTR_MIN_FILE_COUNT = "minFileCount";
    String ATTR_MAX_FILE_COUNT = "maxFileCount";
    String ATTR_MIN_TOTAL_FILE_SIZE = "minTotalFileSize";
    String ATTR_MAX_TOTAL_FILE_SIZE = "maxTotalFileSize";
}

