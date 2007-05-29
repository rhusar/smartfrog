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
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerUtils;
import org.smartfrog.SFSystem;
import org.smartfrog.services.quartz.collector.DataSource;
import org.smartfrog.services.quartz.scheduler.SchedulerImpl;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * extend the compound with some remote job deployment
 */
public class JobImpl extends CompoundImpl implements Job {

    private Scheduler sched = null;
    private String configFile = null;

    private Vector machines = new Vector();

    private String name = "";
    private ComponentDescription template = null;

    private String application = null;
    public static Hashtable allValues = new Hashtable();

    private String componentNamePrefix = "comp";

    private static final Log log = LogFactory.getLog(JobImpl.class);

    public JobImpl() throws RemoteException {
    }

    public static void putValue(String name,Object value) {
        synchronized(allValues) {
            allValues.put(name, value);
        }
    }

    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();

        configFile = sfResolve(ATTR_CONFIG, configFile, false);

        sched = ((SchedulerImpl) sfResolve(ATTR_SCHEDULER, sched, true)).getScheduler();

        template = sfResolve(ATTR_TEMPLATE, template, true);

        machines = sfResolve(ATTR_MACHINES, machines, false);

        application = sfResolve(ATTR_APPLICATION, application, false);

        if (configFile != null) {
            readPropertiesFromIniFile();
        }

        name = sfCompleteName().toString();
    }


    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        Prim deploy = null;
        try {

            // computer a time that is on the next round minute
            Date runTime = TriggerUtils.getEvenMinuteDate(new Date());

            // define the job and tie it to our HelloJob class
            JobDetail job = new JobDetail("job1", "group1", HelloJob.class);

            // job.getJobDataMap().put("jobSays", "Hello World!");
            //  job.getJobDataMap().put("myFloatValue", 3.141f);
            // find application name from context.ini file
            job.getJobDataMap().put(ATTR_APPLICATION, application);

            // find hostname from Collector

            log.info("Number of machines to collect data from are:" + machines.size());
            setTargetInstances(machines.size());


            //REVISIT: steve says: I'm not sure what this code does.
            //it looks like we sort all the values
            //then enum through the original key set to find the key that matches
            //the first in the list. Surely there is a more efficient way to do this.


            //Sort the array based on the values in allValues and then schedule on the first element.
            Collection collect = allValues.values();
            Object[] array = collect.toArray();
            List list = Arrays.asList(array);
            Collections.sort(list);
            if(log.isDebugEnabled()) {
                for (int i = 0; i < list.size(); i++) {
                    log.debug("Element in sorted list: " + (list.get(i)).toString());
                }
            }
            Enumeration keys = allValues.keys();
            Object key = null;
            Object value = null;
            while (keys.hasMoreElements()) {
                key = keys.nextElement();
                value = allValues.get(key);
                if (value == list.get(0)) {
                    break;
                }
            }

            log.info("Final machine for scheduling is:" + key.toString());

            //job.getJobDataMap().put("hostname", "localhost");
            //job.getJobDataMap().put("hostname", machines.elementAt(0).toString());
            job.getJobDataMap().put("hostname", key.toString());

            MyJobListener listener = new MyJobListener();
            sched.addJobListener(listener);
            job.addJobListener(listener.getName());
            log.info("------- Job Listener Added -----------------");
            // Trigger the job to run on the next round minute
            SimpleTrigger trigger =
                    new SimpleTrigger("trigger1", "group1", runTime);

            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(job, trigger);
            log.info(job.getFullName() + " will run at: " + runTime);
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }


    /**
     * Reads properties given a system property "org.smartfrog.iniFile".
     *
     * @throws SmartFrogException if failed to read properties from the ini file
     */
    private void readPropertiesFromIniFile() throws SmartFrogException {
        try {
            if (configFile != null) {
                InputStream iniFileStream = SFSystem.getInputStreamForResource(configFile);
                try {
                    readPropertiesFrom(iniFileStream);
                } catch (IOException ioEx) {
                    throw new SmartFrogException(ioEx);
                } finally {
                    FileSystem.close(iniFileStream);
                }
            }
        } catch (Throwable ex) {
            log.error("Could not find config file "+configFile, ex);
            throw SmartFrogException.forward(ex);
        }
    }

    /**
     * Reads and sets system properties given in input stream.
     *
     * @param is input stream
     * @throws IOException failed to read properties
     */
    private void readPropertiesFrom(InputStream is) throws IOException {
        Properties props = new Properties();
        props.load(is);

        Properties sysProps = System.getProperties();
        String name = "";
        for (Enumeration e = props.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object value = props.get(key);
            if (key.equals(ATTR_MACHINES)) {
                // log.info("-----------------" + key + "====" + value);
                String list = value.toString();
                int length = list.length();

                for (int i = 0; i < length; i++) {
                    if (list.charAt(i) != ':') {
                        name = name.concat(list.valueOf(list.charAt(i)));
                    } else {
                        //	log.info("---Adding host---" + name + "----to machines vector");
                        machines.addElement(name);
                        name = "";
                    }
                }
            }

        }
    }

    // these are internal methods that should not be used directly
    public void setTargetInstances(int target) {

        for (int i = 0; i < target; i++) {
            try {
                startInstance(i);
            } catch (Exception ex) {
                log.error(ex);
            }
        }

    }

    protected void startInstance(int instance) throws Exception {

        Prim deployed = null;
        // log.info("Servers after replacement" + machines.toString());
        // read the template file
        try {
            String server = (String) machines.elementAt(instance);

            Context instanceContext = new ContextImpl();
            //  instanceContext.put(SF_PROCESS_HOST, server);
            Compound cp = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(server));

            DataSource monitor = (DataSource) cp.sfResolve("cpumonitor", true);
            template.sfReplaceAttribute("dataSource", monitor);
            template.sfAddAttribute("hostname", server);
            log.info("Template after replacement-----" + template.toString());

            deployed = sfDeployComponentDescription(componentNamePrefix + instance, this, template, instanceContext);
            log.info(name + "  instance created" + instance);

            deployed.sfDeploy();
            log.info(name + "  deployed  " + instance);
            deployed.sfStart();

            log.info(name + "  started  " + instance);


        } catch (Exception e) {
            try {
                deployed.sfDetachAndTerminate(TerminationRecord.normal(null));
            } catch (Exception ex) {
                log.error(ex);
            }

            throw e;
        }

    }
}
