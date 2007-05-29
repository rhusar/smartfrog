/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.deployapi.test.unit;

import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.services.deployapi.engine.Application;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

/**

 */
public class JobTest extends UnitTestBase {

    public JobTest(String name) {
        super(name);
    }

    private Application job;

    private JobRepository repository;


    protected void setUp() throws Exception {
        super.setUp();
        repository=new JobRepository(new URL("http://localhost:5050"), null, Executors.newSingleThreadExecutor());
        job = createJob();
    }

    Application createJob() {
        return repository.createNewJob("localhost");
    }

    public void testDelete() throws Exception {

        assertTrue("job found",repository.inRepository(job));
        repository.remove(job);
        assertTrue("job deleted", !repository.inRepository(job));

    }

    public void testTerminate() throws Exception {
        repository.terminate(job,"testing");
        assertTrue("job deleted", job.getState()== LifecycleStateEnum.terminated);
    }

    public void testMappingWorks() throws Exception {
        AlpineEPR epr = getJobEndpointer();
        Application job2=repository.lookupJobFromEndpointer(epr);
        assertSame(job,job2);
    }

    private AlpineEPR getJobEndpointer() {
        return job.getAlpineEPR();
    }

    public void testQueryWithSpacesWorks() throws Exception {
        String query=getJobQuery();
        assertQueryResolvesToJob(query + "   ");
    }

    public void testQueryWithTrailingParam() throws Exception {
        String query = getJobQuery();
        assertQueryResolvesToJob(query + "&arg3=bar ");
    }
    
    public void testQueryWithLeadingParam() throws Exception {
        String query1 = getJobQuery();
        String query = query1;
        assertQueryResolvesToJob("arg1=foo&"+query);
    }

    public void testQueryWithLeadingAndTrailingParam() throws Exception {
        String query1 = getJobQuery();
        String query = query1;
        assertQueryResolvesToJob("arg1=foo&" + query+"&arg3=bar ");
    }

    private void assertQueryResolvesToJob(String query) {
        Application job2 = repository.lookupJobFromQuery(query);
        assertSame(job, job2);
    }

    private String getJobQuery() throws MalformedURLException {
        AlpineEPR epr = getJobEndpointer();
        URL url=new URL(epr.getAddress());
        return url.getQuery();
    }

    public void testNoQuery() throws Exception {
        assertLookupFaults(null);
    }

    public void testWrongCase() throws Exception {
        assertLookupFaults("JOB=12344");
    }

    public void testMissingParam() throws Exception {
        assertLookupFaults("arg1=foo&arg3=bar ");        
    }

    public void testEmptyArg() throws Exception {
        assertLookupFaults("job=");
    }
    
    public void testUnknownJob() throws Exception {
        assertLookupFaults(Constants.JOB_ID_PARAM + "=1234");
    }
    
    private void assertLookupFaults(String query) {
        try {
            Application job=repository.lookupJobFromQuery(query);
            fail("expected to fail on "+query+" but got "+job);
        } catch (BaseException e) {
            //success
        }
    }

    public void testTerminateRetains() throws Exception {
        String id=job.getId();
        assertEquals(LifecycleStateEnum.undefined, job.getState());
        repository.terminate(job,"");
        assertEquals(LifecycleStateEnum.terminated,job.getState());
        //check it is still there
        assertTrue(repository.lookup(id)!=null);
        //try again
        repository.terminate(job, "");
        //and terminate
        repository.destroy(job);
        assertTrue(repository.lookup(id) == null);
    }

    public void testDestroy() throws Exception {
        String id = job.getId();
        repository.destroy(job);
        assertTrue(repository.lookup(id) == null);
    }
    
}
