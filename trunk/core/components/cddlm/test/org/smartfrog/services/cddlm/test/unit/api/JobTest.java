/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.cddlm.test.unit.api;

import junit.framework.TestCase;
import org.smartfrog.services.cddlm.api.JobRepository;
import org.smartfrog.services.cddlm.api.JobState;
import org.smartfrog.services.cddlm.api.Processor;
import org.smartfrog.services.cddlm.api.ListApplicationsProcessor;
import org.smartfrog.services.cddlm.generated.api.types._deployRequest;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationReferenceListType;
import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;

import java.util.Iterator;

/**
 * Date: 10-Aug-2004
 * Time: 22:42:09
 */
public class JobTest extends TestCase {

    JobRepository jobs;

    _deployRequest request;
    URI jobURI;
    JobState job1;
    JobState job2;
    private static final String APPNAME = "application";
    private NCName name;
    private URI otherURI;

    public JobTest() {
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        request=new _deployRequest();
        name = new NCName(APPNAME);
        request.setName(name);
        jobURI=Processor.makeURIFromApplication(request.getName());
        job1=new JobState(request);
        jobs = new JobRepository();
        otherURI = Processor.makeURIFromApplication("hello");
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown() throws Exception {

    }


    public void testJobStateBinding() {
        JobState job = new JobState(request);
        assertEquals("name extracted", name.toString(), job.getName());
        assertEquals("url extracted", jobURI, job.getUri());
        assertEquals("jobs dont match",job1, job);
        assertEquals("hashcodes dont match", job1.hashCode(), job.hashCode());
    }

    public void testJobEqualityIsURIonly() {
        JobState job = new JobState();
        job.setUri(jobURI);
        assertEquals("jobs dont match", job1, job);
        job.setUri(otherURI);
        assertTrue("different URIs are equal",!job1.equals(job));
    }

    public void testJobsAdd() {
        initRepository();
        JobState noMatch=jobs.lookup(otherURI);
        assertNull("lookup didnt fail",noMatch);
        JobState match=jobs.lookup(jobURI);
        assertNotNull("lookup failed",match);
    }

    private void initRepository() {
        assertEquals(0,jobs.size());
        jobs.add(job1);
        assertEquals(1, jobs.size());
    }

    public void testIterator() {
        initRepository();
        Iterator it=jobs.iterator();
        int count=0;
        while ( it.hasNext() ) {
            JobState jobState = (JobState) it.next();
            assertEquals(jobState,job1);
            count++;
        }
        assertEquals(1,count);
    }

    public void testListApplications() {
        URI[] uriList;
        uriList=jobs.listJobs();
        assertEquals(0,uriList.length);
        initRepository();
        uriList = jobs.listJobs();
        assertEquals(1, uriList.length);        
        ApplicationReferenceListType results = new ApplicationReferenceListType(uriList);
        URI[] uriList2=results.getApplication();
        assertEquals(1,uriList2.length);
        assertEquals(uriList[0],uriList2[0]);
    }
}
