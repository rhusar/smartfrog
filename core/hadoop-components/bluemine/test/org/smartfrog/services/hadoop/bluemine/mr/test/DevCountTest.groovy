package org.smartfrog.services.hadoop.bluemine.mr.test

import org.smartfrog.services.hadoop.bluemine.mr.MapToDevice
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase

class DevCountTest extends BluemineTestBase {

    
    void testDevCount() {
        runEventCountJob("devcount", MapToDevice.class)
    }

}
