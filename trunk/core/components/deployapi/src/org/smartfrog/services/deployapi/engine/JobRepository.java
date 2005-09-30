/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.engine;


import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * This class remembers what got deployed by whom. It retains weak references to
 * running apps, for easy purging.
 * <p/>
 * This class *must* be thread safe
 * <p/>
 * created Aug 5, 2004 2:59:38 PM
 */

public class JobRepository implements Iterable<Job> {

    private Hashtable<String, Job> jobs = new Hashtable<String, Job>();
    private URL systemsURL;
    public static final String JOB_ID_PARAM = "job";
    public static final String SEARCH_STRING = JOB_ID_PARAM + "=";
    Log log= LogFactory.getLog(JobRepository.class);


    public JobRepository(URL systemsURL) {
        this.systemsURL = systemsURL;
    }

    public void clear() {
        jobs.clear();
    }


    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public Set keySet() {
        return jobs.keySet();
    }

    public Object put(String key, Job value) {
        return jobs.put(key, value);
    }


    public int size() {
        return jobs.size();
    }

    public Collection values() {
        return jobs.values();
    }

    public void add(Job job) {
        put(job.getId().toString(), job);
    }

    /**
     * lookup by uri
     *
     * @param uri job uri
     * @return
     */
    public Job lookup(URI uri) {
        assert uri != null;
        return lookup(uri.toString());
    }

    public Job lookup(String id) {
        return jobs.get(id);
    }

    /**
     * Predicate for testing if a job is in the repository
     * @param job
     * @return
     */
    public boolean inRepository(Job job) {
        Job job2 = lookup(job.getId());
        return job2!=null;
    }

    /**
     * remove an item identified by a URI
     *
     * @param uri
     */
    public void remove(URI uri) {
        jobs.remove(uri.toString());
    }

    public void remove(Job job) {
        jobs.remove(job.getId());
    }

    /**
     * get an iterator
     *
     * @return
     */
    public Iterator<Job> iterator() {
        return values().iterator();
    }

    /**
     * Termination routine
     * @throws RemoteException
     */
    public void terminate() throws RemoteException {
    
    }
    
    /**
     * Thread safe termination of job 
     * @param job
     * @param reason
     */
    public synchronized boolean terminate(Job job,String reason) throws
            RemoteException {
        log.info("Terminating " + job.getId() + " with reason:" + reason);
        
        boolean result=job.terminate(reason);
        return result;
    }

    /**
     * Thread safe termination of job and removal from the stack
     *
     * @param job
     */
    public synchronized void destroy(Job job) throws
            RemoteException {
        terminate(job, "destroy");
        remove(job);
    }


    /**
     * list all the jobs
     *
     * @return
     */
    public String[] listJobs() {
        String[] uriList = new String[size()];
        int count = 0;
        for (Job job : this) {
            String id = job.getId();
            uriList[count++] = createJobAddress(id);
        }
        return uriList;
    }


    /**
     * get an address for job requests.
     * @param jobID
     * @return
     */
    public String createJobAddress(String jobID) {
        return systemsURL + "?"+JOB_ID_PARAM +"=" + jobID;
    }


    /**
     * if the job has no name, we give it one. If it has a name or no, a new URI
     * is assigned
     *
     * @param job
     */
    public void assignID(Job job) {
        String id = Utils.createNewID();
        job.setId(id);
    }

    public Job createNewJob(String hostname) {
        Job job = new Job();
        job.setId(Utils.createNewID());
        job.setHostname(hostname);
        job.setState(Constants.LifecycleStateEnum.initialized);
        String id = job.getId();
        job.setName(id);
        job.setAddress(createJobAddress(id));
        add(job);
        return job;
    }

    
    
    public String extractJobIDFromQuery(String query) {
        if (query == null) {
            throw FaultRaiser.raiseNoSuchApplicationFault("No job in address");
        }
        int index = query.indexOf(SEARCH_STRING);
        if (index == -1) {
            String message = "Didn't find query (" +
                    SEARCH_STRING +
                    ") in " +
                    query;
            log.debug(message);
            throw FaultRaiser.raiseNoSuchApplicationFault(message);
        }
        int start = index + SEARCH_STRING.length();
        int end = query.indexOf("&", start);
        if (end == -1) {
            end = query.length();
        }
        String substrate = query.substring(start, end).trim();
        if(substrate.length()==0) {
            throw FaultRaiser.raiseNoSuchApplicationFault("Empty job in "+query);
        }
            
        return substrate;
    }

    /**
     * Look up the job ID in a query
     * @param query
     * @return the job if it is present, null if not
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException on bad data
     */
    public Job lookupJobFromQuery(String query) {
        String jobID=extractJobIDFromQuery(query);
        log.debug("job is [" + jobID + "]");
        Job job=lookup(jobID);
        return job;
    }

    /**
     * Go from an EPR to a job
     * @param epr
     * @return the job if it is present, null if not
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException on bad data
     */
    public Job lookupJobFromEndpointer(EndpointReference epr) {
        String address = epr.getAddress();
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new BaseException("Couldn't turn an addr into a URL " +
                    address, e);
        }
        String query = url.getQuery();
        Job job = lookupJobFromQuery(query);
        return job;
    }
}
