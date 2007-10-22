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
package org.smartfrog.services.cddlm.engine;

import org.apache.axis.types.URI;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class remembers what got deployed by whom. It retains weak references to
 * running apps, for easy purging. This class *must* be thread safe created Aug
 * 5, 2004 2:59:38 PM
 */

public class JobRepository /* implements Map */ {

    private Hashtable jobs = new Hashtable();
    private final String uriPrefix;
    private final String appPrefix;

    public JobRepository() {
        uriPrefix = "http://localhost/cddlm/" +
                System.currentTimeMillis() +
                "/job";
        appPrefix = "job";
    }

    public void clear() {
        jobs.clear();
    }

    public boolean containsKey(Object key) {
        return jobs.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return jobs.containsValue(value);
    }

    public Set entrySet() {
        return jobs.entrySet();
    }

    public boolean equals(Object o) {
        return jobs.equals(o);
    }

    public Object get(Object key) {
        return jobs.get(key);
    }

    public int hashCode() {
        return jobs.hashCode();
    }

    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public Set keySet() {
        return jobs.keySet();
    }

    public Object put(Object key, Object value) {
        return jobs.put(key, value);
    }

    public void putAll(Map t) {
        jobs.putAll(t);
    }

    private Object remove(Object key) {
        return jobs.remove(key);
    }

    public int size() {
        return jobs.size();
    }

    public Collection values() {
        return jobs.values();
    }

    public void add(JobState job) {
        put(job.getUri().toString(), job);
    }

    /**
     * lookup by uri
     *
     * @param uri job uri
     * @return
     */
    public JobState lookup(URI uri) {
        assert uri != null;
        return (JobState) get(uri.toString());
    }

    /**
     * remove an item identified by a URI
     *
     * @param uri
     */
    public void remove(URI uri) {
        remove(uri.toString());
    }

    /**
     * get an iterator
     *
     * @return
     */
    public Iterator iterator() {
        return values().iterator();
    }

    /**
     * list all the jobs
     *
     * @return
     */
    public URI[] listJobs() {
        URI[] uriList = new URI[size()];
        Iterator it = iterator();
        int count = 0;
        while (it.hasNext()) {
            JobState jobState = (JobState) it.next();
            uriList[count++] = jobState.getUri();
        }
        return uriList;
    }


    /**
     * job counter
     */
    private int counter = 0;


    private synchronized int getNewCounterValue() {
        return ++counter;
    }

    /**
     * if the job has no name, we give it one. If it has a name or no, a new URI
     * is assigned
     *
     * @param job
     */
    public void assignNameAndUri(JobState job) {
        int value = getNewCounterValue();
        String uri = uriPrefix + counter;
        if (job.getName() == null) {
            job.setName(appPrefix + counter);
        }
        job.setUri(URIHelper.toAxisUri(uri));
    }

}
