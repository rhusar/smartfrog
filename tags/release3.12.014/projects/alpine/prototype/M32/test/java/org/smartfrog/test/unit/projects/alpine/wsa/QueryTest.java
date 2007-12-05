/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.unit.projects.alpine.wsa;

import org.smartfrog.projects.alpine.wsa.AlpineEPR;

/**
 * created 10-Oct-2006 16:30:34
 */

public class QueryTest extends AddressingTestBase {


    public QueryTest(String name) {
        super(name);
    }

    public void testName1() throws Exception {
        assertEquals("value1", epr.lookupQuery("name1"));
    }

    public void testName2() throws Exception {
        assertEquals("value2",epr.lookupQuery("name2"));
    }

    public void testName3() throws Exception {
        assertNull(epr.lookupQuery("name3"));
    }

    public void testDuplicate() throws Exception {
        AlpineEPR job=new AlpineEPR("http://chamonix.hpl.hp.com:8080/alpine/system/?system=uuid_023713ca_edfc_43a4_abaa_fc8c8244fbfd");
        String jobID = job.lookupQuery("system");
        assertEquals("uuid_023713ca_edfc_43a4_abaa_fc8c8244fbfd",jobID);
    }
}
