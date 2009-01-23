/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.test.system.internals;

import junit.framework.TestCase;
import org.apache.hadoop.mapred.ExtJobTracker;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created 29-Jul-2008 17:34:47
 */

public class ExtJobTrackerTest extends TestCase {

    private Log log = LogFactory.getLog(ExtJobTrackerTest.class);

    /**
     * Constructs a test case with the given name.
     */
    public ExtJobTrackerTest(String name) {
        super(name);
    }

    public void testExtJobTrackerState() throws Throwable {
        ExtJobTracker jt=new ExtJobTracker(new JobConf());
        try {
            jt.offerService();
            fail("Should not reach here");
        } catch (Service.ServiceStateException e) {
            //this is expected.
        }
    }
}
