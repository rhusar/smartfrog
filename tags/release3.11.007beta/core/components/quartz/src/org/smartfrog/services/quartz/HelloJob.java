/* 
 * Copyright 2005 OpenSymphony 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */

package org.smartfrog.services.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.smartfrog.services.sfinterface.SmartFrogAdapterImpl;
import org.smartfrog.services.sfinterface.SmartfrogAdapter;

import java.util.Date;
import java.util.Map;

/**
 * <p> This is just a simple job that says "Hello" to the world. </p>
 *
 * @author Bill Kratzer
 */
public class HelloJob implements Job {


    private static final Log log = LogFactory.getLog(HelloJob.class);

    /**
     * Empty constructor for job initilization.
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     */
    public HelloJob() {
    }

    /**
     * Execute the job.
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.Trigger}</code> fires
     * that is associated with the <code>Job</code>.
     *
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        try {
            String instName = context.getJobDetail().getName();
            String instGroup = context.getJobDetail().getGroup();

            JobDataMap dataMap = context.getJobDetail().getJobDataMap();

            //	String jobSays = dataMap.getString("jobSays");
            //  float myFloatValue = dataMap.getFloat("myFloatValue");
            //test_name and hostname should extracted from context
            String application = dataMap.getString("application");
            String hostname = dataMap.getString("hostname");

            // System.err.println("Instance " + instName + " of DumbJob says: " + jobSays);
            // Call Submition API
            SmartfrogAdapter sfAdap = new SmartFrogAdapterImpl("D:\\cvs\\forge\\2006\\aug21\\core\\smartfrog\\dist");
            Map cd1 = sfAdap.submit(application, null, new String[]{hostname});

            // Say Hello to the World and display the date/time
            log.info("Hello World! - " + new Date());
            log.info("Submission Done " + cd1.toString());
        } catch (Exception ex) {
            log.error(ex);
        }
    }

}
