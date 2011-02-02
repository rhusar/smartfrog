
/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.server.engines.sf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.smartfrog.services.sfinterface.SFSubmitException;

import java.util.*;
import java.util.Vector;

/**
 * <p> This is just a simple job execution. </p>
 *
 * @author Ritu Sabharwal
 */
public class ScheduleJob implements Job {


    private static final Log log = LogFactory.getLog(ScheduleJob.class);

    /**
     * <p> Empty constructor for job initilization </p> <p> Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs. </p>
     */
    public ScheduleJob() {
    }

    /**
     * <p> Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.Trigger}</code> fires
     * that is associated with the <code>Job</code>. </p>
     *
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
		try {
				String instName = context.getJobDetail().getName();
				String instGroup = context.getJobDetail().getGroup();

				JobDataMap dataMap = context.getJobDetail().getJobDataMap();

			  //  String hostname = dataMap.getString("hostname");
				String moduleId = dataMap.getString("moduleId");
				String version = dataMap.getString("version");
				String instanceName = dataMap.getString("instanceName");
				String actionTitle = dataMap.getString("title");
				 String repeatcount = dataMap.getString("repeatcount");
				String [] keys = dataMap.getKeys();
				Map attrMap = null;
				SFAdapter adapter = null;
				Vector hosts = null;
				String [] array = null;
				for (String key : keys) {
					if (key.equals("attrMap")) {
						attrMap = (Map) dataMap.get("attrMap");
					} else if (key.equals("adapter")) {
						adapter = (SFAdapter) dataMap.get("adapter");
					} else if (key.equals("hostname")) {
						String s = (String)dataMap.get("hostname");
						array= s.split(",");					
					}
				}
				
				if(repeatcount== null || repeatcount.equals(""))
					repeatcount="1";
				int cnt = 0;
				int reptcnt = new Integer(repeatcount);
				boolean success = false;
				while (cnt < reptcnt && success == false)
				{			
					List<String> list = new ArrayList<String>();;
					int cntr = 0;
					Map<String,HashMap> cd1 = adapter.submit(moduleId, version, instanceName, actionTitle, attrMap, array);
					 log.info("Submission Done " + cd1.toString());
						Set<String> st = cd1.keySet();
						Iterator<String> itr = st.iterator();
						while(itr.hasNext()){
							String machine = itr.next();
							HashMap<String,String>  hm = cd1.get(machine);
							String sta = (String)hm.get("STATUS");								
							if(!sta.trim().equalsIgnoreCase("Success")){
									list.add(machine);
							}
						}					
					if(list.size()>0){
						success = false ;
						cnt = cnt+1;
						array = new String[list.size()];
						list.toArray(array);						
					}else{
						success = true ;
						cnt = reptcnt;
					}
				}
        } catch (Exception ex) {
            log.error(ex);
        }
    }

}
