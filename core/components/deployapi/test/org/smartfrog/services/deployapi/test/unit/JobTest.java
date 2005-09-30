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

import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.binding.EprHelper;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.system.Constants;
import static org.smartfrog.services.deployapi.system.Constants.LifecycleStateEnum;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.EndpointReferenceType;
import org.apache.axis2.addressing.EndpointReference;

import java.net.URL;
import java.net.MalformedURLException;

/**

 */
public class JobTest extends UnitTestBase {

    public JobTest(String name) {
        super(name);
    }

    Job job;

    JobRepository repository;


    protected void setUp() throws Exception {
        super.setUp();
        repository=new JobRepository(new URL("http://localhost:5050"));
        job = createJob();
    }

    Job createJob() {
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
        EndpointReference epr = getJobEndpointer();
        Job job2=repository.lookupJobFromEndpointer(epr);
        assertSame(job,job2);
    }

    private EndpointReference getJobEndpointer() {
        EndpointReferenceType endpoint = job.getEndpoint();
        EndpointReference epr= EprHelper.Wsa2003ToEPR(endpoint);
        return epr;
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
        Job job2 = repository.lookupJobFromQuery(query);
        assertSame(job, job2);
    }

    private String getJobQuery() throws MalformedURLException {
        EndpointReference epr = getJobEndpointer();
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
        Job job = repository.lookupJobFromQuery("job=1234");
        assertNull(job);
    }
    
    private void assertLookupFaults(String query) {
        try {
            Job job=repository.lookupJobFromQuery(query);
            fail("expected to fail on "+query+" but got "+job);
        } catch (BaseException e) {
            //success
        }
    }

    public void testTerminateRetains() throws Exception {
        String id=job.getId();
        repository.terminate(job,"");
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
