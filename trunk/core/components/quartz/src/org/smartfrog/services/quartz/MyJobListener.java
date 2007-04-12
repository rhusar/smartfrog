/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;


public class MyJobListener implements JobListener {

    private static final Log log = LogFactory.getLog(MyJobListener.class);

    public String getName() {
        return "myjoblistener";
    }

    public void jobToBeExecuted(JobExecutionContext context) {
        log.info("Job to be executed");
    }

    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("Job execution vetoed");
    }

    public void jobWasExecuted(JobExecutionContext context,
                               JobExecutionException jobException) {
        log.info("Job was executed");
    }


}
