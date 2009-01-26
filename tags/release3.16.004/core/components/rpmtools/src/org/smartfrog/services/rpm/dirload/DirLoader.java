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
package org.smartfrog.services.rpm.dirload;

import org.smartfrog.services.filesystem.FileUsingComponent;

/**
 * Created 08-Dec-2008 16:41:17
 */


public interface DirLoader extends FileUsingComponent {

    /**
     * {@value }
     */
    String ATTR_PATTERN = "pattern";

    /**
     * {@value }
     */
    String ATTR_PARENT = "parent";


    /**
     * skip a failed deployment
     * {@value }
     */
    int FAILURE_SKIP = 0;

    /**
     * on failure: rollback everything and then terminate
     * {@value }
     */
    int FAILURE_ROLLBACK = 1;

    /**
     * On failure, halt following deployments but leave what was deployed live
     * {@value }
     */
    int FAILURE_HALT = 2;

    /**
     * Failure actions
     * {@value }
     */
    String ATTR_ONFAILURE = "onFailure";

    /**
     * What file to look for
     * {@value }
     */
    String ATTR_APPLICATION = "application";

    String ATTR_HOSTS = "hosts";

    /**
     *  count of number of deployments
     */
    String ATTR_DEPLOYED_COUNT = "deployedCount";
    String ATTR_ATTEMPTED_COUNT = "attemptedCount";
    String ATTR_FAILED_COUNT = "failedCount";
}
