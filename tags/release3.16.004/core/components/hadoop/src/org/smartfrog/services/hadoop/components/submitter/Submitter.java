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
package org.smartfrog.services.hadoop.components.submitter;

import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.conf.HadoopConfiguration;

/**
 * Created 16-Apr-2008 14:28:09
 */


public interface Submitter extends HadoopConfiguration, HadoopCluster {

    /**
     * {@value}
     */
    String ATTR_JOB = "job";

    String ATTR_TERMINATEJOB = "terminateJob";

    String ATTR_JOBID = "jobID";
    String ATTR_JOBURL = "jobURL";

    /**
     ping the job on liveness by checking its status
     */
    String ATTR_PINGJOB ="pingJob";

    /**
     only relevant when pingJob==true ; should we terminate when the job has finished?
     */
    String ATTR_TERMINATE_WHEN_JOB_FINISHES = "terminateWhenJobFinishes";

    /**
     * should we delete the output directory on startup?
     */

    String ATTR_DELETE_OUTPUT_DIR_ON_STARTUP = "deleteOutputDirOnStartup";

}
