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
package org.smartfrog.services.cddlm.api;

import org.apache.axis.types.URI;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class remembers what got deployed by whom. It retains weak references to running
 * apps, for easy purging.
 * created Aug 5, 2004 2:59:38 PM
 */

public class JobRepository implements Map {

    HashMap jobs = new HashMap();

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

    public Object remove(Object key) {
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
        return (JobState) get(uri.toString());
    }

    public void remove(URI uri) {
        remove(uri.toString());
    }

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
        int counter = 0;
        while ( it.hasNext() ) {
            JobState jobState = (JobState) it.next();
            uriList[counter++] = jobState.getUri();
        }
        return uriList;
    }
}
