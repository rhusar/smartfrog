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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
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

public class JobRepository implements Iterable<Application> {

    private Hashtable<String, Application> jobs = new Hashtable<String, Application>();
    private URL systemsURL;
    public static final String SEARCH_STRING = Constants.JOB_ID_PARAM + "=";
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

    public Object put(String key, Application value) {
        return jobs.put(key, value);
    }


    public int size() {
        return jobs.size();
    }

    public Collection<Application> values() {
        return jobs.values();
    }

    public void add(Application job) {
        put(job.getId().toString(), job);
    }

    /**
     * lookup by uri
     *
     * @param uri job uri
     * @return
     */
    public Application lookup(URI uri) {
        assert uri != null;
        return lookup(uri.toString());
    }

    public Application lookup(String id) {
        return jobs.get(id);
    }

    /**
     * Predicate for testing if a job is in the repository
     * @param job
     * @return
     */
    public boolean inRepository(Application job) {
        Application job2 = lookup(job.getId());
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

    public void remove(Application job) {
        jobs.remove(job.getId());
    }

    /**
     * get an iterator
     *
     * @return
     */
    public Iterator<Application> iterator() {
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
    public synchronized boolean terminate(Application job,String reason) throws
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
    public synchronized void destroy(Application job) throws
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
        for (Application job : this) {
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
        return systemsURL + "?"+ Constants.JOB_ID_PARAM +"=" + jobID;
    }


    public Application createNewJob(String hostname) {
        Application job = new Application(Utils.createNewID());
        job.setHostname(hostname);
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
    public Application lookupJobFromQuery(String query) {
        String jobID=extractJobIDFromQuery(query);
        log.debug("job is [" + jobID + "]");
        Application job=lookup(jobID);
        return job;
    }

    /**
     * Go from an EPR to a job
     * @param epr
     * @return the job if it is present, null if not
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException on bad data
     */
    public Application lookupJobFromEndpointer(AlpineEPR epr) {
        String address = epr.getAddress();
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new BaseException("Couldn't turn an addr into a URL " +
                    address, e);
        }
        String query = url.getQuery();
        Application job = lookupJobFromQuery(query);
        return job;
    }
}
